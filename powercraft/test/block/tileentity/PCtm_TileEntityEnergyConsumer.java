package powercraft.test.block.tileentity;

import powercraft.api.block.PC_TileEntity;
import powercraft.api.energy.PC_EnergyGrid;
import powercraft.api.energy.PC_IEnergyGridConsumer;
import powercraft.api.energy.PC_IEnergyGridTile;
import powercraft.api.grid.PC_GridHelper;
import powercraft.api.grid.PC_IGridHolder;

public class PCtm_TileEntityEnergyConsumer extends PC_TileEntity implements PC_IEnergyGridConsumer, PC_IGridHolder {
	
	private PC_EnergyGrid grid;
	
	@Override
	public void getGridIfNull() {
		PC_GridHelper.getGridIfNull(worldObj, xCoord, yCoord, zCoord, 0x3F, this, PC_EnergyGrid.factory, PC_IEnergyGridTile.class);
	}
	
	@Override
	public void removeFormGrid() {
		PC_GridHelper.removeFormGrid(worldObj, (PC_IEnergyGridTile)this);
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

}
