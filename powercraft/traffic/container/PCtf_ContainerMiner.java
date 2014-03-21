package powercraft.traffic.container;

import net.minecraft.entity.player.EntityPlayer;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.traffic.entity.PCtf_EntityMiner;


public class PCtf_ContainerMiner extends PC_GresBaseWithInventory {

	protected PCtf_EntityMiner miner;
	
	public PCtf_ContainerMiner(EntityPlayer player, PCtf_EntityMiner miner) {
		super(player, miner);
		this.miner = miner;
	}
	
}
