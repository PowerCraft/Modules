package powercraft.itemstorage.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import powercraft.api.PC_Vec2I;
import powercraft.api.inventory.PC_IInventorySizeOverrider;
import powercraft.api.inventory.PC_InventoryUtils;

public class PCis_HightCompressorInventory extends PCis_NormalCompressorInventory implements PC_IInventorySizeOverrider {
	
	public PCis_HightCompressorInventory(IInventory inventory, int slot, String name){
		super(inventory, slot, new PC_Vec2I(3, 3), name);
		NBTTagCompound tag = getItemStack().getTagCompound();
		if(tag.hasKey("size")){
			int[] size = tag.getIntArray("size");
			for(int i=0; i<this.is.length; i++){
				if(this.is[i] != null) this.is[i].stackSize = size[i];	
			}
		}
	}

	@Override
	public ItemStack getStackInSlot(int var1) {
		return this.is[var1];
	}

	@Override
	public void setInventorySlotContents(int var1, ItemStack var2) {
		this.is[var1]=var2;
	}
	
	@Override
	public ItemStack decrStackSize(int var1, int var2) {
		int size = var2;
		if(size>64)
			size = 64;
		return super.decrStackSize(var1, size);
	}

	@Override
	public int getInventoryStackLimit() {
		return 512;
	}
	
	@Override
	public int getSlotStackLimit(int slotIndex) {
		return 512;
	}

	@Override
	public void closeInventory() {
		if(getItemStack()!=null){
			int[] size = new int[this.is.length];
			for(int i=0; i<this.is.length; i++){
				if(this.is[i]!=null){
					size[i] = this.is[i].stackSize;
					this.is[i].stackSize = 1;
				}
			}
			getItemStack().getTagCompound().setIntArray("size", size);
			PC_InventoryUtils.saveInventoryToNBT(this, getItemStack().getTagCompound(), "inv"); 
		}
	}

	@Override
	public int getMaxStackSize(ItemStack itemStack, int slot) {
		int maxStack = itemStack.getMaxStackSize()*8;
		int maxSlot = getSlotStackLimit(slot);
		return maxStack>maxSlot?maxSlot:maxStack;
	}
	
}
