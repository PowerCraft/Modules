package powercraft.itemstorage.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import powercraft.api.PC_Vec2I;
import powercraft.api.inventory.PC_InventoryUtils;

public class PCis_NormalCompressorInventory extends PCis_CompressorInventory {

	protected ItemStack[] is;
	
	public PCis_NormalCompressorInventory(IInventory inventory, int slot, PC_Vec2I size) {
		super(inventory, slot, size);
		NBTTagCompound tag = getItemStack().getTagCompound();
		if(tag==null){
			getItemStack().setTagCompound(tag = new NBTTagCompound());
			tag.setInteger("sizeX", size.x);
			tag.setInteger("sizeY", size.y);
			this.is = new ItemStack[size.x*size.y];
		}else{
			size.x = tag.getInteger("sizeX");
			size.y = tag.getInteger("sizeY");
			this.is = new ItemStack[size.x*size.y];
			PC_InventoryUtils.loadInventoryFromNBT(this, tag, "inv");
		}
	}
	
	public PCis_NormalCompressorInventory(IInventory inventory, int slot) {
		this(inventory, slot, new PC_Vec2I(9, 3));
	}

	@Override
	public int getSizeInventory() {
		return this.is.length;
	}

	@Override
	public ItemStack getStackInSlot(int var1) {
		return this.is[var1];
	}

	@Override
	public ItemStack decrStackSize(int var1, int var2) {
		if (this.is[var1] != null) {
			if (this.is[var1].stackSize <= var2) {
				ItemStack itemstack = this.is[var1];
				this.is[var1] = null;
				markDirty();
				return itemstack;
			}
			ItemStack itemstack1 = splitStack(this.is[var1], var2);
			if (this.is[var1].stackSize == 0) {
				this.is[var1] = null;
			}
			markDirty();
			return itemstack1;
		} 
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int var1) {
		return this.is[var1];
	}

	@Override
	public void setInventorySlotContents(int var1, ItemStack var2) {
		this.is[var1] = var2;
	}

	@Override
	public void closeInventory() {
		PC_InventoryUtils.saveInventoryToNBT(this, getItemStack().getTagCompound(), "inv"); 
	}

	public static ItemStack splitStack(ItemStack itemStack, int par1){
        ItemStack var2 = new ItemStack(itemStack.getItem(), par1, itemStack.getItemDamage());

        if (itemStack.stackTagCompound != null){
            var2.stackTagCompound = (NBTTagCompound)itemStack.stackTagCompound.copy();
        }

        itemStack.stackSize -= par1;
        return var2;
    }
	
}
