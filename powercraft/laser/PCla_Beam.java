package powercraft.laser;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import powercraft.api.PC_ClientUtils;
import powercraft.api.PC_MathHelper;
import powercraft.api.PC_Vec3;
import powercraft.api.PC_Vec3I;
import powercraft.api.beam.PC_BeamHitResult;
import powercraft.api.beam.PC_IBeam;
import powercraft.api.beam.PC_LightFilter;
import powercraft.api.beam.PC_LightValue;
import powercraft.laser.entity.PCla_LaserEntityFX;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class PCla_Beam implements PC_IBeam {
	
	private World world;
	private PCla_IBeamHandler handler;
	private PC_Vec3 startPos;
	private PC_Vec3 pos;
	private PC_Vec3 dir;
	private PC_LightValue lightValue;
	private double maxLength;
	private double startLength;
	private List<Entity> handledEntities;
	
	public PCla_Beam(World world, PCla_IBeamHandler handler, double maxLength, PC_Vec3 startPos, PC_Vec3 dir, PC_LightValue lightValue){
		this(world, handler, new ArrayList<Entity>(), maxLength, 0, startPos, dir, lightValue);
	}
	
	public PCla_Beam(World world, PCla_IBeamHandler handler, List<Entity> handledEntities, double maxLength, double startLength, PC_Vec3 startPos, PC_Vec3 dir, PC_LightValue lightValue){
		this.world = world;
		this.handler = handler;
		this.handledEntities = handledEntities;
		this.maxLength = maxLength;
		this.startLength = startLength;
		this.startPos = startPos;
		this.pos = new PC_Vec3(startPos);
		this.dir = dir.normalize();
		this.lightValue = lightValue;
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
	public PC_LightValue getLightValue(){
		return this.lightValue;
	}
	
	@Override
	public PC_Vec3 getColor() {
		return this.lightValue.toColor();
	}
	
	@Override
	public double getLength() {
		return this.startPos.distanceTo(this.pos)+this.startLength;
	}

	@SuppressWarnings("hiding")
	@Override
	public PC_IBeam getNewBeam(double maxLength, PC_Vec3 startPos, PC_Vec3 newDirection, PC_LightFilter filter) {
		PC_LightValue lv = this.lightValue.filterBy(filter);
		double nml = maxLength<0?this.maxLength:maxLength;
		if(lv==null || nml<=0)
			return null;
		return new PCla_Beam(this.world, this.handler, this.handledEntities, nml, getLength(), startPos==null?this.pos:startPos, newDirection==null?this.dir:newDirection, lv);
	}
	
	public void trace() {
		while(getLength()<this.maxLength)
			if(!nextStep())
				break;
		if(getLength()>this.maxLength){
			this.pos = this.pos.sub(this.startPos).normalize().mul(this.maxLength-this.startLength).add(this.startPos);
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
		boolean toLong = getLength()>this.maxLength;
		if(toLong){
			this.pos = this.pos.sub(this.startPos).normalize().mul(this.maxLength-this.startLength).add(this.startPos);
		}
		List<Entity> entities = this.world.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(this.pos.x, this.pos.y, this.pos.z, this.pos.x, this.pos.y, this.pos.z).expand(0.6, 0.6, 0.6));
		PC_BeamHitResult result;
		boolean stop = false;
		Vec3 v3pos = Vec3.createVectorHelper(this.pos.x, this.pos.y, this.pos.z);
		Vec3 v3dir = Vec3.createVectorHelper(this.dir.x, this.dir.y, this.dir.z);
		for(Entity entity:entities){
			if(!this.handledEntities.contains(entity)){
				double expand = 0.2;
				if(entity instanceof EntityItem){
					expand = 0.4;
				}
				AxisAlignedBB aabb = entity.boundingBox.expand(expand, expand, expand);
				if(aabb.isVecInside(v3pos) || aabb.calculateIntercept(v3pos, v3dir)!=null){
					this.handledEntities.add(entity);
					result = PCla_Beams.onHitEntity(this.world, entity, this);
					switch(result){
					case CONTINUE:
						break;
					case INTERACT:
					case STANDARD:
						stop |= !this.handler.onHitEntity(this.world, entity, this);
						break;
					case STOP:
					default:
						stop = true;
						break;
					}
				}
			}
		}
		if(stop || toLong)
			return false;
		PC_Vec3 blockPos = this.pos.add(add);
		PC_Vec3I blockPosI = new PC_Vec3I(PC_MathHelper.floor_double(blockPos.x), PC_MathHelper.floor_double(blockPos.y), PC_MathHelper.floor_double(blockPos.z));
		result = PCla_Beams.onHitBlock(this.world, blockPosI.x, blockPosI.y, blockPosI.z, this);
		switch(result){
		case CONTINUE:
			return true;
		case INTERACT:
		case STANDARD:
			return this.handler.onHitBlock(this.world, blockPosI.x, blockPosI.y, blockPosI.z, this);
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
		PC_ClientUtils.spawnParicle(new PCla_LaserEntityFX(this.world, this.startPos, this.pos, getColor()));
	}

	public void onFinished() {
		this.handler.onFinished(this);
	}
	
}
