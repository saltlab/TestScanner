import instrumentor.JSASTInstrumentor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.CopyUtils;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;

import core.JSAnalyzer;
import core.TraceAnalyzer;

public class Runner {


	//private static String testsFolder = "/Users/aminmf/Documents/workspace/testDependency/input/web-storage/";
	//private static String htmlTestRunner = "/Users/aminmf/Documents/workspace/testDependency/input/web-storage/tests.html";

	//private static String testsFolder = "/Users/aminmf/Documents/workspace/testDependency/input/Mocky/test/";
	//private static String htmlTestRunner = "/Users/aminmf/Documents/workspace/testDependency/input/Mocky/test/index.html";

	//private static String testsFolder = "/Users/aminmf/Documents/workspace/testDependency/input/logit/test";
	//private static String htmlTestRunner = "/Users/aminmf/Documents/workspace/testDependency/input/logit/test/TaskCollectionTests.html";


	//private static String testsFolder = "http://localhost:8888/testAnalysisProject/logit/test";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/logit/test/TaskCollectionTests.html";

	//private static String testsFolder = "/Applications/MAMP/htdocs/testAnalysisProject/Mocky/test/";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/Mocky/test/index.html";

	//private static String testsFolder = "/Applications/MAMP/htdocs/testAnalysisProject/jQuery-Heart-Beater/tests/";
	//private static String htmlTestRunner = "/Applications/MAMP/htdocs/testAnalysisProject/jQuery-Heart-Beater/tests/index.html";

	//private static String testsFolder = "/Users/aminmf/Documents/workspace/testDependency/input/validity/test";
	//private static String htmlTestRunner = "/Users/aminmf/Documents/workspace/testDependency/input/validity/test/validitytests.html";

	//private static String testsFolder = "/Applications/MAMP/htdocs/testAnalysisProject/Stalwart/tests/modules/";
	//private static String htmlTestRunner = "/Applications/MAMP/htdocs/testAnalysisProject/Stalwart/tests/index.html";
	
	//private static String testsFolder = "/Applications/MAMP/htdocs/testAnalysisProject/site-com-trium/js/";
	//private static String htmlTestRunner = "/Applications/MAMP/htdocs/testAnalysisProject/site-com-trium/js/tests.html";

	//private static String testsFolder = "/Applications/MAMP/htdocs/testAnalysisProject/matrix/test/";
	//private static String htmlTestRunner = "/Applications/MAMP/htdocs/testAnalysisProject/matrix/test/index.html";
	
	//private static String testsFolder = "/Applications/MAMP/htdocs/testAnalysisProject/vlaams-parlement/test/";
	//private static String htmlTestRunner = "/Applications/MAMP/htdocs/testAnalysisProject/vlaams-parlement/unit_tests.html";

	//private static String testsFolder = "/Applications/MAMP/htdocs/testAnalysisProject/sort-util-js/test";
	//private static String htmlTestRunner = "/Applications/MAMP/htdocs/testAnalysisProject/sort-util-js/test/index.html";
	
	//private static String testsFolder = "/Applications/MAMP/htdocs/testAnalysisProject/theAsteroids/tests/";
	//private static String htmlTestRunner = "/Applications/MAMP/htdocs/testAnalysisProject/theAsteroids/tests/index.html";
	
	//private static String testsFolder = "/Applications/MAMP/htdocs/testAnalysisProject/web-storage/";
	//private static String htmlTestRunner = "/Applications/MAMP/htdocs/testAnalysisProject/web-storage/tests.html";
	
	//private static String testsFolder = "/Applications/MAMP/htdocs/testAnalysisProject/sudokujs/test";
	//private static String htmlTestRunner = "/Applications/MAMP/htdocs/testAnalysisProject/sudokujs/test/sudoku-test.html";
	
	//private static String testsFolder = "/Users/aminmf/Documents/workspace/testDependency/input/analytics/js/tests";
	//private static String htmlTestRunner = "/Users/aminmf/Documents/workspace/testDependency/input/analytics/js/tests/QUnitRunnerDisplay.html";
	//private static String htmlTestRunner = "/Users/aminmf/Documents/workspace/testDependency/input/analytics/js/tests/QUnitRunnerFactSelector.html";
	//private static String htmlTestRunner = "/Users/aminmf/Documents/workspace/testDependency/input/analytics/js/tests/QUnitRunnerQuery.html";
	
	//private static String testsFolder = "/Applications/MAMP/htdocs/testAnalysisProject/ProjetoAmazingWorld/test/";
	//private static String htmlTestRunner = "/Applications/MAMP/htdocs/testAnalysisProject/ProjetoAmazingWorld/test/index.html";
	
	//private static String testsFolder = "/Applications/MAMP/htdocs/testAnalysisProject/confero/test/";
	//private static String htmlTestRunner = "/Applications/MAMP/htdocs/testAnalysisProject/confero/test/index.html";
	
	//private static String testsFolder = "/Applications/MAMP/htdocs/testAnalysisProject/fxosRate/";
	//private static String htmlTestRunner = "/Applications/MAMP/htdocs/testAnalysisProject/fxosRate/tests.html";
	
	//private static String testsFolder = "/Applications/MAMP/htdocs/testAnalysisProject/Dable/test";
	//private static String htmlTestRunner = "/Applications/MAMP/htdocs/testAnalysisProject/Dable/test/test.html";

	//private static String testsFolder = "/Applications/MAMP/htdocs/testAnalysisProject/jsgraph/test";
	//private static String htmlTestRunner = "/Applications/MAMP/htdocs/testAnalysisProject/jsgraph/test/test.html";
	
	//private static String testsFolder = "/Applications/MAMP/htdocs/testAnalysisProject/PackPack/testing/";
	//private static String htmlTestRunner = "/Applications/MAMP/htdocs/testAnalysisProject/PackPack/testing/testing.html";
	
	//private static String testsFolder = "/Applications/MAMP/htdocs/testAnalysisProject/LocalStorageCache/test/";
	//private static String htmlTestRunner = "/Applications/MAMP/htdocs/testAnalysisProject/LocalStorageCache/test/index.html";

	//private static String testsFolder = "/Applications/MAMP/htdocs/testAnalysisProject/classListJS/tests/";
	//private static String htmlTestRunner = "/Applications/MAMP/htdocs/testAnalysisProject/classListJS/tests/qunit.html";

	//private static String testsFolder = "/Applications/MAMP/htdocs/testAnalysisProject/webuploader/test/";
	//private static String htmlTestRunner = "/Applications/MAMP/htdocs/testAnalysisProject/webuploader/test/index.html";
	
	//private static String testsFolder = "/Applications/MAMP/htdocs/testAnalysisProject/articles/proc_slide/qunit/";
	//private static String htmlTestRunner = "/Applications/MAMP/htdocs/testAnalysisProject/articles/proc_slide/qunit/base.tests.html";
	//private static String htmlTestRunner = "/Applications/MAMP/htdocs/testAnalysisProject/articles/proc_slide/qunit/entity.tests.html";
	//private static String htmlTestRunner = "/Applications/MAMP/htdocs/testAnalysisProject/articles/proc_slide/qunit/node.tests.html";
	//private static String htmlTestRunner = "/Applications/MAMP/htdocs/testAnalysisProject/articles/proc_slide/qunit/timeline.tests.html";

	//private static String testsFolder = "/Applications/MAMP/htdocs/testAnalysisProject/hybrid-events/test/";
	//private static String htmlTestRunner = "/Applications/MAMP/htdocs/testAnalysisProject/hybrid-events/test/runner.html";

	//private static String testsFolder = "/Applications/MAMP/htdocs/testAnalysisProject/storage/StorageJS.Core/tests/cache";
	//private static String htmlTestRunner = "/Applications/MAMP/htdocs/testAnalysisProject/storage/StorageJS.Core/tests/cache/cache-tests.html";
	
	private static String testsFolder = "/Applications/MAMP/htdocs/testAnalysisProject/Roguelike";
	private static String htmlTestRunner = "/Applications/MAMP/htdocs/testAnalysisProject/Roguelike/test.html";

	private static ArrayList<String> instrumentedJSFiles = new ArrayList<String>();

	private static ArrayList<String> jsFileNames = new ArrayList<String>();
	private static JSAnalyzer codeAnalyzer;
	private static TraceAnalyzer traceAnalyzer = new TraceAnalyzer();

	private static WebDriver driver;

	public static void main(String[] args) throws Exception {

		/*
		 * 1) Instrument JavaScript unit tests
		 * 2) Execute the instrumented tests and get the exec trace
		 */
		boolean doneWithInstrumentation = true;

		if (!doneWithInstrumentation){
			// Load JS unit tests in Qunit format and wrap function calls in test files
			codeAnalyzer = new JSAnalyzer(new JSASTInstrumentor(), testsFolder, null);		

			// For each .js test file
			File[] files = new File(testsFolder).listFiles();
			for (File file : files) {
				if (file.isFile()) {
					String fileName = file.getName();

					//if (fileName.endsWith(".js")){
					if (!fileName.contains("qunit") && fileName.endsWith(".js") && !fileName.contains("instrumented")) { // && !fileName.equals("experimental.js")
						//&& !fileName.equals("es.js") && !fileName.equals("helpers.js") && !fileName.equals("karma.conf.js") && !fileName.equals("es.js") && !fileName.equals("library.js")
						//){
						codeAnalyzer.setJSFileName(fileName);
						codeAnalyzer.setJSAddress(testsFolder + "/" + fileName);

						System.out.println("Instrumenting the test suite in file " + fileName);
						// Instrument the JavaScript code by wrapping function calls
						instrumentedJSFiles.add(fileName.replace(".js", ""));
						codeAnalyzer.instrumentJavaScript();
					}
				}

				// generate new test runner file
				String input = "";
				FileInputStream inputStream = new FileInputStream(htmlTestRunner);
				String outputFileAddress = htmlTestRunner.replace(".html", "_instrumented.html");
				FileOutputStream outputStream = new FileOutputStream(outputFileAddress);
				try {
					input = IOUtils.toString(inputStream);
					// rename all instrumented js files
					for (String i: instrumentedJSFiles){
						input = input.replace(i + ".js" , i + "_instrumented.js");
					}
					CopyUtils.copy( input, outputStream);
					outputStream.flush();
				} finally {
					inputStream.close();
					outputStream.close();
				}	    				


			}
		}else{
			driver = new FirefoxDriver();
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

			// Load the html test runner with the instrumented tests in the browser to execute the tests
			String newTestRunner = "file:///" + htmlTestRunner.replace(".html", "_instrumented.html");
			try{
				driver.get(newTestRunner);
				System.out.println("Loading the URL " + newTestRunner);
			}catch (UnhandledAlertException ue)
			{
				try{((JavascriptExecutor) driver).executeScript("window.onbeforeunload = function(e){};");}
				catch(Exception e){System.out.println("Failed to close the popup!" + e);}
				Alert alert = driver.switchTo().alert();
				System.out.println(alert.getText());
			}   

			// TODO: make sure all tests are finished before getting the trace
			waitForPageToLoad();

			// Collect execution traces
			File[] files = new File(testsFolder).listFiles();
			for (File file : files) {
				if (file.isFile()) {
					String fileName = file.getName();
					if (!fileName.contains("qunit") && fileName.endsWith(".js") && !fileName.contains("instrumented"))
						jsFileNames.add(fileName);
				}
			}
			ArrayList traceList = null;
			HashMultimap<String, String> functionCallsMultiMap = HashMultimap.create();
			for (String jsFile: jsFileNames){
				try{
					System.out.println("Getting the traceList from " + jsFile);
					//System.out.println("return " + jsFile.replace(".js","") + "_getFuncionCallTrace();");
					traceList = (ArrayList)((JavascriptExecutor) driver).executeScript("return " + jsFile.replace(".js","").replace("-", "_") + "_getFuncionCallTrace();");
					System.out.println("traceList: " + traceList);
					Map<String,String> traceMap;
					for (int i=0; i<traceList.size(); i++){
						traceMap = (Map<String,String>)(traceList.get(i));
						String testFunction = traceMap.get("testFunction");
						String calledFunctionName = traceMap.get("functionName");
						functionCallsMultiMap.put(testFunction, calledFunctionName);
					}
				}
				catch(Exception e){
					System.out.println("Failed to execute " + jsFile.replace(".js","") + " _getFuncionCallTrace();" + e);
				}
			}

			int numUniqueFunCalls = 0;
			int maxUniqueFunCalls = 0;
			for (String testFunc : functionCallsMultiMap.keySet()) {
				Set<String> calledFunc = functionCallsMultiMap.get(testFunc);
				System.out.println(testFunc + ": " + calledFunc);
				if (calledFunc.size() > maxUniqueFunCalls)
					maxUniqueFunCalls = calledFunc.size(); 
				numUniqueFunCalls += calledFunc.size();
			}

			System.out.println("numUniqueFunCalls: " + numUniqueFunCalls);
			if (functionCallsMultiMap.keySet().size()!=0) 
				System.out.println("aveUniqueFunCalls: " + numUniqueFunCalls/functionCallsMultiMap.keySet().size());
			System.out.println("maxUniqueFunCalls: " + maxUniqueFunCalls);
			driver.quit();
		}
	}



	public void driverExecute(String javascript) throws Exception {
		((JavascriptExecutor) driver).executeScript(javascript);
	}


	private static void waitForPageToLoad() {  // could be used to make sure the js code execution happens after the page is fully loaded
		String pageLoadStatus = null;
		do {
			pageLoadStatus = (String)((JavascriptExecutor) driver).executeScript("return document.readyState");
			System.out.print(".");
		} while ( !pageLoadStatus.equals("complete") );
		System.out.println();
		System.out.println("Page Loaded.");
	}

}
