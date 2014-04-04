package powercraft.itemstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import powercraft.api.gres.PC_GresBaseWithInventory;

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
	
	
	
}
