package powercraft.weasel.engine;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.tools.Diagnostic;

import net.minecraft.nbt.NBTTagCompound;
import powercraft.api.PC_ImmutableList;
import powercraft.api.script.weasel.PC_WeaselSourceClass;

public class PCws_WeaselSourceClass implements PC_WeaselSourceClass {

	private String source = "";
	private byte[] data;
	private boolean dirty;
	private List<Diagnostic<String>> diagnostics;
	
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
		return this.dirty || this.diagnostics!=null;
	}

	@SuppressWarnings("static-method")
	public String getCompiler() {
		return "xscript";
	}

	public void addDiagnostic(Diagnostic<String> diagnostic) {
		if(this.diagnostics==null){
			this.diagnostics = new ArrayList<Diagnostic<String>>();
		}
		this.diagnostics.add(diagnostic);
	}

	public void clearDiagnostics() {
		this.diagnostics = null;
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

	@Override
	public List<Diagnostic<String>> getDiagnostics() {
		return new PC_ImmutableList<Diagnostic<String>>(this.diagnostics);
	}
	
}
