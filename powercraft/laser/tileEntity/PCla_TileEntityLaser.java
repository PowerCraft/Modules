package powercraft.laser.tileEntity;

import java.util.Vector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Utils;
import powercraft.api.PC_Vec3I;
import powercraft.api.PC_Vec4I;
import powercraft.api.block.PC_TileEntityWithInventory;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.PC_IGresGuiOpenHandler;
import powercraft.api.inventory.PC_InventoryUtils;
import powercraft.api.redstone.PC_RedstoneWorkType;
import powercraft.laser.container.PCla_ContainerLaser;
import powercraft.laser.gui.PCla_GuiLaser;
import powercraft.laser.item.PCla_ItemLens;

public class PCla_TileEntityLaser extends PC_TileEntityWithInventory implements PC_IGresGuiOpenHandler {

	public Vector<PC_Vec3I> validLaserPos = new Vector<PC_Vec3I>(15);
	public PC_Direction orientation;
	public PC_Vec4I currColor;
	public int maxLaserLength = 15;

	public PCla_TileEntityLaser() {
		super("Laser", 12, new Group(true, PC_InventoryUtils.makeIndexList(0, 4)), new Group(true,
				PC_InventoryUtils.makeIndexList(4, 8)), new Group(true, PC_InventoryUtils.makeIndexList(8, 12)));
		orientation = PC_Direction.NORTH;
		this.workWhen = PC_RedstoneWorkType.EVER;
		this.currColor = new PC_Vec4I(255, 255, 255, 255);

	}

	@Override
	public void onTick() {
		updateBlockList();
	}

	public void updateBlockList() {
		validLaserPos.clear();
		switch (orientation) {
		case WEST:
			for (int xPos = xCoord + 1; xPos < xCoord + maxLaserLength; xPos++)
				if (worldObj.getBlock(xPos, yCoord, zCoord).isAir(worldObj, xPos, yCoord, zCoord))
					validLaserPos.add(new PC_Vec3I(xPos, yCoord, zCoord));
				else
					return;
			break;
		case SOUTH:
			for (int zPos = zCoord - 1; zPos > zCoord - maxLaserLength; zPos--)
				if (worldObj.getBlock(xCoord, yCoord, zPos).isAir(worldObj, xCoord, yCoord, zPos))
					validLaserPos.add(new PC_Vec3I(xCoord, yCoord, zPos));
				else
					return;
			break;
		case NORTH:
			for (int zPos = zCoord + 1; zPos < zCoord + maxLaserLength; zPos++)
				if (worldObj.getBlock(xCoord, yCoord, zPos).isAir(worldObj, xCoord, yCoord, zPos))
					validLaserPos.add(new PC_Vec3I(xCoord, yCoord, zPos));
				else
					return;
			break;
		case EAST:
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

	@Override
	public PC_IGresGui openClientGui(EntityPlayer player, NBTTagCompound serverData) {
		return new PCla_GuiLaser(player, this);
	}

	@Override
	public PC_GresBaseWithInventory openServerGui(EntityPlayer player) {
		return new PCla_ContainerLaser(player, this);
	}

	@Override
	public NBTTagCompound sendOnGuiOpenToClient(EntityPlayer player) {
		return null;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if (itemstack.getItem().getClass().equals(PCla_ItemLens.class)) {
			if (i >= 0 && i < 4)
				return true;
		}
		return false;
	}

	public void doColorCalc() {
		PC_Vec4I[] colors = new PC_Vec4I[4];
		for (int i = 0; i < 4; i++) {
			if (inventoryContents[i] != null) {
				colors[i] = ((PCla_ItemLens) inventoryContents[i].getItem()).getColorFromMeta(inventoryContents[i]
						.getItemDamage());
			}
		}
		currColor = PC_Utils.averageVec4I(colors);
	}

	@Override
	public PC_RedstoneWorkType[] getAllowedRedstoneWorkTypes() {
		return new PC_RedstoneWorkType[] { PC_RedstoneWorkType.EVER, PC_RedstoneWorkType.ON_ON,
				PC_RedstoneWorkType.ON_OFF, PC_RedstoneWorkType.ON_FLANK, PC_RedstoneWorkType.ON_HI_FLANK,
				PC_RedstoneWorkType.ON_LOW_FLANK };
	}
}
