package powercraft.transport.block;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import powercraft.api.network.PC_Packet;
import powercraft.api.network.PC_PacketServerToClient;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PCtr_PacketSetEntitySpeed extends PC_PacketServerToClient {

	private int entity;
	private NBTTagCompound compound;
	private double posX;
	private double posY;
	private double posZ;
	
	public PCtr_PacketSetEntitySpeed(NBTTagCompound compound, Entity entity) {
		this.compound = compound;
		this.entity = entity.getEntityId();
		this.posX = entity.posX;
		this.posY = entity.posY;
		this.posZ = entity.posZ;
	}

	public PCtr_PacketSetEntitySpeed(){
		
	}
	
	@SuppressWarnings("hiding")
	@Override
	@SideOnly(Side.CLIENT)
	protected PC_Packet doAndReply(NetHandlerPlayClient iNetHandler, World world, EntityPlayer player) {
		Entity entity = world.getEntityByID(this.entity);
		if(entity!=null){
			entity.getEntityData().setTag("PowerCraft", this.compound);
			//entity.posX = posX;
			//entity.posY = posY;
			//entity.posZ = posZ;
		}
		return null;
	}

	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		try {
			this.entity = buf.readInt();
			byte[] bytes = new byte[buf.readUnsignedShort()];
			buf.readBytes(bytes);
			this.compound = CompressedStreamTools.decompress(bytes);
			this.posX = buf.readDouble();
			this.posY = buf.readDouble();
			this.posZ = buf.readDouble();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		try {
			byte[] bytes = CompressedStreamTools.compress(this.compound);
			buf.writeInt(this.entity);
			buf.writeShort(bytes.length);
			buf.writeBytes(bytes);
			buf.writeDouble(this.posX);
			buf.writeDouble(this.posY);
			buf.writeDouble(this.posZ);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	

}
