package powercraft.laser.block;

import net.minecraft.creativetab.CreativeTabs;
import powercraft.api.block.PC_BlockTileEntity;
import powercraft.api.block.PC_BlockType;
import powercraft.api.block.PC_TileEntity;
import powercraft.laser.tileEntity.PCla_TileEntityMirror;


public class PCla_BlockMirror extends PC_BlockTileEntity {

	public PCla_BlockMirror() {
		super(PC_BlockType.MACHINE);
		setCreativeTab(CreativeTabs.tabBlock);
	}

	@Override
	public Class<? extends PC_TileEntity> getTileEntityClass() {
		return PCla_TileEntityMirror.class;
	}
	
}
