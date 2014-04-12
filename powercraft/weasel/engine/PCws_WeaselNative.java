package powercraft.weasel.engine;

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
		nativeProvider.addNativeMethod("weasel.devices.Device", "getTypeUnsafe{(int)int}", new GetTypeUnsafe());
		nativeProvider.addNativeMethod("weasel.devices.Device", "isDevicePresent{(bool)int}", new IsDevicePresent());
		nativeProvider.addNativeMethod("weasel.devices.Core", "getRedstoneValueUnsafe{(int, int)int}", new GetRedstoneValueUnsafe());
		nativeProvider.addNativeMethod("weasel.devices.Core", "setRedstoneValueUnsafe{(int, int, int)bool}", new SetRedstoneValueUnsafe());
		nativeProvider.addNativeMethod("weasel.devices.Console", "printUnsafe{(int, xscript.lang.String)bool}", new PrintUnsafe());
		nativeProvider.addNativeMethod("weasel.devices.Console", "clsUnsafe{(int)bool}", new ClsUnsafe());
	}
	
	private static class GetTypeUnsafe implements XNativeMethod{

		GetTypeUnsafe() {}

		@Override
		public Object invoke(XVirtualMachine vm, XThread thread,
				XMethodExecutor me, XGenericClass[] gen, String name,
				XObject _this, Object[] param) {
			return Integer.valueOf(((PCws_WeaselContainer)vm.getUserData()).getTypeUnsafe(((Integer)param[0]).intValue()));
		}
		
	}
	
	private static class IsDevicePresent implements XNativeMethod{
		
		IsDevicePresent() {}

		@Override
		public Object invoke(XVirtualMachine vm, XThread thread,
				XMethodExecutor me, XGenericClass[] gen, String name,
				XObject _this, Object[] param) {
			return Boolean.valueOf(((PCws_WeaselContainer)vm.getUserData()).isDevicePresent(((Integer)param[0]).intValue()));
		}
		
	}
	
	private static class GetRedstoneValueUnsafe implements XNativeMethod{
		
		GetRedstoneValueUnsafe() {}

		@Override
		public Object invoke(XVirtualMachine vm, XThread thread,
				XMethodExecutor me, XGenericClass[] gen, String name,
				XObject _this, Object[] param) {
			return Integer.valueOf(((PCws_WeaselContainer)vm.getUserData()).getRedstoneValueUnsafe(((Integer)param[0]).intValue(), ((Integer)param[1]).intValue()));
		}
		
	}
	
	private static class SetRedstoneValueUnsafe implements XNativeMethod{
		
		SetRedstoneValueUnsafe() {}

		@Override
		public Object invoke(XVirtualMachine vm, XThread thread,
				XMethodExecutor me, XGenericClass[] gen, String name,
				XObject _this, Object[] param) {
			return Boolean.valueOf(((PCws_WeaselContainer)vm.getUserData()).setRedstoneValueUnsafe(((Integer)param[0]).intValue(), ((Integer)param[1]).intValue(), ((Integer)param[2]).intValue()));
		}
		
	}
	
	private static class PrintUnsafe implements XNativeMethod{
		
		PrintUnsafe() {}

		@Override
		public Object invoke(XVirtualMachine vm, XThread thread,
				XMethodExecutor me, XGenericClass[] gen, String name,
				XObject _this, Object[] param) {
			return Boolean.valueOf(((PCws_WeaselContainer)vm.getUserData()).printUnsafe(((Integer)param[0]).intValue(), (String)param[1]));
		}
		
	}
	
	private static class ClsUnsafe implements XNativeMethod{
		
		ClsUnsafe() {}

		@Override
		public Object invoke(XVirtualMachine vm, XThread thread,
				XMethodExecutor me, XGenericClass[] gen, String name,
				XObject _this, Object[] param) {
			return Boolean.valueOf(((PCws_WeaselContainer)vm.getUserData()).clsUnsafe(((Integer)param[0]).intValue()));
		}
		
	}
	
}
