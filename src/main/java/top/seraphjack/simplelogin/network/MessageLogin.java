package top.seraphjack.simplelogin.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import top.seraphjack.simplelogin.server.handler.PlayerLoginHandler;
import top.seraphjack.simplelogin.utils.SHA256;

import java.util.function.Supplier;

import static top.seraphjack.simplelogin.SLConstants.MAX_PASSWORD_LENGTH;

public class MessageLogin {
    private final String pwd;

    public MessageLogin(String pwd) {
        this.pwd = SHA256.getSHA256(pwd);
    }

    public static void encode(MessageLogin packet, PacketBuffer buf) {
        buf.writeString(packet.pwd, MAX_PASSWORD_LENGTH);
    }

    public static MessageLogin decode(PacketBuffer buffer) {
        return new MessageLogin(buffer.readString(MAX_PASSWORD_LENGTH));
    }

    public static void handle(MessageLogin message, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity player = ctx.get().getSender();
        if (player != null) {
            PlayerLoginHandler.instance().login(player.getGameProfile().getName(), message.pwd);
        }
        ctx.get().setPacketHandled(true);
    }
}
