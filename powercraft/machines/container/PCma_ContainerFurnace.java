package powercraft.machines.container;

import net.minecraft.entity.player.EntityPlayer;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.machines.tileentity.PCma_TileEntityFurnace;

public class PCma_ContainerFurnace extends PC_GresBaseWithInventory {

	protected PCma_TileEntityFurnace furnace;
	
	public PCma_ContainerFurnace(EntityPlayer player, PCma_TileEntityFurnace furnace) {
		super(player, furnace);
		this.furnace = furnace;
	}

}
