package powercraft.laser.block;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import powercraft.api.PC_Vec3;
import powercraft.api.block.PC_AbstractBlockBase;
import powercraft.api.renderer.PC_ISimpleBlockRenderingHandler;
import powercraft.api.renderer.PC_ModelHelper;

// UNUSED!!! 
//use PC_Block.renderWorldBlock(IBlockAccess world, int x, int y, int z, int modelId, RenderBlocks renderer);
// and public void renderInventoryBlock(int metadata, int modelId, RenderBlocks renderer)
@Deprecated
public class PCla_BlockLaserRender extends PC_ISimpleBlockRenderingHandler {

	@Override
	public int getRenderID() {
		return 0;
	}

	@Override
	public void renderInventoryPC_Block(PC_AbstractBlockBase block, int metadata, int modelId,
			RenderBlocks renderer) {

	}

	@Override
	public boolean renderWorldPC_Block(IBlockAccess world, int x, int y, int z,
			PC_AbstractBlockBase block, int modelId, RenderBlocks renderer) {
		Tessellator tessellator = Tessellator.instance;
		tessellator.addTranslation(x, y, z);
		// for each quad/triangle:
		/*tessellator.addVertex(0.8, 0.2, -1);
		tessellator.addVertex(0.8, 0.8, -1);
		tessellator.addVertex(0.8, 0.8, -3);
		tessellator.addVertex(0.8, 0.2, -3);*/
		PC_ModelHelper.drawBox(new PC_Vec3(0.8, 0.8, -1), new PC_Vec3(0.2, 0.2, 0), tessellator,
				PCla_BlockLaser.icons[2]);
		PC_ModelHelper.drawBox(new PC_Vec3(0.8, 0.8, -2), new PC_Vec3(0.2, 0.2, -1), tessellator,
				PCla_BlockLaser.icons[2]);
		PC_ModelHelper.drawBox(new PC_Vec3(0.8, 0.8, -3), new PC_Vec3(0.2, 0.2, -2), tessellator,
				PCla_BlockLaser.icons[2]);
		PC_ModelHelper.drawBlockAsUsual(block, tessellator, 0);
		// again and again, until you're done, then:
		tessellator.addTranslation(-x, -y, -z);
		return false;
	}

	@Override
	public boolean shouldRender3DInPC_Inventory(int modelId) {
		return false;
	}

}
