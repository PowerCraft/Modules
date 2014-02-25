package powercraft.machines.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Field;
import powercraft.api.PC_Utils;
import powercraft.api.block.PC_TileEntityWithInventory;
import powercraft.api.energy.PC_EnergyGrid;
import powercraft.api.energy.PC_IEnergyGridConsumer;
import powercraft.api.energy.PC_IEnergyGridTile;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.PC_IGresGuiOpenHandler;
import powercraft.api.grid.PC_GridHelper;
import powercraft.api.grid.PC_IGridHolder;
import powercraft.api.redstone.PC_RedstoneWorkType;
import powercraft.machines.block.PCma_BlockFurnace;
import powercraft.machines.container.PCma_ContainerFurnace;
import powercraft.machines.gui.PCma_GuiFurnace;

public class PCma_TileEntityFurnace extends PC_TileEntityWithInventory implements PC_IEnergyGridConsumer, PC_IGridHolder, PC_IGresGuiOpenHandler {

	private PC_EnergyGrid grid;
	@PC_Field
	private float done;
	private boolean working;
	
	public PCma_TileEntityFurnace() {
		super("Furnace", 2, new Group(true, 0), new Group(false, 1));
		workWhen = PC_RedstoneWorkType.EVER;
	}

	@Override
	public void setGrid(PC_EnergyGrid grid) {
		this.grid = grid;
	}

	@Override
	public PC_EnergyGrid getGrid() {
		return grid;
	}

	@Override
	public void getGridIfNull() {
		PC_GridHelper.getGridIfNull(worldObj, xCoord, yCoord, zCoord, 0x3F, this, PC_EnergyGrid.factory, PC_IEnergyGridTile.class);
	}

	@Override
	public void removeFormGrid() {
		PC_GridHelper.removeFormGrid(worldObj, (PC_IEnergyGridTile)this);
	}

	public boolean couldWork(){
		if(!isWorking())
			return false;
		ItemStack smeltingResult = PC_Utils.getSmeltingResult(inventoryContents[0]);
		if(smeltingResult==null)
			return false;
		if(inventoryContents[1]==null)
			return true;
		return inventoryContents[1].isItemEqual(smeltingResult) && inventoryContents[1].getMaxStackSize()>inventoryContents[1].stackSize;
	}
	
	@Override
	public float getEnergyRequested() {
		if(couldWork())
			return 10;
		return 0;
	}

	@Override
	public void useEnergy(float energy) {
		if(couldWork()){
			if(!working){
				working = true;
				sendWorking();
			}
			done += energy/5.0f;
			if(done>=100){
				done = 0;
				ItemStack itemStack = PC_Utils.getSmeltingResult(decrStackSize(0, 1));
				if(inventoryContents[1]!=null){
					itemStack.stackSize += inventoryContents[1].stackSize;
				}
				setInventorySlotContents(1, itemStack);
				detectAndSendChanges();
			}
			sendProgressBarUpdate(0, (int)(done));
		}else{
			if(working){
				working = false;
				sendWorking();
			}
			done=0;
		}
	}

	public void sendWorking(){
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		nbtTagCompound.setInteger("type", 0);
		nbtTagCompound.setBoolean("working", working);
		sendMessage(nbtTagCompound);
	}
	
	@Override
	public void sendProgressBarUpdates() {
		sendProgressBarUpdate(0, (int)(done));
	}

	@Override
	public float getMaxPercentToWork() {
		return 0.8f;
	}

	@Override
	public PC_IGresGui openClientGui(EntityPlayer player, NBTTagCompound serverData) {
		return new PCma_GuiFurnace(player, this);
	}

	@Override
	public PC_GresBaseWithInventory openServerGui(EntityPlayer player) {
		return new PCma_ContainerFurnace(player, this);
	}

	@Override
	public NBTTagCompound sendOnGuiOpenToClient(EntityPlayer player) {
		return null;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return i==0;
	}

	@Override
	public IIcon getIcon(PC_Direction side) {
		if(side==PC_Direction.NORTH){
			if(working){
				return PCma_BlockFurnace.frontOn;
			}
			return PCma_BlockFurnace.front;
		}
		return PCma_BlockFurnace.side;
	}

	@Override
	public void onClientMessage(EntityPlayer player, NBTTagCompound nbtTagCompound) {
		if(nbtTagCompound.getInteger("type")==0){
			working = nbtTagCompound.getBoolean("working");
			renderUpdate();
		}
	}
	
}
