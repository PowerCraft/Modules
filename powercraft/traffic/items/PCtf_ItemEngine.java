package powercraft.traffic.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import powercraft.api.item.PC_Item;
import powercraft.api.recipes.PC_Recipes;

public class PCtf_ItemEngine extends PC_Item{
	
	public PCtf_ItemEngine(){
		setCreativeTab(CreativeTabs.tabTools);
	}

	@Override
	public void initRecipes() {
		PC_Recipes.addShapelessRecipe(new ItemStack(this), Items.furnace_minecart);
	}
	
	
	
}
