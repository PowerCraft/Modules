package powercraft.machines.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;
import powercraft.api.PC_Direction;
import powercraft.api.PC_IconRegistry;
import powercraft.api.block.PC_BlockTileEntity;
import powercraft.api.block.PC_TileEntity;
import powercraft.machines.tileentity.PCma_TileEntityFurnace;

public class PCma_BlockFurnace extends PC_BlockTileEntity {

	public static IIcon front;
	public static IIcon frontOn;
	public static IIcon side;
	
	public PCma_BlockFurnace() {
		super(Material.ground);
		setCreativeTab(CreativeTabs.tabDecorations);
	}

	@Override
	public Class<? extends PC_TileEntity> getTileEntityClass() {
		return PCma_TileEntityFurnace.class;
	}

	@Override
	public IIcon getIcon(PC_Direction side, int metadata) {
		if(side==PC_Direction.NORTH){
			return front;
		}
		return PCma_BlockFurnace.side;
	}

	@Override
	public void registerIcons(PC_IconRegistry iconRegistry) {
		front = iconRegistry.registerIcon("front");
		frontOn = iconRegistry.registerIcon("frontOn");
		side = iconRegistry.registerIcon("side");
	}
	
}