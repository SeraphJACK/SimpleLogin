package top.seraphjack.simplelogin.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.mindrot.jbcrypt.BCrypt;
import top.seraphjack.simplelogin.SLConstants;
import top.seraphjack.simplelogin.server.storage.SLStorage;

public class MessageChangePassword implements IMessage {
    private String oldPassword;
    private String newPassword;

    public MessageChangePassword() {
    }

    public MessageChangePassword(String oldPassword, String newPassword) {
        this.oldPassword = BCrypt.hashpw(oldPassword, SLConstants.defaultBcryptSalt);
        this.newPassword = BCrypt.hashpw(newPassword, SLConstants.defaultBcryptSalt);
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
            String username = player.getGameProfile().getName();
            if (SLStorage.instance().storageProvider.checkPassword(username, message.oldPassword)) {
                player.sendMessage(new TextComponentString("Password changed successfully."));
                SLStorage.instance().storageProvider.changePassword(username, message.newPassword);
            } else {
                player.sendMessage(new TextComponentString("Wrong password."));
            }
            return null;
        }
    }
}
