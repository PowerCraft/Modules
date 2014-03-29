package powercraft.weasel.engine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.autoadd.PC_AutoCompleteDisplay;
import powercraft.api.gres.autoadd.PC_SortedStringList;
import powercraft.api.gres.autoadd.PC_StringListPart;
import powercraft.api.gres.autoadd.PC_StringWithInfo;
import powercraft.api.gres.doc.PC_GresDocument;
import powercraft.api.gres.doc.PC_GresDocumentLine;
import powercraft.api.script.weasel.PC_WeaselGresEdit;
import powercraft.weasel.PCws_Weasel;
import xscript.runtime.XModifier;
import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XClassProvider;
import xscript.runtime.clazz.XField;
import xscript.runtime.clazz.XPackage;
import xscript.runtime.method.XMethod;


public final class PCws_AutoCompleteHelper {

	private static class AutoCompleteHelper{

		PC_SortedStringList runtimeClasses;
		
		AutoCompleteHelper() {
			this.runtimeClasses = getAllRuntimeClasses();
		}
		
	}
	
	public static void makeComplete(PC_GresComponent component, PC_GresDocument document, PC_GresDocumentLine line, int x, PC_AutoCompleteDisplay info, PC_WeaselGresEdit weaselGresEdit) {
		AutoCompleteHelper ach = (AutoCompleteHelper)weaselGresEdit.getAutoCompleteHelper();
		if(ach==null){
			weaselGresEdit.setAutoCompleteHelper(ach = new AutoCompleteHelper());
		}
		info.display = true;
		info.parts = new PC_StringListPart[1];
		info.parts[0] = new PC_StringListPart(ach.runtimeClasses);
		info.done = "";
	}
	
	
	
	static PC_SortedStringList getAllRuntimeClasses(){
		XVirtualMachine vm = new XVirtualMachine(PCws_Weasel.getRTClassLoader(), 0);
		vm.getClassProvider().addClassLoader(PCws_Weasel.getWeaselRTClassLoader());
		XClassProvider classProvider = vm.getClassProvider();
		List<String> list = loadAllRuntimeClasses();
		for(String s:list){
			try{
				classProvider.getXClass(s);
			}catch(RuntimeException e){
				//
			}
		}
		List<XClass> c = vm.getClassProvider().getAllLoadedClasses();
		PC_SortedStringList l = new PC_SortedStringList();
		for(XClass cc:c){
			if(!cc.isArray() && isVisible(cc)){
				PC_StringWithInfo swi = new PC_StringWithInfo(cc.getSimpleName(), cc.getName());
				l.add(swi);
				String s = cc.getParent().getName();
				if(s!=null && !s.isEmpty()){
					swi = new PC_StringWithInfo(s, s);
					l.add(swi);
				}
			}
		}
		return l;
	}
	
	private static boolean isVisible(XPackage c){
		if(c instanceof XClass){
			if(!XModifier.isPublic(((XClass)c).getModifier()))
				return false;
		}else if(c instanceof XField){
			if(!XModifier.isPublic(((XField)c).getModifier()))
				return false;
		}else if(c instanceof XMethod){
			if(!XModifier.isPublic(((XMethod)c).getModifier()))
				return false;
		}
		return c.getParent()==null?true:isVisible(c.getParent());
	}
	
	private static List<String> loadAllRuntimeClasses(){
		List<String> list = getAllClassNames(PCws_Weasel.rt);
		list.addAll(getAllClassNames(PCws_Weasel.weaselrt));
		return list;
	}
	
	private static List<String> getAllClassNames(File file){
		List<String> list = new ArrayList<String>();
		if(file.isDirectory()){
			getAllClassNamesDir(file, list, null);
		}else{
			getAllClassNamesZip(file, list);
		}
		return list;
	}
	
	private static void getAllClassNamesZip(File file, List<String> list){
		try {
			ZipFile zip = new ZipFile(file);
			Enumeration<? extends ZipEntry> entries = zip.entries();
			while(entries.hasMoreElements()){
				ZipEntry zipEntry = entries.nextElement();
				String name = zipEntry.getName();
				if(name.endsWith(".xcbc")){
					list.add(name.substring(0, name.length()-5).replace('/', '.').replace('\\', '.'));
				}
			}
			zip.close();
		} catch (ZipException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void getAllClassNamesDir(File file, List<String> list, String name){
		String fName = file.getName();
		if(file.isDirectory()){
			File[] files = file.listFiles();
			if(name!=null){
				fName = name+"."+fName;
			}
			for(File f:files){
				getAllClassNamesDir(f, list, fName);
			}
		}else if(fName.endsWith(".xcbc")){
			fName = fName.substring(0, fName.length()-5);
			if(name!=null){
				fName = name+"."+fName;
			}
			list.add(fName);
		}
	}
	
}
