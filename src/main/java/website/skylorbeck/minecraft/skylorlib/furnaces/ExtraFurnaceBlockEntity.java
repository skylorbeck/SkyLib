package website.skylorbeck.minecraft.skylorlib.furnaces;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import website.skylorbeck.minecraft.skylorlib.Declarer;

public class ExtraFurnaceBlockEntity extends AbstractExtraFurnaceBlockEntity {

    public ExtraFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(Declarer.EXTRA_FURNACE_ENTITY,pos,state,RecipeType.SMELTING,1f);
    }

    public static <T extends BlockEntity> void tick(World world, BlockPos blockPos, BlockState state, T t) {
        AbstractExtraFurnaceBlockEntity.tick(world,blockPos,state, (AbstractExtraFurnaceBlockEntity) t);
    }
}