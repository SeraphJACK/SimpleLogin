package top.seraphjack.simplelogin.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import top.seraphjack.simplelogin.server.capability.CapabilityLoader;
import top.seraphjack.simplelogin.server.capability.IPassword;
import top.seraphjack.simplelogin.utils.SHA256;

public class MessageChangePassword implements IMessage {
    private String oldPassword;
    private String newPassword;

    public MessageChangePassword() {
    }

    public MessageChangePassword(String oldPassword, String newPassword) {
        this.oldPassword = SHA256.getSHA256(oldPassword);
        this.newPassword = SHA256.getSHA256(newPassword);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.oldPassword = ByteBufUtils.readUTF8String(buf);
        this.newPassword = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.oldPassword);
        ByteBufUtils.writeUTF8String(buf, this.newPassword);

    }

    public static class MessageHandler implements IMessageHandler<MessageChangePassword, IMessage> {
        @Override
        public IMessage onMessage(MessageChangePassword message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            IPassword cap = player.getCapability(CapabilityLoader.CAPABILITY_PASSWORD, null);
            if (cap != null) {
                if (cap.getPassword().equals(message.oldPassword)) {
                    cap.setPassword(message.newPassword);
                    player.sendMessage(new TextComponentTranslation("Password Changed."));
                } else {
                    player.sendMessage(new TextComponentTranslation("Wrong Password."));
                }
            }
            return null;
        }
    }
}
