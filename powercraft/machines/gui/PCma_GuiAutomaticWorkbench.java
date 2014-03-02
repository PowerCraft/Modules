package powercraft.machines.gui;

import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Keyboard;

import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresGroupContainer;
import powercraft.api.gres.PC_GresGuiHandler;
import powercraft.api.gres.PC_GresInventory;
import powercraft.api.gres.PC_GresPlayerInventory;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.PC_GresWindowSideTab;
import powercraft.api.gres.PC_GresWindowSideTab.EnergyPerTick;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.events.PC_GresEvent;
import powercraft.api.gres.events.PC_GresKeyEvent;
import powercraft.api.gres.events.PC_IGresEventListener;
import powercraft.api.gres.layout.PC_GresLayoutHorizontal;
import powercraft.api.gres.layout.PC_GresLayoutVertical;
import powercraft.machines.container.PCma_ContainerAutomaticWorkbench;
import powercraft.machines.tileentity.PCma_TileEntityAutomaticWorkbench;

public class PCma_GuiAutomaticWorkbench extends PCma_ContainerAutomaticWorkbench implements PC_IGresGui, PC_IGresEventListener {

	private EnergyPerTick energy;
	
	public PCma_GuiAutomaticWorkbench(EntityPlayer player, PCma_TileEntityAutomaticWorkbench automaticWorkbench) {
		super(player, automaticWorkbench);
	}

	@Override
	public void initGui(PC_GresGuiHandler gui) {
		PC_GresWindow window = new PC_GresWindow("AutomaticWorkbench");
		window.setLayout(new PC_GresLayoutVertical());
		this.energy = new EnergyPerTick();
		window.addSideTab(PC_GresWindowSideTab.createEnergySideTab(this.energy));
		window.addSideTab(PC_GresWindowSideTab.createRedstoneSideTab(this.automaticWorkbench));
		window.addSideTab(PC_GresWindowSideTab.createIOConfigurationSideTab(this.automaticWorkbench));
		PC_GresGroupContainer group = new PC_GresGroupContainer();
		group.setLayout(new PC_GresLayoutHorizontal());
		PC_GresInventory inv;
		group.add(inv = new PC_GresInventory(3, 3));
		for(int j=0; j<3; j++){
			for(int i=0; i<3; i++){
				inv.setSlot(i, j, this.invSlots[j*3+i]);
			}
		}
		group.add(inv = new PC_GresInventory(1, 1));
		inv.setSlot(0, 0, this.invSlots[9]);
		window.add(group);
		window.add(inv = new PC_GresInventory(9, 2));
		for(int j=0; j<2; j++){
			for(int i=0; i<9; i++){
				inv.setSlot(i, j, this.invSlots[10+j*9+i]);
			}
		}
		window.add(new PC_GresPlayerInventory(this));
		gui.add(window);
		gui.addEventListener(this);
	}

	@Override
	public void updateProgressBar(int key, int value) {
		if(key==0)
			this.energy.setToValue(value/100.0f);
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
