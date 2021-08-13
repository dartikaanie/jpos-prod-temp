package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import helper.Helper;

public class DBTransaction {
	
	private Connection connect = null;
	  private Statement statement = null;
	  private PreparedStatement preparedStatement = null;
	  private ResultSet resultSet = null;
	  
	  final private String host = "jdbc:mysql://172.21.3.3:3306/artajasa_debit?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
	  final private String user = "root";
	  final private String password = "kmzway87saa";

//	   final private String host = "jdbc:mysql://localhost:3306/artajasa_debit?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
//	   final private String user = "root";
//	   final private String password = "";

      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  
	  public Connection setConnection() {
		  try {
			  Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			  if(connect == null) {
				  connect = DriverManager.getConnection(host, user, password);
			  }else if(connect.isClosed()){
				  connect = DriverManager.getConnection(host, user, password);
			  }
			  statement = connect.createStatement();
		  }catch(Exception e) {
			  System.out.println("SQLException-error: "+ e.getMessage());
		  }
		  return connect;
	  }
	  
	  public void closeConnection() {
		  try {
			  if(connect != null) {
				  if(!connect.isClosed()) {
					  connect.close();
				  }
			  }
			  System.out.println("SQLException-closeConnection: "+ !connect.isClosed());
		  }catch(SQLException e) {
			  System.out.println("SQLException-error: "+ e.getMessage());
		  }
	  }
	  
	  public boolean insertTrx(String type, String code, String f41, String f42, JSONObject array, String date) {
		  boolean result = false;
		  if(type.equalsIgnoreCase("0800")) {
			  LogonModel model = LogonModel.convertLogonModel(array, date);
			  result =  requestLogon(model);
		  }else if(type.equalsIgnoreCase("0200")) {
			  if(code.equals(Helper.PROC_SALE_DEFAULT) || code.equals(Helper.PROC_SALE_CHEQUE)
					  || code.equals(Helper.PROC_SALE_SAVING)) {
				  SaleModel model = SaleModel.convertSaleModel(array, date);
				  result =  requestSale(model);
			  }else {
				  VoidModel model = VoidModel.convertVoidModel(array, date);
				  result =  requestVoid(model);
			  }
		  }else if(type.equalsIgnoreCase("0500")) {
			  SettleModel model = SettleModel.convertSettleModel(array, date);
			  result =  requestSettle(model);
		  }else if(type.equalsIgnoreCase("0400")) {
			  if(code.equals(Helper.PROC_SALE_DEFAULT) || code.equals(Helper.PROC_SALE_CHEQUE)
					  || code.equals(Helper.PROC_SALE_SAVING)) {
				  ReversalModel model = ReversalModel.convertReversalModel(array, date);
				  result =  requestReversal(model);
			  }else {
				  ReversalModel model = ReversalModel.convertReversalModel(array, date);
				  result =  requestReversalVoid(model);
			  }
		  }
		  return result;
	  }
	  
	  public boolean updateTrx(String type, String code, String f41, String f42, JSONObject array, String date) {
		  boolean result = false;
		  
		  if(type.equalsIgnoreCase("0800")) {
			  LogonModel model = LogonModel.convertLogonModel(array, date);
			  result =  updateLogon(model);
		  }else if(type.equalsIgnoreCase("0200")) {
			  if(code.equals(Helper.PROC_SALE_DEFAULT) || code.equals(Helper.PROC_SALE_CHEQUE)
					  || code.equals(Helper.PROC_SALE_SAVING)) {
				  SaleModel model = SaleModel.convertSaleModel(array, date);
				  result =  updateSale(model);
			  }else {
				  VoidModel model = VoidModel.convertVoidModel(array, date);
				  result =  updateVoid(model);
				  if(result) {
					  updateTrxStatus(f42,f41,model.getBit_11(),model.getTransaction_date(),1);
				  }
			  }
		  }else if(type.equalsIgnoreCase("0500")) {
			  SettleModel model = SettleModel.convertSettleModel(array, date);
			  result =  updateSettle(model);
			  if(result) {
				  JSONArray list = model.getList_trace();
				  System.out.println("Settlement-ready update-"+list.toString());
				  if(list != null) {
					  if(list.length() > 0) {
						  for(int i=0; i < list.length(); i++) {
							  JSONObject obj = list.getJSONObject(i);
							  String trace = Helper.formatingValue(obj.getString("trace_number"),6);
							  String trDate = obj.getString("transaction_date");

							  boolean status = updateTrxStatus(f42,f41,trace,trDate,3);
							  System.out.println("Update status to settle-"+status);
						  }
					  }
				  }
			  }
		  }else if(type.equalsIgnoreCase("0400")) {
			  if(code.equals(Helper.PROC_SALE_DEFAULT) || code.equals(Helper.PROC_SALE_CHEQUE)
					  || code.equals(Helper.PROC_SALE_SAVING)) {
				  ReversalModel model = ReversalModel.convertReversalModel(array, date);
				  result =  updateReversal(f42,model);
				  if(result) {
					  updateTrxStatus(f42,f41,model.getBit_11(),model.getTransaction_date(),2);
				  }
			  }else {
				  ReversalModel model = ReversalModel.convertReversalModel(array, date);
				  result =  updateReversalVoid(f42,model);
				  if(result) {
					  updateTrxStatus(f42,f41,model.getBit_11(),model.getTransaction_date(),5);
				  }
			  }
		  }

		  return result;
	  }
	  
	  public boolean requestLogon(LogonModel model) {
		  boolean result = false;
		  try {
			  preparedStatement = connect
			          .prepareStatement("INSERT INTO `trx_logon`(`bit_0`, `bit_3`, `bit_11`,`bit_24`,"
			          		+ " `bit_41`,`created_date`) "
			          		+ "VALUES (?,?,?,?,?,?)");
		      preparedStatement.setString(1, model.getBit_0());
		      preparedStatement.setString(2, model.getBit_3());
		      preparedStatement.setString(3, model.getBit_11());
		      preparedStatement.setString(4, model.getBit_24());
		      preparedStatement.setString(5, model.getBit_41());
		      preparedStatement.setString(6, model.getCreated_date());
			  
		      result = preparedStatement.execute();
	
		  }catch(SQLException e) {
			  System.out.println("SQLException-insertData-error: "+ e.getMessage());
		  } 
		  return result;
	  }
	  
	  public boolean updateLogon(LogonModel model) {
		  boolean result = false;
		  try {
			  preparedStatement = connect
			          .prepareStatement("UPDATE `trx_logon` SET "
			          		+ "`bit_39`=?,`bit_42`=?,`bit_62`=?,`response_code`=?, `response_name`=?,`update_date`=? "
			          		+ "WHERE bit_11=? and bit_41=? and created_date=?");
		      preparedStatement.setString(1, model.getBit_39());
		      preparedStatement.setString(2, model.getBit_42());
		      preparedStatement.setString(3, model.getBit_62());
		      preparedStatement.setString(4, model.getResponse_code());
		      preparedStatement.setString(5, model.getResponse_name());
		      preparedStatement.setString(6, sdf.format(new Date()));
		      preparedStatement.setString(7, model.getBit_11());
		      preparedStatement.setString(8, model.getBit_41());
		      preparedStatement.setString(9, model.getCreated_date());
		      result = preparedStatement.executeUpdate() > 0;
		      
	
		  }catch(SQLException e) {
			  System.out.println("SQLException-updateLogon-error: "+ e.getMessage());
		  }
	      return result;
	  }
	  
	  public boolean requestSale(SaleModel model) {
		  boolean result = false;
		  try {
		      preparedStatement = connect
			          .prepareStatement("INSERT INTO `trx_sale`(`bit_0`, `bit_2`, `bit_3`, `bit_4`, `bit_11`,`bit_14`,`bit_22`"
			          		+ ", `bit_24`, `bit_25`, `bit_35`,`bit_41`, `bit_42`,`bit_48`, `bit_52`, `bit_55`,`bit_60`, `bit_62`"
			          		+ ",`bit_64`,`iso_request`,`transaction_date` ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		      preparedStatement.setString(1, model.getBit_0());
		      preparedStatement.setString(2, model.getBit_2());
		      preparedStatement.setString(3, model.getBit_3());
		      preparedStatement.setString(4, model.getBit_4());
		      preparedStatement.setString(5, model.getBit_11());
		      preparedStatement.setString(6, model.getBit_14());
		      preparedStatement.setString(7, model.getBit_22());
		      preparedStatement.setString(8, model.getBit_24());
		      preparedStatement.setString(9, model.getBit_25());
		      preparedStatement.setString(10, model.getBit_35());
		      preparedStatement.setString(11, model.getBit_41());
		      preparedStatement.setString(12, model.getBit_42());
		      preparedStatement.setString(13, model.getBit_48());
		      preparedStatement.setString(14, model.getBit_52());
		      preparedStatement.setString(15, model.getBit_55());
		      preparedStatement.setString(16, model.getBit_60());
		      preparedStatement.setString(17, model.getBit_62());
		      preparedStatement.setString(18, model.getBit_64());
		      preparedStatement.setString(19, model.getIso_request());
		      preparedStatement.setString(20, model.getTransaction_date());
		      result = preparedStatement.execute();
		    
	
		  }catch(SQLException e) {
			  System.out.println("SQLException-requestSale-error: "+ e.getMessage());
		  }
		  return result;
				  
	  }
	  
	  public boolean updateSale(SaleModel model) {
		  boolean result = false;
		  try {
			  preparedStatement = connect
			          .prepareStatement("UPDATE `trx_sale` SET `bit_12`=?, `bit_13`=?, `bit_37`=?,`bit_38`=?,`bit_39`=?,`bit_63`=?,"
			          		+ "`iso_response`=?, `response_code`=?, `response_name`=?, `update_date`=? "
			          		+ "WHERE `bit_11`=? and `bit_41`=? and `transaction_date`=?");
		      preparedStatement.setString(1, model.getBit_12());
		      preparedStatement.setString(2, model.getBit_13());
		      preparedStatement.setString(3, model.getBit_37());
		      preparedStatement.setString(4, model.getBit_38());
		      preparedStatement.setString(5, model.getBit_39());
		      preparedStatement.setString(6, model.getBit_63());
		      preparedStatement.setString(7, model.getIso_response());
		      preparedStatement.setString(8, model.getResponse_code());
		      preparedStatement.setString(9, model.getResponse_name());
		      preparedStatement.setString(10, sdf.format(new Date()));
		      preparedStatement.setString(11, model.getBit_11());
		      preparedStatement.setString(12, model.getBit_41());
		      preparedStatement.setString(13, model.getTransaction_date());
		      result = preparedStatement.executeUpdate() > 0;
		      
	
		  }catch(SQLException e) {
			  System.out.println("SQLException-requestSale-error: "+ e.getMessage());
		  }
		  return result;
	      
	  }
	  
	  public boolean requestVoid(VoidModel model) {
		  boolean result = false;
		  try {
			  preparedStatement = connect
			          .prepareStatement("INSERT INTO `trx_void`(`bit_0`, `bit_2`, `bit_3`, `bit_4`, `bit_11`, `bit_12`,"
			          		+ "`bit_13`, `bit_14`, `bit_22`, `bit_24`, `bit_25`, `bit_37`, `bit_41`,`bit_42`,"
			          		+ "`bit_52`, `bit_55`, `bit_60`, `bit_62`, `bit_64`,`iso_request`, `transaction_date` ) "
			          		+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		      preparedStatement.setString(1, model.getBit_0());
		      preparedStatement.setString(2, model.getBit_2());
		      preparedStatement.setString(3, model.getBit_3());
		      preparedStatement.setString(4, model.getBit_4());
		      preparedStatement.setString(5, model.getBit_11());
		      preparedStatement.setString(6, model.getBit_12());
		      preparedStatement.setString(7, model.getBit_13());
		      preparedStatement.setString(8, model.getBit_14());
		      preparedStatement.setString(9, model.getBit_22());
		      preparedStatement.setString(10, model.getBit_24());
		      preparedStatement.setString(11, model.getBit_25());
		      preparedStatement.setString(12, model.getBit_37());
		      preparedStatement.setString(13, model.getBit_41());
		      preparedStatement.setString(14, model.getBit_42());
		      preparedStatement.setString(15, model.getBit_52());
		      preparedStatement.setString(16, model.getBit_55());
		      preparedStatement.setString(17, model.getBit_60());
		      preparedStatement.setString(18, model.getBit_62());
		      preparedStatement.setString(19, model.getBit_64());
		      preparedStatement.setString(20, model.getIso_request());
		      preparedStatement.setString(21, model.getTransaction_date());
		      result = preparedStatement.execute();
	
		  }catch(SQLException e) {
			  System.out.println("SQLException-requestVoid-error: "+ e.getMessage());
		  }
		  return result;
	  }
	  
	  public boolean updateVoid(VoidModel model) {
		  boolean result = false;
		  try {
			  preparedStatement = connect
			          .prepareStatement("UPDATE `trx_void` SET `bit_38`=?, `bit_39`=?, `bit_63`=?,"
			          		+ "iso_response=?, response_code=?, response_name=?, update_date=? "
			          		+ "WHERE `bit_11`=? and `bit_41`=? and `transaction_date`=?");
		      preparedStatement.setString(1, model.getBit_38());
		      preparedStatement.setString(2, model.getBit_39());
		      preparedStatement.setString(3, model.getBit_63());
		      preparedStatement.setString(4, model.getIso_response());
		      preparedStatement.setString(5, model.getResponse_code());
		      preparedStatement.setString(6, model.getResponse_name());
		      preparedStatement.setString(7, sdf.format(new Date()));
		      preparedStatement.setString(8, model.getBit_11());
		      preparedStatement.setString(9, model.getBit_41());
		      preparedStatement.setString(10, model.getTransaction_date());
		      result = preparedStatement.executeUpdate() > 0;
	
		  }catch(SQLException e) {
			  System.out.println("SQLException-updateVoid-error: "+ e.getMessage());
		  }
	      return result;
	  }
	  
	  public boolean requestSettle(SettleModel model) {
		  boolean result = false;
		  try {
			  preparedStatement = connect
			          .prepareStatement("INSERT INTO `trx_settle`(`bit_0`, `bit_3`, `bit_11`,`bit_24`,"
			          		+ "`bit_41`, `bit_42`,`bit_60`, `bit_63`, `bit_64`, `iso_request`,"
			          		+ "`transaction_date`) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
		      preparedStatement.setString(1, model.getBit_0());
		      preparedStatement.setString(2, model.getBit_3());
		      preparedStatement.setString(3, model.getBit_11());
		      preparedStatement.setString(4, model.getBit_24());
		      preparedStatement.setString(5, model.getBit_41());
		      preparedStatement.setString(6, model.getBit_42());
		      preparedStatement.setString(7, model.getBit_60());
		      preparedStatement.setString(8, model.getBit_63());
		      preparedStatement.setString(9, model.getBit_64());
		      preparedStatement.setString(10, model.getIso_request());
		      preparedStatement.setString(11, model.getTransaction_date());
		      result = preparedStatement.execute();
	
		  }catch(SQLException e) {
			  System.out.println("SQLException-requestSettle-error: "+ e.getMessage());
		  }
		  return result;
	  }
	  
	  public boolean updateSettle(SettleModel model) {
		  boolean result = false;
		  try {
			  preparedStatement = connect
			          .prepareStatement("UPDATE `trx_settle` SET `bit_12`=?,`bit_13`=?,`bit_37`=?,`bit_39`=?,"
			          		+ "`bit_48`=?,`iso_response`=?,"
			          		+ "`update_date`=?,`response_code`=?,`response_name`=? "
			          		+ "WHERE `bit_11`=? and `bit_41`=? and `transaction_date`=?");
		      preparedStatement.setString(1, model.getBit_12());
		      preparedStatement.setString(2, model.getBit_13());
		      preparedStatement.setString(3, model.getBit_37());
		      preparedStatement.setString(4, model.getBit_39());
		      preparedStatement.setString(5, model.getBit_48());
		      preparedStatement.setString(6, model.getIso_response());
		      preparedStatement.setString(7, sdf.format(new Date()));
		      preparedStatement.setString(8, model.getResponse_code());
		      preparedStatement.setString(9, model.getResponse_name());
		      preparedStatement.setString(10, model.getBit_11());
		      preparedStatement.setString(11, model.getBit_41());
		      preparedStatement.setString(12, model.getTransaction_date());
		      result = preparedStatement.executeUpdate() > 0;
	
		  }catch(SQLException e) {
			  System.out.println("SQLException-updateSettle-error: "+ e.getMessage());
		  }
	      return result;
	  }
	  
	  public boolean requestReversal(ReversalModel model) {
		  boolean result = false;
		  try {
		      preparedStatement = connect
			          .prepareStatement("INSERT INTO `trx_sale_reversal`(`bit_0`, `bit_2`, `bit_3`, `bit_4`, `bit_11`,`bit_14`,`bit_22`"
			          		+ ", `bit_24`, `bit_25`, `bit_41`, `bit_42`,`bit_62`"
			          		+ ",`bit_64`,`iso_request`,`transaction_date` ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		      preparedStatement.setString(1, model.getBit_0());
		      preparedStatement.setString(2, model.getBit_2());
		      preparedStatement.setString(3, model.getBit_3());
		      preparedStatement.setString(4, model.getBit_4());
		      preparedStatement.setString(5, model.getBit_11());
		      preparedStatement.setString(6, model.getBit_14());
		      preparedStatement.setString(7, model.getBit_22());
		      preparedStatement.setString(8, model.getBit_24());
		      preparedStatement.setString(9, model.getBit_25());
		      preparedStatement.setString(10, model.getBit_41());
		      preparedStatement.setString(11, model.getBit_42());
		      preparedStatement.setString(12, model.getBit_62());
		      preparedStatement.setString(13, model.getBit_64());
		      preparedStatement.setString(14, model.getIso_request());
		      preparedStatement.setString(15, model.getTransaction_date());
		      result = preparedStatement.execute();
		    
	
		  }catch(SQLException e) {
			  System.out.println("SQLException-requestReversal-error: "+ e.getMessage());
		  }
		  return result;
				  
	  }
	  
	  public boolean updateReversal(String mid, ReversalModel model) {
		  boolean result = false;
		  try {
			  preparedStatement = connect
			          .prepareStatement("UPDATE `trx_sale_reversal` SET `bit_12`=?, `bit_13`=?, `bit_37`=?,"
			          		+ "`bit_38`=?,`bit_39`=?, "
			          		+ "`iso_response`=?, `response_code`=?, `response_name`=?, `update_date`=? "
			          		+ "WHERE `bit_11`=? and `bit_41`=? and `bit_42`=? and `transaction_date`=?");
		      preparedStatement.setString(1, model.getBit_12());
		      preparedStatement.setString(2, model.getBit_13());
		      preparedStatement.setString(3, model.getBit_37());
		      preparedStatement.setString(4, model.getBit_38());
		      preparedStatement.setString(5, model.getBit_39());
		      preparedStatement.setString(6, model.getIso_response());
		      preparedStatement.setString(7, model.getResponse_code());
		      preparedStatement.setString(8, model.getResponse_name());
		      preparedStatement.setString(9, sdf.format(new Date()));
		      preparedStatement.setString(10, model.getBit_11());
		      preparedStatement.setString(11, model.getBit_41());
		      preparedStatement.setString(12, mid);
		      preparedStatement.setString(13, model.getTransaction_date());
		      result = preparedStatement.executeUpdate() > 0;
		      
		  }catch(SQLException e) {
			  System.out.println("SQLException-updateReversal-error: "+ e.getMessage());
		  }
		  return result;
	      
	  }
	  
	  public boolean requestReversalVoid(ReversalModel model) {
		  boolean result = false;
		  try {
		      preparedStatement = connect
			          .prepareStatement("INSERT INTO `trx_void_reversal`(`bit_0`, `bit_2`, `bit_3`, `bit_4`, `bit_11`, `bit_12`,"
			          		+ " `bit_13`,`bit_14`,`bit_22`, `bit_24`, `bit_25`,`bit_37`, `bit_41`, `bit_42`,`bit_62`"
			          		+ ",`bit_64`,`iso_request`,`transaction_date` ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		      preparedStatement.setString(1, model.getBit_0());
		      preparedStatement.setString(2, model.getBit_2());
		      preparedStatement.setString(3, model.getBit_3());
		      preparedStatement.setString(4, model.getBit_4());
		      preparedStatement.setString(5, model.getBit_11());
		      preparedStatement.setString(6, model.getBit_12());
		      preparedStatement.setString(7, model.getBit_13());
		      preparedStatement.setString(8, model.getBit_14());
		      preparedStatement.setString(9, model.getBit_22());
		      preparedStatement.setString(10, model.getBit_24());
		      preparedStatement.setString(11, model.getBit_25());
		      preparedStatement.setString(12, model.getBit_37());
		      preparedStatement.setString(13, model.getBit_41());
		      preparedStatement.setString(14, model.getBit_42());
		      preparedStatement.setString(15, model.getBit_62());
		      preparedStatement.setString(16, model.getBit_64());
		      preparedStatement.setString(17, model.getIso_request());
		      preparedStatement.setString(18, model.getTransaction_date());
		      result = preparedStatement.execute();
		    
	
		  }catch(SQLException e) {
			  System.out.println("SQLException-requestReversal-error: "+ e.getMessage());
		  }
		  return result;
				  
	  }
	  
	  public boolean updateReversalVoid(String mid, ReversalModel model) {
		  boolean result = false;
		  try {
			  preparedStatement = connect
			          .prepareStatement("UPDATE `trx_void_reversal` SET `bit_39`=?, "
			          		+ "`iso_response`=?, `response_code`=?, `response_name`=?, `update_date`=? "
			          		+ "WHERE `bit_11`=? and `bit_41`=? and `bit_42`=? and `transaction_date`=?");
		      preparedStatement.setString(1, model.getBit_39());
		      preparedStatement.setString(2, model.getIso_response());
		      preparedStatement.setString(3, model.getResponse_code());
		      preparedStatement.setString(4, model.getResponse_name());
		      preparedStatement.setString(5, sdf.format(new Date()));
		      preparedStatement.setString(6, model.getBit_11());
		      preparedStatement.setString(7, model.getBit_41());
		      preparedStatement.setString(8, mid);
		      preparedStatement.setString(9, model.getTransaction_date());
		      result = preparedStatement.executeUpdate() > 0;
		  }catch(SQLException e) {
			  System.out.println("SQLException-updateReversal-error: "+ e.getMessage());
		  }
		  return result;
	      
	  }
	  
	  public boolean updateTrxStatus(String mid, String tid, String trace, String trxDate, int type) {
		  boolean result = false;
		  try {

			  if(type == 1) { // UPDATE VOID
				  preparedStatement = connect
				          .prepareStatement("UPDATE `trx_sale` SET `is_void`=?,`void_date`=? "
				          		+ "WHERE `bit_11`=? and `bit_41`=? and `bit_42`=? and `transaction_date`=?");
			  }else if(type == 2) {
				  preparedStatement = connect
				          .prepareStatement("UPDATE `trx_sale` SET `is_reversal_sale`=?,`reversal_sale_date`=? "
				          		+ "WHERE `bit_11`=? and `bit_41`=? and `bit_42`=? and `transaction_date`=?");
			  }else if(type == 3) {
				  preparedStatement = connect
				          .prepareStatement("UPDATE `trx_sale` SET `is_settle`=?,`settle_date`=? "
				          		+ "WHERE `bit_11`=? and `bit_41`=? and `bit_42`=? and `transaction_date`=?");
			  }else if(type == 4) {
				  preparedStatement = connect
				          .prepareStatement("UPDATE `trx_sale` SET `is_batch_upload`=?,`batch_upload_date`=? "
				          		+ "WHERE `bit_11`=? and `bit_41`=? and `bit_42`=? and `transaction_date`=?");
			  }else if(type == 5) {
				  preparedStatement = connect
				          .prepareStatement("UPDATE `trx_sale` SET `is_reversal_void`=?,`reversal_void_date`=? "
				          		+ "WHERE `bit_11`=? and `bit_41`=? and `bit_42`=? and `transaction_date`=?");
			  }
		      preparedStatement.setInt(1, 1);
		      preparedStatement.setString(2, sdf.format(new Date()));
		      preparedStatement.setString(3, trace);
		      preparedStatement.setString(4, tid);
		      preparedStatement.setString(5, mid);
		      preparedStatement.setString(6, trxDate);
		      
		      result = preparedStatement.executeUpdate() > 0;
		      
		      if(result && (type == 2 || type ==5)) {
		    	  String query = "Select * from `trx_sale` "
		    	  		+ "WHERE `bit_11`='"+trace+"' and `bit_41`='"+tid+"' and `bit_42`='"+mid+"' "
		    	  				+ "and `transaction_date`='"+trxDate+"'";
		    	  Statement st = connect.createStatement();
		    	  ResultSet rs = st.executeQuery(query);
		          
		          // iterate through the java resultset
		    	  int revSaleCount = 0;
		    	  int revVoidCount = 0;
		          while (rs.next())
		          {
		        	  revSaleCount = rs.getInt("reversal_sale_count");
		        	  revVoidCount = rs.getInt("reversal_void_count");
		          }
		          st.close();
		          if(type == 2) {
		        	  preparedStatement = connect
				          .prepareStatement("UPDATE `trx_sale` SET `reversal_sale_count`=? "
				          		+ "WHERE `bit_11`=? and `bit_41`=? and `bit_42`=? and `transaction_date`=?");
			          preparedStatement.setInt(1, (revSaleCount+1));
		          }else {
		        	  preparedStatement = connect
					          .prepareStatement("UPDATE `trx_sale` SET `reversal_void_count`=? "
					          		+ "WHERE `bit_11`=? and `bit_41`=? and `bit_42`=? and `transaction_date`=?");
			          preparedStatement.setInt(1, (revVoidCount+1));
		          }
			      preparedStatement.setString(2, trace);
			      preparedStatement.setString(3, tid);
			      preparedStatement.setString(4, mid);
			      preparedStatement.setString(5, trxDate);
			      result = preparedStatement.executeUpdate() > 0;
		      }
	
		  }catch(SQLException e) {
			  System.out.println("SQLException-updateTrxStatus-error: "+ e.getMessage());
		  }
		  return result;
	  }
	  
	 
	  
}
