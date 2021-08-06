package website.skylorbeck.minecraft.skylorlib.furnaces;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.*;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

public abstract class AbstractExtraFurnaceBlockEntity extends LockableContainerBlockEntity implements SidedInventory, RecipeUnlocker, RecipeInputProvider {//this is essentially a copy of minecrafts default furnace code but without the constant rebuilding of the library on every smelt
    private static final int[] TOP_SLOTS = new int[]{0};
    private static final int[] BOTTOM_SLOTS = new int[]{2, 1};
    private static final int[] SIDE_SLOTS = new int[]{1};
    public DefaultedList<ItemStack> inventory;
    public int burnTime;
    public int fuelTime;
    public int cookTime;
    public int cookTimeTotal;
    protected final PropertyDelegate propertyDelegate;
    private final Object2IntOpenHashMap<Identifier> recipesUsed;
    protected final RecipeType<? extends AbstractCookingRecipe> recipeType;

    @Override
    public Text getContainerName() {
        return new TranslatableText("container.furnace");
    }
    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new FurnaceScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }
    protected AbstractExtraFurnaceBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state, RecipeType<? extends AbstractCookingRecipe> recipeType) {
        super(blockEntityType,pos,state);
        this.inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
        this.propertyDelegate = new PropertyDelegate() {
            public int get(int index) {
                return switch (index) {
                    case 0 -> AbstractExtraFurnaceBlockEntity.this.burnTime;
                    case 1 -> AbstractExtraFurnaceBlockEntity.this.fuelTime;
                    case 2 -> AbstractExtraFurnaceBlockEntity.this.cookTime;
                    case 3 -> AbstractExtraFurnaceBlockEntity.this.cookTimeTotal;
                    default -> 0;
                };
            }

            public void set(int index, int value) {
                switch (index) {
                    case 0 -> AbstractExtraFurnaceBlockEntity.this.burnTime = value;
                    case 1 -> AbstractExtraFurnaceBlockEntity.this.fuelTime = value;
                    case 2 -> AbstractExtraFurnaceBlockEntity.this.cookTime = value;
                    case 3 -> AbstractExtraFurnaceBlockEntity.this.cookTimeTotal = value;
                }

            }

            public int size() {
                return 4;
            }
        };
        this.recipesUsed = new Object2IntOpenHashMap();
        this.recipeType = recipeType;
    }

    public boolean isBurning() {
        return this.burnTime > 0;
    }
    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt( tag);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.readNbt(tag, this.inventory);
        this.burnTime = tag.getShort("BurnTime");
        this.cookTime = tag.getShort("CookTime");
        this.cookTimeTotal = tag.getShort("CookTimeTotal");
        this.fuelTime = this.getFuelTime(this.inventory.get(1));
        NbtCompound compoundTag = tag.getCompound("RecipesUsed");

        for (String string : compoundTag.getKeys()) {
            this.recipesUsed.put(new Identifier(string), compoundTag.getInt(string));
        }

    }
    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putShort("BurnTime", (short)this.burnTime);
        tag.putShort("CookTime", (short)this.cookTime);
        tag.putShort("CookTimeTotal", (short)this.cookTimeTotal);
        Inventories.writeNbt(tag, this.inventory);
        NbtCompound compoundTag = new NbtCompound();
        this.recipesUsed.forEach((identifier, integer) -> compoundTag.putInt(identifier.toString(), integer));
        tag.put("RecipesUsed", compoundTag);
        return tag;
    }

    public static void tick(World world, BlockPos pos, BlockState state, AbstractExtraFurnaceBlockEntity blockEntity) {
        boolean bl = blockEntity.isBurning();
        boolean bl2 = false;
        if (blockEntity.isBurning()) {
            --blockEntity.burnTime;
        }

        ItemStack itemStack = (ItemStack)blockEntity.inventory.get(1);
        if (blockEntity.isBurning() || !itemStack.isEmpty() && !((ItemStack)blockEntity.inventory.get(0)).isEmpty()) {
            Recipe<?> recipe = world.getRecipeManager().getFirstMatch(blockEntity.recipeType, blockEntity, world).orElse(null);
            int i = blockEntity.getMaxCountPerStack();
            if (!blockEntity.isBurning() && canAcceptRecipeOutput(recipe, blockEntity.inventory, i)) {
                blockEntity.burnTime = blockEntity.getFuelTime(itemStack);
                blockEntity.fuelTime = blockEntity.burnTime;
                if (blockEntity.isBurning()) {
                    bl2 = true;
                    if (!itemStack.isEmpty()) {
                        Item item = itemStack.getItem();
                        itemStack.decrement(1);
                        if (itemStack.isEmpty()) {
                            Item item2 = item.getRecipeRemainder();
                            blockEntity.inventory.set(1, item2 == null ? ItemStack.EMPTY : new ItemStack(item2));
                        }
                    }
                }
            }

            if (blockEntity.isBurning() && canAcceptRecipeOutput(recipe, blockEntity.inventory, i)) {
                ++blockEntity.cookTime;
                if (blockEntity.cookTime == blockEntity.cookTimeTotal) {
                    blockEntity.cookTime = 0;
                    /*boolean iron = blockEntity instanceof IronExtraFurnaceEntity || blockEntity instanceof IronExtraSmokerEntity || blockEntity instanceof IronExtraBlastEntity;
                    boolean gold = blockEntity instanceof GoldExtraFurnaceEntity || blockEntity instanceof GoldExtraSmokerEntity || blockEntity instanceof GoldExtraBlastEntity;
                    boolean diamond = blockEntity instanceof DiamondExtraFurnaceEntity || blockEntity instanceof DiamondExtraSmokerEntity || blockEntity instanceof DiamondExtraBlastEntity;
                    boolean amethyst = blockEntity instanceof AmethystExtraFurnaceEntity || blockEntity instanceof AmethystExtraSmokerEntity || blockEntity instanceof AmethystExtraBlastEntity;

                    if (iron){
                        blockEntity.cookTimeTotal = (int) (getCookTime(world, blockEntity.recipeType, blockEntity)*0.8);
                    } else if (gold){
                        blockEntity.cookTimeTotal = (int) (getCookTime(world, blockEntity.recipeType, blockEntity)*0.6);
                    } else if (diamond){
                        blockEntity.cookTimeTotal = (int) (getCookTime(world, blockEntity.recipeType, blockEntity)*0.4);
                    } else if (amethyst){
                        blockEntity.cookTimeTotal = (int) (getCookTime(world, blockEntity.recipeType, blockEntity)*0.2);
                    } else {
                        blockEntity.cookTimeTotal = getCookTime(world, blockEntity.recipeType, blockEntity);
                    }*/
                    if (craftRecipe(recipe, blockEntity.inventory, i)) {
                        blockEntity.setLastRecipe(recipe);
                    }

                    bl2 = true;
                }
            } else {
                blockEntity.cookTime = 0;
            }
        } else if (!blockEntity.isBurning() && blockEntity.cookTime > 0) {
            blockEntity.cookTime = MathHelper.clamp(blockEntity.cookTime - 2, 0, blockEntity.cookTimeTotal);
        }

        if (bl != blockEntity.isBurning()) {
            bl2 = true;
            state = (BlockState)state.with(AbstractFurnaceBlock.LIT, blockEntity.isBurning());
            world.setBlockState(pos, state, 3);
        }

        if (bl2) {
            markDirty(world, pos, state);
        }

    }

    protected static boolean canAcceptRecipeOutput(@Nullable Recipe<?> recipe, DefaultedList<ItemStack> slots, int count) {
        if (!((ItemStack)slots.get(0)).isEmpty() && recipe != null) {
            ItemStack itemStack = recipe.getOutput();
            if (itemStack.isEmpty()) {
                return false;
            } else {
                ItemStack itemStack2 = (ItemStack)slots.get(2);
                if (itemStack2.isEmpty()) {
                    return true;
                } else if (!itemStack2.isItemEqualIgnoreDamage(itemStack)) {
                    return false;
                } else if (itemStack2.getCount() < count && itemStack2.getCount() < itemStack2.getMaxCount()) {
                    return true;
                } else {
                    return itemStack2.getCount() < itemStack.getMaxCount();
                }
            }
        } else {
            return false;
        }
    }

    protected static boolean craftRecipe(@Nullable Recipe<?> recipe, DefaultedList<ItemStack> slots, int count) {
        if (recipe != null && canAcceptRecipeOutput(recipe, slots, count)) {
            ItemStack itemStack = (ItemStack)slots.get(0);
            ItemStack itemStack2 = recipe.getOutput();
            ItemStack itemStack3 = (ItemStack)slots.get(2);
            if (itemStack3.isEmpty()) {
                slots.set(2, itemStack2.copy());
            } else if (itemStack3.isOf(itemStack2.getItem())) {
                itemStack3.increment(1);
            }

            if (itemStack.isOf(Blocks.WET_SPONGE.asItem()) && !((ItemStack)slots.get(1)).isEmpty() && ((ItemStack)slots.get(1)).isOf(Items.BUCKET)) {
                slots.set(1, new ItemStack(Items.WATER_BUCKET));
            }

            itemStack.decrement(1);
            return true;
        } else {
            return false;
        }
    }

    protected int getFuelTime(ItemStack fuel) {
        if (fuel.isEmpty()) {
            return 0;
        } else {
            return FuelRegistry.INSTANCE.get(fuel.getItem());
        }
    }

    protected static int getCookTime(World world, RecipeType<? extends AbstractCookingRecipe> recipeType, Inventory inventory) {
        return (Integer)world.getRecipeManager().getFirstMatch(recipeType, inventory, world).map(AbstractCookingRecipe::getCookTime).orElse(200);
    }



    public static boolean canUseAsFuel(ItemStack stack) {
        Integer i= FuelRegistry.INSTANCE.get(stack.getItem());
        return (i != null && i > 0);
    }
    @Override
    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.DOWN) {
            return BOTTOM_SLOTS;
        } else {
            return side == Direction.UP ? TOP_SLOTS : SIDE_SLOTS;
        }
    }
    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return this.isValid(slot, stack);
    }
    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if (dir == Direction.DOWN && slot == 1) {
            Item item = stack.getItem();
            return item == Items.WATER_BUCKET || item == Items.BUCKET;
        }

        return true;
    }
    @Override
    public int size() {
        return this.inventory.size();
    }
    @Override
    public boolean isEmpty() {
        Iterator var1 = this.inventory.iterator();

        ItemStack itemStack;
        do {
            if (!var1.hasNext()) {
                return true;
            }

            itemStack = (ItemStack)var1.next();
        } while(itemStack.isEmpty());

        return false;
    }
    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }
    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.inventory, slot, amount);
    }
    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }
    public void setStack(int slot, ItemStack stack) {
        ItemStack itemStack = (ItemStack)this.inventory.get(slot);
        boolean bl = !stack.isEmpty() && stack.isItemEqualIgnoreDamage(itemStack) && ItemStack.areNbtEqual(stack, itemStack);
        this.inventory.set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }

        if (slot == 0 && !bl) {
            assert this.world != null;
            BlockEntity blockEntity = world.getBlockEntity(pos);
          /*  boolean iron = blockEntity instanceof IronExtraFurnaceEntity || blockEntity instanceof IronExtraSmokerEntity || blockEntity instanceof IronExtraBlastEntity;
            boolean gold = blockEntity instanceof GoldExtraFurnaceEntity || blockEntity instanceof GoldExtraSmokerEntity || blockEntity instanceof GoldExtraBlastEntity;
            boolean diamond = blockEntity instanceof DiamondExtraFurnaceEntity || blockEntity instanceof DiamondExtraSmokerEntity || blockEntity instanceof DiamondExtraBlastEntity;
            boolean amethyst = blockEntity instanceof AmethystExtraFurnaceEntity || blockEntity instanceof AmethystExtraSmokerEntity || blockEntity instanceof AmethystExtraBlastEntity;
            if (iron){
                this.cookTimeTotal = (int) (getCookTime(world, this.recipeType, this)*0.8);
            } else if (gold){
                this.cookTimeTotal = (int) (getCookTime(world, this.recipeType, this)*0.6);
            } else if (diamond){
                this.cookTimeTotal = (int) (getCookTime(world, this.recipeType, this)*0.4);
            } else if (amethyst){
                this.cookTimeTotal = (int) (getCookTime(world, this.recipeType, this)*0.2);
            } else {
                this.cookTimeTotal = getCookTime(world, this.recipeType, this);
            }
            */
            this.cookTime = 0;
            this.markDirty();
        }

    }
    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        assert this.world != null;
        if (this.world.getBlockEntity(this.pos) != this) {
            return false;
        } else {
            return player.squaredDistanceTo((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }
    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (slot == 2) {
            return false;
        } else if (slot != 1) {
            return true;
        } else {
            ItemStack itemStack = this.inventory.get(1);
            return canUseAsFuel(stack) || stack.getItem() == Items.BUCKET && itemStack.getItem() != Items.BUCKET;
        }
    }
    @Override
    public void clear() {
        this.inventory.clear();
    }
    @Override
    public void setLastRecipe(@Nullable Recipe<?> recipe) {
        if (recipe != null) {
            Identifier identifier = recipe.getId();
            this.recipesUsed.addTo(identifier, 1);
        }

    }

    @Override
    public Recipe<?> getLastRecipe() {
        return null;
    }
    @Override
    public void unlockLastRecipe(PlayerEntity player) {
    }

    public void dropExperience(PlayerEntity player) {
        List<Recipe<?>> list = this.dropExperience(player.world, player.getPos());
        player.unlockRecipes(list);
        this.recipesUsed.clear();
    }

    public List<Recipe<?>> dropExperience(World world, Vec3d vec3d) {
        List<Recipe<?>> list = Lists.newArrayList();

        for (Object2IntMap.Entry<Identifier> identifierEntry : this.recipesUsed.object2IntEntrySet()) {
            world.getRecipeManager().get(identifierEntry.getKey()).ifPresent((recipe) -> {
                list.add(recipe);
                dropExperience(world, vec3d, identifierEntry.getIntValue(), ((AbstractCookingRecipe) recipe).getExperience());
            });
        }

        return list;
    }


    private static void dropExperience(World world, Vec3d vec3d, int i, float f) {
        int j = MathHelper.floor((float)i * f);
        float g = MathHelper.fractionalPart((float)i * f);
        if (g != 0.0F && Math.random() < (double)g) {
            ++j;
        }

        while(j > 0) {
            int k = ExperienceOrbEntity.roundToOrbSize(j);
            j -= k;
            world.spawnEntity(new ExperienceOrbEntity(world, vec3d.x, vec3d.y, vec3d.z, k));
        }


    }

    @Override
    public void provideRecipeInputs(RecipeMatcher finder) {

        for (ItemStack itemStack : this.inventory) {
            finder.addInput(itemStack);
        }

    }

    public List<Recipe<?>> getRecipesUsedAndDropExperience(ServerWorld world, Vec3d pos) {
        List<Recipe<?>> list = Lists.newArrayList();
        ObjectIterator var4 = this.recipesUsed.object2IntEntrySet().iterator();

        while(var4.hasNext()) {
            Object2IntMap.Entry<Identifier> entry = (Object2IntMap.Entry)var4.next();
            world.getRecipeManager().get((Identifier)entry.getKey()).ifPresent((recipe) -> {
                list.add(recipe);
                dropExperience(world, pos, entry.getIntValue(), ((AbstractCookingRecipe)recipe).getExperience());
            });
        }

        return list;
    }

    public void setInvStackList(DefaultedList<ItemStack> list) {
        for (int i = 0; i < list.size(); i++) {
            this.inventory.set(i,list.get(i));
        }
    }
}
