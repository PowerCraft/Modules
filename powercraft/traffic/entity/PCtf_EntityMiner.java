package powercraft.traffic.entity;

import net.minecraft.world.World;
import powercraft.api.entity.PC_Entity;
import powercraft.api.renderer.PC_EntityRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PCtf_EntityMiner extends PC_Entity{
	
	private int instruction;
	
	public PCtf_EntityMiner(World world) {
		super(world);
	}

	@Override
	protected void entityInit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpdate() {
		
		super.onUpdate();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void doRender(PC_EntityRenderer<?> renderer, double x, double y, double z, float rotYaw, float timeStamp) {
		// TODO Auto-generated method stub
		
	}

}
