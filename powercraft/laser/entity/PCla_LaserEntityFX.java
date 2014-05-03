package powercraft.laser.entity;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import powercraft.api.PC_ClientUtils;
import powercraft.api.PC_MathHelper;
import powercraft.api.PC_Utils;
import powercraft.api.PC_Vec3;
import powercraft.laser.PCla_Laser;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PCla_LaserEntityFX extends EntityFX {

	private static final ResourceLocation texture = PC_Utils.getResourceLocation(PCla_Laser.INSTANCE, "textures/blocks/Laser2/beam.png");
	
	private PC_Vec3 endPos;
	
	public PCla_LaserEntityFX(World world, PC_Vec3 startPos, PC_Vec3 endPos, PC_Vec3 color) {
		super(world, startPos.x, startPos.y, startPos.z);
		this.endPos = endPos;
		this.particleRed = (float) color.x;
		this.particleGreen = (float) color.y;
		this.particleBlue = (float) color.z;
		this.motionX = this.motionY = this.motionZ = 0.0D;
		this.particleMaxAge = 0;
	}
	
	@Override
	public int getFXLayer() {
        return 3;
    }

	@Override
	public void renderParticle(Tessellator tessellator, float patrialTickTime, float par3, float par4, float par5, float par6, float par7) {
		if(this.isDead)
			return;
		Entity entity = PC_ClientUtils.mc().renderViewEntity;
		interpPosX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * patrialTickTime;
        interpPosY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * patrialTickTime;
        interpPosZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * patrialTickTime;
		double startPosX = this.posX - interpPosX;
		double startPosY = this.posY - interpPosY;
		double startPosZ = this.posZ - interpPosZ;
		double endPosX = this.endPos.x - interpPosX;
		double endPosY = this.endPos.y - interpPosY;
		double endPosZ = this.endPos.z - interpPosZ;
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDepthMask(false);
		tessellator.startDrawingQuads();
		tessellator.setColorOpaque_F(this.particleRed/2, this.particleGreen/2, this.particleBlue/2);
		tessellator.setBrightness(65535);
		drawTube(startPosX, startPosY, startPosZ, endPosX, endPosY, endPosZ, 0.2, 8, 0, tessellator);
		drawTube(startPosX, startPosY, startPosZ, endPosX, endPosY, endPosZ, 0.15, 8, 0, tessellator);
		drawTube(startPosX, startPosY, startPosZ, endPosX, endPosY, endPosZ, 0.1, 8, 0, tessellator);
		drawTube(startPosX, startPosY, startPosZ, endPosX, endPosY, endPosZ, 0.05, 8, 0, tessellator);
		tessellator.draw();
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_LIGHTING);
	}
	
	private static void drawTube(double startPosX, double startPosY, double startPosZ, double endPosX, double endPosY, double endPosZ, double radius, int nums, float ty, Tessellator tessellator){
		double lx = radius;
		double ly = 0;
		double dx = startPosX-endPosX;
		double dy = startPosY-endPosY;
		double dz = startPosZ-endPosZ;
		double l = PC_MathHelper.length(dx, dy, dz);
		dx /= l;
		dy /= l;
		dz /= l;
		double dxx;
		double dyx;
		double dzx;
		if(dz==0){
			dxx = dy;
			dyx = -dx;
			dzx = 0;
		}else{
			dxx = 1;
			dyx = 1;
			dzx = -(dx+dy)/dz;
			double ll = PC_MathHelper.length(dxx, dyx, dzx);
			dxx /= ll;
			dyx /= ll;
			dzx /= ll;
		}
		double dxy = dy*dzx-dz*dyx;
		double dyy = dz*dxx-dx*dzx;
		double dzy = dx*dyx-dy*dxx;
		float tx = 0;
		for(int i=1; i<=nums; i++){
			float ntx = i/(float)nums;
			float rad = (float) (ntx*2*Math.PI);
			double x = PC_MathHelper.cos(rad)*radius;
			double y = PC_MathHelper.sin(rad)*radius;
			tessellator.addVertexWithUV(startPosX+dxx*lx+dxy*ly, startPosY+dyx*lx+dyy*ly, startPosZ+dzx*lx+dzy*ly, tx, ty);
			tessellator.addVertexWithUV(startPosX+dxx*x+dxy*y, startPosY+dyx*x+dyy*y, startPosZ+dzx*x+dzy*y, ntx, ty);
			tessellator.addVertexWithUV(endPosX+dxx*x+dxy*y, endPosY+dyx*x+dyy*y, endPosZ+dzx*x+dzy*y, ntx, ty+l);
			tessellator.addVertexWithUV(endPosX+dxx*lx+dxy*ly, endPosY+dyx*lx+dyy*ly, endPosZ+dzx*lx+dzy*ly, tx, ty+l);
			tx = ntx;
			lx = x;
			ly = y;
		}
	}
	
}
