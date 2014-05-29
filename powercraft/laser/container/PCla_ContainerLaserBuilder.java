package powercraft.laser.container;

import net.minecraft.entity.player.EntityPlayer;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.laser.tileEntity.PCla_TileEntityLaserBuilder;


public class PCla_ContainerLaserBuilder extends PC_GresBaseWithInventory {
	
	protected PCla_TileEntityLaserBuilder builder;

	public PCla_ContainerLaserBuilder(EntityPlayer player, PCla_TileEntityLaserBuilder builder) {
		super(player, builder);
		this.builder = builder;

	}
	
}
