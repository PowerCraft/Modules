package powercraft.laser.tileEntity;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;
import powercraft.api.PC_3DRotationY;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Utils;
import powercraft.api.PC_Vec3;
import powercraft.api.beam.PC_LightValue;
import powercraft.api.block.PC_TileEntityRotateable;
import powercraft.laser.PCla_Beam;
import powercraft.laser.PCla_IBeamHandler;
import powercraft.laser.block.PCla_BlockLaserDamage;

public class PCla_TileEntityLaserDamage extends PC_TileEntityRotateable implements PCla_IBeamHandler {
	
	private static class PC_DamageSourceLaser extends EntityDamageSource{

		public PC_DamageSourceLaser(WorldServer worldServer) {
			super("Laser", FakePlayerFactory.getMinecraft(worldServer));
		}

		@Override
		public IChatComponent func_151519_b(EntityLivingBase living) {
	        return new ChatComponentTranslation("laser.kill.entity", living.func_145748_c_());
		}

		@Override
		public boolean isDifficultyScaled() {
			return false;
		}
		
	}
	
	@SuppressWarnings("unused")
	@Override
	public void onTick() {
		super.onTick();
		PC_Direction dir = get3DRotation().getSidePosition(PC_Direction.NORTH);
		PC_Vec3 vec = new PC_Vec3(dir.offsetX, dir.offsetY, dir.offsetZ);
		new PCla_Beam(this.worldObj, this, 20, new PC_Vec3(this.xCoord+0.5, this.yCoord+0.5, this.zCoord+0.5).add(vec.mul(0.3)), vec, new PC_LightValue(450*PC_LightValue.THz, 1));
	}

	@Override
	public boolean onHitBlock(World world, int x, int y, int z, PCla_Beam beam) {
		Block block = PC_Utils.getBlock(world, x, y, z);
		return block==null||!block.isNormalCube();
	}

	@Override
	public boolean onHitEntity(World world, Entity entity, PCla_Beam beam) {
		if(entity instanceof EntityItem || entity instanceof EntityXPOrb)
			return true;
		if(world instanceof WorldServer){
			entity.attackEntityFrom(new PC_DamageSourceLaser((WorldServer)world), 2);
		}
		return true;
	}

	@Override
	public void onFinished(PCla_Beam beam) {
		//
	}

	@Override
	public void onAdded(EntityPlayer player) {
		set3DRotation(new PC_3DRotationY(player));
		super.onAdded(player);
	}
	
	@Override
	public void onBlockPostSet(PC_Direction side, ItemStack stack, EntityPlayer player, float hitX, float hitY, float hitZ) {
		if(this.rotation==null)
			set3DRotation(new PC_3DRotationY(player));
	}
	
	@Override
	public IIcon getIcon(PC_Direction side) {
		if(side==PC_Direction.NORTH){
			return PCla_BlockLaserDamage.front;
		}
		return PCla_BlockLaserDamage.side;
	}
	
}
