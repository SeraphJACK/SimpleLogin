package top.seraphjack.simplelogin.server.handler;

import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.concurrent.ScheduledExecutorService;

public interface HandlerPlugin {
    default void enable(ScheduledExecutorService executor) {
    }

    /**
     * Called only once when player joined the server
     *
     * @param player player joining in
     */
    void preLogin(ServerPlayerEntity player, Login login);

    /**
     * Called only once when the player log in successfully
     *
     * @param player player logging in
     */
    void postLogin(ServerPlayerEntity player, Login login);

    /**
     * Called only once before the player leaving server
     *
     * @param player player leaving server
     */
    void preLogout(ServerPlayerEntity player);
}
