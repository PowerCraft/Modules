package powercraft.itemstorage.item.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;
import powercraft.api.network.PC_Packet;
import powercraft.api.network.PC_PacketClientToServer;
import powercraft.itemstorage.item.PCis_ItemCompressor;


public class PCis_PacketItemSetPutStacks extends PC_PacketClientToServer {

	private int num;
	
	public PCis_PacketItemSetPutStacks(){
		
	}
	
	public PCis_PacketItemSetPutStacks(int num){
		this.num = num;
	}
	
	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		this.num = buf.readInt();
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeInt(this.num);
	}

	@Override
	protected PC_Packet doAndReply(NetHandlerPlayServer playServer, World world, EntityPlayer player) {
		PCis_ItemCompressor.setPutStacks(player, this.num);
		return null;
	}
	
}
