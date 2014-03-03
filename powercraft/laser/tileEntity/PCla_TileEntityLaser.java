package powercraft.laser.tileEntity;

import java.util.Vector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Vec3I;
import powercraft.api.block.PC_TileEntityWithInventory;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.PC_IGresGuiOpenHandler;
import powercraft.api.redstone.PC_RedstoneWorkType;
import powercraft.laser.container.PCla_ContainerLaser;
import powercraft.laser.gui.PCla_GuiLaser;

public class PCla_TileEntityLaser extends PC_TileEntityWithInventory implements PC_IGresGuiOpenHandler {

	public Vector<PC_Vec3I> validLaserPos = new Vector<PC_Vec3I>(15);
	public PC_Direction orientation;
	public int maxLaserLength = 15;

	public PCla_TileEntityLaser() {
		super("Laser", 2, new Group(true, 0), new Group(true, 1));
		orientation = PC_Direction.NORTH;
		this.workWhen = PC_RedstoneWorkType.EVER;

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
		return true;
	}

	@Override
	public PC_RedstoneWorkType[] getAllowedRedstoneWorkTypes() {
		return new PC_RedstoneWorkType[] { null, PC_RedstoneWorkType.EVER, PC_RedstoneWorkType.ON_ON,
				PC_RedstoneWorkType.ON_OFF };
	}
}
