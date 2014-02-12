package powercraft.transport.block;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetHandler;
import powercraft.api.PC_ClientUtils;
import powercraft.api.network.PC_Packet;
import powercraft.api.network.PC_PacketServerToClient;

public class PCtr_PacketSetEntitySpeed extends PC_PacketServerToClient {

	private int entity;
	private NBTTagCompound compound;
	
	public PCtr_PacketSetEntitySpeed(NBTTagCompound compound, int entity) {
		this.compound = compound;
		this.entity = entity;
	}

	public PCtr_PacketSetEntitySpeed(){
		
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	protected PC_Packet doAndReply(INetHandler iNetHandler) {
		PC_ClientUtils.mc().theWorld.getEntityByID(entity).getEntityData().setTag("PowerCraft", compound);
		return null;
	}

	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		try {
			entity = buf.readInt();
			byte[] bytes = new byte[buf.readUnsignedShort()];
			buf.readBytes(bytes);
			compound = CompressedStreamTools.decompress(bytes);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		try {
			byte[] bytes = CompressedStreamTools.compress(compound);
			buf.writeInt(entity);
			buf.writeShort(bytes.length);
			buf.writeBytes(bytes);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	

}
