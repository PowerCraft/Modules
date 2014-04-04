package powercraft.itemstorage;

import net.minecraft.item.ItemStack;
import powercraft.api.PC_Api;
import powercraft.api.PC_Module;
import powercraft.api.network.PC_PacketHandler;
import powercraft.itemstorage.block.PCis_BlockChannelChest;
import powercraft.itemstorage.item.PCis_ItemCompressor;
import powercraft.itemstorage.item.packet.PCis_PacketItemSetName;
import powercraft.itemstorage.item.packet.PCis_PacketItemSetPutStacks;
import powercraft.itemstorage.item.packet.PCis_PacketItemSetTakeStacks;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.InstanceFactory;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;


@Mod(modid = PCis_ItemStorage.NAME, name = PCis_ItemStorage.NAME, version = PCis_ItemStorage.VERSION, dependencies = PCis_ItemStorage.DEPENDENCIES)
public class PCis_ItemStorage extends PC_Module {

	public static final String NAME = POWERCRAFT + "-ItemStorage";
	public static final String VERSION = "1.7.2";
	public static final String DEPENDENCIES = "required-after:" + PC_Api.NAME + "@" + PC_Api.VERSION;
	
	public static final PCis_ItemStorage INSTANCE = new PCis_ItemStorage();
	public static final PCis_ItemCompressor compressor = new PCis_ItemCompressor();
	public static final PCis_BlockChannelChest CHANNEL_CHEST = new PCis_BlockChannelChest();
	
	@InstanceFactory
	public static PCis_ItemStorage factory() {
		return INSTANCE;
	}
	
	private PCis_ItemStorage() {
		PC_PacketHandler.registerPacket(PCis_PacketItemSetPutStacks.class);
		PC_PacketHandler.registerPacket(PCis_PacketItemSetName.class);
		PC_PacketHandler.registerPacket(PCis_PacketItemSetTakeStacks.class);
	}
	
	@Override
	public ItemStack getCreativeTabItemStack() {
		return new ItemStack(CHANNEL_CHEST);
	}

	@SuppressWarnings({ "static-method", "unused" })
	@EventHandler
	public void onServerStopping(FMLServerStoppedEvent serverStoppedEvent){
		PCis_ChannelChestSave.cleanup();
	}
	
}