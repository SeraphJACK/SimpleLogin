package top.seraphjack.simplelogin.server;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import top.seraphjack.simplelogin.SLConfig;
import top.seraphjack.simplelogin.SimpleLogin;
import top.seraphjack.simplelogin.network.MessageRequestLogin;
import top.seraphjack.simplelogin.network.NetworkLoader;
import top.seraphjack.simplelogin.server.capability.CapabilityLoader;
import top.seraphjack.simplelogin.server.capability.IPassword;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

@SideOnly(Side.SERVER)
public class PlayerLoginHandler {
    private static Thread PLAYER_HANDLER_THREAD;
    private static PlayerLoginHandler INSTANCE;

    private boolean alive;
    private ConcurrentLinkedQueue<Runnable> tasks = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Login> loginList = new ConcurrentLinkedQueue<>();
    private Set<String> resetPasswordUsers = new HashSet<>();

    private PlayerLoginHandler() {
        PLAYER_HANDLER_THREAD = new Thread(() -> {
            while (alive) {
                while (!tasks.isEmpty()) {
                    tasks.poll().run();
                }

                for (Login login : loginList) {
                    EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(login.name);
                    if (player == null) {
                        loginList.remove(login);
                        return;
                    }

                    try {

                        if (System.currentTimeMillis() - login.lastRequested >= 1000) {
                            NetworkLoader.INSTANCE.sendTo(new MessageRequestLogin(), player);
                            login.lastRequested = System.currentTimeMillis();
                        }

                        player.connection.setPlayerLocation((double) login.pos.getX(), (double) login.pos.getY(), (double) login.pos.getZ(), 0, 0);

                        if (System.currentTimeMillis() - login.time >= SLConfig.server.secs * 1000) {
                            player.connection.disconnect(new TextComponentTranslation("Login timed out."));
                        }
                    } catch (Exception ignore) {

                    }
                }

                try {
                    if (loginList.isEmpty()) {
                        Thread.sleep(1000);
                    } else {
                        Thread.sleep(400);
                    }
                } catch (InterruptedException ignore) {

                }
            }
        });
        alive = true;
        PLAYER_HANDLER_THREAD.start();
    }

    public static PlayerLoginHandler instance() {
        if (INSTANCE == null) INSTANCE = new PlayerLoginHandler();
        return INSTANCE;
    }

    public void login(String id, String pwd) {
        Login login;
        try {
            login = (Login) loginList.stream().filter(l -> l.name.equals(id)).toArray()[0];
        } catch (Exception e) {
            return;
        }
        loginList.removeIf((l) -> l.name.equals(id));
        EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(id);
        if (player == null) {
            return;
        }

        IPassword capability = player.getCapability(CapabilityLoader.CAPABILITY_PASSWORD, null);
        if (capability == null) {
            SimpleLogin.logger.warn("Fail to load capability for player " + id + ". Ignoring...");
            return;
        }

        if (pwd.length() >= 100) {
            player.connection.disconnect(new TextComponentTranslation("Password too long."));
        } else if (capability.isFirst() || resetPasswordUsers.contains(id)) {
            capability.setFirst(false);
            capability.setPassword(pwd);
            processLogin(login, player);
            resetPasswordUsers.remove(id);
            SimpleLogin.logger.info("Player " + id + " has successfully registered.");
        } else if (capability.getPassword().equals(pwd)) {
            processLogin(login, player);
            SimpleLogin.logger.info("Player " + id + " has successfully logged in.");
        } else {
            player.connection.disconnect(new TextComponentTranslation("Wrong Password."));
        }
    }

    private void processLogin(Login login, EntityPlayerMP player) {
        FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
            player.setGameType(GameType.SURVIVAL);
            player.setPosition(login.pos.getX(), login.pos.getY(), login.pos.getZ());
        });
    }

    void addPlayerToLoginList(EntityPlayerMP player) {
        loginList.add(new Login(player));
        player.setGameType(GameType.SPECTATOR);
    }

    boolean isPlayerInLoginList(String id) {
        return loginList.stream().anyMatch(e -> e.name.equals(id));
    }

    void resetPassword(String id) {
        resetPasswordUsers.add(id);
    }

    String getResetPasswordUsers() {
        StringBuilder ret = new StringBuilder();
        resetPasswordUsers.stream().map(i -> i + "\n").forEach(ret::append);
        return ret.toString();
    }

    private static class Login {
        String name;
        long time;
        BlockPos pos;
        long lastRequested = 0;

        Login(EntityPlayerMP player) {
            this.name = player.getGameProfile().getName();
            this.time = System.currentTimeMillis();
            this.pos = new BlockPos(player.getPosition());
        }
    }

    void stop() {
        alive = false;
        try {
            PLAYER_HANDLER_THREAD.join();
        } catch (InterruptedException e) {
            SimpleLogin.logger.warn("Fail to shutdown login handler. ", e);
        }
    }
}
