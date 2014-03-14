package powercraft.laser.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import powercraft.api.PC_Direction;
import powercraft.api.PC_IconRegistry;
import powercraft.api.PC_Vec3;
import powercraft.api.PC_Vec3I;
import powercraft.api.PC_Vec4I;
import powercraft.api.block.PC_BlockTileEntity;
import powercraft.api.block.PC_TileEntity;
import powercraft.api.renderer.PC_ModelHelper;
import powercraft.laser.tileEntity.PCla_TileEntityLaser;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PCla_BlockLaser extends PC_BlockTileEntity {

	public static IIcon[] icons = new IIcon[3];

	private final double laserT = 0.4f;
	private final double minLaserP = 0.5 - 0.5 * laserT;
	private final double maxLaserP = 0.5 + 0.5 * laserT;// Used in Rendering

	public PCla_BlockLaser() {
		super(Material.wood);
		setCreativeTab(CreativeTabs.tabBlock);
		setBlockName("Laser");
	}

	@Override
	public Class<? extends PC_TileEntity> getTileEntityClass() {
		return PCla_TileEntityLaser.class;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemInHand) {
		int l = MathHelper.floor_double((double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		switch (l) {
		case 0:
			l = 2;
			break;
		case 1:
			l = 5;
			break;
		case 2:
			l = 3;
			break;
		case 3:
			l = 4;
			break;
		}
		((PCla_TileEntityLaser) world.getTileEntity(x, y, z)).orientation = PC_Direction.fromSide(l);
	}

	@Override
	public boolean canRedstoneConnect(IBlockAccess world, int x, int y, int z, PC_Direction side, int faceSide) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(PC_Direction side, int metadata) {
		if (side.equals(PC_Direction.NORTH))
			return icons[1];
		return icons[0];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(PC_IconRegistry iconRegistry) {
		icons[0] = iconRegistry.registerIcon("side");
		icons[1] = iconRegistry.registerIcon("front");
		icons[2] = iconRegistry.registerIcon("beam");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, int modelId, RenderBlocks renderer) {
		Tessellator tessellator = Tessellator.instance;
		tessellator.addTranslation(x, y, z);
		tessellator.setColorRGBA(255, 255, 255, 255);
		PCla_TileEntityLaser tileLaser = (PCla_TileEntityLaser) world.getTileEntity(x, y, z);
		PC_Vec4I colorToDraw = tileLaser.currColor;
		/*
		 * PC_ModelHelper.drawBox(new PC_Vec3(0.8, 0.8, -1), new PC_Vec3(0.2,
		 * 0.2, 0), tessellator, PCla_BlockLaser.icons[2]);
		 * PC_ModelHelper.drawBox(new PC_Vec3(0.8, 0.8, -2), new PC_Vec3(0.2,
		 * 0.2, -1), tessellator, PCla_BlockLaser.icons[2]);
		 * PC_ModelHelper.drawBox(new PC_Vec3(0.8, 0.8, -3), new PC_Vec3(0.2,
		 * 0.2, -2), tessellator, PCla_BlockLaser.icons[2]);
		 */
		IIcon[] iconsToDraw = new IIcon[6];
		for (int i = 0; i < 6; i++) {
			int index = i;
			if (index == 3)
				index = 2;
			else if (index == 2)
				index = 3;
			iconsToDraw[i] = (((PCla_TileEntityLaser) world.getTileEntity(x, y, z)).orientation.equals(PC_Direction
					.fromSide(index))) ? icons[1] : icons[0];
		}
		PC_ModelHelper.drawBlockAsUsual(tessellator, iconsToDraw);
		PCla_TileEntityLaser tileEntity = (PCla_TileEntityLaser) world.getTileEntity(x, y, z);

		tessellator.setColorRGBA(colorToDraw.x, colorToDraw.y, colorToDraw.z, 255);
		for (PC_Vec3I posToDraw : tileEntity.validLaserPos)
			switch (tileEntity.orientation) {
			case EAST:
				PC_ModelHelper.drawBox(new PC_Vec3(posToDraw.x - x, minLaserP, minLaserP), new PC_Vec3(posToDraw.x - x
						+ 1, maxLaserP, maxLaserP), tessellator, PCla_BlockLaser.icons[2]);
				break;
			case NORTH:
				PC_ModelHelper.drawBox(new PC_Vec3(minLaserP, minLaserP, posToDraw.z - z), new PC_Vec3(maxLaserP,
						maxLaserP, posToDraw.z - z + 1), tessellator, PCla_BlockLaser.icons[2]);
				break;
			case SOUTH:
				PC_ModelHelper.drawBox(new PC_Vec3(minLaserP, minLaserP, posToDraw.z - z), new PC_Vec3(maxLaserP,
						maxLaserP, posToDraw.z - z + 1), tessellator, PCla_BlockLaser.icons[2]);
				break;
			case WEST:
				PC_ModelHelper.drawBox(new PC_Vec3(posToDraw.x - x, minLaserP, minLaserP), new PC_Vec3(posToDraw.x - x
						+ 1, maxLaserP, maxLaserP), tessellator, PCla_BlockLaser.icons[2]);
				break;
			default:
				break;
			}
		tessellator.addTranslation(-x, -y, -z);
		tessellator.setColorRGBA(255, 255, 255, 255);
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderInventoryBlock(int metadata, int modelId, RenderBlocks renderer) {
		Tessellator tessellator = Tessellator.instance;
		tessellator.setTranslation(0, 0, 0);
		PC_ModelHelper.drawBlockAsUsual(this, tessellator, metadata);
		// Idk why it doesn't render in Inventorys. Someone has an Idea?
	}

}
