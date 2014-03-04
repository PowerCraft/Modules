package powercraft.transport.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.tools.Diagnostic;

import org.lwjgl.input.Keyboard;

import powercraft.api.gres.PC_GresAlign.Fill;
import powercraft.api.gres.PC_GresButton;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresGroupContainer;
import powercraft.api.gres.PC_GresGuiHandler;
import powercraft.api.gres.PC_GresMultilineHighlightingTextEdit;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.PC_GresWindowSideTab;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.autoadd.PC_AutoAdd;
import powercraft.api.gres.autoadd.PC_AutoComplete;
import powercraft.api.gres.autoadd.PC_StringWithInfo;
import powercraft.api.gres.doc.PC_GresHighlighting;
import powercraft.api.gres.events.PC_GresEvent;
import powercraft.api.gres.events.PC_GresKeyEvent;
import powercraft.api.gres.events.PC_GresMouseButtonEvent;
import powercraft.api.gres.events.PC_GresMouseButtonEvent.Event;
import powercraft.api.gres.events.PC_IGresEventListener;
import powercraft.api.gres.font.PC_FontTexture;
import powercraft.api.gres.font.PC_Fonts;
import powercraft.api.gres.layout.PC_GresLayoutHorizontal;
import powercraft.api.gres.layout.PC_GresLayoutVertical;
import powercraft.api.script.miniscript.PC_MiniscriptHighlighting;
import powercraft.transport.tileentity.PCtr_TileEntityBeltScriptable;

public class PCtr_GuiBeltScriptable implements PC_IGresGui, PC_IGresEventListener {

	private PCtr_TileEntityBeltScriptable te;
	private String source;
	private PC_GresMultilineHighlightingTextEdit textEdit;
	private PC_GresButton save;
	private PC_GresButton cancel;
	private List<Diagnostic<? extends Void>> diagnostics;
	
	public PCtr_GuiBeltScriptable(PCtr_TileEntityBeltScriptable te, String source, List<Diagnostic<? extends Void>> diagnostics) {
		this.te = te;
		this.source = source;
		this.diagnostics = diagnostics;
	}

	@Override
	public void initGui(PC_GresGuiHandler gui) {
		PC_FontTexture fontTexture = PC_Fonts.getByName("Consolas", 0, 24);
		PC_GresHighlighting highlighting = PC_MiniscriptHighlighting.makeHighlighting(this.te.getReplacements().keySet());
		PC_AutoAdd autoAdd = PC_MiniscriptHighlighting.makeAutoAdd();
		List<PC_StringWithInfo> list = new ArrayList<PC_StringWithInfo>();
		for(Entry<String, Integer> e:this.te.getReplacements().entrySet()){
			list.add(new PC_StringWithInfo(e.getKey(), "Const: "+e.getValue()));
		}
		PC_AutoComplete autoComplete = PC_MiniscriptHighlighting.makeAutoComplete(list);
		PC_GresWindow win = new PC_GresWindow("Belt");
		win.addSideTab(PC_GresWindowSideTab.createRedstoneSideTab(this.te));
		win.setLayout(new PC_GresLayoutVertical());
		this.textEdit = new PC_GresMultilineHighlightingTextEdit(fontTexture, highlighting, autoAdd, autoComplete, this.source);
		if(this.diagnostics!=null){
			this.textEdit.setErrors(this.diagnostics);
		}
		win.add(this.textEdit);
		PC_GresGroupContainer gc = new PC_GresGroupContainer();
		gc.setFill(Fill.HORIZONTAL);
		gc.setLayout(new PC_GresLayoutHorizontal());
		this.save = new PC_GresButton("Save & Compile");
		this.save.addEventListener(this);
		gc.add(this.save);
		this.cancel = new PC_GresButton("Cancel");
		this.cancel.addEventListener(this);
		gc.add(this.cancel);
		win.add(gc);
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
		}else if(event instanceof PC_GresMouseButtonEvent){
			PC_GresMouseButtonEvent mbe = (PC_GresMouseButtonEvent)event;
			if(mbe.getEvent()==Event.CLICK){
				if(mbe.getComponent()==this.cancel){
					component.getGuiHandler().close();
				}else if(mbe.getComponent()==this.save){
					this.te.sendSaveMessage(this.textEdit.getText());
					this.textEdit.removeErrors();
					this.diagnostics = null;
				}
			}
		}
	}

	public void setErrors(List<Diagnostic<? extends Void>> diagnostics) {
		this.diagnostics = diagnostics;
		this.textEdit.setErrors(diagnostics);
	}
	
}
