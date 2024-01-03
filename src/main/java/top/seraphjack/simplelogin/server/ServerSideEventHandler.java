package top.seraphjack.simplelogin.server;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.network.PacketDistributor;
import top.seraphjack.simplelogin.SLConfig;
import top.seraphjack.simplelogin.SLConstants;
import top.seraphjack.simplelogin.SimpleLogin;
import top.seraphjack.simplelogin.network.MessageRequestLogin;
import top.seraphjack.simplelogin.network.NetworkLoader;
import top.seraphjack.simplelogin.server.handler.PlayerLoginHandler;

import java.lang.reflect.Field;

@OnlyIn(Dist.DEDICATED_SERVER)
@Mod.EventBusSubscriber(value = Dist.DEDICATED_SERVER, modid = SLConstants.MODID)
public class ServerSideEventHandler {
    @SubscribeEvent
    public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerLoginHandler.instance().playerJoin((ServerPlayer) event.getEntity());
        // noinspection InstantiationOfUtilityClass
        NetworkLoader.INSTANCE.send(new MessageRequestLogin(), PacketDistributor.PLAYER.with((ServerPlayer) event.getEntity()));
    }

    @SubscribeEvent
    public static void playerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        PlayerLoginHandler.instance().playerLeave((ServerPlayer) event.getEntity());
    }

    // Block command usage from unauthenticated players
    @SubscribeEvent
    public static void onCommand(CommandEvent event) {
        String command = event.getParseResults().getReader().getString();
        if (command.startsWith("/")) command = command.substring(1);
        SimpleLogin.logger.debug("Checking command '{}'", command);
        CommandSource realSource = getRealSource(event.getParseResults().getContext().getSource());
        if (realSource == null) {
            return;
        }
        if (!(realSource instanceof ServerPlayer cast)) {
            return;
        }
        if (PlayerLoginHandler.instance().hasPlayerLoggedIn(cast.getScoreboardName())) {
            return;
        }
        if (SLConfig.SERVER.whitelistCommands.get().contains(command)) {
            return;
        }
        SimpleLogin.logger.warn("Denied {} to execute command '{}' before login",
                event.getParseResults().getContext().getSource().getTextName(), command);
        event.setCanceled(true);
    }

    private static final Field COMMAND_SOURCE_FIELD;

    static {
        Field f;
        try {
            f = ObfuscationReflectionHelper.findField(CommandSourceStack.class, "f_81288_");
            f.setAccessible(true);
        } catch (Exception ex) {
            SimpleLogin.logger.error("Failed to get command source field", ex);
            f = null;
        }
        COMMAND_SOURCE_FIELD = f;
    }

    private static CommandSource getRealSource(CommandSourceStack sourceStack) {
        if (COMMAND_SOURCE_FIELD == null) return null;
        try {
            return (CommandSource) COMMAND_SOURCE_FIELD.get(sourceStack);
        } catch (IllegalAccessException e) {
            SimpleLogin.logger.error("Failed to get real command source", e);
            return null;
        }
    }
}
