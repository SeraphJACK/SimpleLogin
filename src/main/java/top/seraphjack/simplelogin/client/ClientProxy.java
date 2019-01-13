package top.seraphjack.simplelogin.client;

import top.seraphjack.simplelogin.CommonProxy;

public class ClientProxy extends CommonProxy {
    @Override
    public boolean isPhysicalServer() {
        return false;
    }

    @Override
    public boolean isPhysicalClient() {
        return true;
    }
}
