package powercraft.transport.block;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import powercraft.api.PC_3DRotation;
import powercraft.api.PC_3DRotationY;
import powercraft.api.PC_Direction;
import powercraft.api.PC_IconRegistry;
import powercraft.api.PC_Utils;
import powercraft.api.block.PC_Block;
import powercraft.api.inventory.PC_InventoryUtils;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.recipes.PC_Recipes;
import powercraft.api.renderer.PC_Renderer;
import powercraft.transport.PCtr_BeltHelper;

public class PCtr_BlockBeltNormal extends PC_Block {

	protected IIcon[] icons = new IIcon[4];
	
	public PCtr_BlockBeltNormal() {
		super(Material.circuits);
		setCreativeTab(CreativeTabs.tabTransport);
		this.maxY = 1.0f/16.0f;
		PC_Recipes.addShapedRecipe(new ItemStack(this, 16, 0), "LLL", "IRI", Character.valueOf('I'), Items.iron_ingot, Character.valueOf('L'), Items.leather, Character.valueOf('R'), Items.redstone);
	}
	
	@Override
	public boolean renderAsNormalBlock(){
		return false;
	}
	
	@Override
    public boolean isOpaqueCube(){
        return false;
    }

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		return PCtr_BeltHelper.hasValidGround(world, x, y, z);
	}
	
	@Override
	public void registerIcons(PC_IconRegistry iconRegistry) {
		this.icons[0] = iconRegistry.registerIcon("top");
		this.icons[1] = iconRegistry.registerIcon("down");
		this.icons[2] = iconRegistry.registerIcon("side");
		this.icons[3] = iconRegistry.registerIcon("top2");
	}

	@Override
	public IIcon getIcon(PC_Direction side, int metadata) {
		if(side==PC_Direction.UP){
			return this.icons[0];
		}else if(side==PC_Direction.DOWN){
			return this.icons[1];
		}
		return this.icons[2];
	}

	@Override
	public boolean canRotate() {
		return true;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		if(PCtr_BeltHelper.isEntityIgnored(entity))
			return;
		if(!(Math.floor(entity.posX)==x && (Math.floor(entity.posY)==y) && Math.floor(entity.posZ)==z)){
			return;
		}
		int diff = PCtr_BeltHelper.combineEntityItems(entity)?2:1;
		boolean upwards = (PC_Utils.getMetadata(world, x, y, z)&3)==1;
		if(world.isRemote){
			PCtr_BeltHelper.handleEntity(entity, world, x, y, z, false, true, upwards);
			return;
		}
		NBTTagCompound compound = PC_Utils.getWritableNBTTagOf(entity);
		int prevDir = -1;
		if(compound.hasKey("dir")){
			int xx = compound.getInteger("lastX");
			int yy = compound.getInteger("lastY");
			int zz = compound.getInteger("lastZ");
			int lastTick = compound.getInteger("lastTick");
			prevDir = compound.getInteger("dir");
			if(lastTick==entity.ticksExisted)
				return;
			if(x==xx && y==yy && z==zz && (lastTick==entity.ticksExisted-diff || lastTick==entity.ticksExisted-1 || entity.ticksExisted==0)){
				if(!PCtr_BeltHelper.handleEntity(entity, world, x, y, z, false, true, upwards)){
					compound.setInteger("lastTick", entity.ticksExisted);
					return;
				}
			}
		}
		compound.setInteger("lastX", x);
		compound.setInteger("lastY", y);
		compound.setInteger("lastZ", z);
		compound.setInteger("lastTick", entity.ticksExisted);
		
		PC_3DRotation rotation = getRotation(world, x, y, z);
		PC_Direction direction = rotation.getSidePosition(PC_Direction.SOUTH);
		compound.setInteger("dir", direction.ordinal());
		PCtr_BeltHelper.handleEntity(entity, world, x, y, z, false, true, upwards);
		if(prevDir!=direction.ordinal()){
			PC_PacketHandler.sendToAllAround(new PCtr_PacketSetEntitySpeed(compound, entity), world, x, y, z, 16);
		}
	}
	
	private static boolean isValid(World world, int x, int y, int z){
		return PCtr_BeltHelper.isConveyorAt(world, x, y, z)||PC_InventoryUtils.getBlockInventoryAt(world, x, y, z)!=null;
	}
	
	private static int calcHill(World world, int x, int y, int z, int metadata){
		PC_3DRotation rot = new PC_3DRotationY((metadata>>>2)&3);
		int hill = metadata&3;
		if(hill==0){
			PC_Direction dir = rot.getSidePosition(PC_Direction.SOUTH);
			boolean b1 = isValid(world, x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ);
			if(!b1 && isValid(world, x+dir.offsetX, y+dir.offsetY+1, z+dir.offsetZ)){
				return 1;
			}
			dir = rot.getSidePosition(PC_Direction.NORTH);
			boolean b2 = isValid(world, x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ);
			if(!b2 && isValid(world, x+dir.offsetX, y+dir.offsetY+1, z+dir.offsetZ)){
				return 2;
			}
			if(hill==1 && b1){
				return 0;
			}
			if(hill==2 && b2){
				return 0;
			}
			return hill;
		}else if(hill==1){
			PC_Direction dir = rot.getSidePosition(PC_Direction.SOUTH);
			boolean b1 = isValid(world, x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ);
			if(isValid(world, x+dir.offsetX, y+dir.offsetY+1, z+dir.offsetZ)){
				return 1;
			}
			dir = rot.getSidePosition(PC_Direction.NORTH);
			boolean b2 = isValid(world, x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ);
			if(!b2 && isValid(world, x+dir.offsetX, y+dir.offsetY+1, z+dir.offsetZ)){
				return 2;
			}
			if(hill==1 && b1){
				return 0;
			}
			if(hill==2 && b2){
				return 0;
			}
			return hill;
		}else if(hill==2){
			PC_Direction dir = rot.getSidePosition(PC_Direction.NORTH);
			boolean b2 = isValid(world, x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ);
			if(isValid(world, x+dir.offsetX, y+dir.offsetY+1, z+dir.offsetZ)){
				return 2;
			}
			dir = rot.getSidePosition(PC_Direction.SOUTH);
			boolean b1 = isValid(world, x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ);
			if(!b1 && isValid(world, x+dir.offsetX, y+dir.offsetY+1, z+dir.offsetZ)){
				return 1;
			}
			if(hill==1 && b1){
				return 0;
			}
			if(hill==2 && b2){
				return 0;
			}
			return hill;
		}
		return 0;
	}

	private void setupHill(World world, int x, int y, int z, int hill, int md){
		if(hill==0){
			Block b = PC_Utils.getBlock(world, x, y+1, z);
			if(b==this){
				PC_Utils.setAir(world, x, y+1, z);
			}
		}else if(hill!=3){
			Block b = PC_Utils.getBlock(world, x, y+1, z);
			if(b.isAir(world, x, y, z)){
				PC_Utils.setBlock(world, x, y+1, z, this, (md&~3)|3);
			}
		}
	}
	
	@Override
	public int modifiyMetadataPreSet(World world, int x, int y, int z, PC_Direction side, ItemStack stack, EntityPlayer player, float hitX, float hitY, float hitZ, int metadata) {
		int md = super.modifiyMetadataPreSet(world, x, y, z, side, stack, player, hitX, hitY, hitZ, 0);
		int hill = calcHill(world, x, y, z, md);
		setupHill(world, x, y, z, hill, md);
		return md|hill;
	}
	
	@Override
	public AxisAlignedBB getSelectedBoundingBox(World world, int x, int y, int z) {
		int metadata = PC_Utils.getMetadata(world, x, y, z);
		int hill = metadata&3;
		if(hill==3){
			return AxisAlignedBB.getAABBPool().getAABB(0, 0, 0, 0, 0, 0);
		}else if(hill!=0){
			return AxisAlignedBB.getAABBPool().getAABB(0, 0, 0, 1, 1, 1);
		}
		return super.getSelectedBoundingBox(world, x, y, z);
	}
	
	@Override
	public List<AxisAlignedBB> getCollisionBoundingBoxes(World world, int x, int y, int z, Entity entity) {
		int metadata = PC_Utils.getMetadata(world, x, y, z);
		int hill = metadata&3;
		if(hill==3){
			return new ArrayList<AxisAlignedBB>();
		}else if(hill!=0){
			List<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
			if(hill==1){
				for(int i=0; i<16; i++){
					list.add(AxisAlignedBB.getAABBPool().getAABB(0, 0, i/16.0, 1, (i+2)/16.0, (i+1)/16.0));
				}
			}else{  
				for(int i=0; i<16; i++){
					list.add(AxisAlignedBB.getAABBPool().getAABB(0, 0, i/16.0, 1, (17-i)/16.0, (i+1)/16.0));
				}
			}
			return list;
		}
		return super.getCollisionBoundingBoxes(world, x, y, z, entity);
	}
	
	@Override
	public AxisAlignedBB getMainCollisionBoundingBoxPre(World world, int x, int y, int z) {
		int metadata = PC_Utils.getMetadata(world, x, y, z);
		int hill = metadata&3;
		if(hill==3){
			return null;
		}
		return super.getMainCollisionBoundingBoxPre(world, x, y, z);
	}

	@Override
	public void onBlockPostSet(World world, int x, int y, int z, PC_Direction side, ItemStack stack, EntityPlayer player, float hitX, float hitY, float hitZ, int metadata) {
		PC_3DRotation rot = getRotation(world, x, y, z);
		PC_Direction dir = rot.getSidePosition(PC_Direction.NORTH);
		PC_Utils.notifyBlockOfNeighborChange(world, x+dir.offsetX, y+dir.offsetY-1, z+dir.offsetZ, this);
		dir = rot.getSidePosition(PC_Direction.SOUTH);
		PC_Utils.notifyBlockOfNeighborChange(world, x+dir.offsetX, y+dir.offsetY-1, z+dir.offsetZ, this);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		int metadata = PC_Utils.getMetadata(world, x, y, z);
		if((metadata&3)==3){
			if(this!=PC_Utils.getBlock(world, x, y-1, z)){
				PC_Utils.setAir(world, x, y, z);
			}else{
				int md = PC_Utils.getMetadata(world, x, y-1, z);
				int hill = md&3;
				if(hill!=1 && hill!=2){
					PC_Utils.setAir(world, x, y, z);
				}
			}
		}else{
			int hill = calcHill(world, x, y, z, metadata);
			setupHill(world, x, y, z, hill, metadata);
			if(hill!=(metadata&3)){
				PC_Utils.setMetadata(world, x, y, z, (metadata&~3)|hill);
			}
			super.onNeighborBlockChange(world, x, y, z, block);
		}
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int metadata) {
		int hill = metadata&3;
		if(hill!=3){
			Block b = PC_Utils.getBlock(world, x, y+1, z);
			if(b==this){
				PC_Utils.setAir(world, x, y+1, z);
			}
		}
		super.breakBlock(world, x, y, z, block, metadata);
	}

	@SuppressWarnings("hiding")
	@Override
	@SideOnly(Side.CLIENT)
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, int modelId, RenderBlocks renderer) {
		int metadata = PC_Utils.getMetadata(world, x, y, z);
		int hill = metadata&3;
		if(hill==0){
			super.renderWorldBlock(world, x, y, z, modelId, renderer);
		}else if(hill!=3){
			IIcon[] icons = new IIcon[6];
			for(int i=0; i<6; i++){
				icons[i] = getIcon(i, metadata);
			}
			icons[0] = null;
			PC_3DRotation rot = getRotation(world, x, y, z);
			PC_Direction dir = rot.getSidePosition(PC_Direction.NORTH);
			if(hill==1){
				icons[dir.ordinal()] = this.icons[3];
				icons[dir.getOpposite().ordinal()] = null;
				for(int i=0; i<15; i++){
					AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(0, (i+1)/16.0, i/16.0, 1, (i+2)/16.0, (i+1)/16.0);
					aabb = rot.rotateBox(aabb);
					renderer.setRenderBounds(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
					PC_Renderer.renderStandardBlockInWorld(world, x, y, z, icons, -1, 0, renderer);
				}
				icons[dir.getOpposite().ordinal()] = this.icons[2];
				AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(0, 0/16.0, 15.0/16.0, 1, 1/16.0, 1);
				aabb = rot.rotateBox(aabb);
				renderer.setRenderBounds(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
				PC_Renderer.renderStandardBlockInWorld(world, x, y+1, z, icons, -1, 0, renderer);
				icons[dir.ordinal()] = null;
				icons[dir.getOpposite().ordinal()] = this.icons[1];
				icons[1] = null;
				icons[0] = this.icons[1];
				for(int i=1; i<16; i++){
					aabb = AxisAlignedBB.getBoundingBox(0, i/16.0, i/16.0, 1, (i+1)/16.0, (i+1)/16.0);
					aabb = rot.rotateBox(aabb);
					renderer.setRenderBounds(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
					PC_Renderer.renderStandardBlockInWorld(world, x, y, z, icons, -1, 0, renderer);
				}
				icons[dir.ordinal()] = this.icons[2];
				aabb = AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 1/16.0, 1/16.0);
				aabb = rot.rotateBox(aabb);
				renderer.setRenderBounds(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
				PC_Renderer.renderStandardBlockInWorld(world, x, y, z, icons, -1, 0, renderer);
			}else{
				icons[dir.getOpposite().ordinal()] = this.icons[3];
				icons[dir.ordinal()] = null;
				switch(dir){
				case EAST:
					renderer.uvRotateNorth = 3;
					break;
				case NORTH:
					renderer.uvRotateWest = 3;
					break;
				case SOUTH:
					renderer.uvRotateEast = 3;
					break;
				case WEST:
					renderer.uvRotateSouth = 3;
					break;
				default:
					break;
				}
				for(int i=1; i<16; i++){
					AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(0, (16-i)/16.0, i/16.0, 1, (17-i)/16.0, (i+1)/16.0);
					aabb = rot.rotateBox(aabb);
					renderer.setRenderBounds(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
					PC_Renderer.renderStandardBlockInWorld(world, x, y, z, icons, -1, 0, renderer);
				}
				icons[dir.ordinal()] = this.icons[2];
				AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 1/16.0, 1/16.0);
				aabb = rot.rotateBox(aabb);
				renderer.setRenderBounds(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
				PC_Renderer.renderStandardBlockInWorld(world, x, y+1, z, icons, -1, 0, renderer);
				icons[dir.getOpposite().ordinal()] = null;
				icons[dir.ordinal()] = this.icons[1];
				icons[1] = null;
				icons[0] = this.icons[1];
				for(int i=0; i<15; i++){
					aabb = AxisAlignedBB.getBoundingBox(0, (15-i)/16.0, i/16.0, 1, (16-i)/16.0, (i+1)/16.0);
					aabb = rot.rotateBox(aabb);
					renderer.setRenderBounds(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
					PC_Renderer.renderStandardBlockInWorld(world, x, y, z, icons, -1, 0, renderer);
				}
				icons[dir.getOpposite().ordinal()] = this.icons[2];
				aabb = AxisAlignedBB.getBoundingBox(0, 0, 15/16.0, 1, 1/16.0, 1);
				aabb = rot.rotateBox(aabb);
				renderer.setRenderBounds(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
				PC_Renderer.renderStandardBlockInWorld(world, x, y, z, icons, -1, 0, renderer);

			}
		}
		return true;
	}

	@Override
	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 vec1, Vec3 vec2) {
		int metadata = PC_Utils.getMetadata(world, x, y, z);
		int hill = metadata&3;
		if(hill==3)
			return null;
		if(hill!=0){
			this.maxY = 1;
		}
		MovingObjectPosition mop =  super.collisionRayTrace(world, x, y, z, vec1, vec2);
		this.maxY = 1/16.0f;
		return mop;
	}

	@Override
	public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
		int metadata = PC_Utils.getMetadata(world, x, y, z);
		int hill = metadata&3;
		return hill==3;
	}
	
}
