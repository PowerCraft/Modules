package powercraft.transport.block.tileentity;

import java.util.HashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Utils;
import powercraft.api.block.PC_TileEntityScriptable;
import powercraft.api.network.PC_PacketHandler;
import powercraft.transport.block.PCtr_PacketSetEntitySpeed;

public class PCtr_TileEntityScriptableBelt extends PC_TileEntityScriptable {
	
	private static HashMap<String, Integer> replacements = new HashMap<String, Integer>();
	
	public static final int EXT_OUT_FRONT_COUNT = 0;
	public static final int EXT_OUT_RIGHT_COUNT = 1;
	public static final int EXT_OUT_BACK_COUNT = 2;
	public static final int EXT_OUT_LEFT_COUNT = 3;
	public static final int EXT_IN_OUT_DIR = 4;
	public static final int EXT_IN_TYPE = 5;
	public static final int EXT_IN_ID = 6;
	public static final int EXT_IN_ITEMDAMAGE = 7;
	public static final int EXT_IN_ITEMSTACKSIZE = 8;
	//Entity Type
	public static final int TYPE_ITEMSTACK = 0;
	
	public static final int DIR_NORTH = 0;
	public static final int DIR_EAST = 1;
	public static final int DIR_SOUTH = 2;
	public static final int DIR_WEST = 3;
	
	private static final PC_Direction[] DIR_TO_PCDIR = {PC_Direction.NORTH, PC_Direction.EAST, PC_Direction.SOUTH, PC_Direction.WEST};
	
	static{
		replacements.put("front", EXT_OUT_FRONT_COUNT);
		replacements.put("right", EXT_OUT_RIGHT_COUNT);
		replacements.put("back", EXT_OUT_BACK_COUNT);
		replacements.put("left", EXT_OUT_LEFT_COUNT);
		replacements.put("dir", EXT_IN_OUT_DIR);
		replacements.put("type", EXT_IN_TYPE);
		replacements.put("id", EXT_IN_ID);
		replacements.put("itemdamage", EXT_IN_ITEMDAMAGE);
		replacements.put("itemstacksize", EXT_IN_ITEMSTACKSIZE);
		
		replacements.put("item", TYPE_ITEMSTACK);
		
		replacements.put("north", DIR_NORTH);
		replacements.put("east", DIR_EAST);
		replacements.put("south", DIR_SOUTH);
		replacements.put("west", DIR_WEST);
	}
	
	public PCtr_TileEntityScriptableBelt(){
		super(16);
		setSource("mov [frontcount], 1");
	}
	
	private void moveEntity(Entity entity){
		NBTTagCompound compound = PC_Utils.getNBTTagOf(entity);
		PC_Direction dir = DIR_TO_PCDIR[(compound.getInteger("dir")%4+4)%4];
		entity.motionX = dir.offsetX*0.2;
		entity.motionZ = dir.offsetZ*0.2;
		
	}
	
	@Override
	public void onEntityCollidedWithBlock(Entity entity) {
		if(!(Math.floor(entity.posX)==xCoord && Math.floor(entity.posY)==yCoord && Math.floor(entity.posZ)==zCoord)){
			return;
		}
		if(worldObj.isRemote){
			moveEntity(entity);
			return;
		}
		NBTTagCompound compound = PC_Utils.getNBTTagOf(entity);
		int x = compound.getInteger("lastX");
		int y = compound.getInteger("lastY");
		int z = compound.getInteger("lastZ");
		if(x==xCoord && y==yCoord && z==zCoord){
			moveEntity(entity);
			return;
		}
		compound.setInteger("lastX", xCoord);
		compound.setInteger("lastY", yCoord);
		compound.setInteger("lastZ", zCoord);
		int[] ext = getExt();
		ext[EXT_OUT_FRONT_COUNT] = 0;
		ext[EXT_OUT_RIGHT_COUNT] = 0;
		ext[EXT_OUT_BACK_COUNT] = 0;
		ext[EXT_OUT_LEFT_COUNT] = 0;
		switch(PC_Utils.getEntityMovement2D(entity)){
		case EAST:
			ext[EXT_IN_OUT_DIR] = DIR_EAST;
			break;
		case NORTH:
			ext[EXT_IN_OUT_DIR] = DIR_NORTH;
			break;
		case SOUTH:
			ext[EXT_IN_OUT_DIR] = DIR_SOUTH;
			break;
		case WEST:
			ext[EXT_IN_OUT_DIR] = DIR_WEST;
			break;
		default:
			return;
		}
		if(entity instanceof EntityItem){
			ItemStack is = ((EntityItem) entity).getEntityItem();
			ext[EXT_IN_TYPE] = TYPE_ITEMSTACK;
			ext[EXT_IN_ID] = Item.getIdFromItem(is.getItem());
			ext[EXT_IN_ITEMDAMAGE] = is.getItemDamage();
			ext[EXT_IN_ITEMSTACKSIZE] = is.stackSize;
		}else{
			return;
		}
		invoke();
		int direction = ext[EXT_IN_OUT_DIR];
		compound.setInteger("dir", (direction%4+4)%4);
		moveEntity(entity);
		PC_PacketHandler.sendToAllAround(new PCtr_PacketSetEntitySpeed(compound, entity.getEntityId()), worldObj.getWorldInfo().getVanillaDimension(), xCoord, yCoord, zCoord, 16);
	}
	
	protected HashMap<String, Integer> getReplacements(){
		return replacements;
	}
	
}
