package top.seraphjack.simplelogin.client.util;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.seraphjack.simplelogin.SLConfig;
import top.seraphjack.simplelogin.SLConstants;
import top.seraphjack.simplelogin.SimpleLogin;
import top.seraphjack.simplelogin.client.ChangePasswordCommand;
import top.seraphjack.simplelogin.client.PasswordHolder;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = SLConstants.MODID)
public class ClientCommandHandler {
    private static final CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher<>();

    public static void registerCommands() {
        ChangePasswordCommand.register(dispatcher);
    }

    private static boolean active = false;

    @SubscribeEvent
    public static void onOpenGui(GuiScreenEvent.InitGuiEvent event) {
        if (event.getGui() instanceof ChatScreen && !active) {
            active = true;
            assert Minecraft.getInstance().player != null;
            Minecraft.getInstance().player.connection.getCommandDispatcher().register(
                    LiteralArgumentBuilder.<ISuggestionProvider>literal("sl_changepassword").then(
                            LiteralArgumentBuilder.<ISuggestionProvider>literal(PasswordHolder.instance().password()).requires((s) -> true)
                    )
            );
        }
    }

    @SubscribeEvent
    public static void onChat(ClientChatEvent event) {
        String msg = event.getMessage();
        if (msg.startsWith("/sl_")) {
            event.setCanceled(true);

            StringReader sr = new StringReader(msg);
            sr.skip();
            assert Minecraft.getInstance().player != null;
            CommandSource cs = Minecraft.getInstance().player.getCommandSource();

            try {
                dispatcher.execute(sr, cs);
            } catch (CommandSyntaxException e) {
                cs.sendErrorMessage(new StringTextComponent(e.getLocalizedMessage()));
                SimpleLogin.logger.error("Error syntax command", e);
            }
        }
    }
}
