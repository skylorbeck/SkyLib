package website.skylorbeck.minecraft.skylorlib.storage;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.IntStream;

public abstract class ExtraShulkerEntity extends LootableContainerBlockEntity implements SidedInventory {
    public static final String ITEMS_KEY = "Items";
    public int[] AVAILABLE_SLOTS = IntStream.range(0, 27).toArray();
    public DefaultedList<ItemStack> inventory;
    private int viewerCount;
    private AnimationStage animationStage;
    private float animationProgress;
    private float prevAnimationProgress;
    @Nullable
    private final DyeColor cachedColor;
    private static String MODID = "skylorlib";
    private final static Identifier identifier = new Identifier("textures/atlas/shulker_boxes.png");
    private static String base = MODID + ":entity/shulker/";
    private final String addition;

    public ExtraShulkerEntity(BlockEntityType blockEntityType, @Nullable DyeColor color, BlockPos pos, BlockState state,int size,String type,String MODID) {
        super(blockEntityType, pos, state);
        this.inventory = DefaultedList.ofSize(size, ItemStack.EMPTY);
        this.AVAILABLE_SLOTS = IntStream.range(0, size).toArray();
        this.animationStage = AnimationStage.CLOSED;
        this.cachedColor = color;
        ExtraShulkerEntity.MODID = MODID;
        this.addition = type;
        base = MODID + ":entity/shulker/";


    }

    public ExtraShulkerEntity(BlockEntityType blockEntityType,BlockPos pos, BlockState state,int size,String type,String MODID) {
        super(blockEntityType, pos, state);
        this.inventory = DefaultedList.ofSize(size, ItemStack.EMPTY);
        this.AVAILABLE_SLOTS = IntStream.range(0, size).toArray();
        this.animationStage = AnimationStage.CLOSED;
        this.cachedColor = ExtraShulkerBlock.getColor(state.getBlock());
        ExtraShulkerEntity.MODID = MODID;
        this.addition = type;
        base = MODID + ":entity/shulker/";
    }

    public static void tick(World world, BlockPos pos, BlockState state, ExtraShulkerEntity blockEntity) {
        blockEntity.updateAnimation(world, pos, state);
    }

    private void updateAnimation(World world, BlockPos pos, BlockState state) {
        this.prevAnimationProgress = this.animationProgress;
        switch (this.animationStage) {
            case CLOSED -> this.animationProgress = 0.0F;
            case OPENING -> {
                this.animationProgress += 0.1F;
                if (this.animationProgress >= 1.0F) {
                    this.animationStage = AnimationStage.OPENED;
                    this.animationProgress = 1.0F;
                    updateNeighborStates(world, pos, state);
                }
                this.pushEntities(world, pos, state);
            }
            case CLOSING -> {
                this.animationProgress -= 0.1F;
                if (this.animationProgress <= 0.0F) {
                    this.animationStage = AnimationStage.CLOSED;
                    this.animationProgress = 0.0F;
                    updateNeighborStates(world, pos, state);
                }
            }
            case OPENED -> this.animationProgress = 1.0F;
        }

    }

    public AnimationStage getAnimationStage() {
        return this.animationStage;
    }

    public Box getBoundingBox(BlockState state) {
        return ShulkerEntity.calculateBoundingBox((Direction)state.get(ExtraShulkerBlock.FACING), 0.5F * this.getAnimationProgress(1.0F));
    }

    private void pushEntities(World world, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof ExtraShulkerBlock) {
            Direction direction = (Direction)state.get(ExtraShulkerBlock.FACING);
            Box box = ShulkerEntity.calculateBoundingBox(direction, this.prevAnimationProgress, this.animationProgress).offset(pos);
            List<Entity> list = world.getOtherEntities((Entity)null, box);
            if (!list.isEmpty()) {
                for (Entity value : list) {
                    if (((Entity) value).getPistonBehavior() != PistonBehavior.IGNORE) {
                        ((Entity) value).move(MovementType.SHULKER_BOX, new Vec3d((box.getXLength() + 0.01D) * (double) direction.getOffsetX(), (box.getYLength() + 0.01D) * (double) direction.getOffsetY(), (box.getZLength() + 0.01D) * (double) direction.getOffsetZ()));
                    }
                }

            }
        }
    }

    public int size() {
        return this.inventory.size();
    }

    public boolean onSyncedBlockEvent(int type, int data) {
        if (type == 1) {
            this.viewerCount = data;
            if (data == 0) {
                this.animationStage = AnimationStage.CLOSING;
                updateNeighborStates(this.getWorld(), this.pos, this.getCachedState());
            }

            if (data == 1) {
                this.animationStage = AnimationStage.OPENING;
                updateNeighborStates(this.getWorld(), this.pos, this.getCachedState());
            }

            return true;
        } else {
            return super.onSyncedBlockEvent(type, data);
        }
    }

    private static void updateNeighborStates(World world, BlockPos pos, BlockState state) {
        state.updateNeighbors(world, pos, Block.NOTIFY_ALL);
    }

    public void onOpen(PlayerEntity player) {
        if (!player.isSpectator()) {
            if (this.viewerCount < 0) {
                this.viewerCount = 0;
            }

            ++this.viewerCount;
            assert this.world != null;
            this.world.addSyncedBlockEvent(this.pos, this.getCachedState().getBlock(), 1, this.viewerCount);
            if (this.viewerCount == 1) {
                this.world.emitGameEvent(player, GameEvent.CONTAINER_OPEN, this.pos);
                this.world.playSound((PlayerEntity)null, this.pos, SoundEvents.BLOCK_SHULKER_BOX_OPEN, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
            }
        }

    }

    public void onClose(PlayerEntity player) {
        if (!player.isSpectator()) {
            --this.viewerCount;
            assert this.world != null;
            this.world.addSyncedBlockEvent(this.pos, this.getCachedState().getBlock(), 1, this.viewerCount);
            if (this.viewerCount <= 0) {
                this.world.emitGameEvent(player, GameEvent.CONTAINER_CLOSE, this.pos);
                this.world.playSound((PlayerEntity)null, this.pos, SoundEvents.BLOCK_SHULKER_BOX_CLOSE, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
            }
        }

    }




    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.readInventoryNbt(nbt);
    }

    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        this.writeInventoryNbt(nbt);
    }

    public void readInventoryNbt(NbtCompound nbt) {
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (!this.deserializeLootTable(nbt) && (nbt.contains("Items", 9)||nbt.contains("Items2", 9))) {
//            Inventories.readNbt(nbt, this.inventory);
            StorageUtils.readNbt(nbt,this.inventory);
        }

    }

    public NbtCompound writeInventoryNbt(NbtCompound nbt) {
        if (!this.serializeLootTable(nbt)) {
//            Inventories.writeNbt(nbt, this.inventory, false);
            StorageUtils.writeNbt(nbt,this.inventory);
        }
        return nbt;
    }

    protected DefaultedList<ItemStack> getInvStackList() {
        return this.inventory;
    }

    public void setInvStackList(DefaultedList<ItemStack> list) {
        for (int i = 0; i < list.size(); i++) {
            this.inventory.set(i,list.get(i));
        }
    }

    public int[] getAvailableSlots(Direction side) {
        return AVAILABLE_SLOTS;
    }

    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return !(Block.getBlockFromItem(stack.getItem()) instanceof ExtraShulkerBlock);
    }

    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    public float getAnimationProgress(float delta) {
        return MathHelper.lerp(delta, this.prevAnimationProgress, this.animationProgress);
    }

    @Nullable
    public DyeColor getColor() {
        return this.cachedColor;
    }

    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new ShulkerBoxScreenHandler(syncId, playerInventory, this);
    }

    public boolean suffocates() {
        return this.animationStage == AnimationStage.CLOSED;
    }

    public static enum AnimationStage {
        CLOSED,
        OPENING,
        OPENED,
        CLOSING;
    }

    public SpriteIdentifier getSpriteIdentifier(){
        return new SpriteIdentifier(identifier,new Identifier(base+addition));
    }

    protected Text getContainerName() {
        return Text.translatable("error");
    }
}
