package powercraft.laser;

import net.minecraft.item.ItemStack;
import powercraft.api.PC_Api;
import powercraft.api.PC_Module;
import powercraft.laser.block.PCla_BlockLaser;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.InstanceFactory;

@Mod(modid = PCla_Laser.NAME, name = PCla_Laser.NAME, version = PCla_Laser.VERSION, dependencies = PCla_Laser.DEPENDENCIES)
public class PCla_Laser extends PC_Module {

	public static final String NAME = POWERCRAFT + "-Laser";
	public static final String VERSION = "1.7.2";
	public static final String DEPENDENCIES = "required-after:" + PC_Api.NAME
			+ "@" + PC_Api.VERSION;

	public static final PCla_Laser INSTANCE = new PCla_Laser();

	public static final PCla_BlockLaser laser = new PCla_BlockLaser();

	@InstanceFactory
	public static PCla_Laser factory() {
		return INSTANCE;
	}

	private PCla_Laser() {

	}

	@Override
	public ItemStack getCreativeTabItemStack() {
		// TODO Auto-generated method stub
		return null;
	}

}
