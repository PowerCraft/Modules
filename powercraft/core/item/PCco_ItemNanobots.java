package powercraft.core.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercraft.api.PC_IconRegistry;
import powercraft.api.item.PC_Item;
import powercraft.api.recipes.PC_Recipes;


public class PCco_ItemNanobots extends PC_Item {
	
	public PCco_ItemNanobots(){
		setCreativeTab(CreativeTabs.tabTools);
	}

	@Override
	public void registerIcons(PC_IconRegistry iconRegistry) {
		this.itemIcon = iconRegistry.registerIcon("item");
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		return PC_Recipes.tryToDo3DRecipe(world, x, y, z);
	}
	
}
