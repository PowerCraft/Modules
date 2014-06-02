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

public class PCla_ItemLens extends PC_Item {

	private String[] names = new String[] { "blue", "aqua", "green", "pink", "red", "white", "yellow" };

	private IIcon[] icons = new IIcon[this.names.length];

	public PCla_ItemLens() {
		this.hasSubtypes = true;
		setCreativeTab(CreativeTabs.tabRedstone);
	}

	public static PC_Vec4I getColorFromMeta(int meta) {
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
		default:
			break;
		}
		return new PC_Vec4I(255, 255, 255, 255);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(PC_IconRegistry iconRegistry) {
		for (int i = 0; i < this.names.length; i++) {
			this.icons[i] = iconRegistry.registerIcon(this.names[i]);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean bool) {
		par3List.add("This is a §lLENS");
		par3List.add("You can change the §bcolor§7");
		par3List.add("of a laser with this Item");
		par3List.add("This lens has the color");
		String formatStr = "§";
		switch (par1ItemStack.getItemDamage()) {
		case 0:
			formatStr += "9";
			break;
		case 1:
			formatStr += "b";
			break;
		case 2:
			formatStr += "a";
			break;
		case 3:
			formatStr += "d";
			break;
		case 4:
			formatStr += "c";
			break;
		case 5:
			formatStr += "f";
			break;
		case 6:
			formatStr += "e";
			break;
		default:
			break;
		}
		par3List.add(formatStr + this.names[par1ItemStack.getItemDamage()]);
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
