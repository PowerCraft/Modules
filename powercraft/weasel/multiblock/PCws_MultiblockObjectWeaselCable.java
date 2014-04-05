package powercraft.weasel.multiblock;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import powercraft.api.PC_Field.Flag;
import powercraft.api.energy.PC_EnergyGrid;
import powercraft.api.energy.PC_IEnergyGridConduit;
import powercraft.api.energy.PC_IEnergyGridTile;
import powercraft.api.grid.PC_GridHelper;
import powercraft.api.multiblock.cable.PC_MultiblockObjectCable;
import powercraft.weasel.PCws_Weasel;

public class PCws_MultiblockObjectWeaselCable extends PC_MultiblockObjectCable implements PC_IEnergyGridConduit {

	private PC_EnergyGrid grid;
	
	public PCws_MultiblockObjectWeaselCable(NBTTagCompound nbtTagCompound, Flag flag){
		super(nbtTagCompound, flag);
	}
	
	public PCws_MultiblockObjectWeaselCable(){
		super(1, 8);
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

	@Override
	protected IIcon getCableIcon() {
		return PCws_Weasel.WEASEL_CABLE.normal;
	}
	
	@Override
	protected IIcon getCableCornerIcon() {
		return PCws_Weasel.WEASEL_CABLE.corner;
	}
	
	@Override
	protected IIcon getCableSideIcon() {
		return PCws_Weasel.WEASEL_CABLE.side;
	}

	@SuppressWarnings("hiding")
	@Override
	protected IIcon getCableLineIcon(int index) {
		return null;
	}

	@Override
	protected boolean useOverlay() {
		return false;
	}

	@Override
	protected int getColorForCable(int cableID) {
		return 0xFFFFFFFF;
	}

	@Override
	protected int getMask() {
		return -1;
	}
	
}
