package powercraft.laser.block;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import powercraft.api.PC_Direction;
import powercraft.api.PC_IconRegistry;
import powercraft.api.PC_Utils;
import powercraft.api.beam.PC_BeamHitResult;
import powercraft.api.beam.PC_IBeam;
import powercraft.api.block.PC_Block;


public class PCla_BlockLaserDetector extends PC_Block {

	public static IIcon side;
	
	public PCla_BlockLaserDetector() {
		super(Material.ground);
		setCreativeTab(CreativeTabs.tabBlock);
	}

	@Override
	public PC_BeamHitResult onHitByBeam(World world, int x, int y, int z, PC_IBeam beam) {
		PC_Utils.setMetadata(world, x, y, z, 2, PC_Utils.BLOCK_NOTIFY | PC_Utils.BLOCK_UPDATE | PC_Utils.BLOCK_ONLY_SERVERSIDE);
		world.scheduleBlockUpdate(x, y, z, this, 1);
		return PC_BeamHitResult.CONTINUE;
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	@SuppressWarnings("hiding")
	@Override
	public int getRedstonePowerValue(IBlockAccess world, int x, int y, int z, PC_Direction side, int faceSide) {
		return PC_Utils.getMetadata(world, x, y, z)>0?15:0;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		int metadata = PC_Utils.getMetadata(world, x, y, z);
		if(metadata>0){
			PC_Utils.setMetadata(world, x, y, z, metadata-1, PC_Utils.BLOCK_NOTIFY | PC_Utils.BLOCK_UPDATE | PC_Utils.BLOCK_ONLY_SERVERSIDE);
			if(metadata>1){
				world.scheduleBlockUpdate(x, y, z, this, 1);
			}
		}
	}

	@Override
	public int tickRate(World world) {
		return 1;
	}
	
	@SuppressWarnings("hiding")
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(PC_Direction side, int metadata) {
		return PCla_BlockLaserDetector.side;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(PC_IconRegistry iconRegistry) {
		side = iconRegistry.registerIcon("side");
	}
	
}
