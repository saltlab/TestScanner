package core;

import instrumentor.ASTNodeUtility;
import instrumentor.ConsoleErrorReporter;
import instrumentor.JSASTInstrumentor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.ParenthesizedExpression;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.StringLiteral;
import org.mozilla.javascript.ast.UnaryExpression;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * This class is used to visit analyze the trace generated during a function execution. It extracts all 
 * DOM dependent functions, and generates a constraints table as well as DOM elements table to be used later 
 * for translating to xpath and solving them.
 */

public class TraceAnalyzer {

	private CompilerEnvirons compilerEnvirons = new CompilerEnvirons();

	public HashSet<String> DOMDependentFunctions = new HashSet<String>();

	private static String xpath="";
	private static int numOfDOMElementsInFixture = 0;

	private String lastLoopCondition = "";
	public static int numOfCombinations = 0;
	public static int generatedID = 0;   // designed to be auto-increment and reset once fixture generated


	private String currentDOMJSVariable = "";

	public TraceAnalyzer(){
		// adding the initial "document" node to be used for xpath generation
	}

	public void analyzeTrace(Map<String, String> map) {

		System.out.println("****** Analyzing a new trace ******");
		//System.out.println("map: " + map);
		System.out.printf("statementType: %s\n", map.get("statementType"));
		System.out.printf("enclosingFunction: %s\n", map.get("enclosingFunction"));
		System.out.printf("statement: %s\n", map.get("statement"));
		System.out.printf("varList: %s\n", map.get("varList"));
		System.out.printf("varValueList: %s\n", map.get("varValueList"));
		System.out.printf("actualStatement: %s\n", map.get("actualStatement"));
		System.out.println("***********************************");

		// parsing the original statement for analysis
		if (map.get("statementType").equals("functionCall"))
			analyseFunctionCallNode(map);		
	}


	private void analyseFunctionCallNode(Map<String, String> map) {
		System.out.println("=== analyseFunctionCallNode ===");
		/*  Detecting DOM accessing function calls
		document.getElementById() 			Returns the element that has the ID attribute with the specified value
		document.getElementsByClassName() 	Returns a NodeList containing all elements with the specified class name
		document.getElementsByName() 		Accesses all elements with a specified name
		document.getElementsByTagName() 	Returns a NodeList containing all elements with the specified tagname
		$()									(jQuery) : Find an element by element id
		 */		
		AstNode generatedNode = parse(map.get("statement"));
		ExpressionStatement es = (ExpressionStatement)((AstNode) generatedNode.getFirstChild());
		AstNode node = es.getExpression();

		FunctionCall fcall = (FunctionCall) node;
		AstNode targetNode = fcall.getTarget(); // node evaluating to the function to call. E.g document.getElemenyById(x)
		String targetBody = targetNode.toSource();
		AstNode parentNode = node.getParent();

		String functionType = "";  // The called function is either "accessingDOM" or "notAccessingDOM" 
		String argument = "";
		String argumentShortName = "";
		String enclosingFunctionName = 	map.get("enclosingFunction");
		// to store the var in the JS code that a DOM element is assigned to
		String DOMJSVariable = "";
		//String DOMJSVariable = "anonym"+Integer.toString((new Random()).nextInt(100)); 

		// parsing actual statement to see if there is a DOM element access. e.g. actualStatement: [org.openqa.selenium.remote.RemoteWebElement@1e2123e2 -> unknown locator]
		String RemoteWebElement = "";
		String actualStatement = String.format("%s", map.get("actualStatement"));
		if (actualStatement.contains("RemoteWebElement")){
			int start = actualStatement.indexOf("@") + 1;
			int end = actualStatement.indexOf(" ", start);
			//System.out.println("contains RemoteWebElement from " + start + " to " + end);
			// extract RemoteWebElementID
			RemoteWebElement = actualStatement.substring(start, end);
			System.out.println("RemoteWebElement: " + RemoteWebElement);
		}

		ArrayList<String> argumentList = getArguments(map, "varList");
		System.out.println("argumentList: " + argumentList);
		ArrayList<String> argumentValueList = getArguments(map, "varValueList");
		System.out.println("argumentValueList: " + argumentValueList);

		// if varList and varValueList are different then a variable is used to refer to an element locator

		System.out.println("parentNode.toSource(): " + parentNode.toSource());
		System.out.println("parentNode.shortName(): " + parentNode.shortName());
		System.out.println("parentNode.getParent().toSource(): " + parentNode.getParent().toSource());
		System.out.println("parentNode.getParent().shortName(): " + parentNode.getParent().shortName());

		// e.g. var x = document.getElemenyById('id1')
		if (parentNode.shortName().equals("VariableInitializer")){
			VariableInitializer vi = (VariableInitializer)parentNode;
			Name varName = (Name) vi.getTarget();
			AstNode varLiteral = vi.getInitializer();
			DOMJSVariable = varName.toSource();
			//System.out.println("parentNode.getChildBefore(ASTNode).getString() :" + parentNode.getChildBefore(ASTNode).getString());
			System.out.println("Variable:" + DOMJSVariable + " initialized to: " + varLiteral.toSource());
		}else 
			// e.g. x = document.getElemenyById('id2')
			if (parentNode.shortName().equals("Assignment")){
				Assignment asmt = (Assignment)parentNode;
				DOMJSVariable = asmt.getLeft().toSource();
				System.out.println("Variable:" + DOMJSVariable + " is set to: " + asmt.getRight().toSource());
			}

		// getting the argument (id, class, tag, etc.) based on which DOM element is selected
		if (fcall.getArguments().size()>0){
			argument = fcall.getArguments().get(0).toSource();
			argument = argument.replace("'", "");
			argumentShortName = fcall.getArguments().get(0).shortName();
			System.out.println("argument: " + argument);
			System.out.println("argumentShortName: " + argumentShortName);
		}


		if (targetBody.contains("getElementById") || targetBody.contains("getElementsByTagName") || 
				targetBody.contains("getElementsByName") || targetBody.contains("getElementsByClassName") ||
				targetBody.equals("$") || targetBody.equals("jQuery"))
			functionType = "accessingDOM";
		else
			functionType = "notAccessingDOM"; 


		// e.g. document.getElemenyById(x)
		if (targetNode instanceof PropertyGet && functionType.equals("accessingDOM")){
			PropertyGet pg = (PropertyGet)targetNode;
			targetBody = pg.getRight().toSource();
			// getting parentNodeElement e.g. document in document.getElemenyById(x) or a in a.getElemenyById(x)
			String parentNodeElement = pg.getLeft().toSource();
			// getting rid of [] for array DOMJSVariables. e.g. for spanElems[i].getElementsByTagName('div'), spanElems will be considered as parentNodeElement
			if (parentNodeElement.contains("["))
				parentNodeElement = parentNodeElement.substring(0, parentNodeElement.indexOf('['));

			// TODO: return document.getElementbyID(x) -> return should be considered as an assignment *********
			// TODO: some static analysis!!!!

			// e.g. getElementsByTagName("p")
			//if (argumentShortName.equals("StringLiteral")){   

			// Adding the enclosingFunctionName to the list of DDF during static instrumentation. DDF can increase during dynamic execution if a function calls a DDF   
			DOMDependentFunctions.add(enclosingFunctionName);

		}

	}

	private ArrayList<String> getArguments(Map<String, String> map, String value){
		ArrayList<String> result = new ArrayList<String>();
		String temp = String.format("%s", map.get(value));
		temp = temp.replace("[", "").replace("]", "").replace(" ", "");
		StringTokenizer st = new StringTokenizer(temp, ",");
		while (st.hasMoreElements()) {
			result.add((String) st.nextElement());
		}
		return result;
	}

	public HashSet<String> getDOMDependentFunctions() {
		return DOMDependentFunctions;
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
}