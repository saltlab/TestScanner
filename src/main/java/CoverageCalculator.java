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
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;

import core.JSAnalyzer;
import core.TraceAnalyzer;

public class CoverageCalculator {

	//private static String repositoryName = "validity";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/validity/test/validitytests.html";

	//private static String repositoryName = "jQuery-Heart-Beater";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/jQuery-Heart-Beater/tests/index.html";
	
	//private static String repositoryName = "Stalwart";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/Stalwart/tests/index.html";
	
	//private static String repositoryName = "site-com-trium";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/site-com-trium/js/tests.html";

	//private static String repositoryName = "matrix";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/matrix/test/index.html";
	
	//private static String repositoryName = "vlaams-parlement";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/vlaams-parlement/unit_tests.html";
	
	//private static String repositoryName = "sort-util-js";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/sort-util-js/test/index.html";

	//private static String repositoryName = "theAsteroids";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/theAsteroids/tests/index.html";

	//private static String repositoryName = "web-storage";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/web-storage/tests.html";
	
	//private static String repositoryName = "sudokujs";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/sudokujs/test/sudoku-test.html";
	
	//private static String repositoryName = "analytics";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/analytics/js/tests/QUnitRunnerQuery.html";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/analytics/js/tests/QUnitRunnerDisplay.html";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/analytics/js/tests/QUnitRunnerFactSelector.html";
	
	//private static String repositoryName = "ProjetoAmazingWorld";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/ProjetoAmazingWorld/test/index.html";
	
	//private static String repositoryName = "TomatoTimer";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/TomatoTimer/test.html";
		
	//private static String repositoryName = "teste-apontador";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/teste-apontador/tests/index.html";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/teste-apontador/tests/index_integration.html";

	//private static String repositoryName = "confero";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/confero/test/index.html";

	//private static String repositoryName = "fxosRate";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/fxosRate/tests.html";
		
	//private static String repositoryName = "Dable";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/Dable/test/test.html";

	//private static String repositoryName = "jsQuad";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/jsQuad/test/index.html";

	//private static String repositoryName = "jsgraph";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/jsgraph/test/test.html";

	//private static String repositoryName = "PackPack";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/PackPack/testing/testing.html";

	//private static String repositoryName = "LocalStorageCache";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/LocalStorageCache/test/index.html";
	
	//private static String repositoryName = "ticktacktoe";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/ticktacktoe/test.html";

	//private static String repositoryName = "implementsJs";
	///private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/implementsJs/test.html";

	//private static String repositoryName = "webuploader";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/webuploader/test/index.html";
		
	//private static String repositoryName = "articles";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/articles/proc_slide/qunit/base.tests.html";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/articles/proc_slide/qunit/entity.tests.html";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/articles/proc_slide/qunit/node.tests.html";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/articles/proc_slide/qunit/timeline.tests.html";
		
	//private static String repositoryName = "majora";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/majora/majora-tests.html";
	
	//private static String repositoryName = "analytics";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/analytics/js/tests/QUnitRunnerDisplay.html";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/analytics/js/tests/QUnitRunnerFactSelector.html";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/analytics/js/tests/QUnitRunnerQuery.html";
	
	
	//private static String repositoryName = "System_js";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/System_js/1.0/tests/index.html";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/System_js/2.0/public_html/tests/core.html";
	
	//private static String repositoryName = "Mocky";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/Mocky/test/index.html";

	//private static String repositoryName = "hybrid-events";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/hybrid-events/test/runner.html";
	
	//private static String repositoryName = "storage";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/storage/StorageJS.Core/tests/cache/cache-tests.html";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/storage/StorageJS.Core/tests/provider/provider-tests.html";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/storage/StorageJS.Core/tests/storage/storage-tests.html";
	
	//private static String repositoryName = "Roguelike";
	//private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/Roguelike/test.html";
	
	private static String repositoryName = "Leaflet";
	private static String htmlTestRunner = "http://localhost:8888/testAnalysisProject/Leaflet/spec/index.html";
	// java -jar target/dist/JSCover-all.jar -ws --proxy --port=3128 --report-dir=target/jscover --log=WARNING --no-instrument=/testAnalysisProject/Leaflet/spec/
	
	
	private static WebDriver driver;

	public static void main(String[] args) throws Exception {


		FirefoxProfile profile = new FirefoxProfile();
		profile.setPreference("network.proxy.http", "localhost");
		profile.setPreference("network.proxy.http_port", 3128);
		profile.setPreference("network.proxy.type", 1);
		profile.setPreference("network.proxy.no_proxies_on", "");
		driver = new FirefoxDriver(profile);
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

		// Load the html test runner in the browser to get coverage
		try{
			driver.get(htmlTestRunner);
			System.out.println("Loading the URL " + htmlTestRunner);
		}catch (UnhandledAlertException ue)
		{
			try{((JavascriptExecutor) driver).executeScript("window.onbeforeunload = function(e){};");}
			catch(Exception e){System.out.println("Failed to close the popup!" + e);}
			//Alert alert = driver.switchTo().alert();
			//System.out.println(alert.getText());
		}   

		// make sure all tests are finished before getting the trace
		waitForPageToLoad();

		try{
			((JavascriptExecutor) driver).executeScript("return jscoverage_report('" + repositoryName + "CoverageReport');");
			System.out.println("Coverage report generated.");
		}
		catch(Exception e){
			System.out.println("Failed to execute function " + e);
		}

		driver.quit();
	}



	public void driverExecute(String javascript) throws Exception {
		((JavascriptExecutor) driver).executeScript(javascript);
	}


	private static void waitForPageToLoad() {  // could be used to make sure the js code execution happens after the page is fully loaded
		String pageLoadStatus = null;
		do {
			pageLoadStatus = (String)((JavascriptExecutor) driver).executeScript("return document.readyState");
			System.out.print(".");
		} while (!pageLoadStatus.equals("complete"));
		//} while (!driver.findElement(By.className("result")).getText().contains("completetd"));// && !pageLoadStatus.equals("complete"));
		System.out.println();
		System.out.println("Page Loaded.");
	}

}
