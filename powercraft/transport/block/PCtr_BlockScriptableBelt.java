package powercraft.transport.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import powercraft.api.block.PC_BlockTileEntity;
import powercraft.api.block.PC_TileEntity;
import powercraft.transport.block.tileentity.PCtr_TileEntityScriptableBelt;

public class PCtr_BlockScriptableBelt extends PC_BlockTileEntity {

	public PCtr_BlockScriptableBelt() {
		super(Material.circuits);
		setCreativeTab(CreativeTabs.tabTransport);
		maxY = 1.0f/16.0f;
	}
	
	@Override
	public boolean renderAsNormalBlock(){
		return false;
	}
	
	@Override
    public boolean isOpaqueCube(){
        return false;
    }
	
	@Override
	public Class<? extends PC_TileEntity> getTileEntityClass() {
		return PCtr_TileEntityScriptableBelt.class;
	}

	@Override
	public int getRenderType() {
		return 0;
	}

	
	
}
