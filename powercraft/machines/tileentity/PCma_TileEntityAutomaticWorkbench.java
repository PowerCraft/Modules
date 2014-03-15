package powercraft.machines.tileentity;

import java.util.HashMap;
import java.util.Map.Entry;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import powercraft.api.PC_3DRotation;
import powercraft.api.PC_3DRotationY;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Field;
import powercraft.api.PC_Field.Flag;
import powercraft.api.block.PC_TileEntityWithInventory;
import powercraft.api.energy.PC_EnergyGrid;
import powercraft.api.energy.PC_IEnergyGridConsumer;
import powercraft.api.energy.PC_IEnergyGridTile;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.PC_IGresGuiOpenHandler;
import powercraft.api.grid.PC_GridHelper;
import powercraft.api.grid.PC_IGridHolder;
import powercraft.api.grid.PC_IGridSided;
import powercraft.api.grid.PC_IGridTile;
import powercraft.api.inventory.PC_IInventoryBackground;
import powercraft.api.inventory.PC_InventoryUtils;
import powercraft.api.redstone.PC_RedstoneWorkType;
import powercraft.machines.block.PCma_BlockAutomaticWorkbench;
import powercraft.machines.container.PCma_ContainerAutomaticWorkbench;
import powercraft.machines.gui.PCma_GuiAutomaticWorkbench;

public class PCma_TileEntityAutomaticWorkbench extends PC_TileEntityWithInventory implements PC_IEnergyGridConsumer, PC_IGridHolder, PC_IGresGuiOpenHandler, PC_IGridSided, PC_IInventoryBackground {

	private PC_EnergyGrid grid;
	@PC_Field(flags={Flag.SAVE, Flag.SYNC})
	protected PC_3DRotation rotation;
	
	private boolean workNext;
	
	private boolean marking;
	
	private boolean working;
	
	public PCma_TileEntityAutomaticWorkbench() {
		super("AutomaticWorkbench", 29, new Group(true, PC_InventoryUtils.makeIndexList(10, 28)), new Group(false, 9));
		// 0-8 => Grid, 9=>Out, 10-27=>In, 28=>Prod
		this.workWhen = PC_RedstoneWorkType.EVER;
	}

	@Override
	public void setGrid(PC_EnergyGrid grid) {
		this.grid = grid;
	}

	@Override
	public PC_EnergyGrid getGrid() {
		return this.grid;
	}
	
	@Override
	public void onAdded(EntityPlayer player) {
		set3DRotation(new PC_3DRotationY(player));
		super.onAdded(player);
	}

	@Override
	public void getGridIfNull() {
		if(!this.worldObj.isRemote && this.grid==null){
			int connectable = 0;
			PC_Direction not = this.rotation.getSidePosition(PC_Direction.NORTH);
			for(PC_Direction dir:PC_Direction.VALID_DIRECTIONS){
				connectable<<=1;
				if(dir!=not){
					connectable |= 1;
				}
			}
			PC_GridHelper.getGridIfNull(this.worldObj, this.xCoord, this.yCoord, this.zCoord, connectable, this, PC_EnergyGrid.factory, PC_IEnergyGridTile.class);
		}
	}

	@Override
	public void removeFromGrid() {
		PC_GridHelper.removeFromGrid(this.worldObj, (PC_IEnergyGridTile)this);
	}

	public boolean canCraft(){
		if(this.inventoryContents[28]==null)
			return false;
		HashMap<ItemStack, Integer> counts = new HashMap<ItemStack, Integer>();
		if(this.inventoryContents[9]!=null){
			if(!(this.inventoryContents[9].isItemEqual(this.inventoryContents[28]) && ItemStack.areItemStackTagsEqual(this.inventoryContents[9], this.inventoryContents[28]))){
				return false;
			}
			if(this.inventoryContents[9].stackSize>=this.inventoryContents[9].getMaxStackSize())
				return false;
			if(this.inventoryContents[9].stackSize>=getSlotStackLimit(9))
				return false;
		}
		for(int i=0; i<9; i++){
			ItemStack is = this.inventoryContents[i];
			if(is!=null){
				boolean ok = false;
				for(Entry<ItemStack, Integer> e:counts.entrySet()){
					ItemStack o = e.getKey();
					if(is.isItemEqual(o) && ItemStack.areItemStackTagsEqual(is, o)){
						e.setValue(Integer.valueOf(e.getValue().intValue()+1));
						ok = true;
						break;
					}
				}
				if(!ok){
					counts.put(is, Integer.valueOf(1));
				}
			}
		}
		int[]indices = PC_InventoryUtils.makeIndexList(10, 28);
		for(Entry<ItemStack, Integer> e:counts.entrySet()){
			int count = PC_InventoryUtils.getInventoryCountOf(this, e.getKey(), indices);
			if(count<e.getValue().intValue())
				return false;
		}
		return true;
	}
	
	public boolean couldWork(){
		if(this.workNext || isWorking()){
			this.workNext = false;
			return canCraft();
		}
		return false;
	}
	
	@Override
	protected void doWork() {
		if(!isWorking())
			this.workNext = true;
	}

	@Override
	public float getEnergyRequested() {
		if(couldWork())
			return 10;
		return 0;
	}

	@Override
	public void useEnergy(float energy) {
		if(canCraft() && energy>0){
			this.working = true;
			sendProgressBarUpdate(0, (int)(energy*100));
			for(int i=0; i<9; i++){
				ItemStack is = this.inventoryContents[i];
				if(is!=null){
					for(int j=10; j<28; j++){
						ItemStack o = this.inventoryContents[j];
						if(o!=null && is.isItemEqual(o) && ItemStack.areItemStackTagsEqual(is, o)){
							decrStackSize(j, 1);
							break;
						}
					}
				}
			}
			ItemStack itemStack = this.inventoryContents[9];
			setInventorySlotContents(9, null);
			if(itemStack==null){
				itemStack = this.inventoryContents[28].copy();
				itemStack.stackSize = 1;
			}else{
				itemStack.stackSize++;
			}
			moveOrStore(9, itemStack);
			detectAndSendChanges();
		}else{
			if(this.working){
				sendProgressBarUpdate(0, 0);
				this.working = false;
			}
		}
	}
	
	@Override
	public void markDirty() {
		if(this.marking)
			return;
		this.marking = true;
		Container c = new Container() {
			@Override
			public boolean canInteractWith(EntityPlayer var1) {
				return true;
			}
		};
		InventoryCrafting inventoryCrafting = new InventoryCrafting(c, 3, 3);
		for(int i=0; i<9; i++){
			inventoryCrafting.setInventorySlotContents(i, this.inventoryContents[i]);
		}
		ItemStack itemStack = CraftingManager.getInstance().findMatchingRecipe(inventoryCrafting, this.worldObj);
		if(itemStack!=null){
			itemStack = itemStack.copy();
			itemStack.stackSize = 0;
		}
		setInventorySlotContents(28, itemStack);
		this.marking = false;
		super.markDirty();
	}

	@Override
	public float getMaxPercentToWork() {
		return 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public PC_IGresGui openClientGui(EntityPlayer player, NBTTagCompound serverData) {
		return new PCma_GuiAutomaticWorkbench(player, this);
	}

	@Override
	public PC_GresBaseWithInventory openServerGui(EntityPlayer player) {
		return new PCma_ContainerAutomaticWorkbench(player, this);
	}

	@Override
	public NBTTagCompound sendOnGuiOpenToClient(EntityPlayer player) {
		return null;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return i!=9 && i!=28;
	}

	@Override
	public IIcon getIcon(PC_Direction side) {
		if(side==PC_Direction.NORTH){
			return PCma_BlockAutomaticWorkbench.front;
		}
		return PCma_BlockAutomaticWorkbench.side;
	}
	
	@Override
	public void onBlockPostSet(PC_Direction side, ItemStack stack, EntityPlayer player, float hitX, float hitY, float hitZ) {
		if(this.rotation==null)
			set3DRotation(new PC_3DRotationY(player));
	}

	@Override
	public PC_3DRotation get3DRotation() {
		return this.rotation;
	}

	@Override
	public boolean set3DRotation(PC_3DRotation rotation) {
		this.rotation = rotation;
		sync();
		return true;
	}
	
	@Override
	public void onLoadedFromNBT(Flag flag) {
		super.onLoadedFromNBT(flag);
		renderUpdate();
	}

	@Override
	public boolean canRotate() {
		return true;
	}

	@Override
	public <T extends PC_IGridTile<?, T, ?, ?>> T getTile(PC_Direction side, Class<T> tileClass) {
		if(this.rotation.getSidePosition(PC_Direction.NORTH)==side)
			return null;
		if(tileClass==PC_IEnergyGridTile.class)
			return tileClass.cast(this);
		return null;
	}
	
	@Override
	public PC_RedstoneWorkType[] getAllowedRedstoneWorkTypes() {
		return new PC_RedstoneWorkType[]{null, PC_RedstoneWorkType.EVER, PC_RedstoneWorkType.ON_ON, PC_RedstoneWorkType.ON_OFF, PC_RedstoneWorkType.ON_FLANK, PC_RedstoneWorkType.ON_HI_FLANK, PC_RedstoneWorkType.ON_LOW_FLANK};
	}

	@Override
	public int getSlotStackLimit(int i) {
		return super.getSlotStackLimit(i);
	}

	@Override
	public ItemStack getBackgroundStack(int slotIndex) {
		if(slotIndex==9)
			return this.inventoryContents[28];
		return null;
	}

	@Override
	public boolean renderGrayWhenEmpty(int slotIndex) {
		if(slotIndex==9)
			return true;
		return false;
	}
	
}
