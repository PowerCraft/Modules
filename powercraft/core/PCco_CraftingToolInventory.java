package powercraft.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercraft.api.PC_Vec2I;
import powercraft.api.inventory.PC_IInventory;
import powercraft.api.inventory.PC_IInventoryBackground;
import powercraft.core.gui.PCco_GuiCraftingTool;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PCco_CraftingToolInventory implements PC_IInventory, PC_IInventoryBackground {

	List<ItemStack> items = new ArrayList<ItemStack>();
	List<ItemStack> items2Display = Collections.synchronizedList(new ArrayList<ItemStack>());
	
	PC_Vec2I size;
	boolean[] can;
	int scroll=0;
	
	PCco_GuiCraftingTool gui;
	
	private SearchThread search;
	private UpdateAvailabilityThread updateAvailability;
	
	@SuppressWarnings("unchecked")
	public PCco_CraftingToolInventory(PCco_GuiCraftingTool gui, PC_Vec2I size){
		this.size = new PC_Vec2I(size);
		this.gui = gui;
		this.can = new boolean[size.x*size.y];
		Iterator<Item> iterator = Item.itemRegistry.iterator();
		
        while (iterator.hasNext()){
            Item item = iterator.next();
            if (item != null && item.getCreativeTab()!=null){
            	item.getSubItems(item, item.getCreativeTab(), this.items);
            }
        }
        
		this.items2Display.addAll(this.items);
	}
	
	public void setSearchString(String searchString){
		if(this.search!=null){
			this.search.stopSearch();
			this.search = null;
		}
		this.items2Display.clear();
		if(searchString == null || searchString.equals("")){
			this.items2Display.addAll(this.items);
		}else{
			this.search = new SearchThread(searchString);
		}
		updateAvailability();
	}
	
	public void setScroll(int scroll){
		if(this.scroll != scroll){
			this.scroll = scroll;
			updateAvailability();
		}
	}
	
	public ItemStack getProductForSlot(int i){
		int slot = i+this.scroll*this.size.x;
		synchronized(this.items2Display){
			if(slot<this.items2Display.size()){
				return this.items2Display.get(slot);
			}
		}
		return null;
	}
	
	public int getNumRows(){
		synchronized(this.items2Display){
			return this.items2Display.size()/this.size.x+1;
		}
	}
	
	public boolean canBeCrafted(int slot){
		synchronized(this.can){
			return this.can[slot];
		}
	}
	
	@Override
	public int getSizeInventory() {
		return this.size.x*this.size.y;
	}

	public synchronized void updateAvailability(){
		if(this.updateAvailability!=null){
			this.updateAvailability.stopUpdateAvailability();
			this.updateAvailability = null;
		}
		this.updateAvailability = new UpdateAvailabilityThread();
	}
	
	@Override
	public ItemStack getStackInSlot(int i) {
		if(canBeCrafted(i)){
			ItemStack is = getProductForSlot(i);
			if(is!=null){
				is = is.copy();
				is.stackSize = 1;
				return is;
			}
		}
		return null;
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
		return "CraftingToolInventory";
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
		return true;
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
		ItemStack is=getProductForSlot(i);
		return is==null?0:is.getMaxStackSize();
	}

	@Override
	public ItemStack getBackgroundStack(int slotIndex) {
		return getProductForSlot(slotIndex);
	}

	@Override
	public boolean renderGrayWhenEmpty(int slotIndex) {
		return true;
	}

	private class SearchThread extends Thread{
		
		private String searchString;
		private Object sync = new Object();
		private boolean stop = false;
		
		public SearchThread(String searchString){
			this.searchString = searchString.toLowerCase();
			setDaemon(true);
			setPriority(MIN_PRIORITY);
			start();
		}
		
		public void stopSearch(){
			synchronized(this.sync){
				this.stop = true;
			}
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void run(){
			PCco_CraftingToolInventory.this.gui.addWorking();
			try{
				int num=0;
				EntityPlayer player = PCco_CraftingToolInventory.this.gui.getPlayer();
				for(ItemStack itemStack:PCco_CraftingToolInventory.this.items){
					List<String> info = itemStack.getTooltip(player, false);
					for(String infoString:info){
						if (infoString.toLowerCase().contains(this.searchString)){
							synchronized(this.sync){
								if(this.stop)
									return;
							}
							synchronized(PCco_CraftingToolInventory.this.items2Display){
								PCco_CraftingToolInventory.this.items2Display.add(itemStack);
							}
							num++;
							PCco_CraftingToolInventory.this.gui.updateSrcoll();
							if(num==PCco_CraftingToolInventory.this.scroll*PCco_CraftingToolInventory.this.size.x+PCco_CraftingToolInventory.this.size.x*PCco_CraftingToolInventory.this.size.y-1){
								updateAvailability();
							}
							break;
						}
					}
					synchronized(this.sync){
						if(this.stop)
							return;
					}
				}
				if(num<PCco_CraftingToolInventory.this.scroll*PCco_CraftingToolInventory.this.size.x+PCco_CraftingToolInventory.this.size.x*PCco_CraftingToolInventory.this.size.y-1){
					updateAvailability();
				}
			}finally{
				PCco_CraftingToolInventory.this.gui.removeWorking();
			}
		}
		
	}
	
	private class UpdateAvailabilityThread extends Thread{
		
		private Object sync = new Object();
		private boolean stop = false;
		
		public UpdateAvailabilityThread(){
			setDaemon(true);
			setPriority(MIN_PRIORITY);
			start();
		}
		
		public void stopUpdateAvailability(){
			synchronized(this.sync){
				this.stop = true;
			}
		}
		
		@Override
		public void run(){
			PCco_CraftingToolInventory.this.gui.addWorking();
			try{
				synchronized(PCco_CraftingToolInventory.this.can){
					for(int i=0; i<PCco_CraftingToolInventory.this.can.length; i++){
						PCco_CraftingToolInventory.this.can[i] = false;
					}
				}
				EntityPlayer player = PCco_CraftingToolInventory.this.gui.getPlayer();
				for(int i=0; i<PCco_CraftingToolInventory.this.can.length; i++){
					ItemStack is = getProductForSlot(i);
					if(is!=null){
						boolean availabe = PCco_CraftingToolCrafter.tryToCraft(is, player);
						synchronized(this.sync){
							if(this.stop)
								return;
						}
						if(availabe){
							synchronized(PCco_CraftingToolInventory.this.can){
								PCco_CraftingToolInventory.this.can[i] = true;
							}
						}
					}
				}
			}finally{
				PCco_CraftingToolInventory.this.gui.removeWorking();
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
