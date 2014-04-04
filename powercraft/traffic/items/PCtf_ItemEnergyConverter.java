package powercraft.traffic.items;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import powercraft.api.item.PC_Item;
import powercraft.api.recipes.PC_Recipes;

public class PCtf_ItemEnergyConverter extends PC_Item {
	public PCtf_ItemEnergyConverter(){
		PC_Recipes.addShapelessRecipe(new ItemStack(this), Blocks.furnace);
	}
}
