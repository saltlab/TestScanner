import instrumentor.JSASTInstrumentor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.CopyUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
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

public class TestCodePropertyAnalyzer {

	//private static String testsFramework = "qunit";  // {"qunit", "jasmine", "mocha"}
	//private static String testsFolder = "/Users/aminmf/Documents/JavaScriptTestsStudy/popularJS/velocity/test/";
	//private static String testsFolder = "/Users/aminmf/Documents/JavaScriptTestsStudy/popularJS/lazysizes/tests/";   // => QUnit.test.apply
	//private static String testsFolder = "/Users/aminmf/Documents/JavaScriptTestsStudy/popularJS/ember.js/tests/"; => Wrong answer! also consider node-test folder
	//private static String testsFolder = "/Users/aminmf/Documents/JavaScriptTestsStudy/popularJS/sizzle/test/"; => Wrong answer!
	//private static String testsFolder = "/Users/aminmf/Documents/JavaScriptTestsStudy/popularJS/moment/src/test/";
		

	private static String testsFramework = "jasmine";
	private static String testsFolder = "/Users/aminmf/Documents/JavaScriptTestsStudy/popularJS/dropzone/test";

	
	
	//private static String testsFramework = "mocha";
	//private static String testsFolder = "/Users/aminmf/Documents/JavaScriptTestsStudy/popularJS/dropzone/test";
	//private static String testsFolder = "/Users/aminmf/Documents/JavaScriptTestsStudy/popularJS/material-ui/test";
	//private static String testsFolder = "/Users/aminmf/Documents/JavaScriptTestsStudy/popularJS/redux/test";
	//private static String testsFolder = "/Users/aminmf/Documents/JavaScriptTestsStudy/popularJS/snabbt.js/src/tests";
	//private static String testsFolder = "/Users/aminmf/Documents/JavaScriptTestsStudy/popularJS/webpack/test";
	//private static String testsFolder = "/Users/aminmf/Documents/JavaScriptTestsStudy/popularJS/component/test";

		
	
	private static String[] excludeFolders = {"assets", "coverage", "lib", "libs", "casper", "lcov-report"	};		
	

	private static ArrayList<String> jsFileNames = new ArrayList<String>();
	private static JSAnalyzer codeAnalyzer;
	private static TraceAnalyzer traceAnalyzer = new TraceAnalyzer();
	private static int NumTests;
	private static int NumAsyncTests;
	private static int NumTestModules;
	private static int NumAssertions;
	private static int NumFunCallTest = 0;
	private static int NumFunCallTestModule = 0;
	private static int NumTotalFunCall = 0;
	private static int MaxFunCall = 0;
	private static float AveFunCall = 0;
	private static int NumTriggerInTest;
	private static int NumObjCreate;

	public static void main(String[] args) throws Exception {

		codeAnalyzer = new JSAnalyzer(new JSASTInstrumentor(), testsFolder, null);		

		// For each .js test file
		System.out.println("Test framework: " + testsFramework);
		File[] files = new File(testsFolder).listFiles();
		if (files==null){
			System.out.println("No file found in directory: " + testsFolder);
			return;
		}
		for (File file : files)
			processFile(file);

	}

	private static void processFile(File file) throws IOException, Exception {
		if (ArrayUtils.contains(excludeFolders, file.getName())){
			System.out.println("*** Analysis excluded for: " + file.getName());
			return;
		}
		if (file.isDirectory()){
			System.out.println("*** Analysing directory: " + file.getAbsolutePath().replace(testsFolder, ""));
			File[] files = file.listFiles();
			if (files==null){
				System.out.println("No test file found in directory: " + file.getAbsolutePath().replace(testsFolder, ""));
				return;
			}
			for (File innerFile : files)
				processFile(innerFile);
		}
		if (file.isFile()) {
			String fileName = file.getName();
			//if (fileName.endsWith(".js")){
			if (!fileName.contains("qunit") && fileName.endsWith(".js") && !fileName.contains("jquery")) {
				//&& !fileName.equals("es.js") && !fileName.equals("helpers.js") && !fileName.equals("karma.conf.js") && !fileName.equals("es.js") && !fileName.equals("library.js")
				//){
				analyseJSTestFile(file.getCanonicalPath());
			}
		}
		
	}
	
	

	private static void analyseJSTestFile(String canonicalPath) throws Exception {
		/*
	 	NumTests: Number of tests
		NumAsyncTests: Number of async tests
		NumAssertions: Number of assertions
		MaxFunCall: Maximum number of unique function calls per test
		AveFunCall: Average number of unique function calls per test
		NumDOMFixture: Number of DOM fixtures in the test suite
		NumTriggerTest: Number of tests with event triggering methods
		NumObjCreate: Number of objects creation in the test suite
		*/
		File jsFile = new File(canonicalPath);
		String fileName = jsFile.getName();

		System.out.println(canonicalPath);
		codeAnalyzer.setJSFileName(fileName);
		codeAnalyzer.setJSAddress(canonicalPath);
		codeAnalyzer.setTestFramework(testsFramework);
		//codeAnalyzer.setJSAddress(testsFolder + "/" + fileName);
		System.out.println("Analysing the test suite in file " + fileName);
		codeAnalyzer.analyzeTestCodeProperties();
		
		
		NumTests += codeAnalyzer.getNumTests();
		NumAsyncTests += codeAnalyzer.getNumAsyncTests();
		NumTestModules += codeAnalyzer.getNumTestModules();
		NumAssertions += codeAnalyzer.getNumAssertions();
		NumFunCallTest += codeAnalyzer.getNumFunCallTest();
		NumFunCallTestModule += codeAnalyzer.getNumFunCallTestModule();
		if (codeAnalyzer.getMaxFunCallTest() > MaxFunCall)
			MaxFunCall = codeAnalyzer.getMaxFunCallTest();
		AveFunCall  = (float)NumFunCallTest/(float)NumTests;
		NumTriggerInTest += codeAnalyzer.getNumTriggerTest();
		NumObjCreate += codeAnalyzer.getNumObjCreate();
		
		
		System.out.println("==========================");
		System.out.println("++++ NumTests: " + NumTests);
		System.out.println("++++ NumAsyncTests: " + NumAsyncTests);
		System.out.println("++++ NumTestModules: " + NumTestModules);
		System.out.println("++++ NumAssertions: " + NumAssertions);
		System.out.println("++++ NumFunCall: " + NumFunCallTest);
		System.out.println("++++ MaxFunCall: " + MaxFunCall);
		System.out.println("++++ AveFunCall: " + AveFunCall);
		System.out.println("++++ NumTriggerInTest: " + NumTriggerInTest);
		System.out.println("++++ NumObjCreate: " + NumObjCreate);
		System.out.println("==========================");

		System.out.println(NumTests + "\t" + NumAsyncTests + "\t" + NumTestModules + "\t" + NumAssertions + "\t" + NumFunCallTest + "\t" + MaxFunCall + "\t" + AveFunCall + "\t" + NumTriggerInTest + "\t" + NumObjCreate);
	}

	
}


