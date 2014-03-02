package powercraft.energy.multiblock;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Field.Flag;
import powercraft.api.energy.PC_EnergyGrid;
import powercraft.api.energy.PC_IEnergyGridConduit;
import powercraft.api.energy.PC_IEnergyGridTile;
import powercraft.api.grid.PC_GridHelper;
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
		if(PC_GridHelper.hasGrid(world, x, y, z, side, PC_IEnergyGridTile.class)){
			return 2;
		}
		return 0;
	}

	@Override
	public void reconnect(){
		if (!isClient()) {
			PC_EnergyGrid g = this.grid;
			if(g==null){
				getGridIfNull();
			}else{
				g.disableSplitting();
				removeFromGrid();
				getGridIfNull();
				g.enableSplitting();
			}
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
	public void setGrid(PC_EnergyGrid grid) {
		this.grid = grid;
	}

	@Override
	public PC_EnergyGrid getGrid() {
		return this.grid;
	}

	@Override
	public float getMaxEnergy() {
		return 100;
	}

	@Override
	public void setEnergyFlow(float energy) {
		//
	}

	@Override
	public void handleToMuchEnergy(float energy) {
		//
	}
	
	@Override
	public void getGridIfNull() {
		World world = getWorld();
		int x = this.multiblock.xCoord;
		int y = this.multiblock.yCoord;
		int z = this.multiblock.zCoord;
		PC_GridHelper.getGridIfNull(world, x, y, z, 0x3F, this, PC_EnergyGrid.factory, PC_IEnergyGridTile.class);
	}
	
	@Override
	public void removeFromGrid() {
		PC_GridHelper.removeFromGrid(getWorld(), (PC_IEnergyGridTile)this);
	}
	
}
