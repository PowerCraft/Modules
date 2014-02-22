package powercraft.laser.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import powercraft.api.PC_Direction;
import powercraft.api.PC_IconRegistry;
import powercraft.api.PC_Vec3;
import powercraft.api.block.PC_BlockTileEntity;
import powercraft.api.block.PC_TileEntity;
import powercraft.api.redstone.PC_RedstoneConnectable;
import powercraft.api.renderer.PC_ModelHelper;
import powercraft.laser.tileEntity.PCla_TileEntityLaser;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PCla_BlockLaser extends PC_BlockTileEntity implements PC_RedstoneConnectable {

	@SideOnly(Side.CLIENT)
	public static IIcon[] icons = new IIcon[3];
	public static int renderID = 0;

	public PCla_BlockLaser() {
		super(Material.wood);
		setCreativeTab(CreativeTabs.tabBlock);

	}

	@Override
	public Class<? extends PC_TileEntity> getTileEntityClass() {
		return PCla_TileEntityLaser.class;
	}

	// Client Side Stuff
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(PC_Direction side, int metadata) {
		if (side.equals(PC_Direction.NORTH))
			return icons[1];
		return icons[0];
	}

	/*@SideOnly(Side.CLIENT)
	@Override
	Use the standate PowerCraft renderer, it will pass the call to PC_Block.renderWorldBlock(IBlockAccess world, int x, int y, int z, int modelId, RenderBlocks renderer)
	public int getRenderType() {
		return renderID;
	}*/

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
	// Draw the thing here!!!
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, int modelId, RenderBlocks renderer) {
		Tessellator tessellator = Tessellator.instance;
		tessellator.addTranslation(x, y, z);
		// for each quad/triangle:
		/*tessellator.addVertex(0.8, 0.2, -1);
		tessellator.addVertex(0.8, 0.8, -1);
		tessellator.addVertex(0.8, 0.8, -3);
		tessellator.addVertex(0.8, 0.2, -3);*/
		PC_ModelHelper.drawBox(new PC_Vec3(0.8, 0.8, -1), new PC_Vec3(0.2, 0.2, 0), tessellator,
				PCla_BlockLaser.icons[2]);
		PC_ModelHelper.drawBox(new PC_Vec3(0.8, 0.8, -2), new PC_Vec3(0.2, 0.2, -1), tessellator,
				PCla_BlockLaser.icons[2]);
		PC_ModelHelper.drawBox(new PC_Vec3(0.8, 0.8, -3), new PC_Vec3(0.2, 0.2, -2), tessellator,
				PCla_BlockLaser.icons[2]);
		PC_ModelHelper.drawBlockAsUsual(this, tessellator, 0);
		// again and again, until you're done, then:
		tessellator.addTranslation(-x, -y, -z);
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	// And here!!!
	public void renderInventoryBlock(int metadata, int modelId,
			RenderBlocks renderer) {
		// TODO Auto-generated method stub
		super.renderInventoryBlock(metadata, modelId, renderer);
	}
	
	
	
}
