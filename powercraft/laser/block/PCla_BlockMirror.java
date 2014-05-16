package powercraft.laser.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import powercraft.api.block.PC_BlockTileEntity;
import powercraft.api.block.PC_TileEntity;
import powercraft.laser.tileEntity.PCla_TileEntityMirror;


public class PCla_BlockMirror extends PC_BlockTileEntity {

	public PCla_BlockMirror() {
		super(Material.ground);
		setCreativeTab(CreativeTabs.tabBlock);
	}

	@Override
	public Class<? extends PC_TileEntity> getTileEntityClass() {
		return PCla_TileEntityMirror.class;
	}
	
}
