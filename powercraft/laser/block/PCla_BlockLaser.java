package powercraft.laser.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;
import powercraft.api.PC_Direction;
import powercraft.api.PC_IconRegistry;
import powercraft.api.block.PC_BlockTileEntity;
import powercraft.api.block.PC_TileEntity;
import powercraft.laser.tileEntity.PCla_TileEntityLaser;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PCla_BlockLaser extends PC_BlockTileEntity {

	@SideOnly(Side.CLIENT)
	private IIcon[] icons = new IIcon[2];

	public PCla_BlockLaser() {
		super(Material.wood);
		setCreativeTab(CreativeTabs.tabBlock);

	}

	@Override
	public Class<? extends PC_TileEntity> getTileEntityClass() {
		return PCla_TileEntityLaser.class;
	}

	// Client Side Stuff
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(PC_Direction side, int metadata) {
		if (side.equals(PC_Direction.NORTH))
			return icons[1];
		return icons[0];
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getRenderType() {
		return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(PC_IconRegistry iconRegistry) {
		icons[0] = iconRegistry.registerIcon("side");
		icons[1] = iconRegistry.registerIcon("front");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

}
