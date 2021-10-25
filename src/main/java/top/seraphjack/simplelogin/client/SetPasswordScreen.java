package top.seraphjack.simplelogin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import top.seraphjack.simplelogin.SLConstants;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
public final class SetPasswordScreen extends Screen {
    private final Screen parentScreen;

    private EditBox textFieldPassword;
    private Button buttonRandom;
    private Button buttonComplete;

    SetPasswordScreen(Screen parent) {
        super(new TranslatableComponent("simplelogin.password.title"));
        this.parentScreen = parent;
    }

    @Override
    protected void init() {
        this.textFieldPassword = new EditBox(this.font,
                this.width / 2 - 100, this.height / 2, 170, 20,
                new TranslatableComponent("simplelogin.password"));
        this.textFieldPassword.setMaxLength(SLConstants.MAX_PASSWORD_LENGTH);
//        this.textFieldPassword.setValidator((p) -> p.length() <= SLConstants.MAX_PASSWORD_LENGTH);
        this.textFieldPassword.setResponder((p) -> {
            buttonComplete.active = !p.isEmpty();
        });

        this.buttonRandom = this.addWidget(new Button(this.width / 2 + 80, this.height / 2, 20, 20,
                new TextComponent("R"), (btn) -> {
            this.textFieldPassword.setValue(UUID.randomUUID().toString());
        }));
        this.buttonComplete = this.addWidget(new Button(this.width / 2 - 100, this.height / 2 + 40, 200, 20,
                CommonComponents.GUI_DONE, (btn) -> {
            String password = textFieldPassword.getValue();
            if (!password.isEmpty()) {
                PasswordHolder.instance().initialize(password);
                onClose();
            }
        }));
        this.buttonComplete.active = false;
    }

    @Override
    public void tick() {
        this.textFieldPassword.tick();
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        String pwd = textFieldPassword.getValue();
        this.init(minecraft, width, height);
        this.textFieldPassword.setValue(pwd);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(poseStack);

        int middle = width / 2;
        drawCenteredString(poseStack, font, new TranslatableComponent("simplelogin.password.title"),
                middle, height / 4, 0xFFFFFF);

        this.textFieldPassword.render(poseStack, mouseX, mouseY, partialTicks);
        this.buttonRandom.render(poseStack, mouseX, mouseY, partialTicks);
        this.buttonComplete.render(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {
        if (!PasswordHolder.instance().initialized()) {
            if (!this.textFieldPassword.getValue().isEmpty()) {
                PasswordHolder.instance().initialize(this.textFieldPassword.getValue());
            } else {
                PasswordHolder.instance().initialize(UUID.randomUUID().toString());
            }
        }
        assert this.minecraft != null;
        this.minecraft.setScreen(this.parentScreen);
    }
}
