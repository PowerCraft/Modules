package powercraft.redstone.multiblock;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;
import powercraft.api.PC_IconRegistry;
import powercraft.api.multiblock.PC_MultiblockObject;
import powercraft.api.multiblock.cable.PC_MultiblockItemCable;


public class PCrs_MultiblockItemRedstoneCable extends PC_MultiblockItemCable{
	
	static IIcon icon;
	
	public PCrs_MultiblockItemRedstoneCable(){
		setCreativeTab(CreativeTabs.tabRedstone);
	}
	
	@Override
	public Class<? extends PC_MultiblockObject> getMultiblockObjectClass() {
		return PCrs_MultiblockObjectRedstoneCable.class;
	}

	@Override
	public void loadMultiblockIcons(PC_IconRegistry iconRegistry) {
		icon = iconRegistry.registerIcon("redstone");
	}

	@Override
	public void registerIcons(PC_IconRegistry iconRegistry) {
		this.itemIcon = iconRegistry.registerIcon("item");
	}
	
}
