package powercraft.core;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;
import powercraft.api.network.PC_Packet;
import powercraft.api.network.PC_PacketClientToServer;


public class PCco_PacketCrafting extends PC_PacketClientToServer {
	
	private ItemStack itemStack;
	
	public PCco_PacketCrafting(){
		
	}
	
	public PCco_PacketCrafting(ItemStack itemStack){
		this.itemStack = itemStack;
	}
	
	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		this.itemStack = readItemStackFromBuf(buf);
	}
	
	@Override
	protected void toByteBuffer(ByteBuf buf) {
		writeItemStackToBuf(buf, this.itemStack);
	}
	
	@Override
	protected PC_Packet doAndReply(NetHandlerPlayServer playServer, World world, EntityPlayerMP player) {
		PCco_CraftingToolCrafter.onPacket(player, this.itemStack);
		return null;
	}
	
}
