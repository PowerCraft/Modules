package powercraft.weasel.multiblock;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;
import powercraft.api.PC_IconRegistry;
import powercraft.api.multiblock.PC_MultiblockObject;
import powercraft.api.multiblock.cable.PC_MultiblockItemCable;

public class PCws_MultiblockItemWeaselCable extends PC_MultiblockItemCable {

	IIcon normal;
	IIcon corner;
	IIcon side;
	
	public PCws_MultiblockItemWeaselCable(){
		setCreativeTab(CreativeTabs.tabRedstone);
	}
	
	@Override
	public Class<? extends PC_MultiblockObject> getMultiblockObjectClass() {
		return PCws_MultiblockObjectWeaselCable.class;
	}

	@Override
	public void loadMultiblockIcons(PC_IconRegistry iconRegistry) {
		this.normal = iconRegistry.registerIcon("normal");
		this.corner = iconRegistry.registerIcon("corner");
		this.side = iconRegistry.registerIcon("side");
	}

	@Override
	public void registerIcons(PC_IconRegistry iconRegistry) {
		this.itemIcon = iconRegistry.registerIcon("normal");
	}
	
}
