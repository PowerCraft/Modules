package powercraft.laser;

import powercraft.laser.block.PCla_BlockLaser;
import powercraft.laser.block.PCla_BlockLaserRender;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class PCla_ClientProxy extends PCla_CommonProxy {

	@Override
	public void registerRenderStuff() {
		int nextID = RenderingRegistry.getNextAvailableRenderId();
		PCla_BlockLaser.renderID = nextID;
		ISimpleBlockRenderingHandler handler = new PCla_BlockLaserRender();
		RenderingRegistry.registerBlockHandler(nextID, handler);
	}

}
