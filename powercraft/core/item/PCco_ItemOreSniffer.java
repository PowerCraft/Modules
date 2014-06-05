package powercraft.core.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import powercraft.api.PC_Field.Flag;
import powercraft.api.PC_IconRegistry;
import powercraft.api.PC_Lang;
import powercraft.api.PC_Vec4I;
import powercraft.api.gres.PC_Gres;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.PC_IGresGuiOpenHandler;
import powercraft.api.item.PC_Item;
import powercraft.api.recipes.PC_Recipes;
import powercraft.core.PCco_Core;
import powercraft.core.gui.PCco_GuiOreSnifferResultScreen;


public class PCco_ItemOreSniffer extends PC_Item implements PC_IGresGuiOpenHandler {
	
	public PCco_ItemOreSniffer(){
        setMaxStackSize(1);
        setMaxDamage(500);
        setCreativeTab(CreativeTabs.tabTools);
    }

	@Override
	public void initRecipes() {
		PC_Recipes.addShapedRecipe(new ItemStack(this), " G ", "GNG", " G ", Character.valueOf('G'), Items.gold_ingot, Character.valueOf('N'), PCco_Core.NANOBOTS);
	}
    
	@Override
	public void registerIcons(PC_IconRegistry iconRegistry) {
		this.itemIcon = iconRegistry.registerIcon("icon");
	}
	
    @Override
    public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int x, int y, int z, int side, float par8, float par9, float par10){
        if (!world.isRemote){
        	PC_Gres.openGui(entityplayer, this, new PC_Vec4I(x, y, z, side));
        }

        itemstack.damageItem(1, entityplayer);
        return true;
    }
    
    @Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
		return itemStack;
	}

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean b){
        list.add(PC_Lang.tr("item.PCco_ItemOreSniffer.desc"));
    }

	@Override
	public PC_IGresGui openClientGui(EntityPlayer player, NBTTagCompound serverData) {
		return new PCco_GuiOreSnifferResultScreen(player, new PC_Vec4I(serverData, Flag.SYNC));
	}

	@Override
	public PC_GresBaseWithInventory openServerGui(EntityPlayer player, Object[] params) {
		return null;
	}

	@Override
	public NBTTagCompound sendOnGuiOpenToClient(EntityPlayer player, Object[] params) {
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		((PC_Vec4I)params[0]).saveToNBT(nbtTagCompound, Flag.SYNC);
		return nbtTagCompound;
	}
    
    
	
}
