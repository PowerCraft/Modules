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

	private static ResourceLocation start = PC_Utils.getResourceLocation(PCla_Laser.INSTANCE, "textures/fx/Laser-Start.png");
	private static ResourceLocation middel = PC_Utils.getResourceLocation(PCla_Laser.INSTANCE, "textures/fx/Laser-Middel.png");
	private static ResourceLocation end = PC_Utils.getResourceLocation(PCla_Laser.INSTANCE, "textures/fx/Laser-End.png");
	private static ResourceLocation noise = PC_Utils.getResourceLocation(PCla_Laser.INSTANCE, "textures/fx/Laser-Noise.png");
	
	private boolean specialStart;
	private boolean specialEnd;
	private PC_Vec3 endPos;
	
	public PCla_LaserEntityFX(World world, PC_Vec3 startPos, boolean specialStart, PC_Vec3 endPos, boolean specialEnd, PC_Vec3 color) {
		super(world, startPos.x, startPos.y, startPos.z);
		this.specialStart = specialStart;
		this.endPos = endPos;
		this.specialEnd = specialEnd;
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

	@SuppressWarnings("hiding")
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
		
		PC_Vec3 startPos = new PC_Vec3(startPosX, startPosY, startPosZ);
		PC_Vec3 endPos = new PC_Vec3(endPosX, endPosY, endPosZ);
		
		PC_Vec3 d = startPos.sub(endPos).normalize();
		
		PC_Vec3 normal1;
		if(d.z==0){
			normal1 = new PC_Vec3(d.y, -d.x, 0);
		}else{
			normal1 = new PC_Vec3(1, 1, -(d.x+d.y)/d.z).normalize();
		}
		PC_Vec3 normal2 = d.cross(normal1);
		
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDepthMask(false);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		
		PC_Vec3 sp = startPos;
		
		PC_Vec3 sp1 = startPos;
		
		if(this.specialStart){
			PC_ClientUtils.mc().renderEngine.bindTexture(start);
			tessellator.startDrawingQuads();
			tessellator.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, 0.5f);
			tessellator.setBrightness(65535);
			
			sp = sp.sub(d.mul(0.8));
			
			sp1 = sp1.sub(d.mul(0.4));
			
			for(int i=0; i<20; i++){
				drawBeam(startPos, sp, normal1, normal2, i/20.0, 0.2, tessellator, 0, 1);
			}
			
			tessellator.draw();
			
		}
		PC_Vec3 ep = endPos;
		PC_Vec3 ep1 = endPos;
		if(this.specialEnd){
			ep = ep.add(d.mul(0.8));
			ep1 = ep1.add(d.mul(0.4));
		}
		
		PC_ClientUtils.mc().renderEngine.bindTexture(middel);
		
		tessellator.startDrawingQuads();
		tessellator.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, 0.5f);
		tessellator.setBrightness(65535);
		
		for(int i=0; i<20; i++){
			drawBeam(sp, ep, normal1, normal2, i/20.0, 0.2, tessellator, 0, 1);
		}
		tessellator.draw();
		
		if(this.specialEnd){
			PC_ClientUtils.mc().renderEngine.bindTexture(end);
			tessellator.startDrawingQuads();
			tessellator.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, 0.5f);
			tessellator.setBrightness(65535);
			
			for(int i=0; i<20; i++){
				drawBeam(ep, endPos, normal1, normal2, i/20.0, 0.2, tessellator, 0, 1);
			}
			
			tessellator.draw();
		}
		
		PC_ClientUtils.mc().renderEngine.bindTexture(noise);
		
		tessellator.startDrawingQuads();
		tessellator.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, 0.5f);
		tessellator.setBrightness(65535);
		
		double m = (System.currentTimeMillis()%1000)/1000.0;
		
		double l = sp1.sub(ep1).length();
		
		for(int i=0; i<20; i++){
			drawBeam(sp1, ep1, normal1, normal2, i/20.0, 0.2, tessellator, m, l);
		}
		tessellator.draw();
		
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_LIGHTING);
	}
	
	private static void drawBeam(PC_Vec3 startPos, PC_Vec3 endPos, PC_Vec3 normal1, PC_Vec3 normal2, double part, double radius, Tessellator tessellator, double m, double l){
		float rad = (float) (part*Math.PI);
		double x = PC_MathHelper.cos(rad)*radius;
		double y = PC_MathHelper.sin(rad)*radius;
		double ox = -x;
		double oy = -y;
		tessellator.addVertexWithUV(startPos.x+normal1.x*ox+normal2.x*oy, startPos.y+normal1.y*ox+normal2.y*oy, startPos.z+normal1.z*ox+normal2.z*oy, 0, m+l);
		tessellator.addVertexWithUV(startPos.x+normal1.x*x+normal2.x*y, startPos.y+normal1.y*x+normal2.y*y, startPos.z+normal1.z*x+normal2.z*y, 1, m+l);
		tessellator.addVertexWithUV(endPos.x+normal1.x*x+normal2.x*y, endPos.y+normal1.y*x+normal2.y*y, endPos.z+normal1.z*x+normal2.z*y, 1, m);
		tessellator.addVertexWithUV(endPos.x+normal1.x*ox+normal2.x*oy, endPos.y+normal1.y*ox+normal2.y*oy, endPos.z+normal1.z*ox+normal2.z*oy,0, m);
	}
	
	/*private static void drawTube(double startPosX, double startPosY, double startPosZ, double endPosX, double endPosY, double endPosZ, double radius, int nums, float ty, Tessellator tessellator){
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
	}*/
	
}
