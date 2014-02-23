package powercraft.energy.multiblock;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import powercraft.api.PC_Direction;
import powercraft.api.block.PC_Field.Flag;
import powercraft.api.energy.PC_EnergyGrid;
import powercraft.api.energy.PC_IEnergyGridConduit;
import powercraft.api.energy.PC_IEnergyGridTile;
import powercraft.api.multiblock.conduit.PC_MultiblockObjectConduit;
import powercraft.energy.PCeg_Energy;

public class PCeg_MultiblockObjectEnergyConduit extends PC_MultiblockObjectConduit implements PC_IEnergyGridConduit {

	private PC_EnergyGrid grid;
	
	public PCeg_MultiblockObjectEnergyConduit(NBTTagCompound nbtTagCompound, Flag flag){
		super(nbtTagCompound, flag);
	}
	
	public PCeg_MultiblockObjectEnergyConduit(){
		
	}
	
	@Override
	public int canConnectToBlock(World world, int x, int y, int z, PC_Direction side, Block block, int oldConnectionInfo) {
		if(PC_EnergyGrid.hasGrid(world, x, y, z, side)){
			return 2;
		}
		return 0;
	}

	@Override
	public void checkConnections() {
		int oldConnections = connections;
		super.checkConnections();
		if (!isClient() && oldConnections != connections) {
			removeFormGrid();
			getGridIfNull();
		}
	}

	@Override
	public IIcon getNormalConduitIcon() {
		return PCeg_Energy.energyConduit.normalConduit;
	}

	@Override
	public IIcon getCornerConduitIcon() {
		return PCeg_Energy.energyConduit.cornerConduit;
	}

	@Override
	public IIcon getConnectionConduitIcon(int connectionInfo) {
		return PCeg_Energy.energyConduit.connections[connectionInfo-2];
	}

	@Override
	public void updateObject() {
		super.updateObject();
		if(!isClient()){
			getGridIfNull();
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
	public float getMaxEnergy() {
		return 100;
	}

	@Override
	public void setEnergyFlow(float energy) {
		
	}

	@Override
	public void handleToMuchEnergy(float energy) {
		
	}
	
	@Override
	public boolean onAdded() {
		super.onAdded();
		getGridIfNull();
		return true;
	}

	@Override
	public void onRemoved() {
		super.onRemoved();
		removeFormGrid();
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		removeFormGrid();
	}
	
	private void getGridIfNull() {
		if(grid == null && !isClient()){
			World world = getWorld();
			int x = multiblock.xCoord;
			int y = multiblock.yCoord;
			int z = multiblock.zCoord;
			for(PC_Direction dir:PC_Direction.VALID_DIRECTIONS){
				if(!notingOnSide(dir)){
					PC_IEnergyGridTile tile = PC_EnergyGrid.getGridTile(world, x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ, dir.getOpposite());
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
				grid = new PC_EnergyGrid();
				grid.addTile(this, null);
			}
		}
	}
	
	private void removeFormGrid() {
		if (grid != null && !isClient()) {
			PC_EnergyGrid.remove((PC_IEnergyGridTile)this);
		}
	}
	
}
