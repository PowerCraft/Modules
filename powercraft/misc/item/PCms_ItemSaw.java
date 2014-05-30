package powercraft.misc.item;

import java.util.List;
import java.util.WeakHashMap;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import powercraft.api.PC_IconRegistry;
import powercraft.api.PC_Utils;
import powercraft.api.PC_Vec4I;
import powercraft.api.building.PC_Build;
import powercraft.api.building.PC_Build.ItemStackSpawn;
import powercraft.api.building.PC_Harvest;
import powercraft.api.item.PC_ItemTool;
import powercraft.api.recipes.PC_Recipes;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class PCms_ItemSaw extends PC_ItemTool {

	private static final Object proper[] = new Object[] {Blocks.planks, Blocks.bookshelf, Blocks.log, Blocks.log2, Blocks.chest, Blocks.pumpkin, Blocks.lit_pumpkin, Material.wood, Material.plants, Material.vine};
	
	private static final WeakHashMap<EntityPlayer, PC_Vec4I> digSpeed = new WeakHashMap<EntityPlayer, PC_Vec4I>();
	
	public static final IIcon[] icons = new IIcon[4];
	
	public PCms_ItemSaw() {
		super(3.0f, ToolMaterial.IRON, proper);
		setCreativeTab(CreativeTabs.tabTools);
		setContainerItem(this);
		setMaxDamage(100);
		setMaxStackSize(1);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@Override
	public void initRecipes() {
		PC_Recipes.addShapedRecipe(new ItemStack(this), "sss", " ii", Character.valueOf('s'), Items.stick, Character.valueOf('i'), Items.iron_ingot);
	}
	
	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player) {
		digSpeed.remove(player);
		PC_Harvest harvest = PC_Build.getHarvest(player.worldObj, x, y, z, itemstack.getMaxDamage()-itemstack.getItemDamage()+1);
		List<ItemStackSpawn> list = PC_Build.harvestWithDropPos(player.worldObj, harvest, 0);
		PC_Utils.spawnItems(player.worldObj, list);
		itemstack.damageItem(harvest.itemUse, player);
		player.addStat(StatList.objectUseStats[Item.getIdFromItem(this)], 1);
		if (itemstack.stackSize == 0){
			player.destroyCurrentEquippedItem();
        }
		return true;
	}

	@Override
	public void registerIcons(PC_IconRegistry iconRegistry) {
		this.itemIcon = iconRegistry.registerIcon("icon");
	}
	
	@Override
	public boolean doesContainerItemLeaveCraftingGrid(ItemStack itemStack) {
		return false;
	}

	@Override
	public ItemStack getContainerItem(ItemStack itemStack) {
		return new ItemStack(itemStack.getItem(), 1, itemStack.getItemDamage()+1);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldRotateAroundWhenRendering(){
		return true;
	}

	@Override
	public float updateDigSpeed(ItemStack itemStack, float speed, int x, int y, int z, EntityPlayer entityPlayer){
		PC_Vec4I vec = digSpeed.get(entityPlayer);
		if(vec==null || vec.x != x || vec.y != y || vec.z != z){
			vec = null;
			PC_Harvest harvest = PC_Build.getHarvest(entityPlayer.worldObj, x, y, z, itemStack.getMaxDamage()-itemStack.getItemDamage());
			if(harvest!=null){
				digSpeed.put(entityPlayer, vec = new PC_Vec4I(x, y, z, Float.floatToIntBits(harvest.digTimeMultiply)));
				return speed / harvest.digTimeMultiply;
			}
		}
		if(vec==null){
			digSpeed.remove(entityPlayer);
			return speed;
		}
		return speed / Float.intBitsToFloat(vec.w);
	}
	
	@SuppressWarnings("static-method")
	@SubscribeEvent
	public void getPlayerInteract(PlayerInteractEvent event){
		digSpeed.remove(event.entityPlayer);
	}
	
}
