package powercraft.weasel.engine;

import powercraft.api.script.weasel.PC_IWeaselNativeHandler;
import xscript.runtime.XVirtualMachine;
import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.nativemethod.XNativeMethod;
import xscript.runtime.nativemethod.XNativeProvider;
import xscript.runtime.object.XObject;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public final class PCws_WeaselNative {

	static void registerNatives(XVirtualMachine virtualMachine){
		XNativeProvider nativeProvider = virtualMachine.getNativeProvider();
		nativeProvider.addNativeMethod("weasel.devices.Device", "getTypeUnsafe(int)int", new GetTypeUnsafe());
		nativeProvider.addNativeMethod("weasel.devices.Device", "isDevicePresent(bool)int", new IsDevicePresent());
		nativeProvider.addNativeMethod("weasel.devices.Core", "getRedstoneValueUnsafe(int, int)int", new GetRedstoneValueUnsafe());
		nativeProvider.addNativeMethod("weasel.devices.Core", "setRedstoneValueUnsafe(int, int, int)bool", new SetRedstoneValueUnsafe());
	}
	
	private static class GetTypeUnsafe implements XNativeMethod{

		GetTypeUnsafe() {}

		@Override
		public Object invoke(XVirtualMachine vm, XThread thread,
				XMethodExecutor me, XGenericClass[] gen, String name,
				XObject _this, Object[] param) {
			if(vm.getUserData() instanceof PC_IWeaselNativeHandler){
				PC_IWeaselNativeHandler handler = (PC_IWeaselNativeHandler)vm.getUserData();
				return Integer.valueOf(handler.getTypeUnsafe(((Integer)param[0]).intValue()));
			}
			return Integer.valueOf(-1);
		}
		
	}
	
	private static class IsDevicePresent implements XNativeMethod{
		
		IsDevicePresent() {}

		@Override
		public Object invoke(XVirtualMachine vm, XThread thread,
				XMethodExecutor me, XGenericClass[] gen, String name,
				XObject _this, Object[] param) {
			if(vm.getUserData() instanceof PC_IWeaselNativeHandler){
				PC_IWeaselNativeHandler handler = (PC_IWeaselNativeHandler)vm.getUserData();
				return Boolean.valueOf(handler.isDevicePresent(((Integer)param[0]).intValue()));
			}
			return Boolean.FALSE;
		}
		
	}
	
	private static class GetRedstoneValueUnsafe implements XNativeMethod{
		
		GetRedstoneValueUnsafe() {}

		@Override
		public Object invoke(XVirtualMachine vm, XThread thread,
				XMethodExecutor me, XGenericClass[] gen, String name,
				XObject _this, Object[] param) {
			if(vm.getUserData() instanceof PC_IWeaselNativeHandler){
				PC_IWeaselNativeHandler handler = (PC_IWeaselNativeHandler)vm.getUserData();
				return Integer.valueOf(handler.getRedstoneValueUnsafe(((Integer)param[0]).intValue(), ((Integer)param[1]).intValue()));
			}
			return Integer.valueOf(-1);
		}
		
	}
	
	private static class SetRedstoneValueUnsafe implements XNativeMethod{
		
		SetRedstoneValueUnsafe() {}

		@Override
		public Object invoke(XVirtualMachine vm, XThread thread,
				XMethodExecutor me, XGenericClass[] gen, String name,
				XObject _this, Object[] param) {
			if(vm.getUserData() instanceof PC_IWeaselNativeHandler){
				PC_IWeaselNativeHandler handler = (PC_IWeaselNativeHandler)vm.getUserData();
				return Boolean.valueOf(handler.setRedstoneValueUnsafe(((Integer)param[0]).intValue(), ((Integer)param[1]).intValue(), ((Integer)param[2]).intValue()));
			}
			return Boolean.FALSE;
		}
		
	}
	
}
