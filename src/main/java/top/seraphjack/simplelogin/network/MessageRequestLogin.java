package top.seraphjack.simplelogin.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import top.seraphjack.simplelogin.SLConfig;

public class MessageRequestLogin implements IMessage {

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public static class MessageHandler implements IMessageHandler<MessageRequestLogin,MessageLogin> {

        @Override
        public MessageLogin onMessage(MessageRequestLogin message, MessageContext ctx) {
            System.out.println("Get server password request.");
            return new MessageLogin(SLConfig.client.password);
        }
    }
}
