package powercraft.teleporter;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import powercraft.api.PC_Utils;


public final class PCtp_Teleport {

	private PCtp_Teleport(){
		PC_Utils.staticClassConstructor();
	}
	
	public static void teleport(Entity entity, double x, double y, double z, float rot, int dimension){
		World world = entity.worldObj;
		if(world.isRemote)
			return;
		moveToDimension(entity, dimension);
		if(entity instanceof EntityPlayerMP){
            EntityPlayerMP player = (EntityPlayerMP)entity;
            player.playerNetServerHandler.setPlayerLocation(x + 0.5, y + 0.1, z + 0.5, entity.rotationYaw+rot, entity.rotationPitch);
            player.fallDistance = 0.0F;
		}else{
	        entity.setPositionAndRotation(x + 0.5, y + 0.1, z + 0.5, entity.rotationYaw+rot, entity.rotationPitch);
		}
		entity.motionX = 0;
		entity.motionY = 0;
		entity.motionZ = 0;
	}
	
	private static void moveToDimension(Entity entity, int dimension){
		if(PC_Utils.getDimensionID(entity.worldObj)==dimension){
			return;
		}
		entity.travelToDimension(dimension);
	}
	
}
