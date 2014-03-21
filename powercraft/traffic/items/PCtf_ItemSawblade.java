package powercraft.traffic.items;

import net.minecraft.creativetab.CreativeTabs;
import powercraft.api.PC_IconRegistry;
import powercraft.api.item.PC_Item;


public class PCtf_ItemSawblade extends PC_Item {
	
	public PCtf_ItemSawblade(){
		setCreativeTab(CreativeTabs.tabTools);
	}

	@Override
	public void registerIcons(PC_IconRegistry iconRegistry) {
		this.itemIcon = iconRegistry.registerIcon("item");
	}
	
}
