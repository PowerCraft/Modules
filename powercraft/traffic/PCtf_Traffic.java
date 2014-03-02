package powercraft.traffic;

import net.minecraft.item.ItemStack;
import powercraft.api.PC_Api;
import powercraft.api.PC_Module;
import powercraft.api.entity.PC_Entities;
import powercraft.traffic.entity.PCtf_EntityMiner;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.InstanceFactory;

@Mod(modid = PCtf_Traffic.NAME, name = PCtf_Traffic.NAME, version = PCtf_Traffic.VERSION, dependencies = PCtf_Traffic.DEPENDENCIES)
public class PCtf_Traffic extends PC_Module {

	public static final String NAME = POWERCRAFT + "-Traffic";
	public static final String VERSION = "1.7.2";
	public static final String DEPENDENCIES = "required-after:" + PC_Api.NAME + "@" + PC_Api.VERSION;
	
	public static final PCtf_Traffic INSTANCE = new PCtf_Traffic();
	
	@InstanceFactory
	public static PCtf_Traffic factory() {
		return INSTANCE;
	}
	
	private PCtf_Traffic() {
		PC_Entities.register(PCtf_EntityMiner.class, 64, 10, false);
	}
	
	@Override
	public ItemStack getCreativeTabItemStack() {
		// TODO Auto-generated method stub
		return null;
	}

}
