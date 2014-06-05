package powercraft.core.gui;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Lang;
import powercraft.api.PC_MathHelper;
import powercraft.api.PC_Utils;
import powercraft.api.PC_Vec3I;
import powercraft.api.PC_Vec4I;
import powercraft.api.gres.PC_GresAlign.Fill;
import powercraft.api.gres.PC_GresContainer;
import powercraft.api.gres.PC_GresGroupContainer;
import powercraft.api.gres.PC_GresGuiHandler;
import powercraft.api.gres.PC_GresInventory;
import powercraft.api.gres.PC_GresLabel;
import powercraft.api.gres.PC_GresSlider;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.events.PC_GresEvent;
import powercraft.api.gres.events.PC_IGresEventListener;
import powercraft.api.gres.layout.PC_GresLayoutVertical;
import powercraft.api.gres.slot.PC_Slot;


public class PCco_GuiOreSnifferResultScreen implements PC_IGresGui, PC_IGresEventListener {
	
	private PC_GresSlider slider;
	private PC_GresLabel distanceL;
	private PC_GresInventory inv;
	private PC_Direction vector;
	private PC_Vec3I[][] startpos;
	private World world;
	private PC_Vec3I start;
	
	private static final int RANGE = 16;
	
	private void rotateRight() {
		PC_Vec3I swap = this.startpos[0][0];
		this.startpos[0][0] = this.startpos[0][1];
		this.startpos[0][1] = this.startpos[0][2];
		this.startpos[0][2] = this.startpos[1][2];
		this.startpos[1][2] = this.startpos[2][2];
		this.startpos[2][2] = this.startpos[2][1];
		this.startpos[2][1] = this.startpos[2][0];
		this.startpos[2][0] = this.startpos[1][0];
		this.startpos[1][0] = swap;
	}
	
	public PCco_GuiOreSnifferResultScreen(EntityPlayer player, PC_Vec4I vec4) {
		this.startpos = new PC_Vec3I[3][3];
		this.world = player.worldObj;
		this.start = new PC_Vec3I(vec4.x, vec4.y, vec4.z);
		this.vector = PC_Direction.fromSide(vec4.w).getOpposite();
				
		int l = PC_MathHelper.floor_double(((player.rotationYaw * 4F) / 360F) + 0.5D) & 3;

		if (this.vector == PC_Direction.DOWN) {

			this.startpos[0][0] = this.start.offset(-1, 0, -1);
			this.startpos[1][0] = this.start.offset(0, 0, -1);
			this.startpos[2][0] = this.start.offset(1, 0, -1);
			this.startpos[0][1] = this.start.offset(-1, 0, 0);
			this.startpos[1][1] = this.start;
			this.startpos[2][1] = this.start.offset(1, 0, 0);
			this.startpos[0][2] = this.start.offset(-1, 0, 1);
			this.startpos[1][2] = this.start.offset(0, 0, 1);
			this.startpos[2][2] = this.start.offset(1, 0, 1);

			l = 3 - l;
			l += 3;
			for (int i = 0; i < l; i++) {
				rotateRight();
				rotateRight();
			}


		} else if (this.vector == PC_Direction.EAST) {
			this.startpos[0][0] = this.start.offset(0, 1, -1);
			this.startpos[1][0] = this.start.offset(0, 1, 0);
			this.startpos[2][0] = this.start.offset(0, 1, 1);
			this.startpos[0][1] = this.start.offset(0, 0, -1);
			this.startpos[1][1] = this.start;
			this.startpos[2][1] = this.start.offset(0, 0, 1);
			this.startpos[0][2] = this.start.offset(0, -1, -1);
			this.startpos[1][2] = this.start.offset(0, -1, 0);
			this.startpos[2][2] = this.start.offset(0, -1, 1);
		} else if (this.vector == PC_Direction.NORTH) {
			this.startpos[0][0] = this.start.offset(-1, 1, 0);
			this.startpos[1][0] = this.start.offset(0, 1, 0);
			this.startpos[2][0] = this.start.offset(1, 1, 0);
			this.startpos[0][1] = this.start.offset(-1, 0, 0);
			this.startpos[1][1] = this.start;
			this.startpos[2][1] = this.start.offset(1, 0, 0);
			this.startpos[0][2] = this.start.offset(-1, -1, 0);
			this.startpos[1][2] = this.start.offset(0, -1, 0);
			this.startpos[2][2] = this.start.offset(1, -1, 0);


		} else if (this.vector == PC_Direction.UP) {
			this.startpos[0][2] = this.start.offset(-1, 0, -1);
			this.startpos[1][2] = this.start.offset(0, 0, -1);
			this.startpos[2][2] = this.start.offset(1, 0, -1);
			this.startpos[0][1] = this.start.offset(-1, 0, 0);
			this.startpos[1][1] = this.start;
			this.startpos[2][1] = this.start.offset(1, 0, 0);
			this.startpos[0][0] = this.start.offset(-1, 0, 1);
			this.startpos[1][0] = this.start.offset(0, 0, 1);
			this.startpos[2][0] = this.start.offset(1, 0, 1);

			l += 2;
			for (int i = 0; i < l; i++) {
				rotateRight();
				rotateRight();
			}

		} else if (this.vector == PC_Direction.WEST) {
			this.startpos[2][0] = this.start.offset(0, 1, -1);
			this.startpos[1][0] = this.start.offset(0, 1, 0);
			this.startpos[0][0] = this.start.offset(0, 1, 1);
			this.startpos[2][1] = this.start.offset(0, 0, -1);
			this.startpos[1][1] = this.start;
			this.startpos[0][1] = this.start.offset(0, 0, 1);
			this.startpos[2][2] = this.start.offset(0, -1, -1);
			this.startpos[1][2] = this.start.offset(0, -1, 0);
			this.startpos[0][2] = this.start.offset(0, -1, 1);
		} else if (this.vector == PC_Direction.SOUTH) {
			this.startpos[2][0] = this.start.offset(-1, 1, 0);
			this.startpos[1][0] = this.start.offset(0, 1, 0);
			this.startpos[0][0] = this.start.offset(1, 1, 0);
			this.startpos[2][1] = this.start.offset(-1, 0, 0);
			this.startpos[1][1] = this.start;
			this.startpos[0][1] = this.start.offset(1, 0, 0);
			this.startpos[2][2] = this.start.offset(-1, -1, 0);
			this.startpos[1][2] = this.start.offset(0, -1, 0);
			this.startpos[0][2] = this.start.offset(1, -1, 0);
		}
	}
	
	@Override
	public void initGui(PC_GresGuiHandler gui) {
		PC_GresWindow w = new PC_GresWindow(PC_Lang.tr("item.PCco_ItemOreSniffer.name"));
		w.setLayout(new PC_GresLayoutVertical());
		PC_GresContainer vg = new PC_GresGroupContainer().setLayout(new PC_GresLayoutVertical());

		vg.add(new PC_GresLabel(PC_Lang.tr("PCco.gui.sniffer.distance")));

		vg.add(this.slider = new PC_GresSlider().setSteps(RANGE-1));
		this.slider.setFill(Fill.HORIZONTAL);
		this.slider.setEditable(true);
		this.slider.addEventListener(this);
		vg.add(this.distanceL = new PC_GresLabel("0"));
		w.add(vg);

		w.add(this.inv = new PC_GresInventory(3, 3));
		IInventory inven = new InventoryBasic("", true, 1);
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				this.inv.setSlot(x, y, new PC_Slot(inven, 0));
			}
		}
		gui.add(w);

		loadBlocksForDistance(0);
	}

	private void loadBlocksForDistance(int distance) {
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				
				PC_Vec3I pos = this.startpos[x][y].offset(new PC_Vec3I().offset(this.vector).mul(distance));

				ItemStack stack = getItemStackFrom(pos);

				((PC_Slot) this.inv.getSlot(x, y)).setBackgroundStack(stack);
			}
		}
	}
	
	private ItemStack getItemStackFrom(PC_Vec3I pos){
		Block block = PC_Utils.getBlock(this.world, pos);
		if (block==null || block.isAir(this.world, pos.x, pos.y, pos.z)) {
			return null;
		}
		Item item = block.getItem(this.world, pos.x, pos.y, pos.z);

        if (item == null)
        {
            return null;
        }

        Block b = item instanceof ItemBlock && !block.isFlowerPot() ? Block.getBlockFromItem(item) : block;
        return new ItemStack(item, 1, b.getDamageValue(this.world, pos.x, pos.y, pos.z));
	}

	@Override
	public void onEvent(PC_GresEvent event) {
		if (event.getComponent() == this.slider) {
			int distance = (int) this.slider.getProgress();
			this.distanceL.setText(""+distance);
			loadBlocksForDistance(distance);
		}
	}
	
}
