package powercraft.laser;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import powercraft.api.PC_ClientUtils;
import powercraft.api.PC_MathHelper;
import powercraft.api.PC_Vec3;
import powercraft.api.beam.PC_BeamHitResult;
import powercraft.api.beam.PC_IBeam;
import powercraft.laser.entity.PCla_LaserEntityFX;


public class PCla_Beam implements PC_IBeam {
	
	private World world;
	private PCla_IBeamHandler handler;
	private PC_Vec3 startPos;
	private PC_Vec3 pos;
	private PC_Vec3 dir;
	private PC_Vec3 color;
	private double maxLength=20;
	
	public PCla_Beam(World world, PCla_IBeamHandler handler, PC_Vec3 startPos, PC_Vec3 dir, PC_Vec3 color){
		this.world = world;
		this.handler = handler;
		this.startPos = startPos;
		this.pos = new PC_Vec3(startPos);
		this.dir = dir.normalize();
		this.color = color;
		PCla_Beams.addBeam(this);
	}
	
	@Override
	public PC_Vec3 getDirection() {
		return this.dir;
	}
	
	@Override
	public PC_Vec3 getPosition() {
		return this.pos;
	}
	
	@Override
	public void setPosition(PC_Vec3 pos) {
		this.pos = pos;
	}
	
	@Override
	public PC_Vec3 getColor() {
		return this.color;
	}
	
	@SuppressWarnings("hiding")
	@Override
	public PC_IBeam getNewBeam(PC_Vec3 startPos, PC_Vec3 newDirection, PC_Vec3 newColor) {
		return new PCla_Beam(this.world, this.handler, startPos==null?this.pos:startPos, newDirection==null?this.dir:newDirection, newColor==null?this.color:onRecolor(newColor));
	}

	private PC_Vec3 onRecolor(PC_Vec3 newColor){
		return this.handler.onRecolor(newColor, this);
	}
	
	public void trace() {
		while(this.startPos.distanceTo(this.pos)<this.maxLength)
			if(!nextStep())
				break;
		if(this.startPos.distanceTo(this.pos)>this.maxLength){
			this.pos = this.pos.sub(this.startPos).normalize().mul(this.maxLength).add(this.startPos);
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean nextStep(){
		PC_Vec3 add = new PC_Vec3(this.dir.x>0?0.5:-0.5, 0, 0);
		double l = getCollideLength(this.pos.x, this.dir.x);
		double ll = getCollideLength(this.pos.y, this.dir.y);
		if((ll<l && ll>0)||l<=0){
			l=ll;
			add.x = 0;
			add.y = this.dir.y>0?0.5:-0.5;
		}
		ll = getCollideLength(this.pos.z, this.dir.z);
		if((ll<l && ll>0)||l<=0){
			l=ll;
			add.x = 0;
			add.y = 0;
			add.z = this.dir.z>0?0.5:-0.5;
		}
		if(Double.isInfinite(l) || l<=0)
			return false;
		this.pos = this.pos.add(this.dir.mul(l));
		if(add.x!=0){
			this.pos.x = (int)(this.pos.x+0.5);
		}else if(add.y!=0){
			this.pos.y = (int)(this.pos.y+0.5);
		}else if(add.z!=0){
			this.pos.z = (int)(this.pos.z+0.5);
		}
		PC_Vec3 blockPos = this.pos.add(add);
		List<Entity> entities = this.world.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(this.pos.x, this.pos.y, this.pos.z, this.pos.x, this.pos.y, this.pos.z).expand(0.5, 0.5, 0.5));
		PC_BeamHitResult result;
		boolean stop = false;
		for(Entity entity:entities){
			result = PCla_Beams.onHitEntity(this.world, entity, this);
			switch(result){
			case CONTINUE:
				break;
			case INTERACT:
			case STANDART:
				stop |= !this.handler.onHitEntity(this.world, entity, this);
				break;
			case STOP:
			default:
				stop = true;
				break;
			}
		}
		if(stop)
			return false;
		result = PCla_Beams.onHitBlock(this.world, (int)blockPos.x, (int)blockPos.y, (int)blockPos.z, this);
		switch(result){
		case CONTINUE:
			return true;
		case INTERACT:
		case STANDART:
			return this.handler.onHitBlock(this.world, (int)blockPos.x, (int)blockPos.y, (int)blockPos.z, this);
		case STOP:
		default:
			break;
		}
		return false;
	}
	
	private static double getCollideLength(double pos, double dir){
		if(dir==0)
			return Double.POSITIVE_INFINITY;
		int stop = PC_MathHelper.floor_double(pos);
		if(dir>0){
			stop++;
		}
		if(stop==pos){
			stop--;
		}
		return (stop-pos)/dir;
	}

	@SideOnly(Side.CLIENT)
	public void generate() {
		PC_ClientUtils.spawnParicle(new PCla_LaserEntityFX(this.world, this.startPos, this.pos, this.color));
	}
	
}
