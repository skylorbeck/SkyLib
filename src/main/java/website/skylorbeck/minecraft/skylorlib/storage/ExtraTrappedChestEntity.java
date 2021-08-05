package website.skylorbeck.minecraft.skylorlib.storage;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ExtraTrappedChestEntity extends ExtraChestEntity {

    protected ExtraTrappedChestEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState, int size, String type, boolean trapped) {
        super(blockEntityType, blockPos, blockState, size, type, trapped);
    }

    protected void onInvOpenOrClose(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
        super.onInvOpenOrClose(world, pos, state, oldViewerCount, newViewerCount);
        if (oldViewerCount != newViewerCount) {
            Block block = state.getBlock();
            world.updateNeighborsAlways(pos, block);
            world.updateNeighborsAlways(pos.down(), block);
        }
    }
}
