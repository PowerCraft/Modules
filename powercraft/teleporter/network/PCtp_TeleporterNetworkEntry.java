package powercraft.teleporter.network;

import net.minecraft.nbt.NBTTagCompound;
import powercraft.api.PC_Vec4I;


public abstract class PCtp_TeleporterNetworkEntry {
	
	private PC_Vec4I pos;
	
	private String name;
	
	private PCtp_TeleporterNetwork parent;
	
	public PCtp_TeleporterNetworkEntry(PC_Vec4I pos){
		this.pos = pos;
	}
	
	public PC_Vec4I getPos(){
		return new PC_Vec4I(this.pos);
	}
	
	public String getName(){
		return this.name;
	}
	
	public PCtp_TeleporterNetwork getParentNetwork(){
		return this.parent;
	}
	
	public void readFromNBT(NBTTagCompound nbtTagCompound, PCtp_TeleporterGlobalNetwork globalNetwork) {
		this.name = nbtTagCompound.getString("name");
		globalNetwork.addAfterLoding(this);
	}

	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		nbtTagCompound.setString("name", this.name);
	}
	
	@SuppressWarnings("static-method")
	public PCtp_TeleporterNetworkEntry getEntryByPath(String target) {
		return null;
	}

	public PCtp_TeleporterNetworkEntry getChildEntry(String name) {
		return null;
	}
	
}
