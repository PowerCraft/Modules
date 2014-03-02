package powercraft.energy.multiblock;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;
import powercraft.api.PC_IconRegistry;
import powercraft.api.multiblock.PC_MultiblockObject;
import powercraft.api.multiblock.conduit.PC_MultiblockItemConduit;

public class PCeg_MultiblockItemEnergyConduit extends PC_MultiblockItemConduit {

	IIcon normalConduit;
	IIcon cornerConduit;
	IIcon connections[];
	
	public PCeg_MultiblockItemEnergyConduit(){
		setCreativeTab(CreativeTabs.tabRedstone);
	}
	
	@Override
	public Class<? extends PC_MultiblockObject> getMultiblockObjectClass() {
		return PCeg_MultiblockObjectEnergyConduit.class;
	}

	@Override
	public void loadMultiblockIcons(PC_IconRegistry iconRegistry) {
		this.normalConduit = iconRegistry.registerIcon("Nanotube_Normal");
		this.cornerConduit = iconRegistry.registerIcon("Nanotube_Corner");
		this.connections = new IIcon[4];
		this.connections[0] = iconRegistry.registerIcon("Nanotube_Connection");
		this.connections[1] = iconRegistry.registerIcon("Nanotube_Connection_Input");
		this.connections[2] = iconRegistry.registerIcon("Nanotube_Connection_Output");
		this.connections[3] = iconRegistry.registerIcon("Nanotube_Connection_None");
	}

	@Override
	public void registerIcons(PC_IconRegistry iconRegistry) {
		this.itemIcon = iconRegistry.registerIcon("Nanotube_Normal");
	}
	
}
