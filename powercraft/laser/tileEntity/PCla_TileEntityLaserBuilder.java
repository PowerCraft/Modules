package powercraft.laser.tileEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import powercraft.api.PC_3DRotation;
import powercraft.api.PC_3DRotationY;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Field;
import powercraft.api.PC_Field.Flag;
import powercraft.api.PC_Utils;
import powercraft.api.PC_Vec3;
import powercraft.api.PC_Vec3I;
import powercraft.api.beam.PC_LightValue;
import powercraft.api.block.PC_TileEntityWithInventory;
import powercraft.api.building.PC_Build;
import powercraft.api.inventory.PC_InventoryUtils;
import powercraft.laser.PCla_Beam;
import powercraft.laser.PCla_IBeamHandler;
import powercraft.laser.block.PCla_BlockLaserBuilder;

public class PCla_TileEntityLaserBuilder extends PC_TileEntityWithInventory implements PCla_IBeamHandler {
	
	private static class BlockPosAndDist{
		
		double dist;
		PC_Vec3I pos;
		
		public BlockPosAndDist(double dist, PC_Vec3I pos) {
			this.dist = dist;
			this.pos = pos;
		}
		
	}
	
	private static class C implements Comparator<BlockPosAndDist>{

		public C() {
			
		}

		@Override
		public int compare(BlockPosAndDist o1, BlockPosAndDist o2) {
			return o1.dist>o2.dist?-1:o1.dist<o2.dist?1:0;
		}
		
	}
	
	private static final C C = new C();
	
	private static Random rand = new Random();
	
	@PC_Field(flags={Flag.SAVE, Flag.SYNC}, notNull=true)
	protected PC_3DRotation rotation;
	
	private List<BlockPosAndDist> blockList = new ArrayList<BlockPosAndDist>();
	
	public PCla_TileEntityLaserBuilder() {
		super("Builder", 9, new Group(true, PC_InventoryUtils.makeIndexList(0, 9)));
		for(int i=0; i<6; i++){
			setSideGroup(i, 0);
		}
	}
	
	@SuppressWarnings("unused")
	@Override
	public void onTick() {
		super.onTick();
		PC_Direction dir = get3DRotation().getSidePosition(PC_Direction.NORTH);
		PC_Vec3 vec = new PC_Vec3(dir.offsetX, dir.offsetY, dir.offsetZ);
		this.blockList.clear();
		new PCla_Beam(this.worldObj, this, 20, new PC_Vec3(this.xCoord+0.5, this.yCoord+0.5, this.zCoord+0.5), vec, new PC_LightValue(525*PC_LightValue.THz, 1));
	}

	@Override
	public boolean onHitBlock(World world, int x, int y, int z, PCla_Beam beam) {
		Block block = PC_Utils.getBlock(world, x, y, z);
		if(block==null||!block.isNormalCube()){
			this.blockList.add(new BlockPosAndDist(beam.getLength(), new PC_Vec3I(x, y, z)));
			return true;
		}
		return false;
	}

	@Override
	public boolean onHitEntity(World world, Entity entity, PCla_Beam beam) {
		
		return true;
	}

	@Override
	public void onFinished(PCla_Beam beam) {
		BlockPosAndDist[] blocks = this.blockList.toArray(new BlockPosAndDist[this.blockList.size()]);
		Arrays.sort(blocks, C);
		for(BlockPosAndDist blockPos:blocks){
			if(tryBuildHere(blockPos.pos)){
				break;
			}
		}
		this.blockList.clear();
	}
	
	private boolean tryBuildHere(PC_Vec3I pos){
		int i = -1;
		int j = 1;
		for (int k = 0; k < this.inventoryContents.length; k++) {
			if (this.inventoryContents[k] != null && rand.nextInt(j++) == 0) {
				i = k;
			}
		}
		if (i >= 0) {
			ItemStack itemStack = getStackInSlot(i);
			if(itemStack!=null && PC_Build.tryUseItem(this.worldObj, pos.x, pos.y, pos.z, itemStack)){
				if(itemStack.stackSize<=0){
					itemStack = null;
				}
				setInventorySlotContents(i, itemStack);
				return true;
			}
		}
		return false;
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
	public IIcon getIcon(PC_Direction side) {
		if(side==PC_Direction.NORTH){
			return PCla_BlockLaserBuilder.front;
		}
		return PCla_BlockLaserBuilder.side;
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
	
}
