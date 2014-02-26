package powercraft.machines.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;
import powercraft.api.PC_Direction;
import powercraft.api.PC_IconRegistry;
import powercraft.api.block.PC_BlockTileEntity;
import powercraft.api.block.PC_TileEntity;
import powercraft.machines.tileentity.PCma_TileEntityRoaster;

public class PCma_BlockRoaster extends PC_BlockTileEntity {

	public static IIcon bottom;
	public static IIcon side;
	public static IIcon top;
	
	public PCma_BlockRoaster() {
		super(Material.ground);
		maxY = 12.0f/16.0f;
		setCreativeTab(CreativeTabs.tabDecorations);
		setTickRandomly(true);
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
		return PCma_TileEntityRoaster.class;
	}

	@Override
	public IIcon getIcon(PC_Direction side, int metadata) {
		if(side==PC_Direction.UP){
			return top;
		}else if(side==PC_Direction.DOWN){
			return bottom;
		}
		return PCma_BlockRoaster.side;
	}

	@Override
	public void registerIcons(PC_IconRegistry iconRegistry) {
		bottom = iconRegistry.registerIcon("bottom");
		side = iconRegistry.registerIcon("side");
		top = iconRegistry.registerIcon("top");
	}
	
}
