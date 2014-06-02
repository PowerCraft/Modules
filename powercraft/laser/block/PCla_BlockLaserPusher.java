package powercraft.laser.block;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;
import powercraft.api.PC_Direction;
import powercraft.api.PC_IconRegistry;
import powercraft.api.block.PC_BlockTileEntity;
import powercraft.api.block.PC_BlockType;
import powercraft.api.block.PC_TileEntity;
import powercraft.laser.PCla_LaserRenderer;
import powercraft.laser.tileEntity.PCla_TileEntityLaserPusher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class PCla_BlockLaserPusher extends PC_BlockTileEntity {

	public static IIcon side;
	public static IIcon inside;
	public static IIcon black;
	public static IIcon white;
	
	public PCla_BlockLaserPusher() {
		super(PC_BlockType.MACHINE);
		setCreativeTab(CreativeTabs.tabBlock);
		setBlockName("Tractor Laser");
	}

	@Override
	public Class<? extends PC_TileEntity> getTileEntityClass() {
		return PCla_TileEntityLaserPusher.class;
	}
	
	@SuppressWarnings("hiding")
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(PC_Direction side, int metadata) {
		return PCla_BlockLaserPusher.side;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(PC_IconRegistry iconRegistry) {
		side = iconRegistry.registerIcon("side");
		inside = iconRegistry.registerIcon("inside");
		black = iconRegistry.registerIcon("black");
		white = iconRegistry.registerIcon("white");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void renderInventoryBlock(int metadata, int modelId, RenderBlocks renderer) {
		PCla_LaserRenderer.renderLaserInInventory(renderer, side, inside, black, white);
	}
	
}
