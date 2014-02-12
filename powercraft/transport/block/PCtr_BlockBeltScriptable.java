package powercraft.transport.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;
import powercraft.api.PC_Direction;
import powercraft.api.PC_IconRegistry;
import powercraft.api.block.PC_BlockTileEntity;
import powercraft.api.block.PC_TileEntity;
import powercraft.transport.block.tileentity.PCtr_TileEntityBeltScriptable;

public class PCtr_BlockBeltScriptable extends PC_BlockTileEntity {

	private IIcon[] icons = new IIcon[2];
	
	public PCtr_BlockBeltScriptable() {
		super(Material.circuits);
		setCreativeTab(CreativeTabs.tabTransport);
		maxY = 1.0f/16.0f;
	}
	
	@Override
	public boolean renderAsNormalBlock(){
		return false;
	}
	
	@Override
    public boolean isOpaqueCube(){
        return false;
    }
	
	@Override
	public Class<? extends PC_TileEntity> getTileEntityClass() {
		return PCtr_TileEntityBeltScriptable.class;
	}

	@Override
	public int getRenderType() {
		return 0;
	}

	@Override
	public void registerIcons(PC_IconRegistry iconRegistry) {
		icons[0] = iconRegistry.registerIcon("top");
		icons[1] = iconRegistry.registerIcon("side");
	}

	@Override
	public IIcon getIcon(PC_Direction side, int metadata) {
		if(side==PC_Direction.UP || side==PC_Direction.DOWN){
			return icons[0];
		}
		return icons[1];
	}
	
}
