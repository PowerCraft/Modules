package powercraft.traffic.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Field;
import powercraft.api.PC_Field.Flag;
import powercraft.api.PC_Logger;
import powercraft.api.PC_MathHelper;
import powercraft.api.PC_NBTTagHandler;
import powercraft.api.PC_Utils;
import powercraft.api.PC_Vec3I;
import powercraft.api.entity.PC_Entities;
import powercraft.api.entity.PC_Entity;
import powercraft.api.gres.PC_Gres;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.PC_IGresGuiOpenHandler;
import powercraft.api.inventory.PC_IInventory;
import powercraft.api.inventory.PC_IWeaselInventory;
import powercraft.api.inventory.PC_InventoryDescription;
import powercraft.api.inventory.PC_InventoryUtils;
import powercraft.api.recipes.PC_3DRecipe.StructStart;
import powercraft.api.recipes.PC_I3DRecipeHandler;
import powercraft.api.recipes.PC_Recipes;
import powercraft.api.renderer.PC_EntityRenderer;
import powercraft.api.renderer.model.PC_Model;
import powercraft.api.script.weasel.events.PC_WeaselEventInventorySlotEmpty;
import powercraft.traffic.PCtf_MinerController;
import powercraft.traffic.container.PCtf_ContainerMiner;
import powercraft.traffic.gui.PCtf_GuiMiner;
import powercraft.traffic.items.PCtf_ItemSawblade;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import static powercraft.traffic.entity.PCtf_EntityMiner.INVENTORIES.*;


public class PCtf_EntityMiner extends PC_Entity implements PC_IGresGuiOpenHandler, PC_IInventory, PC_IWeaselInventory{
	
	public static final int OPERATION_FINISHED = 0;
	public static final int OPERATION_ERRORED = -1;
	public static final int OPERATION_INWORK = 1;
	
	@PC_Field
	protected ItemStack[] inventoryContents = new ItemStack[6*9+1];
	@PC_Field
	protected PCtf_MinerController minerController;
	@PC_Field
	protected boolean miningEnabled=false;
	@PC_Field
	protected int[] minings = {
			-1 , -1,
			-1 , -1,
			-1 , -1,
			-1 , -1,
			-1 , -1,
			-1 , -1
	};
	@PC_Field
	protected int moveAfterMining;
	@PC_Field
	protected boolean operationErrored;
	
	public static class INVENTORIES{
		public static final PC_InventoryDescription GLOBAL = new PC_InventoryDescription(0, 9*6, "global");
		public static final PC_InventoryDescription INVENTORY = new PC_InventoryDescription(0, 9*6-1, "inventory");
		public static final PC_InventoryDescription SAWBLADE = new PC_InventoryDescription(9*6, 9*6, "sawblade");
		
		private static final PC_InventoryDescription array[] = {SAWBLADE, INVENTORY, GLOBAL};
		public static PC_InventoryDescription byName(String name){
			for(PC_InventoryDescription desc:array){
				if(name.equalsIgnoreCase(GLOBAL.inventoryName)) return desc;
			}
			return null;
		}
	}
	
	public PCtf_EntityMiner(World world) {
		super(world);
		setSize(1.3F, 1.4F);
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
	
	@Override
	public void applyEntityCollision(Entity entity) {
		if (entity.riddenByEntity == this || entity.ridingEntity == this) {
			return;
		}

		double d = entity.posX - this.posX;
		double d1 = entity.posZ - this.posZ;
		double d2 = PC_MathHelper.abs_max(d, d1);
		if (d2 >= 0.001D) {
			d2 = PC_MathHelper.sqrt_double(d2);
			d /= d2;
			d1 /= d2;
			double d3 = 1.0D / d2;
			if (d3 > 1.0D) {
				d3 = 1.0D;
			}
			d *= d3;
			d1 *= d3;
			d *= 0.05D;
			d1 *= 0.05D;
			d *= 1.0F - this.entityCollisionReduction;
			d1 *= 1.0F - this.entityCollisionReduction;
			this.isAirBorne = true;

			// this entity won't be moved!

			entity.addVelocity(d, 0.0D, d1);
		}
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
		
		
		pickupItems();
		pushEntities();
		
		moveEntity(this.motionX, this.motionY, this.motionZ);
		
		for(int i=0; i<this.minings.length; i++){
			if(this.minings[i]>=0){
				if(this.minings[i]==0){
					PC_Vec3I pos = getPosFor(miningIndexToOffset(i));
					if(!destroyBlock(pos)){
						this.operationErrored = true;
					}
					this.minings[i] = -1;
				}else{
					if((--this.minings[i])%10==0){
						ItemStack is = getStackInSlot(SAWBLADE.offset(0));
						if(is!=null){
							if(is.attemptDamageItem(1, new Random())){
								setInventorySlotContents(SAWBLADE.offset(0), null);
								if(!worldObj.isRemote)
									minerController.makeInterrupt(new PC_WeaselEventInventorySlotEmpty(minerController.getAddress(), SAWBLADE.inventoryName, 0));
							}
						}
					}
				}
			}
		}
		
		if(!isMining() && this.moveAfterMining!=0){
			if(!this.operationErrored){
				moveForwardWithoutMining(this.moveAfterMining);
			}
			this.moveAfterMining = 0;
		}
		PC_InventoryUtils.onTick(this, this.worldObj);
	}

	private void pickupItems(){
		if(this.worldObj.isRemote)
			return;
			
		@SuppressWarnings("unchecked")
		List<EntityItem> list = this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand(1.5D, 0.5D, 1.5D));
		if (list != null && list.size() > 0) {
			for (int j1 = 0; j1 < list.size(); j1++) {
				EntityItem entity = list.get(j1);
				if (entity.delayBeforeCanPickup >= 6) {
					continue;
				}

				ItemStack itemStack = entity.getEntityItem();
				
				if(PC_InventoryUtils.storeItemStackToInventoryFrom(this, itemStack))
					entity.setDead();
				
			}
		}
	}
	
	private void pushEntities(){
		if(this.worldObj.isRemote)
			return;
		
		@SuppressWarnings("unchecked")
		List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(0.2D, 0.01D, 0.2D));
		if (list != null && list.size() > 0) {
			for (int j1 = 0; j1 < list.size(); j1++) {
				Entity entity = list.get(j1);
				if (PC_Utils.isEntityFX(entity)|| entity instanceof EntityXPOrb) {
					continue;
				}
				if (entity.isDead) {
					continue;
				}

				if (entity instanceof EntityArrow) {
					PC_InventoryUtils.storeItemStackToInventoryFrom(this, new ItemStack(Items.arrow, 1, 0));
					entity.setDead();
					return;
				}

				// keep the same old velocity
				double motionX_prev = this.motionX;
				double motionY_prev = this.motionY;
				double motionZ_prev = this.motionZ;

				entity.applyEntityCollision(this);

				this.motionX = motionX_prev;
				this.motionY = motionY_prev;
				this.motionZ = motionZ_prev;
			}
		}
	}
	
	private boolean destroyBlock(PC_Vec3I pos){
		Block block = PC_Utils.getBlock(this.worldObj, pos);
		if(this.worldObj instanceof WorldServer){
			int metadata = PC_Utils.getMetadata(this.worldObj, pos);
			EntityPlayer player = FakePlayerFactory.getMinecraft((WorldServer)this.worldObj);
			block.harvestBlock(this.worldObj, player, pos.x, pos.y, pos.z, metadata);
			return block.removedByPlayer(this.worldObj, player, pos.x, pos.y, pos.z);
		}
		return true;
	}
	
	private PC_Vec3I getPosFor(PC_Vec3I offset){
		int offX = offset.x, offY = offset.y, offZ=offset.z;
		System.out.println("relativeOffset:"+offX+":"+offY+":"+offZ);
		PC_Direction facing = PC_Direction.directionFacing(this.rotationYaw, 0, null);
		int tmpX=0, tmpZ=0;
		System.out.println("facing:"+facing);
		if(facing.offsetX+facing.offsetZ>=0){
			if(offZ>0) offZ--;
		}else{
			if(offZ<0) offZ++;
		}
		tmpX+=facing.offsetX*offZ;
		tmpZ+=facing.offsetZ*offZ;
		
		PC_Direction side = facing.rotateOnce(PC_Direction.UP);
		System.out.println("side:"+side);
		if(side.offsetX+side.offsetZ>=0){
			if(offX>0) offX--;
		}else{
			if(offX<0) offX++;
		}
		tmpX+=side.offsetX*offX;
		tmpZ+=side.offsetZ*offX;
		System.out.println("rotatedOffset:"+tmpX+":"+(offY<0?offY+1:offY)+":"+tmpZ);
		System.out.println("absolutePosition:"+(int)(Math.floor(this.posX+tmpX)) +":"+ (int)(Math.floor(this.posY+(offY<0?offY+1:offY))) +":"+ (int)(Math.floor(this.posZ+tmpZ)));
		return new PC_Vec3I((int)(Math.floor(this.posX+tmpX)), (int)(Math.floor(this.posY+(offY<0?offY+1:offY))), (int)(Math.floor(this.posZ+tmpZ)));
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
	public boolean canBePushed() {
		return true;
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
		NBTTagCompound diagnostics = serverData.getCompoundTag("diagnostics");
		return new PCtf_GuiMiner(player, this, sources, diagnostics);
	}

	@Override
	public PC_GresBaseWithInventory openServerGui(EntityPlayer player) {
		return new PCtf_ContainerMiner(player, this);
	}

	@Override
	public NBTTagCompound sendOnGuiOpenToClient(EntityPlayer player) {
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		PC_NBTTagHandler.saveMapToNBT(nbtTagCompound, "sources", this.minerController.getSources(), Flag.SYNC);
		nbtTagCompound.setTag("diagnostics", this.minerController.getDiagnostics());
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
		case 1:
		default:
			break;
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void onClientMessage(EntityPlayer player, NBTTagCompound nbtTagCompound) {
		switch(nbtTagCompound.getInteger("type")){
		case 1:
			PCtf_GuiMiner gui = PC_Gres.getCurrentClientGui(PCtf_GuiMiner.class);
			if(gui!=null){
				gui.setErrors(this, nbtTagCompound.getCompoundTag("diagnostics"));
			}
			break;
		default:
			super.onClientMessage(player, nbtTagCompound);
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
		return i==9*6?itemstack.getItem() instanceof PCtf_ItemSawblade:true;
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
    
    public boolean tryToPlace(ItemStack is, PC_Vec3I pos) {
		Block block = PC_Utils.getBlock(this.worldObj, pos);
		if((block==null || block.isReplaceable(this.worldObj, pos.x, pos.y, pos.z)) && is!=null && is.stackSize>0 && this.worldObj instanceof WorldServer){
			return is.tryPlaceItemIntoWorld(FakePlayerFactory.getMinecraft((WorldServer)this.worldObj), this.worldObj, pos.x, pos.y, pos.z, 1, 0.5f, 1f, 0.5f);
		}
		return false;
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
	
	public int getTimeForBlockHarvest(PC_Vec3I pos){
		Block block = PC_Utils.getBlock(this.worldObj, pos);
		if(block.isAir(this.worldObj, pos.x, pos.y, pos.z))
			return -1;
		int metadata = PC_Utils.getMetadata(this.worldObj, pos);
		ItemStack is = getStackInSlot(SAWBLADE.offset(0));
		/*Material material = block.getMaterial();
		if(!material.isToolNotRequired()){
			int level = block.getHarvestLevel(metadata);
			ItemStack is = inventoryContents[6*9];
			if(is==null)
				return 0;
			
		}*/
		float hardness = block.getBlockHardness(this.worldObj, pos.x, pos.y, pos.z);
		if(hardness<0)
			return -1;
		hardness *= 10;
		if(is!=null){
			PCtf_ItemSawblade sawblade = (PCtf_ItemSawblade)is.getItem();
			hardness /= sawblade.getSpeed();
		}
		return (int) hardness;
	}
	
	public void moveForwardWithoutMining(int steps) {
		this.operationErrored = false;
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
	
	public boolean isMining(){
		for(int i=0; i<this.minings.length; i++){
			if(this.minings[i]>=0)
				return true;
		}
		return false;
	}
	
	public boolean isRotating(){
		yawToRange();
		return this.rotationYaw!=getTargetRot()*90;
	}
	
	public boolean isMoving(){
		return this.posX!=getTargetX() || this.posZ!=getTargetZ();
	}

	public void moveForward(int steps) {
		if(this.miningEnabled){
			digForward();
			this.moveAfterMining = steps;
			return;
		}
		moveForwardWithoutMining(steps);
	}
	
	public void rotate(int dir) {
		this.operationErrored = false;
		setTargetRot(getTargetRot()+dir);
	}
	
	public int operationFinished() {
		return isMining() || isRotating() || isMoving()?OPERATION_INWORK:this.operationErrored?OPERATION_ERRORED:OPERATION_FINISHED;
	}
	
	
    
    public boolean isMiningEnabled() {
		return this.miningEnabled;
	}

	public boolean setMining(boolean state) {
		this.miningEnabled = state;
		return true;
	}
	
	public void digPos(PC_Vec3I offset){
		PC_Vec3I realPos = getPosFor(offset);
		this.minings[offsetToMiningIndex(offset)] = getTimeForBlockHarvest(realPos);
	}
	
	public void digForward(){
		this.operationErrored = false;
		PC_Vec3I pos= new PC_Vec3I(1, 1, 2);
		
		for(int i=0; i<4; i++){
			digPos(pos);
			pos=pos.rotate(PC_Direction.SOUTH, 1);
		}
	}
	
	public void digUpward() {
		this.operationErrored = false;
		digForward();
		PC_Vec3I offset = new PC_Vec3I(1, 2, 2);
		digPos(offset);
		offset = offset.mirror(PC_Direction.EAST);
		digPos(offset);
		offset.z--;
		digPos(offset);
		offset = offset.mirror(PC_Direction.EAST);
		digPos(offset);
	}

	public void digDownward() {
		this.operationErrored = false;
		digForward();
		PC_Vec3I offset = new PC_Vec3I(1, -2, 2);
		digPos(offset);
		offset = offset.mirror(PC_Direction.EAST);
		digPos(offset);
	}
	
	public int offsetToMiningIndex(PC_Vec3I offset){
		int x=offset.x, y=offset.y, z=offset.z;
		if(x<-1 || x>1 || y>2 || y<-2 || z>2 || z<1 || (Math.abs(y)==1 && Math.abs(x)==z)) return -1;
		if(x==-1) x=0;
		if(z==1) return (y>0?0*2:5*2)+x;
		if(y<0) y+=1;
		return 6-(y*2)+x;
	}
	
	public PC_Vec3I miningIndexToOffset(int i){
		int tmpY=0;
		tmpY=(i>=4 && i<=7)?1:2;
		if(i>=6) tmpY*=-1;
		return new PC_Vec3I((i%2==0)?-1:1, tmpY, (i<2 || i>9)?1:2);
	}
	
	public void placeBlock(String inventory, int invPlace, int x, int y, int z) {
		PC_InventoryDescription inv = INVENTORIES.byName(inventory);
		PC_Vec3I pos = getPosFor(new PC_Vec3I(x, y, z));
		ItemStack is = this.getStackInSlot(inv.offset(invPlace));
		this.operationErrored = !tryToPlace(is, pos);
		if(is.stackSize==0){
			this.setInventorySlotContents(inv.offset(invPlace), null);
			if(!worldObj.isRemote)
				minerController.makeInterrupt(new PC_WeaselEventInventorySlotEmpty(minerController.getAddress(), inv.inventoryName, invPlace));
		}
	}

	@Override
	public boolean canBeDragged(int i) {
		return true;
	}

	@Override
	public PC_InventoryDescription getInventory(String name) {
		return INVENTORIES.byName(name);
	}
	
	
}
