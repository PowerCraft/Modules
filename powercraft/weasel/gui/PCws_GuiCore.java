package powercraft.weasel.gui;

import java.util.HashMap;

import org.lwjgl.input.Keyboard;

import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresGuiHandler;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.events.PC_GresEvent;
import powercraft.api.gres.events.PC_GresKeyEvent;
import powercraft.api.gres.events.PC_IGresEventListener;
import powercraft.api.gres.layout.PC_GresLayoutVertical;
import powercraft.api.script.weasel.PC_WeaselGresEdit;
import powercraft.api.script.weasel.PC_WeaselGresEdit.SaveEvent;
import powercraft.weasel.tileentity.PCws_TileEntityCore;


public class PCws_GuiCore implements PC_IGresGui, PC_IGresEventListener {
	
	private PCws_TileEntityCore te;
	
	private PC_WeaselGresEdit edit;
	
	private HashMap<String, String> sources;
	
	public PCws_GuiCore(PCws_TileEntityCore te, HashMap<String, String> sources){
		this.te = te;
		this.sources = sources;
		if(sources.isEmpty()){
			sources.put("Main", "/* TODO Report this bug!\n * I'm sorry but the source is lost :(\n * Or at least if you save...\n */");
		}
	}
	
	@Override
	public void initGui(PC_GresGuiHandler gui) {
		PC_GresWindow win = new PC_GresWindow("Core");
		win.setLayout(new PC_GresLayoutVertical());
		win.add(this.edit = new PC_WeaselGresEdit(this.sources));
		this.edit.addEventListener(this);
		gui.add(win);
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
		}else if(event instanceof SaveEvent){
			SaveEvent se = (SaveEvent)event;
			if(se.getComponent()==this.edit){
				this.te.sendSourcesAndCompile(se.getSources());
			}
		}
	}
	
}
