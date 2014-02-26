package powercraft.energy.tileentity;

import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import powercraft.api.PC_Direction;
import powercraft.api.block.PC_TileEntity;
import powercraft.api.energy.PC_EnergyGrid;
import powercraft.api.energy.PC_IEnergyGridProvider;
import powercraft.api.energy.PC_IEnergyGridTile;
import powercraft.api.grid.PC_GridHelper;
import powercraft.api.grid.PC_IGridHolder;
import powercraft.api.grid.PC_IGridSided;
import powercraft.api.grid.PC_IGridTile;

public class PCeg_TileEntitySolarPanel extends PC_TileEntity implements PC_IGridSided, PC_IEnergyGridProvider, PC_IGridHolder {
	
	private PC_EnergyGrid grid;
	
	@Override
	public void getGridIfNull() {
		PC_GridHelper.getGridIfNull(worldObj, xCoord, yCoord, zCoord, 0x3D, this, PC_EnergyGrid.factory, PC_IEnergyGridTile.class);
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

            return power;
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

	@Override
	public <T extends PC_IGridTile<?, T, ?, ?>> T getTile(PC_Direction side, Class<T> tileClass) {
		if(side==PC_Direction.UP)
			return null;
		if(tileClass==PC_IEnergyGridTile.class)
			return tileClass.cast(this);
		return null;
	}

}
