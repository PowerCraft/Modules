package powercraft.teleporter.gui;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import powercraft.api.gres.PC_GresGuiHandler;
import powercraft.api.gres.PC_GresListBox;
import powercraft.api.gres.PC_GresTextEdit;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.layout.PC_GresLayoutVertical;
import powercraft.teleporter.tileentity.PCtp_TileEntityTeleporter;


public class PCtp_GuiTeleporter implements PC_IGresGui {
	
	private EntityPlayer player;
	private PCtp_TileEntityTeleporter teleporter;
	private int lastRequest;
	private PC_GresGuiHandler gui;
	private int nextRequest = 1;
	
	public PCtp_GuiTeleporter(EntityPlayer player, PCtp_TileEntityTeleporter teleporter) {
		this.player = player;
		this.teleporter = teleporter;
	}

	@Override
	public void initGui(PC_GresGuiHandler gui) {
		this.gui = gui;
		PC_GresWindow w = new PC_GresWindow("Teleporter");
		w.setLayout(new PC_GresLayoutVertical());
		PC_GresTextEdit name = new PC_GresTextEdit("", 10);
		w.add(name);
		PC_GresTextEdit path = new PC_GresTextEdit("", 10);
		w.add(path);
		PC_GresListBox posibilities = new PC_GresListBox(new ArrayList<String>());
		w.add(posibilities);
		gui.add(w);
	}
	
	private void startWaitingForServer(int request){
		if(this.lastRequest==0){
			this.gui.addWorking();
		}
		this.lastRequest = request;
	}
	
	private void stopWaitingForServer(int request){
		if(this.lastRequest==request){
			this.lastRequest = 0;
			this.gui.removeWorking();
		}
	}
	
}
