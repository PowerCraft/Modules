package powercraft.core;

import net.minecraft.item.ItemStack;
import powercraft.api.PC_Api;
import powercraft.api.PC_Build;
import powercraft.api.PC_Module;
import powercraft.api.multiblock.PC_BlockMultiblock;
import powercraft.api.multiblock.PC_Multiblocks;
import powercraft.api.network.PC_PacketHandler;
import powercraft.core.item.PCco_ItemCraftingTool;
import powercraft.core.item.PCco_ItemNanobots;
import powercraft.core.item.PCco_ItemOreSniffer;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.InstanceFactory;


@Mod(modid = PCco_Core.NAME, name = PCco_Core.NAME, version = PCco_Core.VERSION, dependencies = PCco_Core.DEPENDENCIES)
public class PCco_Core extends PC_Module {

	public static final String NAME = POWERCRAFT + "-Core";
	public static final String VERSION = PC_Build.BUILD_VERSION;
	public static final String DEPENDENCIES = "required-after:" + PC_Api.NAME + "@" + PC_Api.VERSION;
	
	public static final PCco_Core INSTANCE = new PCco_Core();
	
	public static final PC_BlockMultiblock MULTIBLOCK = PC_Multiblocks.getMultiblock();
	
	public static final PCco_ItemNanobots NANOBOTS = new PCco_ItemNanobots();
	
	public static final PCco_ItemCraftingTool CRAFTING_TOOL = new PCco_ItemCraftingTool();
	
	public static final PCco_ItemOreSniffer ORE_SNIFFER = new PCco_ItemOreSniffer();
	
	@InstanceFactory
	public static PCco_Core factory() {
		return INSTANCE;
	}
	
	private PCco_Core() {
		PC_PacketHandler.registerPacket(PCco_PacketCrafting.class);
	}
	
	@Override
	public ItemStack getCreativeTabItemStack() {
		return new ItemStack(NANOBOTS);
	}

}