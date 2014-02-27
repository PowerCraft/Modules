package powercraft.transport;

import net.minecraft.item.ItemStack;
import powercraft.api.PC_Api;
import powercraft.api.PC_Module;
import powercraft.api.network.PC_PacketHandler;
import powercraft.transport.block.PCtr_BlockBeltScriptable;
import powercraft.transport.block.PCtr_PacketSetEntitySpeed;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.InstanceFactory;

@Mod(modid = PCtr_Transport.NAME, name = PCtr_Transport.NAME, version = PCtr_Transport.VERSION, dependencies=PCtr_Transport.DEPENDENCIES)
public class PCtr_Transport extends PC_Module {

	public static final String NAME = POWERCRAFT+"-Transport";
	public static final String VERSION = "1.7.2";
	public static final String DEPENDENCIES = "required-after:"+PC_Api.NAME+"@"+PC_Api.VERSION;
	
	public static final PCtr_Transport INSTANCE = new PCtr_Transport();
	
	public static final PCtr_BlockBeltScriptable ScriptableBelt = new PCtr_BlockBeltScriptable();
	
	@InstanceFactory
	public static PCtr_Transport factory(){
		return INSTANCE;
	}

	private PCtr_Transport(){
		PC_PacketHandler.registerPacket(PCtr_PacketSetEntitySpeed.class);
	}
	
	@Override
	public ItemStack getCreativeTabItemStack() {
		return new ItemStack(ScriptableBelt);
	}

}
