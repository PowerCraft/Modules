package powercraft.itemstorage.block;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercraft.api.block.PC_BlockTileEntity;
import powercraft.api.block.PC_ItemBlock;
import powercraft.api.block.PC_TileEntity;
import powercraft.api.recipes.PC_Recipes;
import powercraft.itemstorage.tileentity.PCis_TileEntityChannelChest;


public class PCis_BlockChannelChest extends PC_BlockTileEntity {
	
	private static PCis_TileEntityChannelChest te = new PCis_TileEntityChannelChest();
	
	public PCis_BlockChannelChest(){
		super(Material.wood);
		setCreativeTab(CreativeTabs.tabDecorations);
        setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
	}
	
	@Override
	public void initRecipes(){
		 PC_Recipes.addShapedRecipe(new ItemStack(this), "WWW", "WPW", "WWW", Character.valueOf('W'), Blocks.planks, Character.valueOf('P'), Items.ender_pearl);
	}
	
	@Override
	public Class<? extends PC_TileEntity> getTileEntityClass() {
		return PCis_TileEntityChannelChest.class;
	}

	@Override
	public PC_TileEntity createNewTileEntity(World world, int metadata) {
		return new PCis_TileEntityChannelChest(world, metadata);
	}

	@Override
	public Class<? extends PC_ItemBlock> getItemBlock() {
		return PCis_ItemBlockChannelChest.class;
	}
	
	@Override
	public boolean isOpaqueCube(){
        return false;
    }
	
	@Override
	public boolean renderAsNormalBlock(){
		return false;
	}
	
	@Override
	public boolean hasComparatorInputOverride(){
        return true;
    }

	@Override
	@SideOnly(Side.CLIENT)
	public void renderInventoryBlock(int metadata, int modelId, RenderBlocks renderer) {
		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		TileEntityRendererDispatcher.instance.renderTileEntityAt(te, 0.0D, 0.0D, 0.0D, 0.0F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	}
	
}
