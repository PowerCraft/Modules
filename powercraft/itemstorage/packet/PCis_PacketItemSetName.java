package powercraft.itemstorage.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;
import powercraft.api.network.PC_Packet;
import powercraft.api.network.PC_PacketClientToServer;
import powercraft.itemstorage.item.PCis_ItemCompressor;


public class PCis_PacketItemSetName extends PC_PacketClientToServer {

	private String name;
	
	public PCis_PacketItemSetName(){
		
	}
	
	public PCis_PacketItemSetName(String name){
		this.name = name;
	}
	
	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		this.name = readStringFromBuf(buf);
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		writeStringToBuf(buf, this.name);
	}

	@Override
	protected PC_Packet doAndReply(NetHandlerPlayServer playServer, World world, EntityPlayer player) {
		PCis_ItemCompressor.setName(player, this.name);
		return null;
	}
	
}
