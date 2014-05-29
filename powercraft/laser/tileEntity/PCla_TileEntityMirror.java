package powercraft.laser.tileEntity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Field;
import powercraft.api.PC_Field.Flag;
import powercraft.api.PC_Utils;
import powercraft.api.PC_Vec3;
import powercraft.api.beam.PC_BeamHitResult;
import powercraft.api.beam.PC_IBeam;
import powercraft.api.block.PC_TileEntity;
import powercraft.api.renderer.PC_Renderer;
import powercraft.laser.block.PCla_BlockMirror;


public class PCla_TileEntityMirror extends PC_TileEntity {

	@PC_Field(flags={Flag.SAVE, Flag.SYNC})
	protected PC_Vec3 normal;
	@PC_Field(flags={Flag.SAVE, Flag.SYNC})
	protected PC_Direction placing;
	
	@Override
	public void onLoadedFromNBT(Flag flag) {
		if(flag==Flag.SYNC)
			renderUpdate();
	}

	@Override
	public void onBlockPostSet(PC_Direction side, ItemStack stack, EntityPlayer player, float hitX, float hitY, float hitZ) {
		this.normal = PC_Utils.getLookDir(player);
		this.placing = side;
		sync();
	}

	@Override
	public PC_BeamHitResult onHitByBeam(PC_IBeam beam) {
		if(this.normal!=null){
			beam.setPosition(beam.getPosition().add(beam.getDirection().mul(0.5)));
			PC_Vec3 dir = beam.getDirection();
			PC_Vec3 result = dir.sub(this.normal.mul(dir.dot(this.normal)*2));
			beam.getNewBeam(-1, new PC_Vec3(this.xCoord+0.5, this.yCoord+0.5, this.zCoord+0.5), result, null);
		}
		return PC_BeamHitResult.STOP;
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, PC_Direction side) {
		this.normal = PC_Utils.getLookDir(player);
		sync();
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean renderWorldBlock(int modelId, RenderBlocks renderer) {
		IIcon[] icons = new IIcon[6];
		double minX = this.placing==PC_Direction.WEST?15/16.0:this.placing==PC_Direction.EAST?0:5/16.0;
		double minY = this.placing==PC_Direction.DOWN?15/16.0:this.placing==PC_Direction.UP?0:5/16.0;
		double minZ = this.placing==PC_Direction.NORTH?15/16.0:this.placing==PC_Direction.SOUTH?0:5/16.0;
		double maxX = this.placing==PC_Direction.WEST?1:this.placing==PC_Direction.EAST?1/16.0:11/16.0;
		double maxY = this.placing==PC_Direction.DOWN?1:this.placing==PC_Direction.UP?1/16.0:11/16.0;
		double maxZ = this.placing==PC_Direction.NORTH?1:this.placing==PC_Direction.SOUTH?1/16.0:11/16.0;
		for(int i=0; i<6; i++){
			icons[i] = PCla_BlockMirror.black;
		}
		renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
		PC_Renderer.renderStandardBlockInWorld(this.worldObj, this.xCoord, this.yCoord, this.zCoord, icons, -1, 0, renderer);
		
		Tessellator tessellator = Tessellator.instance;
		
		PC_Vec3 d = this.normal;
		
		PC_Vec3 n1;
		if(this.placing.offsetX!=0){
			if(d.x==0){
				n1 = new PC_Vec3(1, 0, 0);
			}else if(d.y==0 && d.z==0){
				n1 = new PC_Vec3(0, 1, 0);
			}else{
				n1 = new PC_Vec3((d.y*d.y+d.z*d.z)/d.x, -d.y, -d.z).normalize();
			}
		}else if(this.placing.offsetY!=0){
			if(d.y==0){
				n1 = new PC_Vec3(0, 1, 0);
			}else if(d.x==0 && d.z==0){
				n1 = new PC_Vec3(1, 0, 0);
			}else{
				n1 = new PC_Vec3(-d.x, (d.x*d.x+d.z*d.z)/d.y, -d.z).normalize();
			}
		}else if(this.placing.offsetZ!=0){
			if(d.z==0){
				n1 = new PC_Vec3(0, 0, 1);
			}else if(d.x==0 && d.y==0){
				n1 = new PC_Vec3(1, 0, 0);
			}else{
				n1 = new PC_Vec3(-d.x, -d.y, (d.x*d.x+d.y*d.y)/d.z).normalize();
			}
		}else{
			return true;
		}
		PC_Vec3 n2 = d.cross(n1).mul(0.5);
		n1 = n1.mul(0.5);
		
		//tessellator.addVertexWithUV(par1, par3, par5, par7, par9);
		
		return true;
	}
	
}
