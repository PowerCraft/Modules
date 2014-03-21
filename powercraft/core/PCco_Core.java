package powercraft.core;

import net.minecraft.item.ItemStack;
import powercraft.api.PC_Api;
import powercraft.api.PC_Module;
import powercraft.api.multiblock.PC_BlockMultiblock;
import powercraft.api.multiblock.PC_Multiblocks;
import powercraft.core.item.PCco_ItemNanobots;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.InstanceFactory;


@Mod(modid = PCco_Core.NAME, name = PCco_Core.NAME, version = PCco_Core.VERSION, dependencies = PCco_Core.DEPENDENCIES)
public class PCco_Core extends PC_Module {

	public static final String NAME = POWERCRAFT + "-Core";
	public static final String VERSION = "1.7.2";
	public static final String DEPENDENCIES = "required-after:" + PC_Api.NAME + "@" + PC_Api.VERSION;
	
	public static final PCco_Core INSTANCE = new PCco_Core();
	
	public static final PC_BlockMultiblock MULTIBLOCK = PC_Multiblocks.getMultiblock();
	
	public static final PCco_ItemNanobots NANOBOTS = new PCco_ItemNanobots();
	
	@InstanceFactory
	public static PCco_Core factory() {
		return INSTANCE;
	}
	
	private PCco_Core() {
		
	}
	
	@Override
	public ItemStack getCreativeTabItemStack() {
		// TODO Auto-generated method stub
		return null;
	}

}