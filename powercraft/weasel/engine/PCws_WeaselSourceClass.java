package powercraft.weasel.engine;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import powercraft.api.script.weasel.PC_WeaselSourceClass;
import xscript.compiler.message.XMessageElement;

public class PCws_WeaselSourceClass implements PC_WeaselSourceClass {

	private String source = "";
	private byte[] data;
	private boolean dirty;
	private List<XMessageElement> messageElements;
	
	public PCws_WeaselSourceClass(){
		
	}
	
	public PCws_WeaselSourceClass(NBTTagCompound tagCompound) {
		this.source = tagCompound.getString("source");
		if(tagCompound.hasKey("data"))
			this.data = tagCompound.getByteArray("data");
	}

	@Override
	public String getSource() {
		return this.source;
	}

	@SuppressWarnings("hiding")
	public void save(byte[] data) {
		this.data = data;
		this.dirty = false;
	}

	public InputStream getInputStream() {
		return new ByteArrayInputStream(this.data);
	}

	public boolean needRecompile(){
		return this.dirty;
	}

	@SuppressWarnings("static-method")
	public String getCompiler() {
		return "xscript";
	}

	public void addMessageElement(XMessageElement messageElement) {
		if(this.messageElements==null){
			this.messageElements = new ArrayList<XMessageElement>();
		}
		this.messageElements.add(messageElement);
	}

	public void clearMessages() {
		this.messageElements = null;
	}
	
	public void saveToNBT(NBTTagCompound tagCompound) {
		tagCompound.setString("source", this.source);
		if(this.data!=null)
			tagCompound.setByteArray("data", this.data);
	}

	@Override
	public void setSource(String source) {
		this.dirty = true;
		this.source = source;
	}

	public boolean canUseByteCode() {
		return !this.dirty && this.data!=null;
	}
	
}
