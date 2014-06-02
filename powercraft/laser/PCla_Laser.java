package powercraft.laser;

import net.minecraft.item.ItemStack;
import powercraft.api.PC_Api;
import powercraft.api.PC_Build;
import powercraft.api.PC_Module;
import powercraft.laser.block.PCla_BlockLaserBuilder;
import powercraft.laser.block.PCla_BlockLaserDamage;
import powercraft.laser.block.PCla_BlockLaserDetector;
import powercraft.laser.block.PCla_BlockLaserHarvester;
import powercraft.laser.block.PCla_BlockLaserPusher;
import powercraft.laser.block.PCla_BlockLaserTractor;
import powercraft.laser.block.PCla_BlockMirror;
import powercraft.laser.block.PCla_BlockPrism;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.InstanceFactory;

@Mod(modid = PCla_Laser.NAME, name = PCla_Laser.NAME, version = PCla_Laser.VERSION, dependencies = PCla_Laser.DEPENDENCIES)
public class PCla_Laser extends PC_Module {

	public static final String NAME = POWERCRAFT + "-Laser";
	public static final String VERSION = PC_Build.BUILD_VERSION;
	public static final String DEPENDENCIES = "required-after:" + PC_Api.NAME + "@" + PC_Api.VERSION;

	public static final PCla_Laser INSTANCE = new PCla_Laser();

	/*public static final PCla_BlockLaser laser = new PCla_BlockLaser();

	public static final PCla_ItemLens lens = new PCla_ItemLens();
	public static final PCla_ItemCatalysator catalysator = new PCla_ItemCatalysator();
	public static final PCla_ItemLaserEmitter laserEmitter = new PCla_ItemLaserEmitter();
	public static final PCla_ItemLaserUpgrade laserUpgrade = new PCla_ItemLaserUpgrade();*/

	public static final PCla_BlockLaserHarvester laserHarvester = new PCla_BlockLaserHarvester();
	
	public static final PCla_BlockLaserDamage laserDamage = new PCla_BlockLaserDamage();
	
	public static final PCla_BlockLaserTractor laserTractor = new PCla_BlockLaserTractor();
	
	public static final PCla_BlockLaserDetector laserDetector = new PCla_BlockLaserDetector();
	
	public static final PCla_BlockLaserBuilder laserBuilder = new PCla_BlockLaserBuilder();
	
	public static final PCla_BlockLaserPusher laserPusher = new PCla_BlockLaserPusher();
	
	public static final PCla_BlockMirror MIRROR = new PCla_BlockMirror();
	
	public static final PCla_BlockPrism PRISM = new PCla_BlockPrism();
	
	@InstanceFactory
	public static PCla_Laser factory() {
		return INSTANCE;
	}

	private PCla_Laser() {

	}

	@Override
	public ItemStack getCreativeTabItemStack() {
		return new ItemStack(laserHarvester);
	}

}
