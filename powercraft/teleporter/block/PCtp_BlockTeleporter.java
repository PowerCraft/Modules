package powercraft.teleporter.block;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.World;
import powercraft.api.PC_Material;
import powercraft.api.PC_Utils;
import powercraft.api.PC_Vec4I;
import powercraft.api.block.PC_BlockTileEntity;
import powercraft.api.block.PC_TileEntity;
import powercraft.teleporter.network.PCtp_TeleporterGlobalNetwork;
import powercraft.teleporter.tileentity.PCtp_TileEntityTeleporter;


public class PCtp_BlockTeleporter extends PC_BlockTileEntity {

	public PCtp_BlockTeleporter() {
		super(PC_Material.MACHINES);
		setCreativeTab(CreativeTabs.tabDecorations);
	}

	@Override
	public Class<? extends PC_TileEntity> getTileEntityClass() {
		return PCtp_TileEntityTeleporter.class;
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		PCtp_TeleporterGlobalNetwork.instance().removeEntry(new PC_Vec4I(x, y, z, PC_Utils.getDimensionID(world)));
	}
	
}
