package powercraft.weasel.tileentity;

import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Field;
import powercraft.api.PC_Field.Flag;
import powercraft.api.block.PC_TileEntity;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.PC_IGresGuiOpenHandler;
import powercraft.api.grid.PC_GridHelper;
import powercraft.api.grid.PC_IGridHolder;
import powercraft.api.script.weasel.PC_IWeaselEvent;
import powercraft.api.script.weasel.PC_IWeaselNativeHandler;
import powercraft.api.script.weasel.PC_Weasel;
import powercraft.api.script.weasel.PC_WeaselClassSave;
import powercraft.api.script.weasel.PC_WeaselEngine;
import powercraft.api.script.weasel.PC_WeaselSourceClass;
import powercraft.api.script.weasel.grid.PC_IWeaselGridTile;
import powercraft.api.script.weasel.grid.PC_IWeaselGridTileAddressable;
import powercraft.api.script.weasel.grid.PC_WeaselGrid;
import powercraft.weasel.gui.PCws_GuiCore;


public class PCws_TileEntityCore extends PC_TileEntity implements PC_IGresGuiOpenHandler, PC_IGridHolder, PC_IWeaselGridTileAddressable, PC_IWeaselNativeHandler {
	
	private PC_WeaselGrid grid;
	
	@PC_Field
	private PC_WeaselClassSave classSave;
	private PC_WeaselEngine engine;
	
	int address;
	
	private boolean occupied;
	
	private int[] redstoneValuesIn = new int[6];
	
	private int[] redstoneValuesOut = new int[6];
	
	public PCws_TileEntityCore(){
		if(!isClient()){
			this.classSave = PC_Weasel.createClassSave();
			this.engine = PC_Weasel.createEngine(this.classSave, 1024, this);
		}
	}
	
	@Override
	public void onTick(){
		if(!isClient())
			this.engine.run(10, 100);
	}

	@Override
	public void onLoadedFromNBT(Flag flag) {
		if(flag==Flag.SAVE && !isClient())
			this.engine = PC_Weasel.createEngine(this.classSave, 1024, this);
	}

	@Override
	public void onMessage(EntityPlayer player, NBTTagCompound nbtTagCompound) {
		if(nbtTagCompound.getInteger("type")==0 && !isClient()){
			NBTTagList list = (NBTTagList)nbtTagCompound.getTag("list");
			for(int i=0; i<list.tagCount(); i++){
				NBTTagCompound tagCompound = list.getCompoundTagAt(i);
				String file = tagCompound.getString("FileName");
				if(tagCompound.hasKey("source")){
					PC_WeaselSourceClass sourceClass = this.classSave.getClass(file);
					if(sourceClass==null){
						sourceClass = this.classSave.addClass(file);
					}
					sourceClass.setSource(tagCompound.getString("source"));
				}else{
					this.classSave.removeClass(file);
				}
			}
			this.classSave.compileMarked();
			this.engine = PC_Weasel.createEngine(this.classSave, 1024, this);
			try {
				this.engine.callMain("Main", "main()void");
			} catch (RuntimeException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void sendSourcesAndCompile(HashMap<String, String> sources){
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		nbtTagCompound.setInteger("type", 0);
		NBTTagList list = new NBTTagList();
		for(Entry<String, String> e:sources.entrySet()){
			NBTTagCompound tagCompound = new NBTTagCompound();
			tagCompound.setString("FileName", e.getKey());
			if(e.getValue()!=null)
				tagCompound.setString("source", e.getValue());
			list.appendTag(tagCompound);
		}
		nbtTagCompound.setTag("list", list);
		sendMessage(nbtTagCompound);
	}

	@Override
	public PC_IGresGui openClientGui(EntityPlayer player, NBTTagCompound serverData) {
		HashMap<String, String> sources = new HashMap<String, String>();
		NBTTagList list = (NBTTagList) serverData.getTag("list");
		for(int i=0; i<list.tagCount(); i++){
			NBTTagCompound tagCompound = list.getCompoundTagAt(i);
			sources.put(tagCompound.getString("FileName"), tagCompound.getString("source"));
		}
		return new PCws_GuiCore(this, sources);
	}

	@Override
	public PC_GresBaseWithInventory openServerGui(EntityPlayer player) {
		return null;
	}

	@Override
	public NBTTagCompound sendOnGuiOpenToClient(EntityPlayer player) {
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		HashMap<String, ? extends PC_WeaselSourceClass> hm = this.classSave.getSources();
		for(Entry<String, ? extends PC_WeaselSourceClass> e:hm.entrySet()){
			NBTTagCompound tagCompound = new NBTTagCompound();
			tagCompound.setString("FileName", e.getKey());
			tagCompound.setString("source", e.getValue().getSource());
			list.appendTag(tagCompound);
		}
		nbtTagCompound.setTag("list", list);
		return nbtTagCompound;
	}

	@Override
	public void setGrid(PC_WeaselGrid grid) {
		this.grid = grid;
	}

	@Override
	public PC_WeaselGrid getGrid() {
		return this.grid;
	}

	@Override
	public int getAddress() {
		return this.address;
	}

	@Override
	public void setAddressOccupied(boolean b) {
		this.occupied = b;
	}

	@Override
	public void getGridIfNull() {
		PC_GridHelper.getGridIfNull(this.worldObj, this.xCoord, this.yCoord, this.zCoord, 0x3F, this, PC_WeaselGrid.factory, PC_IWeaselGridTile.class);
	}

	@Override
	public void removeFromGrid() {
		PC_GridHelper.removeFromGrid(this.worldObj, (PC_IWeaselGridTile)this);
	}

	@Override
	public void onNeighborBlockChange(Block neighbor) {
		super.onNeighborBlockChange(neighbor);
		if(!this.occupied){
			for(PC_Direction dir:PC_Direction.VALID_DIRECTIONS){
				int value = this.redstoneValuesIn[dir.ordinal()];
				int newValue = this.worldObj.getIndirectPowerLevelTo(this.xCoord, this.yCoord, this.zCoord, dir.ordinal());
				if(value!=newValue){
					this.redstoneValuesIn[dir.ordinal()] = newValue;
					sendRedstoneChangeEvent(dir.ordinal(), newValue);
				}
			}
		}
	}
	
	private void sendRedstoneChangeEvent(final int side, final int value){
		if(!this.occupied){
			this.grid.sendEvent(new PC_IWeaselEvent(){
	
				@Override
				public String getEventName() {
					return "Redstone Changed";
				}
	
				@Override
				public String getEntryClass() {
					return "weasel.devices.Core";
				}
	
				@Override
				public String getEntryMethod() {
					return "redstoneChangeInterruptEntryPoint(int, int, int)void";
				}
	
				@Override
				public long[] getParams() {
					return new long[]{PCws_TileEntityCore.this.address, side, value};
				}
				
			});
		}
	}
	
	@Override
	public int getRedstonePowerValue(PC_Direction side, int faceSide) {
		return this.redstoneValuesOut[side.ordinal()];
	}

	@Override
	public void onEvent(PC_IWeaselEvent event) {
		this.engine.onEvent(event);
	}

	@SuppressWarnings("hiding")
	@Override
	public int getTypeUnsafe(int address) {
		PC_IWeaselGridTileAddressable tile = this.grid.getTileByAddress(address);
		return tile==null?0:tile.getType();
	}

	@SuppressWarnings("hiding")
	@Override
	public boolean isDevicePresent(int address) {
		return this.grid.getTileByAddress(address)!=null;
	}

	@SuppressWarnings("hiding")
	@Override
	public int getRedstoneValueUnsafe(int address, int side) {
		PC_IWeaselGridTileAddressable tile = this.grid.getTileByAddress(address);
		return tile==null?-1:tile.getRedstoneValue(side);
	}

	@Override
	public boolean setRedstoneValueUnsafe(int address, int side, int value) {
		PC_IWeaselGridTileAddressable tile = this.grid.getTileByAddress(address);
		if(tile==null){
			return false;
		}
		tile.setRedstoneValue(side, value);
		return true;
	}

	@Override
	public int getType() {
		return 1;
	}

	@Override
	public int getRedstoneValue(int side) {
		return this.redstoneValuesIn[side];
	}

	@Override
	public void setRedstoneValue(int side, int value) {
		this.redstoneValuesOut[side] = value;
		notifyNeighbors();
	}

	@Override
	public boolean canRedstoneConnect(PC_Direction side, int faceSide) {
		return true;
	}

	@Override
	public boolean shouldCheckWeakPower(PC_Direction side) {
		return false;
	}
	
	
	
}
