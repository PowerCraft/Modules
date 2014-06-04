package powercraft.laser.tileEntity;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercraft.api.PC_3DRotationY;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Utils;
import powercraft.api.PC_Vec3;
import powercraft.api.beam.PC_LightValue;
import powercraft.api.block.PC_TileEntityRotateable;
import powercraft.laser.PCla_Beam;
import powercraft.laser.PCla_IBeamHandler;
import powercraft.laser.PCla_LaserRenderer;
import powercraft.laser.block.PCla_BlockLaserTractor;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PCla_TileEntityLaserTractor extends PC_TileEntityRotateable implements PCla_IBeamHandler {
	
	@SuppressWarnings("unused")
	@Override
	public void onTick() {
		super.onTick();
		if(get3DRotation()==null)
			return;
		PC_Direction dir = get3DRotation().getSidePosition(PC_Direction.NORTH);
		PC_Vec3 vec = new PC_Vec3(dir.offsetX, dir.offsetY, dir.offsetZ);
		new PCla_Beam(this.worldObj, this, 20, new PC_Vec3(this.xCoord+0.5, this.yCoord+0.5, this.zCoord+0.5).add(vec.mul(0.1)), vec, new PC_LightValue(650*PC_LightValue.THz, 1));
	}

	@Override
	public boolean onHitBlock(World world, int x, int y, int z, PCla_Beam beam) {
		Block block = PC_Utils.getBlock(world, x, y, z);
		return block==null||!block.isNormalCube();
	}

	@Override
	public boolean onHitEntity(World world, Entity entity, PCla_Beam beam) {
		PC_Vec3 dir = beam.getDirection();
		double strength = beam.getLightValue().getIntensity()/10;
		entity.motionX -= dir.x*strength;
		entity.motionY -= dir.y*strength;
		entity.motionZ -= dir.z*strength;
		return true;
	}

	@Override
	public void onFinished(PCla_Beam beam) {
		//
	}

	@Override
	public void onAdded(EntityPlayer player) {
		set3DRotation(new PC_3DRotationY(player));
		super.onAdded(player);
	}
	
	@Override
	public void onBlockPostSet(PC_Direction side, ItemStack stack, EntityPlayer player, float hitX, float hitY, float hitZ) {
		if(this.rotation==null)
			set3DRotation(new PC_3DRotationY(player));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean renderWorldBlock(int modelId, RenderBlocks renderer) {
		if(get3DRotation()==null)
			return true;
		
		PC_Direction dir = get3DRotation().getSidePosition(PC_Direction.NORTH);
		
		PCla_LaserRenderer.renderLaser(this.worldObj, this.xCoord, this.yCoord, this.zCoord, dir, renderer, PCla_BlockLaserTractor.side, PCla_BlockLaserTractor.inside, PCla_BlockLaserTractor.black, PCla_BlockLaserTractor.white);
		
		return true;
	}
	
}
