package powercraft.oldlaser.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import powercraft.api.PC_Direction;
import powercraft.api.block.PC_TileEntityWithInventory;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.PC_IGresGuiOpenHandler;
import powercraft.api.inventory.PC_InventoryUtils;
import powercraft.api.redstone.PC_RedstoneWorkType;
import powercraft.oldlaser.PCla_LaserTypeCalculator;
import powercraft.oldlaser.container.PCla_ContainerLaser;
import powercraft.oldlaser.gui.PCla_GuiLaser;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PCla_TileEntityLaser extends PC_TileEntityWithInventory implements PC_IGresGuiOpenHandler {

	public PC_Direction orientation;
	private int counterForTick = 0;
	private boolean[] laserSlotsEnabled = new boolean[] { true, false, false, false };
	private boolean[] upgradeSlotsEnabled = new boolean[] { true, false, false, false, false };
	public PCla_LaserTypeCalculator calculator = new PCla_LaserTypeCalculator(this);

	public PCla_TileEntityLaser() {
		super("Laser", 21, new Group(true, PC_InventoryUtils.makeIndexList(0, 4)), new Group(true,
				PC_InventoryUtils.makeIndexList(4, 12)), new Group(true, PC_InventoryUtils.makeIndexList(12, 16)),
				new Group(true, PC_InventoryUtils.makeIndexList(16, 21)));
		this.orientation = PC_Direction.NORTH;
		this.workWhen = PC_RedstoneWorkType.ALWAYS;
		markDirty();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);
		markDirty();
	}

	@Override
	public void onTick() {
		if (this.counterForTick >= 20) {
			this.counterForTick = 0;
			this.calculator.performBlockUpdate(this.orientation);
			this.calculator.performUpdateTick();
			updateDisabledSlots();
			renderUpdate();
		}
		this.counterForTick++;
	}

	@Override
	public void markDirty() {
		super.markDirty();
		this.calculator.performItemUpdate();
		updateDisabledSlots();
		renderUpdate();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public PC_IGresGui openClientGui(EntityPlayer player, NBTTagCompound serverData) {
		return new PCla_GuiLaser(player, this);
	}

	@Override
	public PC_GresBaseWithInventory openServerGui(EntityPlayer player, Object[] params) {
		return new PCla_ContainerLaser(player, this);
	}

	@Override
	public NBTTagCompound sendOnGuiOpenToClient(EntityPlayer player, Object[] params) {
		return null;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		//enabled?
		if (i < 16 && !this.laserSlotsEnabled[i % 4])
			return false;
		if (i >= 16 && i < 21 && !this.upgradeSlotsEnabled[i - 16])
			return false;

		//correct Item?
		/*if (itemstack.getItem() == PCla_Laser.lens) {
			if (i >= 0 && i < 4)
				return true;
		}
		if (itemstack.getItem() == PCla_Laser.catalysator) {
			if (i >= 4 && i < 12)
				return true;
		}
		if (itemstack.getItem() == PCla_Laser.laserEmitter) {
			if (i >= 12 && i < 16)
				return true;
		}
		if (itemstack.getItem() == PCla_Laser.laserUpgrade) {
			if (i >= 16 && i < 21)
				return true;
		}*/
		return false;
	}

	public void updateDisabledSlots() {
		ItemStack[] upgrades = new ItemStack[5];
		for (int i = 0; i < 5; i++) {
			upgrades[i] = this.inventoryContents[i + 16];
		}
		int newNumUp = 1;
		int newNumLas = 1;
		//for (ItemStack currUp : upgrades) {
			//if (currUp != null)
				/*if (currUp.getItem() == PCla_Laser.laserUpgrade) {
					newNumLas += ((PCla_ItemLaserUpgrade) currUp.getItem()).getAddedNumLaserThings(currUp
							.getItemDamage());
					newNumUp += ((PCla_ItemLaserUpgrade) currUp.getItem()).getAddedNumUpgrades(currUp.getItemDamage());
				}*/
		//}
		if (newNumUp > 5) {
			newNumUp = 5;
		}
		if (newNumLas > 4) {
			newNumLas = 4;
		}
		for (int i = 0; i < 5; i++) {
			if (newNumUp - 1 >= i) {
				this.upgradeSlotsEnabled[i] = true;
			} else {
				this.upgradeSlotsEnabled[i] = false;
			}
		}
		for (int i = 0; i < 4; i++) {
			if (newNumLas - 1 >= i) {
				this.laserSlotsEnabled[i] = true;
			} else {
				this.laserSlotsEnabled[i] = false;
			}
		}
	}

	@Override
	public PC_RedstoneWorkType[] getAllowedRedstoneWorkTypes() {
		return new PC_RedstoneWorkType[] { PC_RedstoneWorkType.ALWAYS, PC_RedstoneWorkType.ON_ON,
				PC_RedstoneWorkType.ON_OFF, PC_RedstoneWorkType.ON_FLANK, PC_RedstoneWorkType.ON_HI_FLANK,
				PC_RedstoneWorkType.ON_LOW_FLANK };
	}

	public ItemStack[] getInvContents() {
		return this.inventoryContents;
	}

	@Override
	public World getWorldObj() {
		return this.worldObj;
	}
}
