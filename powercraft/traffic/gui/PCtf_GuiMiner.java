package powercraft.traffic.gui;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.lwjgl.input.Keyboard;

import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresGroupContainer;
import powercraft.api.gres.PC_GresGuiHandler;
import powercraft.api.gres.PC_GresInventory;
import powercraft.api.gres.PC_GresMultilineHighlightingTextEdit;
import powercraft.api.gres.PC_GresPlayerInventory;
import powercraft.api.gres.PC_GresTab;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.events.PC_GresEvent;
import powercraft.api.gres.events.PC_GresKeyEvent;
import powercraft.api.gres.events.PC_IGresEventListener;
import powercraft.api.gres.font.PC_Fonts;
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
	
	private NBTTagCompound diagnostics;
	
	private PC_GresMultilineHighlightingTextEdit consoleOutput;
	
	private PC_GresMultilineHighlightingTextEdit consoleInput;
	
	private String consoleOut;
	
	public PCtf_GuiMiner(EntityPlayer player, PCtf_EntityMiner miner, HashMap<String, String> sources, NBTTagCompound diagnostics, String consoleOut) {
		super(player, miner);
		this.sources = sources;
		this.diagnostics = diagnostics;
		this.consoleOut = consoleOut;
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
		
		PC_Slot slot = (PC_Slot) this.invSlots[PCtf_EntityMiner.INVENTORIES.SAWBLADE.firstIndex];
		slot.setBackgroundStack(new ItemStack(PCtf_Traffic.SAWBLADE_IRON));
		slot.setRenderGrayWhenEmpty(true);
		gc.add(new PC_GresInventory(1, 1).setSlot(0, 0, slot));
		
		slot = (PC_Slot) this.invSlots[PCtf_EntityMiner.INVENTORIES.ENGINE.firstIndex];
		slot.setBackgroundStack(new ItemStack(PCtf_Traffic.ENGINE));
		slot.setRenderGrayWhenEmpty(true);
		gc.add(new PC_GresInventory(1, 1).setSlot(0, 0, slot));
		
		slot = (PC_Slot) this.invSlots[PCtf_EntityMiner.INVENTORIES.CONVERTER.firstIndex];
		slot.setBackgroundStack(new ItemStack(PCtf_Traffic.CONVERTER));
		slot.setRenderGrayWhenEmpty(true);
		gc.add(new PC_GresInventory(1, 1).setSlot(0, 0, slot));
		
		gc.add(new PC_GresPlayerInventory(this));
		tab.add("Equipment", gc);
		tab.add("Programm", this.edit = new PC_WeaselGresEdit(this.sources));
		this.edit.addEventListener(this);
		this.edit.setErrors(this.diagnostics);
		gc = new PC_GresGroupContainer();
		gc.setLayout(new PC_GresLayoutVertical());
		this.consoleOutput = new PC_GresMultilineHighlightingTextEdit(PC_Fonts.getFontByName("Consolas", 24.0f), null, null, null, this.consoleOut);
		this.consoleOutput.setMinSize(new PC_Vec2I(300, 150));
		this.consoleOutput.setPrefSize(new PC_Vec2I(300, 150));
		this.consoleOutput.setSize(new PC_Vec2I(300, 150));
		this.consoleOutput.setEditable(false);
		int p = this.consoleOut.length();
		if(p>0 && this.consoleOut.charAt(p-1)=='\n')
			p--;
		this.consoleOutput.setCursorPos(p);
		gc.add(this.consoleOutput);
		this.consoleInput = new PC_GresMultilineHighlightingTextEdit(PC_Fonts.getFontByName("Consolas", 24.0f), null, null, null, "");
		this.consoleInput.setMinSize(new PC_Vec2I(300, 50));
		this.consoleInput.setPrefSize(new PC_Vec2I(300, 50));
		this.consoleInput.setSize(new PC_Vec2I(300, 50));
		this.consoleInput.addEventListener(this);
		gc.add(this.consoleInput);
		tab.add("Console", gc);
		this.diagnostics = null;
		this.sources = null;
		window.add(tab);
		gui.add(window);
		gui.addEventListener(this);
	}

	@Override
	public void onEvent(PC_GresEvent event) {
		PC_GresComponent component = event.getComponent();
		if(event instanceof PC_GresKeyEvent){
			PC_GresKeyEvent kEvent = (PC_GresKeyEvent)event;
			if(component == this.consoleInput){
				if(kEvent.getKeyCode()==Keyboard.KEY_RETURN){
					if(!(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))){
						this.miner.onConsoleInput(this.consoleInput.getText());
						this.consoleInput.setText("");
						kEvent.consume();
					}
				}
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

	public void setConsoleOut(PCtf_EntityMiner miner, String out) {
		if(miner==this.miner){
			this.consoleOutput.setText(out);
			int p = out.length();
			if(p>0 && out.charAt(p-1)=='\n')
				p--;
			this.consoleOutput.setCursorPos(p);
		}
	}
	
}
