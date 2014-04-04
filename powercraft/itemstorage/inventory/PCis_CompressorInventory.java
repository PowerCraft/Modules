package powercraft.itemstorage.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercraft.api.PC_Vec2I;
import powercraft.api.inventory.PC_IInventory;
import powercraft.itemstorage.PCis_ItemStorage;

public abstract class PCis_CompressorInventory implements PC_IInventory {

	protected IInventory inventory;
	protected int slot;
	protected PC_Vec2I size;
	
	public PCis_CompressorInventory(IInventory inventory, int slot, PC_Vec2I size){
		this.inventory = inventory;
		this.slot = slot;
		this.size = size;
	}
	
	public ItemStack getItemStack(){
		return this.inventory.getStackInSlot(this.slot);
	}
	
	public PC_Vec2I getSize(){
		return new PC_Vec2I(this.size);
	}

	@Override
	public String getInventoryName() {
		return "Compressor";
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
	public void markDirty() {/**/}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) {
		return true;
	}

	@Override
	public void openInventory() {/**/}
	
	@Override
	public boolean isItemValidForSlot(int var1, ItemStack var2) {
		return var2.getItem() != PCis_ItemStorage.compressor;
	}
	
	@Override
	public int getSlotStackLimit(int slotIndex) {
		return getInventoryStackLimit();
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
	public boolean canTakeStack(int i, EntityPlayer entityPlayer) {
		return true;
	}

	@Override
	public boolean canDropStack(int i) {
		return false;
	}

	@Override
	public boolean canBeDragged(int i) {
		return true;
	}

	@Override
	public void onTick(World world) {/**/}

	@Override
	public int[] getAppliedGroups(int i) {
		return null;
	}

	@Override
	public int[] getAppliedSides(int i) {
		return null;
	}

}
