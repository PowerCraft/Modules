package powercraft.traffic.gui;

import org.lwjgl.input.Keyboard;

import net.minecraft.entity.player.EntityPlayer;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresGuiHandler;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.events.PC_GresEvent;
import powercraft.api.gres.events.PC_GresKeyEvent;
import powercraft.api.gres.events.PC_IGresEventListener;
import powercraft.traffic.entity.PCtf_EntityMiner;
import powercraft.traffic.entity.container.PCtf_ContainerMiner;


public class PCtf_GuiMiner extends PCtf_ContainerMiner implements PC_IGresGui, PC_IGresEventListener {
	
	public PCtf_GuiMiner(EntityPlayer player, PCtf_EntityMiner miner) {
		super(player, miner);
	}

	@Override
	public void initGui(PC_GresGuiHandler gui) {
		
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
