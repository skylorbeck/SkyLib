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
    protected ChestTabClickable tab1;
    protected ChestTabClickable tab2;
    protected ChestTabClickable tab3;
    protected ChestTabClickable tab4;
    protected ChestTabClickable tab5;
    protected ChestTabClickable tab6;
    protected ChestTabClickable tab7;
    protected ChestTabClickable tab8;
    protected ChestTabClickable tab9;
    protected ChestTabClickable tab10;
    protected ChestTabClickable tab11;
    protected ChestTabClickable tab12;
    protected ChestTabClickable tab13;
    protected ChestTabClickable tab14;
    protected ChestTabClickable tab15;
    protected ChestTabClickable tab16;
    private HandledScreen<?> screen;
    private int x;
    private int y;
    private int w;
    private int h;
    private int tabCount;

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
        tab1 = new ChestTabClickable(this.x, this.y + 4, Text.of("Tab 1"), false);
        tab1.setToggled(true);
        tab2 = new ChestTabClickable(this.x, this.y + 20, Text.of("Tab 2"), false);
        tab3 = new ChestTabClickable(this.x, this.y + 36, Text.of("Tab 3"), false);
        tab4 = new ChestTabClickable(this.x, this.y + 52, Text.of("Tab 4"), false);
        tab5 = new ChestTabClickable(this.x, this.y + 68, Text.of("Tab 5"), false);
        tab6 = new ChestTabClickable(this.x, this.y + 84, Text.of("Tab 6"), false);
        tab7 = new ChestTabClickable(this.x, this.y + 100, Text.of("Tab 7"), false);
        tab8 = new ChestTabClickable(this.x, this.y + 116, Text.of("Tab 8"), false);

        tab9 = new ChestTabClickable(this.x + this.w, this.y + 4, Text.of("Tab 9"), true);
        tab10 = new ChestTabClickable(this.x + this.w, this.y + 20, Text.of("Tab 10"), true);
        tab11 = new ChestTabClickable(this.x + this.w, this.y + 36, Text.of("Tab 11"), true);
        tab12 = new ChestTabClickable(this.x + this.w, this.y + 52, Text.of("Tab 12"), true);
        tab13 = new ChestTabClickable(this.x + this.w, this.y + 68, Text.of("Tab 13"), true);
        tab14 = new ChestTabClickable(this.x + this.w, this.y + 84, Text.of("Tab 14"), true);
        tab15 = new ChestTabClickable(this.x + this.w, this.y + 100, Text.of("Tab 15"), true);
        tab16 = new ChestTabClickable(this.x + this.w, this.y + 116, Text.of("Tab 16"), true);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        switch (tabCount) {
            case 16:
                tab9.render(matrices, mouseX, mouseY, delta);
                tab10.render(matrices, mouseX, mouseY, delta);
                tab11.render(matrices, mouseX, mouseY, delta);
                tab12.render(matrices, mouseX, mouseY, delta);
                tab13.render(matrices, mouseX, mouseY, delta);
                tab14.render(matrices, mouseX, mouseY, delta);
                tab15.render(matrices, mouseX, mouseY, delta);
                tab16.render(matrices, mouseX, mouseY, delta);
            case 8:
                tab8.render(matrices, mouseX, mouseY, delta);
            case 7:
                tab7.render(matrices, mouseX, mouseY, delta);
            case 6:
                tab6.render(matrices, mouseX, mouseY, delta);
            case 5:
                tab5.render(matrices, mouseX, mouseY, delta);
            case 4:
                tab4.render(matrices, mouseX, mouseY, delta);
            case 3:
                tab3.render(matrices, mouseX, mouseY, delta);
            case 2:
                tab2.render(matrices, mouseX, mouseY, delta);
            case 1:
                tab1.render(matrices, mouseX, mouseY, delta);
            case 0:
                break;
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
        if (tabCount > 0 && tab1.mouseClicked(mouseX, mouseY, button)) {
            tab1.setToggled(true);
            tab2.setToggled(false);
            tab3.setToggled(false);
            tab4.setToggled(false);
            tab5.setToggled(false);
            tab6.setToggled(false);
            tab7.setToggled(false);
            tab8.setToggled(false);
            tab9.setToggled(false);
            tab10.setToggled(false);
            tab11.setToggled(false);
            tab12.setToggled(false);
            tab13.setToggled(false);
            tab14.setToggled(false);
            tab15.setToggled(false);
            tab16.setToggled(false);
            ((AbstractScreenHandler) screen.getScreenHandler()).setTab(0);
        } else if (tabCount > 1 && tab2.mouseClicked(mouseX, mouseY, button)) {
            tab1.setToggled(false);
            tab2.setToggled(true);
            tab3.setToggled(false);
            tab4.setToggled(false);
            tab5.setToggled(false);
            tab6.setToggled(false);
            tab7.setToggled(false);
            tab8.setToggled(false);
            tab9.setToggled(false);
            tab10.setToggled(false);
            tab11.setToggled(false);
            tab12.setToggled(false);
            tab13.setToggled(false);
            tab14.setToggled(false);
            tab15.setToggled(false);
            tab16.setToggled(false);
            ((AbstractScreenHandler) screen.getScreenHandler()).setTab(1);
        } else if (tabCount > 2 && tab3.mouseClicked(mouseX, mouseY, button)) {
            tab1.setToggled(false);
            tab2.setToggled(false);
            tab3.setToggled(true);
            tab4.setToggled(false);
            tab5.setToggled(false);
            tab6.setToggled(false);
            tab7.setToggled(false);
            tab8.setToggled(false);
            tab9.setToggled(false);
            tab10.setToggled(false);
            tab11.setToggled(false);
            tab12.setToggled(false);
            tab13.setToggled(false);
            tab14.setToggled(false);
            tab15.setToggled(false);
            tab16.setToggled(false);
            ((AbstractScreenHandler) screen.getScreenHandler()).setTab(2);
        } else if (tabCount > 3 && tab4.mouseClicked(mouseX, mouseY, button)) {
            tab1.setToggled(false);
            tab2.setToggled(false);
            tab3.setToggled(false);
            tab4.setToggled(true);
            tab5.setToggled(false);
            tab6.setToggled(false);
            tab7.setToggled(false);
            tab8.setToggled(false);
            tab9.setToggled(false);
            tab10.setToggled(false);
            tab11.setToggled(false);
            tab12.setToggled(false);
            tab13.setToggled(false);
            tab14.setToggled(false);
            tab15.setToggled(false);
            tab16.setToggled(false);
            ((AbstractScreenHandler) screen.getScreenHandler()).setTab(3);
        } else if (tabCount > 4 && tab5.mouseClicked(mouseX, mouseY, button)) {
            tab1.setToggled(false);
            tab2.setToggled(false);
            tab3.setToggled(false);
            tab4.setToggled(false);
            tab5.setToggled(true);
            tab6.setToggled(false);
            tab7.setToggled(false);
            tab8.setToggled(false);
            tab9.setToggled(false);
            tab10.setToggled(false);
            tab11.setToggled(false);
            tab12.setToggled(false);
            tab13.setToggled(false);
            tab14.setToggled(false);
            tab15.setToggled(false);
            tab16.setToggled(false);
            ((AbstractScreenHandler) screen.getScreenHandler()).setTab(4);
        } else if (tabCount > 5 && tab6.mouseClicked(mouseX, mouseY, button)) {
            tab1.setToggled(false);
            tab2.setToggled(false);
            tab3.setToggled(false);
            tab4.setToggled(false);
            tab5.setToggled(false);
            tab6.setToggled(true);
            tab7.setToggled(false);
            tab8.setToggled(false);
            tab9.setToggled(false);
            tab10.setToggled(false);
            tab11.setToggled(false);
            tab12.setToggled(false);
            tab13.setToggled(false);
            tab14.setToggled(false);
            tab15.setToggled(false);
            tab16.setToggled(false);
            ((AbstractScreenHandler) screen.getScreenHandler()).setTab(5);
        } else if (tabCount > 6 && tab7.mouseClicked(mouseX, mouseY, button)) {
            tab1.setToggled(false);
            tab2.setToggled(false);
            tab3.setToggled(false);
            tab4.setToggled(false);
            tab5.setToggled(false);
            tab6.setToggled(false);
            tab7.setToggled(true);
            tab8.setToggled(false);
            tab9.setToggled(false);
            tab10.setToggled(false);
            tab11.setToggled(false);
            tab12.setToggled(false);
            tab13.setToggled(false);
            tab14.setToggled(false);
            tab15.setToggled(false);
            tab16.setToggled(false);
            ((AbstractScreenHandler) screen.getScreenHandler()).setTab(6);
        } else if (tabCount > 7 && tab8.mouseClicked(mouseX, mouseY, button)) {
            tab1.setToggled(false);
            tab2.setToggled(false);
            tab3.setToggled(false);
            tab4.setToggled(false);
            tab5.setToggled(false);
            tab6.setToggled(false);
            tab7.setToggled(false);
            tab8.setToggled(true);
            tab9.setToggled(false);
            tab10.setToggled(false);
            tab11.setToggled(false);
            tab12.setToggled(false);
            tab13.setToggled(false);
            tab14.setToggled(false);
            tab15.setToggled(false);
            tab16.setToggled(false);
            ((AbstractScreenHandler) screen.getScreenHandler()).setTab(7);
        } else if (tabCount > 15) {
            if (tab9.mouseClicked(mouseX, mouseY, button)) {
                tab1.setToggled(false);
                tab2.setToggled(false);
                tab3.setToggled(false);
                tab4.setToggled(false);
                tab5.setToggled(false);
                tab6.setToggled(false);
                tab7.setToggled(false);
                tab8.setToggled(false);
                tab9.setToggled(true);
                tab10.setToggled(false);
                tab11.setToggled(false);
                tab12.setToggled(false);
                tab13.setToggled(false);
                tab14.setToggled(false);
                tab15.setToggled(false);
                tab16.setToggled(false);
                ((AbstractScreenHandler) screen.getScreenHandler()).setTab(8);
            } else if (tab10.mouseClicked(mouseX, mouseY, button)) {
                tab1.setToggled(false);
                tab2.setToggled(false);
                tab3.setToggled(false);
                tab4.setToggled(false);
                tab5.setToggled(false);
                tab6.setToggled(false);
                tab7.setToggled(false);
                tab8.setToggled(false);
                tab9.setToggled(false);
                tab10.setToggled(true);
                tab11.setToggled(false);
                tab12.setToggled(false);
                tab13.setToggled(false);
                tab14.setToggled(false);
                tab15.setToggled(false);
                tab16.setToggled(false);
                ((AbstractScreenHandler) screen.getScreenHandler()).setTab(9);
            } else if (tab11.mouseClicked(mouseX, mouseY, button)) {
                tab1.setToggled(false);
                tab2.setToggled(false);
                tab3.setToggled(false);
                tab4.setToggled(false);
                tab5.setToggled(false);
                tab6.setToggled(false);
                tab7.setToggled(false);
                tab8.setToggled(false);
                tab9.setToggled(false);
                tab10.setToggled(false);
                tab11.setToggled(true);
                tab12.setToggled(false);
                tab13.setToggled(false);
                tab14.setToggled(false);
                tab15.setToggled(false);
                tab16.setToggled(false);
                ((AbstractScreenHandler) screen.getScreenHandler()).setTab(10);
            } else if (tab12.mouseClicked(mouseX, mouseY, button)) {
                tab1.setToggled(false);
                tab2.setToggled(false);
                tab3.setToggled(false);
                tab4.setToggled(false);
                tab5.setToggled(false);
                tab6.setToggled(false);
                tab7.setToggled(false);
                tab8.setToggled(false);
                tab9.setToggled(false);
                tab10.setToggled(false);
                tab11.setToggled(false);
                tab12.setToggled(true);
                tab13.setToggled(false);
                tab14.setToggled(false);
                tab15.setToggled(false);
                tab16.setToggled(false);
                ((AbstractScreenHandler) screen.getScreenHandler()).setTab(11);
            } else if (tab13.mouseClicked(mouseX, mouseY, button)) {
                tab1.setToggled(false);
                tab2.setToggled(false);
                tab3.setToggled(false);
                tab4.setToggled(false);
                tab5.setToggled(false);
                tab6.setToggled(false);
                tab7.setToggled(false);
                tab8.setToggled(false);
                tab9.setToggled(false);
                tab10.setToggled(false);
                tab11.setToggled(false);
                tab12.setToggled(false);
                tab13.setToggled(true);
                tab14.setToggled(false);
                tab15.setToggled(false);
                tab16.setToggled(false);
                ((AbstractScreenHandler) screen.getScreenHandler()).setTab(12);
            } else if (tab14.mouseClicked(mouseX, mouseY, button)) {
                tab1.setToggled(false);
                tab2.setToggled(false);
                tab3.setToggled(false);
                tab4.setToggled(false);
                tab5.setToggled(false);
                tab6.setToggled(false);
                tab7.setToggled(false);
                tab8.setToggled(false);
                tab9.setToggled(false);
                tab10.setToggled(false);
                tab11.setToggled(false);
                tab12.setToggled(false);
                tab13.setToggled(false);
                tab14.setToggled(true);
                tab15.setToggled(false);
                tab16.setToggled(false);
                ((AbstractScreenHandler) screen.getScreenHandler()).setTab(13);
            } else if (tab15.mouseClicked(mouseX, mouseY, button)) {
                tab1.setToggled(false);
                tab2.setToggled(false);
                tab3.setToggled(false);
                tab4.setToggled(false);
                tab5.setToggled(false);
                tab6.setToggled(false);
                tab7.setToggled(false);
                tab8.setToggled(false);
                tab9.setToggled(false);
                tab10.setToggled(false);
                tab11.setToggled(false);
                tab12.setToggled(false);
                tab13.setToggled(false);
                tab14.setToggled(false);
                tab15.setToggled(true);
                tab16.setToggled(false);
                ((AbstractScreenHandler) screen.getScreenHandler()).setTab(14);
            } else if (tab16.mouseClicked(mouseX, mouseY, button)) {
                tab1.setToggled(false);
                tab2.setToggled(false);
                tab3.setToggled(false);
                tab4.setToggled(false);
                tab5.setToggled(false);
                tab6.setToggled(false);
                tab7.setToggled(false);
                tab8.setToggled(false);
                tab9.setToggled(false);
                tab10.setToggled(false);
                tab11.setToggled(false);
                tab12.setToggled(false);
                tab13.setToggled(false);
                tab14.setToggled(false);
                tab15.setToggled(false);
                tab16.setToggled(true);
                ((AbstractScreenHandler) screen.getScreenHandler()).setTab(15);
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
        switch (tabCount) {
            case 16:
                result = !tab1.isHovered()
                        && !tab2.isHovered()
                        && !tab3.isHovered()
                        && !tab4.isHovered()
                        && !tab5.isHovered()
                        && !tab6.isHovered()
                        && !tab7.isHovered()
                        && !tab8.isHovered()
                        && !tab9.isHovered()
                        && !tab10.isHovered()
                        && !tab11.isHovered()
                        && !tab12.isHovered()
                        && !tab13.isHovered()
                        && !tab14.isHovered()
                        && !tab15.isHovered()
                        && !tab16.isHovered();
                break;
            case 8:
                result = !tab1.isHovered()
                        && !tab2.isHovered()
                        && !tab3.isHovered()
                        && !tab4.isHovered()
                        && !tab5.isHovered()
                        && !tab6.isHovered()
                        && !tab7.isHovered()
                        && !tab8.isHovered();
                break;
            case 7:
                result = !tab1.isHovered()
                        && !tab2.isHovered()
                        && !tab3.isHovered()
                        && !tab4.isHovered()
                        && !tab5.isHovered()
                        && !tab6.isHovered()
                        && !tab7.isHovered();
                break;
            case 6:
                result = !tab1.isHovered()
                        && !tab2.isHovered()
                        && !tab3.isHovered()
                        && !tab4.isHovered()
                        && !tab5.isHovered()
                        && !tab6.isHovered();
                break;
            case 5:
                result = !tab1.isHovered()
                        && !tab2.isHovered()
                        && !tab3.isHovered()
                        && !tab4.isHovered()
                        && !tab5.isHovered();
                break;
            case 4:
                result = !tab1.isHovered()
                        && !tab2.isHovered()
                        && !tab3.isHovered()
                        && !tab4.isHovered();
                break;
            case 3:
                result = !tab1.isHovered()
                        && !tab2.isHovered()
                        && !tab3.isHovered();
                break;
            case 2:
                result = !tab1.isHovered()
                        && !tab2.isHovered();
                break;
            case 1:
                result = tab1.isHovered();
            case 0:
                break;
        }
        return result;
    }
}
