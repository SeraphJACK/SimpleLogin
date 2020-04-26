package top.seraphjack.simplelogin.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import top.seraphjack.simplelogin.SimpleLogin;

public class NetworkLoader {
    public static SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(SimpleLogin.MODID);
    private static int msgId = 0;

    public NetworkLoader() {
        registerMessage(MessageLogin.MessageHandler.class, MessageLogin.class, Side.SERVER);
        registerMessage(MessageRequestLogin.MessageHandler.class, MessageRequestLogin.class, Side.CLIENT);
        registerMessage(MessageChangePassword.MessageHandler.class, MessageChangePassword.class, Side.SERVER);
        registerMessage(MessageRequestCommandLogin.MessageHandler.class, MessageRequestCommandLogin.class, Side.CLIENT);
    }

    private static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side) {
        INSTANCE.registerMessage(messageHandler, requestMessageType, msgId++, side);
    }
}
