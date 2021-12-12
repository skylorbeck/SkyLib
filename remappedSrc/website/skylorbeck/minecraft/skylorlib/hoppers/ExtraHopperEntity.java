package website.skylorbeck.minecraft.skylorlib.hoppers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class ExtraHopperEntity extends LootableContainerBlockEntity implements Hopper {
    private DefaultedList<ItemStack> inventory;
    private int transferCooldown;
    private long lastTickTime;
    private int multiplier;

    public ExtraHopperEntity(BlockEntityType<?> blockEntityType,BlockPos pos, BlockState state,int multiplier) {
        super(blockEntityType, pos, state);
        this.multiplier = multiplier;
        this.inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
        this.transferCooldown = -1;
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (!this.deserializeLootTable(nbt)) {
            Inventories.readNbt(nbt, this.inventory);
        }

        this.transferCooldown = nbt.getInt("TransferCooldown");
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (!this.serializeLootTable(nbt)) {
            Inventories.writeNbt(nbt, this.inventory);
        }

        nbt.putInt("TransferCooldown", this.transferCooldown);
        return nbt;
    }

    public int size() {
        return this.inventory.size();
    }

    public ItemStack removeStack(int slot, int amount) {
        this.checkLootInteraction((PlayerEntity)null);
        return Inventories.splitStack(this.getInvStackList(), slot, amount);
    }

    public void setStack(int slot, ItemStack stack) {
        this.checkLootInteraction((PlayerEntity)null);
        this.getInvStackList().set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }

    }

    protected Text getContainerName() {
        return new TranslatableText("container.hopper");
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, ExtraHopperEntity blockEntity) {
        --blockEntity.transferCooldown;
        blockEntity.lastTickTime = world.getTime();
        if (!blockEntity.needsCooldown()) {
            blockEntity.setCooldown(0);
            insertAndExtract(world, pos, state, blockEntity, () -> {
                return extract((World)world, blockEntity);
            });
        }
    }

    private static boolean insertAndExtract(World world, BlockPos pos, BlockState state, ExtraHopperEntity blockEntity, BooleanSupplier booleanSupplier) {
        if (world.isClient) {
            return false;
        } else {
            if (!blockEntity.needsCooldown() && (Boolean)state.get(ExtraHopperBlock.ENABLED)) {
                boolean bl = false;
                if (!blockEntity.isEmpty()) {
                    bl = insert(world, pos, state, blockEntity);
                }

                if (!blockEntity.isFull()) {
                    bl |= booleanSupplier.getAsBoolean();
                }

                if (bl) {
                    blockEntity.setCooldown(8);
                    markDirty(world, pos, state);
                    return true;
                }
            }

            return false;
        }
    }

    private boolean isFull() {
        Iterator var1 = this.inventory.iterator();
        ItemStack itemStack;
        do {
            if (!var1.hasNext()) {
                return true;
            }

            itemStack = (ItemStack)var1.next();
        } while(!itemStack.isEmpty() && itemStack.getCount() == itemStack.getMaxCount());

        return false;
    }

    private static boolean insert(World world, BlockPos pos, BlockState state, Inventory inventory) {
        Inventory inventory2 = getOutputInventory(world, pos, state);
        BlockEntity be = world.getBlockEntity(pos);

        if (inventory2 == null) {
            return false;
        } else {
            Direction direction = ((Direction)state.get(ExtraHopperBlock.FACING)).getOpposite();
            if (isInventoryFull(inventory2, direction)) {
                return false;
            } else {
                for(int i = 0; i < inventory.size(); ++i) {
                    if (!inventory.getStack(i).isEmpty()) {
                        ItemStack itemStack = inventory.getStack(i).copy();
                        assert be != null;
                        ItemStack itemStack2 = transfer(inventory, inventory2, inventory.removeStack(i, ((ExtraHopperEntity)be).getMultiplier()), direction);
                        if (itemStack2.isEmpty()) {
                            inventory2.markDirty();
                            return true;
                        }

                        inventory.setStack(i, itemStack);
                    }
                }

                return false;
            }
        }
    }

    private static IntStream getAvailableSlots(Inventory inventory, Direction side) {
        return inventory instanceof SidedInventory ? IntStream.of(((SidedInventory)inventory).getAvailableSlots(side)) : IntStream.range(0, inventory.size());
    }

    private static boolean isInventoryFull(Inventory inventory, Direction direction) {
        return getAvailableSlots(inventory, direction).allMatch((i) -> {
            ItemStack itemStack = inventory.getStack(i);
            return itemStack.getCount() >= itemStack.getMaxCount();
        });
    }

    private static boolean isInventoryEmpty(Inventory inv, Direction facing) {
        return getAvailableSlots(inv, facing).allMatch((i) -> {
            return inv.getStack(i).isEmpty();
        });
    }

    public static boolean extract(World world, Hopper hopper) {
        Inventory inventory = getInputInventory(world, hopper);
        if (inventory != null) {
            Direction direction = Direction.DOWN;
            return isInventoryEmpty(inventory, direction) ? false : getAvailableSlots(inventory, direction).anyMatch((i) -> {
                return extract(hopper, inventory, i, direction);
            });
        } else {
            Iterator var3 = getInputItemEntities(world, hopper).iterator();

            ItemEntity itemEntity;
            do {
                if (!var3.hasNext()) {
                    return false;
                }

                itemEntity = (ItemEntity)var3.next();
            } while(!extract((Inventory)hopper, (ItemEntity)itemEntity));

            return true;
        }
    }

    private static boolean extract(Hopper hopper, Inventory inventory, int slot, Direction side) {
        ItemStack itemStack = inventory.getStack(slot);

        if (!itemStack.isEmpty() && canExtract(inventory, itemStack, slot, side)) {
            ItemStack itemStack2 = itemStack.copy();
            ItemStack itemStack3 = transfer(inventory, hopper, inventory.removeStack(slot, ((ExtraHopperEntity)hopper).getMultiplier()), (Direction)null);
            if (itemStack3.isEmpty()) {
                inventory.markDirty();
                return true;
            }

            inventory.setStack(slot, itemStack2);
        }

        return false;
    }

    public static boolean extract(Inventory inventory, ItemEntity itemEntity) {
        boolean bl = false;
        ItemStack itemStack = itemEntity.getStack().copy();
        ItemStack itemStack2 = transfer((Inventory)null, inventory, itemStack, (Direction)null);
        if (itemStack2.isEmpty()) {
            bl = true;
            itemEntity.discard();
        } else {
            itemEntity.setStack(itemStack2);
        }

        return bl;
    }

    public static ItemStack transfer(@Nullable Inventory from, Inventory to, ItemStack stack, @Nullable Direction side) {
        if (to instanceof SidedInventory && side != null) {
            SidedInventory sidedInventory = (SidedInventory)to;
            int[] is = sidedInventory.getAvailableSlots(side);

            for(int i = 0; i < is.length && !stack.isEmpty(); ++i) {
                stack = transfer(from, to, stack, is[i], side);
            }
        } else {
            int j = to.size();

            for(int k = 0; k < j && !stack.isEmpty(); ++k) {
                stack = transfer(from, to, stack, k, side);
            }
        }

        return stack;
    }

    private static boolean canInsert(Inventory inventory, ItemStack stack, int slot, @Nullable Direction side) {
        if (!inventory.isValid(slot, stack)) {
            return false;
        } else {
            return !(inventory instanceof SidedInventory) || ((SidedInventory)inventory).canInsert(slot, stack, side);
        }
    }

    private static boolean canExtract(Inventory inv, ItemStack stack, int slot, Direction facing) {
        return !(inv instanceof SidedInventory) || ((SidedInventory)inv).canExtract(slot, stack, facing);
    }

    private static ItemStack transfer(@Nullable Inventory from, Inventory to, ItemStack stack, int slot, @Nullable Direction direction) {
        ItemStack itemStack = to.getStack(slot);
        if (canInsert(to, stack, slot, direction)) {
            boolean bl = false;
            boolean bl2 = to.isEmpty();
            if (itemStack.isEmpty()) {
                to.setStack(slot, stack);
                stack = ItemStack.EMPTY;
                bl = true;
            } else if (canMergeItems(itemStack, stack)) {
                int i = stack.getMaxCount() - itemStack.getCount();
                int j = Math.min(stack.getCount(), i);
                stack.decrement(j);
                itemStack.increment(j);
                bl = j > 0;
            }

            if (bl) {
                if (bl2 && to instanceof ExtraHopperEntity ExtraHopperBlockEntity) {
                    if (!ExtraHopperBlockEntity.isDisabled()) {
                        int k = 0;
                        if (from instanceof ExtraHopperEntity ExtraHopperBlockEntity2) {
                            if (ExtraHopperBlockEntity.lastTickTime >= ExtraHopperBlockEntity2.lastTickTime) {
                                k = 1;
                            }
                        }

                        ExtraHopperBlockEntity.setCooldown(8 - k);
                    }
                }

                to.markDirty();
            }
        }

        return stack;
    }

    @Nullable
    private static Inventory getOutputInventory(World world, BlockPos pos, BlockState state) {
        Direction direction = (Direction)state.get(ExtraHopperBlock.FACING);
        return getInventoryAt(world, pos.offset(direction));
    }

    @Nullable
    private static Inventory getInputInventory(World world, Hopper hopper) {
        return getInventoryAt(world, hopper.getHopperX(), hopper.getHopperY() + 1.0D, hopper.getHopperZ());
    }

    public static List<ItemEntity> getInputItemEntities(World world, Hopper hopper) {
        return (List)hopper.getInputAreaShape().getBoundingBoxes().stream().flatMap((box) -> {
            return world.getEntitiesByClass(ItemEntity.class, box.offset(hopper.getHopperX() - 0.5D, hopper.getHopperY() - 0.5D, hopper.getHopperZ() - 0.5D), EntityPredicates.VALID_ENTITY).stream();
        }).collect(Collectors.toList());
    }

    @Nullable
    public static Inventory getInventoryAt(World world, BlockPos pos) {
        return getInventoryAt(world, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D);
    }

    @Nullable
    private static Inventory getInventoryAt(World world, double x, double y, double z) {
        Inventory inventory = null;
        BlockPos blockPos = new BlockPos(x, y, z);
        BlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();
        if (block instanceof InventoryProvider) {
            inventory = ((InventoryProvider)block).getInventory(blockState, world, blockPos);
        } else if (blockState.hasBlockEntity()) {
            BlockEntity blockEntity = world.getBlockEntity(blockPos);
            if (blockEntity instanceof Inventory) {
                inventory = (Inventory)blockEntity;
                if (inventory instanceof ChestBlockEntity && block instanceof ChestBlock) {
                    inventory = ChestBlock.getInventory((ChestBlock)block, blockState, world, blockPos, true);
                }
            }
        }

        if (inventory == null) {
            List<Entity> list = world.getOtherEntities((Entity)null, new Box(x - 0.5D, y - 0.5D, z - 0.5D, x + 0.5D, y + 0.5D, z + 0.5D), EntityPredicates.VALID_INVENTORIES);
            if (!list.isEmpty()) {
                inventory = (Inventory)list.get(world.random.nextInt(list.size()));
            }
        }

        return (Inventory)inventory;
    }

    private static boolean canMergeItems(ItemStack first, ItemStack second) {
        if (!first.isOf(second.getItem())) {
            return false;
        } else if (first.getDamage() != second.getDamage()) {
            return false;
        } else if (first.getCount() > first.getMaxCount()) {
            return false;
        } else {
            return ItemStack.areNbtEqual(first, second);
        }
    }

    public double getHopperX() {
        return (double)this.pos.getX() + 0.5D;
    }

    public double getHopperY() {
        return (double)this.pos.getY() + 0.5D;
    }

    public double getHopperZ() {
        return (double)this.pos.getZ() + 0.5D;
    }

    private void setCooldown(int cooldown) {
        this.transferCooldown = cooldown;
    }

    private boolean needsCooldown() {
        return this.transferCooldown > 0;
    }

    private boolean isDisabled() {
        return this.transferCooldown > 8;
    }

    protected DefaultedList<ItemStack> getInvStackList() {
        return this.inventory;
    }

    public void setInvStackList(DefaultedList<ItemStack> list) {
        for (int i = 0; i < list.size(); i++) {
            this.inventory.set(i,list.get(i));
        }
    }

    public static void onEntityCollided(World world, BlockPos pos, BlockState state, Entity entity, ExtraHopperEntity blockEntity) {
        if (entity instanceof ItemEntity && VoxelShapes.matchesAnywhere(VoxelShapes.cuboid(entity.getBoundingBox().offset((double)(-pos.getX()), (double)(-pos.getY()), (double)(-pos.getZ()))), blockEntity.getInputAreaShape(), BooleanBiFunction.AND)) {
            insertAndExtract(world, pos, state, blockEntity, () -> {
                return extract((Inventory)blockEntity, (ItemEntity)((ItemEntity)entity));
            });
        }

    }

    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new HopperScreenHandler(syncId, playerInventory, this);
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }
}
