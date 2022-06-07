package website.skylorbeck.minecraft.skylorlib.storage;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LidOpenable;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import website.skylorbeck.minecraft.skylorlib.mixin.ChestInventoryAccessor;

public abstract class ExtraChestEntity extends ChestBlockEntity implements LidOpenable {
    private static String MODID = "skylorlib";
//    public DefaultedList<ItemStack> inventory;
    private final ViewerCountManager stateManager;
    private final static Identifier identifier = new Identifier("textures/atlas/chest.png");
    private static String base = MODID + ":entity/chest/";
    private final String addition;
    private final boolean trapped;
    public final boolean singleLatch;


    protected ExtraChestEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState,int size,String type, boolean trapped, boolean singleLatch, String MODID) {
        super(blockEntityType, blockPos, blockState);
        this.addition = type;
        this.trapped= trapped;
        this.singleLatch = singleLatch;
        ExtraChestEntity.MODID = MODID;
        base = MODID + ":entity/chest/";
        ((ChestInventoryAccessor)this).setInventory(DefaultedList.ofSize(size, ItemStack.EMPTY));
        this.stateManager = new ViewerCountManager() {
                protected void onContainerOpen(World world, BlockPos pos, BlockState state) {
                }

                protected void onContainerClose(World world, BlockPos pos, BlockState state) {
                }

                protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
                }

                protected boolean isPlayerViewing(PlayerEntity player) {
                    if (!(player.currentScreenHandler instanceof GenericContainerScreenHandler || player.currentScreenHandler instanceof AbstractScreenHandler)) {
                        return false;
                    } else if (player.currentScreenHandler instanceof AbstractScreenHandler) {
                        Inventory inventory = ((AbstractScreenHandler) player.currentScreenHandler).getInventory();
                        return inventory == ExtraChestEntity.this || inventory instanceof DoubleInventory && ((DoubleInventory) inventory).isPart(ExtraChestEntity.this);
                    } else {
                        Inventory inventory = ((GenericContainerScreenHandler) player.currentScreenHandler).getInventory();
                        return inventory == ExtraChestEntity.this || inventory instanceof DoubleInventory && ((DoubleInventory) inventory).isPart(ExtraChestEntity.this);
                    }
                }
        };
    }

    public SpriteIdentifier getSpriteIdentifier(){
        return new SpriteIdentifier(identifier,new Identifier((trapped ? base+"trapped/" : base)+addition));
    }
    public SpriteIdentifier getSpriteIdentifierLeft(){
        return new SpriteIdentifier(identifier,new Identifier((trapped ? base+"trapped/" : base)+addition+"_left"));
    }
    public SpriteIdentifier getSpriteIdentifierRight(){
        return new SpriteIdentifier(identifier,new Identifier((trapped ? base+"trapped/" : base)+addition+"_right"));
    }

    public int size() {
        return ((ChestInventoryAccessor)this).getInventory().size();
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        ((ChestInventoryAccessor)this).setInventory(DefaultedList.ofSize(this.size(), ItemStack.EMPTY));
        if (!this.deserializeLootTable(nbt)) {
            StorageUtils.readNbt(nbt,((ChestInventoryAccessor)this).getInventory());
        }

    }

    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (!this.serializeLootTable(nbt)) {
            StorageUtils.writeNbt(nbt,((ChestInventoryAccessor)this).getInventory());
        }
    }

    static void playSound(World world, BlockPos pos, BlockState state, SoundEvent soundEvent) {
        ChestType chestType = (ChestType) state.get(ChestBlock.CHEST_TYPE);
        if (chestType != ChestType.LEFT) {
            double d = (double) pos.getX() + 0.5D;
            double e = (double) pos.getY() + 0.5D;
            double f = (double) pos.getZ() + 0.5D;
            if (chestType == ChestType.RIGHT) {
                Direction direction = ChestBlock.getFacing(state);
                d += (double) direction.getOffsetX() * 0.5D;
                f += (double) direction.getOffsetZ() * 0.5D;
            }

            world.playSound((PlayerEntity) null, d, e, f, soundEvent, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
        }
    }


    protected DefaultedList<ItemStack> getInvStackList() {
        return ((ChestInventoryAccessor)this).getInventory();
    }

    public void setInvStackList(DefaultedList<ItemStack> list) {
        for (int i = 0; i < list.size(); i++) {
            ((ChestInventoryAccessor)this).getInventory().set(i,list.get(i));
        }
    }

    public static int getPlayersLookingInChestCount(BlockView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.hasBlockEntity()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ExtraChestEntity) {
                return ((ExtraChestEntity) blockEntity).stateManager.getViewerCount();
            }
        }

        return 0;
    }

    public static void copyInventory(ExtraChestEntity from, ExtraChestEntity to) {
        DefaultedList<ItemStack> defaultedList = from.getInvStackList();
        from.setInvStackList(to.getInvStackList());
        to.setInvStackList(defaultedList);
    }

    public void onScheduledTick() {
        if (!this.removed) {
            this.stateManager.updateViewerCount(this.getWorld(), this.getPos(), this.getCachedState());
        }

    }

    protected void onInvOpenOrClose(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
        Block block = state.getBlock();
        world.addSyncedBlockEvent(pos, block, 1, newViewerCount);
    }

    //override these
    protected Text getContainerName() {
        return new TranslatableTextContent("container.chest");
    }
    protected Text getDoubleContainerName(){
        return new TranslatableTextContent("container.chestDouble");
    }
    protected ScreenHandler createDoubleScreenHandler(int syncid, PlayerInventory playerInventory, Inventory inventory){
        return GenericContainerScreenHandler.createGeneric9x6(syncid,playerInventory,inventory);
    }
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, this);
    }

}