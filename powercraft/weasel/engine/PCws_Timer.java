package powercraft.weasel.engine;

import powercraft.api.PC_Utils;
import xscript.runtime.XTimer;

public class PCws_Timer extends XTimer {

	public static PCws_Timer INSTANCE = new PCws_Timer();

	private PCws_Timer(){
		
	}

	@Override
	public long getMilliSeconds() {
		return PC_Utils.mcs().getTickCounter()*50;
	}

	
	
}
