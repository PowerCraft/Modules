package powercraft.weasel.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import powercraft.api.PC_Field.Flag;
import powercraft.api.script.weasel.PC_WeaselClassSave;
import powercraft.api.script.weasel.PC_WeaselSourceClass;
import powercraft.weasel.PCws_Weasel;
import xscript.compiler.XCompiler;
import xscript.compiler.XSourceProvider;
import xscript.compiler.message.XMessageElement;
import xscript.compiler.tree.XTreeMakeEasy;
import xscript.runtime.XRuntimeException;
import xscript.runtime.clazz.XClassLoader;
import xscript.runtime.clazz.XInputStream;


public class PCws_WeaselClassSave implements XSourceProvider, XClassLoader, PC_WeaselClassSave {

	private HashMap<String, PCws_WeaselSourceClass> sourceFiles = new HashMap<String, PCws_WeaselSourceClass>();
	private List<XMessageElement> globalMessageElements;
	
	public PCws_WeaselClassSave(){
		PCws_WeaselSourceClass sc = new PCws_WeaselSourceClass();
		sc.setSource("/*\n * A XScript powered Core\n */\n\npublic class Main{\n\t\n\tpublic static void main(){\n"+
		"\t\t// TODO write your program here\n\t}\n\t\n}");
		this.sourceFiles.put("Main", sc);
	}
	
	@SuppressWarnings("unused")
	public PCws_WeaselClassSave(NBTTagCompound tagCompound, Flag flag){
		NBTTagList list = (NBTTagList)tagCompound.getTag("list");
		for(int i=0; i<list.tagCount(); i++){
			NBTTagCompound t = list.getCompoundTagAt(i);
			this.sourceFiles.put(t.getString("FileName"), new PCws_WeaselSourceClass(t));
		}
	}
	
	@Override
	public PC_WeaselSourceClass addClass(String name){
		PCws_WeaselSourceClass n = new PCws_WeaselSourceClass();
		this.sourceFiles.put(name, n);
		return n;
	}
	
	@Override
	public void removeClass(String name){
		this.sourceFiles.remove(name);
	}
	
	@Override
	public PC_WeaselSourceClass getClass(String name){
		return this.sourceFiles.get(name);
	}
	
	@Override
	public void endSave() {
		//
	}

	@Override
	public String getClassCompiler(String name) {
		return this.sourceFiles.get(name).getCompiler();
	}

	@Override
	public String getClassSource(String name) {
		return this.sourceFiles.get(name).getSource();
	}

	@Override
	public List<String> getProvidedClasses() {
		List<String> providedClasses = new ArrayList<String>();
		for(Entry<String, PCws_WeaselSourceClass> sourceFile:this.sourceFiles.entrySet()){
			if(sourceFile.getValue().needRecompile()){
				providedClasses.add(sourceFile.getKey());
			}
		}
		return providedClasses;
	}

	@Override
	public void saveClass(String name, byte[] data) {
		this.sourceFiles.get(name).save(data);
	}

	@Override
	public void startSave() {
		//
	}

	@Override
	public XInputStream getInputStream(String name) {
		try{
			PCws_WeaselSourceClass wcs = this.sourceFiles.get(name);
			if(wcs!=null)
				return new XInputStream(wcs.getInputStream(), name);
			for(Entry<String, PCws_WeaselSourceClass> e:this.sourceFiles.entrySet()){
				String className = e.getKey();
				if(name.startsWith(className) && (name.length() == className.length() || (name.length() > className.length() && name.charAt(className.length())=='.'))){
					if(e.getValue().canUseByteCode()){
						return new XInputStream(e.getValue().getInputStream(), className);
					}
					return null;
				}
			}
			return null;
		}catch(IOException e){
			throw new XRuntimeException(e, "error on class loading");
		}
	}
	
	@Override
	public void compileMarked(){
		for(PCws_WeaselSourceClass sourceFile:this.sourceFiles.values()){
			if(sourceFile.needRecompile()){
				sourceFile.clearMessages();
			}
		}
		XCompiler compiler = new XCompiler(PCws_Weasel.getRTClassLoader());
		compiler.addPredefStaticIndirectImports("weasel.Predef");
		compiler.getClassProvider().addClassLoader(this);
		compiler.registerSourceProvider(this);
		compiler.addTreeChanger(new XTreeMakeEasy());
		compiler.compile();
		List<XMessageElement> messageElements = compiler.getMessageList();
		this.globalMessageElements = new ArrayList<XMessageElement>();
		for(XMessageElement messageElement:messageElements){
			PCws_WeaselSourceClass wsc = getClassEx(messageElement.className);
			if(wsc==null){
				this.globalMessageElements.add(messageElement);
			}else{
				wsc.addMessageElement(messageElement);
			}
		}
		if(this.globalMessageElements.isEmpty())
			this.globalMessageElements = null;
	}

	public PCws_WeaselSourceClass getClassEx(String name){
		PCws_WeaselSourceClass wcs = this.sourceFiles.get(name);
		if(wcs!=null)
			return wcs;
		for(Entry<String, PCws_WeaselSourceClass> e:this.sourceFiles.entrySet()){
			String className = e.getKey();
			if(name.startsWith(className) && (name.length() == className.length() || (name.length() > className.length() && name.charAt(className.length())=='.'))){
				return e.getValue();
			}
		}
		return null;
	}

	@Override
	public void saveToNBT(NBTTagCompound tag, Flag flag) {
		NBTTagList list = new NBTTagList();
		for(Entry<String, PCws_WeaselSourceClass> e:this.sourceFiles.entrySet()){
			NBTTagCompound t = new NBTTagCompound();
			e.getValue().saveToNBT(t);
			t.setString("FileName", e.getKey());
			list.appendTag(t);
		}
		tag.setTag("list", list);
	}

	@Override
	public HashMap<String, ? extends PC_WeaselSourceClass> getSources() {
		return this.sourceFiles;
	}
	
}
