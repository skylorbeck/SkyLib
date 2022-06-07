package website.skylorbeck.minecraft.skylorlib;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Registrar {
    /**
     * Registers items.
     * Example: regItem("testitem_", Items.TESTITEM, "skylorlib"); item will be identified as "skylorlib:testitem_item"
     * @param name Identifier second half, appended with "item"
     * @param itemid a static final reference to the item
     * @param MODID your mod identifier
     */
    public static void regItem(String name, Item itemid, String MODID) {
        Registry.register(Registry.ITEM, new Identifier(MODID, name + "item"), itemid);
    }
    /**
     * Registers blocks.
     * Example: regBlock("testblock_", Blocks.TESTBLOCK, "skylorlib"); item will be identified as "skylorlib:testblock_block"
     * @param name Identifier second half, appended with "item"
     * @param blockid a static final reference to the Block
     * @param MODID your mod identifier
     */
    public static void regBlock(String name, Block blockid, String MODID) {
        Registry.register(Registry.BLOCK, new Identifier(MODID, name + "block"), blockid);
    }

}