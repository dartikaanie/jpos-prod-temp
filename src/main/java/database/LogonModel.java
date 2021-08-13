package database;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LogonModel {
	private String bit_0, bit_3, bit_11, bit_24, bit_39, bit_41, bit_42, bit_62, created_date;
	private String response_code, response_name;
	
	public String getCreated_date() {
		return created_date;
	}

	public void setCreated_date(String created_date) {
		this.created_date = created_date;
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

	public String getBit_39() {
		return bit_39;
	}

	public void setBit_39(String bit_39) {
		this.bit_39 = bit_39;
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

	public String getBit_24() {
		return bit_24;
	}

	public void setBit_24(String bit_24) {
		this.bit_24 = bit_24;
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

	public String getBit_62() {
		return bit_62;
	}

	public void setBit_62(String bit_62) {
		this.bit_62 = bit_62;
	}
	
	public static LogonModel convertLogonModel(JSONObject object, String date) {
		LogonModel model = new LogonModel();
		try {
			if(object.has("response_code")) model.setResponse_code(object.getString("response_code"));
			if(object.has("response_name")) model.setResponse_name(object.getString("response_name"));
			
			JSONArray array = object.getJSONArray("isoFields");
			for(int i=0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);

				if ((Integer)obj.get("bit") == 0) model.setBit_0(obj.getString("value"));
				if ((Integer)obj.get("bit") == 3) model.setBit_3(obj.getString("value"));
				if ((Integer)obj.get("bit") == 11) model.setBit_11(formatingValue(obj.getString("value"),6));
				
				if ((Integer)obj.get("bit") == 24) model.setBit_24(obj.getString("value"));
				if ((Integer)obj.get("bit") == 39) model.setBit_39(obj.getString("value"));
				if ((Integer)obj.get("bit") == 41) model.setBit_41(obj.getString("value"));
				if ((Integer)obj.get("bit") == 42) model.setBit_42(obj.getString("value"));
				if ((Integer)obj.get("bit") == 62) model.setBit_62(obj.getString("value"));
				model.setCreated_date(date);

			}
		}catch(JSONException e) {
			System.out.println("convertLogonModel-Error: "+e.getMessage());
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
