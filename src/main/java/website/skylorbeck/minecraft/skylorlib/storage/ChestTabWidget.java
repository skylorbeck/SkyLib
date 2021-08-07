package website.skylorbeck.minecraft.skylorlib.storage;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class ChestTabWidget extends DrawableHelper implements Drawable, Element, Selectable {
    protected ChestTabClickable[] tabs;
    private final HandledScreen<?> screen;
    private final int x;
    private final int y;
    private final int w;
    private final int h;
    private final int tabCount;

    public ChestTabWidget(HandledScreen<?> screen, int x, int y, int w, int h, int tabCount) {
        this.screen = screen;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.tabCount = tabCount;
        this.init();
    }

    public void init() {

        tabs = new ChestTabClickable[tabCount];
        for (int i = 0; i < tabCount; i++) {
            boolean flipped = i >= 8;
            tabs[i] = new ChestTabClickable(this.x+(flipped ? this.w : 0), this.y + 4 + (16 * i) - (flipped ? 128 : 0), Text.of("Tab " + i), flipped);
        }
        if (tabs.length > 0) {
            tabs[0].setToggled(true);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        for (int i = 0; i <tabCount ; i++) {
            tabs[i].render(matrices,mouseX,mouseY,delta);
        }
    }

    @Override
    public SelectionType getType() {
        return null;
    }

    @Override
    public boolean isNarratable() {
        return Selectable.super.isNarratable();
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        Element.super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (int i = 0; i <tabCount ; i++) {
            if (tabs[i].mouseClicked(mouseX,mouseY,button)){
                for (ChestTabClickable tab:tabs) {
                    tab.setToggled(false);
                }
                tabs[i].setToggled(true);
                ((AbstractScreenHandler) screen.getScreenHandler()).setTab(i);
            }
        }
        return Element.super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return Element.super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return Element.super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return Element.super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return Element.super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return Element.super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return Element.super.charTyped(chr, modifiers);
    }

    @Override
    public boolean changeFocus(boolean lookForwards) {
        return Element.super.changeFocus(lookForwards);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return Element.super.isMouseOver(mouseX, mouseY);
    }

    public boolean isClickOutsideBounds() {
        boolean result = true;
        for (ChestTabClickable tab:tabs) {
            if (tab.isHovered()){
                result = false;
            }
        }
        return result;
    }
}
