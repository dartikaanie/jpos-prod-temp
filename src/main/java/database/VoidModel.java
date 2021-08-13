package database;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VoidModel {
	
	private String bit_0,bit_2, bit_3,bit_4, bit_11,bit_12,bit_13,bit_14,bit_22
	, bit_24,bit_25,bit_37,bit_38, bit_39, bit_41, bit_42,bit_55,bit_52,bit_60,bit_62, bit_63,bit_64,
	iso_request,iso_response,transaction_date;
	private String response_code, response_name;
	
	public String getTransaction_date() {
		return transaction_date;
	}
	public void setTransaction_date(String transaction_date) {
		this.transaction_date = transaction_date;
	}
	public String getIso_response() {
		return iso_response;
	}
	public void setIso_response(String iso_response) {
		this.iso_response = iso_response;
	}
	public String getIso_request() {
		return iso_request;
	}
	public void setIso_request(String iso_request) {
		this.iso_request = iso_request;
	}
	public String getBit_52() {
		return bit_52;
	}
	public void setBit_52(String bit_52) {
		this.bit_52 = bit_52;
	}
	public String getBit_0() {
		return bit_0;
	}
	public void setBit_0(String bit_0) {
		this.bit_0 = bit_0;
	}
	public String getBit_2() {
		return bit_2;
	}
	public void setBit_2(String bit_2) {
		this.bit_2 = bit_2;
	}
	public String getBit_3() {
		return bit_3;
	}
	public void setBit_3(String bit_3) {
		this.bit_3 = bit_3;
	}
	public String getBit_4() {
		return bit_4;
	}
	public void setBit_4(String bit_4) {
		this.bit_4 = bit_4;
	}
	public String getBit_11() {
		return bit_11;
	}
	public void setBit_11(String bit_11) {
		this.bit_11 = bit_11;
	}
	public String getBit_12() {
		return bit_12;
	}
	public void setBit_12(String bit_12) {
		this.bit_12 = bit_12;
	}
	public String getBit_13() {
		return bit_13;
	}
	public void setBit_13(String bit_13) {
		this.bit_13 = bit_13;
	}
	public String getBit_14() {
		return bit_14;
	}
	public void setBit_14(String bit_14) {
		this.bit_14 = bit_14;
	}
	public String getBit_22() {
		return bit_22;
	}
	public void setBit_22(String bit_22) {
		this.bit_22 = bit_22;
	}
	public String getBit_24() {
		return bit_24;
	}
	public void setBit_24(String bit_24) {
		this.bit_24 = bit_24;
	}
	public String getBit_25() {
		return bit_25;
	}
	public void setBit_25(String bit_25) {
		this.bit_25 = bit_25;
	}
	public String getBit_37() {
		return bit_37;
	}
	public void setBit_37(String bit_37) {
		this.bit_37 = bit_37;
	}
	public String getBit_38() {
		return bit_38;
	}
	public void setBit_38(String bit_38) {
		this.bit_38 = bit_38;
	}
	public String getBit_39() {
		return bit_39;
	}
	public void setBit_39(String bit_39) {
		this.bit_39 = bit_39;
	}
	public String getBit_41() {
		return bit_41;
	}
	public void setBit_41(String bit_41) {
		this.bit_41 = bit_41;
	}
	public String getBit_42() {
		return bit_42;
	}
	public void setBit_42(String bit_42) {
		this.bit_42 = bit_42;
	}
	public String getBit_55() {
		return bit_55;
	}
	public void setBit_55(String bit_55) {
		this.bit_55 = bit_55;
	}
	public String getBit_60() {
		return bit_60;
	}
	public void setBit_60(String bit_60) {
		this.bit_60 = bit_60;
	}
	public String getBit_62() {
		return bit_62;
	}
	public void setBit_62(String bit_62) {
		this.bit_62 = bit_62;
	}
	public String getBit_63() {
		return bit_63;
	}
	public void setBit_63(String bit_63) {
		this.bit_63 = bit_63;
	}
	public String getBit_64() {
		return bit_64;
	}
	public void setBit_64(String bit_64) {
		this.bit_64 = bit_64;
	}
	public String getResponse_code() {
		return response_code;
	}
	public void setResponse_code(String response_code) {
		this.response_code = response_code;
	}
	public String getResponse_name() {
		return response_name;
	}
	public void setResponse_name(String response_name) {
		this.response_name = response_name;
	}
	
	public static VoidModel convertVoidModel(JSONObject object, String date) {
		VoidModel model = new VoidModel();
		try {
			if(object.has("response_code")) model.setResponse_code(object.getString("response_code"));
			if(object.has("response_name")) model.setResponse_name(object.getString("response_name"));
			model.setIso_request(object.toString());
			model.setIso_response(object.toString());
			model.setTransaction_date(date);
			
			JSONArray array = object.getJSONArray("isoFields");
			for(int i=0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				
				if ((Integer)obj.get("bit") == 0) model.setBit_0(obj.getString("value"));
				if ((Integer)obj.get("bit") == 2) model.setBit_2(obj.getString("value"));
				if ((Integer)obj.get("bit") == 3) model.setBit_3(obj.getString("value"));
				if ((Integer)obj.get("bit") == 4) model.setBit_4(formatingValue(obj.getString("value"),12));
				if ((Integer)obj.get("bit") == 11) model.setBit_11(formatingValue(obj.getString("value"),6));
				if ((Integer)obj.get("bit") == 12) model.setBit_12(obj.getString("value"));
				if ((Integer)obj.get("bit") == 13) model.setBit_13(obj.getString("value"));
				if ((Integer)obj.get("bit") == 14) model.setBit_14(obj.getString("value"));
				if ((Integer)obj.get("bit") == 22) model.setBit_22(obj.getString("value"));
				if ((Integer)obj.get("bit") == 24) model.setBit_24(obj.getString("value"));
				if ((Integer)obj.get("bit") == 25) model.setBit_25(obj.getString("value"));
				if ((Integer)obj.get("bit") == 37) model.setBit_37(obj.getString("value"));
				if ((Integer)obj.get("bit") == 38) model.setBit_38(obj.getString("value"));
				if ((Integer)obj.get("bit") == 39) model.setBit_39(obj.getString("value"));
				if ((Integer)obj.get("bit") == 41) model.setBit_41(obj.getString("value"));
				if ((Integer)obj.get("bit") == 42) model.setBit_42(obj.getString("value"));
				if ((Integer)obj.get("bit") == 52) model.setBit_52(obj.getString("value"));
				if ((Integer)obj.get("bit") == 55) model.setBit_55(obj.getString("value"));
				if ((Integer)obj.get("bit") == 60) model.setBit_60(obj.getString("value"));
				if ((Integer)obj.get("bit") == 62) model.setBit_62(obj.getString("value"));
				if ((Integer)obj.get("bit") == 63) model.setBit_63(obj.getString("value"));
				if ((Integer)obj.get("bit") == 64) model.setBit_64(obj.getString("value"));
			}
		}catch(JSONException e) {
			System.out.println("convertSaleModel-Error1: "+e.getMessage());
		}
		return model;
	}
	
	private static String formatingValue(String value, int length) {
		String result = value;
		for(int i=0; i < length-value.length(); i++) {
			result = "0"+result;
		}
		
		return result;
	}

}
