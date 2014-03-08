package powercraft.transport.tileentity;

import static powercraft.transport.tileentity.PCtr_TileEntityBeltScriptable.REPLACEMENT.BACK_COUNT;
import static powercraft.transport.tileentity.PCtr_TileEntityBeltScriptable.REPLACEMENT.DIR_EAST;
import static powercraft.transport.tileentity.PCtr_TileEntityBeltScriptable.REPLACEMENT.DIR_IN;
import static powercraft.transport.tileentity.PCtr_TileEntityBeltScriptable.REPLACEMENT.DIR_NORTH;
import static powercraft.transport.tileentity.PCtr_TileEntityBeltScriptable.REPLACEMENT.DIR_SOUTH;
import static powercraft.transport.tileentity.PCtr_TileEntityBeltScriptable.REPLACEMENT.DIR_WEST;
import static powercraft.transport.tileentity.PCtr_TileEntityBeltScriptable.REPLACEMENT.FRONT_COUNT;
import static powercraft.transport.tileentity.PCtr_TileEntityBeltScriptable.REPLACEMENT.ITEM_DAMAGE;
import static powercraft.transport.tileentity.PCtr_TileEntityBeltScriptable.REPLACEMENT.ITEM_ID;
import static powercraft.transport.tileentity.PCtr_TileEntityBeltScriptable.REPLACEMENT.ITEM_STACKSIZE;
import static powercraft.transport.tileentity.PCtr_TileEntityBeltScriptable.REPLACEMENT.LEFT_COUNT;
import static powercraft.transport.tileentity.PCtr_TileEntityBeltScriptable.REPLACEMENT.MOB_TYPE;
import static powercraft.transport.tileentity.PCtr_TileEntityBeltScriptable.REPLACEMENT.RIGHT_COUNT;
import static powercraft.transport.tileentity.PCtr_TileEntityBeltScriptable.REPLACEMENT.TYPE;
import static powercraft.transport.tileentity.PCtr_TileEntityBeltScriptable.REPLACEMENT.TYPE_ITEMSTACK;
import static powercraft.transport.tileentity.PCtr_TileEntityBeltScriptable.REPLACEMENT.TYPE_MOB;
import static powercraft.transport.tileentity.PCtr_TileEntityBeltScriptable.REPLACEMENT.TYPE_PLAYER;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.tools.Diagnostic;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Utils;
import powercraft.api.block.PC_TileEntityScriptable;
import powercraft.api.gres.PC_Gres;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.PC_IGresGuiOpenHandler;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.script.PC_FakeDiagnostic;
import powercraft.transport.PCtr_BeltHelper;
import powercraft.transport.block.PCtr_PacketSetEntitySpeed;
import powercraft.transport.gui.PCtr_GuiBeltScriptable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import static powercraft.transport.tileentity.PCtr_TileEntityBeltScriptable.REPLACEMENT.*;

@SuppressWarnings("boxing")
public class PCtr_TileEntityBeltScriptable extends PC_TileEntityScriptable implements PC_IGresGuiOpenHandler {
	
	private static final HashMap<String, Integer> replacements = new HashMap<String, Integer>();
	private static final String[] entryVectors = new String[]{"itemOver", "redstoneToggeled"};
	
	private static final PC_Direction[] DIR_TO_PCDIR = {PC_Direction.NORTH, PC_Direction.EAST, PC_Direction.SOUTH, PC_Direction.WEST};
	
	protected enum REPLACEMENT{
		
		//ext_places
		FRONT_COUNT("out.front", 0),
		RIGHT_COUNT("out.right", 1),
		BACK_COUNT("out.back", 2),
		LEFT_COUNT("out.left", 3),
		DIR_OUT("out.dir", 4),
		DIR_IN("in.dir", 4),
		TYPE("in.type", 5),
		
		//ext_places if item
		ITEM_ID("in.item.id", 6),
		ITEM_DAMAGE("in.item.damage", 7),
		ITEM_STACKSIZE("in.item.stacksize", 8),
		ITEM_STORE_FAILURE("in.item.storefailure", 9),
		
		//ext_places if mob
		MOB_TYPE("in.mob.type", 6),
		
		//values
		TYPE_ITEMSTACK("type.item", 0),
		TYPE_MOB("type.mob", 1),
		TYPE_PLAYER("type.player", 2),
		
		DIR_NORTH("dir.north", 0),
		DIR_EAST("dir.east", 1),
		DIR_SOUTH("dir.south", 2),
		DIR_WEST("dir.west", 3),
		
		;
		final String name;
		final int value;
		REPLACEMENT(String name, int num){
			this.name = name;
			this.value = num;
		}
	}
	
	static{
		for(REPLACEMENT e:REPLACEMENT.values()){
			replacements.put(e.name, e.value);
		}
	}
	
	public PCtr_TileEntityBeltScriptable(){
		super(16);
		setSource(";A MiniScript powered Belt");
	}

	@Override
	public void onEntityCollidedWithBlock(Entity entity) {
		if(PCtr_BeltHelper.isEntityIgnored(entity))
			return;
		if(!(Math.floor(entity.posX)==this.xCoord && Math.floor(entity.posY)==this.yCoord && Math.floor(entity.posZ)==this.zCoord)){
			return;
		}
		int diff = PCtr_BeltHelper.combineEntityItems(entity)?2:1;
		if(isClient()){
			PCtr_BeltHelper.handleEntity(entity, worldObj, xCoord, yCoord, zCoord, false, true);
			return;
		}
		NBTTagCompound compound = PC_Utils.getNBTTagOf(entity);
		int prevDir = -1;
		boolean recalc = false;
		if(compound.hasKey("dir")){
			int x = compound.getInteger("lastX");
			int y = compound.getInteger("lastY");
			int z = compound.getInteger("lastZ");
			int lastTick = compound.getInteger("lastTick");
			prevDir = compound.getInteger("dir");
			if(lastTick==entity.ticksExisted)
				return;
			if(x==this.xCoord && y==this.yCoord && z==this.zCoord && (lastTick==entity.ticksExisted-diff || lastTick==entity.ticksExisted-1 || entity.ticksExisted==0)){
				if(!PCtr_BeltHelper.handleEntity(entity, worldObj, xCoord, yCoord, zCoord, false, true)){
					compound.setInteger("lastTick", entity.ticksExisted);
					return;
				}
				recalc = true;
			}
		}
		compound.setInteger("lastX", this.xCoord);
		compound.setInteger("lastY", this.yCoord);
		compound.setInteger("lastZ", this.zCoord);
		compound.setInteger("lastTick", entity.ticksExisted);
		int[] ext = getExt();
		ext[FRONT_COUNT.value] = 0;
		ext[RIGHT_COUNT.value] = 0;
		ext[BACK_COUNT.value] = 0;
		ext[LEFT_COUNT.value] = 0;
		switch(PC_Utils.getEntityMovement2D(entity)){
		case EAST:
			ext[DIR_IN.value] = DIR_EAST.value;
			break;
		case NORTH:
			ext[DIR_IN.value] = DIR_NORTH.value;
			break;
		case SOUTH:
			ext[DIR_IN.value] = DIR_SOUTH.value;
			break;
		case WEST:
			ext[DIR_IN.value] = DIR_WEST.value;
			break;
		default:
			return;
		}
		if(entity instanceof EntityItem){
			ItemStack is = ((EntityItem) entity).getEntityItem();
			ext[TYPE.value] = TYPE_ITEMSTACK.value;
			ext[ITEM_ID.value] = Item.getIdFromItem(is.getItem());
			ext[ITEM_DAMAGE.value] = is.getItemDamage();
			ext[ITEM_STACKSIZE.value] = is.stackSize;
			ext[ITEM_STORE_FAILURE.value] = recalc?-1:0;
		}else if(entity instanceof EntityPlayer){
			ext[TYPE.value] = TYPE_PLAYER.value;
		}else if(entity instanceof EntityCreature || entity instanceof EntitySlime){
			ext[TYPE.value] = TYPE_MOB.value;
			ext[MOB_TYPE.value] = EntityList.getEntityID(entity);
		}else{
			return;
		}
		invoke(0); // entry: itemOver
		int direction = ext[DIR_OUT.value];
		int sum = ext[FRONT_COUNT.value]+ext[RIGHT_COUNT.value]+ext[BACK_COUNT.value]+ext[LEFT_COUNT.value];
		direction=(direction%4+4)%4;
		if(sum!=0){
			if(sum==ext[RIGHT_COUNT.value]){
				direction += 1;
			}else if(sum==ext[BACK_COUNT.value]){
				direction += 2;
			}else if(sum==ext[LEFT_COUNT.value]){
				direction += 3;
			}else if(sum!=ext[FRONT_COUNT.value]){
				//
			}
		}
		direction=DIR_TO_PCDIR[direction%4].ordinal();
		compound.setInteger("dir", direction);
		PCtr_BeltHelper.handleEntity(entity, worldObj, xCoord, yCoord, zCoord, false, true);
		if(prevDir!=direction){
			PC_PacketHandler.sendToAllAround(new PCtr_PacketSetEntitySpeed(compound, entity), this.worldObj.getWorldInfo().getVanillaDimension(), this.xCoord, this.yCoord, this.zCoord, 16);
		}
	}
	
	@Override
	public HashMap<String, Integer> getReplacements(){
		return replacements;
	}
	
	@Override
	protected String[] getEntryVectors() {
		return entryVectors;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public PC_IGresGui openClientGui(EntityPlayer player, NBTTagCompound nbtTagCompound) {
		List<Diagnostic<? extends Void>> diagnostics;
		if(nbtTagCompound.hasKey("diagnostics")){
			diagnostics = new ArrayList<Diagnostic<? extends Void>>();
			NBTTagList list = (NBTTagList)nbtTagCompound.getTag("diagnostics");
			for(int i=0; i<list.tagCount(); i++){
				NBTTagCompound compound =list.getCompoundTagAt(i);
				diagnostics.add(PC_FakeDiagnostic.fromCompound(compound));
			}
		}else{
			diagnostics = null;
		}
		return new PCtr_GuiBeltScriptable(this, nbtTagCompound.getString("source"), diagnostics);
	}

	@Override
	public PC_GresBaseWithInventory openServerGui(EntityPlayer player) {
		return null;
	}
	
	@Override
	public NBTTagCompound sendOnGuiOpenToClient(EntityPlayer player) {
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		if(getSource()!=null)
			nbtTagCompound.setString("source", getSource());
		if(this.diagnostic!=null){
			List<Diagnostic<? extends Void>> diagnostics = this.diagnostic.getDiagnostics();
			NBTTagList list = new NBTTagList();
			for(Diagnostic<? extends Void> dgc:diagnostics){
				list.appendTag(PC_FakeDiagnostic.toCompound(dgc));
			}
			nbtTagCompound.setTag("diagnostics", list);
		}
		return nbtTagCompound;
	}

	public void sendSaveMessage(String text) {
		NBTTagCompound tagCompound = new NBTTagCompound();
		tagCompound.setInteger("type", 1);
		tagCompound.setString("source", text);
		sendMessage(tagCompound);
	}

	public void sendErrorMessage(){
		if(this.diagnostic!=null){
			List<Diagnostic<? extends Void>> diagnostics = this.diagnostic.getDiagnostics();
			NBTTagList list = new NBTTagList();
			for(Diagnostic<? extends Void> dgc:diagnostics){
				list.appendTag(PC_FakeDiagnostic.toCompound(dgc));
			}
			NBTTagCompound tagCompound = new NBTTagCompound();
			tagCompound.setInteger("type", 2);
			tagCompound.setTag("diagnostics", list);
			sendMessage(tagCompound);
		}
	}
	
	@SideOnly(Side.CLIENT)
	private static void handleDiagnostic(NBTTagList list){
		PCtr_GuiBeltScriptable gui = PC_Gres.getCurrentClientGui(PCtr_GuiBeltScriptable.class);
		if(gui!=null){
			List<Diagnostic<? extends Void>> diagnostics = new ArrayList<Diagnostic<? extends Void>>();
			for(int i=0; i<list.tagCount(); i++){
				NBTTagCompound compound =list.getCompoundTagAt(i);
				diagnostics.add(PC_FakeDiagnostic.fromCompound(compound));
			}
			gui.setErrors(diagnostics);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void onClientMessage(EntityPlayer player, NBTTagCompound tagCompound) {
		switch(tagCompound.getInteger("type")){
		case 2:
			handleDiagnostic((NBTTagList)tagCompound.getTag("diagnostics"));
			break;
		default:
			onMessage(player, tagCompound);
			break;
		}
	}

	@Override
	public void onMessage(EntityPlayer player, NBTTagCompound tagCompound) {
		switch(tagCompound.getInteger("type")){
		case 1:
			String text = tagCompound.getString("source");
			setSource(text);
			sendErrorMessage();
			break;
		case 2:
			break;
		default:
			break;
		}
	}
	
}
