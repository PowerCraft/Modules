package powercraft.energy.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;
import powercraft.api.PC_Direction;
import powercraft.api.PC_IconRegistry;
import powercraft.api.block.PC_BlockTileEntity;
import powercraft.api.block.PC_TileEntity;
import powercraft.energy.tileentity.PCeg_TileEntityAccumulator;

public class PCeg_BlockAccumulator extends PC_BlockTileEntity {

	private IIcon side;
	
	public PCeg_BlockAccumulator() {
		super(Material.ground);
		setCreativeTab(CreativeTabs.tabRedstone);
	}

	@Override
	public Class<? extends PC_TileEntity> getTileEntityClass() {
		return PCeg_TileEntityAccumulator.class;
	}

	@Override
	public IIcon getIcon(PC_Direction side, int metadata) {
		return this.side;
	}

	@Override
	public void registerIcons(PC_IconRegistry iconRegistry) {
		side = iconRegistry.registerIcon("side");
	}
	
}
