package powercraft.machines.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import powercraft.api.PC_3DRotation;
import powercraft.api.PC_3DRotationY;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Field;
import powercraft.api.PC_Field.Flag;
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
import powercraft.api.grid.PC_IGridSided;
import powercraft.api.grid.PC_IGridTile;
import powercraft.api.redstone.PC_RedstoneWorkType;
import powercraft.machines.block.PCma_BlockFurnace;
import powercraft.machines.container.PCma_ContainerFurnace;
import powercraft.machines.gui.PCma_GuiFurnace;

public class PCma_TileEntityFurnace extends PC_TileEntityWithInventory implements PC_IEnergyGridConsumer, PC_IGridHolder, PC_IGresGuiOpenHandler, PC_IGridSided {

	private PC_EnergyGrid grid;
	@PC_Field
	private float done;
	@PC_Field(flags={Flag.SYNC})
	private boolean working;
	@PC_Field(flags={Flag.SAVE, Flag.SYNC})
	protected PC_3DRotation rotation;
	
	public PCma_TileEntityFurnace() {
		super("Furnace", 2, new Group(true, 0), new Group(false, 1));
		this.workWhen = PC_RedstoneWorkType.ALWAYS;
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

	public boolean couldWork(){
		if(!isWorking())
			return false;
		ItemStack smeltingResult = PC_Utils.getSmeltingResult(this.inventoryContents[0]);
		if(smeltingResult==null)
			return false;
		if(this.inventoryContents[1]==null)
			return true;
		return this.inventoryContents[1].isItemEqual(smeltingResult) && this.inventoryContents[1].getMaxStackSize()>this.inventoryContents[1].stackSize;
	}
	
	@Override
	public float getEnergyRequested() {
		if(couldWork())
			return 10;
		return 0;
	}

	@Override
	public void useEnergy(float energy) {
		if(couldWork() && energy>0){
			if(!this.working){
				this.working = true;
				sendWorking();
			}
			sendProgressBarUpdate(1, (int)(energy*100));
			this.done += energy/5.0f;
			if(this.done>=100){
				this.done = 0;
				ItemStack itemStack = PC_Utils.getSmeltingResult(decrStackSize(0, 1));
				if(this.inventoryContents[1]!=null){
					itemStack.stackSize += this.inventoryContents[1].stackSize;
					setInventorySlotContents(1, null);
				}
				moveOrStore(1, itemStack);
				detectAndSendChanges();
			}
			sendProgressBarUpdate(0, (int)(this.done));
		}else{
			if(this.working){
				this.working = false;
				sendWorking();
				sendProgressBarUpdate(0, 0);
				sendProgressBarUpdate(1, 0);
			}
			this.done=0;
		}
	}

	public void sendWorking(){
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		nbtTagCompound.setInteger("type", 0);
		nbtTagCompound.setBoolean("working", this.working);
		sendMessage(nbtTagCompound);
	}
	
	@Override
	public void sendProgressBarUpdates() {
		sendProgressBarUpdate(0, (int)(this.done));
	}

	@Override
	public float getMaxPercentToWork() {
		return 0.8f;
	}

	@Override
	@SideOnly(Side.CLIENT)
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
			if(this.working){
				return PCma_BlockFurnace.frontOn;
			}
			return PCma_BlockFurnace.front;
		}
		return PCma_BlockFurnace.side;
	}

	@Override
	public void onClientMessage(EntityPlayer player, NBTTagCompound nbtTagCompound) {
		if(nbtTagCompound.getInteger("type")==0){
			this.working = nbtTagCompound.getBoolean("working");
			renderUpdate();
		}
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
	public <T extends PC_IGridTile<?, T, ?, ?>> T getTile(PC_Direction side, int flags, Class<T> tileClass) {
		if(this.rotation.getSidePosition(PC_Direction.NORTH)==side)
			return null;
		if(tileClass==PC_IEnergyGridTile.class)
			return tileClass.cast(this);
		return null;
	}
	
	@Override
	public PC_RedstoneWorkType[] getAllowedRedstoneWorkTypes() {
		return new PC_RedstoneWorkType[]{null, PC_RedstoneWorkType.ALWAYS, PC_RedstoneWorkType.ON_ON, PC_RedstoneWorkType.ON_OFF};
	}
	
}
