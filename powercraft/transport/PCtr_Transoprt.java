package powercraft.transport;

import net.minecraft.item.ItemStack;
import powercraft.api.PC_Api;
import powercraft.api.PC_Module;
import powercraft.api.network.PC_PacketHandler;
import powercraft.transport.block.PCtr_BlockScriptableBelt;
import powercraft.transport.block.PCtr_PacketSetEntitySpeed;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.InstanceFactory;

@Mod(modid = PCtr_Transoprt.NAME, name = PCtr_Transoprt.NAME, version = PCtr_Transoprt.VERSION, dependencies=PCtr_Transoprt.DEPENDENCIES)
public class PCtr_Transoprt extends PC_Module {

	public static final String NAME = POWERCRAFT+"-Transport";
	public static final String VERSION = "1.7.2";
	public static final String DEPENDENCIES = "required-after:"+PC_Api.NAME+"@"+PC_Api.VERSION;
	
	public static final PCtr_Transoprt INSTANCE = new PCtr_Transoprt();
	
	public static final PCtr_BlockScriptableBelt ScriptableBelt = new PCtr_BlockScriptableBelt();
	
	@InstanceFactory
	public static PCtr_Transoprt factory(){
		return INSTANCE;
	}

	private PCtr_Transoprt(){
		PC_PacketHandler.registerPacket(PCtr_PacketSetEntitySpeed.class);
	}
	
	@Override
	public ItemStack getCreativeTabItemStack() {
		// TODO Auto-generated method stub
		return null;
	}

}
