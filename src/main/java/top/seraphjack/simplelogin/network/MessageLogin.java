package top.seraphjack.simplelogin.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import top.seraphjack.simplelogin.server.PlayerLoginHandler;
import top.seraphjack.simplelogin.utils.SHA256;

import java.util.Objects;
import java.util.function.Supplier;

public class MessageLogin {
    private String pwd;

    public MessageLogin(String pwd) {
        this.pwd = SHA256.getSHA256(pwd);
    }

    public static void encode(MessageLogin packet, PacketBuffer buf) {
        buf.writeString(packet.pwd, 200);
    }

    public static MessageLogin decode(PacketBuffer buffer) {
        return new MessageLogin(buffer.readString(200));
    }

    public static void handle(MessageLogin message, Supplier<NetworkEvent.Context> ctx) {
        PlayerLoginHandler.instance().login(Objects.requireNonNull(ctx.get().getSender()).getGameProfile().getName(), message.pwd);
        ctx.get().setPacketHandled(true);
    }
}
