package helper;

public class Helper {
	public static String PROC_SALE_DEFAULT = "000000";
	public static String PROC_SALE_SAVING = "001000";
	public static String PROC_SALE_CHEQUE = "002000";
	
	public static String PROC_VOID_DEFAULT = "020000";
	public static String PROC_VOID_SAVING = "021000";
	public static String PROC_VOID_CHEQUE = "022000";
	


	public static String formatingValue(String value, int length) {
		String result = value;
		for(int i=0; i < length-value.length(); i++) {
			result = "0"+result;
		}
		
		return result;
	}
}
