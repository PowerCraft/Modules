package powercraft.itemstorage.tileentity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import powercraft.api.PC_3DRotationY;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Field;
import powercraft.api.PC_Utils;
import powercraft.api.PC_Field.Flag;
import powercraft.api.block.PC_TileEntityRotateable;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.PC_IGresGuiOpenHandler;
import powercraft.api.renderer.PC_ITileEntityRenderer;
import powercraft.api.renderer.PC_TileEntitySpecialRenderer;
import powercraft.itemstorage.PCis_ChannelChestSave;
import powercraft.itemstorage.PCis_ChannelChestSave.PCis_ChannelChestInventory;
import powercraft.itemstorage.PCis_ItemStorage;
import powercraft.itemstorage.container.PCis_ContainerChannelChest;
import powercraft.itemstorage.gui.PCis_GuiChannelChest;
import powercraft.itemstorage.item.PCis_ItemCompressor;


public class PCis_TileEntityChannelChest extends PC_TileEntityRotateable implements IInventory, PC_IGresGuiOpenHandler, PC_ITileEntityRenderer {

	@PC_Field(name="channelID")
	private int id;
	private PCis_ChannelChestInventory inventory;
	private float prevLidAngle;
	private float lidAngle;
	private boolean open;
	
	@SideOnly(Side.CLIENT)
	private static class CLIENT{
		static final ModelChest model = new ModelChest();
		static final ResourceLocation texture = PC_Utils.getResourceLocation(PCis_ItemStorage.INSTANCE, "textures/blocks/ChannelChest/normal.png");
	}
	
	public PCis_TileEntityChannelChest() {
		
	}
	
	public PCis_TileEntityChannelChest(World world, int id) {
		if(!world.isRemote){
			this.id = id;
			this.inventory = PCis_ChannelChestSave.addRef(id);
		}else{
			this.inventory = PCis_ChannelChestSave.getFake();
		}
	}
	
	@Override
	public void onBlockPostSet(PC_Direction side, ItemStack stack, EntityPlayer player, float hitX, float hitY, float hitZ) {
		set3DRotation(new PC_3DRotationY(player));
	}
	
	@SuppressWarnings("hiding")
	public void changeID(int id){
		if(!isClient()){
			PCis_ChannelChestSave.remove(this.id);
			this.id = id;
			this.inventory = PCis_ChannelChestSave.addRef(id);
		}
	}
	
	@Override
	public void onLoadedFromNBT(Flag flag) {
		if(flag==Flag.SAVE){
			this.inventory = PCis_ChannelChestSave.getInventoryForChannelChest(this.id);
		}else if(this.inventory!=null){
			this.inventory = PCis_ChannelChestSave.getFake();
		}
	}

	@Override
	public int getSizeInventory() {
		return this.inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return this.inventory.getStackInSlot(i);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return this.inventory.decrStackSize(i, j);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return this.inventory.getStackInSlotOnClosing(i);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemStack) {
		this.inventory.setInventorySlotContents(i, itemStack);
	}

	@Override
	public String getInventoryName() {
		return this.inventory.getInventoryName();
	}

	@Override
	public boolean hasCustomInventoryName() {
		return this.inventory.hasCustomInventoryName();
	}

	@Override
	public int getInventoryStackLimit() {
		return this.inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.inventory.isUseableByPlayer(player);
	}

	@Override
	public void openInventory() {
		this.inventory.openInventory();
	}

	@Override
	public void closeInventory() {
		this.inventory.closeInventory();
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemStack) {
		return this.inventory.isItemValidForSlot(i, itemStack);
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, PC_Direction side) {
		ItemStack is = player.getHeldItem();
		if(is!=null && ((is.getItem()==PCis_ItemStorage.compressor && is.getItemDamage()==PCis_ItemCompressor.CHANNEL)
				||(is.getItem() instanceof ItemBlock && ((ItemBlock)is.getItem()).field_150939_a==PCis_ItemStorage.CHANNEL_CHEST))){
			NBTTagCompound tagCompound = is.getTagCompound();
			if(tagCompound==null){
				is.setTagCompound(tagCompound = new NBTTagCompound());
			}
			tagCompound.setInteger("id", this.id);
			return true;
		}
		return super.onBlockActivated(player, side);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public PC_IGresGui openClientGui(EntityPlayer player, NBTTagCompound serverData) {
		return new PCis_GuiChannelChest(player, this);
	}

	@Override
	public PC_GresBaseWithInventory openServerGui(EntityPlayer player) {
		return new PCis_ContainerChannelChest(player, this);
	}

	@Override
	public NBTTagCompound sendOnGuiOpenToClient(EntityPlayer player) {
		return null;
	}

	@Override
	public int getComparatorInput(PC_Direction side) {
		return Container.calcRedstoneFromInventory(this.inventory);
	}
	
	@Override
	public void onTick() {
		if(this.worldObj.isRemote){
			this.prevLidAngle = this.lidAngle;
			if(this.open){
				this.lidAngle+=0.1f;
			}else{
				this.lidAngle-=0.1f;
			}
			if(this.lidAngle>1){
				this.lidAngle = 1;
			}else if(this.lidAngle<0){
				this.lidAngle = 0;
			}
		}else if(this.inventory!=null){
			if(this.inventory.getPlayersAccessing()>0 != this.open){
				this.open = !this.open;
				NBTTagCompound nbtTagCompound = new NBTTagCompound();
				nbtTagCompound.setInteger("type", 1);
				nbtTagCompound.setBoolean("open", this.open);
				sendMessage(nbtTagCompound);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onClientMessage(EntityPlayer player, NBTTagCompound nbtTagCompound) {
		switch(nbtTagCompound.getInteger("type")){
		case 1:
			this.open = nbtTagCompound.getBoolean("open");
			break;
		default:
			super.onClientMessage(player, nbtTagCompound);
			break;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderTileEntityAt(PC_TileEntitySpecialRenderer tileEntityRenderer, double x, double y, double z, float timeStamp) {
		tileEntityRenderer.bindTexture(CLIENT.texture);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glTranslatef(0, 1, 1);
        GL11.glScalef(1.0F, -1.0F, -1.0F);
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        
        int rot = 0;
        if(get3DRotation()!=null){
	        PC_Direction dir = get3DRotation().getSidePosition(PC_Direction.SOUTH);
	        switch(dir){
			case EAST:
				rot = 90;
				break;
			case NORTH:
				rot = 0;
				break;
			case SOUTH:
				rot = 180;
				break;
			case WEST:
				rot = 270;
				break;
			default:
				break;
	        }
        }
        GL11.glRotatef(rot, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        float angle = this.prevLidAngle + (this.lidAngle - this.prevLidAngle) * timeStamp;

        angle = 1.0F - angle;
        angle = 1.0F - angle * angle * angle;
        CLIENT.model.chestLid.rotateAngleX = -(angle * (float)Math.PI / 2.0F);
        CLIENT.model.renderAll();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean renderWorldBlock(int modelId, RenderBlocks renderer) {
		return true;
	}
	
}
