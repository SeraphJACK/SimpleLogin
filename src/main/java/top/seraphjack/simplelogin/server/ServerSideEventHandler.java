package top.seraphjack.simplelogin.server;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import top.seraphjack.simplelogin.SLConfig;
import top.seraphjack.simplelogin.SLConstants;
import top.seraphjack.simplelogin.SimpleLogin;
import top.seraphjack.simplelogin.network.MessageRequestLogin;
import top.seraphjack.simplelogin.network.NetworkLoader;
import top.seraphjack.simplelogin.server.handler.PlayerLoginHandler;

@OnlyIn(Dist.DEDICATED_SERVER)
@Mod.EventBusSubscriber(value = Dist.DEDICATED_SERVER, modid = SLConstants.MODID)
public class ServerSideEventHandler {
    @SubscribeEvent
    public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerLoginHandler.instance().playerJoin((ServerPlayer) event.getPlayer());
        // noinspection InstantiationOfUtilityClass
        NetworkLoader.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getPlayer()), new MessageRequestLogin());
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
        CommandSourceStack source = event.getParseResults().getContext().getSource();
        if (source == null) {
            return;
        }
        if (!(source.getEntity() instanceof ServerPlayer cast)) {
            return;
        }
        if (PlayerLoginHandler.instance().hasPlayerLoggedIn(cast.getScoreboardName())) {
            return;
        }
        if (SLConfig.SERVER.whitelistCommands.get().contains(command)) {
            return;
        }
        SimpleLogin.logger.debug("Denied {} to execute command '{}' before login",
                event.getParseResults().getContext().getSource().getTextName(), command);
        event.setCanceled(true);
    }
}
