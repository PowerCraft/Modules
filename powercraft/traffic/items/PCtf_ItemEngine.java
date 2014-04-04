package powercraft.traffic.items;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import powercraft.api.item.PC_Item;
import powercraft.api.recipes.PC_Recipes;

public class PCtf_ItemEngine extends PC_Item{
	
	public PCtf_ItemEngine(){
		PC_Recipes.addShapelessRecipe(new ItemStack(this), Items.furnace_minecart);
	}
}
