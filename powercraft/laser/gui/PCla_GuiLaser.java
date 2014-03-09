package powercraft.laser.gui;

import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;
import powercraft.api.PC_RectI;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresGroupContainer;
import powercraft.api.gres.PC_GresGuiHandler;
import powercraft.api.gres.PC_GresInventory;
import powercraft.api.gres.PC_GresLabel;
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
	private PC_GresGroupContainer lensSlot;
	private PC_GresGroupContainer catalysator;
	private PC_GresGroupContainer laserSlot;
	private PC_GresGroupContainer upgradeSlot;

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

		PC_GresGroupContainer itemTab = new PC_GresGroupContainer();
		tabs.add("Items", itemTab);
		itemTab.setLayout(new PC_GresLayoutVertical());

		PC_GresGroupContainer laserItems = new PC_GresGroupContainer();
		laserItems.setLayout(new PC_GresLayoutHorizontal());
		itemTab.add(laserItems);
		PC_GresGroupContainer upgradeItems = new PC_GresGroupContainer();
		upgradeItems.setLayout(new PC_GresLayoutVertical());
		itemTab.add(upgradeItems);

		PC_GresGroupContainer presentTab = new PC_GresGroupContainer();
		tabs.add("Presents", presentTab);
		presentTab.setLayout(new PC_GresLayoutHorizontal());

		PC_GresGroupContainer scriptTab = new PC_GresGroupContainer();
		tabs.add("Script", scriptTab);
		presentTab.setLayout(new PC_GresLayoutHorizontal());

		lensSlot = new PC_GresGroupContainer();
		lensSlot.setLayout(new PC_GresLayoutVertical());
		lensSlot.add(inv = new PC_GresInventory(1, 4));
		inv.setSlot(0, 0, this.invSlots[0]);
		inv.setSlot(0, 1, this.invSlots[1]);
		inv.setSlot(0, 2, this.invSlots[2]);
		inv.setSlot(0, 3, this.invSlots[3]);
		PC_RectI newRect = new PC_RectI(lensSlot.getPadding());
		newRect.width += 5;
		newRect.x += 3;
		newRect.y += 3;
		newRect.height += 3;
		lensSlot.setPadding(newRect);
		laserItems.add(lensSlot);

		catalysator = new PC_GresGroupContainer();
		catalysator.setLayout(new PC_GresLayoutHorizontal());
		catalysator.add(inv = new PC_GresInventory(2, 4));
		inv.setSlot(0, 0, this.invSlots[4]);
		inv.setSlot(1, 0, this.invSlots[5]);
		inv.setSlot(0, 1, this.invSlots[6]);
		inv.setSlot(1, 1, this.invSlots[7]);
		inv.setSlot(0, 2, this.invSlots[8]);
		inv.setSlot(1, 2, this.invSlots[9]);
		inv.setSlot(0, 3, this.invSlots[10]);
		inv.setSlot(1, 3, this.invSlots[11]);
		laserItems.add(catalysator);

		laserSlot = new PC_GresGroupContainer();
		laserSlot.setLayout(new PC_GresLayoutHorizontal());
		laserSlot.add(inv = new PC_GresInventory(1, 4));
		inv.setSlot(0, 0, this.invSlots[12]);
		inv.setSlot(0, 1, this.invSlots[13]);
		inv.setSlot(0, 2, this.invSlots[14]);
		inv.setSlot(0, 3, this.invSlots[15]);
		newRect = new PC_RectI(laserSlot.getPadding());
		newRect.width += 3;
		newRect.x += 5;
		newRect.y += 3;
		newRect.height += 3;
		laserSlot.setPadding(newRect);
		laserItems.add(laserSlot);

		PC_GresLabel upgradeLabel = new PC_GresLabel("Upgrades");
		upgradeItems.add(upgradeLabel);
		upgradeSlot = new PC_GresGroupContainer();
		upgradeSlot.setLayout(new PC_GresLayoutHorizontal());
		upgradeSlot.add(inv = new PC_GresInventory(5, 1));
		inv.setSlot(0, 0, this.invSlots[16]);
		inv.setSlot(1, 0, this.invSlots[17]);
		inv.setSlot(2, 0, this.invSlots[18]);
		inv.setSlot(3, 0, this.invSlots[19]);
		inv.setSlot(4, 0, this.invSlots[20]);
		newRect = new PC_RectI(upgradeSlot.getPadding());
		newRect.width += 3;
		newRect.x += 3;
		newRect.height += 3;
		upgradeSlot.setPadding(newRect);
		upgradeItems.add(upgradeSlot);

		window.add(tabs);
		window.add(new PC_GresPlayerInventory(this));
		gui.add(window);
		gui.addEventListener(this);
	}

}
