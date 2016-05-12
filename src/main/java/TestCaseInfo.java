import org.json.JSONArray;
import org.json.JSONException;

public class TestCaseInfo {
	private String name;
	private String type;
	private boolean array;
	private TestCaseInfo enclosingVariable;

	public TestCaseInfo(String name, String type, boolean array, TestCaseInfo enclosingVariable) {
		this.name = name;
		this.type = type;
		this.array = array;
		this.enclosingVariable = enclosingVariable;
	}

	private String getValue(Object value) throws JSONException {
		if (isArray() && value instanceof JSONArray) {
			return getArray((JSONArray) value, type);
		} else {
			return getValue(value, type);
		}
	}

	private String getValue(Object value, String type) {
		if (value == null) {
			return "null";
		}
		if (type.equals("string")) {
			/* make sure it fits on 1 line by removing new line chars */
			value = removeNewLines(value.toString());
			/* escape quotes */
			value = ((String) value).replaceAll("\\\"", "\\\\\"");
			return "\"" + value.toString() + "\"";

		} else if (type.equals("number")) {
			return value.toString();

		} else if (type.equals("boolean")) {
			if (value.toString().equals("true")) {
				return "1";
			} else {
				return "0";
			}
		} else if (type.equals("object")) {
			return "\"" + value.toString() + "\"";
		}
		return null;
	}

	private static String removeNewLines(String html) {
	return html.replaceAll("[\\t\\n\\x0B\\f\\r]", "");
	}
	
	private String getArray(JSONArray array, String type) throws JSONException {
		String result = "[";

		for (int i = 0; i < array.length(); i++) {
			if (i != 0) {
				result += " ";
			}
			result += getValue(array.get(i), type);
		}
		return result + "]";
	}

	String getDeclaration() {
		StringBuffer varDecl = new StringBuffer();

		if (isArray()) {
			varDecl.append("\tvariable " + name + "[..]\n");
			varDecl.append("\t\tvar-kind array\n\t\tarray 1\n");
			varDecl.append("\t\tenclosing-var " + getEnclosingVariable().getName() + "\n");
		} else {
			varDecl.append("\tvariable " + name + "\n");
			varDecl.append("\t\tvar-kind field " + name + "\n");
		}
		varDecl.append("\t\tdec-type " + type + "\n");
		varDecl.append("\t\trep-type ");

		if (type.equals("string")) {
			varDecl.append("java.lang.String");
		} else if (type.equals("boolean")) {
			varDecl.append("boolean");
		} else if (type.equals("undefined") || type.equals("function") || type.equals("object")
		        || type.equals("pointer")) {
			/*
			 * for undefined, declare as hashcode. this doesn't really matter because it was
			 * apparently never assigned a value. (at least we did not log a value).
			 */
			varDecl.append("hashcode");
		} else if (type.equals("number")) {
			/* number might be int or double. for now use double to be sure */
			varDecl.append("double");
		} 

		if (isArray()) {
			varDecl.append("[]");
		}
		varDecl.append("\n");

		return varDecl.toString();
	}

	public static TestCaseInfo parse(JSONArray var) throws JSONException {
		/* retrieve the three values from the array */
		String name = var.getString(0);
		String type = (String) var.getString(1);
		Object value;
		try {
			value = var.getJSONArray(2);
		} catch (JSONException e) {
			value = var.getString(2);
			/* make sure it fits on 1 line by removing new line chars */
			value = removeNewLines((String) value);
			/* escape quotes */
			value = ((String) value).replaceAll("\\\"", "\\\\\"");
		}

		if (type.endsWith("_array")) {

			type = type.replaceAll("_array", "");

			TestCaseInfo enclosingVariable = new TestCaseInfo(name, "pointer", false, null);

			return new TestCaseInfo(name, type, true, enclosingVariable);
		} else {
			return new TestCaseInfo(name, type, false, null);
		}
	}
}
