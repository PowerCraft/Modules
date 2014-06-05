package powercraft.teleporter.block;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.World;
import powercraft.api.PC_Material;
import powercraft.api.PC_Utils;
import powercraft.api.PC_Vec4I;
import powercraft.api.block.PC_Block;
import powercraft.teleporter.network.PCtp_TeleporterGlobalNetwork;


public class PCtp_BlockTeleporterNetwork extends PC_Block {
	
	public PCtp_BlockTeleporterNetwork(){
		super(PC_Material.MACHINES);
		setCreativeTab(CreativeTabs.tabDecorations);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		super.breakBlock(world, x, y, z, block, meta);
		PCtp_TeleporterGlobalNetwork.instance().removeEntry(new PC_Vec4I(x, y, z, PC_Utils.getDimensionID(world)));
	}
	
}
