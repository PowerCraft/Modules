package powercraft.laser.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;
import powercraft.api.PC_Direction;
import powercraft.api.PC_IconRegistry;
import powercraft.api.block.PC_BlockTileEntity;
import powercraft.api.block.PC_TileEntity;
import powercraft.laser.tileEntity.PCla_TileEntityLaserTractor;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class PCla_BlockLaserTractor extends PC_BlockTileEntity {

	public static IIcon side;
	public static IIcon front;
	
	public PCla_BlockLaserTractor() {
		super(Material.wood);
		setCreativeTab(CreativeTabs.tabBlock);
		setBlockName("Tractor Laser");
	}

	@Override
	public Class<? extends PC_TileEntity> getTileEntityClass() {
		return PCla_TileEntityLaserTractor.class;
	}
	
	@SuppressWarnings("hiding")
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(PC_Direction side, int metadata) {
		if (side == PC_Direction.EAST)
			return front;
		return PCla_BlockLaserTractor.side;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(PC_IconRegistry iconRegistry) {
		side = iconRegistry.registerIcon("side");
		front = iconRegistry.registerIcon("front");
	}

}
