package website.skylorbeck.minecraft.skylorlib.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import website.skylorbeck.minecraft.skylorlib.Declarer;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class SkylorLibClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), Declarer.GOLDEN_WHEAT_BLOCK);
    }
}
