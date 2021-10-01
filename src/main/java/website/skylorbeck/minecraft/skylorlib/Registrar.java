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
    public static void regItem(String name, Item itemid, String MODID) {
        Registry.register(Registry.ITEM, new Identifier(MODID, name + "item"), itemid);
    }

    public static void regBlock(String name, Block blockid, String MODID) {
        Registry.register(Registry.BLOCK, new Identifier(MODID, name + "block"), blockid);
    }

    public static void regServerSidePacket(String name, String MODID, PacketConsumer serverInstructions) {
        ServerSidePacketRegistryImpl.INSTANCE.register(new Identifier(MODID, name), serverInstructions);
    }

    public static void regClientSidePacket(String name, String MODID, PacketConsumer clientInstructions) {
        ClientSidePacketRegistryImpl.INSTANCE.register(new Identifier(MODID, name), clientInstructions);
    }

}