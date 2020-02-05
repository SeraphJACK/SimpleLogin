package top.seraphjack.simplelogin.server.capability;

public interface ISLEntry {
    String getPassword();

    void setPassword(String password);

    void setGameType(int gameType);

    int getGameType();
}
