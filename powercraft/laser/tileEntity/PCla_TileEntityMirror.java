package powercraft.laser.tileEntity;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import powercraft.api.PC_CtrlPressed;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Field;
import powercraft.api.PC_Field.Flag;
import powercraft.api.PC_MathHelper;
import powercraft.api.PC_Utils;
import powercraft.api.PC_Vec3;
import powercraft.api.beam.PC_BeamHitResult;
import powercraft.api.beam.PC_IBeam;
import powercraft.api.block.PC_TileEntity;
import powercraft.api.renderer.PC_Renderer;
import powercraft.laser.block.PCla_BlockMirror;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PCla_TileEntityMirror extends PC_TileEntity {

	@PC_Field(flags = {Flag.SAVE, Flag.SYNC})
	protected PC_Vec3 normal;
	@PC_Field(flags = {Flag.SAVE, Flag.SYNC})
	protected PC_Direction placing;

	private boolean hitBack;
	
	@Override
	public void onLoadedFromNBT(Flag flag) {
		if(flag == Flag.SYNC)
			renderUpdate();
	}

	@Override
	public void onBlockPostSet(PC_Direction side, ItemStack stack, EntityPlayer player, float hitX, float hitY, float hitZ) {
		this.normal = PC_Utils.getLookDir(player);
		if(PC_CtrlPressed.isPressingCtrl(player)){
			this.normal = PC_MathHelper.to45Angles(this.normal);
		}
		this.placing = side;
		sync();
	}

	@Override
	public PC_BeamHitResult onHitByBeam(PC_IBeam beam) {
		if(this.normal != null) {
			this.hitBack = false;
			PC_Vec3 coll = vv(beam.getPosition(), beam.getDirection());
			if(coll==null)
				return PC_BeamHitResult.CONTINUE;
			beam.setPosition(coll);
			if(this.hitBack)
				return PC_BeamHitResult.STOP;
			PC_Vec3 dir = beam.getDirection();
			PC_Vec3 result = dir.sub(this.normal.mul(dir.dot(this.normal) * 2));
			beam.getNewBeam( -1, null, result, null);

		}
		return PC_BeamHitResult.STOP;
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, PC_Direction side) {
		this.normal = PC_Utils.getLookDir(player);
		if(PC_CtrlPressed.isPressingCtrl(player)){
			this.normal = PC_MathHelper.to45Angles(this.normal);
		}
		PC_Vec3 d = new PC_Vec3(this.placing.offsetX, this.placing.offsetY, this.placing.offsetZ);
		if(this.normal.dot(d)>0){
			if(this.placing.offsetX!=0)
				this.normal.x=0;
			if(this.placing.offsetY!=0)
				this.normal.y=0;
			if(this.placing.offsetZ!=0)
				this.normal.z=0;
			if(this.normal.equals(new PC_Vec3(0, 0, 0))){
				this.normal = d;
			}
			this.normal = this.normal.normalize();
		}
		sync();
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean renderWorldBlock(int modelId, RenderBlocks renderer) {
		IIcon[] icons = new IIcon[6];
		double minX = this.placing == PC_Direction.WEST ? 15 / 16.0 : this.placing == PC_Direction.EAST ? 0 : 5 / 16.0;
		double minY = this.placing == PC_Direction.DOWN ? 15 / 16.0 : this.placing == PC_Direction.UP ? 0 : 5 / 16.0;
		double minZ = this.placing == PC_Direction.NORTH ? 15 / 16.0 : this.placing == PC_Direction.SOUTH ? 0 : 5 / 16.0;
		double maxX = this.placing == PC_Direction.WEST ? 1 : this.placing == PC_Direction.EAST ? 1 / 16.0 : 11 / 16.0;
		double maxY = this.placing == PC_Direction.DOWN ? 1 : this.placing == PC_Direction.UP ? 1 / 16.0 : 11 / 16.0;
		double maxZ = this.placing == PC_Direction.NORTH ? 1 : this.placing == PC_Direction.SOUTH ? 1 / 16.0 : 11 / 16.0;
		for(int i = 0; i < 6; i++ ) {
			icons[i] = PCla_BlockMirror.black;
		}
		renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
		PC_Renderer.renderStandardBlockInWorld(this.worldObj, this.xCoord, this.yCoord, this.zCoord, icons, -1, 0, renderer);

		minX = this.placing == PC_Direction.WEST ? 8 / 16.0 : this.placing == PC_Direction.EAST ? 1 / 16.0 : 7 / 16.0;
		minY = this.placing == PC_Direction.DOWN ? 8 / 16.0 : this.placing == PC_Direction.UP ? 1 / 16.0 : 7 / 16.0;
		minZ = this.placing == PC_Direction.NORTH ? 8 / 16.0 : this.placing == PC_Direction.SOUTH ? 1 / 16.0 : 7 / 16.0;
		maxX = this.placing == PC_Direction.WEST ? 15 / 16.0 : this.placing == PC_Direction.EAST ? 8 / 16.0 : 9 / 16.0;
		maxY = this.placing == PC_Direction.DOWN ? 15 / 16.0 : this.placing == PC_Direction.UP ? 8 / 16.0 : 9 / 16.0;
		maxZ = this.placing == PC_Direction.NORTH ? 15 / 16.0 : this.placing == PC_Direction.SOUTH ? 8 / 16.0 : 9 / 16.0;
		renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
		PC_Renderer.renderStandardBlockInWorld(this.worldObj, this.xCoord, this.yCoord, this.zCoord, icons, -1, 0, renderer);

		Tessellator tessellator = Tessellator.instance;

		PC_Vec3 d = this.normal;

		if(this.placing == null)
			return true;

		PC_Vec3 n1;
		if(d.y == 0) {
			n1 = new PC_Vec3(0, 1, 0);
		} else if(d.x == 0 && d.z == 0) {
			n1 = new PC_Vec3(1, 0, 0);
		} else {
			n1 = new PC_Vec3( -d.x, (d.x * d.x + d.z * d.z) / d.y, -d.z).normalize();
		}
		PC_Vec3 n2 = d.cross(n1).mul(7 / 16.0);
		n1 = n1.mul(7 / 16.0);

		PC_Vec3 p = new PC_Vec3(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5);
		PC_Vec3 p1 = p.sub(this.normal.mul(1 / 16.0));
		p = p.add(this.normal.mul(1 / 16.0));

		IIcon i = PCla_BlockMirror.mirror;

		double u1 = i.getInterpolatedU(1);
		double u2 = i.getInterpolatedU(15);
		double v1 = i.getInterpolatedV(1);
		double v2 = i.getInterpolatedV(15);

		tessellator.addVertexWithUV(p1.x + n1.x + n2.x, p1.y + n1.y + n2.y, p1.z + n1.z + n2.z, u1, v1);
		tessellator.addVertexWithUV(p1.x + n1.x - n2.x, p1.y + n1.y - n2.y, p1.z + n1.z - n2.z, u1, v2);
		tessellator.addVertexWithUV(p1.x - n1.x - n2.x, p1.y - n1.y - n2.y, p1.z - n1.z - n2.z, u2, v2);
		tessellator.addVertexWithUV(p1.x - n1.x + n2.x, p1.y - n1.y + n2.y, p1.z - n1.z + n2.z, u2, v1);

		i = PCla_BlockMirror.white;
		u1 = i.getMinU();
		u2 = i.getMaxU();
		v1 = i.getMinV();
		v2 = i.getMaxV();

		tessellator.addVertexWithUV(p.x + n1.x + n2.x, p.y + n1.y + n2.y, p.z + n1.z + n2.z, u1, v1);
		tessellator.addVertexWithUV(p.x - n1.x + n2.x, p.y - n1.y + n2.y, p.z - n1.z + n2.z, u2, v1);
		tessellator.addVertexWithUV(p.x - n1.x - n2.x, p.y - n1.y - n2.y, p.z - n1.z - n2.z, u2, v2);
		tessellator.addVertexWithUV(p.x + n1.x - n2.x, p.y + n1.y - n2.y, p.z + n1.z - n2.z, u1, v2);

		tessellator.addVertexWithUV(p.x + n1.x + n2.x, p.y + n1.y + n2.y, p.z + n1.z + n2.z, u1, v1);
		tessellator.addVertexWithUV(p1.x + n1.x + n2.x, p1.y + n1.y + n2.y, p1.z + n1.z + n2.z, u2, v1);
		tessellator.addVertexWithUV(p1.x - n1.x + n2.x, p1.y - n1.y + n2.y, p1.z - n1.z + n2.z, u2, v2);
		tessellator.addVertexWithUV(p.x - n1.x + n2.x, p.y - n1.y + n2.y, p.z - n1.z + n2.z, u1, v2);

		tessellator.addVertexWithUV(p.x + n1.x - n2.x, p.y + n1.y - n2.y, p.z + n1.z - n2.z, u1, v1);
		tessellator.addVertexWithUV(p.x - n1.x - n2.x, p.y - n1.y - n2.y, p.z - n1.z - n2.z, u1, v2);
		tessellator.addVertexWithUV(p1.x - n1.x - n2.x, p1.y - n1.y - n2.y, p1.z - n1.z - n2.z, u2, v2);
		tessellator.addVertexWithUV(p1.x + n1.x - n2.x, p1.y + n1.y - n2.y, p1.z + n1.z - n2.z, u2, v1);

		tessellator.addVertexWithUV(p.x + n1.x + n2.x, p.y + n1.y + n2.y, p.z + n1.z + n2.z, u1, v1);
		tessellator.addVertexWithUV(p.x + n1.x - n2.x, p.y + n1.y - n2.y, p.z + n1.z - n2.z, u1, v2);
		tessellator.addVertexWithUV(p1.x + n1.x - n2.x, p1.y + n1.y - n2.y, p1.z + n1.z - n2.z, u2, v2);
		tessellator.addVertexWithUV(p1.x + n1.x + n2.x, p1.y + n1.y + n2.y, p1.z + n1.z + n2.z, u2, v1);

		tessellator.addVertexWithUV(p.x - n1.x + n2.x, p.y - n1.y + n2.y, p.z - n1.z + n2.z, u1, v1);
		tessellator.addVertexWithUV(p1.x - n1.x + n2.x, p1.y - n1.y + n2.y, p1.z - n1.z + n2.z, u2, v1);
		tessellator.addVertexWithUV(p1.x - n1.x - n2.x, p1.y - n1.y - n2.y, p1.z - n1.z - n2.z, u2, v2);
		tessellator.addVertexWithUV(p.x - n1.x - n2.x, p.y - n1.y - n2.y, p.z - n1.z - n2.z, u1, v2);

		return true;
	}

	public PC_Vec3 vv(PC_Vec3 ls, PC_Vec3 ld) {
		PC_Vec3 n = this.normal;

		if(this.placing == null)
			return null;

		PC_Vec3 n1;
		if(n.y == 0) {
			n1 = new PC_Vec3(0, 1, 0);
		} else if(n.x == 0 && n.z == 0) {
			n1 = new PC_Vec3(1, 0, 0);
		} else {
			n1 = new PC_Vec3( -n.x, (n.x * n.x + n.z * n.z) / n.y, -n.z).normalize();
		}
		PC_Vec3 n2 = n.cross(n1);
		PC_Vec3 p = new PC_Vec3(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5).sub(this.normal.mul(1 / 16.0));

		// Calculated by Mathematica -> Check for Collision Ray<->Mirror
		
		double nn = -(n1.z * n2.y * ld.x - n1.y * n2.z * ld.x - n1.z * n2.x * ld.y + n1.x * n2.z * ld.y + n1.y * n2.x * ld.z - n1.x * n2.y * ld.z);

		if(nn == 0)
			return null;

		double u = (n2.z * ld.y * p.x - n2.y * ld.z * p.x - n2.z * ld.x * p.y + n2.x * ld.z * p.y + n2.y * ld.x * p.z - n2.x * ld.y * p.z - n2.z * ld.y * ls.x + n2.y * ld.z * ls.x + n2.z * ld.x * ls.y - n2.x * ld.z * ls.y - n2.y * ld.x * ls.z + n2.x * ld.y * ls.z) / nn;

		double v = ( -n1.z * ld.y * p.x + n1.y * ld.z * p.x + n1.z * ld.x * p.y - n1.x * ld.z * p.y - n1.y * ld.x * p.z + n1.x * ld.y * p.z + n1.z * ld.y * ls.x - n1.y * ld.z * ls.x - n1.z * ld.x * ls.y + n1.x * ld.z * ls.y + n1.y * ld.x * ls.z - n1.x * ld.y * ls.z) / nn;

		if(u<-7/16.0 || u>7/16.0 || v<-7/16.0 || v>7/16.0)
			return null;
		
		if(ld.dot(n)<0){
			this.hitBack = true;
		}
		
		return p.add(n1.mul(u)).add(n2.mul(v));
	}

	public PC_Direction getPlacing() {
		return this.placing;
	}

}
