package powercraft.test;

import net.minecraft.item.ItemStack;
import powercraft.api.PC_Api;
import powercraft.api.PC_Module;
import powercraft.test.block.PCtm_BlockEnergyConsumer;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.InstanceFactory;

@Mod(modid = PCtm_Test.NAME, name = PCtm_Test.NAME, version = PCtm_Test.VERSION, dependencies = PCtm_Test.DEPENDENCIES)
public class PCtm_Test extends PC_Module {

	public static final String NAME = POWERCRAFT + "-Test";
	public static final String VERSION = "1.7.2";
	public static final String DEPENDENCIES = "required-after:" + PC_Api.NAME + "@" + PC_Api.VERSION;
	
	public static final PCtm_Test INSTANCE = new PCtm_Test();
	
	public static final PCtm_BlockEnergyConsumer CONSUMER = new PCtm_BlockEnergyConsumer();
	
	@InstanceFactory
	public static PCtm_Test factory() {
		return INSTANCE;
	}
	
	private PCtm_Test() {

	}
	
	@Override
	public ItemStack getCreativeTabItemStack() {
		// TODO Auto-generated method stub
		return null;
	}

}
