package powercraft.oldlaser.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import powercraft.api.PC_IconRegistry;
import powercraft.api.PC_Vec4I;
import powercraft.api.item.PC_Item;
import powercraft.oldlaser.PCla_EnumLaserEffects;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PCla_ItemLaserEmitter extends PC_Item {

	private String[] names = new String[] { "break", "emit", "damage", "sensor", "cosmetic", "heal", "build", "replace" };

	private IIcon[] icons = new IIcon[this.names.length];

	public PCla_ItemLaserEmitter() {
		this.hasSubtypes = true;
		setCreativeTab(CreativeTabs.tabRedstone);
	}

	public static PCla_EnumLaserEffects getEffect(int meta) {
		switch (meta) {
		case 0:
			return PCla_EnumLaserEffects.BREAK;
		case 1:
			return PCla_EnumLaserEffects.EMIT;
		case 2:
			return PCla_EnumLaserEffects.DAMAGE;
		case 3:
			return PCla_EnumLaserEffects.SENSOR;
		case 4:
			return PCla_EnumLaserEffects.NOTHING;
		case 5:
			return PCla_EnumLaserEffects.HEAL;
		case 6:
			return PCla_EnumLaserEffects.BUILD;
		case 7:
			return PCla_EnumLaserEffects.REPLACE;
		default:
			break;
		}
		return PCla_EnumLaserEffects.NOTHING;
	}

	public static PC_Vec4I getColorModifier() {
		return new PC_Vec4I(255, 255, 255, 255);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean bool) {
		par3List.add("This is a §l§bLASER EMITTER");
		par3List.add("You can change the §bfunctionality§7(effect)");
		par3List.add("of a laser with this Item");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(PC_IconRegistry iconRegistry) {
		for (int i = 0; i < this.names.length; i++) {
			this.icons[i] = iconRegistry.registerIcon(this.names[i]);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getUnlocalizedName(ItemStack itemStack) {
		int i = itemStack.getItemDamage();
		return super.getUnlocalizedName() + "." + this.names[i];
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int meta) {
		return this.icons[meta];
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item item, CreativeTabs creaTab, List itemList) {
		for (int i = 0; i < this.names.length; i++) {
			itemList.add(new ItemStack(this, 1, i));
		}
	}
}
