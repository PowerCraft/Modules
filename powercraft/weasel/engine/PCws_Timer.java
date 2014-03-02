package powercraft.weasel.engine;

import net.minecraft.world.World;
import powercraft.api.PC_Side;
import powercraft.api.PC_TickHandler;
import powercraft.api.PC_TickHandler.PC_IWorldTickHandler;
import xscript.runtime.XTimer;

public class PCws_Timer extends XTimer implements PC_IWorldTickHandler {

	public static PCws_Timer INSTANCE = new PCws_Timer();
	
	private static int tick;
	
	private PCws_Timer(){
		PC_TickHandler.registerTickHandler(this);
	}
	
	@Override
	public void onStartTick(PC_Side side, World world) {
		if(side==PC_Side.SERVER && world.getWorldInfo().getVanillaDimension()==0)
			tick++;
	}

	@Override
	public void onEndTick(PC_Side side, World world) {
		//
	}

	@Override
	public long getMilliSeconds() {
		return tick*50;
	}

	
	
}
