package powercraft.laser.tileEntity;

import java.util.Vector;

import powercraft.api.PC_Direction;
import powercraft.api.PC_Vec3I;
import powercraft.api.block.PC_TileEntity;

public class PCla_TileEntityLaser extends PC_TileEntity {

	public Vector<PC_Vec3I> validLaserPos = new Vector<PC_Vec3I>(20);// Please use Java 1.6!!
	public PC_Direction orientation;
	public int maxLaserLength = 15;

	public PCla_TileEntityLaser() {
		orientation = PC_Direction.SOUTH;
	}

	@Override
	public void onTick() {
		updateBlockList();
	}

	public void updateBlockList() {
		validLaserPos.clear();
		switch (orientation) {
		case EAST:
			for (int xPos = xCoord + 1; xPos < xCoord + maxLaserLength; xPos++)
				if (worldObj.getBlock(xPos, yCoord, zCoord).isAir(worldObj, xPos, yCoord, zCoord))
					validLaserPos.add(new PC_Vec3I(xPos, yCoord, zCoord));
				else
					return;
			break;
		case NORTH:
			for (int zPos = zCoord - 1; zPos > zCoord - maxLaserLength; zPos--)
				if (worldObj.getBlock(xCoord, yCoord, zPos).isAir(worldObj, xCoord, yCoord, zPos))
					validLaserPos.add(new PC_Vec3I(xCoord, yCoord, zPos));
				else
					return;
			break;
		case SOUTH:
			for (int zPos = zCoord + 1; zPos < zCoord + maxLaserLength; zPos++)
				if (worldObj.getBlock(xCoord, yCoord, zPos).isAir(worldObj, xCoord, yCoord, zPos))
					validLaserPos.add(new PC_Vec3I(xCoord, yCoord, zPos));
				else
					return;
			break;
		case WEST:
			for (int xPos = xCoord - 1; xPos > xCoord - maxLaserLength; xPos--)
				if (worldObj.getBlock(xPos, yCoord, zCoord).isAir(worldObj, xPos, yCoord, zCoord))
					validLaserPos.add(new PC_Vec3I(xPos, yCoord, zCoord));
				else
					return;
			break;
		default:
			break;
		}
	}
}
