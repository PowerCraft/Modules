package powercraft.weasel;

import java.io.File;
import java.io.IOException;

import net.minecraft.item.ItemStack;
import powercraft.api.PC_Api;
import powercraft.api.PC_Module;
import powercraft.api.PC_Utils;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.autoadd.PC_AutoCompleteDisplay;
import powercraft.api.gres.doc.PC_GresDocument;
import powercraft.api.gres.doc.PC_GresDocumentLine;
import powercraft.api.script.weasel.PC_Weasel;
import powercraft.api.script.weasel.PC_WeaselContainer;
import powercraft.api.script.weasel.PC_WeaselGresEdit;
import powercraft.api.script.weasel.PC_WeaselModule;
import powercraft.weasel.block.PCws_BlockCore;
import powercraft.weasel.engine.PCws_AutoCompleteHelper;
import powercraft.weasel.engine.PCws_WeaselContainer;
import powercraft.weasel.multiblock.PCws_MultiblockItemWeaselCable;
import xscript.runtime.clazz.XClassLoader;
import xscript.runtime.clazz.XZipClassLoader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.InstanceFactory;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = PCws_Weasel.NAME, name = PCws_Weasel.NAME, version = PCws_Weasel.VERSION, dependencies = PCws_Weasel.DEPENDENCIES)
public class PCws_Weasel extends PC_Module implements PC_WeaselModule {

	public static final String NAME = POWERCRAFT + "-Weasel";
	public static final String VERSION = "1.7.2";
	public static final String DEPENDENCIES = "required-after:" + PC_Api.NAME + "@" + PC_Api.VERSION;
	
	public static final PCws_Weasel INSTANCE = new PCws_Weasel();
	
	public static final File rt = PC_Utils.getPowerCraftFile("Weasel", "rt.zip");
	
	public static final File weaselrt = PC_Utils.getPowerCraftFile("Weasel", "weasel.zip");
	
	public static final PCws_BlockCore CORE = new PCws_BlockCore();
	
	public static final PCws_MultiblockItemWeaselCable WEASEL_CABLE = new PCws_MultiblockItemWeaselCable();
	
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
	public PC_WeaselContainer createContainer(String deviceName, int memSize) {
		return new PCws_WeaselContainer(deviceName, memSize);
	}
	
	public static XClassLoader getRTClassLoader() {
		try {
			return new XZipClassLoader(rt);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static XClassLoader getWeaselRTClassLoader() {
		try {
			return new XZipClassLoader(weaselrt);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void makeComplete(PC_GresComponent component, PC_GresDocument document, PC_GresDocumentLine line, int x, PC_AutoCompleteDisplay info, PC_WeaselGresEdit weaselGresEdit) {
		PCws_AutoCompleteHelper.makeComplete(component, document, line, x, info, weaselGresEdit);
	}


}
