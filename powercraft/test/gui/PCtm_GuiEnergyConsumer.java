package powercraft.test.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Keyboard;

import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresAlign.H;
import powercraft.api.gres.PC_GresComboBox;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresContainer;
import powercraft.api.gres.PC_GresGuiHandler;
import powercraft.api.gres.PC_GresListBoxElement;
import powercraft.api.gres.PC_GresListBoxWithoutScroll;
import powercraft.api.gres.PC_GresNeedFocusFrame;
import powercraft.api.gres.PC_GresScrollAreaZoomable;
import powercraft.api.gres.PC_GresTextEdit;
import powercraft.api.gres.PC_GresListBoxWithoutScroll.ElementClicked;
import powercraft.api.gres.PC_GresTextEdit.PC_GresInputType;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.events.PC_GresEvent;
import powercraft.api.gres.events.PC_GresKeyEvent;
import powercraft.api.gres.events.PC_IGresEventListener;
import powercraft.api.gres.layout.PC_GresLayoutVertical;
import powercraft.api.gres.nodesys.PC_GresNodesysConnection;
import powercraft.api.gres.nodesys.PC_GresNodesysEntry;
import powercraft.api.gres.nodesys.PC_GresNodesysGrid;
import powercraft.api.gres.nodesys.PC_GresNodesysNode;

public class PCtm_GuiEnergyConsumer implements PC_IGresGui, PC_IGresEventListener {

	private PC_GresNodesysGrid grid;
	
	@Override
	public void initGui(PC_GresGuiHandler gui) {
		PC_GresWindow window = new PC_GresWindow("Test");
		window.setLayout(new PC_GresLayoutVertical());
		PC_GresScrollAreaZoomable zoomable = new PC_GresScrollAreaZoomable();
		zoomable.setMinSize(new PC_Vec2I(400, 300));
		zoomable.setPrefSize(new PC_Vec2I(400, 300));
		zoomable.setSize(new PC_Vec2I(400, 300));
		PC_GresContainer c = zoomable.getContainer();
		c.setLayout(new PC_GresLayoutVertical());
		this.grid = new PC_GresNodesysGrid();
		this.grid.add(makeNode(3));
		c.add(this.grid);
		window.add(zoomable);
		gui.add(window);
		gui.addEventListener(this);
	}

	@Override
	public void onEvent(PC_GresEvent event) {
		PC_GresComponent component = event.getComponent();
		if(event instanceof PC_GresKeyEvent){
			PC_GresKeyEvent kEvent = (PC_GresKeyEvent)event;
			if(kEvent.getKeyCode()==Keyboard.KEY_A && !kEvent.isRepeatEvents()){
				PC_GresNeedFocusFrame nff = new PC_GresNeedFocusFrame();
				nff.setLayout(new PC_GresLayoutVertical());
				PC_GresListBoxWithoutScroll lbws = new PC_GresListBoxWithoutScroll(getListOfAllNodes(), true);
				nff.add(lbws);
				component.getGuiHandler().add(nff);
				lbws.takeFocus();
				lbws.addEventListener(this);
				kEvent.consume();
			}
		}else if(event instanceof ElementClicked){
			ElementClicked ec = (ElementClicked)event;
			if(ec.getElement().nextLayer==null){
				this.grid.add(makeNode(ec.getElement().id));
				component.getParent().getParent().takeFocus();
			}
		}
	}

	private static PC_GresNodesysNode makeNode(int id){
		PC_GresNodesysNode node;
		PC_GresNodesysEntry entry;
		switch(id){
		case 1:
			node = new PC_GresNodesysNode("Value");
			entry = new PC_GresNodesysEntry("Value");
			entry.add(new PC_GresNodesysConnection(false, false, 0x80202020, 0));
			node.add(entry);
			entry = new PC_GresNodesysEntry("Value");
			entry.add(new PC_GresTextEdit("0", 10, PC_GresInputType.SIGNED_FLOAT));
			node.add(entry);
			return node;
		case 2:
			node = new PC_GresNodesysNode("ItemStack");
			entry = new PC_GresNodesysEntry("ItemStack");
			entry.add(new PC_GresNodesysConnection(false, false, 0x80202020, 0));
			node.add(entry);
			entry = new PC_GresNodesysEntry("Name");
			entry.add(new PC_GresTextEdit("name", 10));
			node.add(entry);
			entry = new PC_GresNodesysEntry("Count");
			entry.add(new PC_GresTextEdit("0", 10, PC_GresInputType.UNSIGNED_INT));
			node.add(entry);
			entry = new PC_GresNodesysEntry("Metadata");
			entry.add(new PC_GresTextEdit("0", 10, PC_GresInputType.UNSIGNED_INT));
			node.add(entry);
			return node;
		case 3:
			node = new PC_GresNodesysNode("Entry");
			entry = new PC_GresNodesysEntry("Prog");
			entry.add(new PC_GresNodesysConnection(true, false, 0x800000FF, 1));
			node.add(entry);
			return node;
		case 4:
			node = new PC_GresNodesysNode("Condition");
			entry = new PC_GresNodesysEntry("Prog");
			entry.add(new PC_GresNodesysConnection(false, true, 0x800000FF, 1));
			entry.add(new PC_GresNodesysConnection(true, false, 0x800000FF, 1));
			node.add(entry);
			entry = new PC_GresNodesysEntry("Prog Cond");
			entry.add(new PC_GresNodesysConnection(true, false, 0x800000FF, 1));
			node.add(entry);
			entry = new PC_GresNodesysEntry("Value1");
			entry.add(new PC_GresComboBox(Arrays.asList("Bigger", "Smaller", "Equal", "Not Equal", "Bigger Equal", "Smaller Equal"), 0));
			node.add(entry);
			entry = new PC_GresNodesysEntry("Value1");
			entry.add(new PC_GresNodesysConnection(true, true, 0x80202020, 0));
			entry.add(new PC_GresTextEdit("0", 10, PC_GresInputType.SIGNED_FLOAT).setAlignH(H.LEFT));
			node.add(entry);
			entry = new PC_GresNodesysEntry("Value2");
			entry.add(new PC_GresNodesysConnection(true, true, 0x80202020, 0));
			entry.add(new PC_GresTextEdit("0", 10, PC_GresInputType.SIGNED_FLOAT).setAlignH(H.LEFT));
			node.add(entry);
			return node;
		default:
			return null;
		}
	}
	
	private static List<PC_GresListBoxElement> getListOfAllNodes(){
		List<PC_GresListBoxElement> list = new ArrayList<PC_GresListBoxElement>();
		List<PC_GresListBoxElement> inputs = new ArrayList<PC_GresListBoxElement>();
		inputs.add(new PC_GresListBoxElement(1, "Value"));
		inputs.add(new PC_GresListBoxElement(2, "ItemStack"));
		list.add(new PC_GresListBoxElement("Inputs", inputs));
		List<PC_GresListBoxElement> prog = new ArrayList<PC_GresListBoxElement>();
		prog.add(new PC_GresListBoxElement(3, "Entry"));
		prog.add(new PC_GresListBoxElement(4, "Condition"));
		list.add(new PC_GresListBoxElement("Prog", prog));
		return list;
	}
	
}
