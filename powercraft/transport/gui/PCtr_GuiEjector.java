package powercraft.transport.gui;

import powercraft.api.PC_RectI;
import powercraft.api.gres.PC_GresAlign.Fill;
import powercraft.api.gres.PC_GresAlign.H;
import powercraft.api.gres.PC_GresAlign.Size;
import powercraft.api.gres.PC_GresButton;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresContainer;
import powercraft.api.gres.PC_GresGroupContainer;
import powercraft.api.gres.PC_GresGuiHandler;
import powercraft.api.gres.PC_GresLabel;
import powercraft.api.gres.PC_GresRadioButton;
import powercraft.api.gres.PC_GresSeparatorH;
import powercraft.api.gres.PC_GresTextEdit;
import powercraft.api.gres.PC_GresTextEdit.PC_GresInputType;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.PC_GresWindowSideTab;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.events.PC_GresEvent;
import powercraft.api.gres.events.PC_GresMouseButtonEvent;
import powercraft.api.gres.events.PC_GresMouseButtonEvent.Event;
import powercraft.api.gres.events.PC_IGresEventListener;
import powercraft.api.gres.layout.PC_GresLayoutHorizontal;
import powercraft.api.gres.layout.PC_GresLayoutVertical;
import powercraft.transport.tileentity.PCtr_TileEntityEjector;


public class PCtr_GuiEjector implements PC_IGresGui, PC_IGresEventListener {
	
	private PCtr_TileEntityEjector ejector;
	
	private PC_GresRadioButton wholeStacks;
	private PC_GresTextEdit wholeStackCount;
	private PC_GresRadioButton singleItems;
	private PC_GresTextEdit singleItemCount;
	private PC_GresRadioButton allContents;
	
	private PC_GresRadioButton firstSlot;
	private PC_GresRadioButton lastSlot;
	private PC_GresRadioButton randomSlot;
	
	private PC_GresButton ok;
	private PC_GresButton cancel;
	
	public PCtr_GuiEjector(PCtr_TileEntityEjector ejector){
		this.ejector = ejector;
	}
	
	@Override
	public void initGui(PC_GresGuiHandler gui) {
		PC_GresWindow window = (PC_GresWindow) new PC_GresWindow("Ejector").setLayout(new PC_GresLayoutVertical());
		window.addSideTab(PC_GresWindowSideTab.createRedstoneSideTab(this.ejector));
		window.addSideTab(PC_GresWindowSideTab.createIOConfigurationSideTab(this.ejector));
		PC_RectI padding = new PC_RectI(2, 2, 2, 2);
		window.add(new PC_GresLabel("Ejection mode:").setAlignH(H.LEFT).setPadding(padding));
		PC_GresContainer gc = (PC_GresContainer) new PC_GresGroupContainer().setLayout(new PC_GresLayoutHorizontal()).setFill(Fill.BOTH);
		gc.add((this.wholeStacks = new PC_GresRadioButton("Whole stacks")).setAlignH(H.LEFT).setPadding(padding));
		gc.add((this.wholeStackCount = new PC_GresTextEdit(""+this.ejector.getStackCount(), 3, PC_GresInputType.UNSIGNED_INT)).setAlignH(H.RIGHT).setPadding(padding));
		window.add(gc);
		gc = (PC_GresContainer) new PC_GresGroupContainer().setLayout(new PC_GresLayoutHorizontal()).setFill(Fill.BOTH);
		gc.add((this.singleItems = new PC_GresRadioButton("Single items", this.wholeStacks)).setAlignH(H.LEFT).setPadding(padding));
		gc.add((this.singleItemCount = new PC_GresTextEdit(""+this.ejector.getItemCount(), 3, PC_GresInputType.UNSIGNED_INT)).setAlignH(H.RIGHT).setPadding(padding));
		window.add(gc);
		window.add((this.allContents = new PC_GresRadioButton("All contents at once", this.wholeStacks)).setAlignH(H.LEFT).setPadding(padding));
		window.add(new PC_GresSeparatorH());
		window.add(new PC_GresLabel("Method of selection:").setAlignH(H.LEFT).setPadding(padding));
		window.add((this.firstSlot = new PC_GresRadioButton("First Slot")).setAlignH(H.LEFT).setPadding(padding));
		window.add((this.lastSlot = new PC_GresRadioButton("Last Slot", this.firstSlot)).setAlignH(H.LEFT).setPadding(padding));
		window.add((this.randomSlot = new PC_GresRadioButton("Random Slot", this.firstSlot)).setAlignH(H.LEFT).setPadding(padding));
		gc = (PC_GresContainer) new PC_GresGroupContainer().setLayout(new PC_GresLayoutHorizontal(Size.BIGGEST)).setAlignH(H.RIGHT);
		gc.add((this.ok = new PC_GresButton("OK")).setFill(Fill.BOTH).setPadding(padding).addEventListener(this));
		gc.add((this.cancel = new PC_GresButton("Cancel")).setFill(Fill.BOTH).setPadding(padding).addEventListener(this));
		window.add(gc);
		switch(this.ejector.getEjectionMode()){
		case PCtr_TileEntityEjector.EJECT_STACKS:
			this.wholeStacks.check();
			break;
		case PCtr_TileEntityEjector.EJECT_ITEMS:
			this.singleItems.check();
			break;
		case PCtr_TileEntityEjector.EJECT_ALL:
			this.allContents.check();
			break;
		default:
			break;
		}
		switch(this.ejector.getSelectionMode()){
		case PCtr_TileEntityEjector.FIRST_SLOT:
			this.firstSlot.check();
			break;
		case PCtr_TileEntityEjector.LAST_SLOT:
			this.lastSlot.check();
			break;
		case PCtr_TileEntityEjector.RANDOM_SLOT:
			this.randomSlot.check();
			break;
		default:
			break;
		}
		gui.add(window);
	}

	@Override
	public void onEvent(PC_GresEvent event) {
		PC_GresComponent component = event.getComponent();
		if(event instanceof PC_GresMouseButtonEvent){
			PC_GresMouseButtonEvent mbe = (PC_GresMouseButtonEvent)event;
			if(mbe.getEvent()==Event.CLICK){
				if(component==this.ok){
					int numStacks = Integer.parseInt(this.wholeStackCount.getText());
					int numItems = Integer.parseInt(this.singleItemCount.getText());
					int ejectionMode = PCtr_TileEntityEjector.EJECT_STACKS;
					if(this.singleItems.getState()){
						ejectionMode = PCtr_TileEntityEjector.EJECT_ITEMS;
					}else if(this.allContents.getState()){
						ejectionMode = PCtr_TileEntityEjector.EJECT_ALL;
					}
					int selectionMode = PCtr_TileEntityEjector.FIRST_SLOT;
					if(this.lastSlot.getState()){
						selectionMode = PCtr_TileEntityEjector.LAST_SLOT;
					}else if(this.randomSlot.getState()){
						selectionMode = PCtr_TileEntityEjector.RANDOM_SLOT;
					}
					this.ejector.setAll(ejectionMode, selectionMode, numStacks, numItems);
					component.getGuiHandler().close();
				}else if(component==this.cancel){
					component.getGuiHandler().close();
				}
			}
		}
	}
	
}
