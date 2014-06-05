package powercraft.teleporter.network;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import powercraft.api.PC_Field.Flag;
import powercraft.api.PC_Vec4I;


public class PCtp_TeleporterNetwork extends PCtp_TeleporterNetworkEntry {

	private List<PCtp_TeleporterNetworkEntry> childs = new ArrayList<PCtp_TeleporterNetworkEntry>();
	
	public PCtp_TeleporterNetwork(PC_Vec4I pos) {
		super(pos);
	}

	public void addEntry(PCtp_TeleporterNetworkEntry entry) {
		this.childs.add(entry);
	}

	public void removeEntry(PCtp_TeleporterNetworkEntry entry) {
		this.childs.remove(entry);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound, PCtp_TeleporterGlobalNetwork globalNetwork) {
		NBTTagList list = nbtTagCompound.getTagList("childs", NBT.TAG_COMPOUND);
		for(int i=0; i<list.tagCount(); i++){
			NBTTagCompound tagCompound = list.getCompoundTagAt(i);
			int type = tagCompound.getInteger("type");
			PC_Vec4I pos = new PC_Vec4I(tagCompound, Flag.SAVE);
			PCtp_TeleporterNetworkEntry entry;
			if(type==0){
				entry = new PCtp_TeleporterNetwork(pos);
			}else{
				entry = new PCtp_TeleporterEndpoint(pos);
			}
			this.childs.add(entry);
			entry.readFromNBT(nbtTagCompound, globalNetwork);
		}
		super.readFromNBT(nbtTagCompound, globalNetwork);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);
		NBTTagList list = new NBTTagList();
		for(PCtp_TeleporterNetworkEntry entry:this.childs){
			NBTTagCompound tagCompound = new NBTTagCompound();
			tagCompound.setInteger("type", entry instanceof PCtp_TeleporterNetwork?0:1);
			entry.getPos().saveToNBT(tagCompound, Flag.SAVE);
			entry.writeToNBT(nbtTagCompound);
			list.appendTag(tagCompound);
		}
		nbtTagCompound.setTag("childs", list);
	}
	
	@Override
	public PCtp_TeleporterNetworkEntry getEntryByPath(String target) {
		String[] path = target.split("/");
		PCtp_TeleporterNetworkEntry entry = this;
		for(String p:path){
			entry = entry.getChildEntry(p);
			if(entry==null)
				return null;
		}
		return entry;
	}
	
	@Override
	public PCtp_TeleporterNetworkEntry getChildEntry(String name) {
		if(name.equals("."))
			return this;
		if(name.equals(".."))
			return getParentNetwork();
		for(PCtp_TeleporterNetworkEntry entry:this.childs){
			if(entry.getName().equals(name)){
				return entry;
			}
		}
		return null;
	}
	
}
