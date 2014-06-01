package powercraft.itemstorage.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import powercraft.api.PC_Vec2I;

public class PCis_EnderCompressorInventory extends PCis_CompressorInventory {

	private EntityPlayer player;
	
	public PCis_EnderCompressorInventory(EntityPlayer player, IInventory inventory, int slot, String name) {
		super(inventory, slot, new PC_Vec2I(9, 3), name);
		this.player = player;
	}

	@Override
	public int getSizeInventory() {
		return this.player.getInventoryEnderChest().getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int var1) {
		return this.player.getInventoryEnderChest().getStackInSlot(var1);
	}

	@Override
	public ItemStack decrStackSize(int var1, int var2) {
		return this.player.getInventoryEnderChest().decrStackSize(var1, var2);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int var1) {
		return this.player.getInventoryEnderChest().getStackInSlotOnClosing(var1);
	}

	@Override
	public void setInventorySlotContents(int var1, ItemStack var2) {
		this.player.getInventoryEnderChest().setInventorySlotContents(var1, var2);
	}

	@Override
	public int getInventoryStackLimit() {
		return this.player.getInventoryEnderChest().getInventoryStackLimit();
	}

	@Override
	public void openInventory() {
		this.player.getInventoryEnderChest().openInventory();
	}

	@Override
	public void closeInventory() {
		this.player.getInventoryEnderChest().closeInventory();
	}

	@Override
	public boolean isItemValidForSlot(int var1, ItemStack var2) {
		return this.player.getInventoryEnderChest().isItemValidForSlot(var1, var2) && super.isItemValidForSlot(var1, var2);
	}
	
}
