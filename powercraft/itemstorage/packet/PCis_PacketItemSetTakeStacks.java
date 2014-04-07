package powercraft.itemstorage.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;
import powercraft.api.network.PC_Packet;
import powercraft.api.network.PC_PacketClientToServer;
import powercraft.itemstorage.item.PCis_ItemCompressor;


public class PCis_PacketItemSetTakeStacks extends PC_PacketClientToServer {

	private boolean take;
	
	public PCis_PacketItemSetTakeStacks(){
		
	}
	
	public PCis_PacketItemSetTakeStacks(boolean take){
		this.take = take;
	}
	
	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		this.take = buf.readBoolean();
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeBoolean(this.take);
	}

	@Override
	protected PC_Packet doAndReply(NetHandlerPlayServer playServer, World world, EntityPlayer player) {
		PCis_ItemCompressor.setTakeStacks(player, this.take);
		return null;
	}
	
}
