package powercraft.weasel.engine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import powercraft.api.PC_Field.Flag;
import powercraft.api.PC_ImmutableList;
import powercraft.api.script.PC_FakeDiagnostic;
import powercraft.api.script.weasel.PC_IWeaselEvent;
import powercraft.api.script.weasel.PC_Weasel;
import powercraft.api.script.weasel.PC_WeaselContainer;
import powercraft.api.script.weasel.PC_WeaselSourceClass;
import powercraft.weasel.PCws_Weasel;
import xscript.compiler.XCompiler;
import xscript.compiler.XSourceProvider;
import xscript.compiler.message.XMessageElement;
import xscript.compiler.tree.XTreeMakeEasy;
import xscript.runtime.XRuntimeException;
import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XClassLoader;
import xscript.runtime.clazz.XInputStream;
import xscript.runtime.clazz.XWrapper;
import xscript.runtime.method.XMethod;
import xscript.runtime.object.XObject;
import xscript.runtime.threads.XInterruptTerminatedListener;
import xscript.runtime.threads.XThread;
import xscript.runtime.threads.XThreadErroredListener;


public class PCws_WeaselContainer implements XSourceProvider, XClassLoader, PC_WeaselContainer, XThreadErroredListener, XInterruptTerminatedListener {
	
	private int memSize;
	
	private HashMap<String, PCws_WeaselSourceClass> sourceFiles = new HashMap<String, PCws_WeaselSourceClass>();
	
	private List<Diagnostic<String>> globalDiagnostics;
	
	private XVirtualMachine virtualMachine;
	
	private PrintStream errorStream;
	
	private Object handler;
	
	private List<Class<?>> nativeClasses = new ArrayList<Class<?>>();
	
	public PCws_WeaselContainer(String deviceName, int memSize){
		this.memSize = memSize;
		if(deviceName!=null){
			PCws_WeaselSourceClass sc = new PCws_WeaselSourceClass();
			sc.setSource("/*\n * A Weasel powered "+deviceName+"\n */\n\npublic class Main{\n\t\n\t// This is the entry point."
					+ "\n\t// It needs the name \"main\" in the Main class\n\t// no params and returns void\n\t// and it has to be static\n\tpublic static "
					+ "void main(){\n\t\t// TODO write your program here\n\t}\n\t\n}");
			this.sourceFiles.put("Main", sc);
		}
	}
	
	@SuppressWarnings("unused")
	public PCws_WeaselContainer(NBTTagCompound tagCompound, Flag flag){
		this.memSize = tagCompound.getInteger("memSize");
		NBTTagList list = (NBTTagList)tagCompound.getTag("list");
		for(int i=0; i<list.tagCount(); i++){
			NBTTagCompound t = list.getCompoundTagAt(i);
			this.sourceFiles.put(t.getString("FileName"), new PCws_WeaselSourceClass(t));
		}
		this.globalDiagnostics = loadDiagnostics(tagCompound, "diagnostics");
		if(tagCompound.hasKey("engine")){
			ByteArrayInputStream bais = new ByteArrayInputStream(tagCompound.getByteArray("engine"));
			List<XClassLoader> classLoader = new ArrayList<XClassLoader>();
			classLoader.add(PCws_Weasel.getRTClassLoader());
			classLoader.add(PCws_Weasel.getWeaselRTClassLoader());
			classLoader.add(this);
			try {
				this.virtualMachine = new XVirtualMachine(classLoader, bais, PCws_Timer.INSTANCE);
				this.virtualMachine.getThreadProvider().registerThreadErroredListener(this);
				this.virtualMachine.getThreadProvider().registerInterruptTerminatedListener(this);
				PCws_WeaselNative.registerNatives(this.virtualMachine);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void setHandler(Object handler){
		this.handler = handler;
		if(this.virtualMachine!=null){
			this.virtualMachine.setUserData(this.handler);
		}
	}
	
	private void setupVirtualMachine(){
		this.virtualMachine = new XVirtualMachine(PCws_Weasel.getRTClassLoader(), this.memSize);
		this.virtualMachine.getClassProvider().addClassLoader(PCws_Weasel.getWeaselRTClassLoader());
		this.virtualMachine.getClassProvider().addClassLoader(this);
		PCws_WeaselNative.registerNatives(this.virtualMachine);
		this.virtualMachine.setTimer(PCws_Timer.INSTANCE);
		this.virtualMachine.getThreadProvider().registerThreadErroredListener(this);
		this.virtualMachine.getThreadProvider().registerInterruptTerminatedListener(this);
		this.virtualMachine.setUserData(this.handler);
		for(Class<?>c:this.nativeClasses){
			this.virtualMachine.getNativeProvider().addNativeClass(c);
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
	public boolean compileMarked(String[] staticIndirectImports, String[] indirectImports){
		for(PCws_WeaselSourceClass sourceFile:this.sourceFiles.values()){
			if(sourceFile.needRecompile()){
				sourceFile.clearDiagnostics();
			}
		}
		XCompiler compiler = new XCompiler(PCws_Weasel.getRTClassLoader());
		compiler.addPredefStaticIndirectImports("weasel.Predef");
		if(staticIndirectImports!=null){
			for(String s:staticIndirectImports){
				compiler.addPredefStaticIndirectImports(s);
			}
		}
		if(indirectImports!=null){
			for(String s:indirectImports){
				compiler.addPredefIndirectImport(s);
			}
		}
		compiler.getClassProvider().addClassLoader(PCws_Weasel.getWeaselRTClassLoader());
		compiler.getClassProvider().addClassLoader(this);
		compiler.registerSourceProvider(this);
		compiler.addTreeChanger(new XTreeMakeEasy());
		boolean errored = compiler.compile();
		List<XMessageElement> messageElements = compiler.getMessageList();
		this.globalDiagnostics = new ArrayList<Diagnostic<String>>();
		for(XMessageElement messageElement:messageElements){
			PCws_WeaselSourceClass wsc = getClassEx(messageElement.className);
			Diagnostic<String> diagnostic = toDiagnostic(messageElement, wsc);
			if(wsc==null){
				this.globalDiagnostics.add(diagnostic);
			}else{
				wsc.addDiagnostic(diagnostic);
			}
		}
		if(this.globalDiagnostics.isEmpty())
			this.globalDiagnostics = null;
		setupVirtualMachine();
		return errored;
	}

	private static Diagnostic<String> toDiagnostic(XMessageElement me, PCws_WeaselSourceClass wsc){
		long line = me.lineDesk.startLine;
		String message = me.key;
		String args[];
		if(me.args==null || me.args.length==0){
			args = null;
		}else{
			args = new String[me.args.length];
			for(int i=0; i<args.length; i++){
				args[i] = me.args[i]==null?"null":me.args[i].toString();
			}
		}
		long columnNumber = me.lineDesk.startLinePos;
		long endPos = getPos(me.lineDesk.endLine, me.lineDesk.endLinePos, wsc);
		long pos = getPos(me.lineDesk.startLine, me.lineDesk.startLinePos, wsc);
		String source = me.className;
		long startPos = pos;
		Kind kind = null;
		switch(me.level){
		case ERROR:
			kind = Kind.ERROR;
			break;
		case INFO:
			kind = Kind.NOTE;
			break;
		case WARNING:
			kind = Kind.WARNING;
			break;
		default:
			break;
		}
		return new PC_FakeDiagnostic(line, message, args, columnNumber, endPos, pos, source, startPos, kind, PC_FakeDiagnostic.DEFAULT_TRANSLATER);
	}
	
	private static long getPos(int line, int c, PCws_WeaselSourceClass wsc){
		String source = wsc.getSource();
		int p = -1;
		int l = line;
		while(l>1){
			p = source.indexOf('\n', p+1);
			if(p==-1)
				return Diagnostic.NOPOS;
			l--;
		}
		return p+c;
		
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
		tag.setInteger("memSize", this.memSize);
		NBTTagList list = new NBTTagList();
		for(Entry<String, PCws_WeaselSourceClass> e:this.sourceFiles.entrySet()){
			NBTTagCompound t = new NBTTagCompound();
			e.getValue().saveToNBT(t);
			t.setString("FileName", e.getKey());
			list.appendTag(t);
		}
		tag.setTag("list", list);
		saveDiagnostics(tag, "diagnostics", this.globalDiagnostics);
		if(this.virtualMachine!=null){
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				this.virtualMachine.save(baos);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			tag.setByteArray("engine", baos.toByteArray());
		}
	}

	@Override
	public HashMap<String, ? extends PC_WeaselSourceClass> getSources() {
		return this.sourceFiles;
	}
	
	@Override
	public List<Diagnostic<String>> getDiagnostics() {
		return this.globalDiagnostics==null?null:new PC_ImmutableList<Diagnostic<String>>(this.globalDiagnostics);
	}
	
	@Override
	public void saveDiagnosticsToNBT(NBTTagCompound tagCompound){
		saveDiagnostics(tagCompound, "#default", this.globalDiagnostics);
		for(Entry<String, PCws_WeaselSourceClass> e:this.sourceFiles.entrySet()){
			saveDiagnostics(tagCompound, e.getKey(), e.getValue().getDiagnostics());
		}
	}
	
	static void saveDiagnostics(NBTTagCompound tagCompound, String name, List<Diagnostic<String>> diagnostics){
		if(diagnostics==null || diagnostics.isEmpty())
			return;
		NBTTagList list = new NBTTagList();
		for(Diagnostic<String> diagnostic:diagnostics){
			list.appendTag(PC_FakeDiagnostic.toCompound(diagnostic));
		}
		tagCompound.setTag(name, list);
	}
	
	static List<Diagnostic<String>> loadDiagnostics(NBTTagCompound tagCompound, String name){
		if(tagCompound.hasKey(name)){
			List<Diagnostic<String>> diagnostics = new ArrayList<Diagnostic<String>>();
			NBTTagList list = (NBTTagList)tagCompound.getTag(name);
			for(int i=0; i<list.tagCount(); i++){
				diagnostics.add(PC_FakeDiagnostic.fromCompound(list.getCompoundTagAt(i), PC_Weasel.DIAGNOSTIC_TRANSLATER));
			}
			return diagnostics;
		}
		return null;
	}

	@Override
	public void run(int numInstructions, int numBlocks){
		if(this.virtualMachine!=null)
			this.virtualMachine.getThreadProvider().run(numInstructions, numBlocks);
	}
	
	@Override
	public void callMain(String className, String methodName, Object...params) throws NoSuchMethodException{
		if(this.virtualMachine!=null)
			this.virtualMachine.invokeFunction(className+"."+methodName, params);
	}

	@Override
	public void onEvent(PC_IWeaselEvent event) {
		XClass xClass = this.virtualMachine.getClassProvider().getXClass(event.getEntryClass());
		XMethod xMethod = xClass.getMethod(event.getEntryMethod());
		Object[] params = event.getParams();
		long[] l = new long[params.length];
		for(int i=0; i<params.length; i++){
			if(params[i] instanceof Float){
				l[i] = Float.floatToIntBits((Float)params[i]);
			}else if(params[i] instanceof Double){
				l[i] = Double.doubleToLongBits((Double)params[i]);
			}else if(params[i] instanceof Number){
				l[i] = ((Number)params[i]).longValue();
			}else if(params[i] instanceof String){
				l[i] = virtualMachine.getObjectProvider().createString(null, null, (String)params[i]);
			}else{
				l[i] = virtualMachine.getObjectProvider().getPointer((XObject)params[i]);
			}
		}
		this.virtualMachine.getThreadProvider().interrupt(event.getEventName(), null, xMethod, null, l);
	}

	@Override
	public void registerNativeClass(Class<?> c) {
		if(this.virtualMachine!=null)
			this.virtualMachine.getNativeProvider().addNativeClass(c);
		if(!this.nativeClasses.contains(c)){
			this.nativeClasses.add(c);
		}
	}

	@Override
	public void setErrorOutput(PrintStream errorStream) {
		this.errorStream = errorStream;
	}
	
	@SuppressWarnings("hiding")
	@Override
	public void onThreadErrored(XVirtualMachine virtualMachine, XThread thread) {
		if(this.errorStream==null)
			return;
		byte[] userData = thread.getUserData();
		if(userData!=null && userData.length>0){
			if(userData[0]==1){
				this.errorStream.println("Fatal Error");
				return;
			}
		}
		long pointer = thread.getException();
		XClass xClass = this.virtualMachine.getClassProvider().getXClass("weasel.Intern");
		XMethod xMethod = xClass.getMethod("t2s(xscript.lang.Throwable)xscript.lang.String");
		XThread interrupt = virtualMachine.getThreadProvider().importantInterrupt("ThrowableToString", xMethod, null, new long[]{pointer});
		interrupt.setUserData(new byte[]{1});
	}

	@SuppressWarnings("hiding")
	@Override
	public void onInterruptTerminated(XVirtualMachine virtualMachine, XThread thread) {
		byte[] userData = thread.getUserData();
		if(userData!=null && userData.length>0){
			if(userData[0]==1){
				String message = virtualMachine.getObjectProvider().getString(virtualMachine.getObjectProvider().getObject(thread.getResult()));
				this.errorStream.println(message);
			}
		}
	}
	
	
	
}
