package top.seraphjack.simplelogin.command.arguments;

import net.minecraft.resources.ResourceLocation;

public final class HandlerPluginInput {
    private final ResourceLocation input;

    private HandlerPluginInput(ResourceLocation input) {
        this.input = input;
    }

    public ResourceLocation get() {
        return input;
    }

    public static HandlerPluginInput of(ResourceLocation name) {
        return new HandlerPluginInput(name);
    }
}
