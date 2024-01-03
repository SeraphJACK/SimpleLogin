package top.seraphjack.simplelogin.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import top.seraphjack.simplelogin.SLConstants;
import top.seraphjack.simplelogin.SimpleLogin;
import top.seraphjack.simplelogin.network.MessageLogin;
import top.seraphjack.simplelogin.network.NetworkLoader;

@Mod.EventBusSubscriber(modid = SLConstants.MODID, value = Dist.CLIENT)
public final class ClientLoader {

    @SubscribeEvent
    public static void joinServer(ClientPlayerNetworkEvent.LoggingIn event) {
        if (event.getConnection().isMemoryConnection()) return;
        SimpleLogin.logger.debug("Sending login packet to the server...");
        NetworkLoader.INSTANCE.send(new MessageLogin(PasswordHolder.instance().password()), PacketDistributor.SERVER.noArg());
    }

    @SubscribeEvent
    public static void onClientRegisterCommand(RegisterClientCommandsEvent event) {
        ChangePasswordCommand.register(event.getDispatcher());
    }
}