package website.skylorbeck.minecraft.skylorlib.tools;

import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Lazy;

import java.util.function.Supplier;

public enum ExtraToolMaterials implements ToolMaterial {
    DIORITE(1, 131, 4.0F, 1.0F, 5, () -> {//stone
        return Ingredient.ofItems(Blocks.DIORITE,Blocks.POLISHED_DIORITE);
    }),
    ANDESITE(1, 131, 4.0F, 1.0F, 5, () -> {//stone
        return Ingredient.ofItems(Blocks.ANDESITE,Blocks.POLISHED_ANDESITE);
    }),
    GRANITE(1, 131, 4.0F, 1.0F, 5, () -> {//stone
        return Ingredient.ofItems(Blocks.GRANITE,Blocks.POLISHED_GRANITE);
    }),
    QUARTZ(2, 250, 6.0F, 3.0F, 14, () -> {//damage of diamond, else iron
        return Ingredient.ofItems(Blocks.QUARTZ_BLOCK,Blocks.QUARTZ_PILLAR,Blocks.CHISELED_QUARTZ_BLOCK,Blocks.SMOOTH_QUARTZ);
    }),
    NETHERRACK(0, 59, 2.0F, 0.0F, 5, () -> {//enchantability of stone, else wood
        return Ingredient.ofItems(Blocks.NETHERRACK);
    }),
    NETHERBRICK(1, 131, 4.0F, 1.0F, 5, () -> {//stone
        return Ingredient.ofItems(Blocks.NETHER_BRICKS);
    }),
    REDNETHERBRICK(1, 131, 4.0F, 1.0F, 5, () -> {//stone
        return Ingredient.ofItems(Blocks.RED_NETHER_BRICKS);
    }),
    SANDSTONE(1, 131, 4.0F, 1.0F, 5, () -> {//stone
        return Ingredient.ofItems(Blocks.SANDSTONE,Blocks.CHISELED_SANDSTONE,Blocks.CUT_SANDSTONE,Blocks.SMOOTH_SANDSTONE);
    }),
    REDSANDSTONE(1, 131, 4.0F, 1.0F, 5, () -> {//stone
        return Ingredient.ofItems(Blocks.RED_SANDSTONE,Blocks.CHISELED_RED_SANDSTONE,Blocks.CUT_RED_SANDSTONE,Blocks.SMOOTH_RED_SANDSTONE);
    }),
    REDSTONE(2,250,12.0F,2.0F,14,()->{//speed of gold, else iron
        return Ingredient.ofItems(Blocks.REDSTONE_BLOCK);
    }),
    LAPIS(2,250,6.0F,2.0F,22,()->{//enchantability of gold, else iron
        return Ingredient.ofItems(Blocks.LAPIS_BLOCK);
    }),
    EMERALD(2,1561,6.0F,2.0F,14,()->{//durability of diamond, else iron
        return Ingredient.ofItems(Items.EMERALD);
    }),
    FLINT(1,250,4.0F,1.0F,14,()->{//durability of iron, else stone
        return Ingredient.ofItems(Items.FLINT);
    }),
    BLACKSTONE(1, 131, 4.0F, 1.0F, 5, () -> {//stone
        return Ingredient.ofItems(Blocks.BLACKSTONE,Blocks.POLISHED_BLACKSTONE);
    }),
    BASALT(1, 131, 4.0F, 1.0F, 5, () -> {//stone
        return Ingredient.ofItems(Blocks.BASALT,Blocks.POLISHED_BASALT);
    }),
    ENDSTONE(1, 131, 4.0F, 1.0F, 5, () -> {//stone
        return Ingredient.ofItems(Blocks.END_STONE,Blocks.END_STONE_BRICKS);
    }),
    CRIMSON(0,59,2.0F,0.0F,15, () -> {//wood
        return Ingredient.ofItems(Blocks.CRIMSON_PLANKS);
    }),
    WARPED(0,59,2.0F,0.0F,15, () -> {//wood
        return Ingredient.ofItems(Blocks.WARPED_PLANKS);
    }),
    AMETHYST(2,250,6.0F,2.0F,14, () -> {//iron
        return Ingredient.ofItems(Items.AMETHYST_SHARD);
    }),
    COPPER(2,250,4.0F,2.0F,14, () -> {//speed of stone, else iron
        return Ingredient.ofItems(Items.COPPER_INGOT);
    }),
    DEEPSLATE(1, 131, 4.0F, 1.0F, 5, () -> {//speed of stone, else iron
        return Ingredient.ofItems(Blocks.COBBLED_DEEPSLATE);
    }),

    ;
    private final int miningLevel;
    private final int itemDurability;
    private final float miningSpeed;
    private final float attackDamage;
    private final int enchantability;
    private final Lazy<Ingredient> repairIngredient;

    ExtraToolMaterials(int miningLevel, int itemDurability, float miningSpeed, float attackDamage, int enchantability, Supplier<Ingredient> repairIngredient) {
        this.miningLevel = miningLevel;
        this.itemDurability = itemDurability;
        this.miningSpeed = miningSpeed;
        this.attackDamage = attackDamage;
        this.enchantability = enchantability;
        this.repairIngredient = new Lazy<>(repairIngredient);
    }
    @Override
    public int getDurability() {
        return this.itemDurability;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return this.miningSpeed;
    }

    @Override
    public float getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public int getMiningLevel() {
        return this.miningLevel;
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }
}
