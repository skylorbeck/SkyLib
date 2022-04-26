package website.skylorbeck.minecraft.skylorlib.storage;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
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

public abstract class ExtraChestBlock extends ChestBlock implements Waterloggable {
    private static final DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Optional<Inventory>> INVENTORY_RETRIEVER
            = new DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Optional<Inventory>>() {
        public Optional<Inventory> getFromBoth(ChestBlockEntity ExtraChestEntity, ChestBlockEntity ExtraChestEntity2) {
            return Optional.of(new DoubleInventory(ExtraChestEntity, ExtraChestEntity2));
        }

        public Optional<Inventory> getFrom(ChestBlockEntity ExtraChestEntity) {
            return Optional.of(ExtraChestEntity);
        }

        public Optional<Inventory> getFallback() {
            return Optional.empty();
        }
    };
    private static final DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Optional<NamedScreenHandlerFactory>> NAME_RETRIEVER = new DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Optional<NamedScreenHandlerFactory>>() {
        public Optional<NamedScreenHandlerFactory> getFromBoth(final ChestBlockEntity ExtraChestEntity, final ChestBlockEntity ExtraChestEntity2) {
            final Inventory inventory = new DoubleInventory(ExtraChestEntity, ExtraChestEntity2);
            return Optional.of(new NamedScreenHandlerFactory() {
                @Nullable
                public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                    if (ExtraChestEntity.checkUnlocked(playerEntity) && ExtraChestEntity2.checkUnlocked(playerEntity)) {
                        ExtraChestEntity.checkLootInteraction(playerInventory.player);
                        ExtraChestEntity2.checkLootInteraction(playerInventory.player);
                        return ((ExtraChestEntity) ExtraChestEntity2).createDoubleScreenHandler(i,playerInventory,inventory);
                    } else {
                        return null;
                    }
                }

                public Text getDisplayName() {
                    if (ExtraChestEntity.hasCustomName()) {
                        return ExtraChestEntity.getDisplayName();
                    } else {
                        return ExtraChestEntity2.hasCustomName() ? ExtraChestEntity2.getDisplayName() : ((ExtraChestEntity) ExtraChestEntity2).getDoubleContainerName();
                    }
                }
            });
        }

        public Optional<NamedScreenHandlerFactory> getFrom(ChestBlockEntity ExtraChestEntity) {
            return Optional.of(ExtraChestEntity);
        }

        public Optional<NamedScreenHandlerFactory> getFallback() {
            return Optional.empty();
        }
    };

    public ExtraChestBlock(Settings settings, Supplier<BlockEntityType<? extends ChestBlockEntity>> supplier) {
        super(settings, supplier);
    }

    @Nullable
    public static Inventory getInventory(ExtraChestBlock block, BlockState state, World world, BlockPos pos, boolean ignoreBlocked) {
        return (Inventory)((Optional)block.getBlockEntitySource(state, world, pos, ignoreBlocked).apply(INVENTORY_RETRIEVER)).orElse(null);
    }

    @Nullable
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return (NamedScreenHandlerFactory)((Optional)this.getBlockEntitySource(state, world, pos, false).apply(NAME_RETRIEVER)).orElse(null);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        super.appendTooltip(stack, world, tooltip, options);
    }
}
