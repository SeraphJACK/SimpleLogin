package top.seraphjack.simplelogin.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;
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

public class SLCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> command = Commands.literal("simplelogin")
                .then(Commands.literal("save").requires((c) -> c.hasPermissionLevel(3))
                        .executes(SLCommand::save))
                .then(Commands.literal("unregister").requires((c) -> c.hasPermissionLevel(3))
                        .then(Commands.argument("entry", ArgumentTypeEntryName.entryName())
                                .executes(SLCommand::unregister)))
                .then(Commands.literal("setDefaultGameType").requires((c) -> c.hasPermissionLevel(3))
                        .then(Commands.argument("entry", ArgumentTypeEntryName.entryName())
                                .then(Commands.argument("mode", IntegerArgumentType.integer(0, 3))
                                        .executes(SLCommand::setDefaultGamemode))))
                .then(Commands.literal("plugin").requires((c) -> c.hasPermissionLevel(3))
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

    private static int save(CommandContext<CommandSource> ctx) {
        try {
            long start = System.currentTimeMillis();
            SLStorage.instance().storageProvider.save();
            long cost = System.currentTimeMillis() - start;
            ctx.getSource().sendFeedback(new StringTextComponent("Done. Took " + cost + " ms."), true);
        } catch (IOException e) {
            ctx.getSource().sendFeedback(new StringTextComponent("Error during saving entries, see log for details"), false);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int unregister(CommandContext<CommandSource> ctx) {
        SLStorage.instance().storageProvider.unregister(ArgumentTypeEntryName.getEntryName(ctx, "entry"));
        ctx.getSource().sendFeedback(new StringTextComponent("Successfully unregistered."), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int setDefaultGamemode(CommandContext<CommandSource> ctx) {
        GameType gameType = GameType.values()[ctx.getArgument("mode", Integer.class) + 1];
        SLStorage.instance().storageProvider.setGameType(ArgumentTypeEntryName.getEntryName(ctx, "entry"), gameType);
        ctx.getSource().sendFeedback(new StringTextComponent("Successfully set entry default game type to " + gameType.getName() + "."), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int about(CommandContext<CommandSource> ctx) {
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        ModInfo info = FMLLoader.getLoadingModList().getMods().stream()
                .filter(modInfo -> modInfo.getModId().equals(SLConstants.MODID))
                .findAny().get();
        ctx.getSource().sendFeedback(
                new TranslationTextComponent("simplelogin.command.about.info", info.getVersion().toString()),
                false
        );
        return Command.SINGLE_SUCCESS;
    }

    private static int loadPlugin(CommandContext<CommandSource> ctx) {
        ResourceLocation plugin = ArgumentTypeHandlerPlugin.getPlugin(ctx, "plugin");
        if (!SLRegistries.PLUGINS.get(plugin).isPresent()) {
            ctx.getSource().sendFeedback(
                    new TranslationTextComponent("simplelogin.command.plugin.not_found", plugin.toString()),
                    true
            );
            return Command.SINGLE_SUCCESS;
        }
        if (PlayerLoginHandler.instance().listPlugins().contains(plugin)) {
            ctx.getSource().sendFeedback(
                    new TranslationTextComponent("simplelogin.command.plugin.already_loaded", plugin.toString()),
                    true
            );
            return Command.SINGLE_SUCCESS;
        }

        PlayerLoginHandler.instance().loadPlugin(plugin);

        ctx.getSource().sendFeedback(
                new TranslationTextComponent("simplelogin.command.plugin.load_success", plugin.toString()),
                true
        );

        return Command.SINGLE_SUCCESS;
    }

    private static int unloadPlugin(CommandContext<CommandSource> ctx) {
        ResourceLocation plugin = ArgumentTypeHandlerPlugin.getPlugin(ctx, "plugin");
        if (!SLRegistries.PLUGINS.get(plugin).isPresent()) {
            ctx.getSource().sendFeedback(
                    new TranslationTextComponent("simplelogin.command.plugin.not_found", plugin.toString()),
                    true
            );
            return Command.SINGLE_SUCCESS;
        }
        if (!PlayerLoginHandler.instance().listPlugins().contains(plugin)) {
            ctx.getSource().sendFeedback(
                    new TranslationTextComponent("simplelogin.command.plugin.not_loaded", plugin.toString()),
                    true
            );
            return Command.SINGLE_SUCCESS;
        }

        PlayerLoginHandler.instance().unloadPlugin(plugin);
        ctx.getSource().sendFeedback(
                new TranslationTextComponent("simplelogin.command.plugin.unload_success", plugin.toString()),
                true
        );

        return Command.SINGLE_SUCCESS;
    }

    private static int listAvailablePlugins(CommandContext<CommandSource> ctx) {
        Collection<ResourceLocation> plugins = SLRegistries.PLUGINS.list();
        ctx.getSource().sendFeedback(
                new TranslationTextComponent("simplelogin.command.plugin.available_plugin_header"),
                false
        );
        for (ResourceLocation plugin : plugins) {
            ctx.getSource().sendFeedback(
                    new TranslationTextComponent("simplelogin.command.plugin.list_member", plugin.toString()),
                    false
            );
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int listLoadedPlugins(CommandContext<CommandSource> ctx) {
        Collection<ResourceLocation> plugins = PlayerLoginHandler.instance().listPlugins();
        ctx.getSource().sendFeedback(
                new TranslationTextComponent("simplelogin.command.plugin.loaded_plugin_header"),
                false
        );
        for (ResourceLocation plugin : plugins) {
            ctx.getSource().sendFeedback(
                    new TranslationTextComponent("simplelogin.command.plugin.list_member", plugin.toString()),
                    false
            );
        }
        return Command.SINGLE_SUCCESS;
    }

}
