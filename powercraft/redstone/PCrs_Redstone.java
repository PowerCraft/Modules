package powercraft.redstone;

import net.minecraft.item.ItemStack;
import powercraft.api.PC_Api;
import powercraft.api.PC_Module;
import powercraft.redstone.multiblock.PCrs_MultiblockItemRedstoneBundleCable;
import powercraft.redstone.multiblock.PCrs_MultiblockItemRedstoneCable;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.InstanceFactory;


@Mod(modid = PCrs_Redstone.NAME, name = PCrs_Redstone.NAME, version = PCrs_Redstone.VERSION, dependencies = PCrs_Redstone.DEPENDENCIES)
public class PCrs_Redstone extends PC_Module{
	
	public static final String NAME = POWERCRAFT + "-Redstone";
	public static final String VERSION = "1.7.2";
	public static final String DEPENDENCIES = "required-after:" + PC_Api.NAME + "@" + PC_Api.VERSION;
	
	public static final PCrs_Redstone INSTANCE = new PCrs_Redstone();
	
	public static final PCrs_MultiblockItemRedstoneCable REDSTONE_CABLE = new PCrs_MultiblockItemRedstoneCable();
	
	public static final PCrs_MultiblockItemRedstoneBundleCable REDSTONE_BUNDLE_CABLE = new PCrs_MultiblockItemRedstoneBundleCable();
	
	@InstanceFactory
	public static PCrs_Redstone factory() {
		return INSTANCE;
	}
	
	private PCrs_Redstone() {
		
	}
	
	@Override
	public ItemStack getCreativeTabItemStack() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
