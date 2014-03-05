package powercraft.weasel.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import powercraft.api.block.PC_BlockTileEntity;
import powercraft.api.block.PC_TileEntity;
import powercraft.weasel.tileentity.PCws_TileEntityCore;


public class PCws_BlockCore extends PC_BlockTileEntity {
	
	public PCws_BlockCore() {
		super(Material.ground);
		setCreativeTab(CreativeTabs.tabRedstone);
	}

	@Override
	public Class<? extends PC_TileEntity> getTileEntityClass() {
		return PCws_TileEntityCore.class;
	}
	
}
