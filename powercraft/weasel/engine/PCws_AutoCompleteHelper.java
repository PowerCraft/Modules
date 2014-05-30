package powercraft.weasel.engine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.autoadd.PC_AutoCompleteDisplay;
import powercraft.api.gres.autoadd.PC_SortedList;
import powercraft.api.gres.autoadd.PC_StringListPart;
import powercraft.api.gres.autoadd.PC_StringWithInfo;
import powercraft.api.gres.doc.PC_GresDocument;
import powercraft.api.gres.doc.PC_GresDocumentLine;
import powercraft.api.script.weasel.PC_WeaselGresEdit;
import powercraft.api.script.weasel.source.PC_WeaselSourceIterator;
import powercraft.api.script.weasel.source.PC_WeaselToken;
import powercraft.api.script.weasel.source.PC_WeaselTokenKind;
import powercraft.weasel.PCws_Weasel;
import xscript.compiler.message.XMessageLevel;
import xscript.compiler.message.XMessageList;
import xscript.compiler.standart.XLexer;
import xscript.compiler.standart.XParser;
import xscript.compiler.token.XLineDesk;
import xscript.compiler.tree.XTree;
import xscript.runtime.XModifier;
import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XClassProvider;
import xscript.runtime.clazz.XField;
import xscript.runtime.clazz.XPackage;
import xscript.runtime.method.XMethod;


public final class PCws_AutoCompleteHelper {

	private static final PC_SortedList<PC_StringWithInfo> keyWords = new PC_SortedList<PC_StringWithInfo>();
	
	static{
		keyWords.add(new PC_StringWithInfo("if", "instruction: if"));
		keyWords.add(new PC_StringWithInfo("for", "instruction: for"));
		keyWords.add(new PC_StringWithInfo("do", "instruction: do"));
		keyWords.add(new PC_StringWithInfo("while", "instruction: while"));
		keyWords.add(new PC_StringWithInfo("try", "instruction: try"));
		keyWords.add(new PC_StringWithInfo("switch", "instruction: switch"));
		keyWords.add(new PC_StringWithInfo("return", "instruction: return"));
	}
	
	private static class AutoCompleteHelper{

		PC_SortedList<PC_StringWithInfo> runtimeClasses;
		
		AutoCompleteHelper() {
			this.runtimeClasses = getAllRuntimeClasses();
		}
		
	}
	
	private static class Type{
		XPackage p;
		boolean s;
		int pos;
		Type(int pos) {
			this.pos = pos;
		}
	}
	
	private static class MessageListener implements XMessageList{

		MessageListener() {}

		@Override
		public void postMessage(XMessageLevel arg0, String arg1, XLineDesk arg2, Object[] arg3) {/**/}
		
	}
	
	public static void makeComplete(PC_GresComponent component, PC_GresDocument document, PC_GresDocumentLine line, int x, PC_AutoCompleteDisplay info, PC_WeaselGresEdit weaselGresEdit) {
		AutoCompleteHelper ach = (AutoCompleteHelper)weaselGresEdit.getAutoCompleteHelper();
		if(ach==null){
			weaselGresEdit.setAutoCompleteHelper(ach = new AutoCompleteHelper());
		}
		int lineNum = document.getLineNum(line);
		PC_WeaselSourceIterator iterator = new PC_WeaselSourceIterator(line, x);
		if(iterator.getTypeAtPos()==0){
			XTree tree = new XParser(new XLexer(document.getWholeText(), new MessageListener()), new MessageListener()).makeTree();
			
			iterator.gotoInstructionStart(";{}");
			PC_WeaselToken token = iterator.readNextToken();
			List<PC_WeaselToken> tokens = new ArrayList<PC_WeaselToken>();
			while(token.lineDesk.isAfterStart(line, x) && token.kind!=PC_WeaselTokenKind.EOF){
				tokens.add(token);
				token = iterator.readNextToken();
			}
			if(tokens.isEmpty()){
				info.display = true;
				info.parts = new PC_StringListPart[2];
				info.parts[0] = new PC_StringListPart(keyWords);
				info.parts[1] = new PC_StringListPart(ach.runtimeClasses);
				info.parts[0].searchFor("");
				info.parts[1].searchFor("");
				info.done = "";
			}else{
				token = tokens.get(tokens.size()-1);
				boolean getType = false;
				if(token.kind==PC_WeaselTokenKind.ELEMENT){
					tokens.remove(tokens.size()-1);
					getType = true;
				}else if(token.kind==PC_WeaselTokenKind.IDENT && tokens.size()>1 && tokens.get(tokens.size()-2).kind==PC_WeaselTokenKind.ELEMENT){
					tokens.remove(tokens.size()-1);
					tokens.remove(tokens.size()-1);
					getType = true;
				}
				if(getType){
					int pos = findStart(tokens);
					if(pos!=-1){
						Type type = new Type(pos);
						XVirtualMachine vm = makeVM();
						while(type.pos != tokens.size()){
							getType(tokens, type, vm);
						}
						if(type.pos == tokens.size()){
							info.display = true;
							String name;
							if(token.kind==PC_WeaselTokenKind.IDENT){
								name = token.param.toString();
								if(!token.lineDesk.isAfter(line, x+1)){
									name = name.substring(0, x-token.lineDesk.startLinePos);
								}
							}else{
								name = "";
							}
							info.done = name;
							if(type.p instanceof XClass){
								if(type.s){
									info.parts = new PC_StringListPart[1];
									info.parts[0] = new PC_StringListPart(getClassPossibilities((XClass)type.p, XModifier.STATIC, 0));
								}else{
									info.parts = new PC_StringListPart[2];
									info.parts[0] = new PC_StringListPart(getClassPossibilities((XClass)type.p, 0, XModifier.STATIC));
									info.parts[1] = new PC_StringListPart(getClassPossibilities((XClass)type.p, XModifier.STATIC, 0));
								}
								for(PC_StringListPart part:info.parts){
									part.searchFor(name);
								}
							}else if(type.p != null){
								info.parts = new PC_StringListPart[1];
								info.parts[0] = new PC_StringListPart(ach.runtimeClasses);
								info.parts[0].searchFor(type.p.getName()+"."+name);
								info.done = type.p.getName()+"."+name;
							}
						}
					}
				}else if(token.kind==PC_WeaselTokenKind.IDENT){
					String name = token.param.toString();
					if(!token.lineDesk.isAfter(line, x+1)){
						name = name.substring(0, x-token.lineDesk.startLinePos);
					}
					info.display = true;
					info.parts = new PC_StringListPart[1];
					info.parts = new PC_StringListPart[2];
					info.parts[0] = new PC_StringListPart(keyWords);
					info.parts[1] = new PC_StringListPart(ach.runtimeClasses);
					info.parts[0].searchFor(name);
					info.parts[1].searchFor(name);
					info.done = name;
				}
			}
		}else{
			info.display = false;
		}
	}
	
	
	
	private static int findStart(List<PC_WeaselToken> list){
		int exp = -1;
		for(int i=list.size()-1; i>=0; i--){
			PC_WeaselToken token = list.get(i);
			if(exp==-1 && token.kind==PC_WeaselTokenKind.IDENT){
				if(i==0)
					return 0;
				exp = -2;
			}else if(exp==-2 && token.kind==PC_WeaselTokenKind.ELEMENT){
				exp = -1;
			}else{
				return i+1;
			}
		}
		return -1;
	}
	
	private static void getType(List<PC_WeaselToken> tokens, Type type, XVirtualMachine vm){
		PC_WeaselToken token = tokens.get(type.pos++);
		if(token.kind==PC_WeaselTokenKind.IDENT){
			String name = token.param.toString();
			XPackage p = vm.getClassProvider().getLoadedXPackage(name);
			if(p!=null){
				type.p = p;
				type.s = true;
			}else{
				List<XClass> list = vm.getClassProvider().getAllLoadedClasses();
				for(XClass c:list){
					if(c.getSimpleName().equals(name)){
						type.p = c;
						type.s = true;
						break;
					}
				}
			}
		}else if(token.kind==PC_WeaselTokenKind.ELEMENT){
			token = tokens.get(type.pos++);
			if(token.kind==PC_WeaselTokenKind.IDENT){
				String name = token.param.toString();
				type.p = type.p.getChild(name);
				if(type.p instanceof XClass){
					type.s = true;
				}else if(type.p instanceof XField){
					type.s = false;
					type.p = ((XField)type.p).getType().getXClass(vm);
				}else if(type.p instanceof XMethod){
					type.s = false;
					type.p = ((XMethod)type.p).getReturnTypePtr().getXClass(vm);
				}
			}
		}
	}
	
	public static PC_SortedList<PC_StringWithInfo> getClassPossibilities(XClass xClass, int acceptedModifier, int inAcceptedModifier){
		PC_SortedList<PC_StringWithInfo> list = new PC_SortedList<PC_StringWithInfo>();
		getClassPossibilities(xClass, list, acceptedModifier, inAcceptedModifier);
		return list;
	}
	
	public static void getClassPossibilities(XClass xClass, PC_SortedList<PC_StringWithInfo> list, int acceptedModifier, int inAcceptedModifier){
		Set<Entry<String, Object>> set = xClass.entrySet();
		for(Entry<String, Object> e:set){
			Object value = e.getValue();
			if(value instanceof XClass){
				if((((XClass)value).getModifier() & acceptedModifier)==acceptedModifier && (((XClass)value).getModifier() & inAcceptedModifier)==0)
					list.add(new PC_StringWithInfo(((XClass)value).getSimpleName(), e.getKey()));
			}else if(value instanceof XMethod){
				if(((XMethod)value).getRealName().indexOf('<')==-1 && ((XMethod)value).getRealName().indexOf('$')==-1 && (((XMethod)value).getModifier() & acceptedModifier)==acceptedModifier && (((XMethod)value).getModifier() & inAcceptedModifier)==0)
					list.add(new PC_StringWithInfo(((XMethod)value).getRealName(), e.getKey()));
			}else if(value instanceof XField){
				if(((XField)value).getSimpleName().indexOf('$')==-1 && (((XField)value).getModifier() & acceptedModifier)==acceptedModifier && (((XField)value).getModifier() & inAcceptedModifier)==0)
					list.add(new PC_StringWithInfo(((XField)value).getSimpleName(), e.getKey()));
			}
		}
	}
	
	private static XVirtualMachine makeVM(){
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
		return vm;
	}
	
	static PC_SortedList<PC_StringWithInfo> getAllRuntimeClasses(){
		XVirtualMachine vm = makeVM();
		List<XClass> c = vm.getClassProvider().getAllLoadedClasses();
		PC_SortedList<PC_StringWithInfo> l = new PC_SortedList<PC_StringWithInfo>();
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
