package top.seraphjack.simplelogin.server.handler;

import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.ScheduledExecutorService;

public interface HandlerPlugin {
    /**
     * Called only once when the plugin is enabled
     * @param executor executor to use for convenience
     */
    default void enable(ScheduledExecutorService executor) {
    }

    /**
     * Called only once when player joined the server
     *
     * @param player player joining in
     */
    void preLogin(ServerPlayer player, Login login);

    /**
     * Called only once when the player log in successfully
     *
     * @param player player logging in
     */
    void postLogin(ServerPlayer player, Login login);

    /**
     * Called only once before the player leaving server
     *
     * @param player player leaving server
     */
    void preLogout(ServerPlayer player);

    /**
     * Called only once when the plugin is disabled
     */
    void disable();
}
