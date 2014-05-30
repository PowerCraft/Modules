package powercraft.laser;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Utils;
import powercraft.api.renderer.PC_Renderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class PCla_LaserRenderer {
	
	private PCla_LaserRenderer(){
		PC_Utils.staticClassConstructor();
	}
	
	public static void renderLaser(World world, int x, int y, int z, PC_Direction facing, RenderBlocks renderer, IIcon side, IIcon inside, IIcon black, IIcon white){
				
		IIcon[] icons = new IIcon[6];
		icons[0] = side;
		icons[1] = side;
		icons[2] = side;
		icons[3] = side;
		icons[4] = side;
		icons[5] = side;
		
		double minY = 0;
		double minZ = 0;
		double maxY = 1;
		double maxZ = 1;
		
		if(facing!=PC_Direction.UP){
			icons[0] = facing==PC_Direction.DOWN?white:inside;
			renderer.setRenderBounds(0, 1-2/16.0, 0, 1, 1, 1);
			PC_Renderer.renderStandardBlockInWorld(world, x, y, z, icons, -1, 0, renderer);
			icons[0] = side;
			maxY = 1-2/16.0;
		}
		if(facing!=PC_Direction.DOWN){
			icons[1] = facing==PC_Direction.UP?white:inside;
			renderer.setRenderBounds(0, 0, 0, 1, 2/16.0, 1);
			PC_Renderer.renderStandardBlockInWorld(world, x, y, z, icons, -1, 0, renderer);
			icons[1] = side;
			minY = 2/16.0;
		}
		if(facing!=PC_Direction.SOUTH){
			icons[2] = facing==PC_Direction.NORTH?white:inside;
			renderer.setRenderBounds(0, minY, 1-2/16.0, 1, maxY, 1);
			PC_Renderer.renderStandardBlockInWorld(world, x, y, z, icons, -1, 0, renderer);
			icons[2] = side;
			maxZ = 1-2/16.0;
		}
		if(facing!=PC_Direction.NORTH){
			icons[3] = facing==PC_Direction.SOUTH?white:inside;
			renderer.setRenderBounds(0, minY, 0, 1, maxY, 2/16.0);
			PC_Renderer.renderStandardBlockInWorld(world, x, y, z, icons, -1, 0, renderer);
			icons[3] = side;
			minZ = 2/16.0;
		}
		if(facing!=PC_Direction.EAST){
			icons[4] = facing==PC_Direction.WEST?white:inside;
			renderer.setRenderBounds(1-2/16.0, minY, minZ, 1, maxY, maxZ);
			PC_Renderer.renderStandardBlockInWorld(world, x, y, z, icons, -1, 0, renderer);
			icons[4] = side;
		}
		if(facing!=PC_Direction.WEST){
			icons[5] = facing==PC_Direction.EAST?white:inside;
			renderer.setRenderBounds(0, minY, minZ, 2/16.0, maxY, maxZ);
			PC_Renderer.renderStandardBlockInWorld(world, x, y, z, icons, -1, 0, renderer);
			icons[5] = side;
		}
		icons[0] = black;
		icons[1] = black;
		icons[2] = black;
		icons[3] = black;
		icons[4] = black;
		icons[5] = black;
		
		if(facing.offsetX==0){
			renderer.setRenderBounds(2/16.0, 7/16.0, 7/16.0, 14/16.0, 9/16.0, 9/16.0);
			PC_Renderer.renderStandardBlockInWorld(world, x, y, z, icons, -1, 0, renderer);
		}
		if(facing.offsetY==0){
			renderer.setRenderBounds(7/16.0, 2/16.0, 7/16.0, 9/16.0, 14/16.0, 9/16.0);
			PC_Renderer.renderStandardBlockInWorld(world, x, y, z, icons, -1, 0, renderer);
		}
		if(facing.offsetZ==0){
			renderer.setRenderBounds(7/16.0, 7/16.0, 2/16.0, 9/16.0, 9/16.0, 14/16.0);
			PC_Renderer.renderStandardBlockInWorld(world, x, y, z, icons, -1, 0, renderer);
		}
		
		double minX = 5/16.0;
		minY = 5/16.0;
		minZ = 5/16.0;
		double maxX = 11/16.0;
		maxY = 11/16.0;
		maxZ = 11/16.0;
		
		if(facing.offsetX<0){
			minX = 1/16.0;
			maxX = 3/16.0;
		}else if(facing.offsetX>0){
			minX = 13/16.0;
			maxX = 15/16.0;
		}else if(facing.offsetY<0){
			minY = 1/16.0;
			maxY = 3/16.0;
		}else if(facing.offsetY>0){
			minY = 13/16.0;
			maxY = 15/16.0;
		}else if(facing.offsetZ<0){
			minZ = 1/16.0;
			maxZ = 3/16.0;
		}else if(facing.offsetZ>0){
			minZ = 13/16.0;
			maxZ = 15/16.0;
		}
		
		renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
		PC_Renderer.renderStandardBlockInWorld(world, x, y, z, icons, -1, 0, renderer);
		
		icons[0] = white;
		icons[1] = white;
		icons[2] = white;
		icons[3] = white;
		icons[4] = white;
		icons[5] = white;
		
		double dx = facing.offsetX!=0?3/16.0:0;
		double dy = facing.offsetY!=0?3/16.0:0;
		double dz = facing.offsetZ!=0?3/16.0:0;
		
		renderer.setRenderBounds(6/16.0-dx, 6/16.0-dy, 6/16.0-dz, 10/16.0+dx, 10/16.0+dy, 10/16.0+dz);
		PC_Renderer.renderStandardBlockInWorld(world, x, y, z, icons, -1, 0, renderer);
		
	}
	
	public static void renderLaserInInventory(RenderBlocks renderer, IIcon side, IIcon inside, IIcon black, IIcon white){
		
		IIcon[] icons = new IIcon[6];
		icons[1] = side;
		icons[2] = side;
		icons[3] = side;
		icons[4] = side;
		icons[5] = side;
		
		icons[0] = inside;
		renderer.setRenderBounds(0, 1-2/16.0, 0, 1, 1, 1);
		PC_Renderer.renderStandardBlockInInventory(icons, -1, 0, renderer);
		icons[0] = side;
		
		icons[1] = inside;
		renderer.setRenderBounds(0, 0, 0, 1, 2/16.0, 1);
		PC_Renderer.renderStandardBlockInInventory(icons, -1, 0, renderer);
		icons[1] = side;

		icons[3] = white;
		renderer.setRenderBounds(0, 2/16.0, 0, 1, 1-2/16.0, 2/16.0);
		PC_Renderer.renderStandardBlockInInventory(icons, -1, 0, renderer);
		icons[3] = side;
		
		icons[4] = inside;
		renderer.setRenderBounds(1-2/16.0, 2/16.0, 2/16.0, 1, 1-2/16.0, 1);
		PC_Renderer.renderStandardBlockInInventory(icons, -1, 0, renderer);
		icons[4] = side;
		
		icons[5] = inside;
		renderer.setRenderBounds(0, 2/16.0, 2/16.0, 2/16.0, 1-2/16.0, 1);
		PC_Renderer.renderStandardBlockInInventory(icons, -1, 0, renderer);
		
		icons[0] = black;
		icons[1] = black;
		icons[2] = black;
		icons[3] = black;
		icons[4] = black;
		icons[5] = black;
		
		renderer.setRenderBounds(2/16.0, 7/16.0, 7/16.0, 14/16.0, 9/16.0, 9/16.0);
		PC_Renderer.renderStandardBlockInInventory(icons, -1, 0, renderer);
		renderer.setRenderBounds(7/16.0, 2/16.0, 7/16.0, 9/16.0, 14/16.0, 9/16.0);
		PC_Renderer.renderStandardBlockInInventory(icons, -1, 0, renderer);
		
		renderer.setRenderBounds(5/16.0, 5/16.0, 13/16.0, 11/16.0, 11/16.0, 15/16.0);
		PC_Renderer.renderStandardBlockInInventory(icons, -1, 0, renderer);
		
		icons[0] = white;
		icons[1] = white;
		icons[2] = white;
		icons[3] = white;
		icons[4] = white;
		icons[5] = white;
		
		renderer.setRenderBounds(6/16.0, 6/16.0, 3/16.0, 10/16.0, 10/16.0, 13/16.0);
		PC_Renderer.renderStandardBlockInInventory(icons, -1, 0, renderer);
		
	}
	
}
