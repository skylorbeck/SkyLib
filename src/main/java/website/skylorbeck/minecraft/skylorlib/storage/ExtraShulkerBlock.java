package website.skylorbeck.minecraft.skylorlib.storage;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public abstract class ExtraShulkerBlock extends BlockWithEntity {
    protected final Supplier<BlockEntityType<? extends ExtraShulkerEntity>> entityTypeRetriever;
    public static final EnumProperty<Direction> FACING;
    public static final Identifier CONTENTS;
    @Nullable
    private final DyeColor color;

    public ExtraShulkerBlock(@Nullable DyeColor color, Settings settings, Supplier<BlockEntityType<? extends ExtraShulkerEntity>> entityTypeSupplier) {
        super(settings);
        this.color = color;
        this.entityTypeRetriever = entityTypeSupplier;
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.UP));
    }

    public abstract BlockEntity createBlockEntity(BlockPos pos, BlockState state);

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? checkType(type, this.getExpectedEntityType(), ExtraShulkerEntity::tick) : null;
    }
    public BlockEntityType<? extends ExtraShulkerEntity> getExpectedEntityType() {
        return this.entityTypeRetriever.get();
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else if (player.isSpectator()) {
            return ActionResult.CONSUME;
        } else {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ExtraShulkerEntity ExtraShulkerEntity) {
                if (canOpen(state, world, pos, ExtraShulkerEntity)) {
                    player.openHandledScreen(ExtraShulkerEntity);
                    player.incrementStat(Stats.OPEN_SHULKER_BOX);
                    PiglinBrain.onGuardedBlockInteracted(player, true);
                }

                return ActionResult.CONSUME;
            } else {
                return ActionResult.PASS;
            }
        }
    }

    private static boolean canOpen(BlockState state, World world, BlockPos pos, ExtraShulkerEntity entity) {
        if (entity.getAnimationStage() != ExtraShulkerEntity.AnimationStage.CLOSED) {
            return true;
        } else {
            Box box = ShulkerEntity.method_33347((Direction)state.get(FACING), 0.0F, 0.5F).offset(pos).contract(1.0E-6D);
            return world.isSpaceEmpty(box);
        }
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with(FACING, ctx.getSide());
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ExtraShulkerEntity ExtraShulkerEntity) {
            if (!world.isClient && !ExtraShulkerEntity.isEmpty()) {
                ItemStack itemStack = this.asItem().getDefaultStack();
                NbtCompound nbtCompound = ExtraShulkerEntity.writeInventoryNbt(new NbtCompound());
                if (!nbtCompound.isEmpty()) {
                    itemStack.setSubNbt("BlockEntityTag", nbtCompound);
                }

                if (ExtraShulkerEntity.hasCustomName()) {
                    itemStack.setCustomName(ExtraShulkerEntity.getCustomName());
                }

                ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, itemStack);
                itemEntity.setToDefaultPickupDelay();
                world.spawnEntity(itemEntity);
            }
        }

        super.onBreak(world, pos, state, player);
    }

    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        BlockEntity blockEntity = (BlockEntity)builder.getNullable(LootContextParameters.BLOCK_ENTITY);
        if (blockEntity instanceof ExtraShulkerEntity ExtraShulkerEntity) {
            builder = builder.putDrop(CONTENTS, (lootContext, consumer) -> {
                for(int i = 0; i < ExtraShulkerEntity.size(); ++i) {
                    consumer.accept(ExtraShulkerEntity.getStack(i));
                }

            });
        }

        return super.getDroppedStacks(state, builder);
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (itemStack.hasCustomName()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ExtraShulkerEntity) {
                ((ExtraShulkerEntity)blockEntity).setCustomName(itemStack.getName());
            }
        }

    }

    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ExtraShulkerEntity) {
                world.updateComparators(pos, state.getBlock());
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        super.appendTooltip(stack, world, tooltip, options);
        NbtCompound nbtCompound = stack.getSubNbt("BlockEntityTag");
        if (nbtCompound != null) {
            if (nbtCompound.contains("LootTable", 8)) {
                tooltip.add(new LiteralText("???????"));
            }

            if (nbtCompound.contains("Items", 9)||nbtCompound.contains("Items2", 9) ) {
                DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(500, ItemStack.EMPTY);
                StorageUtils.readNbt(nbtCompound,defaultedList);
                int size = defaultedList.size();
                defaultedList = DefaultedList.ofSize(size,ItemStack.EMPTY);
                StorageUtils.readNbt(nbtCompound,defaultedList);
                int i = 0;
                int j = 0;

                for (ItemStack itemStack : defaultedList) {
                    if (!itemStack.isEmpty()) {
                        ++j;
                        if (i <= 4) {
                            ++i;
                            MutableText mutableText = itemStack.getName().shallowCopy();
                            mutableText.append(" x").append(String.valueOf(itemStack.getCount()));
                            tooltip.add(mutableText);
                        }
                    }
                }

                if (j - i > 0) {
                    tooltip.add((new TranslatableText("container.shulkerBox.more", j - i)).formatted(Formatting.ITALIC));
                }
            }
        }

    }

    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.DESTROY;
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity instanceof ExtraShulkerEntity ? VoxelShapes.cuboid(((ExtraShulkerEntity)blockEntity).getBoundingBox(state)) : VoxelShapes.fullCube();
    }

    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput((Inventory)world.getBlockEntity(pos));
    }

    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        ItemStack itemStack = super.getPickStack(world, pos, state);
        ExtraShulkerEntity ExtraShulkerEntity = (ExtraShulkerEntity)world.getBlockEntity(pos);
        assert ExtraShulkerEntity != null;
        NbtCompound nbtCompound = ExtraShulkerEntity.writeInventoryNbt(new NbtCompound());
        if (!nbtCompound.isEmpty()) {
            itemStack.setSubNbt("BlockEntityTag", nbtCompound);
        }

        return itemStack;
    }

    @Nullable
    public static DyeColor getColor(Item item) {
        return getColor(Block.getBlockFromItem(item));
    }

    @Nullable
    public static DyeColor getColor(Block block) {
        return block instanceof ShulkerBoxBlock ? ((ShulkerBoxBlock)block).getColor() : null;
    }

    public static Block get(@Nullable DyeColor dyeColor) {
        if (dyeColor == null) {
            return Blocks.SHULKER_BOX;
        } else {
            return switch (dyeColor) {
                case WHITE -> Blocks.WHITE_SHULKER_BOX;
                case ORANGE -> Blocks.ORANGE_SHULKER_BOX;
                case MAGENTA -> Blocks.MAGENTA_SHULKER_BOX;
                case LIGHT_BLUE -> Blocks.LIGHT_BLUE_SHULKER_BOX;
                case YELLOW -> Blocks.YELLOW_SHULKER_BOX;
                case LIME -> Blocks.LIME_SHULKER_BOX;
                case PINK -> Blocks.PINK_SHULKER_BOX;
                case GRAY -> Blocks.GRAY_SHULKER_BOX;
                case LIGHT_GRAY -> Blocks.LIGHT_GRAY_SHULKER_BOX;
                case CYAN -> Blocks.CYAN_SHULKER_BOX;
                default -> Blocks.PURPLE_SHULKER_BOX;
                case BLUE -> Blocks.BLUE_SHULKER_BOX;
                case BROWN -> Blocks.BROWN_SHULKER_BOX;
                case GREEN -> Blocks.GREEN_SHULKER_BOX;
                case RED -> Blocks.RED_SHULKER_BOX;
                case BLACK -> Blocks.BLACK_SHULKER_BOX;
            };
        }
    }

    @Nullable
    public DyeColor getColor() {
        return this.color;
    }

    public static ItemStack getItemStack(@Nullable DyeColor color) {
        return new ItemStack(get(color));
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
    }


    static {
        FACING = FacingBlock.FACING;
        CONTENTS = new Identifier("contents");
    }


}
