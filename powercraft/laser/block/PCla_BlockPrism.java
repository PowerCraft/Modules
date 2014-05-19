package powercraft.laser.block;

import net.minecraft.world.World;
import powercraft.api.PC_Vec3;
import powercraft.api.beam.PC_BeamHitResult;
import powercraft.api.beam.PC_IBeam;
import powercraft.api.block.PC_Block;
import powercraft.api.block.PC_BlockType;


public class PCla_BlockPrism extends PC_Block {

	public PCla_BlockPrism() {
		super(PC_BlockType.MACHINE);
		// TODO Auto-generated constructor stub
	}

	@Override
	public PC_BeamHitResult onHitByBeam(World world, int x, int y, int z, PC_IBeam beam) {
		PC_Vec3 newDir = new PC_Vec3();
		beam.getNewBeam(-1, null, newDir, null);
		return PC_BeamHitResult.STOP;
	}
	
}
