package powercraft.itemstorage.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.World;
import powercraft.api.block.PC_BlockTileEntity;
import powercraft.api.block.PC_ItemBlock;
import powercraft.api.block.PC_TileEntity;
import powercraft.itemstorage.tileentity.PCis_TileEntityChannelChest;


public class PCis_BlockChannelChest extends PC_BlockTileEntity {
	
	public PCis_BlockChannelChest(){
		super(Material.wood);
		setCreativeTab(CreativeTabs.tabDecorations);
        setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
	}
	
	@Override
	public Class<? extends PC_TileEntity> getTileEntityClass() {
		return PCis_TileEntityChannelChest.class;
	}

	@Override
	public PC_TileEntity createNewTileEntity(World world, int metadata) {
		return new PCis_TileEntityChannelChest(metadata);
	}

	@Override
	public Class<? extends PC_ItemBlock> getItemBlock() {
		return PCis_ItemBlockChannelChest.class;
	}
	
}
