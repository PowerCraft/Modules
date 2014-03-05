package powercraft.weasel;

import java.io.File;
import java.io.IOException;

import net.minecraft.item.ItemStack;
import powercraft.api.PC_Api;
import powercraft.api.PC_Module;
import powercraft.api.PC_Utils;
import powercraft.api.script.weasel.PC_Weasel;
import powercraft.api.script.weasel.PC_WeaselClassSave;
import powercraft.api.script.weasel.PC_WeaselEngine;
import powercraft.api.script.weasel.PC_WeaselModule;
import powercraft.weasel.block.PCws_BlockCore;
import powercraft.weasel.engine.PCws_WeaselClassSave;
import powercraft.weasel.engine.PCws_WeaselEngine;
import xscript.runtime.clazz.XClassLoader;
import xscript.runtime.clazz.XZipClassLoader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.InstanceFactory;

@Mod(modid = PCws_Weasel.NAME, name = PCws_Weasel.NAME, version = PCws_Weasel.VERSION, dependencies = PCws_Weasel.DEPENDENCIES)
public class PCws_Weasel extends PC_Module implements PC_WeaselModule {

	public static final String NAME = POWERCRAFT + "-Weasel";
	public static final String VERSION = "1.7.2";
	public static final String DEPENDENCIES = "required-after:" + PC_Api.NAME + "@" + PC_Api.VERSION;
	
	public static final PCws_Weasel INSTANCE = new PCws_Weasel();
	
	public static final File rt = PC_Utils.getPowerCraftFile("Weasel", "rt.zip");
	
	public static final PCws_BlockCore CORE = new PCws_BlockCore();
	
	@InstanceFactory
	public static PCws_Weasel factory() {
		return INSTANCE;
	}
	
	private PCws_Weasel() {
		PC_Weasel.register(this);
	}

	@Override
	public ItemStack getCreativeTabItemStack() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PC_WeaselClassSave createClassSave() {
		return new PCws_WeaselClassSave();
	}

	@Override
	public PC_WeaselEngine createEngine(PC_WeaselClassSave classSave, int memSize) {
		return new PCws_WeaselEngine(classSave, memSize);
	}

	public static XClassLoader getRTClassLoader() {
		try {
			return new XZipClassLoader(rt);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public PC_WeaselEngine loadEngine(PC_WeaselClassSave classSave, byte[] data) {
		try {
			return new PCws_WeaselEngine(classSave, data);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
