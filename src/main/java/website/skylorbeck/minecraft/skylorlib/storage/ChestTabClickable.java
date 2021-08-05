package website.skylorbeck.minecraft.skylorlib.storage;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ChestTabClickable extends ClickableWidget {
    protected final Identifier TEXTURE = new Identifier("skylorlib", "textures/gui/tabs.png");
    protected boolean toggled;
    protected int u = 0;
    protected int v = 0;
    protected int pressedUOffset = 33;
    protected int hoverVOffset = 17;
    protected int flippedOffset = 65;
    private boolean flipped = false;

    public ChestTabClickable(int x, int y, Text message, boolean flipped) {
        super(x-29, y, 32, 16, message);
        this.flipped = flipped;
        if (flipped){
            this.x+=25;
        }
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.TEXTURE);
        RenderSystem.disableDepthTest();
        int i = this.u;
        int j = this.v;
        int x = this.x;
        if (this.toggled) {
            i += this.pressedUOffset;
//            x -= 6;
        }
        if (this.flipped){
            i += this.flippedOffset;
        }

        if (this.isHovered()) {
            j += this.hoverVOffset;
        }
        this.drawTexture(matrices, x, this.y, i, j, this.width, this.height);
        RenderSystem.enableDepthTest();
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }

    public boolean isToggled() {
        return this.toggled;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX,mouseY,button);
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return super.charTyped(chr, modifiers);
    }
}
