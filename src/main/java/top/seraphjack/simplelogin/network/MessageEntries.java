package top.seraphjack.simplelogin.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import top.seraphjack.simplelogin.client.SLEntriesBuf;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Supplier;

public class MessageEntries {
    public Collection<String> entries;

    public MessageEntries(Collection<String> entries) {
        this.entries = entries;
    }

    public static void encode(MessageEntries packet, PacketBuffer buf) {
        buf.writeInt(packet.entries.size());
        packet.entries.forEach(s -> buf.writeString(s, 20));
    }

    public static MessageEntries decode(PacketBuffer buffer) {
        Collection<String> list = new LinkedList<>();
        for (int i = 0; i < buffer.readInt(); i++) {
            list.add(buffer.readString(20));
        }
        return new MessageEntries(list);
    }

    public static void handle(MessageEntries message, Supplier<NetworkEvent.Context> ctx) {
        SLEntriesBuf.entries = message.entries;
    }
}
