package powercraft.laser.item;

import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import powercraft.api.PC_IconRegistry;
import powercraft.api.PC_Vec4I;
import powercraft.api.item.PC_Item;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PCla_ItemLens extends PC_Item {

	private String[] names = new String[] { "blue", "blueGreen", "green", "pink", "red", "white", "yellow" };

	private IIcon[] icons = new IIcon[names.length];

	public PCla_ItemLens() {
		this.hasSubtypes = true;
		setCreativeTab(CreativeTabs.tabRedstone);
	}

	public PC_Vec4I getColorFromMeta(int meta) {
		switch (meta) {
		case 0:
			return new PC_Vec4I(0, 0, 255, 255);
		case 1:
			return new PC_Vec4I(0, 255, 255, 255);
		case 2:
			return new PC_Vec4I(0, 255, 0, 255);
		case 3:
			return new PC_Vec4I(255, 0, 255, 255);
		case 4:
			return new PC_Vec4I(255, 0, 0, 255);
		case 5:
			return new PC_Vec4I(255, 255, 255, 255);
		case 6:
			return new PC_Vec4I(255, 255, 0, 255);
		}
		return new PC_Vec4I(255, 255, 255, 255);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(PC_IconRegistry iconRegistry) {
		for (int i = 0; i < names.length; i++) {
			icons[i] = iconRegistry.registerIcon(names[i]);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getUnlocalizedName(ItemStack itemStack) {
		int i = itemStack.getItemDamage();
		return super.getUnlocalizedName() + "." + names[i];
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int meta) {
		return icons[meta];
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item item, CreativeTabs creaTab, List itemList) {
		for (int i = 0; i < names.length; i++) {
			itemList.add(new ItemStack(this, 1, i));
		}
	}

}
