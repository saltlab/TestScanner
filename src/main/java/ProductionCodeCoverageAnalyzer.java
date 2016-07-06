import instrumentor.JSASTInstrumentor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.CopyUtils;
import org.apache.commons.io.FileUtils;
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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class ProductionCodeCoverageAnalyzer {

	//private static String subjectCoverageFolder = "ButtonsCoverageReport";
	//private static String subjectCoverageFolder = "jquery-placeholderCoverageReport";
	//private static String subjectCoverageFolder = "backboneCoverageReport";
	private static String subjectCoverageFolder = "requirejsCoverageReport";
	

	private static String coverageReportPath = "/Users/aminmf/Downloads/JSCover-1.0.23/target/" + subjectCoverageFolder;
	private static String jsCoveragePath = coverageReportPath + "/jscoverage.json";
	private static String jsSourcePath = coverageReportPath + "/original-src/";

	private static ArrayList<String> instrumentedJSFiles = new ArrayList<String>();

	private static ArrayList<String> jsFileNames = new ArrayList<String>();
	private static JSAnalyzer codeAnalyzer;
	private static TraceAnalyzer traceAnalyzer = new TraceAnalyzer();

	private static int coveredRegularFunc;
	private static int missedRegularFunc;
	private static int coveredCallback;
	private static int missedCallback;
	private static int coveredAsyncCallback;
	private static int missedAsyncCallback;
	private static int coveredEventCallback;
	private static int missedEventCallback;
	private static int coveredClosure;
	private static int missedClosure;
	private static int coveredDOMRelated;
	private static int missedDOMRelated;
	private static int neverExecFunCallSites;
	private static int totalMissedStatementLinesInMissedFunctionCounter;
	private static int totalMissedStatementLines;


	public static void main(String[] args) throws Exception {
		// Load JSON coverage report file
		try {
			ArrayList<String> jsFileList = new ArrayList<String>();
			ArrayList<String> jsFileCanonicalPathList = new ArrayList<String>();

			ArrayList<Integer> coveredStatementLines = new ArrayList<Integer>();
			ArrayList<Integer> missedStatementLines = new ArrayList<Integer>();
			ArrayList<Integer> coveredFunctionsIndices = new ArrayList<Integer>();
			ArrayList<Integer> missedFunctionsIndices = new ArrayList<Integer>();

			// read the json file
			FileReader reader = new FileReader(jsCoveragePath);
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

			File dir = new File("../../../Downloads/JSCover-1.0.23/target/"  + subjectCoverageFolder + "/original-src/");
			String[] extensions = new String[] { "js" };
			System.out.println("Getting all .js files in " + dir.getCanonicalPath() + " including those in subdirectories");
			List<File> files = (List<File>) FileUtils.listFiles(dir, extensions, true);
			for (File file : files) {
				String jsFile = file.getCanonicalPath().substring(file.getCanonicalPath().indexOf("/original-src/")+13);
				jsFileCanonicalPathList.add(file.getCanonicalPath());
				//System.out.println(file.getCanonicalPath().substring(file.getCanonicalPath().indexOf("/original-src/")+13));
				//System.out.println(file.getCanonicalPath());


				System.out.println(jsFile);
				//if (jsFile.equals("/testAnalysisProject/zeroclipboard/src/js/client/state.js"))
				//	continue;
				//System.out.println(jsonObject.get(jsFile));
				JSONObject innerObj = (JSONObject) jsonObject.get(jsFile);
				if (innerObj == null) // bypass libs or externals that are ignored when reporting coverage
					continue;
				//System.out.println("lineData: " + innerObj.get("lineData"));
				ArrayList lineData = (ArrayList)innerObj.get("lineData");
				for (int i=1; i<lineData.size(); i++){
					//System.out.println(lineData.get(i));
					if (lineData.get(i)!=null && !lineData.get(i).toString().equals("0")){//  coveredStatementLines
						coveredStatementLines.add(i);
						//System.out.println(i);
					}else{
						if (lineData.get(i)!=null) 
							missedStatementLines.add(i);
					}
				}
				System.out.println("coveredStatementLines: " + coveredStatementLines);
				System.out.println("missedStatementLines: " + missedStatementLines);

				
				ArrayList functionData = (ArrayList)innerObj.get("functionData");
				for (int i=0; i<functionData.size(); i++){
					//System.out.println("++++" + functionData.get(i));
					if (functionData.get(i)!=null && !functionData.get(i).toString().equals("0")){//  coveredStatementLines
						coveredFunctionsIndices.add(i);
					}else{
						if (functionData.get(i)!=null) 
							missedFunctionsIndices.add(i);
					}
				}

				//System.out.println("coveredFunctionsIndices: " + coveredFunctionsIndices);
				//System.out.println("missedFunctionsIndices: " + missedFunctionsIndices);

				
				analyseJSFile(file.getCanonicalPath(), coveredStatementLines, missedStatementLines, coveredFunctionsIndices, missedFunctionsIndices);

				coveredStatementLines.clear();
				missedStatementLines.clear();
				coveredFunctionsIndices.clear();
				missedFunctionsIndices.clear();
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ParseException ex) {
			ex.printStackTrace();
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}

	}


	private static void analyseJSFile(String canonicalPath, ArrayList<Integer> coveredStatementLines, ArrayList<Integer> missedStatementLines, ArrayList<Integer> coveredFunctionsIndices, ArrayList<Integer> missedFunctionsIndices) throws Exception {
		codeAnalyzer = new JSAnalyzer(new JSASTInstrumentor(), jsSourcePath, null);	
		File jsFile = new File(canonicalPath);
		String fileName = jsFile.getName();

		System.out.println(canonicalPath);
		codeAnalyzer.setJSFileName(fileName);
		codeAnalyzer.setJSAddress(canonicalPath);
		codeAnalyzer.analyzeProductionCodeCoverage(coveredStatementLines, missedStatementLines, coveredFunctionsIndices, missedFunctionsIndices);

		coveredRegularFunc += codeAnalyzer.getCoveredRegularFunc();
		missedRegularFunc += codeAnalyzer.getMissedRegularFunc();
		coveredCallback += codeAnalyzer.getCoveredCallback();
		missedCallback += codeAnalyzer.getMissedCallback();
		coveredAsyncCallback += codeAnalyzer.getCoveredAsyncCallback();
		missedAsyncCallback += codeAnalyzer.getMissedAsyncCallback();
		coveredEventCallback += codeAnalyzer.getCoveredAsyncCallback();
		missedEventCallback += codeAnalyzer.getMissedEventCallback();
		coveredClosure += codeAnalyzer.getCoveredClosure();
		missedClosure += codeAnalyzer.getMissedClosure();
		coveredDOMRelated += codeAnalyzer.getCoveredDOMRelated();
		missedDOMRelated += codeAnalyzer.getMissedDOMRelated();
		neverExecFunCallSites +=  codeAnalyzer.getNeverExecFunCallSites();
		totalMissedStatementLinesInMissedFunctionCounter += codeAnalyzer.getTotalMissedStatementLinesInMissedFunctionCounter();
		totalMissedStatementLines += codeAnalyzer.getTotalMissedStatementLines();
		
		
		System.out.println("==========================");
		System.out.println("++++ coveredRegularFunc: " + coveredRegularFunc);
		System.out.println("++++ missedRegularFunc: " + missedRegularFunc);
		System.out.println("++++ coveredCallback: " + coveredCallback);
		System.out.println("++++ missedCallback: " + missedCallback);
		System.out.println("++++ coveredAsyncCallback: " + coveredAsyncCallback);
		System.out.println("++++ missedAsyncCallback: " + missedAsyncCallback);
		System.out.println("++++ coveredEventCallback: " + coveredEventCallback);
		System.out.println("++++ missedEventCallback: " + missedEventCallback);
		System.out.println("++++ coveredClosure: " + coveredClosure);
		System.out.println("++++ missedClosure: " + missedClosure);
		System.out.println("++++ coveredDOMRelated: " + coveredDOMRelated);
		System.out.println("++++ missedDOMRelated: " + missedDOMRelated);
		System.out.println("++++ neverExecFunCallSites: " + neverExecFunCallSites);
		
		float ratio = 0;
		System.out.println("@ Total missed statement lines in missed functioncounter = " + totalMissedStatementLinesInMissedFunctionCounter);
		System.out.println("@ Total number of missed statements = " + totalMissedStatementLines);
		if (totalMissedStatementLinesInMissedFunctionCounter!=0){
			ratio = (float)totalMissedStatementLinesInMissedFunctionCounter/(float)totalMissedStatementLines;
			System.out.println("@ Percentage of missed statement in missed functions = " + ratio*100 + "%");
		}

		
		System.out.println("==========================");
		System.out.print(coveredRegularFunc + "\t" + missedRegularFunc + "\t" + coveredCallback + "\t" + missedCallback + "\t" + 
				coveredAsyncCallback + "\t" + missedAsyncCallback + "\t" + coveredEventCallback + "\t" + missedEventCallback + "\t" + 
				coveredClosure + "\t" + missedClosure + "\t" + coveredDOMRelated + "\t" + missedDOMRelated + "\t");

		float regFuncCoverage , callbackCoverage, asyncCallbackCoverage, eventCallbackCoverage, closureCoverage, DOMAccessCoverage;
		if (coveredRegularFunc+missedRegularFunc!=0){
			regFuncCoverage = (float)coveredRegularFunc/(float)(coveredRegularFunc+missedRegularFunc);
			System.out.print(regFuncCoverage*100 + "%\t");
		}else
			System.out.print("\t");
		if (coveredCallback+missedCallback!=0){
			callbackCoverage = (float)coveredCallback/(float)(coveredCallback+missedCallback);
			System.out.print(callbackCoverage*100 + "%\t");
		}else
			System.out.print("\t");
		if (coveredAsyncCallback+missedAsyncCallback!=0){
			asyncCallbackCoverage = (float)coveredAsyncCallback/(float)(coveredAsyncCallback+missedAsyncCallback);
			System.out.print(asyncCallbackCoverage*100 + "%\t");
		}else
			System.out.print("\t");
		if (coveredEventCallback+missedEventCallback!=0){
			eventCallbackCoverage = (float)coveredEventCallback/(float)(coveredEventCallback+missedEventCallback);
			System.out.print(eventCallbackCoverage*100 + "%\t");
		}else
			System.out.print("\t");
		if (coveredClosure+missedClosure!=0){
			closureCoverage = (float)coveredClosure/(float)(coveredClosure+missedClosure);
			System.out.print(closureCoverage*100 + "%\t");
		}else
			System.out.print("\t");
		if (coveredDOMRelated+missedDOMRelated!=0){
			DOMAccessCoverage = (float)coveredDOMRelated/(float)(coveredDOMRelated+missedDOMRelated);
			System.out.print(DOMAccessCoverage*100 + "%\t");
		}else
			System.out.print("\t");
		
		System.out.print(neverExecFunCallSites + "\t" + ratio*100 + "%\n");
	
		
		System.out.println("==========================");
		
	}

}
