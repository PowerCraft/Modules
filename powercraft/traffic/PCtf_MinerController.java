package powercraft.traffic;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.nbt.NBTTagCompound;
import powercraft.api.PC_Field.Flag;
import powercraft.api.PC_INBT;
import powercraft.api.PC_NBTTagHandler;
import powercraft.api.script.weasel.PC_Weasel;
import powercraft.api.script.weasel.PC_WeaselClassSave;
import powercraft.api.script.weasel.PC_WeaselEngine;
import powercraft.api.script.weasel.PC_WeaselSourceClass;
import powercraft.traffic.entity.PCtf_EntityMiner;
import xscript.runtime.nativemethod.XNativeClass;
import xscript.runtime.nativemethod.XNativeClass.XNativeMethod;
import xscript.runtime.nativemethod.XNativeClass.XParamSpecial;
import xscript.runtime.nativemethod.XNativeClass.XParamSpecial.XParamTypes;

public class PCtf_MinerController implements PC_INBT{
	
	private PCtf_EntityMiner miner;
	private PC_WeaselClassSave classSave;
	private PC_WeaselEngine engine;
	
	public PCtf_MinerController(PCtf_EntityMiner miner){
		this.classSave = PC_Weasel.createClassSave(true);
		this.engine = PC_Weasel.createEngine(this.classSave, 1024, null);
		this.engine.registerNativeClass(MinerNativeInterface.class);
		this.miner = miner;
	}

	public PCtf_MinerController(NBTTagCompound nbtTagCompound, Flag flag){
		this.classSave = PC_NBTTagHandler.loadFromNBT(nbtTagCompound, "classSave", PC_WeaselClassSave.class, flag);
		this.engine = PC_Weasel.createEngine(this.classSave, 1024, null);
		this.engine.registerNativeClass(MinerNativeInterface.class);
	}
	
	public void setMiner(PCtf_EntityMiner miner){
		if(this.miner!=null)
			throw new IllegalArgumentException();
		this.miner = miner;
	}
	
	@Override
	public void saveToNBT(NBTTagCompound nbtTagCompound, Flag flag) {
		PC_NBTTagHandler.saveToNBT(nbtTagCompound, "classSave", this.classSave, flag);
	}
	
	public void run(){
		this.engine.run(10, 100);
	}
	
	public void setClassesAndCompile(HashMap<String, String> source){
		for(Entry<String, String>e:source.entrySet()){
			if(e.getValue()==null){
				this.classSave.removeClass(e.getKey());
			}else{
				PC_WeaselSourceClass sourceClass = this.classSave.getClass(e.getKey());
				if(sourceClass==null){
					sourceClass = this.classSave.addClass(e.getKey());
				}
				sourceClass.setSource(e.getValue());
			}
		}
		this.classSave.compileMarked();
		this.engine = PC_Weasel.createEngine(this.classSave, 1024, this);
		this.engine.registerNativeClass(MinerNativeInterface.class);
		try {
			this.engine.callMain("Main", "main()void");
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
		for(Entry<String, ? extends PC_WeaselSourceClass> e:this.classSave.getSources().entrySet()){
			map.put(e.getKey(), e.getValue().getSource());
		}
		return map;
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
