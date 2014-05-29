package powercraft.laser.gui;

import net.minecraft.entity.player.EntityPlayer;
import powercraft.api.gres.PC_GresGuiHandler;
import powercraft.api.gres.PC_GresInventory;
import powercraft.api.gres.PC_GresPlayerInventory;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.PC_GresWindowSideTab;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.layout.PC_GresLayoutVertical;
import powercraft.laser.container.PCla_ContainerLaserBuilder;
import powercraft.laser.tileEntity.PCla_TileEntityLaserBuilder;


public class PCla_GuiLaserBuilder extends PCla_ContainerLaserBuilder implements PC_IGresGui {

	public PCla_GuiLaserBuilder(EntityPlayer player, PCla_TileEntityLaserBuilder builder) {
		super(player, builder);
	}

	@Override
	public void initGui(PC_GresGuiHandler gui) {
		PC_GresWindow window = (PC_GresWindow)new PC_GresWindow("Builder").setLayout(new PC_GresLayoutVertical());
		window.addSideTab(PC_GresWindowSideTab.createIOConfigurationSideTab(this.builder));
		window.add(new PC_GresInventory(3, 3).setSlots(this.invSlots, 0));
		window.add(new PC_GresPlayerInventory(this));
		gui.add(window);
	}
	
}
