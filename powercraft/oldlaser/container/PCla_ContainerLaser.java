package powercraft.oldlaser.container;

import net.minecraft.entity.player.EntityPlayer;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.oldlaser.tileentity.PCla_TileEntityLaser;

public class PCla_ContainerLaser extends PC_GresBaseWithInventory {

	protected PCla_TileEntityLaser laser;

	public PCla_ContainerLaser(EntityPlayer player, PCla_TileEntityLaser laser) {
		super(player, laser);
		this.laser = laser;

	}

}
