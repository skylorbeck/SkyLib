package website.skylorbeck.minecraft.skylorlib.storage;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
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
