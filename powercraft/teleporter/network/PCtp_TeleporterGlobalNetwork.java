package powercraft.teleporter.network;

import java.util.HashMap;

import powercraft.api.PC_Vec4I;
import powercraft.teleporter.PCtp_TeleporterSave;


public class PCtp_TeleporterGlobalNetwork extends PCtp_TeleporterNetwork {
	
	private HashMap<PC_Vec4I, PCtp_TeleporterNetworkEntry> entries = new HashMap<PC_Vec4I, PCtp_TeleporterNetworkEntry>();
	
	public PCtp_TeleporterGlobalNetwork() {
		super(new PC_Vec4I(0, -1, 0, 0));
	}
	
	public PCtp_TeleporterNetworkEntry getEntry(PC_Vec4I pos){
		return this.entries.get(pos);
	}

	public void removeEntry(PC_Vec4I pos) {
		PCtp_TeleporterNetworkEntry entry = this.entries.remove(pos);
		if(entry!=null)
			entry.getParentNetwork().removeEntry(entry);
	}
	
	public static PCtp_TeleporterGlobalNetwork instance() {
		return PCtp_TeleporterSave.getGlobalNetwork();
	}
	
	public static void markDirty(){
		PCtp_TeleporterSave.makeDirty();
	}

	void addAfterLoding(PCtp_TeleporterNetworkEntry entry) {
		this.entries.put(entry.getPos(), entry);
	}
	
}
