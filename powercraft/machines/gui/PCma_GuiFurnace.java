package powercraft.machines.gui;

import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Keyboard;

import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresGroupContainer;
import powercraft.api.gres.PC_GresGuiHandler;
import powercraft.api.gres.PC_GresInventory;
import powercraft.api.gres.PC_GresPlayerInventory;
import powercraft.api.gres.PC_GresProgressImage;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.PC_GresWindowSideTab;
import powercraft.api.gres.PC_GresWindowSideTab.EnergyPerTick;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.events.PC_GresEvent;
import powercraft.api.gres.events.PC_GresKeyEvent;
import powercraft.api.gres.events.PC_IGresEventListener;
import powercraft.api.gres.layout.PC_GresLayoutHorizontal;
import powercraft.api.gres.layout.PC_GresLayoutVertical;
import powercraft.machines.container.PCma_ContainerFurnace;
import powercraft.machines.tileentity.PCma_TileEntityFurnace;

public class PCma_GuiFurnace extends PCma_ContainerFurnace implements PC_IGresGui, PC_IGresEventListener {

	private PC_GresProgressImage fire;
	private EnergyPerTick energy;
	
	public PCma_GuiFurnace(EntityPlayer player, PCma_TileEntityFurnace furnace) {
		super(player, furnace);
	}

	@Override
	public void initGui(PC_GresGuiHandler gui) {
		PC_GresWindow window = new PC_GresWindow("Furnace");
		this.energy = new EnergyPerTick();
		window.addSideTab(PC_GresWindowSideTab.createEnergySideTab(this.energy));
		window.addSideTab(PC_GresWindowSideTab.createRedstoneSideTab(this.furnace));
		window.addSideTab(PC_GresWindowSideTab.createIOConfigurationSideTab(this.furnace));
		window.setLayout(new PC_GresLayoutVertical());
		PC_GresGroupContainer group1 = new PC_GresGroupContainer();
		group1.setLayout(new PC_GresLayoutHorizontal());
		PC_GresGroupContainer group2 = new PC_GresGroupContainer();
		group2.setLayout(new PC_GresLayoutVertical());
		PC_GresInventory inv;
		group2.add(inv = new PC_GresInventory(1, 1));
		inv.setSlot(0, 0, this.invSlots[0]);
		group2.add(this.fire = new PC_GresProgressImage("FireShadow", "FireOn"));
		group1.add(group2);
		group1.add(inv = new PC_GresInventory(1, 1));
		inv.setSlot(0, 0, this.invSlots[1]);
		window.add(group1);
		window.add(new PC_GresPlayerInventory(this));
		gui.add(window);
		gui.addEventListener(this);
	}

	@Override
	public void updateProgressBar(int key, int value) {
		if(key==0)
			this.fire.setProgress(value);
		if(key==1)
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
