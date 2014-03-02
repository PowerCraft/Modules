package powercraft.energy.gui;

import java.text.DecimalFormat;

import org.lwjgl.input.Keyboard;

import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresGuiHandler;
import powercraft.api.gres.PC_GresLabel;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.PC_GresWindowSideTab;
import powercraft.api.gres.PC_GresWindowSideTab.EnergyPerTick;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.events.PC_GresEvent;
import powercraft.api.gres.events.PC_GresKeyEvent;
import powercraft.api.gres.events.PC_IGresEventListener;
import powercraft.api.gres.layout.PC_GresLayoutVertical;
import powercraft.energy.tileentity.PCeg_TileEntityAccumulator;

public class PCeg_GuiAccumulator implements PC_IGresGui, PC_IGresEventListener {

	private EnergyPerTick energy;
	
	private PC_GresLabel energyLevel;
	
	private PCeg_TileEntityAccumulator accumulator;
	
	public PCeg_GuiAccumulator(PCeg_TileEntityAccumulator accumulator) {
		this.accumulator = accumulator;
	}

	@Override
	public void initGui(PC_GresGuiHandler gui) {
		PC_GresWindow window = new PC_GresWindow("Accumulator");
		window.setLayout(new PC_GresLayoutVertical());
		this.energy = new EnergyPerTick();
		window.addSideTab(PC_GresWindowSideTab.createEnergySideTab(this.energy));
		window.add(this.energyLevel = new PC_GresLabel("Energy: 0 E"));
		gui.add(window);
		gui.addEventListener(this);
	}

	public PCeg_TileEntityAccumulator getAccumulator(){
		return this.accumulator;
	}
	
	public void setEnergyLevel(float value){
		this.energyLevel.setText("Energy: "+new DecimalFormat("#.##").format(value)+" E");
	}
	
	public void setEnergyPerTick(float value){
		this.energy.setToValue(value);
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
