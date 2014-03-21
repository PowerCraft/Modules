package powercraft.traffic.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import powercraft.api.PC_IconRegistry;
import powercraft.api.item.PC_Item;
import powercraft.api.recipes.PC_Recipes;


public class PCtf_ItemSawblade extends PC_Item {
	
	public static final int IRON = 0;
	public static final int GOLD = 1;
	public static final int DIAMOND = 2;
	public static final int OBSIDIAN = 3;
	
	private static final String[] TEXTURE_NAMES = {"iron", "gold", "diamond", "obsidian"};
	
	private static final int[] MAX_DAMAGS = {1250, 640, 8610, 0};
	
	private static final Object[] ITEMS = {Items.iron_ingot, Items.gold_ingot, Items.diamond, Blocks.obsidian};
	
	private static final float[] SPEED = {1, 3, 2f, 2.5f};
	
	private static final int[] TOOL_LEVEL = {2, 0, 3, 4};
	
	private int type;
	
	public PCtf_ItemSawblade(int type){
		setCreativeTab(CreativeTabs.tabTools);
		this.type = type;
		setMaxDamage(MAX_DAMAGS[type]);
		PC_Recipes.addShapedRecipe(new ItemStack(this), "###", "#S#", "###", Character.valueOf('S'), Items.stick, Character.valueOf('#'), ITEMS[type]);
	}
	
	@Override
	public String getRegisterName() {
		return super.getRegisterName()+":"+TEXTURE_NAMES[this.type];
	}

	@Override
	public void registerIcons(PC_IconRegistry iconRegistry) {
		this.itemIcon = iconRegistry.registerIcon(TEXTURE_NAMES[this.type]);
	}
	
	public float getSpeed(){
		return SPEED[this.type];
	}
	
	public int getToolLevel(){
		return TOOL_LEVEL[this.type];
	}
	
}
