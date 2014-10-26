package powercraft.teleporter.tileentity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import powercraft.api.PC_Field.Flag;
import powercraft.api.PC_Field;
import powercraft.api.PC_MathHelper;
import powercraft.api.PC_Utils;
import powercraft.api.PC_Vec4I;
import powercraft.api.block.PC_TileEntity;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.PC_IGresGuiOpenHandler;
import powercraft.teleporter.PCtp_Teleport;
import powercraft.teleporter.gui.PCtp_GuiTeleporter;
import powercraft.teleporter.network.PCtp_TeleporterEndpoint;
import powercraft.teleporter.network.PCtp_TeleporterGlobalNetwork;
import powercraft.teleporter.network.PCtp_TeleporterNetworkEntry;


public class PCtp_TileEntityTeleporter extends PC_TileEntity implements PC_IGresGuiOpenHandler {
	
	@PC_Field
	private String target;
	private PCtp_TeleporterEndpoint endpoint;
	
	@Override
	public void onLoadedFromNBT(Flag flag) {
		if(flag==Flag.SAVE && !isClient()){
			try{
				this.endpoint = (PCtp_TeleporterEndpoint) PCtp_TeleporterGlobalNetwork.instance().getEntry(new PC_Vec4I(this.xCoord, this.yCoord, this.zCoord, PC_Utils.getDimensionID(this.worldObj)));
			}catch(Throwable e){
				;
			}
		}
	}

	@Override
	public void onTick() {
		if(!isClient()){
			AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(this.xCoord, this.yCoord+1, this.zCoord, this.xCoord+1, this.yCoord+3, this.zCoord+1);
			List<Entity> entities = PC_Utils.getEntitiesWithinAABB(this.worldObj, aabb);
			for(Entity entity:entities){
				AxisAlignedBB bb = PC_Utils.getBoundingBox(entity);
				if(PC_MathHelper.xInsideY(bb, aabb)){
					teleport(entity, this.target);
				}
			}
		}
	}

	private void teleport(Entity entity, String target){
		PCtp_TeleporterNetworkEntry entry = PCtp_TeleporterGlobalNetwork.instance().getEntryByPath(target);
		if(entry instanceof PCtp_TeleporterEndpoint){
			PCtp_TeleporterEndpoint ep = (PCtp_TeleporterEndpoint)entry;
			PC_Vec4I pos = ep.getPos();
			PCtp_Teleport.teleport(entity, pos.x, pos.y, pos.z, 0, pos.w);
		}
	}

	@Override
	public PC_IGresGui openClientGui(EntityPlayer player, NBTTagCompound serverData) {
		return new PCtp_GuiTeleporter(player, this);
	}

	@Override
	public PC_GresBaseWithInventory openServerGui(EntityPlayer player, Object[] params) {
		return null;
	}

	@Override
	public NBTTagCompound sendOnGuiOpenToClient(EntityPlayer player, Object[] params) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
