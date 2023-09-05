package top.seraphjack.simplelogin.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameType;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import top.seraphjack.simplelogin.SLConstants;
import top.seraphjack.simplelogin.command.arguments.ArgumentTypeEntryName;
import top.seraphjack.simplelogin.command.arguments.ArgumentTypeHandlerPlugin;
import top.seraphjack.simplelogin.server.SLRegistries;
import top.seraphjack.simplelogin.server.handler.PlayerLoginHandler;
import top.seraphjack.simplelogin.server.storage.SLStorage;

import java.io.IOException;
import java.util.Collection;
import java.util.function.Supplier;

import static net.minecraft.network.chat.Component.literal;

public class SLCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("simplelogin")
                .then(Commands.literal("save").requires((c) -> c.hasPermission(3))
                        .executes(SLCommand::save))
                .then(Commands.literal("unregister").requires((c) -> c.hasPermission(3))
                        .then(Commands.argument("entry", ArgumentTypeEntryName.entryName())
                                .executes(SLCommand::unregister)))
                .then(Commands.literal("setDefaultGameType").requires((c) -> c.hasPermission(3))
                        .then(Commands.argument("entry", ArgumentTypeEntryName.entryName())
                                .then(Commands.argument("mode", IntegerArgumentType.integer(0, 3))
                                        .executes(SLCommand::setDefaultGamemode))))
                .then(Commands.literal("plugin").requires((c) -> c.hasPermission(3))
                        .then(Commands.literal("load")
                                .then(Commands.argument("plugin", ArgumentTypeHandlerPlugin.unloadedPlugins())
                                        .executes(SLCommand::loadPlugin)))
                        .then(Commands.literal("unload")
                                .then(Commands.argument("plugin", ArgumentTypeHandlerPlugin.loadedPlugins())
                                        .executes(SLCommand::unloadPlugin)))
                        .then(Commands.literal("available")
                                .executes(SLCommand::listAvailablePlugins))
                        .then(Commands.literal("loaded")
                                .executes(SLCommand::listLoadedPlugins)))
                .then(Commands.literal("about").executes(SLCommand::about));
        dispatcher.register(command);
    }

    private static int save(CommandContext<CommandSourceStack> ctx) {
        try {
            long start = System.currentTimeMillis();
            SLStorage.instance().storageProvider.save();
            long cost = System.currentTimeMillis() - start;
            ctx.getSource().sendSuccess((Supplier<Component>) literal("Done. Took " + cost + " ms."), true);
        } catch (IOException e) {
            ctx.getSource().sendSuccess((Supplier<Component>) literal("Error during saving entries, see log for details"), false);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int unregister(CommandContext<CommandSourceStack> ctx) {
        SLStorage.instance().storageProvider.unregister(ArgumentTypeEntryName.getEntryName(ctx, "entry"));
        ctx.getSource().sendSuccess((Supplier<Component>) literal("Successfully unregistered."), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int setDefaultGamemode(CommandContext<CommandSourceStack> ctx) {
        GameType gameType = GameType.byId(ctx.getArgument("mode", Integer.class));
        SLStorage.instance().storageProvider.setGameType(ArgumentTypeEntryName.getEntryName(ctx, "entry"), gameType);
        ctx.getSource().sendSuccess((Supplier<Component>) literal("Successfully set entry default game type to " + gameType.getName() + "."), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int about(CommandContext<CommandSourceStack> ctx) {
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        ModInfo info = FMLLoader.getLoadingModList().getMods().stream()
                .filter(modInfo -> modInfo.getModId().equals(SLConstants.MODID))
                .findAny().get();
        ctx.getSource().sendSuccess(
                (Supplier<Component>) Component.translatable("simplelogin.command.about.info", info.getVersion().toString()),
                false
        );
        return Command.SINGLE_SUCCESS;
    }

    private static int loadPlugin(CommandContext<CommandSourceStack> ctx) {
        ResourceLocation plugin = ArgumentTypeHandlerPlugin.getPlugin(ctx, "plugin");
        if (SLRegistries.PLUGINS.get(plugin).isEmpty()) {
            ctx.getSource().sendSuccess(
                    (Supplier<Component>) Component.translatable("simplelogin.command.plugin.not_found", plugin.toString()),
                    true
            );
            return Command.SINGLE_SUCCESS;
        }
        if (PlayerLoginHandler.instance().listPlugins().contains(plugin)) {
            ctx.getSource().sendSuccess(
                    (Supplier<Component>) Component.translatable("simplelogin.command.plugin.already_loaded", plugin.toString()),
                    true
            );
            return Command.SINGLE_SUCCESS;
        }

        PlayerLoginHandler.instance().loadPlugin(plugin);

        ctx.getSource().sendSuccess(
                (Supplier<Component>) Component.translatable("simplelogin.command.plugin.load_success", plugin.toString()),
                true
        );

        return Command.SINGLE_SUCCESS;
    }

    private static int unloadPlugin(CommandContext<CommandSourceStack> ctx) {
        ResourceLocation plugin = ArgumentTypeHandlerPlugin.getPlugin(ctx, "plugin");
        if (SLRegistries.PLUGINS.get(plugin).isEmpty()) {
            ctx.getSource().sendSuccess(
                    (Supplier<Component>) Component.translatable("simplelogin.command.plugin.not_found", plugin.toString()),
                    true
            );
            return Command.SINGLE_SUCCESS;
        }
        if (!PlayerLoginHandler.instance().listPlugins().contains(plugin)) {
            ctx.getSource().sendSuccess(
                    (Supplier<Component>) Component.translatable("simplelogin.command.plugin.not_loaded", plugin.toString()),
                    true
            );
            return Command.SINGLE_SUCCESS;
        }

        PlayerLoginHandler.instance().unloadPlugin(plugin);
        ctx.getSource().sendSuccess(
                (Supplier<Component>) Component.translatable("simplelogin.command.plugin.unload_success", plugin.toString()),
                true
        );

        return Command.SINGLE_SUCCESS;
    }

    private static int listAvailablePlugins(CommandContext<CommandSourceStack> ctx) {
        Collection<ResourceLocation> plugins = SLRegistries.PLUGINS.list();
        ctx.getSource().sendSuccess(
                (Supplier<Component>) Component.translatable("simplelogin.command.plugin.available_plugin_header"),
                false
        );
        for (ResourceLocation plugin : plugins) {
            ctx.getSource().sendSuccess(
                    (Supplier<Component>) Component.translatable("simplelogin.command.plugin.list_member", plugin.toString()),
                    false
            );
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int listLoadedPlugins(CommandContext<CommandSourceStack> ctx) {
        Collection<ResourceLocation> plugins = PlayerLoginHandler.instance().listPlugins();
        ctx.getSource().sendSuccess(
                (Supplier<Component>) Component.translatable("simplelogin.command.plugin.loaded_plugin_header"),
                false
        );
        for (ResourceLocation plugin : plugins) {
            ctx.getSource().sendSuccess(
                    (Supplier<Component>) Component.translatable("simplelogin.command.plugin.list_member", plugin.toString()),
                    false
            );
        }
        return Command.SINGLE_SUCCESS;
    }

}
