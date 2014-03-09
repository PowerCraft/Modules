package powercraft.laser;

import net.minecraft.item.ItemStack;
import powercraft.api.PC_Api;
import powercraft.api.PC_Module;
import powercraft.laser.block.PCla_BlockLaser;
import powercraft.laser.item.PCla_ItemCatalysator;
import powercraft.laser.item.PCla_ItemLaserEmitter;
import powercraft.laser.item.PCla_ItemLaserUpgrade;
import powercraft.laser.item.PCla_ItemLens;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.InstanceFactory;

@Mod(modid = PCla_Laser.NAME, name = PCla_Laser.NAME, version = PCla_Laser.VERSION, dependencies = PCla_Laser.DEPENDENCIES)
public class PCla_Laser extends PC_Module {

	public static final String NAME = POWERCRAFT + "-Laser";
	public static final String VERSION = "1.7.2";
	public static final String DEPENDENCIES = "required-after:" + PC_Api.NAME + "@" + PC_Api.VERSION;

	public static final PCla_Laser INSTANCE = new PCla_Laser();

	public static final PCla_BlockLaser laser = new PCla_BlockLaser();

	public static final PCla_ItemLens lens = new PCla_ItemLens();
	public static final PCla_ItemCatalysator catalysator = new PCla_ItemCatalysator();
	public static final PCla_ItemLaserEmitter laserEmitter = new PCla_ItemLaserEmitter();
	public static final PCla_ItemLaserUpgrade laserUpgrade = new PCla_ItemLaserUpgrade();

	@InstanceFactory
	public static PCla_Laser factory() {
		return INSTANCE;
	}

	private PCla_Laser() {

	}

	@Override
	public ItemStack getCreativeTabItemStack() {
		return new ItemStack(laser);
	}

}
