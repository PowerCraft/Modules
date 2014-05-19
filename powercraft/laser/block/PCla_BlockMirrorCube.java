package powercraft.laser.block;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.World;
import powercraft.api.PC_3DRotation;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Vec3;
import powercraft.api.beam.PC_BeamHitResult;
import powercraft.api.beam.PC_IBeam;
import powercraft.api.block.PC_Block;
import powercraft.api.block.PC_BlockType;


public class PCla_BlockMirrorCube extends PC_Block {

	public PCla_BlockMirrorCube() {
		super(PC_BlockType.MACHINE);
		setCreativeTab(CreativeTabs.tabBlock);
	}

	@Override
	public PC_BeamHitResult onHitByBeam(World world, int x, int y, int z, PC_IBeam beam) {
		PC_3DRotation rot = getRotation(world, x, y, z);
		PC_Direction dir = rot.getSidePosition(PC_Direction.NORTH);
		PC_Vec3 newDir = new PC_Vec3(dir.offsetX, dir.offsetY, dir.offsetZ);
		beam.getNewBeam(-1, new PC_Vec3(x+0.5, y+0.5, z+0.5), newDir, null);
		return PC_BeamHitResult.STOP;
	}

	@Override
	public boolean canRotate() {
		return true;
	}
	
}
