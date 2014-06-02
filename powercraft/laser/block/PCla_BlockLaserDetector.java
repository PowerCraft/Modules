package powercraft.laser.block;

import java.util.Random;

import net.minecraft.client.renderer.RenderBlocks;
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
import powercraft.api.block.PC_BlockType;
import powercraft.api.renderer.PC_Renderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class PCla_BlockLaserDetector extends PC_Block {

	private static ThreadLocal<Integer> renderPass = new ThreadLocal<Integer>();
	public static IIcon black;
	public static IIcon head;
	
	public PCla_BlockLaserDetector() {
		super(PC_BlockType.MACHINE);
		setCreativeTab(CreativeTabs.tabBlock);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderBlockPass() {
		return PC_Renderer.PASS_TRANSPARENT;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
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
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean canRenderInPass(int pass) {
		renderPass.set(Integer.valueOf(pass));
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(PC_Direction side, int metadata) {
		return PCla_BlockLaserDetector.head;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(PC_IconRegistry iconRegistry) {
		black = iconRegistry.registerIcon("black");
		head = iconRegistry.registerIcon("head");
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, int modelId, RenderBlocks renderer) {
		int pass = renderPass.get().intValue();
		IIcon[] icons = new IIcon[1];
		if(pass==PC_Renderer.PASS_SOLIDE){
			icons[0] = black;
			renderer.setRenderBounds(0, 0, 0, 1, 2/16.0, 1);
			PC_Renderer.renderStandardBlockInWorld(world, x, y, z, icons, -1, 0, renderer);
			renderer.setRenderBounds(7/16.0, 02/16.0, 7/16.0, 9/16.0, 4/16.0, 9/16.0);
			PC_Renderer.renderStandardBlockInWorld(world, x, y, z, icons, -1, 0, renderer);
		}else{
			icons[0] = head;
			renderer.setRenderBounds(4/16.0, 4/16.0, 4/16.0, 12/16.0, 12/16.0, 12/16.0);
			PC_Renderer.renderStandardBlockInWorld(world, x, y, z, icons, -1, 0, renderer);
		}
		return true;
	}

	@Override
	public void renderInventoryBlock(int metadata, int modelId, RenderBlocks renderer) {
		IIcon[] icons = new IIcon[1];
		icons[0] = black;
		renderer.setRenderBounds(0, 0, 0, 1, 2/16.0, 1);
		PC_Renderer.renderStandardBlockInInventory(icons, -1, 0, renderer);
		renderer.setRenderBounds(7/16.0, 02/16.0, 7/16.0, 9/16.0, 4/16.0, 9/16.0);
		PC_Renderer.renderStandardBlockInInventory(icons, -1, 0, renderer);
		icons[0] = head;
		renderer.setRenderBounds(4/16.0, 4/16.0, 4/16.0, 12/16.0, 12/16.0, 12/16.0);
		PC_Renderer.renderStandardBlockInInventory(icons, -1, 0, renderer);
	}
	
}
