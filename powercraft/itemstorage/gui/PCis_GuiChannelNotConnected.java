package powercraft.itemstorage.gui;

import powercraft.api.PC_Lang;
import powercraft.api.gres.PC_GresGuiHandler;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.PC_IGresGui;


public class PCis_GuiChannelNotConnected implements PC_IGresGui {
	
	@Override
	public void initGui(PC_GresGuiHandler gui) {
		gui.add(new PC_GresWindow(PC_Lang.translate("PCis.compressor.not.connected")));
	}
	
}
