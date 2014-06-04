package powercraft.core;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import powercraft.api.PC_Utils;
import powercraft.api.inventory.PC_IInventory;
import powercraft.core.gui.PCco_GuiCraftingTool;

@SideOnly(Side.CLIENT)
public class PCco_CraftingToolCraftingInventory implements PC_IInventory {

	private static final class Crafting{
		
		int nums;
		IRecipe recipe;
		List<ItemStack>[] crafting;
		
		public Crafting(int nums, IRecipe recipe, List<ItemStack>[] crafting) {
			this.nums = nums;
			this.recipe = recipe;
			this.crafting = crafting;
		}
		
	}
	
	ItemStack product;
	List<Crafting> craftings = new ArrayList<Crafting>();
	private int scroll;
	private int tick = 0;
	private int tick2 = 0;
	private RecipeSearchThread recipeSearch;
	PCco_GuiCraftingTool gui;
	
	public PCco_CraftingToolCraftingInventory() {
		
	}

	public void setGui(PCco_GuiCraftingTool gui){
		this.gui = gui;
	}
	
	public void setProduct(ItemStack product){
		this.product = product;
		this.craftings.clear();
		if(this.recipeSearch!=null){
			this.recipeSearch.stopSearch();
			this.recipeSearch=null;
		}
		this.recipeSearch = new RecipeSearchThread();
	}
	
	public void setScroll(int scroll){
		this.scroll = scroll;
	}
	
	public int getNumRecipes(){
		return this.craftings.size();
	}
	
	public void nextTick(){
		this.tick2++;
		if(this.tick2>20){
			this.tick2=0;
			this.tick++;
		}
	}
	
	private ItemStack getItemStackInSlot(int i){
		int page = this.scroll+(i/10);
		int slot = i%10;
		if(this.craftings.size()<=page){
			return null;
		}
		Crafting crafting = this.craftings.get(page);
		if(slot==0){
			InventoryCrafting inventorycrafting = new InventoryCrafting(this.gui, 3, 3);
			for(int j=0; j<9; j++){
				List<ItemStack> list = crafting.crafting[j];
				if(list!=null)
					inventorycrafting.setInventorySlotContents(j, list.get(this.tick%crafting.nums));
			}
			return crafting.recipe.getCraftingResult(inventorycrafting);
		}
		List<ItemStack> list = crafting.crafting[slot-1];
		if(list!=null){
			ItemStack is = list.get(this.tick%crafting.nums);
			is.stackSize=1;
			return is;
		}
		return null;
	}
	
	@Override
	public int getSizeInventory() {
		return 10;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return getItemStackInSlot(i);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		//
	}

	@Override
	public String getInventoryName() {
		return "CraftingToolCraftingInventory";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {
		//
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return false;
	}

	@Override
	public void openInventory() {
		//
	}

	@Override
	public void closeInventory() {
		//
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return false;
	}

	@Override
	public boolean canTakeStack(int i, EntityPlayer entityPlayer) {
		return false;
	}

	@Override
	public boolean canDropStack(int i) {
		return false;
	}

	@Override
	public boolean canBeDragged(int i) {
		return false;
	}

	@Override
	public int getSlotStackLimit(int i) {
		ItemStack is = getItemStackInSlot(i);
		if(is!=null)
			return is.getMaxStackSize();
		return 0;
	}

	private class RecipeSearchThread extends Thread{
		
		private boolean stop;
		
		public RecipeSearchThread(){
			setDaemon(true);
			setPriority(MIN_PRIORITY);
			start();
		}
		
		public void stopSearch(){
			this.stop=true;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void run(){
			PCco_CraftingToolCraftingInventory.this.gui.addWorking();
			try{
				List<IRecipe> recipes = PC_Utils.getRecipesForProduct(PCco_CraftingToolCraftingInventory.this.product);
				for(IRecipe recipe : recipes){
					
					List<ItemStack>[][] expectedInputs2d = PC_Utils.getExpectedInput(recipe, 3, 3);
					if(this.stop)
						return;
					List<ItemStack>[] expectedInputs = new List[9];
					int nums=1;
					for(int y=0; y<3; y++){
						for(int x=0; x<3; x++){
							List<ItemStack> list2 = expectedInputs2d[x][y];
							if(list2!=null && list2.size()>0){
								List<ItemStack> list = expectedInputs[y*3+x] = new ArrayList<ItemStack>();
								for(ItemStack is:list2){
									if(is.getItemDamage()==PC_Utils.WILDCARD_VALUE){
										Item i = is.getItem();
										i.getSubItems(i, i.getCreativeTab(), list);
									}else{
										list.add(is);
									}
								}
								nums *= list.size();
							}
						}
					}
					if(this.stop)
						return;
					List<ItemStack>[] crafting = new List[9];
					for(int i=0; i<crafting.length; i++){
						if(expectedInputs[i]!=null){
							crafting[i] = new ArrayList<ItemStack>();
						}
					}
					if(this.stop)
						return;
					for(int n=0; n<nums; n++){
						int n1 = n;
						for(int i=0; i<crafting.length; i++){
							List<ItemStack> list = expectedInputs[i];
							if(list!=null){
								int n2 = n1 % list.size();
								n1 = n1 / list.size();
								ItemStack is = list.get(n2);
								crafting[i].add(is);
							}
						}
					}
					// TODO change to other type of scrolling through possibilities, so there will be big lists :/
					if(this.stop)
						return;
					PCco_CraftingToolCraftingInventory.this.craftings.add(new Crafting(nums, recipe, crafting));
					PCco_CraftingToolCraftingInventory.this.gui.updateCraftings();
				}
			}finally{
				PCco_CraftingToolCraftingInventory.this.gui.removeWorking();
			}
		}
		
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return null;
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		return false;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return false;
	}

	@Override
	public void onTick(World world) {
		//
	}

	@Override
	public int[] getAppliedGroups(int i) {
		return null;
	}

	@Override
	public int[] getAppliedSides(int i) {
		return null;
	}
	
}
