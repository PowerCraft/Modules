package powercraft.redstone.multiblock;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import powercraft.api.PC_IconRegistry;
import powercraft.api.PC_Utils;
import powercraft.api.multiblock.PC_MultiblockObject;
import powercraft.api.multiblock.cable.PC_MultiblockItemCable;


public class PCrs_MultiblockItemRedstoneBundleCable extends PC_MultiblockItemCable{
	
	static IIcon[] icons = new IIcon[17];
	
	private IIcon redstone;
	
	public PCrs_MultiblockItemRedstoneBundleCable(){
		setCreativeTab(CreativeTabs.tabRedstone);
		setHasSubtypes(true);
	}
	
	@Override
	public Class<? extends PC_MultiblockObject> getMultiblockObjectClass() {
		return PCrs_MultiblockObjectRedstoneBundleCable.class;
	}
	
	@Override
	public PC_MultiblockObject getMultiblockObject(ItemStack itemStack) {
		return new PCrs_MultiblockObjectRedstoneBundleCable(itemStack.getItemDamage());
	}

	@Override
	public void loadMultiblockIcons(PC_IconRegistry iconRegistry) {
		icons[0] = iconRegistry.registerIcon("isolation");
		for(int i=0; i<16; i++){
			icons[i+1] = iconRegistry.registerIcon("Cable"+i);
		}
	}

	@Override
	public void registerIcons(PC_IconRegistry iconRegistry) {
		this.itemIcon = iconRegistry.registerIcon("isolation");
		this.redstone = iconRegistry.registerIcon("redstone");
	}

	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		if(pass==0){
			return this.itemIcon;
		}
		return this.redstone;
	}

	@Override
	public int getColorFromItemStack(ItemStack stack, int pass) {
		if(pass==0){
			return -1;
		}
		return PC_Utils.getColorFor(stack.getItemDamage());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void getSubItems(Item item, CreativeTabs creativeTabs, List list) {
		for(int i=0; i<16; i++){
			list.add(new ItemStack(this, 1, i));
		}
	}
	
	
	
}
