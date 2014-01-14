package powercraft_new.modules.transport.blocks;

import powercraft_new.api.block.PC_Block;
import powercraft_new.api.tileentity.PC_TileEntity;

public class BlockElevatorUp extends PC_Block{

	public BlockElevatorUp(int id, String material, String name, PC_TileEntity te) {
		super(id, material, name, te);
		this.setTexture((short) 0, "elevatorUp_top", "");
		this.setTexture((short) 1, "elevatorDown");
	}
}
