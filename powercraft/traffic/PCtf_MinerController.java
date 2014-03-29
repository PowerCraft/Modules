package powercraft.traffic;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import powercraft.api.PC_Field.Flag;
import powercraft.api.PC_INBT;
import powercraft.api.PC_Logger;
import powercraft.api.PC_NBTTagHandler;
import powercraft.api.inventory.PC_WeaselNativeInventoryInterface;
import powercraft.api.script.weasel.PC_IWeaselEvent;
import powercraft.api.script.weasel.PC_IWeaselInventory;
import powercraft.api.script.weasel.PC_Weasel;
import powercraft.api.script.weasel.PC_WeaselContainer;
import powercraft.api.script.weasel.PC_WeaselSourceClass;
import powercraft.api.script.weasel.grid.PC_IWeaselGridTileAddressable;
import powercraft.api.script.weasel.grid.PC_WeaselGrid;
import powercraft.traffic.entity.PCtf_EntityMiner;
import xscript.runtime.nativemethod.XNativeClass;
import xscript.runtime.nativemethod.XNativeClass.XNativeMethod;
import xscript.runtime.nativemethod.XNativeClass.XParamSpecial;
import xscript.runtime.nativemethod.XNativeClass.XParamSpecial.XParamTypes;

public class PCtf_MinerController implements PC_INBT, PC_IWeaselInventory, PC_IWeaselGridTileAddressable{
	
	private PCtf_EntityMiner miner;
	private PC_WeaselContainer weasel;
	private boolean isOccupied;
	private int address;
	private PC_WeaselGrid grid = PC_WeaselGrid.factory.make(this);
	
	public PCtf_MinerController(PCtf_EntityMiner miner){
		this.weasel = PC_Weasel.createContainer("Miner", 1024);
		registerNativeClasses();
		this.miner = miner;
	}

	public PCtf_MinerController(NBTTagCompound nbtTagCompound, Flag flag){
		this.weasel = PC_NBTTagHandler.loadFromNBT(nbtTagCompound, "container", PC_WeaselContainer.class, flag);
		isOccupied = nbtTagCompound.getBoolean("isOccupied");
		address = nbtTagCompound.getInteger("address");
		registerNativeClasses();
	}
	
	private void registerNativeClasses(){
		this.weasel.setErrorOutput(System.err);
		this.weasel.setHandler(this);
		this.weasel.registerNativeClass(MinerNativeInterface.class);
		this.weasel.registerNativeClass(PC_WeaselNativeInventoryInterface.class);
	}
	
	public void setMiner(PCtf_EntityMiner miner){
		if(this.miner!=null)
			throw new IllegalArgumentException();
		this.miner = miner;
	}
	
	@Override
	public void saveToNBT(NBTTagCompound nbtTagCompound, Flag flag) {
		PC_NBTTagHandler.saveToNBT(nbtTagCompound, "container", this.weasel, flag);
		nbtTagCompound.setBoolean("isOccupied", isOccupied);
		nbtTagCompound.setInteger("address", address);
	}
	
	public void run(){
		this.weasel.run(10, 100);
	}
	
	public NBTTagCompound getDiagnostics() {
		NBTTagCompound tagCompound = new NBTTagCompound();
		this.weasel.saveDiagnosticsToNBT(tagCompound);
		return tagCompound;
	}
	
	public void setClassesAndCompile(HashMap<String, String> source){
		for(Entry<String, String>e:source.entrySet()){
			if(e.getValue()==null){
				this.weasel.removeClass(e.getKey());
			}else{
				PC_WeaselSourceClass sourceClass = this.weasel.getClass(e.getKey());
				if(sourceClass==null){
					sourceClass = this.weasel.addClass(e.getKey());
				}
				sourceClass.setSource(e.getValue());
			}
		}
		boolean success = this.weasel.compileMarked(new String[]{"weasel.miner.Miner"}, new String[]{"weasel.miner"});
		NBTTagCompound tagCompound = new NBTTagCompound();
		NBTTagCompound diagnostics = new NBTTagCompound();
		this.weasel.saveDiagnosticsToNBT(diagnostics);
		tagCompound.setTag("diagnostics", diagnostics);
		tagCompound.setInteger("type", 1);
		this.miner.sendMessage(tagCompound);
		try {
			this.weasel.callMain("Main", "main()void");
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	public PCtf_EntityMiner getMiner(int address){
		return this.miner;
	}
	
	public Map<String, String> getSources() {
		Map<String, String> map = new HashMap<String, String>();
		for(Entry<String, ? extends PC_WeaselSourceClass> e:this.weasel.getSources().entrySet()){
			map.put(e.getKey(), e.getValue().getSource());
		}
		return map;
	}
	
	public void makeInterrupt(PC_IWeaselEvent event){
		String tmp;
		if(event==null || (tmp=event.getEntryClass())==null || tmp.isEmpty() || (tmp=event.getEntryMethod())==null || tmp.isEmpty()){
			PC_Logger.severe("tried to interrupt the Miner using an invalid event-object!");
			return;
		}
		weasel.onEvent(event);
	}

	@Override
	public IInventory[] getInventories() {
		return this.miner.inventoryArray;
	}

	@Override
	public void setGrid(PC_WeaselGrid grid) {
		this.grid = grid;
	}

	@Override
	public PC_WeaselGrid getGrid() {
		return grid;
	}

	@Override
	public int getAddress() {
		return address;
	}

	@Override
	public void setAddressOccupied(boolean b) {
		isOccupied = b;
	}

	@Override
	public void onEvent(PC_IWeaselEvent event) {
		weasel.onEvent(event);
	}

	@Override
	public int getType() {
		return 0;
	}

	@Override
	public int getRedstoneValue(int side) {
		return 0;
	}

	@Override
	public void setRedstoneValue(int side, int value) {
	}
	

	
	//Weasel Available Methods
	@XNativeClass("weasel.miner.Miner")
	public static class MinerNativeInterface{
		
		@XNativeMethod
		public static int operationFinished(@XParamSpecial(XParamTypes.USERDATA)PCtf_MinerController minerController, int address){
			PCtf_EntityMiner miner = minerController.getMiner(address);
			if(miner==null){
				return -1;
			}
			return miner.operationFinished();
		}
		
		@XNativeMethod
		public static void digForward(@XParamSpecial(XParamTypes.USERDATA)PCtf_MinerController minerController, int address){
			PCtf_EntityMiner miner = minerController.getMiner(address);
			if(miner!=null){
				miner.digForward();
			}
		}
		
		@XNativeMethod
		public static void digUpward(@XParamSpecial(XParamTypes.USERDATA)PCtf_MinerController minerController, int address){
			PCtf_EntityMiner miner = minerController.getMiner(address);
			if(miner!=null){
				miner.digUpward();
			}
		}
		
		@XNativeMethod
		public static void digDownward(@XParamSpecial(XParamTypes.USERDATA)PCtf_MinerController minerController, int address){
			PCtf_EntityMiner miner = minerController.getMiner(address);
			if(miner!=null){
				miner.digDownward();
			}
		}
		
		@XNativeMethod
		public static void placeBlock(@XParamSpecial(XParamTypes.USERDATA)PCtf_MinerController minerController, int address, int invPlace, int x, int y, int z){
			PCtf_EntityMiner miner = minerController.getMiner(address);
			if(miner!=null){
				miner.placeBlock(invPlace, x, y, z);
			}
		}
	
		@XNativeMethod
		public static void moveForward(@XParamSpecial(XParamTypes.USERDATA)PCtf_MinerController minerController, int address, int steps){
			PCtf_EntityMiner miner = minerController.getMiner(address);
			if(miner!=null){
				miner.moveForward(steps);
			}
		}
		
		@XNativeMethod
		public static void rotate(@XParamSpecial(XParamTypes.USERDATA)PCtf_MinerController minerController, int address, int dir){
			PCtf_EntityMiner miner = minerController.getMiner(address);
			if(miner!=null){
				miner.rotate(dir);
			}
		}
		
		@XNativeMethod
		public static boolean isMiningEnabled(@XParamSpecial(XParamTypes.USERDATA)PCtf_MinerController minerController, int address){
			PCtf_EntityMiner miner = minerController.getMiner(address);
			if(miner!=null){
				return miner.isMiningEnabled();
			}
			return false;
		}
		
		@XNativeMethod
		public static boolean setMining(@XParamSpecial(XParamTypes.USERDATA)PCtf_MinerController minerController, int address, boolean state){
			PCtf_EntityMiner miner = minerController.getMiner(address);
			if(miner!=null){
				return miner.setMining(state);
			}
			return false;
		}
	}
	
}
