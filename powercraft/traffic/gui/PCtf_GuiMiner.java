package powercraft.traffic.gui;

import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Keyboard;

import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresGuiHandler;
import powercraft.api.gres.PC_GresInventory;
import powercraft.api.gres.PC_GresPlayerInventory;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.events.PC_GresEvent;
import powercraft.api.gres.events.PC_GresKeyEvent;
import powercraft.api.gres.events.PC_IGresEventListener;
import powercraft.api.gres.layout.PC_GresLayoutVertical;
import powercraft.traffic.entity.PCtf_EntityMiner;
import powercraft.traffic.entity.container.PCtf_ContainerMiner;


public class PCtf_GuiMiner extends PCtf_ContainerMiner implements PC_IGresGui, PC_IGresEventListener {
	
	public PCtf_GuiMiner(EntityPlayer player, PCtf_EntityMiner miner) {
		super(player, miner);
	}

	@Override
	public void initGui(PC_GresGuiHandler gui) {
		PC_GresWindow window = new PC_GresWindow("Miner");
		window.setLayout(new PC_GresLayoutVertical());
		PC_GresInventory inv = new PC_GresInventory(9, 6);
		for(int i=0; i<6; i++){
			for(int j=0; j<9; j++){
				inv.setSlot(j, i, this.invSlots[i*9+j]);
			}
		}
		window.add(inv);
		window.add(new PC_GresPlayerInventory(this));
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
