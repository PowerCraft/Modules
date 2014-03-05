package powercraft.weasel.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.lwjgl.input.Keyboard;

import powercraft.api.PC_ClientUtils;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresAlign.Fill;
import powercraft.api.gres.PC_GresButton;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresGroupContainer;
import powercraft.api.gres.PC_GresGuiHandler;
import powercraft.api.gres.PC_GresListBox;
import powercraft.api.gres.PC_GresListBoxWithoutScroll;
import powercraft.api.gres.PC_GresMultilineHighlightingTextEdit;
import powercraft.api.gres.PC_GresNeedFocusFrame;
import powercraft.api.gres.PC_GresTab;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.autoadd.PC_AutoAdd;
import powercraft.api.gres.dialog.PC_GresDialogInput;
import powercraft.api.gres.dialog.PC_GresDialogInput.EventInput;
import powercraft.api.gres.dialog.PC_GresDialogInput.EventInputChanged;
import powercraft.api.gres.dialog.PC_GresDialogYesNo;
import powercraft.api.gres.dialog.PC_GresDialogYesNo.EventYes;
import powercraft.api.gres.doc.PC_GresHighlighting;
import powercraft.api.gres.events.PC_GresEvent;
import powercraft.api.gres.events.PC_GresKeyEvent;
import powercraft.api.gres.events.PC_GresMouseButtonEvent;
import powercraft.api.gres.events.PC_GresMouseButtonEvent.Event;
import powercraft.api.gres.events.PC_GresMouseButtonEventResult;
import powercraft.api.gres.events.PC_IGresEventListener;
import powercraft.api.gres.font.PC_FontRenderer;
import powercraft.api.gres.font.PC_FontTexture;
import powercraft.api.gres.font.PC_Fonts;
import powercraft.api.gres.layout.PC_GresLayoutHorizontal;
import powercraft.api.gres.layout.PC_GresLayoutVertical;
import powercraft.api.script.weasel.PC_WeaselHighlighting;
import powercraft.weasel.tileentity.PCws_TileEntityCore;


public class PCws_GuiCore implements PC_IGresGui, PC_IGresEventListener {
	
	private PC_FontTexture fontTexture = PC_Fonts.create(PC_FontRenderer.getFont("Consolas", 0, 24), null);
	private PC_GresHighlighting highlighting = PC_WeaselHighlighting.makeHighlighting();
	private PC_AutoAdd autoAdd = PC_WeaselHighlighting.makeAutoAdd();
	
	private PCws_TileEntityCore te;
	
	PC_GresTab tab;
	
	HashMap<String, String> sources;
	
	private List<String> remove;
	
	PC_GresListBox listBox;
	
	private PC_GresButton save;
	private PC_GresButton cancel;
	
	private static final String[] LISTBOXELEMENTS = {"new", "rename", "delete"};
	private static final String[] TABELEMENTS1 = {"close"};
	private static final String[] TABELEMENTSMORE = {"close", "close others", "close all"};
	
	public PCws_GuiCore(PCws_TileEntityCore te, HashMap<String, String> sources){
		this.te = te;
		this.sources = sources;
		if(sources.isEmpty()){
			sources.put("Main", "/* TODO Report this bug!\n * I'm sorry but the source is lost :(\n * Or at least if you save...\n */");
		}
		this.remove = new ArrayList<String>(this.sources.keySet());
	}
	
	@Override
	public void initGui(PC_GresGuiHandler gui) {
		PC_GresWindow win = new PC_GresWindow("Core");
		win.setLayout(new PC_GresLayoutVertical());
		List<String> list = new ArrayList<String>(this.sources.keySet());
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
		gc = new PC_GresGroupContainer();
		gc.setFill(Fill.HORIZONTAL);
		gc.setLayout(new PC_GresLayoutHorizontal());
		String[] array = list.toArray(new String[list.size()]);
		Arrays.sort(array);
		gc.add(this.listBox = new PC_GresListBox(Arrays.asList(array)));
		this.listBox.setFill(Fill.VERTICAL);
		this.listBox.addEventListener(this);
		this.tab = new PC_GresTab();
		this.tab.addEventListener(this);
		this.tab.setMinSize(new PC_Vec2I(300, 200).add(this.tab.getFrame().getSize()).add(this.tab.getFrame().getLocation()));
		gc.add(this.tab);
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
				if(component==this.cancel){
					component.getGuiHandler().close();
				}else if(component==this.save){
					List<PC_GresComponent> cl = this.tab.getChildren();
					for(PC_GresComponent c:cl){
						String n = this.tab.getTabName(c);
						this.sources.put(n, c.getText());
					}
					HashMap<String, String> hm = new HashMap<String, String>();
					for(String s:this.remove){
						hm.put(s, null);
					}
					hm.putAll(this.sources);
					this.te.sendSourcesAndCompile(hm);
				}
			}else if(mbe.getEvent()==Event.DOWN && mbe.isDoubleClick()){
				if(component==this.listBox){
					String c = this.listBox.getSelected();
					PC_GresComponent t = this.tab.getTab(c);
					if(t==null){
						this.tab.add(c, t = new PC_GresMultilineHighlightingTextEdit(this.fontTexture, this.highlighting, this.autoAdd, null, this.sources.get(c)));
					}
					t.moveToTop();
				}
			}
		}else if(event instanceof PC_GresMouseButtonEventResult){
			PC_GresMouseButtonEventResult mbe = (PC_GresMouseButtonEventResult)event;
			if(mbe.getEvent()==Event.CLICK && mbe.getEventButton()==1){
				if(mbe.getComponent()==this.listBox){
					String selected = this.listBox.getSelected();
					PC_GresNeedFocusFrame frame = new PC_GresNeedFocusFrame(mbe.getMouse().add(component.getRealLocation()));
					ListBoxEventListener lbel = new ListBoxEventListener(selected);
					frame.addEventListener(lbel);
					frame.setLayout(new PC_GresLayoutVertical());
					PC_GresListBoxWithoutScroll lb;
					if(selected==null){
						lb = new PC_GresListBoxWithoutScroll(Arrays.asList(new String[]{"new"}));
					}else{
						lb = new PC_GresListBoxWithoutScroll(Arrays.asList(LISTBOXELEMENTS));
					}
					lb.addEventListener(lbel);
					frame.add(lb);
					component.getGuiHandler().add(frame);
					frame.takeFocus();
				}else if(mbe.getComponent()==this.tab){
					PC_GresComponent tabContent = this.tab.getVisibleTab();
					if(tabContent!=null){
						PC_GresNeedFocusFrame frame = new PC_GresNeedFocusFrame(mbe.getMouse().add(component.getRealLocation()));
						TabEventListener evl = new TabEventListener(tabContent);
						frame.addEventListener(evl);
						frame.setLayout(new PC_GresLayoutVertical());
						PC_GresListBoxWithoutScroll lb;
						if(this.tab.getChildren().size()==1){
							lb = new PC_GresListBoxWithoutScroll(Arrays.asList(TABELEMENTS1));
						}else{
							lb = new PC_GresListBoxWithoutScroll(Arrays.asList(TABELEMENTSMORE));
						}
						lb.addEventListener(evl);
						frame.add(lb);
						component.getGuiHandler().add(frame);
						frame.takeFocus();
					}
				}
			}
		}
	}
	
	private class TabEventListener implements PC_IGresEventListener{

		private PC_GresComponent tabContent;
		
		public TabEventListener(PC_GresComponent tabContent) {
			this.tabContent = tabContent;
		}

		@Override
		public void onEvent(PC_GresEvent event) {
			PC_GresComponent component = event.getComponent();
			if(event instanceof PC_GresKeyEvent){
				PC_GresKeyEvent kEvent = (PC_GresKeyEvent)event;
				if(kEvent.getKeyCode()==Keyboard.KEY_ESCAPE){
					component.getGuiHandler().takeFocus();
					kEvent.consume();
				}
			}else if(event instanceof PC_GresMouseButtonEventResult){
				if(component instanceof PC_GresListBoxWithoutScroll){
					int selection = ((PC_GresListBoxWithoutScroll)component).getSelection();
					switch(selection){
					case 0:{
						String tabName = PCws_GuiCore.this.tab.getTabName(this.tabContent);
						PCws_GuiCore.this.sources.put(tabName, this.tabContent.getText());
						PCws_GuiCore.this.tab.remove(this.tabContent);
						break;}
					case 1:{
						List<PC_GresComponent> cl = PCws_GuiCore.this.tab.getChildren();
						for(PC_GresComponent c:cl){
							String n = PCws_GuiCore.this.tab.getTabName(c);
							PCws_GuiCore.this.sources.put(n, c.getText());
						}
						String tabName = PCws_GuiCore.this.tab.getTabName(this.tabContent);
						PCws_GuiCore.this.tab.removeAll();
						PCws_GuiCore.this.tab.add(tabName, this.tabContent);
						break;}
					case 2:{
						List<PC_GresComponent> cl = PCws_GuiCore.this.tab.getChildren();
						for(PC_GresComponent c:cl){
							String n = PCws_GuiCore.this.tab.getTabName(c);
							PCws_GuiCore.this.sources.put(n, c.getText());
						}
						PCws_GuiCore.this.tab.removeAll();
						break;}
					default:
						break;
					}
					PCws_GuiCore.this.tab.takeFocus();
				}
			}
		}
		
	}
	
	private class ListBoxEventListener implements PC_IGresEventListener{

		private String selected;
		
		public ListBoxEventListener(String selected) {
			this.selected = selected;
		}

		@Override
		public void onEvent(PC_GresEvent event) {
			PC_GresComponent component = event.getComponent();
			if(event instanceof PC_GresKeyEvent){
				PC_GresKeyEvent kEvent = (PC_GresKeyEvent)event;
				if(kEvent.getKeyCode()==Keyboard.KEY_ESCAPE){
					component.getGuiHandler().takeFocus();
					kEvent.consume();
				}
			}else if(event instanceof PC_GresMouseButtonEventResult){
				if(component instanceof PC_GresListBoxWithoutScroll){
					int selection = ((PC_GresListBoxWithoutScroll)component).getSelection();
					switch(selection){
					case 0:{
						PC_GresDialogInput dialogInput = new PC_GresDialogInput("New", "", "Create");
						dialogInput.addEventListener(new DialogNewEventListener());
						component.getGuiHandler().add(dialogInput);
						dialogInput.takeFocus();
						break;}
					case 1:{
						PC_GresDialogInput dialogInput = new PC_GresDialogInput("Rename", PCws_GuiCore.this.listBox.getSelected(), "Rename");
						dialogInput.addEventListener(new DialogRenameEventListener(this.selected));
						component.getGuiHandler().add(dialogInput);
						dialogInput.takeFocus();
						break;}
					case 2:{
						PC_GresDialogYesNo dialogYesNo = new PC_GresDialogYesNo("Delete", "Delete "+PCws_GuiCore.this.listBox.getSelected()+" really?", "Yes");
						dialogYesNo.addEventListener(new DialogRemoveEventListener(this.selected));
						component.getGuiHandler().add(dialogYesNo);
						dialogYesNo.takeFocus();
						break;}
					default:
						break;
					}
				}
			}
		}
		
	}
	
	private class DialogNewEventListener implements PC_IGresEventListener{

		DialogNewEventListener() {}

		@Override
		public void onEvent(PC_GresEvent event) {
			if(event instanceof EventInput){
				newClass(((EventInput)event).getInput());
			}else if(event instanceof EventInputChanged){
				EventInputChanged inputChanged = (EventInputChanged)event;
				inputChanged.setEnabled(checkName(inputChanged.getInput()));
			}
		}
		
	}
	
	private class DialogRenameEventListener implements PC_IGresEventListener{

		private String selected;
		
		DialogRenameEventListener(String selected) {
			this.selected = selected;
		}
		
		@Override
		public void onEvent(PC_GresEvent event) {
			if(event instanceof EventInput){
				renameClass(((EventInput)event).getInput(), this.selected);
			}else if(event instanceof EventInputChanged){
				EventInputChanged inputChanged = (EventInputChanged)event;
				inputChanged.setEnabled(checkName(inputChanged.getInput()));
			}
		}
		
	}
	
	private class DialogRemoveEventListener implements PC_IGresEventListener{

		private String selected;
		
		DialogRemoveEventListener(String selected) {
			this.selected = selected;
		}
		
		@Override
		public void onEvent(PC_GresEvent event) {
			if(event instanceof EventYes){
				removeClass(this.selected);
			}
		}
		
	}
	
	void newClass(String name){
		String n = name;
		int index = n.lastIndexOf('.');
		String pack;
		if(index==-1){
			pack = "";
		}else{
			pack = "package "+n.substring(0, index)+";\n\n";
			n = n.substring(index+1);
		}
		String source = pack+"/**\n * Class "+name+" generated by "+PC_ClientUtils.mc().thePlayer.getCommandSenderName()+"\n */\n"+
				"\npublic class "+n+"{\n\t\n}";
		this.sources.put(name, source);
		PC_GresComponent component;
		this.tab.add(name, component = new PC_GresMultilineHighlightingTextEdit(this.fontTexture, this.highlighting, this.autoAdd, null, source));
		component.moveToTop();
		updateListBox();
	}

	void renameClass(String name, String selected){
		if(name.equals(selected))
			return;
		this.sources.put(name, this.sources.remove(selected));
		PC_GresComponent component = this.tab.getTab(selected);
		if(component!=null){
			this.tab.remove(component);
			this.tab.add(name, component);
			component.moveToTop();
		}
		updateListBox();
	}
	
	void removeClass(String selected){
		if(this.sources.size()>1){
			this.sources.remove(selected);
			PC_GresComponent tabComponent = this.tab.getTab(selected);
			if(tabComponent!=null)
				this.tab.remove(tabComponent);
			updateListBox();
		}
	}

	private void updateListBox(){
		Set<String> s = this.sources.keySet();
		String[] array = s.toArray(new String[s.size()]);
		Arrays.sort(array);
		this.listBox.setElements(Arrays.asList(array));
	}
	
	boolean checkName(String name){
		if(name.isEmpty())
			return false;
		if(this.sources.get(name)!=null){
			return false;
		}
		char c = name.charAt(0);
		if(!((c>='A' && c<='Z') || (c>='a' && c<='z')))
			return false;
		boolean dot = false;
		for(int i=1; i<name.length(); i++){
			c = name.charAt(i);
			if(!((c>='A' && c<='Z') || (c>='a' && c<='z') || (c>='0' && c<='9') || c=='_' || (!dot && c=='.')))
				return false;
			dot =  c=='.';
		}
		return !dot;
	}
	
}
