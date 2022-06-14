package website.skylorbeck.minecraft.skylorlib;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.ItemEntity;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.potion.Potions;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.util.registry.Registry;
import website.skylorbeck.minecraft.skylorlib.mixin.BrewingRecipeRegistryInvoker;

public class SkylorLib implements ModInitializer {
    @Override
    public void onInitialize() {
        Registrar.regBlock("golden_wheat_", Declarer.GOLDEN_WHEAT_BLOCK, "skylorlib");
        Registrar.regItem("golden_seeds_", Declarer.GOLDEN_WHEAT_SEEDS, "skylorlib");
        Registrar.regItem("golden_wheat_", Declarer.GOLDEN_WHEAT_ITEM, "skylorlib");
        Registrar.regItem("golden_bread_", Declarer.GOLDEN_BREAD_ITEM, "skylorlib");
        Registry.register(Registry.STATUS_EFFECT, "megafy",Declarer.MEGAFY);
        Registry.register(Registry.POTION, "mega_potion",Declarer.MEGA_POTION);
        BrewingRecipeRegistryInvoker.invokeRegisterPotionRecipe(Potions.STRENGTH, Declarer.GOLDEN_WHEAT_ITEM, Declarer.MEGA_POTION);

        LootTableEvents.MODIFY.register(((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (Blocks.WHEAT.getLootTableId().equals(id)){
                LootPool.Builder builder = LootPool.builder().with(ItemEntry.builder(Declarer.GOLDEN_WHEAT_SEEDS)).conditionally( BlockStatePropertyLootCondition.builder(Blocks.WHEAT).properties(StatePredicate.Builder.create().exactMatch(CropBlock.AGE, 7))).conditionally(RandomChanceLootCondition.builder(0.01f));
                tableBuilder.pool(builder);
            }
        }));
    }
}