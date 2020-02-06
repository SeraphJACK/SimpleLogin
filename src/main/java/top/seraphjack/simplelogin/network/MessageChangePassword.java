package top.seraphjack.simplelogin.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import top.seraphjack.simplelogin.SimpleLogin;
import top.seraphjack.simplelogin.server.storage.SLStorage;

import java.util.Objects;
import java.util.function.Supplier;

public class MessageChangePassword {
    private String original, to;

    public MessageChangePassword(String original, String to) {
        this.original = original;
        this.to = to;
    }

    public static void encode(MessageChangePassword msg, PacketBuffer buf) {
        buf.writeString(msg.original);
        buf.writeString(msg.to);
    }

    public static MessageChangePassword decode(PacketBuffer buf) {
        return new MessageChangePassword(buf.readString(), buf.readString());
    }

    public static void handle(MessageChangePassword msg, Supplier<NetworkEvent.Context> ctx) {
        String username = Objects.requireNonNull(ctx.get().getSender()).getGameProfile().getName();
        if (SLStorage.instance().storageProvider.checkPassword(username, msg.original)) {
            SLStorage.instance().storageProvider.changePassword(username, msg.to);
        } else {
            SimpleLogin.logger.warn("Player " + username + " tried to change password with a wrong password.");
        }
    }
}
