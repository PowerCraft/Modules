package powercraft.machines.block;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;
import powercraft.api.PC_Direction;
import powercraft.api.PC_IconRegistry;
import powercraft.api.block.PC_BlockTileEntity;
import powercraft.api.block.PC_BlockType;
import powercraft.api.block.PC_TileEntity;
import powercraft.machines.tileentity.PCma_TileEntityAutomaticWorkbench;

public class PCma_BlockAutomaticWorkbench extends PC_BlockTileEntity {

	public static IIcon front;
	public static IIcon side;
	
	public PCma_BlockAutomaticWorkbench() {
		super(PC_BlockType.MACHINE);
		setCreativeTab(CreativeTabs.tabDecorations);
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
		return PCma_TileEntityAutomaticWorkbench.class;
	}

	@Override
	public IIcon getIcon(PC_Direction side, int metadata) {
		if(side==PC_Direction.EAST){
			return front;
		}
		return PCma_BlockAutomaticWorkbench.side;
	}

	@Override
	public void registerIcons(PC_IconRegistry iconRegistry) {
		front = iconRegistry.registerIcon("front");
		side = iconRegistry.registerIcon("side");
	}
	
}
