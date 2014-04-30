package powercraft.test.gui;

import java.util.Arrays;

import org.lwjgl.input.Keyboard;

import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresAlign.Fill;
import powercraft.api.gres.PC_GresButton;
import powercraft.api.gres.PC_GresComboBox;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresContainer;
import powercraft.api.gres.PC_GresGuiHandler;
import powercraft.api.gres.PC_GresScrollAreaZoomable;
import powercraft.api.gres.PC_GresSlider;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.events.PC_GresEvent;
import powercraft.api.gres.events.PC_GresKeyEvent;
import powercraft.api.gres.events.PC_IGresEventListener;
import powercraft.api.gres.layout.PC_GresLayoutVertical;

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
		c.add(new PC_GresButton("Test1"));
		PC_GresSlider s = new PC_GresSlider();
		s.setFill(Fill.HORIZONTAL);
		c.add(s);
		c.add(new PC_GresComboBox(Arrays.asList("S1", "S2", "S3"), 0));
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
