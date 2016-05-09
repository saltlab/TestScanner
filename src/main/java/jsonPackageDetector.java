import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class jsonPackageDetector {

	private static String mainFolder = "/Applications/MAMP/htdocs/testAnalysisProject/Roguelike";
	private static String appFolder = "/Applications/MAMP/htdocs/testAnalysisProject/Roguelike";


	public static void main(String[] args) throws Exception {

		
		File root = new File("/Users/aminmf/Documents/JavaScriptTestsStudy/popularJS");
		String[] names = root.list();

		for(String name : names)
		{
			File check = new File("/Users/aminmf/Documents/JavaScriptTestsStudy/popularJS/" + name);
		    if (check.isDirectory())
		    {
		        //System.out.println("Analyzing " + name + " ...");
				File[] files = new File("/Users/aminmf/Documents/JavaScriptTestsStudy/popularJS/" + name).listFiles();
				for (File file : files) {
					if (file.isFile()) {
						String fileName = file.getName();
						if (fileName.equals("package.json") || fileName.equals("Package.json")){
							//System.out.println("Found  " + fileName + " in " + name);
							System.out.println(name);
							//System.out.println("Server");
						}
					}
				}
		    }
		}		

	}

}
