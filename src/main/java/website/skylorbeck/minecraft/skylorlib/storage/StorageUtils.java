package website.skylorbeck.minecraft.skylorlib.storage;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class StorageUtils {
    public static void readNbt(NbtCompound nbt, DefaultedList<ItemStack> stacks) {
        NbtList nbtList = nbt.getList("Items", 10);
        NbtList nbtList2 = nbt.getList("Items2", 10);

        for(int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            int j = nbtCompound.getByte("Slot") & 255;
            if (j < stacks.size()) {
                stacks.set(j, ItemStack.fromNbt(nbtCompound));
            }
        }
        for(int i = 0; i < nbtList2.size(); ++i) {
            NbtCompound nbtCompound = nbtList2.getCompound(i);
            int j = nbtCompound.getByte("Slot") & 255;
            if (j+256 <= stacks.size()) {
                stacks.set(j+256, ItemStack.fromNbt(nbtCompound));
            }
        }
    }
    public static NbtCompound writeNbt(NbtCompound nbt, DefaultedList<ItemStack> stacks) {
        NbtList nbtList = new NbtList();
        NbtList nbtList2 = new NbtList();
        for(int i = 0; i < stacks.size(); ++i) {
            ItemStack itemStack = (ItemStack)stacks.get(i);
            if (!itemStack.isEmpty()) {
                NbtCompound nbtCompound = new NbtCompound();
                if (i<=255) {
                    nbtCompound.putByte("Slot", (byte) i);
                    itemStack.writeNbt(nbtCompound);
                    nbtList.add(nbtCompound);
                } else {
                    nbtCompound.putByte("Slot", (byte)(i));
                    itemStack.writeNbt(nbtCompound);
                    nbtList2.add(nbtCompound);
                }
            }
        }
        if (!nbtList.isEmpty()) {
            nbt.put("Items", nbtList);
        }
        if (!nbtList2.isEmpty()){
            nbt.put("Items2", nbtList2);
        }

        return nbt;
    }


    public static void transfer(Inventory to, ItemStack stack, int slot, @Nullable Direction side) {
        ItemStack itemStack = to.getStack(slot);
        if (StorageUtils.canInsert(to, stack, slot, side)) {
            if (itemStack.isEmpty()) {
                to.setStack(slot, stack);
                stack = ItemStack.EMPTY;
                to.markDirty();
            } else if (StorageUtils.canMergeItems(itemStack, stack)) {
                int i = stack.getMaxCount() - itemStack.getCount();
                int j = Math.min(stack.getCount(), i);
                stack.decrement(j);
                itemStack.increment(j);
            }
        }
    }
    public static boolean canInsert(Inventory inventory, ItemStack stack, int slot, Direction dir) {
        if (!inventory.isValid(slot, stack)) {
            return false;
        }
        return !(inventory instanceof SidedInventory) || ((SidedInventory)inventory).canInsert(slot, stack, dir);
    }
    public static boolean canMergeItems(ItemStack first, ItemStack second) {
        if (!first.isOf(second.getItem())) {
            return false;
        }
        if (first.getDamage() != second.getDamage()) {
            return false;
        }
        if (first.getCount() + second.getCount() > first.getMaxCount()) {
            return false;
        }
        return ItemStack.areNbtEqual(first, second);
    }
}
