package powercraft.teleporter;

import net.minecraft.item.ItemStack;
import powercraft.api.PC_Api;
import powercraft.api.PC_Module;
import powercraft.teleporter.block.PCtp_BlockTeleporter;
import powercraft.teleporter.block.PCtp_BlockTeleporterNetwork;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.InstanceFactory;


@Mod(modid = PCtp_Teleporter.NAME, name = PCtp_Teleporter.NAME, version = PCtp_Teleporter.VERSION, dependencies = PCtp_Teleporter.DEPENDENCIES)
public class PCtp_Teleporter extends PC_Module {

	public static final String NAME = POWERCRAFT + "-Teleporter";
	public static final String VERSION = "1.7.2";
	public static final String DEPENDENCIES = "required-after:" + PC_Api.NAME + "@" + PC_Api.VERSION;
	
	public static final PCtp_Teleporter INSTANCE = new PCtp_Teleporter();
	
	public static final PCtp_BlockTeleporter TELEPORTER = new PCtp_BlockTeleporter();
	
	public static final PCtp_BlockTeleporterNetwork TELEPORTER_NETWORK = new PCtp_BlockTeleporterNetwork();
	
	@InstanceFactory
	public static PCtp_Teleporter factory() {
		return INSTANCE;
	}
	
	private PCtp_Teleporter() {
		
	}
	
	@Override
	public ItemStack getCreativeTabItemStack() {
		return new ItemStack(TELEPORTER);
	}

}
