package powercraft.transport.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import powercraft.api.PC_3DRotation;
import powercraft.api.PC_Direction;
import powercraft.api.PC_IconRegistry;
import powercraft.api.PC_Utils;
import powercraft.api.block.PC_Block;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.recipes.PC_Recipes;
import powercraft.transport.PCtr_BeltHelper;

public class PCtr_BlockBeltNormal extends PC_Block {

	protected IIcon[] icons = new IIcon[3];
	
	public PCtr_BlockBeltNormal() {
		super(Material.circuits);
		setCreativeTab(CreativeTabs.tabTransport);
		this.maxY = 1.0f/16.0f;
		PC_Recipes.addShapedRecipe(new ItemStack(this, 16, 0), "LLL", "IRI", 'I', Items.iron_ingot, 'L', Items.leather, 'R', Items.redstone);
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
	public void registerIcons(PC_IconRegistry iconRegistry) {
		this.icons[0] = iconRegistry.registerIcon("top");
		this.icons[1] = iconRegistry.registerIcon("down");
		this.icons[2] = iconRegistry.registerIcon("side");
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
		if(!(Math.floor(entity.posX)==x && Math.floor(entity.posY)==y && Math.floor(entity.posZ)==z)){
			return;
		}
		int diff = PCtr_BeltHelper.combineEntityItems(entity)?2:1;
		if(world.isRemote){
			PCtr_BeltHelper.handleEntity(entity, world, x, y, z, false, true);
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
				if(!PCtr_BeltHelper.handleEntity(entity, world, x, y, z, false, true)){
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
		PCtr_BeltHelper.handleEntity(entity, world, x, y, z, false, true);
		if(prevDir!=direction.ordinal()){
			PC_PacketHandler.sendToAllAround(new PCtr_PacketSetEntitySpeed(compound, entity), world, x, y, z, 16);
		}
	}
	
}
