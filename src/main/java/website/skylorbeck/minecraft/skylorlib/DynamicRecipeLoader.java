package website.skylorbeck.minecraft.skylorlib;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
public class DynamicRecipeLoader {
    /**
     * Creates a shapeless recipe with and arraylist of items/tags
     * @param items the items/tags to be used in the recipe
     * @param type true = tag, false = item. Must match the size of items
     * @param output the resulting item reference.
     * @param outputCount the amount of the resulting item
     * @return the recipe json to be stored statically on the client/server
     */
    public static JsonObject createShapelessRecipeJson(ArrayList<Identifier> items, ArrayList<Boolean> type, Identifier output, int outputCount) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "minecraft:crafting_shapeless");
        JsonObject individualKey;
        JsonArray itemArray = new JsonArray();

        for (int i = 0; i < items.size(); ++i) {
            individualKey = new JsonObject();
            individualKey.addProperty(type.get(i) ? "tag" : "item", items.get(i).toString());
            itemArray.add(individualKey);
        }

        json.add("ingredients", itemArray);
        JsonObject result = new JsonObject();
        result.addProperty("item", output.toString());
        result.addProperty("count", outputCount);
        json.add("result", result);
//        Logger.getAnonymousLogger().log(Level.SEVERE,json.toString());
        return json;
    }

    /**
     * Creates a smelting/blasting/cooking recipe with an item
     * @param item the item/tag to be used in the recipe
     * @param output the resulting item reference.
     * @param experience the experience gained from this recipe
     * @param cookTime the fuel ticks it takes to cook this item
     * @param furnaceType the type of furnace this recipe is for. see {@link furnaceTypes}
     * @return the recipe json to be stored statically on the client/server
     */
    public static JsonObject createSmeltingRecipeJson(Item item, Item output,float experience, int cookTime,furnaceTypes furnaceType) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "minecraft:"+furnaceType);
        JsonObject individualKey;
        individualKey = new JsonObject();
        individualKey.addProperty( "item",Registry.ITEM.getId(item).toString());
        json.add("ingredient", individualKey);
        json.addProperty("result", Registry.ITEM.getId(output).toString());
        json.addProperty("experience",experience);
        json.addProperty("cookingtime",furnaceType.equals(furnaceTypes.smelting)?cookTime:cookTime/2);
//        Logger.getAnonymousLogger().log(Level.SEVERE, json.toString());
        return json;
    }
    public enum furnaceTypes{
        smelting,
        smoking,
        blasting
    }

    /**
     * Creates a smelting/blasting/cooking recipe with an arraylist of items/tags
     * @param items the items to be used in the recipe
     * @param output the resulting item reference.
     * @param experience the experience gained from this recipe
     * @param cookTime the fuel ticks it takes to cook this item
     * @param furnaceType the type of furnace this recipe is for. see {@link furnaceTypes}
     * @return the recipe json to be stored statically on the client/server
     */
    public static JsonObject createSmeltingRecipeJsonComplex(Item[] items, Item output,float experience, int cookTime,furnaceTypes furnaceType) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "minecraft:"+furnaceType);
        JsonObject individualKey;
        JsonArray itemArray = new JsonArray();
        for (Item item : items) {
            individualKey = new JsonObject();
            individualKey.addProperty("item", Registry.ITEM.getId(item).toString());
            itemArray.add(individualKey);
        }
        json.add("ingredient", itemArray);
        json.addProperty("result", Registry.ITEM.getId(output).toString());
        json.addProperty("experience",experience);
        json.addProperty("cookingtime",furnaceType.equals(furnaceTypes.smelting)?cookTime:cookTime/2);
//        Logger.getAnonymousLogger().log(Level.SEVERE, json.toString());
        return json;
    }

    /**
     * Creates a shaped recipe with and arraylist of items/tags
     * @param items the items/tags to be used in the recipe
     * @param type true = tag, false = item. Must match the size of items
     * @param pattern the pattern of the recipe. IE: ["000", "0 0", "000"]
     * @param output the resulting item reference.
     * @param outputCount the amount of the resulting item
     * @return the recipe json to be stored statically on the client/server
     */
    public static JsonObject createShapedRecipeJson(ArrayList<Identifier> items, ArrayList<Boolean> type, ArrayList<String> pattern, Identifier output, int outputCount) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "minecraft:crafting_shaped");
        JsonArray jsonArray = new JsonArray();
        for (String s : pattern) {
            jsonArray.add(s);
        }
        json.add("pattern", jsonArray);
        JsonObject individualKey;
        JsonObject keyList = new JsonObject();

        for (int i = 0; i < items.size(); ++i) {
            individualKey = new JsonObject();
            individualKey.addProperty(type.get(i) ? "tag" : "item", items.get(i).toString());
            keyList.add(i + "", individualKey);
        }

        json.add("key", keyList);
        JsonObject result = new JsonObject();
        result.addProperty("item", output.toString());
        result.addProperty("count", outputCount);
        json.add("result", result);
        //Logger.getAnonymousLogger().log(Level.SEVERE,json.toString());
        return json;
    }

    public static JsonObject createShapedRecipeJsonComplex(ArrayList<String[]> items, ArrayList<ArrayList<Boolean>> type, ArrayList<String> pattern, Identifier output, int outputCount) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "minecraft:crafting_shaped");
        JsonArray jsonArray = new JsonArray();
        for (String s : pattern) {
            jsonArray.add(s);
        }
        json.add("pattern", jsonArray);
        JsonObject keyList = new JsonObject();

        for (int i = 0; i < items.size(); ++i) {
            jsonArray = new JsonArray();
            for (int j = 0; j < items.get(i).length; j++) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty(type.get(i).get(j) ? "tag" : "item", items.get(i)[j]);
                jsonArray.add(jsonObject);
            }
            keyList.add(i + "", jsonArray);
        }

        json.add("key", keyList);
        JsonObject result = new JsonObject();
        result.addProperty("item", output.toString());
        result.addProperty("count", outputCount);
        json.add("result", result);
        //Logger.getAnonymousLogger().log(Level.SEVERE,json.toString());
        return json;
    }

    /**
     * Creates a new furnace crafting recipe
     * @param tag a tag for the item the furnace will be made of
     * @param expectedItem the furnace to be made
     * @return the recipe json to be stored statically on the client/server
     */
    public static JsonObject createFurnace(Identifier tag, Item expectedItem) {
        return createShapedRecipeJson(
                Lists.newArrayList(tag),//items
                Lists.newArrayList(true),//type
                Lists.newArrayList("000", "0 0", "000"),//pattern
                Registry.ITEM.getId(expectedItem),
                1);
    }

    /**
     * {@link #createFurnace(Identifier, Item)} but for blast furnaces
     */
    public static JsonObject createBlast(Identifier tag, Item originalItem, Item expectedItem) {
        return createShapedRecipeJson(
                Lists.newArrayList(Registry.ITEM.getId(Items.IRON_INGOT), Registry.ITEM.getId(originalItem), tag),//items
                Lists.newArrayList(false, false, true),//type
                Lists.newArrayList("000", "010", "222"),//pattern
                Registry.ITEM.getId(expectedItem),
                1
        );
    }

    /**
     * {@link #createFurnace(Identifier, Item)} but for smokers
     */
    public static JsonObject createSmoker(Item originalItem, Item expectedItem) {
        return createShapedRecipeJson(
                Lists.newArrayList(Registry.ITEM.getId(originalItem), new Identifier("minecraft", "logs")),//items
                Lists.newArrayList(false, true),//type
                Lists.newArrayList(" 1 ", "101", " 1 "),//pattern
                Registry.ITEM.getId(expectedItem),
                1
        );
    }

    /**
     * Creates a compressed block recipe.
     * @param uncompressed the uncompressed block to be compressed
     * @param compressed the resulting compressed block
     * @return the recipe json to be stored statically on the client/server
     */
    public static JsonObject createCompressedBlock(Item uncompressed, Item compressed){
        Identifier id = Registry.ITEM.getId(uncompressed);
        return createShapelessRecipeJson(
                Lists.newArrayList(id,id,id,id,id,id,id,id,id),
                Lists.newArrayList(false,false,false,false,false,false,false,false,false),
                Registry.ITEM.getId(compressed),
                1
        );
    }

    /**
     * the inverse of {@link #createCompressedBlock(Item, Item)}
     * @param compressed the compressed block to be uncompressed
     * @param uncompressed the resulting uncompressed block
     * @return the recipe json to be stored statically on the client/server
     */
    public static JsonObject createUncompressedBlock(Item compressed, Item uncompressed){
        Identifier id = Registry.ITEM.getId(compressed);
        return createShapelessRecipeJson(
                Lists.newArrayList(id),
                Lists.newArrayList(false),
                Registry.ITEM.getId(uncompressed),
                9
        );
    }

    /**
     * Creates a tool recipe
     * @param material the material of the tool, as an item
     * @param toolType {@link ToolTypes}
     * @param expectedItem the resulting tool
     * @return the recipe json to be stored statically on the client/server
     */
    public static JsonObject createTool(Item material, ToolTypes toolType, Item expectedItem){
        ArrayList<String> pattern = new ArrayList<>();
        switch (toolType){
            case Pickaxe -> pattern = Lists.newArrayList("111"," 0 "," 0 ");
            case Axe -> pattern = Lists.newArrayList("11","10"," 0");
            case Sword -> pattern = Lists.newArrayList("1","1","0");
            case Shovel -> pattern = Lists.newArrayList("1","0","0");
            case Hoe -> pattern = Lists.newArrayList("11"," 0"," 0");
        }
        return createShapedRecipeJson(
                Lists.newArrayList(Registry.ITEM.getId(Items.STICK),Registry.ITEM.getId(material)),//items
                Lists.newArrayList(false, false),//type
                pattern,//pattern
                Registry.ITEM.getId(expectedItem),
                1
        );
    }

    /**
     * Creates a tool recipe
     * @param materialTag the material of the tool, as a tag
     * @param toolType {@link ToolTypes}
     * @param expectedItem the resulting tool
     * @return the recipe json to be stored statically on the client/server
     */
    public static JsonObject createTool(Identifier materialTag, ToolTypes toolType, Item expectedItem){
        ArrayList<String> pattern = new ArrayList<>();
        switch (toolType){
            case Pickaxe -> pattern = Lists.newArrayList("111"," 0 "," 0 ");
            case Axe -> pattern = Lists.newArrayList("11","10"," 0");
            case Sword -> pattern = Lists.newArrayList("1","1","0");
            case Shovel -> pattern = Lists.newArrayList("1","0","0");
            case Hoe -> pattern = Lists.newArrayList("11"," 0"," 0");
        }
        return createShapedRecipeJson(
                Lists.newArrayList(Registry.ITEM.getId(Items.STICK),materialTag),//items
                Lists.newArrayList(false, true),//type
                pattern,//pattern
                Registry.ITEM.getId(expectedItem),
                1
        );
    }
    public enum ToolTypes{
        Pickaxe,
        Axe,
        Sword,
        Shovel,
        Hoe,
    }
}