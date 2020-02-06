package top.seraphjack.simplelogin;

import net.minecraft.server.MinecraftServer;
import top.seraphjack.simplelogin.server.storage.Position;

public class SLConstants {
    public static final String MODID = "simplelogin";

    public static final Position defaultPosition = new Position(0, 255, 0);

    public static MinecraftServer server;
}
