package powercraft.test.gui;

import java.util.Arrays;

import org.lwjgl.input.Keyboard;

import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresAlign.H;
import powercraft.api.gres.PC_GresComboBox;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresContainer;
import powercraft.api.gres.PC_GresGuiHandler;
import powercraft.api.gres.PC_GresScrollAreaZoomable;
import powercraft.api.gres.PC_GresTextEdit;
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
		PC_GresNodesysGrid grid = new PC_GresNodesysGrid();
		PC_GresNodesysNode node = new PC_GresNodesysNode("Compare");
		PC_GresNodesysEntry entry = new PC_GresNodesysEntry("Prog");
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
		grid.add(node);
		node = new PC_GresNodesysNode("Value");
		entry = new PC_GresNodesysEntry("Value");
		entry.add(new PC_GresNodesysConnection(false, false, 0x80202020, 0));
		node.add(entry);
		entry = new PC_GresNodesysEntry("Value");
		entry.add(new PC_GresTextEdit("0", 10, PC_GresInputType.SIGNED_FLOAT));
		node.add(entry);
		grid.add(node);
		//grid.add(new PC_GresButton("Test1"));
		//PC_GresSlider s = new PC_GresSlider();
		//s.setFill(Fill.HORIZONTAL);
		//grid.add(s);
		//grid.add(new PC_GresComboBox(Arrays.asList("S1", "S2", "S3"), 0));
		c.add(grid);
		window.add(zoomable);
		gui.add(window);
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
