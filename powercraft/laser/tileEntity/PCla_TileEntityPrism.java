package powercraft.laser.tileEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.block.BlockPane;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Field;
import powercraft.api.PC_Field.Flag;
import powercraft.api.PC_INBT;
import powercraft.api.PC_Utils;
import powercraft.api.PC_Vec3;
import powercraft.api.beam.PC_BeamHitResult;
import powercraft.api.beam.PC_IBeam;
import powercraft.api.block.PC_TileEntity;
import powercraft.api.renderer.PC_Renderer;
import powercraft.laser.block.PCla_BlockPrism;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class PCla_TileEntityPrism extends PC_TileEntity {

	@PC_Field(flags={Flag.SAVE, Flag.SYNC})
	private Lenses[] lenses;
	
	public static class Lenses implements PC_INBT{

		int meta = -1;
		PC_Vec3 dir;
		
		Lenses(PC_Vec3 dir){
			this.dir = dir;
		}
		
		Lenses(PC_Vec3 dir, int meta) {
			this.dir = dir;
			this.meta = meta;
		}
		
		public Lenses(NBTTagCompound tag, Flag flag){
			this.meta = tag.getInteger("meta");
			this.dir = new PC_Vec3(tag, flag);
		}

		@Override
		public void saveToNBT(NBTTagCompound tag, Flag flag) {
			tag.setInteger("meta", this.meta);
			this.dir.saveToNBT(tag, flag);
		}
		
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player, PC_Direction side) {
		PC_Vec3 dir = PC_Utils.getLookDir(player).mul(-1);
		Lenses best = null;
		double diff = 0.4;
		if(this.lenses!=null){
			for(Lenses lense:this.lenses){
				double dist = lense.dir.distanceTo(dir);
				if(dist<diff){
					diff = dist;
					best = lense;
				}
			}
		}
		if(best==null){
			ItemStack is = player.getCurrentEquippedItem();
			if(is!=null){
				if(is.getItem()==PC_Utils.getItemForBlock(Blocks.glass_pane)){
					if(!isClient()){
						List<Lenses> l = this.lenses==null?new ArrayList<Lenses>():new ArrayList<Lenses>(Arrays.asList(this.lenses));
						l.add(new Lenses(dir));
						this.lenses = l.toArray(new Lenses[l.size()]);
					}
					sync();
					return true;
				}else if(is.getItem()==PC_Utils.getItemForBlock(Blocks.stained_glass_pane)){
					if(!isClient()){
						List<Lenses> l = this.lenses==null?new ArrayList<Lenses>():new ArrayList<Lenses>(Arrays.asList(this.lenses));
						l.add(new Lenses(dir, is.getItemDamage()));
						this.lenses = l.toArray(new Lenses[l.size()]);
					}
					sync();
					return true;
				}
			}
		}else{
			if(!isClient()){
				List<Lenses> l = new ArrayList<Lenses>(Arrays.asList(this.lenses));
				l.remove(best);
				this.lenses = l.toArray(new Lenses[l.size()]);
				ItemStack is;
				if(best.meta==-1){
					is = new ItemStack(Blocks.glass_pane);
				}else{
					is = new ItemStack(Blocks.stained_glass_pane, 1, best.meta);
				}
				EntityItem entityitem = new EntityItem(this.worldObj, this.xCoord+0.5+best.dir.x*0.5, this.yCoord+0.5+best.dir.y*0.5, this.zCoord+0.5+best.dir.z*0.5, is);
				entityitem.delayBeforeCanPickup = 10;
				PC_Utils.spawnEntity(this.worldObj, entityitem);
				sync();
			}
			return true;
		}
		return false;
	}
	
	@Override
	public void onLoadedFromNBT(Flag flag) {
		if(flag==Flag.SYNC){
			renderUpdate();
		}
	}

	@Override
	public void onBreak() {
		super.onBreak();
		if(!isClient() && this.lenses!=null){
			List<ItemStack> list = new ArrayList<ItemStack>();
			for(Lenses lense:this.lenses){
				ItemStack is;
				if(lense.meta==-1){
					is = new ItemStack(Blocks.glass_pane);
				}else{
					is = new ItemStack(Blocks.stained_glass_pane, 1, lense.meta);
				}
				list.add(is);
			}
			PC_Utils.spawnItems(this.worldObj, this.xCoord, this.yCoord, this.zCoord, list);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean renderWorldBlock(int modelId, RenderBlocks renderer) {
		IIcon[] icons = new IIcon[6];
		for(int i=0; i<6; i++){
			icons[i] = PCla_BlockPrism.side;
		}
		renderer.setRenderBounds(3/16.0, 3/16.0, 3/16.0, 13/16.0, 13/16.0, 13/16.0);
		PC_Renderer.renderStandardBlockInWorld(this.worldObj, this.xCoord, this.yCoord, this.zCoord, icons, -1, 0, renderer);
		for(int i=0; i<6; i++){
			icons[i] = PCla_BlockPrism.side2;
		}
		icons[0] = PCla_BlockPrism.side3;
		icons[1] = PCla_BlockPrism.side3;
		renderer.setRenderBounds(4/16.0, 2/16.0, 4/16.0, 12/16.0, 14/16.0, 12/16.0);
		PC_Renderer.renderStandardBlockInWorld(this.worldObj, this.xCoord, this.yCoord, this.zCoord, icons, -1, 0, renderer);
		icons[0] = PCla_BlockPrism.side2;
		icons[1] = PCla_BlockPrism.side2;
		icons[2] = PCla_BlockPrism.side3;
		icons[3] = PCla_BlockPrism.side3;
		renderer.setRenderBounds(4/16.0, 4/16.0, 2/16.0, 12/16.0, 12/16.0, 14/16.0);
		PC_Renderer.renderStandardBlockInWorld(this.worldObj, this.xCoord, this.yCoord, this.zCoord, icons, -1, 0, renderer);
		icons[2] = PCla_BlockPrism.side2;
		icons[3] = PCla_BlockPrism.side2;
		icons[4] = PCla_BlockPrism.side3;
		icons[5] = PCla_BlockPrism.side3;
		renderer.setRenderBounds(2/16.0, 4/16.0, 4/16.0, 14/16.0, 12/16.0, 12/16.0);
		PC_Renderer.renderStandardBlockInWorld(this.worldObj, this.xCoord, this.yCoord, this.zCoord, icons, -1, 0, renderer);
		Tessellator tessellator = Tessellator.instance;
		if(this.lenses!=null){
			for(Lenses lense:this.lenses){
				IIcon i1;
				IIcon i2;
				if(lense.meta==-1){
					i1 = Blocks.glass_pane.getIcon(0, 0);
					i2 = ((BlockPane)Blocks.glass_pane).func_150097_e();
				}else{
					i1 = Blocks.stained_glass_pane.getIcon(0, lense.meta);
					i2 = Blocks.stained_glass_pane.func_150104_b(lense.meta);
				}
				PC_Vec3 d = lense.dir;
				tessellator.setNormal((float)d.x, (float)d.y, (float)d.z);
				PC_Vec3 n1;
				if(d.y==0){
					n1 = new PC_Vec3(0, 1, 0);
				}else if(d.x==0 && d.z==0){
					n1 = new PC_Vec3(1, 0, 0);
				}else{
					n1 = new PC_Vec3(-d.x, (d.x*d.x+d.z*d.z)/d.y, -d.z).normalize();
				}
				PC_Vec3 n2 = d.cross(n1).mul(0.2);
				PC_Vec3 p = d.mul(0.4).add(new PC_Vec3(this.xCoord+0.5, this.yCoord+0.5, this.zCoord+0.5));
				PC_Vec3 p1 = p.sub(d.mul(0.03));
				n1 = n1.mul(0.2);
				double u1 = i1.getMinU();
				double u2 = i1.getMaxU();
				double v1 = i1.getMinV();
				double v2 = i1.getMaxV();
				tessellator.addVertexWithUV(p.x+n1.x+n2.x, p.y+n1.y+n2.y, p.z+n1.z+n2.z, u1, v1);
				tessellator.addVertexWithUV(p.x-n1.x+n2.x, p.y-n1.y+n2.y, p.z-n1.z+n2.z, u2, v1);
				tessellator.addVertexWithUV(p.x-n1.x-n2.x, p.y-n1.y-n2.y, p.z-n1.z-n2.z, u2, v2);
				tessellator.addVertexWithUV(p.x+n1.x-n2.x, p.y+n1.y-n2.y, p.z+n1.z-n2.z, u1, v2);
				
				tessellator.addVertexWithUV(p1.x+n1.x+n2.x, p1.y+n1.y+n2.y, p1.z+n1.z+n2.z, u1, v1);
				tessellator.addVertexWithUV(p1.x+n1.x-n2.x, p1.y+n1.y-n2.y, p1.z+n1.z-n2.z, u1, v2);
				tessellator.addVertexWithUV(p1.x-n1.x-n2.x, p1.y-n1.y-n2.y, p1.z-n1.z-n2.z, u2, v2);
				tessellator.addVertexWithUV(p1.x-n1.x+n2.x, p1.y-n1.y+n2.y, p1.z-n1.z+n2.z, u2, v1);
				
				u1 = i2.getInterpolatedU(7);
				u2 = i2.getInterpolatedU(9);
				v1 = i2.getMinV();
				v2 = i2.getMaxV();
				
				tessellator.addVertexWithUV(p.x+n1.x+n2.x, p.y+n1.y+n2.y, p.z+n1.z+n2.z, u1, v1);
				tessellator.addVertexWithUV(p1.x+n1.x+n2.x, p1.y+n1.y+n2.y, p1.z+n1.z+n2.z, u2, v1);
				tessellator.addVertexWithUV(p1.x-n1.x+n2.x, p1.y-n1.y+n2.y, p1.z-n1.z+n2.z, u2, v2);
				tessellator.addVertexWithUV(p.x-n1.x+n2.x, p.y-n1.y+n2.y, p.z-n1.z+n2.z, u1, v2);
				
				tessellator.addVertexWithUV(p.x+n1.x-n2.x, p.y+n1.y-n2.y, p.z+n1.z-n2.z, u1, v1);
				tessellator.addVertexWithUV(p.x-n1.x-n2.x, p.y-n1.y-n2.y, p.z-n1.z-n2.z, u1, v2);
				tessellator.addVertexWithUV(p1.x-n1.x-n2.x, p1.y-n1.y-n2.y, p1.z-n1.z-n2.z, u2, v2);
				tessellator.addVertexWithUV(p1.x+n1.x-n2.x, p1.y+n1.y-n2.y, p1.z+n1.z-n2.z, u2, v1);
				
				tessellator.addVertexWithUV(p.x+n1.x+n2.x, p.y+n1.y+n2.y, p.z+n1.z+n2.z, u1, v1);
				tessellator.addVertexWithUV(p.x+n1.x-n2.x, p.y+n1.y-n2.y, p.z+n1.z-n2.z, u1, v2);
				tessellator.addVertexWithUV(p1.x+n1.x-n2.x, p1.y+n1.y-n2.y, p1.z+n1.z-n2.z, u2, v2);
				tessellator.addVertexWithUV(p1.x+n1.x+n2.x, p1.y+n1.y+n2.y, p1.z+n1.z+n2.z, u2, v1);
				
				tessellator.addVertexWithUV(p.x-n1.x+n2.x, p.y-n1.y+n2.y, p.z-n1.z+n2.z, u1, v1);
				tessellator.addVertexWithUV(p1.x-n1.x+n2.x, p1.y-n1.y+n2.y, p1.z-n1.z+n2.z, u2, v1);
				tessellator.addVertexWithUV(p1.x-n1.x-n2.x, p1.y-n1.y-n2.y, p1.z-n1.z-n2.z, u2, v2);
				tessellator.addVertexWithUV(p.x-n1.x-n2.x, p.y-n1.y-n2.y, p.z-n1.z-n2.z, u1, v2);
			}
		}
		return true;
	}

	@Override
	public PC_BeamHitResult onHitByBeam(PC_IBeam beam) {
		if(this.lenses==null || this.lenses.length==0)
			return PC_BeamHitResult.CONTINUE;
		beam.setPosition(beam.getPosition().add(beam.getDirection().mul(0.5)));
		double length = beam.getRemainingLength();
		length /= this.lenses.length;
		for(Lenses lense:this.lenses){
			beam.getNewBeam(beam.getLength()+length, new PC_Vec3(this.xCoord+0.5, this.yCoord+0.5, this.zCoord+0.5), lense.dir, null);
		}
		return PC_BeamHitResult.STOP;
	}
	
}
