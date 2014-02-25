package powercraft.machines;

import net.minecraft.item.ItemStack;
import powercraft.api.PC_Api;
import powercraft.api.PC_Module;
import powercraft.machines.block.PCma_BlockFurnace;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.InstanceFactory;

@Mod(modid = PCma_Machines.NAME, name = PCma_Machines.NAME, version = PCma_Machines.VERSION, dependencies = PCma_Machines.DEPENDENCIES)
public class PCma_Machines extends PC_Module {

	public static final String NAME = POWERCRAFT + "-Machines";
	public static final String VERSION = "1.7.2";
	public static final String DEPENDENCIES = "required-after:" + PC_Api.NAME + "@" + PC_Api.VERSION;
	
	public static final PCma_Machines INSTANCE = new PCma_Machines();
	
	public static final PCma_BlockFurnace FURNACE = new PCma_BlockFurnace();
	
	@InstanceFactory
	public static PCma_Machines factory() {
		return INSTANCE;
	}
	
	private PCma_Machines() {
		
	}
	
	@Override
	public ItemStack getCreativeTabItemStack() {
		// TODO Auto-generated method stub
		return null;
	}

}