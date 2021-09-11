package website.skylorbeck.minecraft.skylorlib.storage;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import website.skylorbeck.minecraft.skylorlib.mixin.SlotAccessor;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final int rows;
    private final int width;
    private int curTab = 0;

    public AbstractScreenHandler(ScreenHandlerType screenHandlerType, int syncId, PlayerInventory playerInventory, Inventory inventory, int rows, int width) {
        super(screenHandlerType, syncId);
        checkSize(inventory, rows*width);
        this.rows = rows;
        this.width = width;
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);

        int i =36;

        int n;
        int m;
        if (this.rows == 1) {
            if (this.width == 1) {
                this.addSlot(new Slot(inventory, 0, 80, 18));
            } else if (this.width == 2) {
                this.addSlot(new Slot(inventory, 0, 71, 18));
                this.addSlot(new Slot(inventory, 1, 89, 18));
            } else if (this.width == 4) {
                for (int j = 0; j < 4; j++) {
                    this.addSlot(new Slot(inventory, j, 53+(j*18), 18));
                }
            } else if (this.width == 8) {
                for (int j = 0; j < 8; j++) {
                    this.addSlot(new Slot(inventory, j, 17+(j*18), 18));
                }
            } else if (this.width == 9) {
                for (int j = 0; j < 9; j++) {
                    this.addSlot(new Slot(inventory, j, 8+(j*18), 18));
                }
            } else if (this.width == 18) {
                for (int j = 0; j < 9; j++) {
                    this.addSlot(new Slot(inventory, j, 8+(j*18), 18));
                }
                for (int j = 0; j < 9; j++) {
                    this.addSlot(new Slot(inventory, j+9, 8+(j*18), 36));
                }
            }
            for (n = 0; n < 3; ++n) {
                for (m = 0; m < 9; ++m) {
                    this.addSlot(new Slot(playerInventory, m + n * 9 + 9, 8 + m * 18, 14 + n * 18 + i));
                }
            }
            for (n = 0; n < 9; ++n) {
                this.addSlot(new Slot(playerInventory, n, 8 + n * 18, 72 + i));
            }
        } else {
            for (n = 0; n < this.rows; ++n) {
                for (m = 0; m <9; ++m) {
                    int x = -10000;
                    int index = m + n * 9;
                    if (index < 54) {
                        x = 8 + m * 18;
                    }
                    this.addSlot(new Slot(inventory, index, x, 18 + n * 18));
                }
            }

            for (n = 0; n < 3; ++n) {
                for (m = 0; m < 9; ++m) {
                    this.addSlot(new Slot(playerInventory, m + n * 9 + 9, 8 + m * 18, 104 + n * 18 + i));
                }
            }

            for (n = 0; n < 9; ++n) {
                this.addSlot(new Slot(playerInventory, n, 8 + n * 18, 162 + i));
            }
        }

    }

    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index < this.rows * 9) {
                if (!this.insertItem(itemStack2, this.rows * 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, this.curTab*54, this.rows * 9, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return itemStack;
    }

    public void close(PlayerEntity player) {
        super.close(player);
        this.inventory.onClose(player);
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public int getRows() {
        return this.rows;
    }

    public void setTab(int tab){
        this.curTab = tab;
        int n;
        int m;
        for(n = 0; n < this.rows; ++n) {
            for(m = 0; m < 9; ++m) {
                int index = m +n*9;
                ((SlotAccessor)slots.get(index)).setX(-10000);
                ((SlotAccessor)slots.get(index)).setY(-10000);
                if (index>=54*curTab && index<54*(curTab+1)){
                    ((SlotAccessor)slots.get(index)).setX(8 + m * 18);
                    ((SlotAccessor)slots.get(index)).setY(18 + (n-(6*curTab)) * 18);
                }
            }
        }
    }
}
