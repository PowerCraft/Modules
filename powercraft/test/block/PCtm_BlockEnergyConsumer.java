package powercraft.test.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import powercraft.api.block.PC_BlockTileEntity;
import powercraft.api.block.PC_TileEntity;
import powercraft.test.block.tileentity.PCtm_TileEntityEnergyConsumer;

public class PCtm_BlockEnergyConsumer extends PC_BlockTileEntity {
	
	public PCtm_BlockEnergyConsumer() {
		super(Material.ground);
		setCreativeTab(CreativeTabs.tabRedstone);
	}
	
	@Override
	public Class<? extends PC_TileEntity> getTileEntityClass() {
		return PCtm_TileEntityEnergyConsumer.class;
	}
	
}
