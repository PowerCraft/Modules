package powercraft.transport.block.tileentity;

import java.util.List;

import javax.tools.Diagnostic;

import org.lwjgl.input.Keyboard;

import powercraft.api.gres.PC_GresAlign.Fill;
import powercraft.api.gres.PC_GresButton;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresGroupContainer;
import powercraft.api.gres.PC_GresGuiHandler;
import powercraft.api.gres.PC_GresMultilineHighlightingTextEdit;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.autoadd.PC_AutoAdd;
import powercraft.api.gres.autoadd.PC_AutoComplete;
import powercraft.api.gres.doc.PC_GresHighlighting;
import powercraft.api.gres.events.PC_GresEvent;
import powercraft.api.gres.events.PC_GresKeyEvent;
import powercraft.api.gres.events.PC_GresMouseButtonEvent;
import powercraft.api.gres.events.PC_GresMouseButtonEvent.Event;
import powercraft.api.gres.events.PC_IGresEventListener;
import powercraft.api.gres.font.PC_FontRenderer;
import powercraft.api.gres.font.PC_FontTexture;
import powercraft.api.gres.font.PC_Fonts;
import powercraft.api.gres.layout.PC_GresLayoutHorizontal;
import powercraft.api.gres.layout.PC_GresLayoutVertical;
import powercraft.api.script.miniscript.PC_MiniScriptHighlighting;

public class PCtr_GuiBeltScriptable implements PC_IGresGui, PC_IGresEventListener {

	private PCtr_TileEntityBeltScriptable te;
	private String source;
	private PC_GresMultilineHighlightingTextEdit textEdit;
	private PC_GresButton save;
	private PC_GresButton cancel;
	
	public PCtr_GuiBeltScriptable(PCtr_TileEntityBeltScriptable te, String source) {
		this.te = te;
		this.source = source;
	}

	@Override
	public void initGui(PC_GresGuiHandler gui) {
		PC_FontTexture fontTexture = PC_Fonts.create(PC_FontRenderer.getFont("Consolas", 0, 24), null);
		PC_GresHighlighting highlighting = PC_MiniScriptHighlighting.makeHighlighting();
		PC_AutoAdd autoAdd = PC_MiniScriptHighlighting.makeAutoAdd();
		PC_AutoComplete autoComplete = PC_MiniScriptHighlighting.makeAutoComplete(te.getReplacements().keySet());
		PC_GresWindow win = new PC_GresWindow("Belt");
		win.setLayout(new PC_GresLayoutVertical());
		textEdit = new PC_GresMultilineHighlightingTextEdit(fontTexture, highlighting, autoAdd, autoComplete, source);
		win.add(textEdit);
		PC_GresGroupContainer gc = new PC_GresGroupContainer();
		gc.setFill(Fill.HORIZONTAL);
		gc.setLayout(new PC_GresLayoutHorizontal());
		save = new PC_GresButton("Save");
		save.addEventListener(this);
		gc.add(save);
		cancel = new PC_GresButton("Cancel");
		cancel.addEventListener(this);
		gc.add(cancel);
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
				if(mbe.getComponent()==cancel){
					component.getGuiHandler().close();
				}else if(mbe.getComponent()==save){
					te.sendSaveMessage(textEdit.getText());
					textEdit.removeErrors();
				}
			}
		}
	}

	public void setErrors(List<Diagnostic<? extends Void>> diagnostics) {
		textEdit.setErrors(diagnostics);
	}
	
}
