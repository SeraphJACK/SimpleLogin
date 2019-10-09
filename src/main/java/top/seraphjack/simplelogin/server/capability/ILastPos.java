package top.seraphjack.simplelogin.server.capability;

import top.seraphjack.simplelogin.server.storage.Position;

public interface ILastPos {
    Position getLastPos();

    void setLastPos(Position pos);
}
