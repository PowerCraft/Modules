package powercraft.oldlaser;

import java.util.Vector;

import powercraft.api.PC_Vec3I;
import powercraft.oldlaser.tileentity.PCla_TileEntityLaser;

public class PCla_LaserBeamCalculator {

	PCla_TileEntityLaser laserObj;
	PCla_LaserTypeCalculator calculator;
	private int maxLaserLength = 15;
	public Vector<PC_Vec3I> validLaserPos = new Vector<PC_Vec3I>(this.maxLaserLength);
	public PC_Vec3I targetingBlock;
	public PC_Vec3I lastBeamBlock;

	public PCla_LaserBeamCalculator(PCla_TileEntityLaser laser, PCla_LaserTypeCalculator calc) {
		this.laserObj = laser;
		this.calculator = calc;
		this.targetingBlock = new PC_Vec3I(0, 0, 0);
		this.lastBeamBlock = new PC_Vec3I(0, 0, 0);
	}

	public void calculate() {
		if (this.validLaserPos.size() > 0) {
			this.lastBeamBlock = this.validLaserPos.lastElement();
		}
		this.validLaserPos.clear();
		switch (this.laserObj.orientation) {
		case WEST:
			for (int xPos = this.laserObj.xCoord + 1; xPos < this.laserObj.xCoord + this.maxLaserLength; xPos++) {
				if (PCla_LaserTypeCalculator.canLaserThrough(this.laserObj.getWorldObj(), xPos, this.laserObj.yCoord, this.laserObj.zCoord, this.laserObj
						.getWorldObj().getBlock(xPos, this.laserObj.yCoord, this.laserObj.zCoord))) {
					this.validLaserPos.add(new PC_Vec3I(xPos, this.laserObj.yCoord, this.laserObj.zCoord));
				} else {
					this.targetingBlock = new PC_Vec3I(xPos, this.laserObj.yCoord, this.laserObj.zCoord);
					return;
				}
			}
			break;
		case SOUTH:
			for (int zPos = this.laserObj.zCoord - 1; zPos > this.laserObj.zCoord - this.maxLaserLength; zPos--) {
				if (PCla_LaserTypeCalculator.canLaserThrough(this.laserObj.getWorldObj(), this.laserObj.xCoord, this.laserObj.yCoord, zPos, this.laserObj
						.getWorldObj().getBlock(this.laserObj.xCoord, this.laserObj.yCoord, zPos))) {
					this.validLaserPos.add(new PC_Vec3I(this.laserObj.xCoord, this.laserObj.yCoord, zPos));
				} else {
					this.targetingBlock = new PC_Vec3I(this.laserObj.xCoord, this.laserObj.yCoord, zPos);
					return;
				}
			}
			break;
		case NORTH:
			for (int zPos = this.laserObj.zCoord + 1; zPos < this.laserObj.zCoord + this.maxLaserLength; zPos++) {
				if (PCla_LaserTypeCalculator.canLaserThrough(this.laserObj.getWorldObj(), this.laserObj.xCoord, this.laserObj.yCoord, zPos, this.laserObj
						.getWorldObj().getBlock(this.laserObj.xCoord, this.laserObj.yCoord, zPos))) {
					this.validLaserPos.add(new PC_Vec3I(this.laserObj.xCoord, this.laserObj.yCoord, zPos));
				} else {
					this.targetingBlock = new PC_Vec3I(this.laserObj.xCoord, this.laserObj.yCoord, zPos);
					return;
				}
			}
			break;
		case EAST:
			for (int xPos = this.laserObj.xCoord - 1; xPos > this.laserObj.xCoord - this.maxLaserLength; xPos--) {
				if (PCla_LaserTypeCalculator.canLaserThrough(this.laserObj.getWorldObj(), xPos, this.laserObj.yCoord, this.laserObj.zCoord, this.laserObj
						.getWorldObj().getBlock(xPos, this.laserObj.yCoord, this.laserObj.zCoord))) {
					this.validLaserPos.add(new PC_Vec3I(xPos, this.laserObj.yCoord, this.laserObj.zCoord));
				} else {
					this.targetingBlock = new PC_Vec3I(xPos, this.laserObj.yCoord, this.laserObj.zCoord);
					return;
				}
			}
			break;
		default:
			break;
		}
	}
}
