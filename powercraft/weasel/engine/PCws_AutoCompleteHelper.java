package powercraft.weasel.engine;

import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.autoadd.PC_AutoCompleteDisplay;
import powercraft.api.gres.doc.PC_GresDocument;
import powercraft.api.gres.doc.PC_GresDocumentLine;
import xscript.compiler.message.XMessageLevel;
import xscript.compiler.message.XMessageList;
import xscript.compiler.standart.XLexer;
import xscript.compiler.standart.XParser;
import xscript.compiler.token.XLineDesk;
import xscript.compiler.tree.XTree;
import xscript.compiler.tree.XTree.XTreeAnnotation;
import xscript.compiler.tree.XTree.XTreeAnnotationEntry;
import xscript.compiler.tree.XTree.XTreeArrayInitialize;
import xscript.compiler.tree.XTree.XTreeAssert;
import xscript.compiler.tree.XTree.XTreeBlock;
import xscript.compiler.tree.XTree.XTreeBreak;
import xscript.compiler.tree.XTree.XTreeCase;
import xscript.compiler.tree.XTree.XTreeCast;
import xscript.compiler.tree.XTree.XTreeCatch;
import xscript.compiler.tree.XTree.XTreeClassDecl;
import xscript.compiler.tree.XTree.XTreeClassFile;
import xscript.compiler.tree.XTree.XTreeCompiledPart;
import xscript.compiler.tree.XTree.XTreeConstant;
import xscript.compiler.tree.XTree.XTreeContinue;
import xscript.compiler.tree.XTree.XTreeDo;
import xscript.compiler.tree.XTree.XTreeError;
import xscript.compiler.tree.XTree.XTreeFor;
import xscript.compiler.tree.XTree.XTreeForeach;
import xscript.compiler.tree.XTree.XTreeGroup;
import xscript.compiler.tree.XTree.XTreeIdent;
import xscript.compiler.tree.XTree.XTreeIf;
import xscript.compiler.tree.XTree.XTreeIfOperator;
import xscript.compiler.tree.XTree.XTreeImport;
import xscript.compiler.tree.XTree.XTreeIndex;
import xscript.compiler.tree.XTree.XTreeInstanceof;
import xscript.compiler.tree.XTree.XTreeLable;
import xscript.compiler.tree.XTree.XTreeLambda;
import xscript.compiler.tree.XTree.XTreeMethodCall;
import xscript.compiler.tree.XTree.XTreeMethodDecl;
import xscript.compiler.tree.XTree.XTreeModifier;
import xscript.compiler.tree.XTree.XTreeNew;
import xscript.compiler.tree.XTree.XTreeNewArray;
import xscript.compiler.tree.XTree.XTreeOperatorPrefixSuffix;
import xscript.compiler.tree.XTree.XTreeOperatorStatement;
import xscript.compiler.tree.XTree.XTreeReturn;
import xscript.compiler.tree.XTree.XTreeSuper;
import xscript.compiler.tree.XTree.XTreeSwitch;
import xscript.compiler.tree.XTree.XTreeSynchronized;
import xscript.compiler.tree.XTree.XTreeThis;
import xscript.compiler.tree.XTree.XTreeThrow;
import xscript.compiler.tree.XTree.XTreeTry;
import xscript.compiler.tree.XTree.XTreeType;
import xscript.compiler.tree.XTree.XTreeTypeParam;
import xscript.compiler.tree.XTree.XTreeVarDecl;
import xscript.compiler.tree.XTree.XTreeVarDecls;
import xscript.compiler.tree.XTree.XTreeWhile;
import xscript.compiler.tree.XVisitor;


public final class PCws_AutoCompleteHelper implements XVisitor {

	public static void makeComplete(PC_GresComponent component, PC_GresDocument document, PC_GresDocumentLine line, int x, PC_AutoCompleteDisplay info) {
		XMessageList messages = new XMessageList() {
			@SuppressWarnings("hiding")
			@Override
			public void postMessage(XMessageLevel level, String message, XLineDesk line, Object[] args) {/**/}
		};
		XParser parser = new XParser(new XLexer(document.getWholeText(), messages), messages);
		XTree tree = parser.makeTree();
		tree.accept(new PCws_AutoCompleteHelper());
	}

	private PCws_AutoCompleteHelper(){
		
	}
	
	@Override
	public void visitAnnotation(XTreeAnnotation arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitAnnotationEntry(XTreeAnnotationEntry arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitArrayInitialize(XTreeArrayInitialize arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitAssert(XTreeAssert arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitBlock(XTreeBlock arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitBreak(XTreeBreak arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitCase(XTreeCase arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitCast(XTreeCast arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitCatch(XTreeCatch arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitClassDecl(XTreeClassDecl arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitCompiled(XTreeCompiledPart arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitConstant(XTreeConstant arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitContinue(XTreeContinue arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitDo(XTreeDo arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitError(XTreeError arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitFor(XTreeFor arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitForeach(XTreeForeach arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitGroup(XTreeGroup arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitIdent(XTreeIdent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitIf(XTreeIf arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitIfOperator(XTreeIfOperator arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitImport(XTreeImport arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitIndex(XTreeIndex arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitInstanceof(XTreeInstanceof arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitLable(XTreeLable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitLambda(XTreeLambda arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitMethodCall(XTreeMethodCall arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitMethodDecl(XTreeMethodDecl arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitModifier(XTreeModifier arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitNew(XTreeNew arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitNewArray(XTreeNewArray arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitOperator(XTreeOperatorStatement arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitOperatorPrefixSuffix(XTreeOperatorPrefixSuffix arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitReturn(XTreeReturn arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitSuper(XTreeSuper arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitSwitch(XTreeSwitch arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitSynchronized(XTreeSynchronized arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitThis(XTreeThis arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitThrow(XTreeThrow arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitTopLevel(XTreeClassFile arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitTry(XTreeTry arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitType(XTreeType arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitTypeParam(XTreeTypeParam arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitVarDecl(XTreeVarDecl arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitVarDecls(XTreeVarDecls arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitWhile(XTreeWhile arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
