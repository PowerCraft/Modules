package powercraft.transport.block;

import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import powercraft.api.PC_Utils;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.recipes.PC_Recipes;
import powercraft.transport.PCtr_BeltHelper;

public class PCtr_BlockBeltBidirectional extends PCtr_BlockBeltNormal {
	
	public PCtr_BlockBeltBidirectional() {
		super();
		PC_Recipes.addShapedRecipe(new ItemStack(this, 16, 0), "LLL", "GRG", Character.valueOf('G'), Items.gold_ingot, Character.valueOf('L'), Items.leather, Character.valueOf('R'), Items.redstone);
	}
	
	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		return PCtr_BeltHelper.hasValidGround(world, x, y, z);
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		if(PCtr_BeltHelper.isEntityIgnored(entity))
			return;
		if(!(Math.floor(entity.posX)==x && Math.floor(entity.posY)==y && Math.floor(entity.posZ)==z)){
			return;
		}
		int diff = PCtr_BeltHelper.combineEntityItems(entity)?2:1;
		if(world.isRemote){
			PCtr_BeltHelper.handleEntity(entity, world, x, y, z, false, true, false);
			return;
		}
		NBTTagCompound compound = PC_Utils.getWritableNBTTagOf(entity);
		if(compound.hasKey("dir")){
			int xx = compound.getInteger("lastX");
			int yy = compound.getInteger("lastY");
			int zz = compound.getInteger("lastZ");
			int lastTick = compound.getInteger("lastTick");
			if(lastTick==entity.ticksExisted)
				return;
			if(x==xx && y==yy && z==zz && (lastTick==entity.ticksExisted-diff || lastTick==entity.ticksExisted-1 || entity.ticksExisted==0)){
				if(!PCtr_BeltHelper.handleEntity(entity, world, x, y, z, false, true, false)){
					compound.setInteger("lastTick", entity.ticksExisted);
					return;
				}
			}
		}
		int prevDir=PC_Utils.getEntityMovement2D(entity).ordinal();
		compound.setInteger("dir", prevDir);
		PC_PacketHandler.sendToAllAround(new PCtr_PacketSetEntitySpeed(compound, entity), world, x, y, z, 16);
		compound.setInteger("lastX", x);
		compound.setInteger("lastY", y);
		compound.setInteger("lastZ", z);
		compound.setInteger("lastTick", entity.ticksExisted);
		PCtr_BeltHelper.handleEntity(entity, world, x, y, z, false, true, false);
	}
	
	@Override
	protected int calcHill(World world, int x, int y, int z, int metadata){
		return 0;
	}
	
}
