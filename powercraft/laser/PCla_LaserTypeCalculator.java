package powercraft.laser;

import java.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Utils;
import powercraft.api.PC_Vec3I;
import powercraft.api.PC_Vec4I;
import powercraft.laser.item.PCla_ItemCatalysator;
import powercraft.laser.item.PCla_ItemLaserEmitter;
import powercraft.laser.item.PCla_ItemLens;
import powercraft.laser.tileEntity.PCla_TileEntityLaser;

public class PCla_LaserTypeCalculator {

	private PCla_TileEntityLaser laserObj;
	private ItemStack[] lens = new ItemStack[4];
	private ItemStack[] catalysator1 = new ItemStack[4];
	private ItemStack[] catalysator2 = new ItemStack[4];
	private ItemStack[] laserEmitter = new ItemStack[4];
	private ItemStack[] upgrades = new ItemStack[5];
	public PCla_EnumLaserEffects[] effects = new PCla_EnumLaserEffects[4];
	public PCla_EnumLaserTargets[] targets = new PCla_EnumLaserTargets[8];
	public Vector<PC_Vec3I> validLaserPos = new Vector<PC_Vec3I>(15);
	private int maxLaserLength = 15;
	private PC_Vec4I currColor = new PC_Vec4I(255, 255, 255, 255);

	public PCla_LaserTypeCalculator(PCla_TileEntityLaser laser) {
		laserObj = laser;
	}

	public boolean hasEffect(PCla_EnumLaserEffects effect) {
		for (PCla_EnumLaserEffects ef : effects) {
			if (ef.equals(effect))
				return true;
		}
		return false;
	}

	public boolean hasSomeEffects(PCla_EnumLaserEffects... effectsToLook) {
		for (PCla_EnumLaserEffects ef : effectsToLook) {
			if (this.hasEffect(ef))
				return true;
		}
		return false;
	}

	public boolean hasAllEffects(PCla_EnumLaserEffects... effectsToLook) {
		boolean retVal = false;
		for (PCla_EnumLaserEffects ef : effectsToLook) {
			if (this.hasEffect(ef))
				retVal = true;
			else
				return false;
		}
		return retVal;
	}

	public boolean hasTarget(PCla_EnumLaserTargets target) {
		for (PCla_EnumLaserTargets tg : targets) {
			if (tg.equals(target))
				return true;
		}
		return false;
	}

	public boolean hasSomeTargets(PCla_EnumLaserTargets... targetsToLook) {
		for (PCla_EnumLaserTargets tg : targetsToLook) {
			if (this.hasTarget(tg))
				return true;
		}
		return false;
	}

	public boolean hasAllTargets(PCla_EnumLaserTargets... targetsToLook) {
		boolean retVal = false;
		for (PCla_EnumLaserTargets tg : targetsToLook) {
			if (this.hasTarget(tg))
				retVal = true;
			else
				return false;
		}
		return retVal;
	}

	public void performItemUpdate() {
		ItemStack[] contents = laserObj.getInvContents();
		PC_Utils.setArrayContentsToNull(lens);
		PC_Utils.setArrayContentsToNull(laserEmitter);
		PC_Utils.setArrayContentsToNull(catalysator1);
		PC_Utils.setArrayContentsToNull(catalysator2);
		PC_Utils.setArrayContentsToNull(upgrades);
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
		//Color
		PC_Vec4I[] colors = new PC_Vec4I[4];
		for (int i = 0; i < lens.length; i++) {
			if (lens[i] == null) {
				colors[i] = null;
			} else {
				ItemStack is = lens[i];
				colors[i] = ((PCla_ItemLens) is.getItem()).getColorFromMeta(is.getItemDamage());
			}
		}
		currColor = null;
		currColor = PC_Utils.averageVec4I(colors);
		//Effects
		for (int i = 0; i < laserEmitter.length; i++) {
			if (laserEmitter[i] != null) {
				ItemStack is = laserEmitter[i];
				if (((PCla_ItemLaserEmitter) is.getItem()).getEffect(is.getItemDamage()) != PCla_EnumLaserEffects.NOTHING) {
					effects[i] = ((PCla_ItemLaserEmitter) is.getItem()).getEffect(is.getItemDamage());
				}
			}
			if (effects[i] == null) {
				effects[i] = PCla_EnumLaserEffects.NOTHING;
			}
		}
		//Targets
		for (int i = 0; i < catalysator1.length; i++) {
			if (catalysator1[i] != null) {
				ItemStack is = catalysator1[i];
				if (((PCla_ItemCatalysator) is.getItem()).getTaget(is.getItemDamage()) != PCla_EnumLaserTargets.NOTHING) {
					targets[i] = ((PCla_ItemCatalysator) is.getItem()).getTaget(is.getItemDamage());
				}
			}
			if (targets[i] == null) {
				targets[i] = PCla_EnumLaserTargets.NOTHING;
			}
		}
		for (int i = 0; i < catalysator2.length; i++) {
			if (catalysator1[i] != null) {
				ItemStack is = catalysator1[i];
				if (((PCla_ItemCatalysator) is.getItem()).getTaget(is.getItemDamage()) != PCla_EnumLaserTargets.NOTHING) {
					targets[i + 4] = ((PCla_ItemCatalysator) is.getItem()).getTaget(is.getItemDamage());
				}
			}
			if (targets[i + 4] == null) {
				targets[i + 4] = PCla_EnumLaserTargets.NOTHING;
			}
		}
	}

	public void performBlockUpdate(PC_Direction orientation) {
		validLaserPos.clear();
		switch (orientation) {
		case WEST:
			for (int xPos = laserObj.xCoord + 1; xPos < laserObj.xCoord + maxLaserLength; xPos++)
				if (canLaserThrough(laserObj.getWorldObj(), xPos, laserObj.yCoord, laserObj.zCoord, laserObj
						.getWorldObj().getBlock(xPos, laserObj.yCoord, laserObj.zCoord)))
					validLaserPos.add(new PC_Vec3I(xPos, laserObj.yCoord, laserObj.zCoord));
				else
					return;
			break;
		case SOUTH:
			for (int zPos = laserObj.zCoord - 1; zPos > laserObj.zCoord - maxLaserLength; zPos--)
				if (canLaserThrough(laserObj.getWorldObj(), laserObj.xCoord, laserObj.yCoord, zPos, laserObj
						.getWorldObj().getBlock(laserObj.xCoord, laserObj.yCoord, zPos)))
					validLaserPos.add(new PC_Vec3I(laserObj.xCoord, laserObj.yCoord, zPos));
				else
					return;
			break;
		case NORTH:
			for (int zPos = laserObj.zCoord + 1; zPos < laserObj.zCoord + maxLaserLength; zPos++)
				if (canLaserThrough(laserObj.getWorldObj(), laserObj.xCoord, laserObj.yCoord, zPos, laserObj
						.getWorldObj().getBlock(laserObj.xCoord, laserObj.yCoord, zPos)))
					validLaserPos.add(new PC_Vec3I(laserObj.xCoord, laserObj.yCoord, zPos));
				else
					return;
			break;
		case EAST:
			for (int xPos = laserObj.xCoord - 1; xPos > laserObj.xCoord - maxLaserLength; xPos--)
				if (canLaserThrough(laserObj.getWorldObj(), xPos, laserObj.yCoord, laserObj.zCoord, laserObj
						.getWorldObj().getBlock(xPos, laserObj.yCoord, laserObj.zCoord)))
					validLaserPos.add(new PC_Vec3I(xPos, laserObj.yCoord, laserObj.zCoord));
				else
					return;
			break;
		default:
			break;
		}
	}

	public PC_Vec4I getCurrColor() {
		return currColor;
	}

	public boolean canLaserThrough(IBlockAccess world, int x, int y, int z, Block block) {
		if (block.isAir(world, x, y, z))
			return true;
		if (block.getMaterial().equals(Material.carpet) || block.getMaterial().equals(Material.circuits)
				|| block.getMaterial().equals(Material.glass) || block.getMaterial().equals(Material.dragonEgg)
				|| block.getMaterial().equals(Material.fire) || block.getMaterial().equals(Material.ice)
				|| block.getMaterial().equals(Material.plants) || block.getMaterial().equals(Material.vine)
				|| block.getMaterial().equals(Material.water) || block.getMaterial().equals(Material.web))
			return true;
		return false;
	}
}
