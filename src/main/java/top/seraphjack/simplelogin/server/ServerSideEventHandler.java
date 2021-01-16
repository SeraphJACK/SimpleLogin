package top.seraphjack.simplelogin.server;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
import top.seraphjack.simplelogin.SLConfig;
import top.seraphjack.simplelogin.SLConstants;
import top.seraphjack.simplelogin.SimpleLogin;
import top.seraphjack.simplelogin.network.MessageRequestLogin;
import top.seraphjack.simplelogin.network.NetworkLoader;

@OnlyIn(Dist.DEDICATED_SERVER)
@Mod.EventBusSubscriber(value = Dist.DEDICATED_SERVER, modid = SLConstants.MODID)
public class ServerSideEventHandler {
    @SubscribeEvent
    public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerLoginHandler.instance().playerJoin((ServerPlayerEntity) event.getPlayer());
        // noinspection InstantiationOfUtilityClass
        NetworkLoader.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer()), new MessageRequestLogin());
    }

    @SubscribeEvent
    public static void playerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        PlayerLoginHandler.instance().playerLeave((ServerPlayerEntity) event.getEntity());
    }

    // Block command usage from unauthenticated players
    @SubscribeEvent
    public static void onCommand(CommandEvent event) throws Exception {
        String command = event.getParseResults().getReader().getString();
        if (SLConfig.SERVER.commandNames.get().contains(command)) {
            SimpleLogin.logger.debug("Allowed {} to execute command {} before login", event.getParseResults().getContext().getSource().getName(), command);
            return;
        }
        if (!(event.getParseResults().getContext().getSource().getEntity() instanceof ServerPlayerEntity)) {
            return;
        }
        if (PlayerLoginHandler.instance().isPlayerInLoginList(event.getParseResults().getContext().getSource().asPlayer().getGameProfile().getName())) {
            SimpleLogin.logger.debug("Denied {} to execute command {} before login", event.getParseResults().getContext().getSource().getName(), command);
            event.setCanceled(true);
        }
    }
}
