import instrumentor.JSASTInstrumentor;

import java.io.BufferedReader;
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

	//private static String coverageReportType = "json"; // json/lcov
	private static String coverageReportType = "lcov"; // json/lcov


	// *********** JSON FORMAT **************
	private static String jf_subjectCoverageFolder = "fueluxCoverageReport";
	private static String jf_coverageReportPath = "/Users/aminmf/Downloads/JSCover-1.0.23/target/" + jf_subjectCoverageFolder;
	private static String jf_jsCoveragePath = jf_coverageReportPath + "/jscoverage.json";
	private static String jf_jsSourcePath = jf_coverageReportPath + "/original-src/";

	// *********** LCOV FORMAT **************
	private static String lf_jsCoveragePath = "/Users/aminmf/Documents/JavaScriptTestsStudy/popularJS/timesheet.js/coverage/source/";    // look for TARGET.js.html 
	private static String lf_projectSourcePath = "/Users/aminmf/Documents/JavaScriptTestsStudy/popularJS/timesheet.js/";   // search for TARGET.js in the lf_projectSourcePath 


	private static ArrayList<String> jsFileNames = new ArrayList<String>();
	private static JSAnalyzer codeAnalyzer;

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

		ArrayList<Integer> coveredStatementLines = new ArrayList<Integer>();
		ArrayList<Integer> missedStatementLines = new ArrayList<Integer>();
		ArrayList<Integer> coveredFunctionsIndices = new ArrayList<Integer>();
		ArrayList<Integer> missedFunctionLines = new ArrayList<Integer>();

		if (coverageReportType.equals("json")){
			try {
				ArrayList<String> jsFileCanonicalPathList = new ArrayList<String>();
				// Load JSON coverage report file
				FileReader reader = new FileReader(jf_jsCoveragePath);
				JSONParser jsonParser = new JSONParser();
				JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
				File dir = new File("../../../Downloads/JSCover-1.0.23/target/"  + jf_subjectCoverageFolder + "/original-src/");
				String[] extensions = new String[] { "js" };
				System.out.println("Getting all .js files in " + dir.getCanonicalPath() + " including those in subdirectories");
				List<File> files = (List<File>) FileUtils.listFiles(dir, extensions, true);
				for (File file : files) {
					String jsFile = file.getCanonicalPath().substring(file.getCanonicalPath().indexOf("/original-src/")+13);
					jsFileCanonicalPathList.add(file.getCanonicalPath());
					//System.out.println(file.getCanonicalPath().substring(file.getCanonicalPath().indexOf("/original-src/")+13));
					//System.out.println(file.getCanonicalPath());
					System.out.println(jsFile);
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
						}
					}
					//System.out.println("coveredFunctionsIndices: " + coveredFunctionsIndices);
					//System.out.println("missedFunctionsIndices: " + missedFunctionsIndices);

					analyseJSFile(file.getCanonicalPath(), coveredStatementLines, missedStatementLines, coveredFunctionsIndices, null);

					coveredStatementLines.clear();
					missedStatementLines.clear();
					coveredFunctionsIndices.clear();
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

		}else if (coverageReportType.equals("lcov")){
			try {
				ArrayList<String> jsFileCanonicalPathList = new ArrayList<String>();
				File coverageDir = new File(lf_jsCoveragePath);
				File suorceDir = new File(lf_projectSourcePath);
				String[] coverageExtensions = new String[] { "js.html" };
				System.out.println("Getting all coverage report of js files in " + coverageDir.getCanonicalPath() + " including those in subdirectories.");
				List<File> coverageFiles = (List<File>) FileUtils.listFiles(coverageDir, coverageExtensions, true);
				for (File coverFile : coverageFiles) {
					String coverFileCanonPath = coverFile.getCanonicalPath();;
					//String jsFile = file.getCanonicalPath();
					//String jsFile = file.getCanonicalPath().substring(file.getCanonicalPath().lastIndexOf("/")+1);
					String jsFile = coverFileCanonPath.substring(coverFileCanonPath.indexOf(lf_jsCoveragePath)+lf_jsCoveragePath.length()).replace(".html", "");
					//System.out.println("Corresponding js file:" + jsFile);
					jsFileCanonicalPathList.add(coverFile.getCanonicalPath());

					System.out.println("Getting the corresponding js files in " + suorceDir.getCanonicalPath() + " including subdirectories.");
					String[] sourceExtensions = new String[] { "js" };
					List<File> source_files = (List<File>) FileUtils.listFiles(suorceDir, sourceExtensions, true);
					for (File source_file : source_files) {
						String jsSourceFile = source_file.getCanonicalPath();
						//String jsSourceFile = source_file.getCanonicalPath().substring(source_file.getCanonicalPath().lastIndexOf("/")+1);
						if (jsSourceFile.contains(jsFile)){
							System.out.println(jsSourceFile);
							System.out.println(coverFileCanonPath);
							// for lcov: read coverFile string by string and if equals cline-neutral, cline-yes, and cline-no, ...etc. start counetr from 1. add the counetr to ciovered/missed list


							BufferedReader br = new BufferedReader(new FileReader(coverFile));
							String line;
							int i = 0, lineCounter = 1;
							while ((line = br.readLine()) != null) {
								if (line.contains("cline-yes")){
									//System.out.println(line);
									coveredStatementLines.add(++i);
								}
								else if (line.contains("cline-no")){
									//System.out.println(line);
									missedStatementLines.add(++i);
								}
								else if (line.contains("cline-neutral"))
									i++;

								if (line.contains("prettyprint lang-js")){ // start reading the source code part in the html report
									while (!(line = br.readLine()).contains("</pre>")) {
										lineCounter++;
										if (line.contains("function not covered")){
											missedFunctionLines.add(lineCounter);
											//System.out.println(lineCounter);
										}
									}								
								}

							}
							//System.out.println("coveredStatementLines: " + coveredStatementLines);
							//System.out.println("missedStatementLines: " + missedStatementLines);

							/*
								if (functionData.get(i)!=null && !functionData.get(i).toString().equals("0")){//  coveredStatementLines
									coveredFunctionsIndices.add(i);
								}else{
									if (functionData.get(i)!=null) 
										missedFunctionsIndices.add(i);
								}
							 */

							//System.out.println("coveredFunctionsIndices: " + coveredFunctionsIndices);
							//System.out.println("missedFunctionsIndices: " + missedFunctionsIndices);

							analyseJSFile(jsSourceFile, coveredStatementLines, missedStatementLines, null, missedFunctionLines);

							coveredStatementLines.clear();
							missedStatementLines.clear();
						}
					}
				}
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			}catch (NullPointerException ex) {
				ex.printStackTrace();
			}

		}

	}


	private static void analyseJSFile(String canonicalPath, ArrayList<Integer> coveredStatementLines, ArrayList<Integer> missedStatementLines, ArrayList<Integer> coveredFunctionsIndices, ArrayList<Integer> missedFunctionLines) throws Exception {
		codeAnalyzer = new JSAnalyzer(new JSASTInstrumentor(), jf_jsSourcePath, null);	
		File jsFile = new File(canonicalPath);
		String fileName = jsFile.getName();

		System.out.println(canonicalPath);
		codeAnalyzer.setJSFileName(fileName);
		codeAnalyzer.setJSAddress(canonicalPath);
		codeAnalyzer.analyzeProductionCodeCoverage(coveredStatementLines, missedStatementLines, coveredFunctionsIndices, missedFunctionLines);

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
