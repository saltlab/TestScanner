package instrumentor;

import java.util.ArrayList;
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
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.NewExpression;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.ObjectProperty;


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
	private int assertionCounter = 0;
	private int funCallCounter = 0;
	private int newExpressionCounter = 0;
	private int triggerCounetr = 0;

	private ArrayList<TestCaseInfo> testCaseInfoList = new ArrayList<TestCaseInfo>();
	public ArrayList<TestCaseInfo> getTestCaseInfoList() {
		return testCaseInfoList;
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
	private ArrayList<Integer> missedFunctionsIndices = new ArrayList<Integer>();
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
	public ArrayList<String> getFunctionCalls() {
		ArrayList<String> funCalls = new ArrayList<String>();
		for (TestCaseInfo tc: testCaseInfoList){
			funCalls.addAll(tc.getFunctionCalls());
		}
		return funCalls;
	}

	public ArrayList<String> getFunctionCallsInTests() {
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

	private int missedRegularFunc = 0;

	private String testsFramework;

	public int getMissedRegularFunc() {
		return missedRegularFunc;
	}


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
			}else{
				if (node instanceof FunctionCall)
					analyzeProductionCodeFunctionCallNode(node);
				else if (node instanceof Assignment)
					analyzeProductionCodeAssignmentNode(node);
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
		int fDepth = node.depth();
		String funcLocation = "regular";
		String functionName = getFunctionName(f);
		//System.out.println("Function name: " + functionName);
		AstNode parentNode = node.getParent();
		String parentNodeSource = parentNode.toSource();
		String parentNodeName = parentNode.shortName();
		//System.out.println("shortName: " + shortName);
		//System.out.println("parentNodeName: " + parentNodeName);

		String enclosingFunction = "";
		if (node.getEnclosingFunction()!=null){
			enclosingFunction  = getFunctionName(node.getEnclosingFunction());
		}
		//System.out.println("enclosingFunction: " + enclosingFunction);

		boolean covered = false;
		if (coveredFunctionsIndices.contains(functionCounter)){
			if (!coveredFunctions.contains(functionName))
				coveredFunctions.add(functionName);
			if (!coveredFunctionLines.contains(lineNumber))
				coveredFunctionLines.add(lineNumber);
			coveredFunctionsLoc.add(funcLocation);
			System.out.println("======== Covered function at line" + (node.getLineno()+1) + " - Function name: " + functionName);
			covered = true;
		}else{
			if (!missedFunctions.contains(functionName))
				missedFunctions.add(functionName);
			if (!missedFunctionLines.contains(lineNumber))
				missedFunctionLines.add(lineNumber);
			System.out.println("======== Missed function at line" + (node.getLineno()+1) + " - Function name: " + functionName);
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
		functionCounter++;


		if (parentNodeName.equals("ParenthesizedExpression")){
			System.out.println("This is an immediately invoked function, just ignore it!");
			//This is an immediately invoked function, just ignore it!
		}else if (parentNodeName.equals("FunctionCall")){
			FunctionCall parentNodeFunctionCall = (FunctionCall) parentNode;
			AstNode targetNode = parentNodeFunctionCall.getTarget();
			String targetSource = targetNode.toSource();
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
			String parentParentNodeSource = parentParentNode.toSource();
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
		String parentNodeSource = parentNode.toSource();
		String parentNodeName = parentNode.shortName();
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
		 * 
		 * some jquery callback receiving functions include each, on, etc.
		 * 
		 * Callbacks can be event-dependent, i.e., the enclosing function is an event function such as on() or click()
		 * 
		 * $.ajax({
        		url:"http://fiddle.jshell.net/favicon.png",
        		success:successCallback,
        		complete:completeCallback,
        		error:errorCallback
    	   });
		 * 
		 * Callback function: sent as an argument of another function. Anonymous functions defined in the parameter of the containing function, is one of the common patterns for using callback functions. 
		 * 
		 */

		FunctionCall fcall = (FunctionCall) node;
		AstNode targetNode = fcall.getTarget(); // node evaluating to the function to call. E.g document.getElemenyById(x)
		String targetSource = targetNode.toSource();
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
		//System.out.println(varName + " is set to: " + asmt.getRight().toSource());
		// check for pattern  X.onclick = nameOfAFunction  or X.onclick = function()
		if (isEventMethod(varName)){
			if (asmt.getRight() instanceof FunctionNode){  // e.g X.onclick = function()
				System.out.println("An event-dependent callback found at line " + (node.getLineno()+1));
				missedEventCallback++;
			}else if (coveredFunctions.contains(asmt.getRight().toSource())){
				System.out.println("Covered event-dependent callback at line " + (node.getLineno()+1));
				coveredEventCallback++;
			}else if (missedFunctions.contains(asmt.getRight().toSource())){
				System.out.println("Missed event-dependent callback at line " + (node.getLineno()+1));
				missedEventCallback++;
			}
			//System.out.println("Event-dependent callback function: " + asmt.toSource());
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
		AstNode targetNode = fcall.getTarget(); // node evaluating to the function to call. E.g document.getElemenyById(x)
		//System.out.println("targetNode.toSource(): " + targetNode.toSource());
		String functionName = targetNode.toSource();
		if (functionName.contains("{")){  // ignoring calls by immediately invoked functions
			//System.out.println("ignoring calls by immediately invoked functions: " + functionName);
			return;
		}
		functionName = targetNode.toSource().substring(targetNode.toSource().lastIndexOf(".")+1);

		if (testsFramework.equals("qunit")){
			if (targetNode.toSource().equals("QUnit.test") || targetNode.toSource().equals("test")){ 
				testCounter++;
				// add a new TestCaseInfo object
				TestCaseInfo t = new TestCaseInfo(testCounter, "sync");
				testCaseInfoList.add(t);
			}
			if (targetNode.toSource().equals("QUnit.asyncTest") || targetNode.toSource().equals("asyncTest")){
				testCounter++;
				asyncTestCounter++;
				TestCaseInfo t = new TestCaseInfo(testCounter, "async");
				testCaseInfoList.add(t);
			}
		}			

		if (targetNode.toSource().equals("trigger") || targetNode.toSource().equals("triggerHandler"))
			triggerCounetr++;

		String[] assertionSkipList = { "assert.expect", "expect", "assert.equal", "equal", "assert.notEqual", "notEqual", "assert.deepEqual", "deepEqual", 
				"assert.notDeepEqual", "notDeepEqual", "assert.strictEqual", "strictEqual", "assert.notStrictEqual", "notStrictEqual", "QUnit.ok", "assert.ok", "ok", "assert.notOk", "notOk", 
				"assert.propEqual", "propEqual", "assert.notPropEqual", "notPropEqual", "assert.push", "assert.throws", "throws", "assert.async"};		

		String[] otherSkipList = { "QUnit.module", "module", "QUnit.test", "test", "QUnit.asyncTest", "asyncTest", "jQuery", "$" , "start", "stop"}; // start/stop for asynchronous control	

		if (ArrayUtils.contains( assertionSkipList, targetNode.toSource() )){
			assertionCounter++;
			if (testCaseInfoList.size()!=0){
				TestCaseInfo t = testCaseInfoList.get(testCaseInfoList.size()-1);
				t.setNumAssertions(t.getNumAssertions()+1);
				System.out.println("Test case " + t.getTestNumber() + " has " + t.getNumAssertions() + " assertions!");
			}else{
				// search for a test utility function with the same name as the enclosingFunction
				for(TestUtilityFunctionInfo tufi: testUtilityFunctionInfoList){
					if (tufi.getFuncName().equals(enclosingFunction)){
						tufi.setNumAssertions(tufi.getNumAssertions()+1);
						System.out.println("Test utility function " + tufi.getFuncName() + " has " + tufi.getNumAssertions() + " assertions!");
						//System.out.println("An assertion found out of a test case");
						
						
						// add to num of assertions
						
						
						
						break;
					}
				}
				//TestUtilityFunctionInfo tufi = testUtilityFunctionInfoList.get(testUtilityFunctionInfoList.size()-1);
			}
		}

		if (ArrayUtils.contains( assertionSkipList, targetNode.toSource() ) || ArrayUtils.contains( otherSkipList, targetNode.toSource() )) {
			System.out.println("Not counting the called function: " + functionName);
			return;
		}else{
			System.out.println("Counting the called function: " + functionName + " with enclosingFunction: " + enclosingFunction);
				if (testCaseInfoList.size()!=0){
					TestCaseInfo t = testCaseInfoList.get(testCaseInfoList.size()-1);
					int currentNumFunCalls = t.getNumFunCall();
					// search for a test utility function with the same name as the functionName
					boolean testUtilFunCall = false;
					for(TestUtilityFunctionInfo tufi: testUtilityFunctionInfoList){
						if (tufi.getFuncName().equals(functionName)){
							System.out.println("The called function " + functionName + " is a test utility function with " + tufi.getNumFunCall() + " function calls! Adding to the test info...");
							currentNumFunCalls += tufi.getNumFunCall();
							System.out.println("Adding function calls in the test utility function: " + tufi.getFunctionCalls());
							for(String fc: tufi.getFunctionCalls())
								t.addFunctionCall(fc);
							testUtilFunCall = true;
							break;
						}
					}
					if (testUtilFunCall==false){
						t.setNumFunCall(currentNumFunCalls+1);
						t.addFunctionCall(functionName);
					}
					else{
						t.setNumFunCall(currentNumFunCalls);  // do not add the call to the test utility function
					}
						
					System.out.println("Test case " + t.getTestNumber() + " has " + t.getNumFunCall() + " function calls!");
					funCallCounter++;
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
					//TestUtilityFunctionInfo tufi = testUtilityFunctionInfoList.get(testUtilityFunctionInfoList.size()-1);
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
		if (targetNode.toSource().equals("QUnit.test") || targetNode.toSource().equals("test")){ 
			currentTestNumber++;
			currentTest = "Test" + Integer.toString(currentTestNumber);
			setTestCounter(getTestCounter() + 1);
		}
		if (targetNode.toSource().equals("QUnit.asyncTest()") || targetNode.toSource().equals("asyncTest()")){
			currentTestNumber++;
			currentTest = "AsynchTest" + Integer.toString(currentTestNumber);
			setAsynchTestCounter(getAsynchTestCounter() + 1);
		}

		if (targetNode.toSource().equals("trigger") || targetNode.toSource().equals("triggerHandler"))
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

	public int getAsynchTestCounter() {
		return asyncTestCounter;
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

	public void setCoverageInfo(ArrayList<Integer> coveredStatementLines, ArrayList<Integer> missedStatementLines, ArrayList<Integer> coveredFunctionsIndices, ArrayList<Integer> missedFunctionsIndices) {
		this.coveredStatementLines = coveredStatementLines;
		this.missedStatementLines = missedStatementLines;
		this.coveredFunctionsIndices = coveredFunctionsIndices;
		this.missedFunctionsIndices = missedFunctionsIndices;

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
		 */

		String[] eventMethods = { ".bind", ".blur", ".change", ".click", ".dblclick", ".delegate", ".error", ".focus", 
				".focusin", ".focusout", ".hover", ".keydown", ".keypress", ".keyup", ".live", ".load", ".mousedown", ".mouseenter", ".mouseleave", ".mousemove",  
				".mouseout", ".mouseover", ".mouseup", ".on", ".one", ".ready", ".resize", ".scroll", ".select", ".submit", ".toggle", ".trigger", ".triggerHandler", ".unload",

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
		 	setTimeout(func, delay, [param1, param2, ...])
			setInterval(func, delay[, param1, param2, ...])
		 */

		// TODO: XHR and others from Keheliya's paper

		String[] asyncMethods = { "setImmediate", "setTimeout", "setInterval"};		

		for (String am: asyncMethods)
			if (functionName.equals(am))
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
		this.newExpressionCounter = 0;
		this.triggerCounetr = 0;
		this.functionCalls.clear();
		this.testCaseInfoList.clear();
	}


	public ArrayList<TestUtilityFunctionInfo> getTestUtilityFunctions() {
		return testUtilityFunctionInfoList;
	}


	
	
	public void setFunCallCounter(int funCallCounter) {
		this.funCallCounter = funCallCounter;
	}
	public int getFunCallCounter() {
		return funCallCounter;
	}

}

