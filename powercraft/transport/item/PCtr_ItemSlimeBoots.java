package powercraft.transport.item;

import net.minecraft.creativetab.CreativeTabs;
import powercraft.api.PC_IconRegistry;
import powercraft.api.item.PC_ItemArmor;

public class PCtr_ItemSlimeBoots extends PC_ItemArmor{

	public PCtr_ItemSlimeBoots(){
		super(FEET, 0, 1, 1);
		setCreativeTab(CreativeTabs.tabCombat);
	}

	@Override
	public void registerIcons(PC_IconRegistry iconRegistry) {
		this.itemIcon = iconRegistry.registerIcon("item");
	}
	
	
	
}
