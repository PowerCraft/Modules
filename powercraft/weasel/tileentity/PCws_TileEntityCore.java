package powercraft.weasel.tileentity;

import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import powercraft.api.PC_Field;
import powercraft.api.PC_Field.Flag;
import powercraft.api.block.PC_TileEntity;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.PC_IGresGuiOpenHandler;
import powercraft.api.script.weasel.PC_Weasel;
import powercraft.api.script.weasel.PC_WeaselClassSave;
import powercraft.api.script.weasel.PC_WeaselEngine;
import powercraft.api.script.weasel.PC_WeaselSourceClass;
import powercraft.weasel.gui.PCws_GuiCore;


public class PCws_TileEntityCore extends PC_TileEntity implements PC_IGresGuiOpenHandler {
	
	@PC_Field
	private PC_WeaselClassSave classSave;
	private PC_WeaselEngine engine;
	
	public PCws_TileEntityCore(){
		if(!isClient()){
			this.classSave = PC_Weasel.createClassSave();
			this.engine = PC_Weasel.createEngine(this.classSave, 1024);
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
			this.engine = PC_Weasel.createEngine(this.classSave, 1024);
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
			this.engine = PC_Weasel.createEngine(this.classSave, 1024);
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
	
}
