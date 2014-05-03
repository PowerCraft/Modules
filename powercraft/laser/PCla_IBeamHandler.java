package powercraft.laser;

import powercraft.api.PC_Vec3;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;


public interface PCla_IBeamHandler {

	public boolean onHitBlock(World world, int x, int y, int z, PCla_Beam beam);

	public boolean onHitEntity(World world, Entity entity, PCla_Beam beam);

	public PC_Vec3 onRecolor(PC_Vec3 newColor, PCla_Beam beam);
	
}
