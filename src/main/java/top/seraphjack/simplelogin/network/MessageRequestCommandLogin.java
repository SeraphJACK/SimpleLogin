package top.seraphjack.simplelogin.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import top.seraphjack.simplelogin.client.Notifier;

public class MessageRequestCommandLogin implements IMessage {
    private boolean isRegister;

    public MessageRequestCommandLogin() {
        // NO-OP
    }
    public MessageRequestCommandLogin(boolean isRegister) {
        this.isRegister = isRegister;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        isRegister = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(isRegister);
    }

    public static class MessageHandler implements IMessageHandler<MessageRequestCommandLogin, IMessage> {

        @Override
        public IMessage onMessage(MessageRequestCommandLogin message, MessageContext ctx) {
            if (message.isRegister) {
                Notifier.instance().notifyLogin();
            } else {
                Notifier.instance().notifyRegister();
            }
            return null;
        }
    }
}
