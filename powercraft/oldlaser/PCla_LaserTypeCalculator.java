package powercraft.oldlaser;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Utils;
import powercraft.api.PC_Vec3I;
import powercraft.api.PC_Vec4I;
import powercraft.oldlaser.item.PCla_ItemCatalysator;
import powercraft.oldlaser.item.PCla_ItemLaserEmitter;
import powercraft.oldlaser.item.PCla_ItemLens;
import powercraft.oldlaser.tileentity.PCla_TileEntityLaser;

public class PCla_LaserTypeCalculator {

	private PCla_TileEntityLaser laserObj;
	public PCla_LaserBeamCalculator beamCalc;
	private ItemStack[] lens = new ItemStack[4];
	private ItemStack[] catalysator1 = new ItemStack[4];
	private ItemStack[] catalysator2 = new ItemStack[4];
	private ItemStack[] laserEmitter = new ItemStack[4];
	private ItemStack[] upgrades = new ItemStack[5];
	public PCla_EnumLaserEffects[] effects = new PCla_EnumLaserEffects[4];
	public PCla_EnumLaserTargets[] targets = new PCla_EnumLaserTargets[8];
	private PC_Vec4I currColor = new PC_Vec4I(255, 255, 255, 255);
	private int percentMined = 0;
	private int percentMiningPerTick = 20;
	//private IInventory chestBehind;

	public PCla_LaserTypeCalculator(PCla_TileEntityLaser laser) {
		this.laserObj = laser;
		this.beamCalc = new PCla_LaserBeamCalculator(this.laserObj, this);
	}

	public boolean hasEffect(PCla_EnumLaserEffects effect) {
		for (PCla_EnumLaserEffects ef : this.effects) {
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
			if (this.hasEffect(ef)) {
				retVal = true;
			} else
				return false;
		}
		return retVal;
	}

	public boolean hasTarget(PCla_EnumLaserTargets target) {
		for (PCla_EnumLaserTargets tg : this.targets) {
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
			if (this.hasTarget(tg)) {
				retVal = true;
			} else
				return false;
		}
		return retVal;
	}

	public void performItemUpdate() {
		ItemStack[] contents = this.laserObj.getInvContents();
		PC_Utils.setArrayContentsToNull(this.lens);
		PC_Utils.setArrayContentsToNull(this.laserEmitter);
		PC_Utils.setArrayContentsToNull(this.catalysator1);
		PC_Utils.setArrayContentsToNull(this.catalysator2);
		PC_Utils.setArrayContentsToNull(this.upgrades);
		for (int i = 0; i < contents.length; i++) {
			//int resIndex = i % 4;
			if (contents[i] != null) {
				/*switch ((int) Math.floor(i / 4)) {
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
				}*/
			}
		}
		//Color
		PC_Vec4I[] colors = new PC_Vec4I[4];
		for (int i = 0; i < this.lens.length; i++) {
			if (this.lens[i] == null) {
				colors[i] = null;
			} else {
				ItemStack is = this.lens[i];
				colors[i] = PCla_ItemLens.getColorFromMeta(is.getItemDamage());
			}
		}
		this.currColor = null;
		this.currColor = PC_Utils.averageVec4I(colors);
		//Effects
		for (int i = 0; i < this.laserEmitter.length; i++) {
			if (this.laserEmitter[i] != null) {
				ItemStack is = this.laserEmitter[i];
				if (PCla_ItemLaserEmitter.getEffect(is.getItemDamage()) != PCla_EnumLaserEffects.NOTHING) {
					this.effects[i] = PCla_ItemLaserEmitter.getEffect(is.getItemDamage());
				}
			}
			if (this.effects[i] == null) {
				this.effects[i] = PCla_EnumLaserEffects.NOTHING;
			}
		}
		//Targets
		for (int i = 0; i < this.catalysator1.length; i++) {
			if (this.catalysator1[i] != null) {
				ItemStack is = this.catalysator1[i];
				if (PCla_ItemCatalysator.getTaget(is.getItemDamage()) != PCla_EnumLaserTargets.NOTHING) {
					this.targets[i] = PCla_ItemCatalysator.getTaget(is.getItemDamage());
				}
			}
			if (this.targets[i] == null) {
				this.targets[i] = PCla_EnumLaserTargets.NOTHING;
			}
		}
		for (int i = 0; i < this.catalysator2.length; i++) {
			if (this.catalysator1[i] != null) {
				ItemStack is = this.catalysator1[i];
				if (PCla_ItemCatalysator.getTaget(is.getItemDamage()) != PCla_EnumLaserTargets.NOTHING) {
					this.targets[i + 4] = PCla_ItemCatalysator.getTaget(is.getItemDamage());
				}
			}
			if (this.targets[i + 4] == null) {
				this.targets[i + 4] = PCla_EnumLaserTargets.NOTHING;
			}
		}
	}

	public void performBlockUpdate(PC_Direction orientation) {
		this.beamCalc.calculate();
		switch (this.laserObj.orientation) {
		case EAST:
			Block candidate = this.laserObj.getWorldObj().getBlock(this.laserObj.xCoord - 1, this.laserObj.yCoord, this.laserObj.zCoord);
			if (candidate instanceof IInventory) {
				System.out.println("valid");
			}
			break;
		case NORTH:
			break;
		case SOUTH:
			break;
		case WEST:
			break;
		default:
			break;
		}
	}

	public PC_Vec4I getCurrColor() {
		return this.currColor;
	}

	public static boolean canLaserThrough(IBlockAccess world, int x, int y, int z, Block block) {
		if (block.isAir(world, x, y, z))
			return true;
		if (block.getMaterial().equals(Material.carpet) || block.getMaterial().equals(Material.circuits)
				|| block.getMaterial().equals(Material.glass) || block.getMaterial().equals(Material.fire)
				|| block.getMaterial().equals(Material.fire) || block.getMaterial().equals(Material.ice)
				|| block.getMaterial().equals(Material.plants) || block.getMaterial().equals(Material.vine)
				|| block.getMaterial().equals(Material.water) || block.getMaterial().equals(Material.web))
			return true;
		return false;
	}

	public void performUpdateTick() {
		if (!this.laserObj.getWorldObj().isRemote) {
			if (hasEffect(PCla_EnumLaserEffects.BREAK)) {
				if (hasTarget(PCla_EnumLaserTargets.PLANT)) {
					doHarvesting();
				}
				if (hasTarget(PCla_EnumLaserTargets.BLOCK)) {
					mineLastBlock();
				}
			}
			if (hasAllEffects(PCla_EnumLaserEffects.BUILD)) {
				if (hasTarget(PCla_EnumLaserTargets.PLANT)) {
					doReplanting();
				}
				if (hasTarget(PCla_EnumLaserTargets.BLOCK)) {
					doBlockPlacing();
				}
			}
		}
	}

	private void doHarvesting() {
		//
	}

	private void doReplanting() {
		//
	}

	private void mineLastBlock() {
		if (this.percentMined < 100) {
			this.percentMined += this.percentMiningPerTick;
		} else {
			this.beamCalc.calculate();
			PC_Vec3I vecToDestroy = this.beamCalc.targetingBlock;
			if (vecToDestroy == null) {
				vecToDestroy = new PC_Vec3I(0, 0, 0);
			}
			this.laserObj.getWorldObj().setBlock(vecToDestroy.x, vecToDestroy.y, vecToDestroy.z, Blocks.air, 0, 3);
			this.percentMined = 0;
		}
	}

	private void doBlockPlacing() {
		//
	}

}
