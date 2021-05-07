package top.seraphjack.simplelogin.client;

import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.seraphjack.simplelogin.SLConstants;
import top.seraphjack.simplelogin.network.MessageLogin;
import top.seraphjack.simplelogin.network.NetworkLoader;

@Mod.EventBusSubscriber(modid = SLConstants.MODID)
public final class ClientLoader {

    @SubscribeEvent
    public static void joinServer(ClientPlayerNetworkEvent.LoggedInEvent event) {
        NetworkLoader.INSTANCE.sendToServer(new MessageLogin(PasswordHolder.instance().password()));
    }
}
