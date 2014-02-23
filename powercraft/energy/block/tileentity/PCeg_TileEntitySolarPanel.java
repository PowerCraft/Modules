package powercraft.energy.block.tileentity;

import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import powercraft.api.PC_Direction;
import powercraft.api.block.PC_TileEntity;
import powercraft.api.energy.PC_EnergyGrid;
import powercraft.api.energy.PC_IEnergyGridProvider;
import powercraft.api.energy.PC_IEnergyGridTile;

public class PCeg_TileEntitySolarPanel extends PC_TileEntity implements PC_IEnergyGridProvider {
	
	private PC_EnergyGrid grid;
	
	@Override
	public void onAdded() {
		getGridIfNull();
	}

	@Override
	public void onChunkUnload() {
		removeFormGrid();
	}

	@Override
	public void onBreak() {
		removeFormGrid();
	}
	
	public void onTick(){
		getGridIfNull();
	}
	
	private void getGridIfNull() {
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

	@Override
	public void setGrid(PC_EnergyGrid grid) {
		this.grid = grid;
	}

	@Override
	public PC_EnergyGrid getGrid() {
		return grid;
	}

	@Override
	public float getEnergyUseable() {
		if (!worldObj.provider.hasNoSky) {
            float power = worldObj.getSavedLightValue(EnumSkyBlock.Sky, xCoord, yCoord, zCoord) - worldObj.skylightSubtracted;
            float angle = worldObj.getCelestialAngleRadians(1.0F);

            if (angle < (float)Math.PI){
            	angle += (0.0F - angle) * 0.2F;
            }else{
            	angle += (((float)Math.PI * 2F) - angle) * 0.2F;
            }

            power *= MathHelper.cos(angle);

            if (power < 0){
            	power = 0;
            }

            return power*10;
        }
		return 0;
	}

	@Override
	public void takeEnergy(float energy) {
		
	}

	@Override
	public boolean dynamic() {
		return false;
	}

}
