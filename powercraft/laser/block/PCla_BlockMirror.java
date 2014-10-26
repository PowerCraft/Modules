package powercraft.laser.block;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import powercraft.api.PC_Direction;
import powercraft.api.PC_IconRegistry;
import powercraft.api.PC_Utils;
import powercraft.api.block.PC_BlockTileEntity;
import powercraft.api.block.PC_BlockType;
import powercraft.api.block.PC_TileEntity;
import powercraft.api.renderer.PC_Renderer;
import powercraft.laser.tileentity.PCla_TileEntityMirror;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


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

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		PCla_TileEntityMirror m = PC_Utils.getTileEntity(world, x, y, z, PCla_TileEntityMirror.class);
		if(m==null)
			return true;
		PC_Direction dir = m.getPlacing();
		if(dir==null)
			return true;
		return PC_Utils.isBlockSideSolid(world, x-dir.offsetX, y-dir.offsetY, z-dir.offsetZ, dir);
	}
	
	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side){
		PC_Direction dir = PC_Direction.fromSide(side);
		return PC_Utils.isBlockSideSolid(world, x-dir.offsetX, y-dir.offsetY, z-dir.offsetZ, dir);
	}

	@Override
	public void renderInventoryBlock(int metadata, int modelId, RenderBlocks renderer) {
		IIcon[] icons = new IIcon[6];
		for(int i = 0; i < 6; i++ ) {
			icons[i] = PCla_BlockMirror.black;
		}
		renderer.setRenderBounds(5 / 16.0, 0, 5 / 16.0, 11 / 16.0, 1 / 16.0, 11 / 16.0);
		PC_Renderer.renderStandardBlockInInventory(icons, -1, 0, renderer);
		
		for(int i = 0; i < 6; i++ ) {
			icons[i] = PCla_BlockMirror.white;
		}
		
		icons[3] = PCla_BlockMirror.mirror;
		
		renderer.setRenderBounds(1 / 16.0, 1/16.0, 7 / 16.0, 15 / 16.0, 15 / 16.0, 9 / 16.0);
		PC_Renderer.renderStandardBlockInInventory(icons, -1, 0, renderer);

	}
	
}
