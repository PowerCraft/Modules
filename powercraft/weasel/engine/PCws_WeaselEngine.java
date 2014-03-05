package powercraft.weasel.engine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import powercraft.api.script.weasel.PC_WeaselClassSave;
import powercraft.api.script.weasel.PC_WeaselEngine;
import powercraft.weasel.PCws_Weasel;
import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XClassLoader;

public class PCws_WeaselEngine implements PC_WeaselEngine {

	private XVirtualMachine virtualMachine;
	
	public PCws_WeaselEngine(PC_WeaselClassSave classes, int memSize){
		this.virtualMachine = new XVirtualMachine(PCws_Weasel.getRTClassLoader(), memSize);
		this.virtualMachine.getClassProvider().addClassLoader(PCws_Weasel.getWeaselRTClassLoader());
		this.virtualMachine.getClassProvider().addClassLoader((PCws_WeaselClassSave)classes);
	}
	
	public PCws_WeaselEngine(PC_WeaselClassSave classes, byte[] data) throws IOException {
		List<XClassLoader>classLoader = new ArrayList<XClassLoader>();
		classLoader.add(PCws_Weasel.getRTClassLoader());
		classLoader.add(PCws_Weasel.getWeaselRTClassLoader());
		classLoader.add((PCws_WeaselClassSave)classes);
		this.virtualMachine = new XVirtualMachine(classLoader, new ByteArrayInputStream(data), PCws_Timer.INSTANCE);
	}

	@Override
	public void run(int numInstructions, int numBlocks){
		this.virtualMachine.getThreadProvider().run(numInstructions, numBlocks);
	}
	
	@Override
	public void callMain(String className, String methodName, Object...params) throws NoSuchMethodException{
		this.virtualMachine.invokeFunction(className+"."+methodName, params);
	}

	@Override
	public byte[] save() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			this.virtualMachine.save(baos);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return baos.toByteArray();
	}
	
}
