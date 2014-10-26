package powercraft.itemstorage.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import powercraft.api.PC_Lang;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresCheckBox;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresDisplayObject;
import powercraft.api.gres.PC_GresGuiHandler;
import powercraft.api.gres.PC_GresInventory;
import powercraft.api.gres.PC_GresLabel;
import powercraft.api.gres.PC_GresPlayerInventory;
import powercraft.api.gres.PC_GresTextEdit;
import powercraft.api.gres.PC_GresTextEdit.PC_GresInputType;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.PC_GresWindowSideTab;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.events.PC_GresEvent;
import powercraft.api.gres.events.PC_GresKeyEventResult;
import powercraft.api.gres.events.PC_GresMouseButtonEventResult;
import powercraft.api.gres.events.PC_IGresEventListener;
import powercraft.api.gres.layout.PC_GresLayoutVertical;
import powercraft.itemstorage.container.PCis_ContainerCompressor;
import powercraft.itemstorage.inventory.PCis_CompressorInventory;
import powercraft.itemstorage.item.PCis_ItemCompressor;

public class PCis_GuiCompressor extends PCis_ContainerCompressor implements PC_IGresGui, PC_IGresEventListener{
	
	public PC_GresTextEdit name;
	public PC_GresCheckBox takeStacks;
	public PC_GresTextEdit putStacks;
	
	public PCis_GuiCompressor(EntityPlayer player, ItemStack itemStack, int slot, IInventory inv) {
		super(player, itemStack, slot, inv);
	}
	
	@Override
	public void initGui(PC_GresGuiHandler gui) {
		PC_GresWindow w = new PC_GresWindow(PC_Lang.translate(this.inventory.getInventoryName()+".name"));
		w.setLayout(new PC_GresLayoutVertical());
		PC_GresWindowSideTab tab = new PC_GresWindowSideTab(PC_Lang.translate("PCis.gui.name"), new PC_GresDisplayObject(Items.paper));
		tab.setLayout(new PC_GresLayoutVertical());
		tab.add(this.name = new PC_GresTextEdit(PCis_ItemCompressor.getName(this.itemStack), 10));
		this.name.addEventListener(this);
		w.addSideTab(tab);
		
		tab = new PC_GresWindowSideTab(PC_Lang.translate("PCis.gui.working"), new PC_GresDisplayObject(Items.fishing_rod));
		tab.setLayout(new PC_GresLayoutVertical());
		tab.add(this.takeStacks = new PC_GresCheckBox(PC_Lang.translate("PCis.gui.compressor.takeStacks")));
		this.takeStacks.addEventListener(this);
		this.takeStacks.check(PCis_ItemCompressor.isTakeStacks(this.itemStack));
		tab.add(new PC_GresLabel(PC_Lang.translate("PCis.gui.compressor.putStacks")));
		tab.add(this.putStacks = new PC_GresTextEdit("" + PCis_ItemCompressor.getPutStacks(this.itemStack), 3, PC_GresInputType.UNSIGNED_INT));
		this.putStacks.addEventListener(this);
		w.addSideTab(tab);
		
		PC_Vec2I size;
		if(this.inventory instanceof PCis_CompressorInventory){
			size = ((PCis_CompressorInventory)this.inventory).getSize();
		}else{
			size = new PC_Vec2I(9, 3);
		}
		PC_GresInventory inventory = new PC_GresInventory(size.x, size.y);
		inventory.setSlots(this.invSlots, 0);
		w.add(inventory);
		w.add(new PC_GresPlayerInventory(this));
		gui.add(w);
	}

	@Override
	public void onEvent(PC_GresEvent event) {
		PC_GresComponent component = event.getComponent();
		if(event instanceof PC_GresMouseButtonEventResult){
			if(component==this.takeStacks){
				PCis_ItemCompressor.setTakeStacks(this.player, this.takeStacks.isChecked());
			}
		}else if(event instanceof PC_GresKeyEventResult){
			if(component==this.name){
				PCis_ItemCompressor.setName(this.player, this.name.getText());
			}else if(component==this.putStacks){
				if(this.putStacks.getText().equals("")){
					PCis_ItemCompressor.setPutStacks(this.player, 0);
				}else{
					PCis_ItemCompressor.setPutStacks(this.player, Integer.parseInt(this.putStacks.getText()));
				}
			}
		}
	}

}
