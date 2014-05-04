package powercraft.transport.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
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
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.PC_IGresGuiOpenHandler;
import powercraft.api.inventory.PC_InventoryUtils;
import powercraft.api.redstone.PC_RedstoneWorkType;
import powercraft.transport.block.PCtr_BlockEjector;
import powercraft.transport.gui.PCtr_GuiEjector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class PCtr_TileEntityEjector extends PC_TileEntityWithInventory implements PC_IGresGuiOpenHandler {

	public static final int EJECT_STACKS = 0, EJECT_ITEMS = 1, EJECT_ALL = 2;
	public static final int FIRST_SLOT = 0, LAST_SLOT = 1, RANDOM_SLOT = 2;
	
	private static Random rand = new Random();
	
	@PC_Field
	private int type;
	@PC_Field
	private int numStacks = 1;
	@PC_Field
	private int numItems = 1;
	@PC_Field(flags={Flag.SAVE, Flag.SYNC})
	protected PC_3DRotation rotation;
	
	public PCtr_TileEntityEjector() {
		super("Ejector", 1, new Group(true, 0));
		for(int i=0; i<6; i++){
			setSideGroup(i, 0);
		}
	}

	@Override
	public PC_RedstoneWorkType[] getAllowedRedstoneWorkTypes() {
		return new PC_RedstoneWorkType[]{PC_RedstoneWorkType.ON_FLANK, PC_RedstoneWorkType.ON_HI_FLANK, PC_RedstoneWorkType.ON_LOW_FLANK};
	}
	
	@Override
	protected void doWork() {
		PC_Direction d = PC_Utils.getSidePosition(this.worldObj, this.xCoord, this.yCoord, this.zCoord, PC_Direction.NORTH);
		double x = this.xCoord+0.5+d.offsetX*0.7;
		double y = this.yCoord+0.5+d.offsetY*0.7;
		double z = this.zCoord+0.5+d.offsetZ*0.7;
		for(PC_Direction dir:PC_Direction.VALID_DIRECTIONS){
			if(getIDForSide(dir)!=-1){
				List<ItemStack> list = getInventoryDrops(dir);
				if(list!=null && !this.worldObj.isRemote){
					for(ItemStack itemStack:list){
						EntityItem entityitem = new EntityItem(this.worldObj, x+(rand.nextFloat()-0.5)*(d.offsetX==0?0.1:0.4), y+(rand.nextFloat()-0.5)*(d.offsetY==0?0.1:0.4), z+(rand.nextFloat()-0.5)*(d.offsetZ==0?0.1:0.4), itemStack);
						entityitem.motionX += d.offsetX*0.2;
						entityitem.motionY *= d.offsetY;
						entityitem.motionZ += d.offsetZ*0.2;
						entityitem.delayBeforeCanPickup = 10;
						PC_Utils.spawnEntity(this.worldObj, entityitem);
					}
				}
			}
		}
	}

	public int getEjectionMode(){
		return this.type&3;
	}
	
	public int getSelectionMode(){
		return (this.type>>>2)&3;
	}
	
	private List<ItemStack> getInventoryDrops(PC_Direction side){
		IInventory inv = PC_InventoryUtils.getInventoryAt(this.worldObj, this.xCoord+side.offsetX, this.yCoord+side.offsetY, this.zCoord+side.offsetZ);
		if(inv==null)
			return null;
		int[] indices = PC_InventoryUtils.getInvIndexesForSide(inv, side);
		int ejectionMode = getEjectionMode();
		int selectionMode = getSelectionMode();
		List<ItemStack> drops = new ArrayList<ItemStack>();
		if(ejectionMode==EJECT_ALL){
			if(indices==null){
				for(int i=0; i<inv.getSizeInventory(); i++){
					ItemStack is = inv.getStackInSlot(i);
					if(is!=null){
						is = inv.decrStackSize(i, is.stackSize);
						if(is!=null){
							drops.add(is);
						}
					}
				}
			}else{
				for(int i=0; i<indices.length; i++){
					int j = indices[i];
					ItemStack is = inv.getStackInSlot(j);
					if(is!=null){
						is = inv.decrStackSize(j, is.stackSize);
						if(is!=null){
							drops.add(is);
						}
					}
				}
			}
		}else if(ejectionMode==EJECT_ITEMS){
			int slot = getSelectedSlot(inv, indices, selectionMode);
			if(slot!=-1)
				drops.add(inv.decrStackSize(slot, this.numItems));
		}else if(ejectionMode==EJECT_STACKS){
			for(int i=0; i<this.numStacks; i++){
				int slot = getSelectedSlot(inv, indices, selectionMode);
				if(slot==-1)
					break;
				drops.add(inv.decrStackSize(slot, inv.getStackInSlot(slot).stackSize));
			}
		}
		return drops;
	}
	
	private static int getSelectedSlot(IInventory inv, int[] indices, int selectionMode){
		if(indices==null){
			if(selectionMode==FIRST_SLOT){
				for(int i=0; i<inv.getSizeInventory(); i++){
					ItemStack is = inv.getStackInSlot(i);
					if(is!=null){
						return i;
					}
				}
			}else if(selectionMode==LAST_SLOT){
				for(int i=inv.getSizeInventory()-1; i>=0; i--){
					ItemStack is = inv.getStackInSlot(i);
					if(is!=null){
						return i;
					}
				}
			}else if(selectionMode==RANDOM_SLOT){
				List<Integer> slots = new ArrayList<Integer>();
				for(int i=0; i<inv.getSizeInventory(); i++){
					ItemStack is = inv.getStackInSlot(i);
					if(is!=null){
						slots.add(Integer.valueOf(i));
					}
				}
				if(slots.size()>0){
					return slots.get(rand.nextInt(slots.size())).intValue();
				}
			}
		}else{
			if(selectionMode==FIRST_SLOT){
				for(int i=0; i<indices.length; i++){
					int j = indices[i];
					ItemStack is = inv.getStackInSlot(j);
					if(is!=null){
						return j;
					}
				}
			}else if(selectionMode==LAST_SLOT){
				for(int i=indices.length-1; i>=0; i--){
					int j = indices[i];
					ItemStack is = inv.getStackInSlot(j);
					if(is!=null){
						return j;
					}
				}
			}else if(selectionMode==RANDOM_SLOT){
				List<Integer> slots = new ArrayList<Integer>();
				for(int i=0; i<indices.length; i++){
					int j = indices[i];
					ItemStack is = inv.getStackInSlot(j);
					if(is!=null){
						slots.add(Integer.valueOf(j));
					}
				}
				if(slots.size()>0){
					return slots.get(rand.nextInt(slots.size())).intValue();
				}
			}
		}
		return -1;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public PC_IGresGui openClientGui(EntityPlayer player, NBTTagCompound serverData) {
		this.type = serverData.getInteger("type");
		this.numStacks = serverData.getInteger("numStacks");
		this.numItems = serverData.getInteger("numItems");
		return new PCtr_GuiEjector(this);
	}

	@Override
	public PC_GresBaseWithInventory openServerGui(EntityPlayer player) {
		return null;
	}

	@Override
	public NBTTagCompound sendOnGuiOpenToClient(EntityPlayer player) {
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		nbtTagCompound.setInteger("type", this.type);
		nbtTagCompound.setInteger("numStacks", this.numStacks);
		nbtTagCompound.setInteger("numItems", this.numItems);
		return nbtTagCompound;
	}

	public int getStackCount() {
		return this.numStacks;
	}
	
	public int getItemCount() {
		return this.numItems;
	}

	public void setAll(int ejectionMode, int selectionMode, int numStacks, int numItems) {
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		nbtTagCompound.setInteger("type", 3);
		nbtTagCompound.setInteger("ttype", (ejectionMode&3)|((selectionMode&3)<<2));
		nbtTagCompound.setInteger("numStacks", numStacks);
		nbtTagCompound.setInteger("numItems", numItems);
		sendMessage(nbtTagCompound);
	}

	@Override
	public void onMessage(EntityPlayer player, NBTTagCompound nbtTagCompound) {
		switch(nbtTagCompound.getInteger("type")){
		case 3:
			this.type = nbtTagCompound.getInteger("ttype");
			this.numStacks = nbtTagCompound.getInteger("numStacks");
			this.numItems = nbtTagCompound.getInteger("numItems");
			break;
		default:
			break;
		}
		if(this.numStacks<1)
			this.numStacks =1;
		if(this.numItems<1)
			this.numItems =1;
	}
	
	@Override
	public void onAdded(EntityPlayer player) {
		set3DRotation(new PC_3DRotationY(player));
		super.onAdded(player);
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
	public boolean canRotate() {
		return true;
	}

	@Override
	public IIcon getIcon(PC_Direction side) {
		if(side==PC_Direction.NORTH){
			return PCtr_BlockEjector.icons[0];
		}
		return PCtr_BlockEjector.icons[1];
	}
	
}
