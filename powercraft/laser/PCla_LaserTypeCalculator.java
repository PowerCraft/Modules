package powercraft.laser;

import net.minecraft.item.ItemStack;
import powercraft.laser.tileEntity.PCla_TileEntityLaser;

public class PCla_LaserTypeCalculator {

	PCla_TileEntityLaser laserObj;
	ItemStack[] lens = new ItemStack[4];
	ItemStack[] catalysator1 = new ItemStack[4];
	ItemStack[] catalysator2 = new ItemStack[4];
	ItemStack[] laserEmitter = new ItemStack[4];
	ItemStack[] upgrades = new ItemStack[5];

	public PCla_LaserTypeCalculator(PCla_TileEntityLaser laser) {
		laserObj = laser;
	}

	public void performUpdate() {
		ItemStack[] contents = laserObj.getInvContents();
		for (int i = 0; i < contents.length; i++) {
			int resIndex = i % 4;
			if (contents[i] != null) {
				switch ((int) Math.floor(i / 4)) {
				case 0:
					if (contents[i].getItem() == PCla_Laser.lens) {
						lens[resIndex] = contents[i];
					}
					break;
				case 1:
					if (contents[i].getItem() == PCla_Laser.catalysator) {
						catalysator1[resIndex] = contents[i];
					}
					break;
				case 2:
					if (contents[i].getItem() == PCla_Laser.catalysator) {
						catalysator2[resIndex] = contents[i];
					}
					break;
				case 3:
					if (contents[i].getItem() == PCla_Laser.laserEmitter) {
						laserEmitter[resIndex] = contents[i];
					}
					break;
				default:
					if (i >= 16 && i < 21) {
						upgrades[i - 4 * 4] = contents[i];
					}
					break;
				}
			}
		}
	}
}
