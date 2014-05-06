package powercraft.misc.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import powercraft.api.PC_Direction;
import powercraft.api.PC_IconRegistry;
import powercraft.api.PC_Utils;
import powercraft.api.block.PC_Block;
import powercraft.api.recipes.PC_IRecipe;
import powercraft.api.recipes.PC_Recipes;


public class PCms_BlockClimbingRope extends PC_Block {
	
	public static final IIcon[] icons = new IIcon[6];
	
	public PCms_BlockClimbingRope() {
		super(Material.ground);
		setCreativeTab(CreativeTabs.tabMisc);
		this.minX = this.minZ = 0.5-1/16.0;
		this.maxX = this.maxZ = 0.5+1/16.0;
	}

	@Override
	public IIcon getIcon(PC_Direction side, int metadata) {
		if(side.ordinal()>=icons.length)
			return icons[0];
		return icons[side.ordinal()];
	}

	@Override
	public void registerIcons(PC_IconRegistry iconRegistry) {
		icons[0] = icons[1] = iconRegistry.registerIcon("end");
		icons[PC_Direction.NORTH.ordinal()] = iconRegistry.registerIcon("side1");
		icons[PC_Direction.EAST.ordinal()] = iconRegistry.registerIcon("side2");
		icons[PC_Direction.SOUTH.ordinal()] = iconRegistry.registerIcon("side3");
		icons[PC_Direction.WEST.ordinal()] = iconRegistry.registerIcon("side4");
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
    public boolean isOpaqueCube(){
        return false;
    }
	
	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		Block block = PC_Utils.getBlock(world, x, y+1, z);
		return block==this || PC_Utils.isBlockSideSolid(world, x, y+1, z, PC_Direction.DOWN);
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		if(PC_Utils.getBlock(world, x, y-1, z)==this){
			this.minY = 0;
		}else{
			this.minY = 2/16.0;
		}
	}

	@Override
	public void onBlockPostSet(World world, int x, int y, int z, PC_Direction side, ItemStack stack, EntityPlayer player, float hitX, float hitY, float hitZ, int metadata) {
		if(stack.stackTagCompound!=null && stack.stackTagCompound.hasKey("ropeLength") && !world.isRemote){
			int length = stack.stackTagCompound.getInteger("ropeLength")-1;
			int by = y-1;
			while(length>0){
				Block block = PC_Utils.getBlock(world, x, by, z);
				if(!(block==null || block.isAir(world, x, by, z))){
					break;
				}
				PC_Utils.setBlock(world, x, by, z, this);
				by--;
				length--;
			}
			if(length>0){
				ItemStack is = new ItemStack(this);
				if(length>1){
					is.stackTagCompound = new NBTTagCompound();
					is.stackTagCompound.setInteger("ropeLength", length);
				}
				PC_Utils.spawnItem(world, x, by+1, z, is);
			}
		}
	}

	@Override
	public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity) {
		return true;
	}

	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> list, boolean advancedItemTooltips) {
		int length;
		if(itemStack.stackTagCompound==null || !itemStack.stackTagCompound.hasKey("ropeLength")){
			length = 1;
		}else{
			length = itemStack.stackTagCompound.getInteger("ropeLength");
		}
		list.add("Length: "+length);
	}

	@Override
	public void initRecipes() {
		PC_Recipes.addShapedRecipe(new ItemStack(this), "s", "s", "s", 's', Items.string);
		PC_Recipes.addRecipe(new PC_IRecipe() {
			
			@Override
			public boolean matches(InventoryCrafting ic, World world) {
				return getCraftingResult(ic)!=null;
			}
			
			@Override
			public int getRecipeSize() {
				return 3*3;
			}
			
			@Override
			public ItemStack getRecipeOutput() {
				return new ItemStack(PCms_BlockClimbingRope.this);
			}
			
			@Override
			public ItemStack getCraftingResult(InventoryCrafting ic) {
				Item thisItem = PC_Utils.getItemForBlock(PCms_BlockClimbingRope.this);
				int length = 0;
				for(int i=0; i<3; i++){
					for(int j=0; j<3; j++){
						ItemStack is = ic.getStackInRowAndColumn(i, j);
						if(is!=null){
							if(is.getItem()!=thisItem)
								return null;
							if(is.stackTagCompound==null || !is.stackTagCompound.hasKey("ropeLength")){
								length++;
							}else{
								length += is.stackTagCompound.getInteger("ropeLength");
							}
						}
					}
				}
				if(length==0)
					return null;
				ItemStack is = new ItemStack(PCms_BlockClimbingRope.this);
				if(length>1){
					is.stackTagCompound = new NBTTagCompound();
					is.stackTagCompound.setInteger("ropeLength", length);
				}
				return is;
			}
		});
	}
	
}
