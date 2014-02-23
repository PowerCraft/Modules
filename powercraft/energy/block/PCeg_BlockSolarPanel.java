package powercraft.energy.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;
import powercraft.api.PC_Direction;
import powercraft.api.PC_IconRegistry;
import powercraft.api.block.PC_BlockTileEntity;
import powercraft.api.block.PC_TileEntity;
import powercraft.energy.block.tileentity.PCeg_TileEntitySolarPanel;

public class PCeg_BlockSolarPanel extends PC_BlockTileEntity {

	private IIcon side;
	private IIcon top;
	
	public PCeg_BlockSolarPanel() {
		super(Material.ground);
		maxY = 15.0f/16.0f;
		setCreativeTab(CreativeTabs.tabRedstone);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	@Override
	public Class<? extends PC_TileEntity> getTileEntityClass() {
		return PCeg_TileEntitySolarPanel.class;
	}

	@Override
	public IIcon getIcon(PC_Direction side, int metadata) {
		if(side==PC_Direction.UP){
			return top;
		}
		return this.side;
	}

	@Override
	public void registerIcons(PC_IconRegistry iconRegistry) {
		top = iconRegistry.registerIcon("top");
		side = iconRegistry.registerIcon("side");
	}
	
}
