package powercraft.itemstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.itemstorage.tileentity.PCis_TileEntityChannelChest;

public class PCis_ContainerChannelChest extends PC_GresBaseWithInventory {
	
	public PCis_ContainerChannelChest(EntityPlayer player, PCis_TileEntityChannelChest channelChest) {
		super(player, channelChest);
	}
	
}
