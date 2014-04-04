package powercraft.itemstorage.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercraft.api.block.PC_ItemBlock;
import powercraft.itemstorage.PCis_ChannelChestSave;


public class PCis_ItemBlockChannelChest extends PC_ItemBlock {

	public PCis_ItemBlockChannelChest(Block block) {
		super(block);
	}
	
	@Override
	public int getMetadata(World world, ItemStack itemStack) {
		return world.isRemote?0:itemStack.hasTagCompound()?itemStack.getTagCompound().getInteger("id"):PCis_ChannelChestSave.getNextFreeID();
	}
	
}
