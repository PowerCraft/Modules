package powercraft.machines.container;

import net.minecraft.entity.player.EntityPlayer;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.gres.slot.PC_Slot;
import powercraft.api.gres.slot.PC_SlotPhantom;
import powercraft.machines.tileentity.PCma_TileEntityAutomaticWorkbench;

public class PCma_ContainerAutomaticWorkbench extends PC_GresBaseWithInventory {

	protected PCma_TileEntityAutomaticWorkbench automaticWorkbench;
	
	public PCma_ContainerAutomaticWorkbench(EntityPlayer player, PCma_TileEntityAutomaticWorkbench automaticWorkbench) {
		super(player, automaticWorkbench);
		this.automaticWorkbench = automaticWorkbench;
	}

	@Override
	protected PC_Slot createSlot(int i){
		if(i<9){
			return new PC_SlotPhantom(this.inventory, i);
		}
		return super.createSlot(i);
	}
	
}
