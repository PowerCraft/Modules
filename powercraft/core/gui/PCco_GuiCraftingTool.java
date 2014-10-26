package powercraft.core.gui;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import powercraft.api.PC_Lang;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_Gres;
import powercraft.api.gres.PC_GresAlign.Fill;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresContainer;
import powercraft.api.gres.PC_GresDisplay;
import powercraft.api.gres.PC_GresDisplayObject;
import powercraft.api.gres.PC_GresFrame;
import powercraft.api.gres.PC_GresGroupContainer;
import powercraft.api.gres.PC_GresGuiHandler;
import powercraft.api.gres.PC_GresInventory;
import powercraft.api.gres.PC_GresPlayerInventory;
import powercraft.api.gres.PC_GresScrollBar;
import powercraft.api.gres.PC_GresTextEdit;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.events.PC_GresEvent;
import powercraft.api.gres.events.PC_GresKeyEvent;
import powercraft.api.gres.events.PC_GresKeyEventResult;
import powercraft.api.gres.events.PC_GresMouseButtonEvent;
import powercraft.api.gres.events.PC_GresMouseButtonEvent.Event;
import powercraft.api.gres.events.PC_GresMouseEventResult;
import powercraft.api.gres.events.PC_GresTickEvent;
import powercraft.api.gres.events.PC_IGresEventListener;
import powercraft.api.gres.layout.PC_GresLayoutHorizontal;
import powercraft.api.gres.layout.PC_GresLayoutVertical;
import powercraft.api.gres.slot.PC_Slot;
import powercraft.api.network.PC_PacketHandler;
import powercraft.core.PCco_CraftingToolCrafter;
import powercraft.core.PCco_CraftingToolCraftingInventory;
import powercraft.core.PCco_CraftingToolInventory;
import powercraft.core.PCco_PacketCrafting;

@SideOnly(Side.CLIENT)
public class PCco_GuiCraftingTool extends PC_GresBaseWithInventory implements PC_IGresGui, PC_IGresEventListener {
	
	private PC_GresWindow window;
	private PCco_CraftingToolInventory ctinv;
	private PCco_CraftingToolCraftingInventory ctcinv;
	private PC_GresScrollBar scrollBar1;
	private PC_GresScrollBar scrollBar2;
	private PC_GresTextEdit search;
	private PC_GresInventory inv;
	private PC_GresContainer searchView;
	private PC_GresContainer recipeView;
	private PC_GresContainer crafting1;
	private PC_GresContainer crafting2;
	private PC_GresContainer crafting3;
	private String lastSearch;
	private PC_GresGuiHandler gui;
	
	public PCco_GuiCraftingTool(EntityPlayer player) {
		super(player, new PCco_CraftingToolCraftingInventory());
	}

	@Override
	protected PC_Slot[] getAllSlots() {
		(this.ctcinv = ((PCco_CraftingToolCraftingInventory)this.inventory)).setGui(this);
		this.ctinv = new PCco_CraftingToolInventory(this, new PC_Vec2I(11, 5));
		this.invSlots = new PC_Slot[this.ctinv.getSizeInventory()+30];
		for(int i=0; i<30; i++){
			this.invSlots[i] = new PC_Slot(this.ctcinv, i);
		}
		for(int i=0; i<this.ctinv.getSizeInventory(); i++){
			this.invSlots[i+30] = new PC_Slot(this.ctinv, i);
		}
		return null;
	}

	@Override
	public void initGui(PC_GresGuiHandler gui) {
		this.gui = gui;
		this.ctinv.updateAvailability();
		this.window = new PC_GresWindow(PC_Lang.translate("PCco.gui.craftingTool.title"));
		this.window.setLayout(new PC_GresLayoutVertical());
		this.searchView = new PC_GresGroupContainer().setLayout(new PC_GresLayoutVertical());
		this.searchView.add(this.search = new PC_GresTextEdit("", 20));
		this.search.addEventListener(this);
		PC_GresContainer lh = new PC_GresGroupContainer().setLayout(new PC_GresLayoutHorizontal());
		this.inv = new PC_GresInventory(11, 5);
		this.inv.addEventListener(this);
		for(int y=0; y<5; y++){
			for(int x=0; x<11; x++){
				this.inv.setSlot(x, y, this.invSlots[y*11+x+30]);
			}
		}
		lh.add(this.inv);
		lh.add(this.scrollBar1 = new PC_GresScrollBar((this.ctinv.getNumRows()+6)*16));
		this.scrollBar1.setFill(Fill.VERTICAL);
		this.scrollBar1.setMinSize(new PC_Vec2I(10, 20));
		this.scrollBar1.addEventListener(this);
		this.searchView.add(lh);
		this.searchView.add(new PC_GresPlayerInventory(this));
		this.window.add(this.searchView);
		
		this.recipeView = new PC_GresGroupContainer().setLayout(new PC_GresLayoutVertical());
		lh = new PC_GresGroupContainer().setLayout(new PC_GresLayoutHorizontal());
		PC_GresContainer lv = new PC_GresGroupContainer().setLayout(new PC_GresLayoutVertical());
		this.crafting1 = new PC_GresFrame().setLayout(new PC_GresLayoutHorizontal());
		this.crafting1.add(new PC_GresInventory(3, 3).setSlots(this.invSlots, 1));
		this.crafting1.add(new PC_GresDisplay(new PC_GresDisplayObject(PC_Gres.getGresTexture("Craft"))));
		this.crafting1.add(new PC_GresInventory(1, 1, 20, 20).setSlot(0, 0, this.invSlots[0]));
		lv.add(this.crafting1);
		this.crafting2 = new PC_GresFrame().setLayout(new PC_GresLayoutHorizontal());
		this.crafting2.add(new PC_GresInventory(3, 3).setSlots(this.invSlots, 11));
		this.crafting2.add(new PC_GresDisplay(new PC_GresDisplayObject(PC_Gres.getGresTexture("Craft"))));
		this.crafting2.add(new PC_GresInventory(1, 1, 20, 20).setSlot(0, 0, this.invSlots[10]));
		lv.add(this.crafting2);
		this.crafting3 = new PC_GresFrame().setLayout(new PC_GresLayoutHorizontal());
		this.crafting3.add(new PC_GresInventory(3, 3).setSlots(this.invSlots, 21));
		this.crafting3.add(new PC_GresDisplay(new PC_GresDisplayObject(PC_Gres.getGresTexture("Craft"))));
		this.crafting3.add(new PC_GresInventory(1, 1, 20, 20).setSlot(0, 0, this.invSlots[20]));
		lv.add(this.crafting3);
		lh.add(lv);
		lh.add(this.scrollBar2 = new PC_GresScrollBar((this.ctinv.getNumRows()+6)*16));
		this.scrollBar2.setFill(Fill.VERTICAL);
		this.scrollBar2.setMinSize(new PC_Vec2I(10, 20));
		this.scrollBar2.addEventListener(this);
		this.recipeView.add(lh);
		this.recipeView.setVisible(false);
		gui.add(this.window);
		gui.addEventListener(this);
	}

	@Override
	public void onEvent(PC_GresEvent event) {
		PC_GresComponent component = event.getComponent();
		if(event instanceof PC_GresTickEvent){
			this.ctcinv.nextTick();
		}else if(event instanceof PC_GresKeyEvent){
			PC_GresKeyEvent ke = (PC_GresKeyEvent)event;
			int kc = ke.getKeyCode();
			if(kc==Keyboard.KEY_R){
				PC_GresGuiHandler handler = component.getGuiHandler();
				PC_Vec2I mp = handler.getMousePos();
				Slot slot = handler.getSlotAtPosition(mp);
				if(slot!=null){
					ItemStack is = slot.getStack();
					if(is==null && slot instanceof PC_Slot){
						is = ((PC_Slot)slot).getBackgroundStack();
					}
					if(is!=null){
						this.crafting1.setVisible(false);
						this.crafting2.setVisible(false);
						this.crafting3.setVisible(false);
						this.ctcinv.setProduct(is.copy());
						this.searchView.setVisible(false);
						this.window.remove(this.searchView);
						this.recipeView.setVisible(true);
						this.window.add(this.recipeView);
						this.scrollBar2.setMaxScollSize(0);
						ke.consume();
					}
				}
			}else if((component!=this.search && (kc==Keyboard.KEY_RETURN || kc==Keyboard.KEY_E)) || kc==Keyboard.KEY_ESCAPE){
				if(this.searchView.isVisible()){
					component.getGuiHandler().close();
				}else{
					this.window.remove(this.recipeView);
					this.recipeView.setVisible(false);
					this.searchView.setVisible(true);
					this.window.add(this.searchView);
					ke.consume();
				}
			}
		}else if(event instanceof PC_GresKeyEventResult){
			if(component==this.search){
				if(this.lastSearch!=null){
					if(this.lastSearch.equals(this.search.getText()))
						return;
				}
				this.ctinv.setSearchString(this.search.getText());
				this.scrollBar1.setMaxScollSize((this.ctinv.getNumRows()+6)*16);
				this.ctinv.setScroll(this.scrollBar1.getScroll()/16);
			}
		}else if(event instanceof PC_GresMouseButtonEvent){
			PC_GresMouseButtonEvent mbe = (PC_GresMouseButtonEvent)event;
			if(component==this.inv && mbe.getEvent()==Event.DOWN){
				Slot slot = this.inv.getSlotAtPosition(mbe.getMouse());
				if(slot!=null){
					craft(slot.getStack());
					mbe.consume();
				}
			}
		}else  if(event instanceof PC_GresMouseEventResult){
			if(component==this.scrollBar1){
				this.ctinv.setScroll(this.scrollBar1.getScroll()/16);
			}else if(component==this.scrollBar2){
				if(this.crafting1.getSize().y>0){
					this.ctcinv.setScroll((int)(this.scrollBar2.getScroll()/this.crafting1.getSize().y+0.5));
				}
			}
		}
	}

	private void craft(ItemStack p){
		if(p!=null){
			ItemStack is = p.copy();
			ItemStack[] pi = PCco_CraftingToolCrafter.getPlayerInventory(this.player);
			is.stackSize = PCco_CraftingToolCrafter.craft(is, pi, new ArrayList<ItemStack>(), 0, this.player);
			if(is.stackSize>0){
				ItemStack isp = this.player.inventory.getItemStack();
				if(isp==null){
					this.player.inventory.setItemStack(is);
				}else{
					if(!isp.isItemEqual(is)){
						return;
					}
					if(isp.stackSize+is.stackSize>is.getMaxStackSize()){
						return;
					}
					isp.stackSize+=is.stackSize;
					this.player.inventory.setItemStack(isp);
				}
				PCco_CraftingToolCrafter.setPlayerInventory(pi, this.player);
				PC_PacketHandler.sendToServer(new PCco_PacketCrafting(is));
				this.ctinv.updateAvailability();
			}
		}
	}

	public synchronized void updateSrcoll() {
		this.scrollBar1.setMaxScollSize(this.ctinv.getNumRows()*16);
		this.ctinv.setScroll((int)(this.scrollBar1.getScroll()/16.0+0.5));
	}

	public void updateCraftings() {
		int recipes = this.ctcinv.getNumRecipes();
		if(recipes>0){
			this.crafting1.setVisible(true);
		}
		if(recipes>1){
			this.crafting2.setVisible(true);
		}
		if(recipes>2){
			this.crafting3.setVisible(true);
		}
		this.scrollBar2.setMaxScollSize(this.crafting1.getSize().y*(recipes+1));
		this.ctcinv.setScroll((int)(this.scrollBar2.getScroll()/this.crafting1.getSize().y+0.5));
	}
	
	@Override
	public ItemStack slotClick(int par1, int par2, int par3, EntityPlayer par4EntityPlayer) {
		ItemStack is = super.slotClick(par1, par2, par3, par4EntityPlayer);
		this.ctinv.updateAvailability();
		return is;
	}
	
	public EntityPlayer getPlayer(){
		return this.player;
	}

	public void addWorking() {
		this.gui.addWorking();
	}
	
	public void removeWorking() {
		this.gui.removeWorking();
	}
	
}
