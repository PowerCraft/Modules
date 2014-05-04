package powercraft.laser;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import powercraft.api.PC_Side;
import powercraft.api.PC_TickHandler;
import powercraft.api.PC_TickHandler.PC_ITickHandler;
import powercraft.api.PC_Utils;
import powercraft.api.beam.PC_BeamHitResult;
import powercraft.api.block.PC_AbstractBlockBase;


public final class PCla_Beams implements PC_ITickHandler {
	
	private static final PCla_Beams INSTANCE = new PCla_Beams();
	
	static{
		PC_TickHandler.registerTickHandler(INSTANCE);
	}
	
	private PCla_Beams(){
		if(INSTANCE!=null){
			PC_Utils.staticClassConstructor();
		}
	}
	
	private static final ThreadLocal<List<PCla_Beam>> beams = new ThreadLocal<List<PCla_Beam>>();
	
	public static void addBeam(PCla_Beam beam){
		List<PCla_Beam> list = beams.get();
		if(list==null){
			beams.set(list = new ArrayList<PCla_Beam>());
		}
		list.add(beam);
	}
	
	public static void trace(){
		List<PCla_Beam> list = beams.get();
		if(list==null)
			return;
		for(int i=0; i<list.size(); i++){
			list.get(i).trace();
		}
		if(PC_Utils.isClient()){
			for(int i=0; i<list.size(); i++){
				list.get(i).generate();
			}
		}
		list.clear();
	}
	
	public static PC_BeamHitResult onHitBlock(World world, int x, int y, int z, PCla_Beam beam){
		Block block = PC_Utils.getBlock(world, x, y, z);
		if(block instanceof PC_AbstractBlockBase){
			return ((PC_AbstractBlockBase)block).onHitByBeam(world, x, y, z, beam);
		}
		return PC_BeamHitResult.STANDART;
	}

	@SuppressWarnings("unused")
	public static PC_BeamHitResult onHitEntity(World world, Entity entity, PCla_Beam beam) {
		return PC_BeamHitResult.STANDART;
	}

	@Override
	public void onStartTick(PC_Side side) {
		//
	}

	@Override
	public void onEndTick(PC_Side side) {
		trace();
	}
	
}
