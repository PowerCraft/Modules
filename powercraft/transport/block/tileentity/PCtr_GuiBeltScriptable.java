package powercraft.transport.block.tileentity;

import org.lwjgl.input.Keyboard;

import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresGuiHandler;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.events.PC_GresEvent;
import powercraft.api.gres.events.PC_GresKeyEvent;
import powercraft.api.gres.events.PC_IGresEventListener;

public class PCtr_GuiBeltScriptable implements PC_IGresGui, PC_IGresEventListener {

	private PCtr_TileEntityBeltScriptable te;
	
	public PCtr_GuiBeltScriptable(PCtr_TileEntityBeltScriptable te) {
		this.te = te;
	}

	@Override
	public void initGui(PC_GresGuiHandler gui) {
		gui.add(new PC_GresWindow("test"));
		gui.addEventListener(this);
	}

	@Override
	public void onEvent(PC_GresEvent event) {
		PC_GresComponent component = event.getComponent();
		if(event instanceof PC_GresKeyEvent){
			PC_GresKeyEvent kEvent = (PC_GresKeyEvent)event;
			if(kEvent.getKeyCode()==Keyboard.KEY_ESCAPE){
				component.getGuiHandler().close();
			}
		}
	}
	
}
