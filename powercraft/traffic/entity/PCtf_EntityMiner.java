package powercraft.traffic.entity;

import java.util.HashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Field;
import powercraft.api.PC_Field.Flag;
import powercraft.api.PC_Logger;
import powercraft.api.PC_NBTTagHandler;
import powercraft.api.PC_Utils;
import powercraft.api.PC_Vec3I;
import powercraft.api.entity.PC_Entities;
import powercraft.api.entity.PC_Entity;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.PC_IGresGuiOpenHandler;
import powercraft.api.inventory.PC_IInventory;
import powercraft.api.inventory.PC_InventoryUtils;
import powercraft.api.recipes.PC_3DRecipe.StructStart;
import powercraft.api.recipes.PC_I3DRecipeHandler;
import powercraft.api.recipes.PC_Recipes;
import powercraft.api.renderer.PC_EntityRenderer;
import powercraft.api.renderer.model.PC_Model;
import powercraft.traffic.PCtf_MinerController;
import powercraft.traffic.container.PCtf_ContainerMiner;
import powercraft.traffic.gui.PCtf_GuiMiner;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PCtf_EntityMiner extends PC_Entity implements PC_IGresGuiOpenHandler, PC_IInventory{
	
	@PC_Field
	protected ItemStack[] inventoryContents = new ItemStack[6*9];
	@PC_Field
	protected PCtf_MinerController minerController;
	@PC_Field
	protected boolean miningEnabled=false;
	
	
	public PCtf_EntityMiner(World world) {
		super(world);
		setSize(2.0f, 2.0f);
		this.yOffset = 0F;
		this.entityCollisionReduction = 1.0F;
		this.stepHeight = 0.6F;
		this.isImmuneToFire = true;
	}
	
	private static int DIR_MAPPER[] = {270, 90, 180, 0};
	
	public PCtf_EntityMiner(World world, PC_Vec3I pos, PC_Direction dir) {
		this(world);
		setPosition(pos.x+(dir==PC_Direction.EAST || dir==PC_Direction.NORTH?1:0), pos.y, pos.z+(dir==PC_Direction.EAST || dir==PC_Direction.SOUTH?1:0));
		setRotation(DIR_MAPPER[dir.ordinal()-2], 0);
		setTargetRot(Math.round(this.rotationYaw/90.0f));
		setTargetX((int) Math.round(this.posX));
		setTargetZ((int) Math.round(this.posZ));
		if(!world.isRemote){
			this.minerController = new PCtf_MinerController(this);
		}
	}
	
	@Override
	protected void onLoadedFromNBT(Flag flag) {
		if(!this.worldObj.isRemote){
			this.minerController.setMiner(this);
		}
		super.onLoadedFromNBT(flag);
	}



	@Override
	protected void entityInit() {
		this.dataWatcher.addObject(17, new Integer(0));
		this.dataWatcher.addObject(18, new Integer(1));
		this.dataWatcher.addObject(19, new Float(0));
		this.dataWatcher.addObject(20, new Integer(0));
		this.dataWatcher.addObject(21, new Integer(0));
		this.dataWatcher.addObject(22, new Integer(0));
	}

	private void yawToRange(){
		this.rotationYaw = ((this.rotationYaw%360)+360)%360;
	}
	
	private static final float MOTION_SPEED = 0.12f;
	
	@Override
	public void onUpdate() {
		if(!this.worldObj.isRemote)
			this.minerController.run();
		super.onUpdate();
        
        
		float diff = (((getTargetRot()*90 - this.rotationYaw) % 360) + 360) % 360;
		if(diff>180)
			diff = -360.0f+diff;
		if(diff>3)
			diff = 3;
		if(diff<-3)
			diff=-3;
		this.rotationYaw += diff;
		yawToRange();
		this.prevRotationYaw = this.rotationYaw-diff;
		
		
		
		
		this.motionX = getTargetX()-this.posX;
		this.motionY -= 0.03999999910593033D;
		this.motionZ = getTargetZ()-this.posZ;
		double diffXZ = Math.sqrt(this.motionX*this.motionX+this.motionZ*this.motionZ);
		if(diffXZ>MOTION_SPEED){
			this.motionX /= diffXZ;
			this.motionZ /= diffXZ;
			this.motionX *= MOTION_SPEED;
			this.motionZ *= MOTION_SPEED;
		}
		moveEntity(this.motionX, this.motionY, this.motionZ);
	}

	@Override
	public boolean canBeCollidedWith(){
        return !this.isDead;
    }
	
	@Override
	public AxisAlignedBB getCollisionBox(Entity entity){
		if (entity instanceof EntityItem || entity instanceof EntityXPOrb) { 
			return null; 
		}
		return entity.boundingBox;
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(){
        return this.boundingBox;
    }
	
	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float i) {
		// all but void and explosion is ignored.
		if (damagesource != DamageSource.outOfWorld
				&& (this.worldObj.isRemote || this.isDead || (damagesource.getSourceOfDamage() == null && damagesource.isExplosion()))) {
			return true;
		}
		setForwardDirection(-getForwardDirection());
		setTimeSinceHit(10);
		setDamageTaken(getDamageTaken() + i * 7);
		setBeenAttacked();
		if (getDamageTaken() > 40) {

			turnIntoBlocks();
		}
		return true;
	}

	@Override
	public void performHurtAnimation() {
		setForwardDirection(-getForwardDirection());
		setTimeSinceHit(10);
		setDamageTaken(getDamageTaken() * 11);
	}
	
	@Override
	public float getShadowSize() {
		return 1.0F;
	}
	
	public void setDamageTaken(float i) {
		this.dataWatcher.updateObject(19, Float.valueOf(i));
	}

	public float getDamageTaken() {
		return this.dataWatcher.getWatchableObjectFloat(19);
	}

	public void setTimeSinceHit(int i) {
		this.dataWatcher.updateObject(17, Integer.valueOf(i));
	}

	public int getTimeSinceHit() {
		return this.dataWatcher.getWatchableObjectInt(17);
	}

	public void setForwardDirection(int i) {
		this.dataWatcher.updateObject(18, Integer.valueOf(i));
	}

	public int getForwardDirection() {
		return this.dataWatcher.getWatchableObjectInt(18);
	}
	
	public void setTargetRot(int i) {
		this.dataWatcher.updateObject(20, Integer.valueOf(((i%4)+4)%4));
	}

	public int getTargetRot() {
		return this.dataWatcher.getWatchableObjectInt(20);
	}
	
	public void setTargetX(int i) {
		this.dataWatcher.updateObject(21, Integer.valueOf(i));
	}

	public int getTargetX() {
		return this.dataWatcher.getWatchableObjectInt(21);
	}
	
	public void setTargetZ(int i) {
		this.dataWatcher.updateObject(22, Integer.valueOf(i));
	}

	public int getTargetZ() {
		return this.dataWatcher.getWatchableObjectInt(22);
	}
	
	public void turnIntoBlocks() {
		if (this.riddenByEntity != null) {
			this.riddenByEntity.mountEntity(null);
		}
		if(this.worldObj.isRemote)
			return;
		int xh = (int) Math.round(this.posX);
		int y = (int) Math.floor(this.posY + 0.0001F);
		int zh = (int) Math.round(this.posZ);
		yawToRange();
		int yaw = (this.rotationYaw < 45 || this.rotationYaw > 315) ? 0 : (this.rotationYaw < 135 ? 1 : (this.rotationYaw < 215 ? 2 : (this.rotationYaw < 315 ? 3 : 0)));

		int xl = xh - 1, zl = zh - 1;
		
		// building chests
		for (int x = xl; x <= xh; x++) {
			for (int z = zl; z <= zh; z++) {
				PC_Utils.setBlock(this.worldObj, x, y, z, Blocks.iron_block);
				if ((yaw == 0 && z == zh) || (yaw == 1 && x == xl) || (yaw == 2 && z == zl) || (yaw == 3 && x == xh)) {
					PC_Utils.setBlock(this.worldObj, x, y+1, z, Blocks.chest);
				} else {
					PC_Utils.setBlock(this.worldObj, x, y+1, z, Blocks.iron_block);
				}
			}
		}
		
		IInventory inv = null;

		for (int x = xl; x <= xh && inv==null; x++) {
			for (int k = zl; k <= zh && inv==null; k++) {
				inv = PC_InventoryUtils.getBlockInventoryAt(this.worldObj, x, y + 1, k);
			}
		}
		
		if (inv != null) {
			PC_InventoryUtils.moveStacks(this, inv);
		} else {
			PC_Logger.warning("Despawning miner - the chest blocks weren't found.");
		}
		PC_InventoryUtils.dropInventoryContent(this, this.worldObj, this.posX, y+1, this.posZ);
		
		setDead();

	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void doRender(PC_EntityRenderer<?> renderer, double x, double y, double z, float rotYaw, float timeStamp) {
		PC_Model model = PC_Entities.getEntityType(this).getModel();
		model.render(this, 0, 0, 0, 0, 0, 0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public PC_IGresGui openClientGui(EntityPlayer player, NBTTagCompound serverData) {
		HashMap<String, String> sources = new HashMap<String, String>();
		PC_NBTTagHandler.loadMapFromNBT(serverData, "sources", sources, String.class, String.class, Flag.SYNC);
		return new PCtf_GuiMiner(player, this, sources);
	}

	@Override
	public PC_GresBaseWithInventory openServerGui(EntityPlayer player) {
		return new PCtf_ContainerMiner(player, this);
	}

	@Override
	public NBTTagCompound sendOnGuiOpenToClient(EntityPlayer player) {
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		PC_NBTTagHandler.saveMapToNBT(nbtTagCompound, "sources", this.minerController.getSources(), Flag.SYNC);
		return nbtTagCompound;
	}
	
	public void saveAndCompile(HashMap<String, String> sources) {
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		nbtTagCompound.setInteger("type", 0);
		PC_NBTTagHandler.saveMapToNBT(nbtTagCompound, "sources", sources, Flag.SYNC);
		sendMessage(nbtTagCompound);
	}
	
	@Override
	public void onMessage(EntityPlayer player, NBTTagCompound nbtTagCompound) {
		switch(nbtTagCompound.getInteger("type")){
		case 0:
			HashMap<String, String> sources = new HashMap<String, String>();
			PC_NBTTagHandler.loadMapFromNBT(nbtTagCompound, "sources", sources, String.class, String.class, Flag.SYNC);
			this.minerController.setClassesAndCompile(sources);
			break;
		default:
			break;
		}
	}

	@Override
	public int getSizeInventory() {
		return this.inventoryContents.length;
	}
	
	@Override
	public ItemStack getStackInSlot(int i) {
		return this.inventoryContents[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (this.inventoryContents[i] != null) {
			ItemStack itemstack;
			if (this.inventoryContents[i].stackSize <= j) {
				itemstack = this.inventoryContents[i];
				this.inventoryContents[i] = null;
				markDirty();
				return itemstack;
			} 
			itemstack = this.inventoryContents[i].splitStack(j);
			if (this.inventoryContents[i].stackSize == 0) {
				this.inventoryContents[i] = null;
			}
			markDirty();
			return itemstack;
		}
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		if (this.inventoryContents[i] != null) {
			ItemStack itemstack = this.inventoryContents[i];
			this.inventoryContents[i] = null;
			return itemstack;
		} 
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		this.inventoryContents[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}
		markDirty();
	}
	
	@Override
	public String getInventoryName() {
		return "Miner";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public void openInventory() {
		//
	}

	@Override
	public void closeInventory() {
		//
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return PC_InventoryUtils.makeIndexList(0, this.inventoryContents.length);
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int side) {
		return false;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int side) {
		return false;
	}

	@Override
	public int getSlotStackLimit(int i) {
		return getInventoryStackLimit();
	}

	@Override
	public boolean canTakeStack(int i, EntityPlayer entityPlayer) {
		return true;
	}
	
	@Override
	public boolean canDropStack(int i) {
		return true;
	}
	
	@Override
	public void onTick(World world) {
		PC_InventoryUtils.onTick(this, this.worldObj);
	}

	@Override
	public void markDirty() {
		//
	}

	@Override
	public int[] getAppliedGroups(int i) {
		return null;
	}

	@Override
	public int[] getAppliedSides(int i) {
		return null;
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		setTargetRot(tag.getInteger("targetRot"));
		setTargetX(tag.getInteger("targetX"));
		setTargetZ(tag.getInteger("targetZ"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		tag.setInteger("targetRot", getTargetRot());
		tag.setInteger("targetX", getTargetX());
		tag.setInteger("targetZ", getTargetZ());
	}
	

	public void moveForward(int steps) {
		switch(getTargetRot()){
		case 0:
			setTargetZ(getTargetZ()-steps);
			break;
		case 1:
			setTargetX(getTargetX()+steps);
			break;
		case 2:
			setTargetZ(getTargetZ()+steps);
			break;
		case 3:
			setTargetX(getTargetX()-steps);
			break;
		default:
			break;
		}
	}
	
	public void rotate(int dir) {
		setTargetRot(getTargetRot()+dir);
	}
	
	public boolean operationFinished() {
		yawToRange();
		return this.rotationYaw==getTargetRot()*90 && this.posX==getTargetX() && this.posZ==getTargetZ();
	}
	
	/**
     * Sets the entity's position and rotation. Args: posX, posY, posZ, yaw, pitch
     */
    @Override
	public void setPositionAndRotation(double par1, double par3, double par5, float par7, float par8){
    	super.setPositionAndRotation(par1, par3, par5, par7, par8);
    	yawToRange();
    }

    @Override
	public void setLocationAndAngles(double par1, double par3, double par5, float par7, float par8){
    	super.setLocationAndAngles(par1, par3, par5, par7, par8);
    	yawToRange();
    }
    
    @Override
	protected void setRotation(float par1, float par2){
    	super.setRotation(par1, par2);
    	yawToRange();
    }

    @Override
	@SideOnly(Side.CLIENT)
    public void setAngles(float par1, float par2){
    	super.setAngles(par1, par2);
    	yawToRange();
    }
	
	public static void registerRecipe(){
		PC_Recipes.add3DRecipe(new PC_I3DRecipeHandler() {
			@Override
			public boolean foundStructAt(World world, StructStart structStart) {
				clearAndSpawnMiner(world, structStart);
				return true;
			}
		}, new String[]{"II", "CC"}, new String[]{"II", "II"}, Character.valueOf('I'), Blocks.iron_block, Character.valueOf('C'), Blocks.chest);
	}
	
	static void clearAndSpawnMiner(World world, StructStart structStart){
		for(int i=0; i<2; i++){
			for(int j=0; j<2; j++){
				for(int k=0; k<2; k++){
					PC_Utils.setAir(world, structStart.relative(i, j, k));
				}
			}
		}
		if(world.isRemote)
			return;
		PCtf_EntityMiner miner = new PCtf_EntityMiner(world, structStart.pos, structStart.dir);
		PC_Utils.spawnEntity(world, miner);
	}
	
}
