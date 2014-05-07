package powercraft.teleporter;

import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldSavedData;
import powercraft.api.PC_Utils;
import powercraft.api.PC_Vec4I;


public class PCtp_TeleporterSave extends WorldSavedData {

	private static final String NAME = "powercraft-teleporter";
	
	static PCtp_TeleporterSave save;
	
	public static void cleanup() {
		save = null;
	}
	
	private static PCtp_TeleporterSave getSave(){
		if(save==null){
			save = (PCtp_TeleporterSave) PC_Utils.mcs().worldServerForDimension(0).mapStorage.loadData(PCtp_TeleporterSave.class, NAME);
			if(save==null){
				save = new PCtp_TeleporterSave();
				PC_Utils.mcs().worldServerForDimension(0).mapStorage.setData(NAME, save);
			}
		}
		return save;
	}
	
	private static TeleporterData getTeleporterData(String name){
		return getSave().getTeleporterData2(name);
	}

	private HashMap<String, TeleporterData> teleporterData = new HashMap<String, PCtp_TeleporterSave.TeleporterData>();
	
	public PCtp_TeleporterSave() {
		super(NAME);
	}
	
	public PCtp_TeleporterSave(String name) {
		super(name);
	}

	private TeleporterData getTeleporterData2(String name) {
		String[] keys = name.split("\\.");
		TeleporterData td = this.teleporterData.get(keys[0]);
		if(keys.length!=1 && td!=null){
			return td.getTeleporterData(keys[1]);
		}
		return td;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		NBTTagList list = (NBTTagList)nbtTagCompound.getTag("save");
		for(int i=0; i<list.tagCount(); i++){
			NBTTagCompound com = list.getCompoundTagAt(i);
			String name = com.getString("key");
			this.teleporterData.put(name, new TeleporterData(com, name, null));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		NBTTagList list = new NBTTagList();
		for(Entry<String, TeleporterData> e:this.teleporterData.entrySet()){
			NBTTagCompound com = new NBTTagCompound();
			com.setString("key", e.getKey());
			e.getValue().saveToNBT(com);
			list.appendTag(com);
		}
		nbtTagCompound.setTag("save", list);
	}
	
	private static class TeleporterData{

		private TeleporterData parent;
		private String name;
		private HashMap<String, TeleporterData> teleporterData;
		private PC_Vec4I pos;
		
		public TeleporterData(NBTTagCompound nbtTagCompound, String name, TeleporterData parent) {
			this.parent = parent;
			this.name = name;
			if(nbtTagCompound.hasKey("x")){
				this.pos = new PC_Vec4I(nbtTagCompound.getInteger("x"), nbtTagCompound.getInteger("y"), nbtTagCompound.getInteger("z"), nbtTagCompound.getInteger("dimension"));
			}else{
				NBTTagList list = (NBTTagList)nbtTagCompound.getTag("save");
				this.teleporterData = new HashMap<String, PCtp_TeleporterSave.TeleporterData>();
				for(int i=0; i<list.tagCount(); i++){
					NBTTagCompound com = list.getCompoundTagAt(i);
					String n = com.getString("key");
					this.teleporterData.put(n, new TeleporterData(com, n, this));
				}
			}
		}

		public TeleporterData getTeleporterData(String name) {
			String[] keys = name.split("\\.");
			TeleporterData td = this.teleporterData.get(keys[0]);
			if(keys.length!=1 && td!=null){
				return td.getTeleporterData(keys[1]);
			}
			return td;
		}

		public void saveToNBT(NBTTagCompound nbtTagCompound) {
			if(this.pos==null){
				NBTTagList list = new NBTTagList();
				for(Entry<String, TeleporterData> e:this.teleporterData.entrySet()){
					NBTTagCompound com = new NBTTagCompound();
					com.setString("key", e.getKey());
					e.getValue().saveToNBT(com);
					list.appendTag(com);
				}
				nbtTagCompound.setTag("save", list);
			}else{
				nbtTagCompound.setInteger("x", this.pos.x);
				nbtTagCompound.setInteger("y", this.pos.y);
				nbtTagCompound.setInteger("z", this.pos.z);
				nbtTagCompound.setInteger("dimension", this.pos.w);
			}
		}
		
	}
	
}
