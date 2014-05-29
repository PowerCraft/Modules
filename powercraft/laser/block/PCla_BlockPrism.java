package powercraft.laser.block;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;
import powercraft.api.PC_IconRegistry;
import powercraft.api.block.PC_BlockTileEntity;
import powercraft.api.block.PC_BlockType;
import powercraft.api.block.PC_TileEntity;
import powercraft.api.renderer.PC_Renderer;
import powercraft.laser.tileEntity.PCla_TileEntityPrism;


public class PCla_BlockPrism extends PC_BlockTileEntity {

	public static IIcon side;
	public static IIcon side2;
	public static IIcon side3;
	
	public PCla_BlockPrism() {
		super(PC_BlockType.MACHINE);
		setCreativeTab(CreativeTabs.tabBlock);
	}

	@Override
	public Class<? extends PC_TileEntity> getTileEntityClass() {
		return PCla_TileEntityPrism.class;
	}

	@Override
	public int getRenderBlockPass() {
		return PC_Renderer.PASS_TRANSPARENT;
	}

	@Override
	public void registerIcons(PC_IconRegistry iconRegistry) {
		side = iconRegistry.registerIcon("side");
		side2 = iconRegistry.registerIcon("side2");
		side3 = iconRegistry.registerIcon("side3");
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
	public void renderInventoryBlock(int metadata, int modelId, RenderBlocks renderer) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		IIcon[] icons = new IIcon[6];
		for(int i=0; i<6; i++){
			icons[i] = side;
		}
		renderer.setRenderBounds(3/16.0, 3/16.0, 3/16.0, 13/16.0, 13/16.0, 13/16.0);
		PC_Renderer.renderStandardBlockInInventory(icons, -1, 0, renderer);
		for(int i=0; i<6; i++){
			icons[i] = side2;
		}
		icons[0] = side3;
		icons[1] = side3;
		renderer.setRenderBounds(4/16.0, 2/16.0, 4/16.0, 12/16.0, 14/16.0, 12/16.0);
		PC_Renderer.renderStandardBlockInInventory(icons, -1, 0, renderer);
		icons[0] = side2;
		icons[1] = side2;
		icons[2] = side3;
		icons[3] = side3;
		renderer.setRenderBounds(4/16.0, 4/16.0, 2/16.0, 12/16.0, 12/16.0, 14/16.0);
		PC_Renderer.renderStandardBlockInInventory(icons, -1, 0, renderer);
		icons[2] = side2;
		icons[3] = side2;
		icons[4] = side3;
		icons[5] = side3;
		renderer.setRenderBounds(2/16.0, 4/16.0, 4/16.0, 14/16.0, 12/16.0, 12/16.0);
		PC_Renderer.renderStandardBlockInInventory(icons, -1, 0, renderer);
	}
	
}
