package powercraft.energy.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;
import powercraft.api.PC_Direction;
import powercraft.api.PC_IconRegistry;
import powercraft.api.block.PC_BlockTileEntity;
import powercraft.api.block.PC_TileEntity;
import powercraft.energy.tileentity.PCeg_TileEntitySolarPanel;

public class PCeg_BlockSolarPanel extends PC_BlockTileEntity {

	private IIcon side;
	private IIcon top;
	
	public PCeg_BlockSolarPanel() {
		super(Material.ground);
		this.maxY = 15.0f/16.0f;
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

	@SuppressWarnings("hiding")
	@Override
	public IIcon getIcon(PC_Direction side, int metadata) {
		if(side==PC_Direction.UP){
			return this.top;
		}
		return this.side;
	}

	@Override
	public void registerIcons(PC_IconRegistry iconRegistry) {
		this.top = iconRegistry.registerIcon("top");
		this.side = iconRegistry.registerIcon("side");
	}
	
}
