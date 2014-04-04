package powercraft.itemstorage.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import powercraft.api.PC_3DRotationY;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Field;
import powercraft.api.PC_Field.Flag;
import powercraft.api.block.PC_TileEntityRotateable;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.PC_IGresGuiOpenHandler;
import powercraft.itemstorage.PCis_ChannelChestSave;
import powercraft.itemstorage.PCis_ItemStorage;
import powercraft.itemstorage.container.PCis_ContainerChannelChest;
import powercraft.itemstorage.gui.PCis_GuiChannelChest;
import powercraft.itemstorage.item.PCis_ItemCompressor;


public class PCis_TileEntityChannelChest extends PC_TileEntityRotateable implements IInventory, PC_IGresGuiOpenHandler {

	@PC_Field(name="channelID")
	private int id;
	private IInventory inventory;
	
	public PCis_TileEntityChannelChest() {
		
	}
	
	public PCis_TileEntityChannelChest(int id) {
		this.id = id;
		this.inventory = PCis_ChannelChestSave.addRef(id);
	}
	
	@Override
	public void onBlockPostSet(PC_Direction side, ItemStack stack, EntityPlayer player, float hitX, float hitY, float hitZ) {
		set3DRotation(new PC_3DRotationY(player));
	}
	
	@SuppressWarnings("hiding")
	public void changeID(int id){
		if(!isClient()){
			PCis_ChannelChestSave.remove(this.id);
			this.id = id;
			this.inventory = PCis_ChannelChestSave.addRef(id);
		}
	}
	
	@Override
	public void onLoadedFromNBT(Flag flag) {
		if(flag==Flag.SAVE){
			this.inventory = PCis_ChannelChestSave.getInventoryForChannelChest(this.id);
		}else if(this.inventory!=null){
			this.inventory = PCis_ChannelChestSave.getFake();
		}
	}

	@Override
	public int getSizeInventory() {
		return this.inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return this.inventory.getStackInSlot(i);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return this.inventory.decrStackSize(i, j);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return this.inventory.getStackInSlotOnClosing(i);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemStack) {
		this.inventory.setInventorySlotContents(i, itemStack);
	}

	@Override
	public String getInventoryName() {
		return this.inventory.getInventoryName();
	}

	@Override
	public boolean hasCustomInventoryName() {
		return this.inventory.hasCustomInventoryName();
	}

	@Override
	public int getInventoryStackLimit() {
		return this.inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.inventory.isUseableByPlayer(player);
	}

	@Override
	public void openInventory() {
		this.inventory.openInventory();
	}

	@Override
	public void closeInventory() {
		this.inventory.closeInventory();
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemStack) {
		return this.inventory.isItemValidForSlot(i, itemStack);
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, PC_Direction side) {
		ItemStack is = player.getHeldItem();
		if(is!=null && ((is.getItem()==PCis_ItemStorage.compressor && is.getItemDamage()==PCis_ItemCompressor.CHANNEL)
				||(is.getItem() instanceof ItemBlock && ((ItemBlock)is.getItem()).field_150939_a==PCis_ItemStorage.CHANNEL_CHEST))){
			NBTTagCompound tagCompound = is.getTagCompound();
			if(tagCompound==null){
				is.setTagCompound(tagCompound = new NBTTagCompound());
			}
			tagCompound.setInteger("id", this.id);
			return true;
		}
		return super.onBlockActivated(player, side);
	}

	@Override
	public PC_IGresGui openClientGui(EntityPlayer player, NBTTagCompound serverData) {
		return new PCis_GuiChannelChest(player, this);
	}

	@Override
	public PC_GresBaseWithInventory openServerGui(EntityPlayer player) {
		return new PCis_ContainerChannelChest(player, this);
	}

	@Override
	public NBTTagCompound sendOnGuiOpenToClient(EntityPlayer player) {
		return null;
	}
	
}
