import instrumentor.JSASTInstrumentor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;

import core.JSAnalyzer;
import core.TraceAnalyzer;

public class RepoClientSideChecker {

	private static String[] repoList = {
		"https://github.com/madrobby/keymaster","https://github.com/docker/kitematic","https://github.com/loadfive/Knwl.js","https://github.com/koajs/koa","https://github.com/aFarkas/lazysizes","https://github.com/javve/list.js","https://github.com/mozilla/localForage","https://github.com/strongloop/loopback","https://github.com/desandro/masonry","https://github.com/angular/material","https://github.com/callemall/material-ui","https://github.com/linnovate/mean","https://github.com/yabwe/medium-editor","https://github.com/mozilla/metrics-graphics","https://github.com/lhorie/mithril.js","https://github.com/Modernizr/Modernizr","https://github.com/Automattic/mongoose","https://github.com/ccampbell/mousetrap","https://github.com/ngbp/ngbp","https://github.com/segmentio/nightmare","https://github.com/nodejs/node","https://github.com/jscs-dev/node-jscs","https://github.com/nodemailer/nodemailer","https://github.com/jaredreich/notie.js","https://github.com/rstacruz/nprogress","https://github.com/jimhigson/oboe.js","https://github.com/ParsePlatform/parse-server","https://github.com/guillaumepotier/Parsley.js","https://github.com/jaredhanson/passport","https://github.com/qiao/PathFinding.js","https://github.com/scottjehl/picturefill","https://github.com/plotly/plotly.js","https://github.com/Unitech/PM2","https://github.com/NetEase/pomelo","https://github.com/purifycss/purifycss","https://github.com/quilljs/quill","https://github.com/twbs/ratchet","https://github.com/react-bootstrap/react-bootstrap","https://github.com/rackt/react-router","https://github.com/rackt/redux","https://github.com/facebook/relay","https://github.com/jrburke/requirejs","https://github.com/scottjehl/Respond","https://github.com/hakimel/reveal.js","https://github.com/riot/riot","https://github.com/Reactive-Extensions/RxJS","https://github.com/balderdashy/sails","https://github.com/sahat/satellizer","https://github.com/janpaepke/ScrollMagic","https://github.com/select2/select2","https://github.com/semantic-org/semantic-ui","https://github.com/jquery/sizzle","https://github.com/Prinzhorn/skrollr","https://github.com/Mango/slideout","https://github.com/jwagner/smartcrop.js","https://github.com/daniel-lundin/snabbt.js","https://github.com/feross/standard","https://github.com/etsy/statsd","https://github.com/swagger-api/swagger-ui","https://github.com/t4t5/sweetalert","https://github.com/NUKnightLab/TimelineJS","https://github.com/sbstjn/timesheet.js","https://github.com/CodeSeven/toastr","https://github.com/google/traceur-compiler","https://github.com/qrohlf/trianglify","https://github.com/twitter/typeahead.js","https://github.com/angular-ui/ui-router","https://github.com/julianshapiro/velocity","https://github.com/videojs/video.js","https://github.com/Matt-Esch/virtual-dom","https://github.com/maxwellito/vivus","https://github.com/auchenberg/volkswagen","https://github.com/Microsoft/vscode","https://github.com/webpack/webpack","https://github.com/madrobby/zepto","https://github.com/yaronn/blessed-contrib","https://github.com/dangrossman/bootstrap-daterangepicker","https://github.com/silviomoreto/bootstrap-select","https://github.com/jessepollak/card","https://github.com/jackmoore/colorbox","https://github.com/designmodo/Flat-UI","https://github.com/woothemes/FlexSlider","https://github.com/malsup/form","https://github.com/greensock/GreenSock-JS","https://github.com/goldfire/howler.js","https://github.com/usablica/intro.js","https://github.com/cubiq/iscroll","https://github.com/tuupola/jquery_lazyload","https://github.com/aterrien/jQuery-Knob","https://github.com/douglascrockford/JSON-js","https://github.com/sdelements/lets-chat","https://github.com/davatron5000/Lettering.js","https://github.com/dimsemenov/Magnific-Popup","https://github.com/olton/Metro-UI-CSS","https://github.com/needim/noty","https://github.com/dimsemenov/PhotoSwipe","https://github.com/mattermost/platform","https://github.com/Selz/plyr","https://github.com/maroslaw/rainyday.js","https://github.com/kriasoft/react-starter-kit","https://github.com/jlmakes/scrollreveal.js","https://github.com/kenwheeler/slick","https://github.com/RubaXa/Sortable","https://github.com/fgnass/spin.js","https://github.com/benweet/stackedit","https://github.com/thebird/Swipe","https://github.com/nolimits4web/swiper","https://github.com/HubSpot/tether","https://github.com/tommoor/tinycon","https://github.com/chrisaljoudi/ublock",
	};

	//private static String repoURL = "https://github.com/twbs/bootstrap";

	private static WebDriver driver;

	public static void main(String[] args) throws Exception {
		driver = new FirefoxDriver();

		for (int i = 0; i < repoList.length; i++){
			// Load the html test runner in the browser to get coverage
			driver.get(repoList[i]+"/search?utf8=???&q=browser%3Atrue");
			//System.out.print("Loading the URL " + repoList[i]);

			try{
				if (driver.findElement(By.cssSelector(".container > h1:nth-child(1)"))!=null){
					driver.quit();
					return;
				}
			}catch(Exception e){
				//System.out.println(e);
			}

			Thread.sleep(10000);

			String htmlSource = driver.getPageSource();

			//System.out.println(htmlSource);
			File file = new File(repoList[i].substring(repoList[i].lastIndexOf("/")+1) + ".html");
			System.out.println(file.getAbsoluteFile());
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(htmlSource);
			bw.close();

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
			//System.out.print(".");
		} while (!pageLoadStatus.equals("complete"));
		System.out.println();
		System.out.println("Page Loaded.");
	}

}
