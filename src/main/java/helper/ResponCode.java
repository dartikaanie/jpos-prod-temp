package helper;

import java.util.HashMap;
import java.util.Map;

public class ResponCode {
	
	public ResponCode() {
		
	}

	static Map<String,String> mapRC = new HashMap();
	public static void setupResponCode() {
		mapRC.put("00", "APPROVED");
		mapRC.put("01", "Refer to card issuer");
		mapRC.put("03", "Invalid Merchant");
		mapRC.put("04", "Pick-up capture card");
		mapRC.put("05", "Undefined Error");
		mapRC.put("12", "Invalid Transaction");
		mapRC.put("13", "Invalid Amount");
		mapRC.put("14", "Invalid Card Number");
		mapRC.put("15", "No Such Issuer");
		mapRC.put("20", "Invalid Response");
		mapRC.put("30", "Format Error");
		mapRC.put("31", "Bank or Collecting Agent not supported by switch");
		mapRC.put("33", "Expired Card");
		mapRC.put("36", "Restricted Card");
		mapRC.put("38", "Allowed PIN tries exceeded");
		mapRC.put("39", "No credit account");
		mapRC.put("40", "Requested function not supported");
		mapRC.put("41", "Lost card");
		mapRC.put("43", "Stolen card");
		mapRC.put("51", "Insufficient funds/over credit limit");
		mapRC.put("52", "No chequing account");
		mapRC.put("53", "No saving account");
		mapRC.put("54", "Expired card");
		mapRC.put("55", "APPROVInvalid PINED");
		mapRC.put("57", "Transaction not permitted to cardholder");
		mapRC.put("58", "Transaction not permitted to terminal");
		mapRC.put("61", "Exceeds withdrawal amount limit");
		mapRC.put("62", "Restricted card");
		mapRC.put("63", "Security violation");
		mapRC.put("65", "Exceeds withdrawal frequency limit");
		mapRC.put("67", "Hard capture (requires that card be picked up at ATM)");
		mapRC.put("68", "Response received too late");
		mapRC.put("69", "Timeout in biller's database");
	}
	
	public static String getRC(String code) {
		setupResponCode();
		return mapRC.get(code);
	}
	
	public static String  RC_01 = "Refer to card issuer";
	public static String  RC_03 = "Invalid Merchant";
	public static String  RC_04 = "Pick-up capture card";
	public static String  RC_05 = "Undefined Error";
	public static String  RC_12 = "Invalid Transaction";
	public static String  RC_13 = "Invalid Amount";
	public static String  RC_14 = "Invalid Card Number";
	public static String  RC_15 = "No Such Issuer";
	public static String  RC_20 = "Invalid Response";
	public static String  RC_30 = "Format Error";
	public static String  RC_31 = "Bank or Collecting Agent not supported by switch";
	public static String  RC_33 = "Expired Card";
	public static String  RC_36 = "Restricted Card";
	public static String  RC_38 = "Allowed PIN tries exceeded";
	public static String  RC_39 = "No credit account";
	public static String  RC_40 = "Requested function not supported";
	public static String  RC_41 = "Lost card";
	public static String  RC_43 = "Stolen card";
	public static String  RC_51 = "Insufficient funds/over credit limit";
	public static String  RC_52 = "No chequing account";
	public static String  RC_53 = "No saving account";
	public static String  RC_54 = "Expired card";
	public static String  RC_55 = "Invalid PIN";
	public static String  RC_57 = "Transaction not permitted to cardholder";
	public static String  RC_58 = "Transaction not permitted to terminal";
	public static String  RC_61 = "Exceeds withdrawal amount limit";
	public static String  RC_62 = "Restricted card";
	public static String  RC_63 = "Security violation";
	public static String  RC_65 = "Exceeds withdrawal frequency limit";
	public static String  RC_67 = "Hard capture (requires that card be picked up at ATM)";
	public static String  RC_68 = "Response received too late";
	public static String  RC_69 = "Timeout in biller's database";
}
