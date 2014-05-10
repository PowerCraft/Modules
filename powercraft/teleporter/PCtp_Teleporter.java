package powercraft.teleporter;

import net.minecraft.item.ItemStack;
import powercraft.api.PC_Api;
import powercraft.api.PC_Module;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.InstanceFactory;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;


@Mod(modid = PCtp_Teleporter.NAME, name = PCtp_Teleporter.NAME, version = PCtp_Teleporter.VERSION, dependencies = PCtp_Teleporter.DEPENDENCIES)
public class PCtp_Teleporter extends PC_Module {

	public static final String NAME = POWERCRAFT + "-Teleporter";
	public static final String VERSION = "1.7.2";
	public static final String DEPENDENCIES = "required-after:" + PC_Api.NAME + "@" + PC_Api.VERSION;
	
	public static final PCtp_Teleporter INSTANCE = new PCtp_Teleporter();
	
	@InstanceFactory
	public static PCtp_Teleporter factory() {
		return INSTANCE;
	}
	
	private PCtp_Teleporter() {
		
	}
	
	@Override
	public ItemStack getCreativeTabItemStack() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@SuppressWarnings({ "static-method", "unused" })
	@EventHandler
	public void onServerStopping(FMLServerStoppedEvent serverStoppedEvent){
		PCtp_TeleporterSave.cleanup();
	}
	
}
