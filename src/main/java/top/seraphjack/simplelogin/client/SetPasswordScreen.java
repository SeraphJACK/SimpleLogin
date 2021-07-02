package top.seraphjack.simplelogin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import top.seraphjack.simplelogin.SLConstants;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
public final class SetPasswordScreen extends Screen {
    private final Screen parentScreen;

    private TextFieldWidget textFieldPassword;
    private Button buttonRandom;
    private Button buttonComplete;

    SetPasswordScreen(Screen parent) {
        super(new TranslationTextComponent("simplelogin.password.title"));
        this.parentScreen = parent;
    }

    @Override
    protected void init() {
        this.textFieldPassword = new TextFieldWidget(this.font,
                this.width / 2 - 100, this.height / 2, 170, 20,
                new TranslationTextComponent("simplelogin.password"));
        this.textFieldPassword.setMaxStringLength(SLConstants.MAX_PASSWORD_LENGTH);
        this.textFieldPassword.setValidator((p) -> p.length() <= SLConstants.MAX_PASSWORD_LENGTH);
        this.textFieldPassword.setResponder((p) -> {
            buttonComplete.active = !p.isEmpty();
        });
        this.children.add(textFieldPassword);

        this.buttonRandom = this.addButton(new Button(this.width / 2 + 80, this.height / 2, 20, 20,
                new StringTextComponent("R"), (btn) -> {
            this.textFieldPassword.setText(UUID.randomUUID().toString());
        }));
        this.buttonComplete = this.addButton(new Button(this.width / 2 - 100, this.height / 2 + 40, 200, 20,
                DialogTexts.GUI_DONE, (btn) -> {
            String password = textFieldPassword.getText();
            if (!password.isEmpty()) {
                PasswordHolder.instance().initialize(password);
                closeScreen();
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
        String pwd = textFieldPassword.getText();
        this.init(minecraft, width, height);
        this.textFieldPassword.setText(pwd);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);

        int middle = width / 2;
        drawCenteredString(matrixStack, font, new TranslationTextComponent("simplelogin.password.title"),
                middle, height / 4, 0xFFFFFF);

        this.textFieldPassword.render(matrixStack, mouseX, mouseY, partialTicks);
        this.buttonRandom.render(matrixStack, mouseX, mouseY, partialTicks);
        this.buttonComplete.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void closeScreen() {
        if (!PasswordHolder.instance().initialized()) {
            if (!this.textFieldPassword.getText().isEmpty()) {
                PasswordHolder.instance().initialize(this.textFieldPassword.getText());
            } else {
                PasswordHolder.instance().initialize(UUID.randomUUID().toString());
            }
        }
        assert this.minecraft != null;
        this.minecraft.displayGuiScreen(this.parentScreen);
    }
}
