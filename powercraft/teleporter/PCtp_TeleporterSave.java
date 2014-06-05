package powercraft.teleporter;

import net.minecraft.nbt.NBTTagCompound;
import powercraft.api.PC_WorldSaveData;
import powercraft.teleporter.network.PCtp_TeleporterGlobalNetwork;


public class PCtp_TeleporterSave extends PC_WorldSaveData {

	private static final String NAME = "powercraft-teleporter";
	
	static PCtp_TeleporterSave save;
	
	@Override
	public void cleanup() {
		save = null;
	}
	
	private static PCtp_TeleporterSave getSave(){
		if(save==null){
			save = loadOrCreate(NAME, PCtp_TeleporterSave.class);
		}
		return save;
	}
	
	public static PCtp_TeleporterGlobalNetwork getGlobalNetwork(){
		return getSave().globalNetwork;
	}

	private PCtp_TeleporterGlobalNetwork globalNetwork = new PCtp_TeleporterGlobalNetwork();
	
	public PCtp_TeleporterSave(String name) {
		super(name);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		this.globalNetwork.readFromNBT(nbtTagCompound, this.globalNetwork);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		this.globalNetwork.writeToNBT(nbtTagCompound);
	}

	public static void makeDirty() {
		if(save!=null)
			save.markDirty();
	}
	
}
