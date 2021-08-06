package website.skylorbeck.minecraft.skylorlib.storage;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;

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
}
