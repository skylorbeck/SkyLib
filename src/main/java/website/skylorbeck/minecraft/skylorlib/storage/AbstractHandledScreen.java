package website.skylorbeck.minecraft.skylorlib.storage;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public abstract class AbstractHandledScreen extends HandledScreen<ScreenHandler> {
    public static final Identifier TEXTURE = new Identifier("minecraft", "textures/gui/container/generic_54.png");
    public ChestTabWidget tabWidget;

    private int zOffset;

    public AbstractHandledScreen(ScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.tabWidget.mouseClicked(mouseX, mouseY, button)) {
            this.setFocused(this.tabWidget);
            return true;
        } else {
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        tabWidget.render(matrices,mouseX,mouseY,delta);
        //this is real stupid but I want my tabs to display over the chest and I can't display itemstacks without this.
        ItemStack itemStack = this.handler.getCursorStack() ;
        if (!itemStack.isEmpty()) {
            String string = null;
            if (this.cursorDragging && this.cursorDragSlots.size() > 1) {
                itemStack = itemStack.copy();
                itemStack.setCount(this.handler.getCursorStack().getCount());
                if (itemStack.isEmpty()) {
                    string = Formatting.YELLOW + "0";
                }
            }
            MatrixStack matrixStack = RenderSystem.getModelViewStack();
            matrixStack.translate(0.0D, 0.0D, 32.0D);
            RenderSystem.applyModelViewMatrix();
            this.setZOffset(200);
            this.itemRenderer.zOffset = 200.0F;
            this.itemRenderer.renderInGuiWithOverrides(itemStack, mouseX-8, mouseY-8);
            this.itemRenderer.renderGuiItemOverlay(this.textRenderer, itemStack, mouseX-8, mouseY-8 , string);
            this.setZOffset(0);
            this.itemRenderer.zOffset = 0.0F;
        }
        for (int i = 0; i <tabWidget.tabs.length ; i++) {
            itemStack = this.handler.getStacks().get(i*54);
            if (!itemStack.isEmpty()) {
                MatrixStack matrixStack = RenderSystem.getModelViewStack();
                matrixStack.translate(0.0D, 0.0D, 32.0D);
                RenderSystem.applyModelViewMatrix();
                this.setZOffset(200);
                this.itemRenderer.zOffset = 200.0F;
                ChestTabClickable tab = tabWidget.tabs[i];
                boolean flipped = i>8;
                this.itemRenderer.renderInGuiWithOverrides(itemStack, tab.x+(flipped ? 0:tab.getWidth()/2), tab.y);
                this.setZOffset(0);
                this.itemRenderer.zOffset = 0.0F;
            }
        }

        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void init() {
        this.backgroundHeight = 222;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
        this.tabWidget = new ChestTabWidget(this, (width - backgroundWidth) / 2,(height - backgroundHeight) / 2, backgroundWidth, backgroundHeight,0);
        super.init();
    }

    @Override
    protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
        return super.isClickOutsideBounds(mouseX, mouseY, left, top, button) && tabWidget.isClickOutsideBounds();
    }

    @Override
    public int getZOffset() {
        return this.zOffset;
    }
    @Override
    public void setZOffset(int zOffset) {
        this.zOffset = zOffset;
    }
}
