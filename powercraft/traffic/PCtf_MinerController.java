package powercraft.traffic;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.nbt.NBTTagCompound;
import powercraft.api.PC_Field.Flag;
import powercraft.api.PC_INBT;
import powercraft.api.PC_Logger;
import powercraft.api.PC_NBTTagHandler;
import powercraft.api.inventory.PC_WeaselNativeInventoryInterface;
import powercraft.api.script.weasel.PC_IWeaselEvent;
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

public class PCtf_MinerController implements PC_INBT, PC_IWeaselGridTileAddressable{
	
	private PCtf_EntityMiner miner;
	private PC_WeaselContainer weasel;
	private boolean isOccupied;
	int address = 1;
	private PC_WeaselGrid grid = PC_WeaselGrid.factory.make(this);
	
	public PCtf_MinerController(PCtf_EntityMiner miner){
		this.weasel = PC_Weasel.createContainer("Miner", 1024);
		registerNativeClasses();
		this.miner = miner;
	}

	public PCtf_MinerController(NBTTagCompound nbtTagCompound, Flag flag){
		this.weasel = PC_NBTTagHandler.loadFromNBT(nbtTagCompound, "container", PC_WeaselContainer.class, flag);
		this.isOccupied = nbtTagCompound.getBoolean("isOccupied");
		this.address = nbtTagCompound.getInteger("address");
		registerNativeClasses();
	}
	
	@SuppressWarnings("resource")
	private void registerNativeClasses(){
		ConsoleOut consoleOut = new ConsoleOut();
		PrintStream ps = new PrintStream(consoleOut, true);
		this.weasel.setErrorOutput(ps);
		this.weasel.setTile(this);
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
		nbtTagCompound.setBoolean("isOccupied", this.isOccupied);
		nbtTagCompound.setInteger("address", this.address);
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
		this.weasel.compileMarked(new String[]{"weasel.miner.Miner"}, new String[]{"weasel.miner"});
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
	
	@SuppressWarnings("hiding")
	public PCtf_EntityMiner getMiner(int address){
		PC_IWeaselGridTileAddressable tile = getTileByAddress(address);
		return tile instanceof PCtf_EntityMiner?(PCtf_EntityMiner)tile:null;
	}
	
	@SuppressWarnings("hiding")
	@Override
	public PC_IWeaselGridTileAddressable getTileByAddress(int address) {
		return this.grid.getTileByAddress(this, address);
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
		this.weasel.onEvent(event);
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
		this.isOccupied = b;
	}

	@Override
	public void onEvent(PC_IWeaselEvent event) {
		this.weasel.onEvent(event);
	}

	@Override
	public int getType() {
		return 0;
	}

	@Override
	public int getRedstoneValue(int side) {
		return -1;
	}

	@Override
	public void setRedstoneValue(int side, int value) {
		//
	}
	
	@Override
	public void print(String out) {
		printToConsole(out);
	}
	
	@Override
	public void cls() {
		this.miner.clearConsole();
	}
	
	public void printToConsole(String s){
		this.miner.printToConsole(s);
	}

	private class ConsoleOut extends OutputStream{

		public ConsoleOut() {}

		private String buff = "";
		
		@Override
		public void write(int b) throws IOException {
			this.buff += (char)b;
		}

		@Override
		public void flush() throws IOException {
			printToConsole(this.buff);
			this.buff = "";
		}
		
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
		public static void placeBlock(@XParamSpecial(XParamTypes.USERDATA)PCtf_MinerController minerController, int address, String inventory, int invPlace, int x, int y, int z){
			PCtf_EntityMiner miner = minerController.getMiner(address);
			if(miner!=null){
				miner.placeBlock(inventory, invPlace, x, y, z);
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

	public void onConsoleInput(final String text) {
		System.out.println("onConsoleInput:"+text);
		this.grid.sendEvent(new PC_IWeaselEvent() {
			
			@Override
			public Object[] getParams() {
				return new Object[]{Integer.valueOf(PCtf_MinerController.this.address), text};
			}
			
			@Override
			public String getEventName() {
				return "Console Input";
			}
			
			@Override
			public String getEntryMethod() {
				return "consoleInputInterruptEntryPoint(int, xscript.lang.String)void";
			}
			
			@Override
			public String getEntryClass() {
				return "weasel.devices.Console";
			}
		});
	}
	
}
