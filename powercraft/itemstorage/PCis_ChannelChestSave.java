package powercraft.itemstorage;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import powercraft.api.PC_WorldSaveData;
import powercraft.api.inventory.PC_IInventorySetNoMark;
import powercraft.api.inventory.PC_InventoryUtils;


public class PCis_ChannelChestSave extends PC_WorldSaveData {

	private static final String NAME = "powercraft-channelchests";
	
	private static int lastID = 0;
	
	static PCis_ChannelChestSave save;
	
	private static PCis_ChannelChestSave getSave(){
		if(save==null){
			save = loadOrCreate(NAME, PCis_ChannelChestSave.class);
		}
		return save;
	}
	
	public static PCis_ChannelChestInventory getInventoryForChannelChest(int id){
		return getSave().channels.get(Integer.valueOf(id));
	}
	
	public static PCis_ChannelChestInventory addRef(int id){
		if(id==0)
			return null;
		PCis_ChannelChestInventory inv = getSave().channels.get(Integer.valueOf(id));
		if(inv==null){
			save.channels.put(Integer.valueOf(id), inv = new PCis_ChannelChestInventory(9*3));
		}
		inv.addRef();
		return inv;
	}
	
	public static void remove(int id){
		PCis_ChannelChestInventory inv = getSave().channels.get(Integer.valueOf(id));
		if(inv!=null && inv.remove()){
			save.channels.remove(Integer.valueOf(id));
		}
	}
	
	private Map<Integer, PCis_ChannelChestInventory> channels = new HashMap<Integer, PCis_ChannelChestInventory>();
	
	public PCis_ChannelChestSave(String name) {
		super(name);
	}

	@Override
	public void cleanup() {
		save = null;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		NBTTagList list = (NBTTagList)nbtTagCompound.getTag("save");
		for(int i=0; i<list.tagCount(); i++){
			NBTTagCompound com = list.getCompoundTagAt(i);
			this.channels.put(Integer.valueOf(com.getInteger("key")), new PCis_ChannelChestInventory(com));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		NBTTagList list = new NBTTagList();
		for(Entry<Integer, PCis_ChannelChestInventory> e:this.channels.entrySet()){
			NBTTagCompound com = new NBTTagCompound();
			com.setInteger("key", e.getKey().intValue());
			e.getValue().saveToNBT(com);
			list.appendTag(com);
		}
		nbtTagCompound.setTag("save", list);
	}

	public static class PCis_ChannelChestInventory implements IInventory, PC_IInventorySetNoMark{

		private ItemStack[] inventoryContents;
		private int refs;
		private boolean isFake;
		private int playersAccessing;
		
		PCis_ChannelChestInventory(int size){
			this.inventoryContents = new ItemStack[size];
		}
		
		PCis_ChannelChestInventory(int size, boolean isFake){
			this.inventoryContents = new ItemStack[size];
			this.isFake = isFake;
		}

		PCis_ChannelChestInventory(NBTTagCompound nbtTagCompound){
			this.inventoryContents = new ItemStack[nbtTagCompound.getInteger("size")];
			PC_InventoryUtils.loadInventoryFromNBT(this, nbtTagCompound, "inv");
			this.refs = nbtTagCompound.getInteger("refs");
		}

		public void saveToNBT(NBTTagCompound tag) {
			tag.setInteger("size", this.inventoryContents.length);
			PC_InventoryUtils.saveInventoryToNBT(this, tag, "inv");
			tag.setInteger("refs", this.refs);
		}
		
		public void addRef() {
			this.refs++;
		}

		public boolean remove() {
			return --this.refs<=0;
		}
		
		@Override
		public int getSizeInventory() {
			return this.inventoryContents.length;
		}

		@Override
		public ItemStack getStackInSlot(int i) {
			return this.inventoryContents[i];
		}

		@Override
		public ItemStack decrStackSize(int i, int j) {
			if (this.inventoryContents[i] != null) {
				ItemStack itemstack;
				if (this.inventoryContents[i].stackSize <= j) {
					itemstack = this.inventoryContents[i];
					this.inventoryContents[i] = null;
					markDirty();
					return itemstack;
				} 
				itemstack = this.inventoryContents[i].splitStack(j);
				if (this.inventoryContents[i].stackSize == 0) {
					this.inventoryContents[i] = null;
				}
				markDirty();
				return itemstack;
			}
			return null;
		}

		@Override
		public ItemStack getStackInSlotOnClosing(int i) {
			if (this.inventoryContents[i] != null) {
				ItemStack itemstack = this.inventoryContents[i];
				this.inventoryContents[i] = null;
				return itemstack;
			} 
			return null;
		}

		@Override
		public void setInventorySlotContents(int i, ItemStack itemstack) {
			this.inventoryContents[i] = itemstack;
			if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
				itemstack.stackSize = getInventoryStackLimit();
			}
			markDirty();
		}
		
		@Override
		public void setInventorySlotContentsNoMark(int i, ItemStack itemstack) {
			this.inventoryContents[i] = itemstack;
			if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
				itemstack.stackSize = getInventoryStackLimit();
			}
		}

		@Override
		public String getInventoryName() {
			return "Channel Chest";
		}

		@Override
		public boolean hasCustomInventoryName() {
			return true;
		}

		@Override
		public int getInventoryStackLimit() {
			return 64;
		}

		@Override
		public void markDirty() {
			if(!this.isFake)
				save.markDirty();
		}

		@Override
		public boolean isUseableByPlayer(EntityPlayer entityplayer) {
			return true;
		}

		@Override
		public void openInventory() {
			this.playersAccessing++;
		}

		@Override
		public void closeInventory() {
			this.playersAccessing--;
			if(this.playersAccessing<0)
				throw new AssertionError();
		}

		@Override
		public boolean isItemValidForSlot(int i, ItemStack itemStack) {
			return true;
		}
		
		public int getPlayersAccessing(){
			return this.playersAccessing;
		}
		
	}

	public static PCis_ChannelChestInventory getFake() {
		return new PCis_ChannelChestInventory(9*3, true);
	}

	public static int getNextFreeID() {
		Set<Integer> set = getSave().channels.keySet();
		lastID++;
		while(set.contains(Integer.valueOf(lastID)) || lastID==0){
			lastID++;
		}
		return lastID;
	}
	
}
