package powercraft.misc;

import net.minecraft.item.ItemStack;
import powercraft.api.PC_Api;
import powercraft.api.PC_Module;
import powercraft.misc.block.PCms_BlockClimbingRope;
import powercraft.misc.item.PCms_ItemSaw;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.InstanceFactory;


@Mod(modid = PCms_Misc.NAME, name = PCms_Misc.NAME, version = PCms_Misc.VERSION, dependencies = PCms_Misc.DEPENDENCIES)
public class PCms_Misc extends PC_Module{
	
	public static final String NAME = POWERCRAFT + "-Misc";
	public static final String VERSION = "1.7.2";
	public static final String DEPENDENCIES = "required-after:" + PC_Api.NAME + "@" + PC_Api.VERSION;
	
	public static final PCms_Misc INSTANCE = new PCms_Misc();
	
	public static final PCms_BlockClimbingRope CLIMBING_ROPE = new PCms_BlockClimbingRope();
	
	public static final PCms_ItemSaw SAW = new PCms_ItemSaw();
	
	@InstanceFactory
	public static PCms_Misc factory() {
		return INSTANCE;
	}
	
	private PCms_Misc() {
		
	}
	
	@Override
	public ItemStack getCreativeTabItemStack() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
