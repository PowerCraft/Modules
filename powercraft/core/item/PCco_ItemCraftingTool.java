package powercraft.core.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import powercraft.api.PC_IconRegistry;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.PC_IGresGuiOpenHandler;
import powercraft.api.item.PC_Item;
import powercraft.api.recipes.PC_Recipes;
import powercraft.core.gui.PCco_GuiCraftingTool;

public class PCco_ItemCraftingTool extends PC_Item implements PC_IGresGuiOpenHandler{
	
    public PCco_ItemCraftingTool(){
        setMaxDamage(0);
        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.tabTools);
    }

    @Override
	public void initRecipes() {
		PC_Recipes.addShapedRecipe(new ItemStack(this), " R ", "RIR", " R ", Character.valueOf('I'), Blocks.iron_block, Character.valueOf('R'), Items.redstone);
	}
    
	@Override
	public void registerIcons(PC_IconRegistry iconRegistry) {
		this.itemIcon = iconRegistry.registerIcon("icon");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public PC_IGresGui openClientGui(EntityPlayer player, NBTTagCompound serverData) {
		return new PCco_GuiCraftingTool(player);
	}

	@Override
	public PC_GresBaseWithInventory openServerGui(EntityPlayer player, Object[] params) {
		return new PC_GresBaseWithInventory(player, new InventoryBasic("Basic", true, 0));
	}

	@Override
	public NBTTagCompound sendOnGuiOpenToClient(EntityPlayer player, Object[] params) {
		return null;
	}
    
}
