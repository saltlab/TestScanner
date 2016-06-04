package core;

import instrumentor.JSASTInstrumentor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.bcel.generic.FLOAD;
import org.apache.commons.io.CopyUtils;
import org.apache.commons.io.IOUtils;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.ast.AstRoot;



public class JSAnalyzer {
	
	private static int NumTests = 0;
	private static int NumAsyncTests = 0;
	private static int NumTestModules = 0;
	private static int NumAssertions = 0;
	private static int NumFunCall = 0;
	private static int NumFunCallTest = 0;  // store number of function calls in a test code
	private static int NumFunCallTestModule = 0;  // store number of function calls in a test module
	private static int NumTotalFunCall = 0;  // store number of function calls in a test code
	private static int MaxNumFunCallTest = 0;  // store max number of function calls in a test case
	private static int NumTriggerTest = 0;
	private static int NumObjCreate = 0;
	
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
	public int getMissedRegularFunc() {
		return missedRegularFunc;
	}

	private int neverExecFunCallSites = 0;
	public int getNeverExecFunCallSites() {
		return neverExecFunCallSites;
	}

	private int totalMissedStatementLinesInMissedFunctionCounter = 0;
	public int getTotalMissedStatementLinesInMissedFunctionCounter() {
		return totalMissedStatementLinesInMissedFunctionCounter;
	}

	private int totalMissedStatementLines;
	public int getTotalMissedStatementLines() {
		return totalMissedStatementLines;
	}
		
	
	private List<String> excludeFilenamePatterns;

	private JSASTInstrumentor astVisitor;
	private String outputfolder;
	private String jsAddress, scopeName;
	private String testsFramework;


	public void setJSAddress(String jsAddress){
		this.jsAddress = jsAddress;
	}

	public void setJSFileName(String scopeName){
		this.scopeName = scopeName;
	}

	/**
	 * Construct without patterns.
	 * 
	 * @param astVisit
	 *            The JSASTVisitor to run over all JavaScript.
	 * @param scopeName 
	 * @param jsAddress 
	 */
	public JSAnalyzer(JSASTInstrumentor astVisit, String jsAddress, String scopeName) {
		this.excludeFilenamePatterns = new ArrayList<String>();
		this.astVisitor = astVisit;
		this.jsAddress = jsAddress;
		this.scopeName = scopeName;
	}

	/**
	 * Constructor with patterns.
	 * 
	 * @param astVisit
	 *            The JSASTVisitor to run over all JavaScript.
	 * @param excludes
	 *            List with variable patterns to exclude.
	 */
	public JSAnalyzer(JSASTInstrumentor astVisit, List<String> excludes) {
		excludeFilenamePatterns = excludes;
		astVisitor = astVisit;
	}

	public JSAnalyzer(String outputfolder){
		excludeFilenamePatterns = new ArrayList<String>();
		this.outputfolder=outputfolder;
	}


	/**
	 * @param jsAddress
	 *            Address of the JavaScript code to be instrumented
	 * @param scopeName
	 *            Name of the current scope (filename mostly)
	 * @throws Exception 
	 */
	@SuppressWarnings("deprecation")
	public String instrumentJavaScript() throws Exception {

		// reading js form the input file
		String input = "";
		FileInputStream inputStream = new FileInputStream(jsAddress);
		String outputFileAddress = jsAddress.replace(".js", "_instrumented.js");
		FileOutputStream outputStream = new FileOutputStream(outputFileAddress);
		try {
			input = IOUtils.toString(inputStream);
		} finally {
			inputStream.close();
		}	    

		try {
			AstRoot ast = null;	

			/* initialize JavaScript context */
			Context cx = Context.enter();

			/* create a new parser */
			Parser rhinoParser = new Parser(new CompilerEnvirons(), cx.getErrorReporter());

			/* parse some script and save it in AST */
			ast = rhinoParser.parse(new String(input), scopeName, 0);

			//System.out.println("AST BEFORE INSTRUMENTATION: ");
			//System.out.println(ast.toSource());

			//System.out.println(ast.debugPrint());

			//writeJSToFile(scopename, input);
			//writeFunctionsToFile(input);

			//System.out.println("AST BEFORE : ");
			//System.out.println(ast.toSource());

			astVisitor.resetUnitTestCounter();

			astVisitor.setScopeName(scopeName);


			astVisitor.start();

			//astVisitor.setInstrumentFunctionCall(true);
			/* recurse through AST */
			ast.visit(astVisitor);


			//System.out.println("DONE WITH FUNCTION CALLS");
			//astVisitor.setInstrumentFunctionCall(false);
			//ast.visit(astVisitor);

			astVisitor.finish(ast);

			System.out.println("assertionCounter: " + astVisitor.getAssertionCounter());
			System.out.println("newExpressionCounter: " + astVisitor.getNewExpressionCounter());
			System.out.println("testCounter: " + astVisitor.getTestCounter());
			System.out.println("asynchTestCounter: " + astVisitor.getAsynchTestCounter());
			System.out.println("trieggerCounter: " + astVisitor.getTriggerCounetr());

			/* clean up */
			Context.exit();

			//System.out.println("AST AFTER INSTRUMENTATION: ");
			String instrumentedCode = ast.toSource();
			//System.out.println(instrumentedCode);
			//System.out.println(ast.debugPrint());

			try {
				CopyUtils.copy( instrumentedCode, outputStream);
				outputStream.flush();
			} finally {
				outputStream.close();
			}

			return ast.toSource();
		} catch (RhinoException re) {
			System.err.println(re.getMessage());
			System.out.println("Unable to instrument. This might be a JSON response sent"
					+ " with the wrong Content-Type or a syntax error.");
		} catch (IllegalArgumentException iae) {

			System.out.println("Invalid operator exception catched. Not instrumenting code.");
		}
		System.out.println("Here is the corresponding buffer: \n" + input + "\n");

		return input;
	}



	private void writeJSToFile(String scopename, String input) {
		try {
			System.out.println("writing on /jsCode/" + scopename);
			File file = new File("jsCode/" + scopename);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fop = new FileOutputStream(file);
			fop.write(input.getBytes());
			fop.flush();
			fop.close();
		}
		catch (IOException ioe) {
			System.out.println("IO Exception");
		}
	}

	// Look for instances of "function" in input then figure out where it ends
	private void writeFunctionsToFile(String input) {
		String inputCopy = input;
		int indexOfFuncString = inputCopy.indexOf("function ");
		while (indexOfFuncString != -1) {
			String sub = inputCopy.substring(indexOfFuncString);
			int nextOpenParen = sub.indexOf("(");
			String funcName = sub.substring(9, nextOpenParen); //"function " has 9 characters
			int firstOpenBrace = sub.indexOf("{");
			int countOpenBraces = 1;
			int countCloseBraces = 0;
			int endIndex = firstOpenBrace;
			while (countOpenBraces != countCloseBraces) {
				endIndex++;
				if (sub.charAt(endIndex) == '{') {
					countOpenBraces++;
				}
				else if (sub.charAt(endIndex) == '}') {
					countCloseBraces++;
				}
			}
			String code = sub.substring(0, endIndex+1);
			try {
				File file = new File("jsCode/" +  funcName + ".js");
				if (!file.exists()) {
					file.createNewFile();
				}
				FileOutputStream fop = new FileOutputStream(file);
				fop.write(code.getBytes());
				fop.flush();
				fop.close();
			}
			catch (IOException ioe) {
				System.out.println("IO Exception");
			}
			inputCopy = sub.substring(endIndex+1);
			indexOfFuncString = inputCopy.indexOf("function ");
		}
	}


	public void analyzeProductionCodeCoverage(ArrayList<Integer> coveredLines, ArrayList<Integer> missedLines, ArrayList<Integer> coveredFunctionsIndices, ArrayList<Integer> missedFunctionsIndices) throws Exception {

		astVisitor.setVisitType("AnalyzeProductionCode");
		astVisitor.setFunctionCounter(0); // resetting the index of visited Function nodes for annotating covered functions
		astVisitor.clearFunctionsList(); // clearing list of covered and missed function from previous visit

		// reading js form the input file
		String input = "";
		FileInputStream inputStream = new FileInputStream(jsAddress);
		try {
			input = IOUtils.toString(inputStream);
		} finally {
			inputStream.close();
		}	    

		try {
			AstRoot ast = null;	
			/* initialize JavaScript context */
			Context cx = Context.enter();
			/* create a new parser */
			Parser rhinoParser = new Parser(new CompilerEnvirons(), cx.getErrorReporter());
			/* parse some script and save it in AST */
			ast = rhinoParser.parse(new String(input), scopeName, 0);

			//System.out.println("************** AST ******************");
			//System.out.println(ast.toSource());
			//System.out.println(ast.debugPrint());
			//writeJSToFile(scopename, input);
			//writeFunctionsToFile(input);
			//System.out.println("AST BEFORE : ");
			//System.out.println(ast.toSource());

			astVisitor.setScopeName(scopeName);
			astVisitor.setCoverageInfo(coveredLines, missedLines, coveredFunctionsIndices, missedFunctionsIndices);
			/* recurse through AST */
			astVisitor.setVisitOnly("FunctionNode");
			ast.visit(astVisitor);

			System.out.println("CoveredFunctions :" + astVisitor.getCoveredFunctions());
			//System.out.println("CoveredFunctions.size() :" + astVisitor.getCoveredFunctions().size());
			System.out.println("CoveredFunctionLines :" + astVisitor.getCoveredFunctionLines());
			System.out.println("MissedFunctions :" + astVisitor.getMissedFunctions());
			//System.out.println("MissedFunctions.size() :" + astVisitor.getMissedFunctions().size());
			System.out.println("MissedFunctionLines :" + astVisitor.getMissedFunctionLines());
			
			
			astVisitor.setVisitOnly("FunctionCall");
			ast.visit(astVisitor);

			System.out.println("FunctionCalls :" + astVisitor.getFunctionCalls());

			for (String functionCall : astVisitor.getFunctionCalls()){
				if (functionCall.contains(".call") || functionCall.contains(".apply"))   // The call() and apply() methods calls a function with a given this value and arguments
					functionCall = functionCall.replace(".call", "").replace(".apply", "");
				if (astVisitor.getMissedFunctions().contains(functionCall)){
					System.out.println("The call to function " + functionCall + " was never executed!");
					neverExecFunCallSites++;
				}
			}

			
			
			coveredRegularFunc = astVisitor.getCoveredRegularFunc();
			missedRegularFunc = astVisitor.getMissedRegularFunc();
			coveredCallback = astVisitor.getCoveredCallback();
			missedCallback = astVisitor.getMissedCallback();
			coveredAsyncCallback = astVisitor.getCoveredAsyncCallback();
			missedAsyncCallback = astVisitor.getMissedAsyncCallback();
			coveredEventCallback = astVisitor.getCoveredAsyncCallback();
			missedEventCallback = astVisitor.getMissedEventCallback();
			coveredClosure = astVisitor.getCoveredClosure();
			missedClosure = astVisitor.getMissedClosure();

			
			System.out.println("++++ coveredRegularFunc: " + astVisitor.getCoveredRegularFunc());
			System.out.println("++++ missedRegularFunc: " + astVisitor.getMissedRegularFunc());
			System.out.println("++++ coveredCallback: " + astVisitor.getCoveredCallback());
			System.out.println("++++ missedCallback: " + astVisitor.getMissedCallback());
			System.out.println("++++ coveredAsyncCallback: " + astVisitor.getCoveredAsyncCallback());
			System.out.println("++++ missedAsyncCallback: " + astVisitor.getMissedAsyncCallback());
			System.out.println("++++ coveredEventCallback: " + astVisitor.getCoveredAsyncCallback());
			System.out.println("++++ missedEventCallback: " + astVisitor.getMissedEventCallback());
			System.out.println("++++ coveredClosure: " + astVisitor.getCoveredClosure());
			System.out.println("++++ missedClosure: " + astVisitor.getMissedClosure());

			System.out.println("++++ neverExecFunCallSites: " + neverExecFunCallSites);
			
			
			ArrayList<Integer> msimf = astVisitor.getMissedStatementInMissedFunction();
			//System.out.println("msimf: " + msimf);
			for (int i=0; i<msimf.size(); i++){
				if (msimf.get(i) >= 0)
					totalMissedStatementLinesInMissedFunctionCounter ++;
			}
			
			totalMissedStatementLines = astVisitor.getMissedStatementLines().size();

			System.out.println("@ Total missed statement lines in missed functioncounter = " + totalMissedStatementLinesInMissedFunctionCounter);
			System.out.println("@ Total number of missed statements = " + totalMissedStatementLines);
			if (totalMissedStatementLines!=0){
				float ratio = (float)totalMissedStatementLinesInMissedFunctionCounter/(float)totalMissedStatementLines;
				System.out.println("@ Percentage of missed statement in missed functions = " + ratio*100 + "%");
			}


			/*
			System.out.println("assertionCounter: " + astVisitor.getAssertionCounter());
			System.out.println("newExpressionCounter: " + astVisitor.getNewExpressionCounter());
			System.out.println("testCounter: " + astVisitor.getTestCounter());
			System.out.println("asynchTestCounter: " + astVisitor.getAsynchTestCounter());
			System.out.println("trieggerCounter: " + astVisitor.getTriggerCounetr());
			 */

			/* clean up */
			Context.exit();
		} catch (RhinoException re) {
			System.err.println(re.getMessage());
			System.out.println("Unable to instrument. This might be a JSON response sent"
					+ " with the wrong Content-Type or a syntax error.");
		} catch (IllegalArgumentException iae) {

			System.out.println("Invalid operator exception catched. Not instrumenting code.");
		}

		//System.out.println("Here is the corresponding buffer: \n" + input + "\n");
		
		astVisitor.setVisitType("");
	}

	public void analyzeTestCodeProperties() throws Exception {
		System.out.println("===== analyzeTestCodeProperties ====");		

		astVisitor.setVisitType("AnalyzeTestCode");
		astVisitor.setTestFramework(testsFramework); 
		
		// reading js form the input file
		String input = "";
		FileInputStream inputStream = new FileInputStream(jsAddress);
		try {
			input = IOUtils.toString(inputStream);
		} finally {
			inputStream.close();
		}	    

		try {
			AstRoot ast = null;	
			/* initialize JavaScript context */
			Context cx = Context.enter();
			/* create a new parser */
			Parser rhinoParser = new Parser(new CompilerEnvirons(), cx.getErrorReporter());
			/* parse some script and save it in AST */
			ast = rhinoParser.parse(new String(input), scopeName, 0);

			//System.out.println("************** AST ******************");
			//System.out.println(ast.toSource());
			//System.out.println(ast.debugPrint());

			astVisitor.setScopeName(scopeName);
			/* recurse through AST */
			astVisitor.setVisitOnly("FunctionNode");   // detecting test utility functions
			ast.visit(astVisitor);

			System.out.println("Test utility functions :" + astVisitor.getTestUtilityFunctions());

			astVisitor.setVisitOnly("FunctionCall");   // collecting stats regarding function calls
			ast.visit(astVisitor);

			System.out.println("FunctionCallsInTests :" + astVisitor.getFunctionCallsInTests());
		
			NumTests = astVisitor.getTestCounter();
			NumAsyncTests = astVisitor.getAsynchTestCounter();
			//NumAssertions = astVisitor.getAssertionCounter();  // => this does not consider assert in modules
			NumAssertions =  astVisitor.getNumAssertInTests();
			NumTestModules = astVisitor.getTestModuleCounter();
			NumFunCallTest = astVisitor.getFunCallCounterInTest();
			NumFunCallTestModule = astVisitor.getFunCallCounterInTestModule();
			NumTotalFunCall = NumFunCallTest + NumFunCallTestModule;
			MaxNumFunCallTest = astVisitor.getMaxFunctionCallsInTests();
			NumTriggerTest = astVisitor.getTriggerCounetr();
			NumObjCreate = astVisitor.getNewExpressionCounter();
		
			System.out.println("NumTests: " + NumTests);
			System.out.println("NumAsyncTests: " + NumAsyncTests);
			System.out.println("NumTestModules: " + NumTestModules);
			System.out.println("NumAssertions: " + NumAssertions);
			System.out.println("NumFunCallTest: " + NumFunCallTest);
			System.out.println("NumFunCallTestModule: " + NumFunCallTestModule);
			System.out.println("NumTotalFunCall: " + NumTotalFunCall);
			System.out.println("MaxNumFunCallTest: " + MaxNumFunCallTest);
			System.out.println("NumTriggerTest: " + NumTriggerTest);
			System.out.println("NumObjCreate: " + NumObjCreate);

			/* clean up */
			Context.exit();
		} catch (RhinoException re) {
			System.err.println(re.getMessage());
			System.out.println("Unable to instrument. This might be a JSON response sent"
					+ " with the wrong Content-Type or a syntax error.");
		}

		astVisitor.setVisitType("");
		astVisitor.resetTestCodeProperties();
	}

	public int getNumTests() {
		return NumTests;
	}

	public int getNumAsyncTests() {
		return NumAsyncTests;
	}

	public int getNumTestModules() {
		return NumTestModules;
	}

	public int getNumAssertions() {
		return NumAssertions;
	}

	public int getNumFunCall() {
		return NumFunCall;
	}
	
	public int getNumFunCallTest() {
		return NumFunCallTest;
	}

	public int getNumFunCallTestModule() {
		return NumFunCallTestModule;
	}

	public int getMaxFunCallTest() {
		return MaxNumFunCallTest;
	}

	public int getNumTriggerTest() {
		return NumTriggerTest;
	}

	public int getNumObjCreate() {
		return NumObjCreate;
	}

	public void setTestFramework(String testsFramework) {
		this.testsFramework = testsFramework;
	}

}
