package powercraft.itemstorage.gui;

import net.minecraft.entity.player.EntityPlayer;
import powercraft.api.gres.PC_GresGuiHandler;
import powercraft.api.gres.PC_GresInventory;
import powercraft.api.gres.PC_GresPlayerInventory;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.layout.PC_GresLayoutVertical;
import powercraft.itemstorage.container.PCis_ContainerChannelChest;
import powercraft.itemstorage.tileentity.PCis_TileEntityChannelChest;


public class PCis_GuiChannelChest extends PCis_ContainerChannelChest implements PC_IGresGui {
	
	public PCis_GuiChannelChest(EntityPlayer player, PCis_TileEntityChannelChest channelChest) {
		super(player, channelChest);
	}

	@SuppressWarnings("hiding")
	@Override
	public void initGui(PC_GresGuiHandler gui) {
		PC_GresWindow w = new PC_GresWindow(this.inventory.getInventoryName()+".name");
		w.setLayout(new PC_GresLayoutVertical());
		PC_GresInventory inventory = new PC_GresInventory(9, 3);
		inventory.setSlots(this.invSlots, 0);
		w.add(inventory);
		w.add(new PC_GresPlayerInventory(this));
		gui.add(w);
	}
	
}
