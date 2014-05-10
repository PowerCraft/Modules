package powercraft.test.gui;

import org.lwjgl.input.Keyboard;

import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresContainer;
import powercraft.api.gres.PC_GresGuiHandler;
import powercraft.api.gres.PC_GresListBoxWithoutScroll;
import powercraft.api.gres.PC_GresListBoxWithoutScroll.ElementClicked;
import powercraft.api.gres.PC_GresNeedFocusFrame;
import powercraft.api.gres.PC_GresScrollAreaZoomable;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.events.PC_GresEvent;
import powercraft.api.gres.events.PC_GresKeyEvent;
import powercraft.api.gres.events.PC_IGresEventListener;
import powercraft.api.gres.layout.PC_GresLayoutVertical;
import powercraft.api.gres.nodesys.PC_GresNodesysGrid;
import powercraft.api.gres.nodesys.PC_GresNodesysHelper;

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
		PC_GresNodesysHelper.addNodeToGrid(grid, new PC_Vec2I(), 3);
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
				PC_GresListBoxWithoutScroll lbws = new PC_GresListBoxWithoutScroll(PC_GresNodesysHelper.allNodes, true);
				nff.add(lbws);
				component.getGuiHandler().add(nff);
				lbws.takeFocus();
				lbws.addEventListener(this);
				kEvent.consume();
			}
		}else if(event instanceof ElementClicked){
			ElementClicked ec = (ElementClicked)event;
			if(ec.getElement().nextLayer==null){
				PC_GresNodesysHelper.addNodeToGrid(grid, new PC_Vec2I(), ec.getElement().id);
				component.getParent().getParent().takeFocus();
			}
		}
	}
	
}
