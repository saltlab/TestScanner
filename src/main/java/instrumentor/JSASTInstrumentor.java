package instrumentor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.Block;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.ReturnStatement;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.Scope;
import org.mozilla.javascript.ast.NewExpression;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.ObjectProperty;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.ElementGet;
import org.mozilla.javascript.ast.VariableInitializer;
import org.mozilla.javascript.ast.SwitchStatement;
import org.mozilla.javascript.ast.SwitchCase;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.DoLoop;
import org.mozilla.javascript.ast.WhileLoop;



/**
 * This class is used to visit AST nodes of the given JS code. When a node matches a certain condition, it will be instrumented with a wrapper function.
 */

public class JSASTInstrumentor implements NodeVisitor{

	private String visitType = "";  // {"InstrumentTestCode", "AnalyzeProductionCode", "AnalyzeTestCode"}

	private int instrumentedLinesCounter = 0;

	private CompilerEnvirons compilerEnvirons = new CompilerEnvirons();
	private String scopeName = null;	// Contains the scopename of the AST we are visiting. Generally this will be the filename
	protected String jsName = null;		//To store js corresponding name

	private int currentTestNumber = 0;
	private String currentTest = "";
	private int testCounter = 0;
	private int asyncTestCounter = 0;
	private int testModuleCounter = 0;
	private int assertionCounter = 0;
	private int newExpressionCounter = 0;
	private int triggerCounetr = 0;

	private ArrayList<FunctionInfo> FunctionInfoList = new ArrayList<FunctionInfo>();

	private ArrayList<TestCaseInfo> testCaseInfoList = new ArrayList<TestCaseInfo>();
	public ArrayList<TestCaseInfo> getTestCaseInfoList() {
		return testCaseInfoList;
	}
	private ArrayList<TestModuleInfo> testModuleInfoList = new ArrayList<TestModuleInfo>();
	public ArrayList<TestModuleInfo> getTestModuleInfoList() {
		return testModuleInfoList;
	}
	private ArrayList<TestUtilityFunctionInfo> testUtilityFunctionInfoList = new ArrayList<TestUtilityFunctionInfo>();
	public ArrayList<TestUtilityFunctionInfo> getTestUtilityFunctionInfoList() {
		return testUtilityFunctionInfoList;
	}


	private ArrayList<Integer> coveredStatementLines = new ArrayList<Integer>();
	private ArrayList<Integer> missedStatementLines = new ArrayList<Integer>();
	public ArrayList<Integer> getMissedStatementLines() {
		return missedStatementLines;
	}
	private ArrayList<Integer> missedStatementInMissedFunction = new ArrayList<Integer>();
	public ArrayList<Integer> getMissedStatementInMissedFunction() {
		return missedStatementInMissedFunction;
	}
	private ArrayList<Integer> coveredFunctionsIndices = new ArrayList<Integer>();
	private ArrayList<String> coveredFunctions = new ArrayList<String>();
	public ArrayList<String> getCoveredFunctions() {
		return coveredFunctions;
	}
	private ArrayList<String> coveredFunctionsLoc = new ArrayList<String>();
	public ArrayList<String> getCoveredFunctionsLoc() {
		return coveredFunctionsLoc;
	}

	private ArrayList<String> missedFunctions = new ArrayList<String>();
	public ArrayList<String> getMissedFunctions() {
		return missedFunctions;
	}
	private ArrayList<String> missedFunctionsLoc = new ArrayList<String>();
	public ArrayList<String> getMissedFunctionsLoc() {
		return missedFunctionsLoc;
	}

	private ArrayList<Integer> coveredFunctionLines = new ArrayList<Integer>();
	public ArrayList<Integer> getCoveredFunctionLines() {
		return coveredFunctionLines;
	}
	private ArrayList<Integer> missedFunctionLines = new ArrayList<Integer>();
	public ArrayList<Integer> getMissedFunctionLines() {
		return missedFunctionLines;
	}

	private ArrayList<String> functionCalls = new ArrayList<String>();

	public int getNumAssertInTests() {
		int num = 0;
		for (TestCaseInfo tc: testCaseInfoList)
			num += tc.getNumAssertions();
		return num;
	}

	public ArrayList<String> getFunctionCallsInTests() {
		ArrayList<String> funCalls = new ArrayList<String>();
		for (TestCaseInfo tc: testCaseInfoList){
			for (String fc: tc.getFunctionCalls())
				// should also consider repeated fun calls so removed if(!funCalls.contains(fc)) 
				funCalls.add(fc);
		}
		return funCalls;
	}

	public int getFunCallCounterInTest() {
		return getFunctionCallsInTests().size();
	}

	public ArrayList<String> getFunctionCallsInTestModules() {
		ArrayList<String> funCalls = new ArrayList<String>();
		for (TestModuleInfo tc: testModuleInfoList){
			for (String fc: tc.getFunctionCalls())
				funCalls.add(fc);
		}
		return funCalls;
	}
	public int getFunCallCounterInTestModule() {
		return getFunctionCallsInTestModules().size();
	}


	public int getMaxFunctionCallsInTests() {
		int max = 0;
		for (TestCaseInfo tc: testCaseInfoList)
			if (tc.getFunctionCalls().size() > max)
				max = tc.getFunctionCalls().size();
		return max;
	}


	public ArrayList<String> getFunctionCalls() {
		return functionCalls;
	}



	private int functionCounter = 0;

	private String visitOnly = "";

	private int coveredEventCallback = 0;
	public int getCoveredEventCallback() {
		return coveredEventCallback;
	}

	private int missedEventCallback = 0;
	public int getMissedEventCallback() {
		return missedEventCallback;
	}

	private int coveredAsyncCallback = 0;
	public int getCoveredAsyncCallback() {
		return coveredAsyncCallback;
	}

	private int missedAsyncCallback = 0;
	public int getMissedAsyncCallback() {
		return missedAsyncCallback;
	}

	private int coveredCallback = 0;
	public int getCoveredCallback() {
		return coveredCallback;
	}

	private int missedCallback = 0;
	public int getMissedCallback() {
		return missedCallback;
	}

	private int coveredClosure = 0;
	public int getCoveredClosure() {
		return coveredClosure;
	}

	private int missedClosure = 0;
	public int getMissedClosure() {
		return missedClosure;
	}

	private int coveredRegularFunc = 0;
	public int getCoveredRegularFunc() {
		return coveredRegularFunc;
	}

	// DOM Access Coverage
	private ArrayList<Integer> coveredDOMRelatedLines = new ArrayList<Integer>();
	public ArrayList<Integer> getCoveredDOMRelatedLines() {
		return coveredDOMRelatedLines;
	}
	private ArrayList<Integer> missedDOMRelatedLines = new ArrayList<Integer>();
	public ArrayList<Integer> getMissedDOMRelatedLines() {
		return missedDOMRelatedLines;
	}

	ArrayList<String> DOMReturningFunction = new ArrayList<String>();  // storing functions that return a DOM element/attribute. 
	ArrayList<DOMVariableInfo> DOMVariableInfoList = new ArrayList<DOMVariableInfo>();

	private int missedRegularFunc = 0;
	public int getMissedRegularFunc() {
		return missedRegularFunc;
	}

	private String testsFramework;


	public void setVisitOnly(String visitOnly){
		this.visitOnly = visitOnly;
	}

	public JSASTInstrumentor(){
	}

	/**
	 * @param scopeName
	 *            the scopeName to set
	 */
	public void setScopeName(String scopeName) {
		this.scopeName = scopeName;
		//This is used to name the array which stores execution count for the scope in URL 
		int index = scopeName.lastIndexOf('/');
		String s = scopeName.substring(index+1, scopeName.length());
		jsName = s.replace('.', '_');
	}

	/**
	 * @return the scopeName
	 */
	public String getScopeName() {
		return scopeName;
	}

	/**
	 * Parse some JavaScript to a simple AST.
	 * 
	 * @param code
	 *            The JavaScript source code to parse.
	 * @return The AST node.
	 */
	public AstNode parse(String code) {
		//Parser p = new Parser(compilerEnvirons, null);

		compilerEnvirons.setErrorReporter(new ConsoleErrorReporter());
		Parser p = new Parser(compilerEnvirons, new ConsoleErrorReporter());

		//System.out.print(code+"*******\n");
		return p.parse(code, null, 0);
	}

	/**
	 * Find out the function name of a certain node and return "anonymous" if it's an anonymous function.
	 * 
	 * @param f
	 *            The function node.
	 * @return The function name.
	 */
	protected String getFunctionName(FunctionNode f) {
		// http://www.bryanbraun.com/2014/11/27/every-possible-way-to-define-a-javascript-function
		if (f==null)
			return "NoFunctionNode";
		else if(f.getParent() instanceof ObjectProperty){
			return ((ObjectProperty)f.getParent()).getLeft().toSource();
		}else if(f.getParent() instanceof Assignment){
			Assignment asmt = (Assignment)f.getParent();
			String varName = asmt.getLeft().toSource();
			//System.out.println("**** Variable:" + varName + " is set to: " + asmt.getRight().toSource());
			return varName;
		}else if(f.getParent() instanceof InfixExpression){
			if(f.getParent().getParent() instanceof Assignment){
				Assignment asmt = (Assignment)f.getParent().getParent();
				String varName = asmt.getLeft().toSource();
				//System.out.println("**** Variable:" + varName + " is set to: " + asmt.getRight().toSource());
				return varName;
			}
		}

		Name functionName = f.getFunctionName();

		if (functionName == null) {
			return "anonymous" + (f.getLineno()+1);
		} else {
			return functionName.toSource();
		}
	}

	/**
	 * Create a new block node with two children.
	 * 
	 * @param node
	 *            The child.
	 * @return The new block.
	 */
	private Block createBlockWithNode(AstNode node) {
		Block b = new Block();
		b.addChild(node);
		return b;
	}


	/**
	 * Actual visiting method.
	 * 
	 * @param node
	 *            The node that is currently visited.
	 * @return Whether to visit the children.
	 */
	public boolean visit(AstNode node) {	

		//System.out.println("visit");
		String nodeName = node.shortName();
		int nodeType = node.getType();
		int nodeDepth = node.depth();

		//System.out.println("node.shortName() : " + nodeName);
		//System.out.println("node.getLineno() : " + (node.getLineno()+1));

		//System.out.println(coveredStatementLines);
		if (!coveredStatementLines.contains(node.getLineno()+1)){
			//System.out.println("node.shortName() : " + nodeName);
			//System.out.println("node.getLineno() : " + (node.getLineno()+1));
			//System.out.println("node.depth() : " + nodeDepth);
			//System.out.println("node.toSource() : \n" + node.toSource());
			//System.out.println("node.getType() : " + node.getType());
			//System.out.println("node.debugPrint() : \n" + node.debugPrint());
		}


		// {"InstrumentTestCode", "AnalyzeProductionCode", "AnalyzeTestCode"}
		if (visitType.equals("InstrumentTestCode")){
			if (node instanceof NewExpression)
				newExpressionCounter++;
			else if (node instanceof FunctionCall)
				instrumentFunctionCallNode(node);
			else if (node instanceof FunctionNode)
				instrumentFunctionNode(node);
		}else if (visitType.equals("AnalyzeProductionCode")){

			if (visitOnly.equals("FunctionNode")){
				if (node instanceof FunctionNode)
					analyzeProductionCodeFunctionNode(node);
			}else if (visitOnly.equals("FunctionCall")){
				if (node instanceof FunctionCall)
					analyzeProductionCodeFunctionCallNode(node);
				else if (node instanceof Assignment){
					analyzeProductionCodeAssignmentNode(node);
				}
			}else if (visitOnly.equals("Variables")){
				if (node instanceof Name || node instanceof PropertyGet || node instanceof ElementGet)
					analyzeDOMRelatedSlice(node);
			}
		}else if (visitType.equals("AnalyzeTestCode")){
			if (visitOnly.equals("FunctionNode")){
				if (node instanceof FunctionNode)
					analyzeTestCodeFunctionNode(node);
			}else{
				if (node instanceof NewExpression)
					newExpressionCounter++;
				else if (node instanceof FunctionCall)
					analyzeTestCodeFunctionCallNode(node);
			}

		}else{
			System.out.println("visitType is not set!");
		}

		/* have a look at the children of this node */
		return true;
	}


	private void analyzeProductionCodeFunctionNode(AstNode node) {
		FunctionNode f = (FunctionNode) node;
		int numOfParam = f.getParams().size();
		int lineNumber = node.getLineno()+1;
		int fLength = f.getEndLineno() - f.getLineno();	
		//int fDepth = node.depth();
		String funcLocation = "regular";
		String functionName = getFunctionName(f);

		// adding to the list of functions
		FunctionInfo fi = new FunctionInfo(functionName, f.getLineno(), f.getEndLineno());
		for (int i=0; i<f.getParams().size(); i++){
			fi.addParam(f.getParams().get(i).toSource());
		}
		FunctionInfoList.add(fi);

		//System.out.println("Function name: " + functionName);
		AstNode parentNode = node.getParent();
		System.out.println("lineNumber: " + lineNumber);

		try{
			String parentNodeSource = parentNode.toSource();
		}catch (Exception e){
			System.out.println(e);
		}

		String parentNodeName = parentNode.shortName();
		//System.out.println("shortName: " + shortName);
		//System.out.println("parentNodeName: " + parentNodeName);

		String enclosingFunction = "";
		if (node.getEnclosingFunction()!=null){
			enclosingFunction  = getFunctionName(node.getEnclosingFunction());
		}
		//System.out.println("enclosingFunction: " + enclosingFunction);

		boolean covered = false;

		if (coveredFunctionsIndices==null){  
			// this is the case for lcov report that we don't have coveredFunctionsIndices and instead have missedFunctionsLines
			// check if lineNumber of function equals a missedFunctionsLines
			if (missedFunctionLines.contains(lineNumber)){
				if (!missedFunctions.contains(functionName))
					missedFunctions.add(functionName);
				System.out.println("======== Missed function at line" + lineNumber + " - Function name: " + functionName);
				//System.out.println("Missed function from line " + lineNumber + " to " + (f.getEndLineno()+1));
				// fill missedStatementInMissedFunction array with corresponding function index value
				int missedStatementLinescounter = 0;
				for (int i=0; i<missedStatementLines.size(); i++){
					if (missedStatementLines.get(i) >= (f.getLineno()+1) && missedStatementLines.get(i) <= (f.getEndLineno()+1)){
						missedStatementInMissedFunction.set(i, functionCounter);
						//System.out.println("Missed statement line " + missedStatementLines.get(i) + " belongs to missed function " + functionCounter);
						missedStatementLinescounter++;
					}
				}
			}else{
				covered = true;
				if (!coveredFunctions.contains(functionName))
					coveredFunctions.add(functionName);
				if (!coveredFunctionLines.contains(lineNumber))
					coveredFunctionLines.add(lineNumber);
				coveredFunctionsLoc.add(funcLocation);
				System.out.println("======== Covered function at line" + lineNumber + " - Function name: " + functionName);
			}

		}else{
			// this is the case for json report format
			if (coveredFunctionsIndices.contains(functionCounter)){
				if (!coveredFunctions.contains(functionName))
					coveredFunctions.add(functionName);
				if (!coveredFunctionLines.contains(lineNumber))
					coveredFunctionLines.add(lineNumber);
				coveredFunctionsLoc.add(funcLocation);
				System.out.println("======== Covered function at line" + lineNumber + " - Function name: " + functionName);
				covered = true;
			}else{
				if (!missedFunctions.contains(functionName))
					missedFunctions.add(functionName);
				if (!missedFunctionLines.contains(lineNumber))
					missedFunctionLines.add(lineNumber);
				System.out.println("======== Missed function at line" + lineNumber + " - Function name: " + functionName);
				//System.out.println("Missed function from line " + (f.getLineno()+1) + " to " + (f.getEndLineno()+1));

				// fill missedStatementInMissedFunction array with corresponding function index value
				int missedStatementLinescounter = 0;
				for (int i=0; i<missedStatementLines.size(); i++){
					if (missedStatementLines.get(i) >= (f.getLineno()+1) && missedStatementLines.get(i) <= (f.getEndLineno()+1)){
						missedStatementInMissedFunction.set(i, functionCounter);
						//System.out.println("Missed statement line " + missedStatementLines.get(i) + " belongs to missed function " + functionCounter);
						missedStatementLinescounter++;
					}
				}
				//System.out.println("missedStatementLinescounter = " + missedStatementLinescounter);
				//System.out.println("missedStatementLines.size() = " + missedStatementLines.size());
				//System.out.println("Ratio of total missed statement lines = " + missedStatementLinescounter/missedStatementLines.size());

			}
		}
		functionCounter++;

		if (parentNodeName.equals("ParenthesizedExpression")){
			System.out.println("This is an immediately invoked function, just ignore it!");
			//This is an immediately invoked function, just ignore it!
		}else if (parentNodeName.equals("FunctionCall")){
			FunctionCall parentNodeFunctionCall = (FunctionCall) parentNode;
			AstNode targetNode = parentNodeFunctionCall.getTarget();
			String targetSource = "";
			try{
				targetSource = targetNode.toSource();
			}catch(Exception e){
				System.out.println(e);
			}
			//System.out.println("targetSource: ++++++++" + targetSource);

			// check for callback
			boolean callbackFound = false;
			for (AstNode n : parentNodeFunctionCall.getArguments()){
				if (n.shortName().equals("FunctionNode") && getFunctionName((FunctionNode) n).equals(functionName)){
					//System.out.println("Callback function passed as an argument to function " + targetSource);
					if (isEventMethod(targetSource)){
						if (covered){
							System.out.println("Covered event-dependent callback at line " + (node.getLineno()+1)); // + " for function " + parentNode.toSource());
							coveredEventCallback++;
						}else{
							System.out.println("Missed event-dependent callback at line " + (node.getLineno()+1)); // + " for function " + parentNode.toSource());
							missedEventCallback++;
						}
					}else if (isAsyncMethod(targetSource)){
						if (covered){
							System.out.println("Covered async callback at line " + (node.getLineno()+1)); // + " for function " + parentNode.toSource());
							coveredAsyncCallback++;
						}else{
							System.out.println("Missed async callback at line " + (node.getLineno()+1)); // + " for function " + parentNode.toSource());
							missedAsyncCallback++;
						}
					}else{
						if (covered){
							System.out.println("Covered callback at line " + (node.getLineno()+1)); // + " for function " + parentNode.toSource());
							coveredCallback++;
						}else{
							System.out.println("Missed callback at line " + (node.getLineno()+1)); // + " for function " + parentNode.toSource());
							missedCallback++;
						}
					}
					callbackFound = true;
					funcLocation = "callback";
				}
			}
		}else if (parentNodeName.equals("Block")){
			//System.out.println("enclosingFunction: " + enclosingFunction);
			AstNode parentParentNode = parentNode.getParent();
			try{
				String parentParentNodeSource = parentParentNode.toSource();
			}catch (Exception e){
				System.out.println(e);
			}
			String parentParentNodeName = parentParentNode.shortName();
			//System.out.println("shortName: " + shortName);
			//System.out.println("parentParentNodeName: " + parentParentNodeName);

			// this is a closure (nested function)
			if (covered){
				System.out.println("Covered function closure at line " + (node.getLineno()+1)); // + " for function " + parentNodeSource);
				coveredClosure++;
			}else{
				System.out.println("Missed function closure at line " + (node.getLineno()+1)); // + " for function " + parentNode.toSource());
				missedClosure++;
			}

		}else{
			// this is a regular function
			if (covered){
				System.out.println("Covered regular function at line " + (node.getLineno()+1)); // + " for function " + parentNodeSource);
				coveredRegularFunc++;
			}else{
				System.out.println("Missed regular function at line " + (node.getLineno()+1)); // + " for function " + parentNode.toSource());
				missedRegularFunc++;
			}
		}



	}

	private void analyzeTestCodeFunctionNode(AstNode node) {
		// Functions in test files are considered as test utility functions
		FunctionNode f = (FunctionNode) node;
		String functionName = getFunctionName(f);
		if (!functionName.contains("anonymous")){
			TestUtilityFunctionInfo tufi = new TestUtilityFunctionInfo(functionName);
			testUtilityFunctionInfoList.add(tufi);
			System.out.println("Test utility function name: " + functionName);
		}
		AstNode parentNode = node.getParent();
		//String parentNodeSource = parentNode.toSource();
		//String parentNodeName = parentNode.shortName();
		//System.out.println("shortName: " + shortName);
		//System.out.println("parentNodeName: " + parentNodeName);

		String enclosingFunction = "";
		if (node.getEnclosingFunction()!=null){
			enclosingFunction  = getFunctionName(node.getEnclosingFunction());
		}
		//System.out.println("enclosingFunction: " + enclosingFunction);
	}

	private void analyzeProductionCodeFunctionCallNode(AstNode node) {
		/**
		 * A function call can be
		 * 1) Regular: can be inside a function or global scope
		 * 2) Callback function call: inside a function and the name of function is an argument of the enclosing function
		 * Callbacks can be event-dependent, i.e., the enclosing function is an event function such as on() or click()
		 */
		FunctionCall fcall = (FunctionCall) node;
		AstNode targetNode = fcall.getTarget(); // node evaluating to the function to call. E.g document.getElemenyById(x)
		String targetSource = "";
		try{		
			targetSource = targetNode.toSource();
		}catch (Exception e){
			System.out.println(e);
		}
		//System.out.println("Calling function " + targetSource);
		//System.out.println("node.getLineno() : " + (node.getLineno()+1));

		String enclosingFunction = "";
		if (node.getEnclosingFunction()!=null){
			enclosingFunction  = getFunctionName(node.getEnclosingFunction());
			//System.out.println("enclosingFunction: " + enclosingFunction);
		}

		if (!targetSource.contains("{"))  // ignoring calls by immediately invoked functions
			if (!functionCalls.contains(targetSource))
				functionCalls.add(targetSource);




		// check for DOM API accessing the DOM and related statements
		HashSet<Integer> DOMRelatedLines = new HashSet<Integer>();
		if (isDOMAPIMethod(targetSource)){
			int lineNumber = node.getLineno()+1;
			System.out.println("***** DOM access at line" + lineNumber + " - DOM access: " + targetSource);
			DOMRelatedLines.add(lineNumber);

			AstNode child = fcall;
			AstNode parent = fcall.getParent();
			try{
				while (!(parent instanceof FunctionNode)){
					//System.out.println("fcall parent: " + parent.toSource());
					//System.out.println("fcall parent.shortName(): " + parent.shortName());

					if (parent instanceof IfStatement){
						// check if child is a Scope then the statement belongs to the body of the condition
						if (child instanceof Scope){ 
							//System.out.println("DOM access in a condition body");
							DOMRelatedLines.add(lineNumber);
						}else{
							//System.out.println("DOM access in a condition"); // then all the body will be affected
							int count = parent.toSource().length() - parent.toSource().replace("\n", "").replace("\r", "").length();
							//System.out.println("Condition body from line " + (parent.getLineno()+1) + " to line " + (parent.getLineno() + count - 2));
							for (int i=parent.getLineno()+1; i<=parent.getLineno() + count - 2;i++)
								DOMRelatedLines.add(i);
						}
						break;
					}else if (parent instanceof SwitchStatement){
						// check if child is a Scope then the statement belongs to the body of the condition
						if (child instanceof Scope || child instanceof SwitchCase){ 
							//System.out.println("DOM access in a SwitchStatement body");
							DOMRelatedLines.add(lineNumber);
						}else{
							//System.out.println("DOM access in a SwitchStatement condition"); // then all the body will be affected
							int count = parent.toSource().length() - parent.toSource().replace("\n", "").replace("\r", "").length();
							//System.out.println("Condition body from line " + (parent.getLineno()+1) + " to line " + (parent.getLineno() + count - 2));
							for (int i=parent.getLineno()+1; i<=parent.getLineno() + count - 2;i++)
								DOMRelatedLines.add(i);
						}
						break;
					}else if (parent instanceof ForLoop || parent instanceof DoLoop || parent instanceof WhileLoop){
						// check if child is a Scope then the statement belongs to the body of the condition
						if (child instanceof Scope){ 
							//System.out.println("DOM access in a Loop body");
							DOMRelatedLines.add(lineNumber);
						}else{
							//System.out.println("DOM access in a Loop condition"); // then all the body will be affected
							int count = parent.toSource().length() - parent.toSource().replace("\n", "").replace("\r", "").length();
							//System.out.println("Condition body from line " + (parent.getLineno()+1) + " to line " + (parent.getLineno() + count - 2));
							for (int i=parent.getLineno()+1; i<=parent.getLineno() + count - 2;i++)
								DOMRelatedLines.add(i);
						}
						break;
					}else if (parent instanceof ReturnStatement){
						DOMRelatedLines.add(parent.getLineno()+1);
						try{
							enclosingFunction = "";
							if (node.getEnclosingFunction()!=null)
								enclosingFunction  = getFunctionName(node.getEnclosingFunction());
							//System.out.println("enclosingFunction: " + enclosingFunction);
							// add the enclosing function to the DOM APIs
							if (!DOMReturningFunction.contains(enclosingFunction))
								DOMReturningFunction.add(enclosingFunction);
						}catch(Exception e){}
						break;
					}else if (parent instanceof Assignment){
						DOMRelatedLines.add(parent.getLineno()+1);
						Assignment asmt = (Assignment) parent;
						String varName = asmt.getLeft().toSource();
						//System.out.println(varName + " is set to: " + asmt.getRight().toSource());
						//System.out.println(varName + " is a: " + asmt.getLeft().shortName());
						// find the enclosing function node
						while (!(parent instanceof FunctionNode)){
							parent = parent.getParent();
						}
						FunctionNode fNode = (FunctionNode) parent;
						// add a new DOM variable
						DOMVariableInfo DV = new DOMVariableInfo(varName, lineNumber, fNode);
						DOMVariableInfoList.add(DV);
						break;
					}else if (parent instanceof VariableInitializer){
						DOMRelatedLines.add(parent.getLineno()+1);
						VariableInitializer vi = (VariableInitializer) parent;
						String varName = vi.getTarget().toSource();
						//System.out.println(varName + " is set to: " + vi.getInitializer().toSource());
						//System.out.println(varName + " is a: " + vi.getTarget().shortName());
						// find the enclosing function node
						while (!(parent instanceof FunctionNode)){
							parent = parent.getParent();
						}
						FunctionNode fNode = (FunctionNode) parent;
						// add a new DOM variable
						DOMVariableInfo DV = new DOMVariableInfo(varName, lineNumber, fNode);
						DOMVariableInfoList.add(DV);
						break;
					}else if (parent instanceof FunctionCall){
						// DOM API call as an argument
						DOMRelatedLines.add(parent.getLineno()+1);
						FunctionCall pCall = (FunctionCall) parent;
						AstNode pCallTargetNode = pCall.getTarget(); // node evaluating to the function to call. E.g document.getElemenyById(x)
						String functionName = pCallTargetNode.toSource();
						// finding which argument is the function call
						int i=0;
						for (; i<pCall.getArguments().size(); i++)
							if (pCall.getArguments().get(i) instanceof FunctionCall)
								break;
						System.out.println("Argument " + (i+1) + " of the function " + functionName + " is DOM related");
						// search in the list of functions
						for (FunctionInfo fi : FunctionInfoList){
							if (fi.getName().equals(functionName)){
								fi.getParams().get(i);
								System.out.println("Parameter " + fi.getParams().get(i) + " of function " + functionName + " is DOM related.");
								// add a new DOM variable to compute forward slice in the next AST visit for variables, etc.
								DOMVariableInfo DV = new DOMVariableInfo(fi.getParams().get(i), fi.getBeginLineNum(), fi.getEndLineNum());
								DOMVariableInfoList.add(DV);
								break;
							}
						}
						break;
					}

					child = parent;
					parent = parent.getParent();
				}
			}catch(Exception e){ System.out.println(e);}

			for (int DRL: DOMRelatedLines){
				if (coveredStatementLines.contains(DRL)){
					if (!coveredDOMRelatedLines.contains(DRL)){
						coveredDOMRelatedLines.add(DRL);
						System.out.println("======== Covered DOM related statement at line" + DRL);
					}
				}else if (missedStatementLines.contains(DRL)){
					if (!missedDOMRelatedLines.contains(DRL)){
						missedDOMRelatedLines.add(DRL);
						System.out.println("======== Missed DOM related statement at line" + DRL);
					}
				}
			}
		}		

		// check for callback and if it's an event-dependent callback
		for (AstNode n : fcall.getArguments()){
			if (n.shortName().equals("Name") && coveredFunctions.contains(n.toSource())){
				if (isEventMethod(targetSource)){
					System.out.println("***** Covered function " + n.toSource() + " is an event-dependent callback at line " + (node.getLineno()+1) + " for named function " + targetSource);
					coveredEventCallback++;
				}else if (isAsyncMethod(targetSource)){
					System.out.println("***** Covered function " + n.toSource() + " is an async callback at line " + (node.getLineno()+1) + " for named function " + targetSource);
					coveredAsyncCallback++;
				}else{
					System.out.println("***** Covered function " + n.toSource() + " is a callback at line " + (node.getLineno()+1) + " for named function " + targetSource);
					coveredCallback++;
				}
			}else if (n.shortName().equals("Name") && missedFunctions.contains(n.toSource())){
				if (isEventMethod(targetSource)){
					System.out.println("***** Missed function " + n.toSource() + " is an event-dependent callback at line " + (node.getLineno()+1) + " for named function " + targetSource);
					missedEventCallback++;
				}else if (isAsyncMethod(targetSource)){
					System.out.println("***** Missed function " + n.toSource() + " is an async callback at line " + (node.getLineno()+1) + " for named function " + targetSource);
					missedAsyncCallback++;
				}else{
					System.out.println("***** Missed function " + n.toSource() + " is a callback at line " + (node.getLineno()+1) + " for named function " + targetSource);
					missedCallback++;
				}
			}
		}

	}




	private void analyzeProductionCodeAssignmentNode(AstNode node) {

		Assignment asmt = (Assignment) node;
		String varName = asmt.getLeft().toSource();
		int lineNumber = node.getLineno()+1;
		//System.out.println(varName + " is set to: " + asmt.getRight().toSource());
		// check for pattern  X.onclick = nameOfAFunction  or X.onclick = function()
		if (isEventMethod(varName)){
			if (asmt.getRight() instanceof FunctionNode){  // e.g X.onclick = function()
				System.out.println("An event-dependent callback found at line " + lineNumber);
				missedEventCallback++;
			}else if (coveredFunctions.contains(asmt.getRight().toSource())){
				System.out.println("Covered event-dependent callback at line " + lineNumber);
				coveredEventCallback++;
			}else if (missedFunctions.contains(asmt.getRight().toSource())){
				System.out.println("Missed event-dependent callback at line " + lineNumber);
				missedEventCallback++;
			}
			//System.out.println("Event-dependent callback function: " + asmt.toSource());
		}
		// checking for DOM element changes 
		AstNode parent = node;
		if (isDOMElementAttribute(varName)){
			if (coveredStatementLines.contains(lineNumber)){
				if (!coveredDOMRelatedLines.contains(lineNumber))	coveredDOMRelatedLines.add(lineNumber);
				System.out.println("======== Covered DOM access at line" + lineNumber + " - DOM elem attribute: " + varName);
			}else if (missedStatementLines.contains(lineNumber)){
				if (!missedDOMRelatedLines.contains(lineNumber))		missedDOMRelatedLines.add(lineNumber);
				System.out.println("======== Missed DOM access at line" + lineNumber + " - DOM elem attribute: " + varName);
			}

			// this part is for DOM related slicing
			// find the enclosing function node
			while (!(parent instanceof FunctionNode)){
				if (parent == null)
					return;
				parent = parent.getParent();
			}
			FunctionNode fNode = (FunctionNode) parent;
			// add a new DOM variable
			DOMVariableInfo DV = new DOMVariableInfo(varName, lineNumber, fNode);
			DOMVariableInfoList.add(DV);

		}

	}




	private void analyzeDOMRelatedSlice(AstNode node) {
		try{

			String varName = "";
			if (node instanceof Name){
				Name name = (Name) node;
				varName = name.toSource();
			}else if (node instanceof PropertyGet){
				PropertyGet name = (PropertyGet) node;
				varName = name.toSource();
			}else if (node instanceof ElementGet){
				ElementGet name = (ElementGet) node;
				varName = name.toSource();
			}

			int lineNumber = node.getLineno()+1;
			HashSet<Integer> DOMRelatedLines = new HashSet<Integer>();
			for (DOMVariableInfo DV: DOMVariableInfoList){
				if (DV.isUsedInForwardSlice(varName, node.getLineno()+1)){
					//System.out.println(varName + " is a DOMVariable used at line: " + (node.getLineno()+1));
					AstNode child = node;
					AstNode parent = node.getParent();
					while (!(parent instanceof FunctionNode)){
						//System.out.println("node parent: " + parent.toSource());
						//System.out.println("node parent.shortName(): " + parent.shortName());

						if (parent instanceof IfStatement){
							// check if child is a Scope then the statement belongs to the body of the condition
							if (child instanceof Scope){ 
								//System.out.println("DOM access in a condition body");
								DOMRelatedLines.add(lineNumber);
							}
							else{
								//System.out.println("DOM access in a condition"); // then all the body will be affected
								int count = parent.toSource().length() - parent.toSource().replace("\n", "").replace("\r", "").length();
								//System.out.println("Condition body from line " + (parent.getLineno()+1) + " to line " + (parent.getLineno() + count - 2));
								for (int i=parent.getLineno()+1; i<=parent.getLineno() + count - 2;i++)
									DOMRelatedLines.add(i);
							}
							break;
						}else if (parent instanceof SwitchStatement){
							// check if child is a Scope then the statement belongs to the body of the condition
							if (child instanceof Scope || child instanceof SwitchCase){ 
								//System.out.println("DOM access in a SwitchStatement body");
								DOMRelatedLines.add(lineNumber);
							}
							else{
								//System.out.println("DOM access in a SwitchStatement condition"); // then all the body will be affected
								int count = parent.toSource().length() - parent.toSource().replace("\n", "").replace("\r", "").length();
								//System.out.println("Condition body from line " + (parent.getLineno()+1) + " to line " + (parent.getLineno() + count - 2));
								for (int i=parent.getLineno()+1; i<=parent.getLineno() + count - 2;i++)
									DOMRelatedLines.add(i);
							}
							break;
						}else if (parent instanceof ForLoop || parent instanceof DoLoop || parent instanceof WhileLoop){
							// check if child is a Scope then the statement belongs to the body of the condition
							if (child instanceof Scope){ 
								//System.out.println("DOM access in a Loop body");
								DOMRelatedLines.add(lineNumber);
							}
							else{
								//System.out.println("DOM access in a Loop condition"); // then all the body will be affected
								int count = parent.toSource().length() - parent.toSource().replace("\n", "").replace("\r", "").length();
								//System.out.println("Condition body from line " + (parent.getLineno()+1) + " to line " + (parent.getLineno() + count - 2));
								for (int i=parent.getLineno()+1; i<=parent.getLineno() + count - 2;i++)
									DOMRelatedLines.add(i);
							}
							break;
						}
						child = parent;
						parent = parent.getParent();
					}

					for (int DRL: DOMRelatedLines){
						if (coveredStatementLines.contains(DRL)){
							if (!coveredDOMRelatedLines.contains(DRL)){
								coveredDOMRelatedLines.add(DRL);
								System.out.println("======== Covered DOM related statement at line" + DRL);
							}
						}else if (missedStatementLines.contains(DRL)){
							if (!missedDOMRelatedLines.contains(DRL)){
								missedDOMRelatedLines.add(DRL);
								System.out.println("======== Missed DOM related statement at line" + DRL);
							}
						}
					}

				}
			}
		}catch (Exception e){
			System.out.println(e);
		}

	}


	private void instrumentFunctionNode(AstNode node) {
		FunctionNode f = (FunctionNode) node;
		int numOfParam = f.getParams().size();
		int lineNumber = node.getLineno()+1;
		int fLength = f.getEndLineno() - f.getLineno();
		int fDepth = node.depth();
		//System.out.println(f.debugPrint());
		/*for (Symbol s: f.getSymbols()){
			int sType = s.getDeclType();
			if (sType == Token.LP || sType == Token.VAR || sType == Token.LET || sType == Token.CONST){
				System.out.println("s.getName() : " + s.getName());
			}
		}*/
		//System.out.println(f.getSymbolTable());
		//System.out.println(f.getSymbols());

		System.out.println("=== instrumentFunctionNode ===");
		String fName = "";
		if (f.getFunctionName()!=null){
			fName = f.getFunctionName().getIdentifier();
			System.out.println("fName = " + fName);
		}
		System.out.println("Nothing instrumented!");

	}

	private void analyzeTestCodeFunctionCallNode(AstNode node) {
		System.out.println("=== analyzeTestCodeFunctionCallNode ===");
		// getting the enclosing function name
		String enclosingFunction = "";
		if (node.getEnclosingFunction()!=null){
			enclosingFunction  = getFunctionName(node.getEnclosingFunction());
		}
		//System.out.println("enclosingFunction: " + enclosingFunction);

		if (node.shortName().equals("NewExpression"))
			return;

		FunctionCall fcall = (FunctionCall) node;
		String functionName = "";
		AstNode targetNode = fcall.getTarget(); // node evaluating to the function to call. E.g document.getElemenyById(x)
		try{
			//System.out.println("targetNode.toSource(): " + targetNode.toSource());
			functionName = targetNode.toSource();
		}catch(Exception e){
			return;
		}
		if (functionName.contains("{")){  // ignoring calls by immediately invoked functions
			//System.out.println("ignoring calls by immediately invoked functions: " + functionName);
			return;
		}
		functionName = targetNode.toSource().substring(targetNode.toSource().lastIndexOf(".")+1);


		boolean testCall = false;
		boolean testModuleCall = false;
		if (testsFramework.equals("qunit")){
			if (targetNode.toSource().equals("QUnit.test") || targetNode.toSource().equals("test") || targetNode.toSource().equals("QUnit.asyncTest") || targetNode.toSource().equals("asyncTest")
					||
					targetNode.toSource().equals("QUnit.test.apply") || targetNode.toSource().equals("test.apply") || targetNode.toSource().equals("QUnit.asyncTest.apply") || targetNode.toSource().equals("asyncTest.apply")
					)
				testCall = true;
			else if (targetNode.toSource().equals("QUnit.module") || targetNode.toSource().equals("module"))
				testModuleCall = true;
		}else if (testsFramework.equals("jasmine") || testsFramework.equals("mocha")){
			if (targetNode.toSource().equals("it") || targetNode.toSource().equals("test.it"))
				testCall = true;
			else if (targetNode.toSource().equals("describe") || targetNode.toSource().equals("test.describe"))
				testModuleCall = true;
		}

		if (testCall == true){
			testCounter++;

			String type = "sync";
			// check if it's a sync or an async test
			if (targetNode.toSource().equals("QUnit.asyncTest") || targetNode.toSource().equals("asyncTest") || targetNode.toSource().equals("QUnit.asyncTest.apply") || targetNode.toSource().equals("asyncTest.apply") ){
				type = "async";
				asyncTestCounter++;
			}

			// add a new TestCaseInfo object
			TestCaseInfo t = new TestCaseInfo(testCounter, type);
			// find begin and end line of code for the test anonym function
			for (AstNode arg: fcall.getArguments()){
				if (arg instanceof FunctionNode){
					FunctionNode f = (FunctionNode) arg;
					t.setBeginEndLines(f.getLineno()+1, f.getEndLineno()+1);
					//System.out.println(t.containsLine(f.getLineno()+1) + " - " +  t.containsLine(f.getLineno()));
				}
			}
			// add testModule info to the test info
			if (testModuleInfoList.size()!=0){
				// retrieving the last testCaseInfo
				TestModuleInfo tmi = testModuleInfoList.get(testModuleInfoList.size()-1);
				tmi.setEndLine(fcall.getLineno());
				System.out.println("Adding Module info to test info");
				System.out.println("t.getNumFunCall(): " + t.getNumFunCall());
				System.out.println("tmi.getNumFunCall() " + tmi.getNumFunCall());
				t.addModuleStat(tmi);
				System.out.println("t.getNumFunCall(): " + t.getNumFunCall());
			}
			testCaseInfoList.add(t);
		}else if (testModuleCall == true){
			System.out.println("NEW MODULE FOUND. num = " + testModuleCounter);
			testModuleCounter++;
			TestModuleInfo tmi = new TestModuleInfo(testModuleCounter);
			// find begin and end line of code for the test anonym function
			tmi.setBeginEndLines(fcall.getLineno()+1, 0);
			testModuleInfoList.add(tmi);
		}


		if (targetNode.toSource().equals("trigger") || targetNode.toSource().equals("triggerHandler") || targetNode.toSource().equals("emit")){
			triggerCounetr++;
			return;
		}


		String[] qunitOtherSkipList = { "QUnit.module", "module", "QUnit.test", "test", "QUnit.asyncTest", "asyncTest", "jQuery", "$" , "start", "stop"}; // start/stop for asynchronous control	
		String[] jasmineOtherSkipList = { "describe", "it", "beforeEach", "afterEach", "jQuery", "$" , "start", "stop", 
				"toBe", "toBeCloseTo", "toBeDefined", "toBeFalsy", "toBeGreaterThan", "toBeGreaterThanOrEqual", "toBeLessThanOrEqual", "toBeLessThan", "toBeNaN",
				"toBeNull", "toBeTruthy", "toBeUndefined", "toContain", "toEqual", "toHaveBeenCalled", "toHaveBeenCalledWith", "toHaveBeenCalledTimes", "toMatch", 
				"toThrow", "toThrowError"}; // start/stop for asynchronous control	

		String[] qunitAssertionSkipList = { "assert.expect", "expect", "assert.equal", "equal", "assert.notEqual", "notEqual", "assert.deepEqual", "deepEqual", 
				"assert.notDeepEqual", "notDeepEqual", "assert.strictEqual", "strictEqual", "assert.notStrictEqual", "notStrictEqual", "QUnit.ok", "assert.ok", "ok", "assert.notOk", "notOk", 
				"assert.propEqual", "propEqual", "assert.notPropEqual", "notPropEqual", "assert.push", "assert.throws", "throws", "assert.async", "assert"};		

		String[] jasmineAssertionSkipList = { "expect", "assert", "should."};		

		String[] assertionSkipList = {};		
		String[] otherSkipList = {};
		if (testsFramework.equals("qunit")){
			otherSkipList = qunitOtherSkipList.clone();	
			assertionSkipList = qunitAssertionSkipList.clone();
		}else if (testsFramework.equals("jasmine")  || testsFramework.equals("mocha")){
			otherSkipList = jasmineOtherSkipList.clone();	
			assertionSkipList = jasmineAssertionSkipList.clone();
		}

		if (ArrayUtils.contains(otherSkipList, targetNode.toSource()) || ArrayUtils.contains(otherSkipList, functionName)) {
			System.out.println("Not counting the called function: " + functionName);
			return;
		}else{
			// checking line number to decide if fun call is in a test case or a module or a test utility function 
			int callLineNum = fcall.getLineno()+1;
			boolean callInTestCase = false, callInTestModule = false;
			if (testCaseInfoList.size()!=0){
				// retrieving the last testCaseInfo
				TestCaseInfo t = testCaseInfoList.get(testCaseInfoList.size()-1);
				if (t.containsLine(callLineNum))
					callInTestCase = true;
			}
			if (testModuleInfoList.size()!=0){					
				// retrieving the last testModuleInfo
				TestModuleInfo tmi = testModuleInfoList.get(testModuleInfoList.size()-1);
				if (tmi.containsLine(callLineNum))
					callInTestModule = true;
			}

			System.out.println("Counting the called function: " + functionName + " with enclosingFunction: " + enclosingFunction);

			if (callInTestCase == true){
				System.out.println("callInTestCase");
				TestCaseInfo t = testCaseInfoList.get(testCaseInfoList.size()-1);
				int currentNumFunCalls = t.getNumFunCall();

				// check if the called function is actually an assertion function
				if (ArrayUtils.contains( assertionSkipList, targetNode.toSource() )){
					assertionCounter++;
					t.setNumAssertions(t.getNumAssertions()+1);
					System.out.println("Test case " + t.getTestNumber() + " has " + t.getNumAssertions() + " assertions!");
				}else{
					// check if a test utility function exist with the same name as the functionName
					boolean testUtilFunCall = false;
					for(TestUtilityFunctionInfo tufi: testUtilityFunctionInfoList){
						if (tufi.getFuncName().equals(functionName)){
							System.out.println("The called function " + functionName + " is a test utility function with " + tufi.getNumFunCall() + " function calls! Adding to the test info...");
							currentNumFunCalls += tufi.getNumFunCall();
							System.out.println("Adding function calls in the test utility function: " + tufi.getFunctionCalls());
							for(String fc: tufi.getFunctionCalls())
								t.addFunctionCall(fc);

							// also add assertions
							t.setNumAssertions(t.getNumAssertions() + tufi.getNumAssertions());

							testUtilFunCall = true;
							break;
						}
					}
					if (testUtilFunCall==false){

						// decide if the test function is an async test in Jasmine/Mocha framework 
						if (testsFramework.equals("jasmine") || testsFramework.equals("mocha")){
							if (functionName.equals("runs") || functionName.equals("waits") || functionName.equals("waitsFor")  || functionName.equals("done")){
								asyncTestCounter++;
								t.setType("async");
							}
							else{
								t.setNumFunCall(currentNumFunCalls+1);
								t.addFunctionCall(functionName);
							}
						}else{  // in general case add one to the number of called function in the test
							t.setNumFunCall(currentNumFunCalls+1);
							t.addFunctionCall(functionName);
						}

					}
					else{
						t.setNumFunCall(currentNumFunCalls);  // do not add the call to the test utility function
					}

					System.out.println("Test case " + t.getTestNumber() + " has " + t.getNumFunCall() + " function calls!");
				}
			}else if(callInTestModule == true){
				System.out.println("callInTestModule");
				TestModuleInfo tmi = testModuleInfoList.get(testModuleInfoList.size()-1);
				int currentNumFunCalls = tmi.getNumFunCall();

				// check if the called function is actually an assertion function
				if (ArrayUtils.contains( assertionSkipList, targetNode.toSource() )){
					assertionCounter++;
					tmi.setNumAssertions(tmi.getNumAssertions()+1);
					System.out.println("Test module " + tmi.getModuleNumber() + " has " + tmi.getNumAssertions() + " assertions!");
				}else{
					// check if a test utility function exist with the same name as the functionName
					boolean testUtilFunCall = false;
					for(TestUtilityFunctionInfo tufi: testUtilityFunctionInfoList){
						if (tufi.getFuncName().equals(functionName)){
							System.out.println("The called function " + functionName + " is a test utility function with " + tufi.getNumFunCall() + " function calls! Adding to the test module info...");
							currentNumFunCalls += tufi.getNumFunCall();
							System.out.println("Adding function calls in the test utility function: " + tufi.getFunctionCalls());
							for(String fc: tufi.getFunctionCalls())
								tmi.addFunctionCall(fc);

							// also add assertions
							tmi.setNumAssertions(tmi.getNumAssertions() + tufi.getNumAssertions());

							testUtilFunCall = true;
							break;
						}
					}
					if (testUtilFunCall==false){
						tmi.setNumFunCall(currentNumFunCalls+1);
						tmi.addFunctionCall(functionName);
					}
					else{
						tmi.setNumFunCall(currentNumFunCalls);  // do not add the call to the test utility function
					}
					System.out.println("Test module " + tmi.getModuleNumber() + " has " + tmi.getNumFunCall() + " function calls!");
				}
			}else{

				// check if the called function in a test utility function is actually an assertion function
				if (ArrayUtils.contains( assertionSkipList, targetNode.toSource() )){
					// search for a test utility function with the same name as the enclosingFunction
					for(TestUtilityFunctionInfo tufi: testUtilityFunctionInfoList){
						if (tufi.getFuncName().equals(enclosingFunction)){
							tufi.setNumAssertions(tufi.getNumAssertions()+1);
							System.out.println("Test utility function " + tufi.getFuncName() + " has " + tufi.getNumAssertions() + " assertions!");
							//System.out.println("An assertion found out of a test case");
							break;
						}
					}
				}else{
					// search for a test utility function with the same name as the enclosingFunction
					for(TestUtilityFunctionInfo tufi: testUtilityFunctionInfoList){
						if (tufi.getFuncName().equals(enclosingFunction)){
							tufi.setNumFunCall(tufi.getNumFunCall()+1);
							System.out.println("Test utility function " + tufi.getFuncName() + " has " + tufi.getNumFunCall() + " function calls!");
							//System.out.println("A function call found out of a test case");
							tufi.addFunctionCall(functionName);
							System.out.println("Adding to the list of function calls for the test utility function: " + tufi.getFunctionCalls());
							break;
						}
					}
				}

			}
		}
	}



	private void instrumentFunctionCallNode(AstNode node) {
		System.out.println("=== instrumentFunctionCallNode ===");
		// getting the enclosing function name
		String enclosingFunction = "";
		if (node.getEnclosingFunction()!=null){
			enclosingFunction  = getFunctionName(node.getEnclosingFunction());
		}
		System.out.println("enclosingFunction: " + enclosingFunction);

		if (node.shortName().equals("NewExpression"))
			return;

		FunctionCall fcall = (FunctionCall) node;
		AstNode targetNode = fcall.getTarget(); // node evaluating to the function to call. E.g document.getElemenyById(x)

		// avoid instrumenting wrapper function calls!
		if (fcall.getParent().toSource().contains("funcionCallWrapper")){
			System.out.println("Not instrumenting " + fcall.getTarget().toSource() + ", because of: " + fcall.getParent().toSource());
			return;
		}

		String functionName = targetNode.toSource().substring(targetNode.toSource().lastIndexOf(".")+1);

		if (targetNode.toSource().equals("QUnit.module") || targetNode.toSource().equals("module"))
			currentTest  = "TestModule";
		if (targetNode.toSource().equals("QUnit.test") || targetNode.toSource().equals("test") || targetNode.toSource().equals("QUnit.test.apply") || targetNode.toSource().equals("test.apply")){ 
			currentTestNumber++;
			currentTest = "Test" + Integer.toString(currentTestNumber);
			setTestCounter(getTestCounter() + 1);
		}
		if (targetNode.toSource().equals("QUnit.asyncTest()") || targetNode.toSource().equals("asyncTest()") || targetNode.toSource().equals("QUnit.asyncTest().apply") || targetNode.toSource().equals("asyncTest().apply")){
			currentTestNumber++;
			currentTest = "AsynchTest" + Integer.toString(currentTestNumber);
			setAsynchTestCounter(getAsynchTestCounter() + 1);
		}

		if (targetNode.toSource().equals("trigger") || targetNode.toSource().equals("triggerHandler")  || targetNode.toSource().equals("emit"))
			setTriggerCounetr(getTriggerCounetr() + 1);

		String[] assertionSkipList = { "assert.expect", "expect", "assert.equal", "equal", "assert.notEqual", "notEqual", "assert.deepEqual", "deepEqual", 
				"assert.notDeepEqual", "notDeepEqual", "assert.strictEqual", "strictEqual", "assert.notStrictEqual", "notStrictEqual", "QUnit.ok", "assert.ok", "ok", "assert.notOk", "notOk", 
				"assert.propEqual", "propEqual", "assert.notPropEqual", "notPropEqual", "assert.push", "assert.throws", "throws", "assert.async"};		

		String[] otherSkipList = { "QUnit.module", "module", "QUnit.test", "test", "QUnit.asyncTest", "asyncTest", "jQuery", "$" , "start", "stop"}; // start/stop for asynchronous control	

		if (ArrayUtils.contains( assertionSkipList, targetNode.toSource() ))
			setAssertionCounter(getAssertionCounter() + 1);

		if (ArrayUtils.contains( assertionSkipList, targetNode.toSource() ) || ArrayUtils.contains( otherSkipList, targetNode.toSource() )) {
			System.out.println("Not instrumenting " + targetNode.toSource());
			return;
		}else
			System.out.println("Instrumenting " + functionName);

		// functionName, enclosingFunction, function
		List<AstNode> args = new ArrayList<AstNode>(fcall.getArguments());
		String wrapperCode = scopeName.replace(".js","").replace("-", "_") + "_funcionCallWrapper(\""+ functionName +"\", \"" + scopeName.replace(".js","") + "_" + currentTest + "\", " + fcall.toSource() + ")";
		System.out.println("wrapperCode : " + wrapperCode );

		AstNode wrapperNode = parse(wrapperCode);
		ExpressionStatement es = (ExpressionStatement)((AstNode) wrapperNode.getFirstChild());
		FunctionCall wrapperFunCall = (FunctionCall) es.getExpression();
		args.add(wrapperFunCall.getArguments().get(0));
		//wrapperFunCall.addArgument(fcall);
		System.out.println("Replacing functionCall: " + fcall.toSource() + " with wrapperFunCall: " + wrapperFunCall.toSource());
		fcall.setTarget(wrapperFunCall.getTarget());
		fcall.setArguments(wrapperFunCall.getArguments());			
		System.out.println("New functionCall: " + fcall.toSource());

	}

	/**
	 * Creates a node that can be inserted at a certain point in the AST root.
	 * 
	 * @param root
	 * 			The AST root that will enclose the node.
	 * @param postfix
	 * 			The postfix name.
	 * @param lineNo
	 * 			Linenumber where the node will be inserted.
	 * @param rootCount
	 * 			Unique integer that identifies the AstRoot
	 * @return The new node
	 */
	protected AstNode createNode(AstRoot root, String postfix, int lineNo, int rootCount) {
		// instrumenting out of function
		// Adds instrumentation code
		String code = jsName + "_exec_counter[" + Integer.toString(instrumentedLinesCounter) + "]++;";
		instrumentedLinesCounter++;

		return parse(code);
	}

	protected AstNode createFunctionTypeNameTrackingNode(FunctionNode callerFunc, AstNode node) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 *  create node for logging variable/function-parameters
	 */
	protected AstNode createNode(FunctionNode function, AstNode nodeForVarLog, String statementCategory) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * create node for tracking function calls
	 */
	protected AstNode createFunctionTrackingNode(FunctionNode calleeFunction, String callerName) {
		return null;
	}	

	/**
	 * This method is called when the complete AST has been traversed.
	 * 
	 * @param node
	 *            The AST root node.
	 */
	public void finish(AstRoot node) {
		// add header code
		node.addChildToFront(headerCode());
		// instrumentedLinesCounter resets to 0 for the next codes
		instrumentedLinesCounter = 0;
	}

	/**
	 * This method is called before the AST is going to be traversed.
	 */
	public void start() {
		// just to be sure that index start from 0
		instrumentedLinesCounter = 0;
	}


	/**
	 * This will be added to the beginning of the script
	 * 
	 * @return The AstNode which contains array.
	 */
	private AstNode headerCode() {
		// statement can be functionCall, assignment, return, condition, etc.
		String code = scopeName.replace(".js","").replace("-", "_") + "_funcionCallTrace = [];";

		code += "function " + scopeName.replace(".js","").replace("-", "_") + "_funcionCallWrapper(functionName, testFunction, functionBody){" +
				scopeName.replace(".js","").replace("-", "_") +"_funcionCallTrace.push({functionName: functionName, testFunction: testFunction});" +
				"return functionBody;" +
				"}";

		code += "function " + scopeName.replace(".js","").replace("-", "_") + "_getFuncionCallTrace(){" +
				"return " + scopeName.replace(".js","").replace("-", "_") + "_funcionCallTrace;" +
				"}";


		instrumentedLinesCounter = 0;

		return parse(code);
	}

	/**
	 * Creates a node that can be inserted at a certain point within a function.
	 * 
	 * @param function
	 *            The function that will enclose the node.
	 * @param postfix
	 *            The postfix function name (enter/exit).
	 * @param lineNo
	 *            Linenumber where the node will be inserted.
	 * @return The new node.
	 */
	protected AstNode createNode(FunctionNode function, String postfix, int lineNo) {
		String name = getFunctionName(function);

		// Adds instrumentation code
		String code = jsName + "_exec_counter[" + Integer.toString(instrumentedLinesCounter) + "]++;";
		instrumentedLinesCounter++;

		return parse(code);
	}

	public void resetUnitTestCounter() {
		currentTestNumber = 0;		
	}

	public int getAssertionCounter() {
		return assertionCounter;
	}

	public void setAssertionCounter(int assertionCounter) {
		this.assertionCounter = assertionCounter;
	}

	public int getNewExpressionCounter() {
		return newExpressionCounter;
	}

	public void setNewExpressionCounter(int newExpressionCounter) {
		this.newExpressionCounter = newExpressionCounter;
	}

	public int getTestCounter() {
		return testCounter;
	}

	public void setTestCounter(int testCounter) {
		this.testCounter = testCounter;
	}

	public int getTestModuleCounter() {
		return testModuleCounter;
	}

	public void setTestModuleCounter(int testModuleCounter) {
		this.testModuleCounter = testModuleCounter;
	}

	public int getAsynchTestCounter() {
		int num = 0;
		for (TestCaseInfo tc: testCaseInfoList)
			if (tc.getType().equals("async"))
				num++;
		return num;
		//return asyncTestCounter;
	}

	public void setAsynchTestCounter(int asynchTestCounter) {
		this.asyncTestCounter = asynchTestCounter;
	}

	public int getTriggerCounetr() {
		return triggerCounetr;
	}

	public void setTriggerCounetr(int triggerCounetr) {
		this.triggerCounetr = triggerCounetr;
	}

	public String getVisitType() {
		return this.visitType;
	}

	public void setVisitType(String visitType) {
		this.visitType = visitType;
	}

	public void setCoverageInfo(ArrayList<Integer> coveredStatementLines, ArrayList<Integer> missedStatementLines, ArrayList<Integer> coveredFunctionsIndices, ArrayList<Integer> missedFunctionLines) {
		this.coveredStatementLines = coveredStatementLines;
		this.missedStatementLines = missedStatementLines;
		this.coveredFunctionsIndices = coveredFunctionsIndices;

		if (missedFunctionLines!=null){
			this.missedFunctionLines = missedFunctionLines;
		}

		for (int i=0; i< missedStatementLines.size(); i++)
			missedStatementInMissedFunction.add(-1);

	}

	public int getFunctionCounter() {
		return functionCounter;
	}

	public void setFunctionCounter(int functionCounter) {
		this.functionCounter = functionCounter;
	}

	public void clearFunctionsList() {
		coveredFunctions.clear();
		missedFunctions.clear();
	}

	public boolean isDOMAPIMethod(String functionName){

		String[] DOMAccessMethods = { 
				".getElementById", ".getElementsByTagName", ".getElementsByClassName", ".getElementsByName",
				".createElement", ".removeChild", ".appendChild", ".replaceChild", ".addEventListener", ".appendChild", ".adoptNode", 
				".blur", ".click",  ".createAttribute", ".createElement", ".createTextNode", ".insertBefore", ".querySelector", ".querySelectorAll", 
				".removeAttribute", ".removeAttributeNode", ".removeChild", ".replaceChild", ".removeEventListener", ".setAttribute", ".setAttributeNode", ".getAttribute",			 				".dblclick", ".hover", ".mouseout", ".mouseover", ".scroll", ".select", ".submit", ".toggle", ".trigger", ".triggerHandler",
				".onclick", ".ondblclick", ".onmouseover", ".onmouseout", ".onselect", ".onsubmit", ".ondrag", ".ondragover",
				".addClass", ".removeClass", ".removeAttr", ".css", ".attr", ".prop", ".append", ".appendTo", ".prepend", ".prependTo", 
				".insertBefore", ".insertAfter", ".detach", ".remove", ".html"
		};		

		for (String pattern: DOMAccessMethods)
			if (functionName.endsWith(pattern))
				return true;

		if (functionName.equals("jQuery") || functionName.equals("$"))
			return true;

		if (DOMReturningFunction.contains(functionName)){
			System.out.println(functionName + " is a DOM returning function!");
			return true;
		}

		return false;
	}

	public boolean isDOMElementAttribute(String functionName){
		String[] DOMElemAtt = { ".innerHTML", ".attribute", ".onclick", ".anchors", ".documentElement", ".forms", ".head", ".images", ".links"};		
		for (String pattern: DOMElemAtt)
			if (functionName.endsWith(pattern))
				return true;

		if (functionName.contains(".style."))
			return true;

		return false;
	}


	public boolean isEventMethod(String functionName){
		/**
		jQuery Event Methods
		Event methods trigger or attach a function to an event handler for the selected elements.

		bind() 	Attaches event handlers to elements
		blur() 	Attaches/Triggers the blur event
		change() 	Attaches/Triggers the change event
		click() 	Attaches/Triggers the click event
		dblclick() 	Attaches/Triggers the double click event
		delegate() 	Attaches a handler to current, or future, specified child elements of the matching elements
		error() 	Deprecated in version 1.8. Attaches/Triggers the error event
		focus() 	Attaches/Triggers the focus event
		focusin() 	Attaches an event handler to the focusin event
		focusout() 	Attaches an event handler to the focusout event
		hover() 	Attaches two event handlers to the hover event
		keydown() 	Attaches/Triggers the keydown event
		keypress() 	Attaches/Triggers the keypress event
		keyup() 	Attaches/Triggers the keyup event
		live() 	Removed in version 1.9. Adds one or more event handlers to current, or future, selected elements
		load() 	Deprecated in version 1.8. Attaches an event handler to the load event
		mousedown() 	Attaches/Triggers the mousedown event
		mouseenter() 	Attaches/Triggers the mouseenter event
		mouseleave() 	Attaches/Triggers the mouseleave event
		mousemove() 	Attaches/Triggers the mousemove event
		mouseout() 	Attaches/Triggers the mouseout event
		mouseover() 	Attaches/Triggers the mouseover event
		mouseup() 	Attaches/Triggers the mouseup event
		on() 	Attaches event handlers to elements
		one() 	Adds one or more event handlers to selected elements. This handler can only be triggered once per element
		ready() 	Specifies a function to execute when the DOM is fully loaded
		resize() 	Attaches/Triggers the resize event
		scroll() 	Attaches/Triggers the scroll event
		select() 	Attaches/Triggers the select event
		submit() 	Attaches/Triggers the submit event
		toggle() 	Removed in version 1.9. Attaches two or more functions to toggle between for the click event
		trigger() 	Triggers all events bound to the selected elements
		triggerHandler() 	Triggers all functions bound to a specified event for the selected elements
		unload() 	Deprecated in version 1.8. Attaches an event handler to the unload event


		Node.js EventEmitter => emit()  : .emit(event, data, ...)
		 **/

		String[] eventMethods = { ".bind", ".blur", ".change", ".click", ".dblclick", ".delegate", ".error", ".focus", 
				".focusin", ".focusout", ".hover", ".keydown", ".keypress", ".keyup", ".live", ".load", ".mousedown", ".mouseenter", ".mouseleave", ".mousemove",  
				".mouseout", ".mouseover", ".mouseup", ".on", ".one", ".ready", ".resize", ".scroll", ".select", ".submit", ".toggle", ".trigger", ".triggerHandler", ".unload",
				".emit",

				".addEventListener", ".attachEvent",

				".onclick", ".oncontextmenu", ".ondblclick", ".onmousedown", ".onmouseenter", ".onmouseleave", ".onmousemove", ".onmouseover", ".onmouseout", ".onmouseup", 
				".onabort", ".onbeforeunload", ".onerror", ".onhashchange", ".onload", ".onpageshow", ".onpagehide", ".onresize", ".onscroll", ".onunload", ".onblur", ".onchange", 
				".onfocus", ".onfocusin", ".onfocusout", ".oninput", ".oninvalid", ".onreset", ".onsearch", ".onselect", ".onsubmit", ".ondrag", ".ondragend", ".ondragenter", ".ondragleave", 
				".ondragover", ".ondragstart", ".ondrop", ".oncopy", ".oncut", ".onpaste", ".onerror", ".onmessage", ".onopen", ".ontouchcancel", ".ontouchend", ".ontouchmove", ".ontouchstart"
		};		


		for (String pattern: eventMethods)
			if (functionName.endsWith(pattern))
				return true;

		return false;

		/*
		 * HTML DOM Events => http://www.w3schools.com/jsref/dom_obj_event.asp
			".onclick", ".oncontextmenu", ".ondblclick", ".onmousedown", ".onmouseenter", ".onmouseleave", ".onmousemove", ".onmouseover", ".onmouseout", ".onmouseup", 
			".onabort", ".onbeforeunload", ".onerror", ".onhashchange", ".onload", ".onpageshow", ".onpagehide", ".onresize", ".onscroll", ".onunload", ".onblur", ".onchange", 
			".onfocus", ".onfocusin", ".onfocusout", ".oninput", ".oninvalid", ".onreset", ".onsearch", ".onselect", ".onsubmit", ".ondrag", ".ondragend", ".ondragenter", ".ondragleave", 
			".ondragover", ".ondragstart", ".ondrop", ".oncopy", ".oncut", ".onpaste", ".onerror", ".onmessage", ".onopen", ".ontouchcancel", ".ontouchend", ".ontouchmove", ".ontouchstart"

			 document.getElementById("myId").onclick = function(){alert("hello")};
			 .onclick = function  => old version

			 document.getElementById("myId").addEventListener("click", function(){alert("hello")},false);
			 .addEventListener("click", modifyText, false); // in most non-IE browsers and IE9 => https://developer.mozilla.org/en-US/docs/Web/API/EventTarget/addEventListener
			 .attachEvent (eventName, function);  // IE before version 9
			 document.getElementById("myId").attachEvent("onclick", function(){alert("hello")});

			 $(document).on('click', '[data-buttons=dropdown]', function(e) {...});		 
			 .on("click", handler)  /  .on('click', [eventData], handler)  => https://api.jquery.com/click/
			 .trigger("click")
			 .bind("click", handler)  => https://api.jquery.com/bind/
			 .delegate( selector, events, data, handler ) => https://api.jquery.com/delegate/
			 .live( events, data, handler ) => https://api.jquery.com/live/
			 .on( events, selector ,data, handler ) => https://api.jquery.com/on/
			 .one( events, selector ,data, handler ) => https://api.jquery.com/one/
			 .resize( [eventData ], handler ) => https://api.jquery.com/resize/
			 .scroll( [eventData ], handler ) => https://api.jquery.com/scroll/
			 .load( [eventData ], handler )  = .on( "load", handler )  =>  https://api.jquery.com/load-event/
			 .keydown( [eventData ], handler )  = .on( "keydown", handler ) => https://api.jquery.com/keydown/
			 .keypress( [eventData ], handler ) = .on( "keypress", handler ) => https://api.jquery.com/keypress/
			 .keyup( [eventData ], handler ) = .on( "keyup", handler ) => https://api.jquery.com/keyup/
			 .toggle( handler, handler [, handler ] ) => https://api.jquery.com/toggle-event/
			 .mouseover( [eventData ], handler ) = .on( "mouseover", handler ) => https://api.jquery.com/mouseover/
			 .mousemove( [eventData ], handler ) = .on( "mousemove", handler ) => https://api.jquery.com/mousemove/
			 .mousedown( [eventData ], handler ) = .on( "mousedown", handler ) => https://api.jquery.com/mousedown/
			 .click([eventData], handler) = .on( "click", handler ) => https://api.jquery.com/click/
			 .dblclick( [eventData ], handler ) = .on( "dblclick", handler ) => https://api.jquery.com/dblclick/
			 .hover( handlerInOut ) = .on( "mouseenter mouseleave", handlerInOut ) => https://api.jquery.com/hover/

		 */
	}


	public boolean isAsyncMethod(String functionName){
		/**
		 	e.g 
		 	setTimeout(func, delay, [param1, param2, ...])
			setInterval(func, delay[, param1, param2, ...])
			...
		 */

		// Updated based on http://salt.ece.ubc.ca/callback-study/#async-apis

		String[] asyncMethods = { "setImmediate", "setTimeout", "setInterval", "XMLHTTPRequest.open", "addEventListener", "onclick", "process.nextTick"};		
		String[] asyncIOPaterns = { "fs.", "net.", "child_process.", "crypto.", "dns.", "domain.", "http.", "https.", "net.", "tls.", "dgram."};		

		for (String pattern: asyncMethods)
			if (functionName.endsWith(pattern))
				return true;
		for (String pattern: asyncIOPaterns)
			if (functionName.startsWith(pattern))
				return true;

		return false;
	}


	public void setTestFramework(String testsFramework) {
		this.testsFramework = testsFramework;

	}


	public void resetTestCodeProperties() {
		this.testCounter = 0;
		this.asyncTestCounter = 0;
		this.assertionCounter = 0;
		this.testModuleCounter = 0;
		this.newExpressionCounter = 0;
		this.triggerCounetr = 0;
		this.functionCalls.clear();
		this.testCaseInfoList.clear();
		this.testModuleInfoList.clear();
	}


	public ArrayList<TestUtilityFunctionInfo> getTestUtilityFunctions() {
		return testUtilityFunctionInfoList;
	}

}

