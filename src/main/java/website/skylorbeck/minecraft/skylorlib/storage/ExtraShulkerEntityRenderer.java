package website.skylorbeck.minecraft.skylorlib.storage;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import static net.minecraft.block.ShulkerBoxBlock.FACING;

@Environment(EnvType.CLIENT)
public class ExtraShulkerEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
    private final ShulkerEntityModel<?> model;
    public ExtraShulkerEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.model = new ShulkerEntityModel(ctx.getLayerModelPart(EntityModelLayers.SHULKER));
    }

    public void render(T ExtraShulkerEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        Direction direction = Direction.UP;
        if (ExtraShulkerEntity.hasWorld()) {
            BlockState blockState = ExtraShulkerEntity.getWorld().getBlockState(ExtraShulkerEntity.getPos());
            if (blockState.getBlock() instanceof ExtraShulkerBlock) {
                direction = (Direction)blockState.get(FACING);
            }
        }

        SpriteIdentifier spriteIdentifier2 = ((ExtraShulkerEntity)ExtraShulkerEntity).getSpriteIdentifier();
        matrixStack.push();
        matrixStack.translate(0.5D, 0.5D, 0.5D);
        float g = 0.9995F;
        matrixStack.scale(0.9995F, 0.9995F, 0.9995F);
        matrixStack.multiply(direction.getRotationQuaternion());
        matrixStack.scale(1.0F, -1.0F, -1.0F);
        matrixStack.translate(0.0D, -1.0D, 0.0D);
        ModelPart modelPart = this.model.getLid();
        modelPart.setPivot(0.0F, 24.0F - ((ExtraShulkerEntity)ExtraShulkerEntity).getAnimationProgress(f) * 0.5F * 16.0F, 0.0F);
        modelPart.yaw = 270.0F * ((ExtraShulkerEntity)ExtraShulkerEntity).getAnimationProgress(f) * 0.017453292F;
        VertexConsumer vertexConsumer = spriteIdentifier2.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntityCutoutNoCull);
        this.model.render(matrixStack, vertexConsumer, i, j, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.pop();
    }

    @Override
    public boolean rendersOutsideBoundingBox(T blockEntity) {
        return BlockEntityRenderer.super.rendersOutsideBoundingBox(blockEntity);
    }

    @Override
    public int getRenderDistance() {
        return BlockEntityRenderer.super.getRenderDistance();
    }

    @Override
    public boolean isInRenderDistance(T blockEntity, Vec3d pos) {
        return BlockEntityRenderer.super.isInRenderDistance(blockEntity, pos);
    }

}
