package powercraft.laser;

import java.util.Vector;
import powercraft.api.PC_Vec3I;
import powercraft.laser.tileEntity.PCla_TileEntityLaser;

public class PCla_LaserBeamCalculator {

	PCla_TileEntityLaser laserObj;
	PCla_LaserTypeCalculator calculator;
	private int maxLaserLength = 15;
	public Vector<PC_Vec3I> validLaserPos = new Vector<PC_Vec3I>(maxLaserLength);
	public PC_Vec3I targetingBlock;
	public PC_Vec3I lastBeamBlock;

	public PCla_LaserBeamCalculator(PCla_TileEntityLaser laser, PCla_LaserTypeCalculator calc) {
		laserObj = laser;
		calculator = calc;
		targetingBlock = new PC_Vec3I(0, 0, 0);
		lastBeamBlock = new PC_Vec3I(0, 0, 0);
	}

	public void calculate() {
		if (validLaserPos.size() > 0) {
			lastBeamBlock = validLaserPos.lastElement();
		}
		validLaserPos.clear();
		switch (laserObj.orientation) {
		case WEST:
			for (int xPos = laserObj.xCoord + 1; xPos < laserObj.xCoord + maxLaserLength; xPos++) {
				if (calculator.canLaserThrough(laserObj.getWorldObj(), xPos, laserObj.yCoord, laserObj.zCoord, laserObj
						.getWorldObj().getBlock(xPos, laserObj.yCoord, laserObj.zCoord))) {
					validLaserPos.add(new PC_Vec3I(xPos, laserObj.yCoord, laserObj.zCoord));
				} else {
					targetingBlock = new PC_Vec3I(xPos, laserObj.yCoord, laserObj.zCoord);
					return;
				}
			}
			break;
		case SOUTH:
			for (int zPos = laserObj.zCoord - 1; zPos > laserObj.zCoord - maxLaserLength; zPos--) {
				if (calculator.canLaserThrough(laserObj.getWorldObj(), laserObj.xCoord, laserObj.yCoord, zPos, laserObj
						.getWorldObj().getBlock(laserObj.xCoord, laserObj.yCoord, zPos))) {
					validLaserPos.add(new PC_Vec3I(laserObj.xCoord, laserObj.yCoord, zPos));
				} else {
					targetingBlock = new PC_Vec3I(laserObj.xCoord, laserObj.yCoord, zPos);
					return;
				}
			}
			break;
		case NORTH:
			for (int zPos = laserObj.zCoord + 1; zPos < laserObj.zCoord + maxLaserLength; zPos++) {
				if (calculator.canLaserThrough(laserObj.getWorldObj(), laserObj.xCoord, laserObj.yCoord, zPos, laserObj
						.getWorldObj().getBlock(laserObj.xCoord, laserObj.yCoord, zPos))) {
					validLaserPos.add(new PC_Vec3I(laserObj.xCoord, laserObj.yCoord, zPos));
				} else {
					targetingBlock = new PC_Vec3I(laserObj.xCoord, laserObj.yCoord, zPos);
					return;
				}
			}
			break;
		case EAST:
			for (int xPos = laserObj.xCoord - 1; xPos > laserObj.xCoord - maxLaserLength; xPos--) {
				if (calculator.canLaserThrough(laserObj.getWorldObj(), xPos, laserObj.yCoord, laserObj.zCoord, laserObj
						.getWorldObj().getBlock(xPos, laserObj.yCoord, laserObj.zCoord))) {
					validLaserPos.add(new PC_Vec3I(xPos, laserObj.yCoord, laserObj.zCoord));
				} else {
					targetingBlock = new PC_Vec3I(xPos, laserObj.yCoord, laserObj.zCoord);
					return;
				}
			}
			break;
		default:
			break;
		}
	}
}
