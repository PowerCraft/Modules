package powercraft.traffic.gui;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.lwjgl.input.Keyboard;

import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresGroupContainer;
import powercraft.api.gres.PC_GresGuiHandler;
import powercraft.api.gres.PC_GresInventory;
import powercraft.api.gres.PC_GresPlayerInventory;
import powercraft.api.gres.PC_GresTab;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.events.PC_GresEvent;
import powercraft.api.gres.events.PC_GresKeyEvent;
import powercraft.api.gres.events.PC_IGresEventListener;
import powercraft.api.gres.layout.PC_GresLayoutVertical;
import powercraft.api.gres.slot.PC_Slot;
import powercraft.api.script.weasel.PC_WeaselGresEdit;
import powercraft.api.script.weasel.PC_WeaselGresEdit.SaveEvent;
import powercraft.traffic.PCtf_Traffic;
import powercraft.traffic.container.PCtf_ContainerMiner;
import powercraft.traffic.entity.PCtf_EntityMiner;


public class PCtf_GuiMiner extends PCtf_ContainerMiner implements PC_IGresGui, PC_IGresEventListener {
	
	private PC_WeaselGresEdit edit;
	
	private HashMap<String, String> sources;
	
	public PCtf_GuiMiner(EntityPlayer player, PCtf_EntityMiner miner, HashMap<String, String> sources) {
		super(player, miner);
		this.sources = sources;
	}

	@Override
	public void initGui(PC_GresGuiHandler gui) {
		PC_GresWindow window = new PC_GresWindow("Miner");
		window.setLayout(new PC_GresLayoutVertical());
		PC_GresTab tab = new PC_GresTab();
		PC_GresGroupContainer gc = new PC_GresGroupContainer();
		gc.setLayout(new PC_GresLayoutVertical());
		gc.add(new PC_GresInventory(9, 6).setSlots(this.invSlots, 0));
		gc.add(new PC_GresPlayerInventory(this));
		tab.add("Inventory", gc);
		gc = new PC_GresGroupContainer();
		gc.setLayout(new PC_GresLayoutVertical());
		PC_Slot slot = (PC_Slot) this.invSlots[9*6];
		slot.setBackgroundStack(new ItemStack(PCtf_Traffic.SAWBLADE_IRON));
		slot.setRenderGrayWhenEmpty(true);
		gc.add(new PC_GresInventory(1, 1).setSlot(0, 0, slot));
		gc.add(new PC_GresPlayerInventory(this));
		tab.add("Equipment", gc);
		tab.add("Programm", this.edit = new PC_WeaselGresEdit(this.sources));
		this.edit.addEventListener(this);
		window.add(tab);
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
		}else if(event instanceof SaveEvent){
			SaveEvent se = (SaveEvent)event;
			if(se.getComponent()==this.edit){
				this.edit.clearErrors();
				this.miner.saveAndCompile(se.getSources());
			}
		}
	}

	public void setErrors(PCtf_EntityMiner miner, NBTTagCompound compoundTag) {
		if(miner==this.miner){
			this.edit.setErrors(compoundTag);
		}
	}
	
}
