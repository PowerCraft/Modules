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
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PCla_ItemLaserUpgrade extends PC_Item {

	private String[] names = new String[] { "2laser", "3laser", "4laser", "2upgrade", "3upgrade", "4upgrade",
			"5upgrade", "rotatingItems", "switchingItems", "colorMixer" };

	private IIcon[] icons = new IIcon[this.names.length];

	public PCla_ItemLaserUpgrade() {
		this.hasSubtypes = true;
		setCreativeTab(CreativeTabs.tabRedstone);
	}

	public static PC_Vec4I getColorModifier() {
		return new PC_Vec4I(255, 255, 255, 255);
	}

	public static int getAddedNumUpgrades(int meta) {
		switch (meta) {
		case 3:
			return 1;
		case 4:
			return 2;
		case 5:
			return 3;
		case 6:
			return 4;
		default:
			return 0;
		}
	}

	public static int getAddedNumLaserThings(int meta) {
		switch (meta) {
		case 0:
			return 1;
		case 1:
			return 2;
		case 2:
			return 3;
		default:
			return 0;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean bool) {
		par3List.add("This is a §l§bUPGRADE");
		par3List.add("You §bupgrade§7 your");
		par3List.add("laser with this Item");
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
