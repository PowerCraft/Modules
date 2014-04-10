package powercraft.traffic.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import powercraft.api.item.PC_Item;
import powercraft.api.recipes.PC_Recipes;

public class PCtf_ItemEnergyConverter extends PC_Item {
	
	public PCtf_ItemEnergyConverter(){
		setCreativeTab(CreativeTabs.tabTools);
	}

	@Override
	public void initRecipes() {
		PC_Recipes.addShapelessRecipe(new ItemStack(this), Blocks.furnace);
	}
	
}
