package powercraft.itemstorage.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import powercraft.api.PC_IconRegistry;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.PC_IGresGuiOpenHandler;
import powercraft.api.inventory.PC_InventoryUtils;
import powercraft.api.item.PC_Item;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.recipes.PC_Recipes;
import powercraft.itemstorage.PCis_ChannelChestSave;
import powercraft.itemstorage.PCis_ItemStorage;
import powercraft.itemstorage.container.PCis_ContainerCompressor;
import powercraft.itemstorage.gui.PCis_GuiChannelNotConnected;
import powercraft.itemstorage.gui.PCis_GuiCompressor;
import powercraft.itemstorage.inventory.PCis_CompressorInventory;
import powercraft.itemstorage.inventory.PCis_EnderCompressorInventory;
import powercraft.itemstorage.inventory.PCis_HightCompressorInventory;
import powercraft.itemstorage.inventory.PCis_NormalCompressorInventory;
import powercraft.itemstorage.packet.PCis_PacketItemSetName;
import powercraft.itemstorage.packet.PCis_PacketItemSetPutStacks;
import powercraft.itemstorage.packet.PCis_PacketItemSetTakeStacks;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PCis_ItemCompressor extends PC_Item implements PC_IGresGuiOpenHandler {
	
	public static final int NORMAL = 0, ENDERACCESS = 1, HEIGHT = 2, BIG = 3, CHANNEL = 4;
	public static final String id2Name[] = {"normal", "enderaccess", "height", "big", "channel"};
	private IIcon[] icons = new IIcon[5];
	
	public PCis_ItemCompressor() {
		setMaxDamage(0);
        setMaxStackSize(1);
        setHasSubtypes(true);
        setCreativeTab(CreativeTabs.tabTools);
	}
	
	@Override
	public void initRecipes(){
		PC_Recipes.addShapedRecipe(new ItemStack(this, 1, NORMAL), " L ", "LCL", " L ", 'L', Blocks.lever, 'C', Blocks.chest);
        PC_Recipes.addShapedRecipe(new ItemStack(this, 1, ENDERACCESS), " L ", "LCL", " L ", 'L', Blocks.lever, 'C', Blocks.ender_chest);
        PC_Recipes.addShapedRecipe(new ItemStack(this, 1, HEIGHT), "LCL", "LCL", "LCL", 'L', Blocks.lever, 'C', Blocks.chest);
        PC_Recipes.addShapedRecipe(new ItemStack(this, 1, BIG), "LLL", "CCC", "LLL", 'L', Blocks.lever, 'C', Blocks.chest);
        PC_Recipes.addShapedRecipe(new ItemStack(this, 1, CHANNEL), " L ", "LCL", " L ", 'L', Blocks.lever, 'C', PCis_ItemStorage.CHANNEL_CHEST);
	}
	
	@Override
	public void registerIcons(PC_IconRegistry iconRegistry) {
		for(int i=0; i<this.icons.length; i++){
			this.icons[i] = iconRegistry.registerIcon(id2Name[i]);
		}
	}

	@Override
	public IIcon getIconFromDamage(int type) {
		return this.icons[type];
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void getSubItems(Item item, CreativeTabs creativeTabs, List list) {
		list.add(new ItemStack(this, 1, NORMAL));
		list.add(new ItemStack(this, 1, ENDERACCESS));
		list.add(new ItemStack(this, 1, HEIGHT));
		list.add(new ItemStack(this, 1, BIG));
		list.add(new ItemStack(this, 1, CHANNEL));
	}
	
	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		return super.getUnlocalizedName() + "." + id2Name[itemStack.getItemDamage()];
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
		if(itemStack.hasTagCompound()){
			if(itemStack.getTagCompound().hasKey("label"))
				list.add(itemStack.getTagCompound().getString("label"));
		}
	}

	private static int findSlotInPlayerInvFor(ItemStack itemStack, IInventory inventory, int putStacks){
		int putStacksCount = putStacks;
		for(int i=0; i<inventory.getSizeInventory(); i++){
			ItemStack is = inventory.getStackInSlot(i);
			if(is!=null && itemStack.isItemEqual(is)){
				if(putStacksCount==0){
					return i;
				}
				putStacksCount--;
			}
		}
		return -1;
	}
	
	private static int findSlotInPlayerInvForStore(ItemStack itemStack, IInventory inventory, int putStacks, int startSlot){
		int putStacksCount = putStacks;
		for(int i=startSlot; i<inventory.getSizeInventory(); i++){
			ItemStack is = inventory.getStackInSlot(i);
			if(is!=null && itemStack.isItemEqual(is) && is.stackSize<is.getMaxStackSize()){
				if(is.stackSize<is.getMaxStackSize()){
					if(putStacksCount>0){
						return i;
					}
					return -1;
				}
				putStacksCount--;
			}
		}
		return -1;
	}
	
	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int slot, boolean currentItem) {
		if(entity instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer)entity;
			if(player.openContainer instanceof PCis_ContainerCompressor && currentItem)
				return;
		}
		IInventory inventory = PC_InventoryUtils.getInventoryFrom(entity);
		boolean takeStacks = isTakeStacks(itemStack);
		int putStacks = getPutStacks(itemStack);
		IInventory compressorinv = getInventoryFor(world, entity, slot);
		if(compressorinv==null)
			return;
		if(takeStacks){
			for(int i=0; i<compressorinv.getSizeInventory(); i++){
				ItemStack is = compressorinv.getStackInSlot(i);
				if(is!=null){
					int need = PC_InventoryUtils.getSlotStackLimit(compressorinv, i)-is.stackSize;
					if(need>0){
						int playerSlot = findSlotInPlayerInvFor(is, inventory, putStacks);
						if(playerSlot!=-1){
							ItemStack isp = inventory.getStackInSlot(playerSlot);
							if(need>isp.stackSize){
								is.stackSize+=isp.stackSize;
								inventory.setInventorySlotContents(playerSlot, null);
							}else{
								is.stackSize+=need;
								isp.stackSize-=need;
								if(isp.stackSize==0)
									inventory.setInventorySlotContents(playerSlot, null);
							}
						}
					}
				}
			}
		}
		for(int n=0; n<putStacks; n++){
			for(int i=0; i<compressorinv.getSizeInventory(); i++){
				ItemStack is = compressorinv.getStackInSlot(i);
				if(is!=null && is.stackSize>1){
					int playerSlot = findSlotInPlayerInvForStore(is, inventory, putStacks, 0);
					while(playerSlot!=-1){
						ItemStack isp = inventory.getStackInSlot(playerSlot);
						int need = isp.getMaxStackSize()-isp.stackSize;
						if(need>is.stackSize){
							isp.stackSize += is.stackSize-1;
							is.stackSize = 1;
						}else{
							isp.stackSize+=need;
							is.stackSize-=need;
							if(is.stackSize==0){
								is.stackSize=1;
								isp.stackSize--;
							}
						}
						playerSlot = findSlotInPlayerInvForStore(is, inventory, putStacks, playerSlot+1);
					}
				}
			}
		}
		PC_InventoryUtils.onTick(compressorinv, world);
		if(compressorinv instanceof PCis_CompressorInventory)
			compressorinv.closeInventory();
	}
	
	@Override
	public void onTick(ItemStack itemStack, World world, IInventory inventory, int slot) {
		boolean takeStacks = isTakeStacks(itemStack);
		int putStacks = getPutStacks(itemStack);
		IInventory compressorinv = getInventoryFor(world, inventory, slot);
		if(compressorinv==null)
			return;
		if(takeStacks){
			for(int i=0; i<compressorinv.getSizeInventory(); i++){
				ItemStack is = compressorinv.getStackInSlot(i);
				if(is!=null){
					int need = PC_InventoryUtils.getSlotStackLimit(compressorinv, i)-is.stackSize;
					if(need>0){
						int playerSlot = findSlotInPlayerInvFor(is, inventory, putStacks);
						if(playerSlot!=-1){
							ItemStack isp = inventory.getStackInSlot(playerSlot);
							if(need>isp.stackSize){
								is.stackSize+=isp.stackSize;
								inventory.setInventorySlotContents(playerSlot, null);
							}else{
								is.stackSize+=need;
								isp.stackSize-=need;
								if(isp.stackSize==0)
									inventory.setInventorySlotContents(playerSlot, null);
							}
						}
					}
				}
			}
		}
		for(int n=0; n<putStacks; n++){
			for(int i=0; i<compressorinv.getSizeInventory(); i++){
				ItemStack is = compressorinv.getStackInSlot(i);
				if(is!=null && is.stackSize>1){
					int playerSlot = findSlotInPlayerInvForStore(is, inventory, putStacks, 0);
					while(playerSlot!=-1){
						ItemStack isp = inventory.getStackInSlot(playerSlot);
						int need = isp.getMaxStackSize()-isp.stackSize;
						if(need>is.stackSize){
							isp.stackSize += is.stackSize-1;
							is.stackSize = 1;
						}else{
							isp.stackSize+=need;
							is.stackSize-=need;
							if(is.stackSize==0){
								is.stackSize=1;
								isp.stackSize--;
							}
						}
						playerSlot = findSlotInPlayerInvForStore(is, inventory, putStacks, playerSlot+1);
					}
				}
			}
		}
		PC_InventoryUtils.onTick(compressorinv, world);
		compressorinv.closeInventory();
	}

	public static IInventory getInventoryFor(World world, Object player, int equipment){
		IInventory inventory = PC_InventoryUtils.getInventoryFrom(player);
		if(inventory==null)
			return null;
		int slot = equipment;
		if(player instanceof EntityPlayer && slot==-1){
			slot = ((EntityPlayer)player).inventory.currentItem;
		}
		ItemStack compressor = inventory.getStackInSlot(slot);
		switch(compressor.getItemDamage()){
		case NORMAL:
			return new PCis_NormalCompressorInventory(inventory, slot);
		case ENDERACCESS:
			if(player instanceof EntityPlayer){
				return new PCis_EnderCompressorInventory((EntityPlayer)player, inventory, slot);
			}
			return null;
		case HEIGHT:
			return new PCis_HightCompressorInventory(inventory, slot);
		case BIG:
			return new PCis_NormalCompressorInventory(inventory, slot, new PC_Vec2I(9, 6));
		case CHANNEL:
			if(world.isRemote){
				return PCis_ChannelChestSave.getFake();
			}
			if(compressor.hasTagCompound())
				return PCis_ChannelChestSave.getInventoryForChannelChest(compressor.getTagCompound().getInteger("id"));
			return null;
		default:
			return null;
		}
	}
	
	public static String getName(ItemStack item) {
		if(item.hasTagCompound()){
			if(item.getTagCompound().hasKey("label"))
				return item.getTagCompound().getString("label");
		}
		return "";
	}
	
	public static void setName(EntityPlayer thePlayer, String name) {
		NBTTagCompound nbtTag = getItem(thePlayer).getTagCompound();
		if(nbtTag==null){
			nbtTag = new NBTTagCompound();
			getItem(thePlayer).setTagCompound(nbtTag);
		}
		if(name==null || name.equals(""))
			nbtTag.removeTag("label");
		else
			nbtTag.setString("label", name);
		if(thePlayer.worldObj.isRemote){
			PC_PacketHandler.sendToServer(new PCis_PacketItemSetName(name));
		}
	}

	public static int getPutStacks(ItemStack item) {
		if(item.hasTagCompound()){
			if(item.getTagCompound().hasKey("putStacks"))
				return item.getTagCompound().getInteger("putStacks");
		}
		return 0;
	}
	
	public static void setPutStacks(EntityPlayer thePlayer, int num) {
		NBTTagCompound nbtTag = getItem(thePlayer).getTagCompound();
		if(nbtTag==null){
			nbtTag = new NBTTagCompound();
			getItem(thePlayer).setTagCompound(nbtTag);
		}
		nbtTag.setInteger("putStacks", num);
		if(thePlayer.worldObj.isRemote){
			PC_PacketHandler.sendToServer(new PCis_PacketItemSetPutStacks(num));
		}
	}

	public static boolean isTakeStacks(ItemStack item) {
		if(item.hasTagCompound()){
			if(item.getTagCompound().hasKey("takeStacks"))
				return item.getTagCompound().getBoolean("takeStacks");
		}
		return false;
	}
	
	public static void setTakeStacks(EntityPlayer thePlayer, boolean checked) {
		NBTTagCompound nbtTag = getItem(thePlayer).getTagCompound();
		if(nbtTag==null){
			nbtTag = new NBTTagCompound();
			getItem(thePlayer).setTagCompound(nbtTag);
		}
		nbtTag.setBoolean("takeStacks", checked);
		if(thePlayer.worldObj.isRemote){
			PC_PacketHandler.sendToServer(new PCis_PacketItemSetTakeStacks(checked));
		}
	}

	public static ItemStack getItem(EntityPlayer player){
		return player.inventory.getCurrentItem();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public PC_IGresGui openClientGui(EntityPlayer player, NBTTagCompound serverData) {
		int slot = serverData.getInteger("slot");
		boolean linked = serverData.getBoolean("linked");
		if(!linked){
			return new PCis_GuiChannelNotConnected();
		}
		return new PCis_GuiCompressor(player, player.inventory.getStackInSlot(slot), slot, getInventoryFor(player.worldObj, player, -1));
	}

	@Override
	public PC_GresBaseWithInventory openServerGui(EntityPlayer player) {
		IInventory inv = getInventoryFor(player.worldObj, player, -1);
		if(inv==null)
			return null;
		return new PCis_ContainerCompressor(player, player.getHeldItem(), player.inventory.currentItem, inv);
	}

	@Override
	public NBTTagCompound sendOnGuiOpenToClient(EntityPlayer player) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("slot", player.inventory.currentItem);
		nbt.setBoolean("linked", getInventoryFor(player.worldObj, player, -1)!=null);
		return nbt;
	}
	
}
