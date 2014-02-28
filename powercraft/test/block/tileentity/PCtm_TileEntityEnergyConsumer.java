package powercraft.test.block.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import powercraft.api.block.PC_TileEntity;
import powercraft.api.energy.PC_EnergyGrid;
import powercraft.api.energy.PC_IEnergyGridConsumer;
import powercraft.api.energy.PC_IEnergyGridTile;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.PC_IGresGuiOpenHandler;
import powercraft.api.grid.PC_GridHelper;
import powercraft.api.grid.PC_IGridHolder;
import powercraft.test.gui.PCtm_GuiEnergyConsumer;

public class PCtm_TileEntityEnergyConsumer extends PC_TileEntity implements PC_IEnergyGridConsumer, PC_IGridHolder, PC_IGresGuiOpenHandler {
	
	private PC_EnergyGrid grid;
	
	@Override
	public void getGridIfNull() {
		PC_GridHelper.getGridIfNull(worldObj, xCoord, yCoord, zCoord, 0x3F, this, PC_EnergyGrid.factory, PC_IEnergyGridTile.class);
	}
	
	@Override
	public void removeFromGrid() {
		PC_GridHelper.removeFromGrid(worldObj, (PC_IEnergyGridTile)this);
	}

	@Override
	public void setGrid(PC_EnergyGrid grid) {
		this.grid = grid;
	}

	@Override
	public PC_EnergyGrid getGrid() {
		return grid;
	}

	@Override
	public float getEnergyRequested() {
		return 1000;
	}

	@Override
	public void useEnergy(float energy) {
		System.out.println("Energy:"+energy);
	}

	@Override
	public float getMaxPercentToWork() {
		return 0;
	}

	@Override
	public PC_IGresGui openClientGui(EntityPlayer player,
			NBTTagCompound serverData) {
		// TODO Auto-generated method stub
		return new PCtm_GuiEnergyConsumer();
	}

	@Override
	public PC_GresBaseWithInventory openServerGui(EntityPlayer player) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NBTTagCompound sendOnGuiOpenToClient(EntityPlayer player) {
		// TODO Auto-generated method stub
		return null;
	}

}
