package powercraft.redstone.multiblock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Field;
import powercraft.api.PC_Field.Flag;
import powercraft.api.PC_Utils;
import powercraft.api.grid.PC_GridHelper;
import powercraft.api.multiblock.PC_MultiblockIndex;
import powercraft.api.multiblock.PC_MultiblockObject;
import powercraft.api.multiblock.PC_TileEntityMultiblock;
import powercraft.api.multiblock.cable.PC_MultiblockObjectCable;
import powercraft.api.redstone.PC_IRedstoneGridTile;
import powercraft.api.redstone.PC_RedstoneGrid;
import powercraft.core.PCco_Core;


public class PCrs_MultiblockObjectRedstoneBundleCable extends PC_MultiblockObjectCable{
	
	private static class BundleWire implements PC_IRedstoneGridTile{

		private PCrs_MultiblockObjectRedstoneBundleCable morbc;
		
		private PC_RedstoneGrid grid;
		
		BundleWire(PCrs_MultiblockObjectRedstoneBundleCable morbc){
			this.morbc = morbc;
		}
		
		@Override
		public void setGrid(PC_RedstoneGrid grid) {
			this.grid = grid;
		}

		@Override
		public PC_RedstoneGrid getGrid() {
			return this.grid;
		}

		@Override
		public boolean isIO() {
			return this.morbc.isIO();
		}

		@Override
		public void onRedstonePowerChange() {
			this.morbc.onRedstonePowerChange();
		}

		@Override
		public int getPower() {
			return this.morbc.getPower();
		}

		void update() {
			//this.grid.update();
		}

		int getRedstonePowerValue() {
			return 0;//this.grid.getRedstonePowerValue();
		}

		void getGridIfNull(World world, int x, int y, int z) {
			
		}
		
		void removeFromGrid(World world) {
			PC_GridHelper.removeFromGrid(world, (PC_IRedstoneGridTile)this);
		}
		
	}
	
	@PC_Field(flags={Flag.SAVE, Flag.SYNC})
	private int mask;
	
	private BundleWire[] bundleWires;
	
	public PCrs_MultiblockObjectRedstoneBundleCable(int i) {
		super(2, 4);
		this.mask |= 1<<i;
		this.bundleWires = new BundleWire[16];
		this.bundleWires[i] = new BundleWire(this);
	}

	public PCrs_MultiblockObjectRedstoneBundleCable(NBTTagCompound tagCompound, Flag flag) {
		super(tagCompound, flag);
	}
	
	@Override
	public boolean canMixWith(PC_MultiblockObject multiblockObject) {
		if(multiblockObject instanceof PCrs_MultiblockObjectRedstoneBundleCable){
			PCrs_MultiblockObjectRedstoneBundleCable bundleCable = (PCrs_MultiblockObjectRedstoneBundleCable)multiblockObject;
			return (bundleCable.getMask() & getMask())==0;
		}
		return super.canMixWith(multiblockObject);
	}

	@Override
	public PC_MultiblockObject mixWith(PC_MultiblockObject multiblockObject) {
		if(multiblockObject instanceof PCrs_MultiblockObjectRedstoneBundleCable){
			PCrs_MultiblockObjectRedstoneBundleCable bundleCable = (PCrs_MultiblockObjectRedstoneBundleCable)multiblockObject;
			this.mask |= bundleCable.getMask();
			if(!isClient()){
				for(int i=0; i<16; i++){
					if((this.mask & 1<<i)!=0 && this.bundleWires[i]==null){
						this.bundleWires[i] = new BundleWire(this);
					}
				}
				onInternalChange();
			}
			return this;
		}
		return super.mixWith(multiblockObject);
	}

	@Override
	protected void onLoadedFromNBT(Flag flag) {
		if(flag==Flag.SAVE){
			this.bundleWires = new BundleWire[16];
			for(int i=0; i<16; i++){
				if((this.mask & 1<<i)!=0){
					this.bundleWires[i] = new BundleWire(this);
				}
			}
		}
		super.onLoadedFromNBT(flag);
	}

	@Override
	public void getGridIfNull() {
		World world = getWorld();
		int x = this.multiblock.xCoord;
		int y = this.multiblock.yCoord;
		int z = this.multiblock.zCoord;
		if(!world.isRemote){
			for(BundleWire bundleWire:this.bundleWires){
				if(bundleWire!=null){
					bundleWire.getGridIfNull(world, x, y, z);
				}
			}
		}
	}
	
	@Override
	public void removeFromGrid() {
		World world = getWorld();
		for(BundleWire bundleWire:this.bundleWires){
			if(bundleWire!=null){
				bundleWire.removeFromGrid(world);
			}
		}
	}
	
	@Override
	protected IIcon getCableIcon() {
		return PCrs_MultiblockItemRedstoneBundleCable.icons[0];
	}
	
	@Override
	protected IIcon getCableCornerIcon() {
		return PCrs_MultiblockItemRedstoneBundleCable.icons[0];
	}
	
	@Override
	protected IIcon getCableSideIcon() {
		return PCrs_MultiblockItemRedstoneBundleCable.icons[0];
	}
	
	@SuppressWarnings("hiding")
	@Override
	protected IIcon getCableLineIcon(int index) {
		return PCrs_MultiblockItemRedstoneBundleCable.icons[index+1];
	}
	
	@Override
	protected boolean useOverlay() {
		return true;
	}
	
	@Override
	protected int getColorForCable(int cableID) {
		return PC_Utils.getColorFor(cableID);
	}
	
	@Override
	protected int getMask() {
		return this.mask;
	}

	@Override
	public boolean canConnectRedstone(PC_Direction side) {
		return canBeIO() && this.index==PC_MultiblockIndex.FACEBOTTOM;
	}

	@Override
	protected int canConnectToBlock(World world, int x, int y, int z, Block block, PC_Direction dir, PC_Direction dir2) {
		if(block instanceof BlockRedstoneWire){
			return canBeIO() && this.index==PC_MultiblockIndex.FACEBOTTOM?0xFFFF:0;
		}
		return super.canConnectToBlock(world, x, y, z, block, dir, dir2);
	}
	
	@Override
	protected int canConnectToMultiblock(PC_MultiblockObject multiblock, PC_Direction dir, PC_Direction dir2) {
		if(multiblock instanceof PCrs_MultiblockObjectRedstoneCable){
			if (!canBeIO()) return 0;
			if(dir.offsetY!=0){
				return 0xFFFF|1<<16;
			}else if(dir.offsetX!=0 && dir2.offsetY==0){
				return 0xFFFF|1<<16;
			}
			return 0xFFFF;
		}
		return super.canConnectToMultiblock(multiblock, dir, dir2);
	}

	@Override
	public void onNeighborBlockChange(Block neighbor) {
		super.onNeighborBlockChange(neighbor);
		BundleWire bundleWire = getSoloBundleWire();
		if(bundleWire!=null)
			bundleWire.update();
	}

	@Override
	public void onInternalChange() {
		super.onInternalChange();
		BundleWire bundleWire = getSoloBundleWire();
		if(bundleWire!=null)
			bundleWire.update();
	}
	
	@Override
	public int getRedstonePowerValue(PC_Direction side) {
		getGridIfNull();
		BundleWire bundleWire = getSoloBundleWire();
		if(bundleWire!=null)
			return bundleWire.getRedstonePowerValue();
		return 0;
	}

	@Override
	public boolean canProvideStrongPower(PC_Direction side) {
		return false;
	}
	
	public BundleWire getSoloBundleWire(){
		BundleWire bundleWire = null;
		for(int i=0; i<16; i++){
			if(this.bundleWires[i]!=null){
				if(bundleWire==null){
					bundleWire=this.bundleWires[i];
				}else{
					return null;
				}
			}
		}
		return bundleWire;
	}
	
	public boolean canBeIO(){
		return PC_Utils.countBits(this.mask)==1;
	}
	
	public boolean isIO(){
		return this.isIO;
	}
	
	public void onRedstonePowerChange() {
		if(canBeIO()){
			World world = getWorld();
			int x = this.multiblock.xCoord;
			int y = this.multiblock.yCoord;
			int z = this.multiblock.zCoord;
			PC_Utils.notifyBlockChange(world, x, y, z, PCco_Core.MULTIBLOCK);
		}
	}
	
	public int getPower() {
		if(canBeIO()){
			int max = 0;
			PC_TileEntityMultiblock tem = getTileEntity();
			if(this.index==PC_MultiblockIndex.FACEBOTTOM){
				for(PC_Direction dir:PC_Direction.VALID_DIRECTIONS){
					Block block = PC_Utils.getBlock(getWorld(), tem.xCoord-dir.offsetX, tem.yCoord-dir.offsetY, tem.zCoord-dir.offsetZ);
					if(block!=null && block!=PCco_Core.MULTIBLOCK){
						int power;
						if(block instanceof BlockRedstoneWire){
							power = PC_Utils.getMetadata(getWorld(), tem.xCoord-dir.offsetX, tem.yCoord-dir.offsetY, tem.zCoord-dir.offsetZ);
							if(power>0){
								power--;
							}
						}else{
							power = getWorld().getIndirectPowerLevelTo(tem.xCoord-dir.offsetX, tem.yCoord-dir.offsetY, tem.zCoord-dir.offsetZ, dir.ordinal());
						}
						if(power==15)
							return 15;
						if(power>max){
							max=power;
						}
					}
				}
			}
			return max;
		}
		return 0;
	}
	
}
