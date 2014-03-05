package powercraft.laser.gui;

import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresGroupContainer;
import powercraft.api.gres.PC_GresGuiHandler;
import powercraft.api.gres.PC_GresInventory;
import powercraft.api.gres.PC_GresPlayerInventory;
import powercraft.api.gres.PC_GresTab;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.PC_GresWindowSideTab;
import powercraft.api.gres.PC_GresWindowSideTab.EnergyPerTick;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.events.PC_GresEvent;
import powercraft.api.gres.events.PC_GresKeyEvent;
import powercraft.api.gres.events.PC_IGresEventListener;
import powercraft.api.gres.layout.PC_GresLayoutHorizontal;
import powercraft.api.gres.layout.PC_GresLayoutVertical;
import powercraft.laser.container.PCla_ContainerLaser;
import powercraft.laser.tileEntity.PCla_TileEntityLaser;

public class PCla_GuiLaser extends PCla_ContainerLaser implements PC_IGresGui, PC_IGresEventListener {

	private EnergyPerTick energy;

	public PCla_GuiLaser(EntityPlayer player, PCla_TileEntityLaser laser) {
		super(player, laser);
	}

	@Override
	public void onEvent(PC_GresEvent event) {
		PC_GresComponent component = event.getComponent();
		if (event instanceof PC_GresKeyEvent) {
			PC_GresKeyEvent kEvent = (PC_GresKeyEvent) event;
			if (kEvent.getKeyCode() == Keyboard.KEY_ESCAPE) {
				component.getGuiHandler().close();
			}
		}
	}

	@Override
	public void initGui(PC_GresGuiHandler gui) {
		PC_GresWindow window = new PC_GresWindow("Laser");
		this.energy = new EnergyPerTick();
		window.addSideTab(PC_GresWindowSideTab.createEnergySideTab(this.energy));
		window.addSideTab(PC_GresWindowSideTab.createRedstoneSideTab(this.laser));
		window.addSideTab(PC_GresWindowSideTab.createIOConfigurationSideTab(this.laser));

		window.setLayout(new PC_GresLayoutVertical());
		PC_GresInventory inv;

		PC_GresTab tabs = new PC_GresTab();

		PC_GresTab itemTab = new PC_GresTab();
		tabs.add("Items", itemTab);
		itemTab.setLayout(new PC_GresLayoutHorizontal());

		PC_GresTab presentTab = new PC_GresTab();
		tabs.add("Presents", presentTab);
		presentTab.setLayout(new PC_GresLayoutHorizontal());

		PC_GresTab scriptTab = new PC_GresTab();
		tabs.add("Miniscript", scriptTab);
		scriptTab.setLayout(new PC_GresLayoutHorizontal());

		PC_GresGroupContainer lensSlot = new PC_GresGroupContainer();
		lensSlot.setLayout(new PC_GresLayoutHorizontal());
		lensSlot.add(inv = new PC_GresInventory(1, 1));
		inv.setSlot(0, 0, this.invSlots[0]);
		itemTab.add(lensSlot);

		PC_GresGroupContainer catalysator = new PC_GresGroupContainer();
		catalysator.setLayout(new PC_GresLayoutHorizontal());
		catalysator.add(inv = new PC_GresInventory(2, 1));
		inv.setSlot(0, 0, this.invSlots[1]);
		inv.setSlot(1, 0, this.invSlots[2]);
		itemTab.add(catalysator);

		window.add(tabs);
		window.add(new PC_GresPlayerInventory(this));
		gui.add(window);
		gui.addEventListener(this);
	}

}
