package powercraft.laser.tileEntity;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import powercraft.laser.PCla_Laser;
import powercraft.laser.block.PCla_BlockLaser;

public class PCla_TileEntityLaserRender extends TileEntitySpecialRenderer {

	@Override
	public void renderTileEntityAt(TileEntity te, double d, double d1, double d2, float f) {
		GL11.glPushMatrix();
		// This will move our renderer so that it will be on proper place in the
		// world
		GL11.glTranslatef((float) d, (float) d1, (float) d2);
		PCla_TileEntityLaser tileEntity = (PCla_TileEntityLaser) te;

		renderBlock(tileEntity, tileEntity.getWorldObj(), tileEntity.xCoord, tileEntity.yCoord,
				tileEntity.zCoord, PCla_Laser.laser);
		GL11.glPopMatrix();
	}

	public void renderBlock(PCla_TileEntityLaser tl, World world, int i, int j, int k,
			PCla_BlockLaser block) {
		Tessellator tessellator = Tessellator.instance;

		// Umgebungslicht
		/*float f = block.getBlockBrightness(world, i, j, k);
		int l = world.getLightBrightnessForSkyBlocks(i, j, k, 0);
		int l1 = l % 65536;
		int l2 = l / 65536;
		tessellator.setColorOpaque_F(f, f, f);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, l1, l2);*/

		GL11.glPushMatrix();
		GL11.glTranslatef(0.5F, 0, 0.5F);

		// GL11.glRotatef(dir * (-90F), 0F, 1F, 0F);
		GL11.glTranslatef(-0.5F, 0, -0.5F);
		// bindTexture(new ResourceLocation(ModInfo.modId +
		// ":textures/models/woodTexture.png"));

		tessellator.startDrawingQuads();

		drawBox(0.375f, 0.625f, 0.375f, 0.625f, 0f, 1f, tessellator);

		tessellator.draw();

		GL11.glPopMatrix();
	}

	public static void drawBox(float x1, float x2, float y1, float y2, float z1, float z2,
			Tessellator tessellator) {
		// vorne
		tessellator.addVertexWithUV(x1, y1, z1, x1, y1);
		tessellator.addVertexWithUV(x1, y2, z1, x1, y2);
		tessellator.addVertexWithUV(x2, y2, z1, x2, y2);
		tessellator.addVertexWithUV(x2, y1, z1, x2, y1);
		// links
		tessellator.addVertexWithUV(x1, y1, z2, z1, y1);
		tessellator.addVertexWithUV(x1, y2, z2, z1, y2);
		tessellator.addVertexWithUV(x1, y2, z1, z2, y2);
		tessellator.addVertexWithUV(x1, y1, z1, z2, y1);
		// hinten
		tessellator.addVertexWithUV(x2, y1, z2, x2, y1);
		tessellator.addVertexWithUV(x2, y2, z2, x2, y2);
		tessellator.addVertexWithUV(x1, y2, z2, x1, y2);
		tessellator.addVertexWithUV(x1, y1, z2, x1, y1);
		// rechts
		tessellator.addVertexWithUV(x2, y1, z1, z2, y1);
		tessellator.addVertexWithUV(x2, y2, z1, z2, y2);
		tessellator.addVertexWithUV(x2, y2, z2, z1, y2);
		tessellator.addVertexWithUV(x2, y1, z2, z1, y1);
		// oben
		tessellator.addVertexWithUV(x1, y2, z1, x1, z1);
		tessellator.addVertexWithUV(x1, y2, z2, x1, z2);
		tessellator.addVertexWithUV(x2, y2, z2, x2, z2);
		tessellator.addVertexWithUV(x2, y2, z1, x2, z1);
		// unten
		tessellator.addVertexWithUV(x1, y1, z2, x1, z1);
		tessellator.addVertexWithUV(x1, y1, z1, x1, z2);
		tessellator.addVertexWithUV(x2, y1, z1, x2, z2);
		tessellator.addVertexWithUV(x2, y1, z2, x2, z1);
	}
}