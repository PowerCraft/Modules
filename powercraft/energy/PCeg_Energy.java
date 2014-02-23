package powercraft.energy;

import net.minecraft.item.ItemStack;
import powercraft.api.PC_Api;
import powercraft.api.PC_Module;
import powercraft.energy.multiblock.PCeg_MultiblockItemEnergyConduit;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.InstanceFactory;

@Mod(modid = PCeg_Energy.NAME, name = PCeg_Energy.NAME, version = PCeg_Energy.VERSION, dependencies = PCeg_Energy.DEPENDENCIES)
public class PCeg_Energy extends PC_Module {

	public static final String NAME = POWERCRAFT + "-Energy";
	public static final String VERSION = "1.7.2";
	public static final String DEPENDENCIES = "required-after:" + PC_Api.NAME + "@" + PC_Api.VERSION;
	
	public static final PCeg_Energy INSTANCE = new PCeg_Energy();
	
	public static final PCeg_MultiblockItemEnergyConduit energyConduit = new PCeg_MultiblockItemEnergyConduit();
	
	@InstanceFactory
	public static PCeg_Energy factory() {
		return INSTANCE;
	}
	
	private PCeg_Energy() {

	}
	
	@Override
	public ItemStack getCreativeTabItemStack() {
		// TODO Auto-generated method stub
		return null;
	}

}
