package powercraft.laser.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;
import powercraft.api.PC_IconRegistry;
import powercraft.api.block.PC_BlockTileEntity;
import powercraft.api.block.PC_BlockType;
import powercraft.api.block.PC_TileEntity;
import powercraft.laser.tileEntity.PCla_TileEntityMirror;


public class PCla_BlockMirror extends PC_BlockTileEntity {

	public static IIcon mirror;
	public static IIcon black;
	public static IIcon white;

	public PCla_BlockMirror() {
		super(PC_BlockType.MACHINE);
		setCreativeTab(CreativeTabs.tabBlock);
	}

	@Override
	public Class<? extends PC_TileEntity> getTileEntityClass() {
		return PCla_TileEntityMirror.class;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(PC_IconRegistry iconRegistry) {
		mirror = iconRegistry.registerIcon("mirror");
		black = iconRegistry.registerIcon("black");
		white = iconRegistry.registerIcon("white");
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
}
