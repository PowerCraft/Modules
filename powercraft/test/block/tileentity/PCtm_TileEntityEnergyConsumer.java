package powercraft.test.block.tileentity;

import powercraft.api.PC_Direction;
import powercraft.api.block.PC_TileEntity;
import powercraft.api.energy.PC_EnergyGrid;
import powercraft.api.energy.PC_IEnergyGridConsumer;
import powercraft.api.energy.PC_IEnergyGridTile;
import powercraft.api.grid.PC_IGridHolder;

public class PCtm_TileEntityEnergyConsumer extends PC_TileEntity implements PC_IEnergyGridConsumer, PC_IGridHolder {
	
	private PC_EnergyGrid grid;
	
	@Override
	public void getGridIfNull() {
		if(grid == null && !isClient()){
			for(PC_Direction dir:PC_Direction.VALID_DIRECTIONS){
				if(dir!=PC_Direction.UP){
					PC_IEnergyGridTile tile = PC_EnergyGrid.getGridTile(worldObj, xCoord+dir.offsetX, yCoord+dir.offsetY, zCoord+dir.offsetZ, dir.getOpposite());
					if(tile!=null && tile.getGrid()!=null){
						if(grid==null){
							tile.getGrid().addTile(this, tile);
						}else{
							PC_EnergyGrid.connect(tile, this);
						}
					}
				}
			}
			if(grid==null){
				grid = new PC_EnergyGrid(this);
			}
		}
	}
	
	@Override
	public void removeFormGrid() {
		if (grid != null && !isClient()) {
			PC_EnergyGrid.remove((PC_IEnergyGridTile)this);
		}
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
