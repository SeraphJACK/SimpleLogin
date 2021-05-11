package top.seraphjack.simplelogin.client;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.seraphjack.simplelogin.SLConstants;
import top.seraphjack.simplelogin.SimpleLogin;
import top.seraphjack.simplelogin.network.MessageChangePassword;
import top.seraphjack.simplelogin.network.NetworkLoader;

@Mod.EventBusSubscriber(modid = SLConstants.MODID, value = Dist.CLIENT)
public final class ClientCommands {
    private static final CommandDispatcher<ISuggestionProvider> dispatcher = new CommandDispatcher<>();
    private static final LiteralArgumentBuilder<ISuggestionProvider> commandChangePassword =
            literal("sl_change_password").then(
                    argument("password", StringArgumentType.greedyString())
                            .executes(ClientCommands::changePassword)
            );

    static {
        dispatcher.register(commandChangePassword);
    }

    private static boolean active = false;

    @SubscribeEvent
    public static void openGui(GuiScreenEvent.InitGuiEvent event) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player == null) return;
        if (event.getGui() instanceof ChatScreen && !active) {
            active = true;
            player.connection.getCommandDispatcher().register(commandChangePassword);
        }
    }

    @SubscribeEvent
    public static void onChat(ClientChatEvent event) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player == null) return;
        String msg = event.getMessage();
        if (msg.startsWith("/sl_")) {
            event.setCanceled(true);

            StringReader sr = new StringReader(msg);
            sr.skip();
            ISuggestionProvider cs = player.getCommandSource();
            try {
                dispatcher.execute(sr, cs);
            } catch (CommandSyntaxException e) {
                player.sendStatusMessage(new StringTextComponent(e.getLocalizedMessage()), false);
                SimpleLogin.logger.error("Error syntax command", e);
            }
        }
    }

    private static LiteralArgumentBuilder<ISuggestionProvider> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    private static <T> RequiredArgumentBuilder<ISuggestionProvider, T> argument(String name, ArgumentType<T> argumentType) {
        return RequiredArgumentBuilder.argument(name, argumentType);
    }

    private static int changePassword(CommandContext<ISuggestionProvider> ctx) {
        if (Minecraft.getInstance().player != null) {
            String password = StringArgumentType.getString(ctx, "password");
            NetworkLoader.INSTANCE.sendToServer(new MessageChangePassword(PasswordHolder.instance().password(), password));
            PasswordHolder.instance().set(password);
        }
        return Command.SINGLE_SUCCESS;
    }
}
