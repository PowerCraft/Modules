package powercraft.laser.tileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Field;
import powercraft.api.PC_Field.Flag;
import powercraft.api.PC_Utils;
import powercraft.api.PC_Vec3;
import powercraft.api.beam.PC_BeamHitResult;
import powercraft.api.beam.PC_IBeam;
import powercraft.api.block.PC_TileEntity;


public class PCla_TileEntityMirror extends PC_TileEntity {

	@PC_Field(flags={Flag.SAVE, Flag.SYNC})
	protected PC_Vec3 normal;
	
	@Override
	public void onBlockPostSet(PC_Direction side, ItemStack stack, EntityPlayer player, float hitX, float hitY, float hitZ) {
		normal = PC_Utils.getLookDir(player);
		sync();
	}

	@Override
	public PC_BeamHitResult onHitByBeam(PC_IBeam beam) {
		if(normal!=null){
			PC_Vec3 dir = beam.getDirection();
			PC_Vec3 result = dir.sub(normal.mul(dir.dot(normal)*2));
			beam.getNewBeam(-1, new PC_Vec3(xCoord+0.5, yCoord+0.5, zCoord+0.5), result, null);
		}
		return PC_BeamHitResult.STOP;
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, PC_Direction side) {
		normal = PC_Utils.getLookDir(player);
		sync();
		return true;
	}
	
}
