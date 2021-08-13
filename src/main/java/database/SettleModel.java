package database;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SettleModel {
	private String bit_0,bit_3, bit_11,bit_12,bit_13
	, bit_24,bit_37,bit_39, bit_41, bit_42, bit_48,bit_60, bit_63,bit_64,
	iso_request,iso_response,update_date,transaction_date;
	
	private String response_code, response_name;
	private JSONArray list_trace;
	
	public JSONArray getList_trace() {
		return list_trace;
	}
	public void setList_trace(JSONArray list_trace) {
		this.list_trace = list_trace;
	}
	public String getBit_0() {
		return bit_0;
	}
	public void setBit_0(String bit_0) {
		this.bit_0 = bit_0;
	}
	public String getBit_3() {
		return bit_3;
	}
	public void setBit_3(String bit_3) {
		this.bit_3 = bit_3;
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
	public String getBit_24() {
		return bit_24;
	}
	public void setBit_24(String bit_24) {
		this.bit_24 = bit_24;
	}
	public String getBit_37() {
		return bit_37;
	}
	public void setBit_37(String bit_37) {
		this.bit_37 = bit_37;
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
	public String getBit_48() {
		return bit_48;
	}
	public void setBit_48(String bit_48) {
		this.bit_48 = bit_48;
	}
	public String getBit_60() {
		return bit_60;
	}
	public void setBit_60(String bit_60) {
		this.bit_60 = bit_60;
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
	public String getIso_request() {
		return iso_request;
	}
	public void setIso_request(String iso_request) {
		this.iso_request = iso_request;
	}
	public String getIso_response() {
		return iso_response;
	}
	public void setIso_response(String iso_response) {
		this.iso_response = iso_response;
	}

	public String getUpdate_date() {
		return update_date;
	}
	public void setUpdate_date(String update_date) {
		this.update_date = update_date;
	}
	public String getTransaction_date() {
		return transaction_date;
	}
	public void setTransaction_date(String transaction_date) {
		this.transaction_date = transaction_date;
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
	

	public static SettleModel convertSettleModel(JSONObject object, String date) {
		SettleModel model = new SettleModel();
		try {
			if(object.has("response_code")) model.setResponse_code(object.getString("response_code"));
			if(object.has("response_name")) model.setResponse_name(object.getString("response_name"));
			model.setIso_request(object.toString());
			model.setIso_response(object.toString());
			model.setTransaction_date(date);
			if(object.has("listTrace")) {
				model.setList_trace(object.getJSONArray("listTrace"));
			}
			
			JSONArray array = object.getJSONArray("isoFields");
			for(int i=0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				
				if ((Integer)obj.get("bit") == 0) model.setBit_0(obj.getString("value"));
				if ((Integer)obj.get("bit") == 3) model.setBit_3(obj.getString("value"));
				if ((Integer)obj.get("bit") == 11) model.setBit_11(formatingValue(obj.getString("value"),6));
				if ((Integer)obj.get("bit") == 12) model.setBit_12(obj.getString("value"));
				if ((Integer)obj.get("bit") == 13) model.setBit_13(obj.getString("value"));
				if ((Integer)obj.get("bit") == 24) model.setBit_24(obj.getString("value"));
				if ((Integer)obj.get("bit") == 37) model.setBit_37(obj.getString("value"));
				if ((Integer)obj.get("bit") == 39) model.setBit_39(obj.getString("value"));
				if ((Integer)obj.get("bit") == 41) model.setBit_41(obj.getString("value"));
				if ((Integer)obj.get("bit") == 42) model.setBit_42(obj.getString("value"));
				if ((Integer)obj.get("bit") == 48) model.setBit_48(obj.getString("value"));
				if ((Integer)obj.get("bit") == 60) model.setBit_60(obj.getString("value"));
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
