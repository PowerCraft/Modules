package powercraft.traffic;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercraft.api.PC_Api;
import powercraft.api.PC_Module;
import powercraft.api.PC_Utils;
import powercraft.api.entity.PC_Entities;
import powercraft.api.recipes.PC_I3DRecipeHandler;
import powercraft.api.recipes.PC_Recipes;
import powercraft.api.recipes.PC_3DRecipe.StructStart;
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
		PC_Recipes.add3DRecipe(true, new PC_I3DRecipeHandler() {
			
			@Override
			public boolean foundStructAt(World world, StructStart structStart) {
				for(int i=0; i<2; i++){
					for(int j=0; j<2; j++){
						for(int k=0; k<2; k++){
							PC_Utils.setAir(world, structStart.relative(i, j, k));
						}
					}
				}
				PCtf_EntityMiner miner = new PCtf_EntityMiner(world, structStart.pos, structStart.dir);
				PC_Utils.spawnEntity(world, miner);
				return true;
			}
		}, new String[]{"II", "CC"}, new String[]{"II", "II"}, 'I', Blocks.iron_block, 'C', Blocks.chest);
	}
	
	@Override
	public ItemStack getCreativeTabItemStack() {
		// TODO Auto-generated method stub
		return null;
	}

}
