package powercraft.laser.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;
import powercraft.api.PC_Direction;
import powercraft.api.PC_IconRegistry;
import powercraft.api.block.PC_BlockTileEntity;
import powercraft.api.block.PC_TileEntity;
import powercraft.laser.tileEntity.PCla_TileEntityLaser2;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PCla_BlockLaser2 extends PC_BlockTileEntity {

	public static IIcon[] icons = new IIcon[3];

	public PCla_BlockLaser2() {
		super(Material.wood);
		setCreativeTab(CreativeTabs.tabBlock);
		setBlockName("Laser");
	}

	@Override
	public Class<? extends PC_TileEntity> getTileEntityClass() {
		return PCla_TileEntityLaser2.class;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(PC_Direction side, int metadata) {
		if (side.equals(PC_Direction.SOUTH))
			return icons[1];
		return icons[0];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(PC_IconRegistry iconRegistry) {
		icons[0] = iconRegistry.registerIcon("side");
		icons[1] = iconRegistry.registerIcon("front");
		icons[2] = iconRegistry.registerIcon("beam");
	}

}
