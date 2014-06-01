package powercraft.traffic;

import net.minecraft.item.ItemStack;
import powercraft.api.PC_Api;
import powercraft.api.PC_Build;
import powercraft.api.PC_Module;
import powercraft.api.entity.PC_Entities;
import powercraft.traffic.entity.PCtf_EntityMiner;
import powercraft.traffic.items.PCtf_ItemEnergyConverter;
import powercraft.traffic.items.PCtf_ItemEngine;
import powercraft.traffic.items.PCtf_ItemSawblade;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.InstanceFactory;

@Mod(modid = PCtf_Traffic.NAME, name = PCtf_Traffic.NAME, version = PCtf_Traffic.VERSION, dependencies = PCtf_Traffic.DEPENDENCIES)
public class PCtf_Traffic extends PC_Module {

	public static final String NAME = POWERCRAFT + "-Traffic";
	public static final String VERSION = PC_Build.BUILD_VERSION;
	public static final String DEPENDENCIES = "required-after:" + PC_Api.NAME + "@" + PC_Api.VERSION;
	
	public static final PCtf_Traffic INSTANCE = new PCtf_Traffic();
	
	public static final PCtf_ItemSawblade SAWBLADE_IRON = new PCtf_ItemSawblade(PCtf_ItemSawblade.IRON);
	public static final PCtf_ItemSawblade SAWBLADE_GOLD = new PCtf_ItemSawblade(PCtf_ItemSawblade.GOLD);
	public static final PCtf_ItemSawblade SAWBLADE_DIAMOND = new PCtf_ItemSawblade(PCtf_ItemSawblade.DIAMOND);
	public static final PCtf_ItemSawblade SAWBLADE_OBSIDIAN = new PCtf_ItemSawblade(PCtf_ItemSawblade.OBSIDIAN);
	
	public static final PCtf_ItemEnergyConverter CONVERTER = new PCtf_ItemEnergyConverter();
	public static final PCtf_ItemEngine ENGINE = new PCtf_ItemEngine();
	
	@InstanceFactory
	public static PCtf_Traffic factory() {
		return INSTANCE;
	}
	
	private PCtf_Traffic() {
		PC_Entities.register(PCtf_EntityMiner.class, 64, 10, false);
		PCtf_EntityMiner.registerRecipe();
	}
	
	@Override
	public ItemStack getCreativeTabItemStack() {
		return new ItemStack(SAWBLADE_OBSIDIAN);
	}

}
