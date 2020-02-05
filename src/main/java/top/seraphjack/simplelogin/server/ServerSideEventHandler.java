package top.seraphjack.simplelogin.server;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.seraphjack.simplelogin.SimpleLogin;

@Mod.EventBusSubscriber(value = Dist.DEDICATED_SERVER, modid = SimpleLogin.MODID)
public class ServerSideEventHandler {
    @SubscribeEvent
    public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        //TODO invoke player handler and send login request packet
    }

    @SubscribeEvent
    public static void playerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        PlayerLoginHandler.instance().playerLeave((ServerPlayerEntity) event.getEntity());
    }

    // Block command usage from unauthenticated players
    @SubscribeEvent
    public static void onCommand(CommandEvent event) {
        // TODO

    }
}
