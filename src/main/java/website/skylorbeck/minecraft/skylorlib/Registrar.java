package website.skylorbeck.minecraft.skylorlib;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.impl.networking.ClientSidePacketRegistryImpl;
import net.fabricmc.fabric.impl.networking.ServerSidePacketRegistryImpl;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Objects;
import java.util.function.Function;

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

    /**
     * Registers a server side packet.
     * @param name identifier for the packet
     * @param MODID your mod identifier
     * @param serverInstructions instructions to be executed on the server side with the packet information
     */
    public static void regServerSidePacket(String name, String MODID, PacketConsumer serverInstructions) {
        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(MODID, name), serverInstructions);
    }

    /**
     * Registers a client side packet.
     * @param name identifier for the packet
     * @param MODID your mod identifier
     * @param clientInstructions instructions to be executed on the client side with the packet information
     */
    public static void regClientSidePacket(String name, String MODID, PacketConsumer clientInstructions) {
        ClientSidePacketRegistryImpl.INSTANCE.register(new Identifier(MODID, name), clientInstructions);
    }

}