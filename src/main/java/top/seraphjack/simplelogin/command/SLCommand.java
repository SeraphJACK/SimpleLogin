package top.seraphjack.simplelogin.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import top.seraphjack.simplelogin.SLConstants;
import top.seraphjack.simplelogin.server.storage.SLStorage;

import java.io.IOException;

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

}
