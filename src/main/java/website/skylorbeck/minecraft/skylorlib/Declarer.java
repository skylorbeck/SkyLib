package website.skylorbeck.minecraft.skylorlib;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.sound.BlockSoundGroup;
import website.skylorbeck.minecraft.skylorlib.furnaces.ExtraBlastFurnaceBlockEntity;
import website.skylorbeck.minecraft.skylorlib.furnaces.ExtraFurnaceBlockEntity;
import website.skylorbeck.minecraft.skylorlib.furnaces.ExtraSmokerBlockEntity;
import website.skylorbeck.minecraft.skylorlib.statuseffects.MegaEffect;

public class Declarer {
    public static BlockEntityType<ExtraFurnaceBlockEntity> EXTRA_FURNACE_ENTITY;
    public static BlockEntityType<ExtraBlastFurnaceBlockEntity> EXTRA_BLAST_FURNACE_ENTITY;
    public static BlockEntityType<ExtraSmokerBlockEntity> EXTRA_SMOKER_FURNACE_ENTITY;

    public static final Block GOLDEN_WHEAT_BLOCK =  new CropBlock(AbstractBlock.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP));
    public static final Item GOLDEN_WHEAT_SEEDS = new AliasedBlockItem(GOLDEN_WHEAT_BLOCK, new Item.Settings().group(ItemGroup.MATERIALS));
    public static final Item GOLDEN_WHEAT_ITEM = new Item(new Item.Settings().group(ItemGroup.MATERIALS));
    public static final Item GOLDEN_BREAD_ITEM =new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.GOLDEN_APPLE));
    public static final StatusEffect MEGAFY = new MegaEffect();
    public static final Potion MEGA_POTION = new Potion("mega_potion", new StatusEffectInstance(MEGAFY, 200));


}
