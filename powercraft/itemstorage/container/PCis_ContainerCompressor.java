package powercraft.itemstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import powercraft.api.block.PC_TileEntity;
import powercraft.api.entity.PC_Entity;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.network.PC_PacketHandler;
import powercraft.itemstorage.packet.PCis_PacketSetSlot;
import powercraft.itemstorage.packet.PCis_PacketWindowItems;

public class PCis_ContainerCompressor extends PC_GresBaseWithInventory {
	
	protected ItemStack itemStack;
	
	private int slot;
	
	public PCis_ContainerCompressor(EntityPlayer player, ItemStack itemStack, int slot, IInventory inv) {
		super(player, inv);
		this.itemStack = itemStack;
		this.slot = slot;
	}

	@Override
	public boolean canTakeStack(int i, EntityPlayer entityPlayer) {
		return i!=this.slot && super.canTakeStack(i, entityPlayer);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void addCraftingToCrafters(ICrafting crafting) {

		if(crafting instanceof EntityPlayerMP){
			if (this.crafters.contains(crafting)){
	            throw new IllegalArgumentException("Listener already listening");
	        }
			this.crafters.add(crafting);
			PC_PacketHandler.sendTo(new PCis_PacketWindowItems(this.windowId, getInventory()), (EntityPlayerMP)crafting);
			PC_PacketHandler.sendTo(new PCis_PacketSetSlot(-1, -1, ((EntityPlayerMP)crafting).inventory.getItemStack()), (EntityPlayerMP)crafting);
			crafting.sendContainerAndContentsToPlayer(this, this.getInventory());
			this.detectAndSendChanges();
			if (this.inventory instanceof PC_TileEntity) {
				((PC_TileEntity) this.inventory).sendProgressBarUpdates();
			}
			if (this.inventory instanceof PC_Entity) {
				((PC_Entity) this.inventory).sendProgressBarUpdates();
			}
		}else{
			super.addCraftingToCrafters(crafting);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void detectAndSendChanges(){
        for (int i = 0; i < this.inventorySlots.size(); ++i){
            ItemStack itemstack = ((Slot)this.inventorySlots.get(i)).getStack();
            ItemStack itemstack1 = (ItemStack)this.inventoryItemStacks.get(i);

            if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)){
                itemstack1 = itemstack == null ? null : itemstack.copy();
                this.inventoryItemStacks.set(i, itemstack1);

                for (int j = 0; j < this.crafters.size(); ++j){
                	sendSlotContentsTo((ICrafting) this.crafters.get(j), i, itemstack1);
                }
            }
        }
    }
	
	private void sendSlotContentsTo(ICrafting crafting, int i, ItemStack itemstack){
		if(crafting instanceof EntityPlayerMP){
			if (!((EntityPlayerMP)crafting).isChangingQuantityOnly){
				PC_PacketHandler.sendTo(new PCis_PacketSetSlot(this.windowId, i, itemstack), (EntityPlayerMP)crafting);
            }
		}else{
			crafting.sendSlotContents(this, i, itemstack);
		}
	}
	
}
