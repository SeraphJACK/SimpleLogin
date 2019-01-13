package top.seraphjack.simplelogin.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import top.seraphjack.simplelogin.server.PlayerLoginHandler;

public class MessageLogin implements IMessage {
    public String pwd;

    public MessageLogin() {

    }

    public MessageLogin(String pwd) {
        this.pwd = pwd;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pwd = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, pwd);
    }

    public static class MessageHandler implements IMessageHandler<MessageLogin, IMessage> {

        @Override
        public IMessage onMessage(MessageLogin message, MessageContext ctx) {
            PlayerLoginHandler.instance().login(ctx.getServerHandler().player.getGameProfile().getName(), message.pwd);
            ctx.getServerHandler().player.getGameProfile().getName();
            return null;
        }
    }
}
