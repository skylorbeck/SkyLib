package website.skylorbeck.minecraft.skylorlib.storage;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.block.ChestAnimationProgress;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

public abstract class ExtraChestBlock extends AbstractChestBlock<ExtraChestEntity> implements Waterloggable {
    public static final DirectionProperty FACING;
    public static final EnumProperty<ChestType> CHEST_TYPE;
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape DOUBLE_NORTH_SHAPE;
    protected static final VoxelShape DOUBLE_SOUTH_SHAPE;
    protected static final VoxelShape DOUBLE_WEST_SHAPE;
    protected static final VoxelShape DOUBLE_EAST_SHAPE;
    protected static final VoxelShape SINGLE_SHAPE;
    private static final DoubleBlockProperties.PropertyRetriever<ExtraChestEntity, Optional<Inventory>> INVENTORY_RETRIEVER;
    private static final DoubleBlockProperties.PropertyRetriever<ExtraChestEntity, Optional<NamedScreenHandlerFactory>> NAME_RETRIEVER;

    public ExtraChestBlock(Settings settings, Supplier<BlockEntityType<? extends ExtraChestEntity>> supplier) {
        super(settings, supplier);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(CHEST_TYPE, ChestType.SINGLE).with(WATERLOGGED, false));
    }

    public static DoubleBlockProperties.Type getDoubleBlockType(BlockState state) {
        ChestType chestType = state.get(CHEST_TYPE);
        if (chestType == ChestType.SINGLE) {
            return DoubleBlockProperties.Type.SINGLE;
        } else {
            return chestType == ChestType.RIGHT ? DoubleBlockProperties.Type.FIRST : DoubleBlockProperties.Type.SECOND;
        }
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        if (neighborState.isOf(this) && direction.getAxis().isHorizontal()) {
            ChestType chestType = neighborState.get(CHEST_TYPE);
            if (state.get(CHEST_TYPE) == ChestType.SINGLE && chestType != ChestType.SINGLE && state.get(FACING) == neighborState.get(FACING) && getFacing(neighborState) == direction.getOpposite()) {
                return state.with(CHEST_TYPE, chestType.getOpposite());
            }
        } else if (getFacing(state) == direction) {
            return state.with(CHEST_TYPE, ChestType.SINGLE);
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(CHEST_TYPE) == ChestType.SINGLE) {
            return SINGLE_SHAPE;
        } else {
            return switch (getFacing(state)) {
                default -> DOUBLE_NORTH_SHAPE;
                case SOUTH -> DOUBLE_SOUTH_SHAPE;
                case WEST -> DOUBLE_WEST_SHAPE;
                case EAST -> DOUBLE_EAST_SHAPE;
            };
        }
    }

    public static Direction getFacing(BlockState state) {
        Direction direction = state.get(FACING);
        return state.get(CHEST_TYPE) == ChestType.LEFT ? direction.rotateYClockwise() : direction.rotateYCounterclockwise();
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
//        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite()).with(CHEST_TYPE,ChestType.SINGLE).with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
        ChestType chestType = ChestType.SINGLE;
        Direction direction = ctx.getPlayerFacing().getOpposite();
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        boolean bl = ctx.shouldCancelInteraction();
        Direction direction2 = ctx.getSide();
        if (direction2.getAxis().isHorizontal() && bl) {
            Direction direction3 = this.getNeighborChestDirection(ctx, direction2.getOpposite());
            if (direction3 != null && direction3.getAxis() != direction2.getAxis()) {
                direction = direction3;
                chestType = direction3.rotateYCounterclockwise() == direction2.getOpposite() ? ChestType.RIGHT : ChestType.LEFT;
            }
        }
        if (chestType == ChestType.SINGLE && !bl) {
            if (direction == this.getNeighborChestDirection(ctx, direction.rotateYClockwise())) {
                chestType = ChestType.LEFT;
            } else if (direction == this.getNeighborChestDirection(ctx, direction.rotateYCounterclockwise())) {
                chestType = ChestType.RIGHT;
            }
        }
        return this.getDefaultState().with(FACING, direction).with(CHEST_TYPE, chestType).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Nullable
    private Direction getNeighborChestDirection(ItemPlacementContext ctx, Direction dir) {
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos().offset(dir));
        /*if (!blockState.isOf(Declarer.pumpkinChestBlock) && !blockState.isOf(Declarer.christmasChestBlock)){
            return null;
        }*/
        return blockState.isOf(this) && blockState.get(CHEST_TYPE) == ChestType.SINGLE ? blockState.get(FACING) : null;
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (itemStack.hasCustomName()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ExtraChestEntity) {
                ((ExtraChestEntity)blockEntity).setCustomName(itemStack.getName());
            }
        }

    }

    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof Inventory) {
                ItemScatterer.spawn(world, pos, (Inventory)blockEntity);
                world.updateComparators(pos, this);
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            NamedScreenHandlerFactory namedScreenHandlerFactory = this.createScreenHandlerFactory(state, world, pos);
            if (namedScreenHandlerFactory != null) {
                player.openHandledScreen(namedScreenHandlerFactory);
                player.incrementStat(this.getOpenStat());
                PiglinBrain.onGuardedBlockInteracted(player, true);
            }

            return ActionResult.CONSUME;
        }
    }

    protected Stat<Identifier> getOpenStat() {
        return Stats.CUSTOM.getOrCreateStat(Stats.OPEN_CHEST);
    }

    public BlockEntityType<? extends ExtraChestEntity> getExpectedEntityType() {
        return this.entityTypeRetriever.get();
    }

    @Nullable
    public static Inventory getInventory(ExtraChestBlock block, BlockState state, World world, BlockPos pos, boolean ignoreBlocked) {
        return (Inventory)((Optional)block.getBlockEntitySource(state, world, pos, ignoreBlocked).apply(INVENTORY_RETRIEVER)).orElse(null);
    }

    public DoubleBlockProperties.PropertySource<? extends ExtraChestEntity> getBlockEntitySource(BlockState state, World world, BlockPos pos, boolean ignoreBlocked) {
        BiPredicate biPredicate2;
        if (ignoreBlocked) {
            biPredicate2 = (worldx, posx) -> false;
        } else {
            biPredicate2 = (worldx, posx) -> ExtraChestBlock.isChestBlocked((WorldAccess) worldx,(BlockPos) posx);
//            biPredicate2 = ExtraChestBlock::isChestBlocked;
        }

        return DoubleBlockProperties.toPropertySource((BlockEntityType)this.entityTypeRetriever.get(), ExtraChestBlock::getDoubleBlockType, ExtraChestBlock::getFacing, FACING, state, world, pos, biPredicate2);
    }

    @Nullable
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return (NamedScreenHandlerFactory)((Optional)this.getBlockEntitySource(state, world, pos, false).apply(NAME_RETRIEVER)).orElse(null);
    }

    public static DoubleBlockProperties.PropertyRetriever<ExtraChestEntity, Float2FloatFunction> getAnimationProgressRetriever(final ChestAnimationProgress chestAnimationProgress) {
        return new DoubleBlockProperties.PropertyRetriever<ExtraChestEntity, Float2FloatFunction>() {
            @Override
            public Float2FloatFunction getFromBoth(ExtraChestEntity first, ExtraChestEntity second) {
                return (delta) -> Math.max(first.getAnimationProgress(delta), second.getAnimationProgress(delta));
            }

            @Override
            public Float2FloatFunction getFrom(ExtraChestEntity single) {
                return single::getAnimationProgress;
            }

            @Override
            public Float2FloatFunction getFallback() {
                return (delta) -> delta;
            }
        };
    }

    @Override
    public abstract BlockEntity createBlockEntity(BlockPos pos, BlockState state);
    //        return new ExtraChestEntity(pos, state);


    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? checkType(type, this.getExpectedEntityType(), ExtraChestEntity::clientTick) : null;
    }

    public static boolean isChestBlocked(WorldAccess world, BlockPos pos) {
        return hasBlockOnTop(world, pos) || hasOcelotOnTop(world, pos);
    }

    private static boolean hasBlockOnTop(BlockView world, BlockPos pos) {
        BlockPos blockPos = pos.up();
        return world.getBlockState(blockPos).isSolidBlock(world, blockPos);
    }

    private static boolean hasOcelotOnTop(WorldAccess world, BlockPos pos) {
        List<CatEntity> list = world.getNonSpectatingEntities(CatEntity.class, new Box(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1));
        if (!list.isEmpty()) {

            for (CatEntity catEntity : list) {
                if (catEntity.isInSittingPose()) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(getInventory(this, state, world, pos, false));
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, CHEST_TYPE, WATERLOGGED);
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ExtraChestEntity) {
            ((ExtraChestEntity)blockEntity).onScheduledTick();
        }

    }

    static {
        FACING = HorizontalFacingBlock.FACING;
        CHEST_TYPE = Properties.CHEST_TYPE;
        WATERLOGGED = Properties.WATERLOGGED;
        DOUBLE_NORTH_SHAPE = Block.createCuboidShape(1.0D, 0.0D, 0.0D, 15.0D, 14.0D, 15.0D);
        DOUBLE_SOUTH_SHAPE = Block.createCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 16.0D);
        DOUBLE_WEST_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
        DOUBLE_EAST_SHAPE = Block.createCuboidShape(1.0D, 0.0D, 1.0D, 16.0D, 14.0D, 15.0D);
        SINGLE_SHAPE = Block.createCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
        INVENTORY_RETRIEVER = new DoubleBlockProperties.PropertyRetriever<ExtraChestEntity, Optional<Inventory>>() {
            public Optional<Inventory> getFromBoth(ExtraChestEntity ExtraChestEntity, ExtraChestEntity ExtraChestEntity2) {
                return Optional.of(new DoubleInventory(ExtraChestEntity, ExtraChestEntity2));
            }

            public Optional<Inventory> getFrom(ExtraChestEntity ExtraChestEntity) {
                return Optional.of(ExtraChestEntity);
            }

            public Optional<Inventory> getFallback() {
                return Optional.empty();
            }
        };
        NAME_RETRIEVER = new DoubleBlockProperties.PropertyRetriever<ExtraChestEntity, Optional<NamedScreenHandlerFactory>>() {
            public Optional<NamedScreenHandlerFactory> getFromBoth(final ExtraChestEntity ExtraChestEntity, final ExtraChestEntity ExtraChestEntity2) {
                final Inventory inventory = new DoubleInventory(ExtraChestEntity, ExtraChestEntity2);
                return Optional.of(new NamedScreenHandlerFactory() {
                    @Nullable
                    public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                        if (ExtraChestEntity.checkUnlocked(playerEntity) && ExtraChestEntity2.checkUnlocked(playerEntity)) {
                            ExtraChestEntity.checkLootInteraction(playerInventory.player);
                            ExtraChestEntity2.checkLootInteraction(playerInventory.player);
                            return ExtraChestEntity2.createDoubleScreenHandler(i,playerInventory,inventory);
                        } else {
                            return null;
                        }
                    }

                    public Text getDisplayName() {
                        if (ExtraChestEntity.hasCustomName()) {
                            return ExtraChestEntity.getDisplayName();
                        } else {
                            return ExtraChestEntity2.hasCustomName() ? ExtraChestEntity2.getDisplayName() : ExtraChestEntity2.getDoubleContainerName();
                        }
                    }
                });
            }

            public Optional<NamedScreenHandlerFactory> getFrom(ExtraChestEntity ExtraChestEntity) {
                return Optional.of(ExtraChestEntity);
            }

            public Optional<NamedScreenHandlerFactory> getFallback() {
                return Optional.empty();
            }
        };
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        super.appendTooltip(stack, world, tooltip, options);
    }
}
