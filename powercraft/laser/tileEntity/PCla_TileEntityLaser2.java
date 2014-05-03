package powercraft.laser.tileEntity;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import powercraft.api.PC_MathHelper;
import powercraft.api.PC_Utils;
import powercraft.api.PC_Vec3;
import powercraft.api.block.PC_TileEntity;
import powercraft.laser.PCla_Beam;
import powercraft.laser.PCla_IBeamHandler;

public class PCla_TileEntityLaser2 extends PC_TileEntity implements PCla_IBeamHandler {

	private float rot;
	
	@SuppressWarnings("unused")
	@Override
	public void onTick() {
		super.onTick();
		this.rot+=0.1;
		PC_Vec3 dir = new PC_Vec3(PC_MathHelper.cos((float) (this.rot/180.0*Math.PI)), 0, PC_MathHelper.sin((float) (this.rot/180.0*Math.PI)));
		new PCla_Beam(this.worldObj, this, new PC_Vec3(this.xCoord+0.5, this.yCoord+0.5, this.zCoord+0.5), dir, new PC_Vec3(1, 0, 0));
	}

	@Override
	public boolean onHitBlock(World world, int x, int y, int z, PCla_Beam beam) {
		Block block = PC_Utils.getBlock(world, x, y, z);
		return block==null||!block.isNormalCube();
	}

	@Override
	public boolean onHitEntity(World world, Entity entity, PCla_Beam beam) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public PC_Vec3 onRecolor(PC_Vec3 newColor, PCla_Beam beam) {
		return newColor;
	}
	
}
