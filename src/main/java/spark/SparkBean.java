package spark;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jpos.q2.QBeanSupport;
import org.jpos.util.NameRegistrar;
import org.jpos.core.ConfigurationException;
import org.jpos.space.Space;
import org.jpos.space.SpaceFactory;
import org.jpos.transaction.Context;
import org.json.JSONArray;
import org.json.JSONObject;

import database.DBTransaction;

/**
 *
 * @author ErwinSn
 */
public class SparkBean extends QBeanSupport {

    public static int JSON_INDENT = 3;
    public static String DEFAULT_CONTENT_TYPE = "application/json";
    public static long DEFAULT_QUEUE_TIMEOUT = 120000l;
    public static String DEFAULT_TXNMGR_NAME = "txnmgr";
    public static int DEFAULT_PORT = 7011;
    public static String DEFAULT_BINDING_HOSTNAME = "127.0.0.1";

    @Override
    public void initService() {
        NameRegistrar.register(getName(), this);
	}

    @Override
    public void startService() throws NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ConfigurationException {
        log.info("Starting Spark Services...");
        Spark.setPort(this.cfg.getInt("port", DEFAULT_PORT));
        Spark.setIpAddress(this.cfg.get("hostname", DEFAULT_BINDING_HOSTNAME));

        String[] basepaths = this.cfg.getAll("basepath");
        String[] contentTypes = this.cfg.getAll("content-type");
        String[] queueNames = this.cfg.getAll("queue");
        long[] queueTimeouts = this.cfg.getLongs("queue-timeout");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for(int i = 0; i < basepaths.length; i++) {
            final int iValue = i;

            Spark.post(new Route(basepaths[iValue], contentTypes[iValue]) {
                @Override
                public Object handle(Request request, Response response) {
                    // Log the request messagework
                log.info("Receive message body : " + request.body());
                JSONObject reqObj = new JSONObject(request.body());
                String reqDate = sdf.format(new Date());

	                if(reqObj.has("isoFields")) {
	                	Context reqContext = new Context();
	                	if(reqObj.has("transaction_date")) {
	                		reqDate = reqObj.getString("transaction_date");
	                	}

	                    JSONArray array = reqObj.getJSONArray("isoFields");
	                    //ADD NIID
	                    JSONObject field24 = new JSONObject();
	                    field24.put("bit", 24);
	                    field24.put("value","009");
	                    reqObj.getJSONArray("isoFields").put(field24);

	                    String f11 = ""; String f41 = ""; String f0 = ""; String f3 = ""; String f42 = "";

	                    for (int i = 0; i < array.length(); i++) {
	                        JSONObject obj = array.getJSONObject(i);
	                        if ((Integer)obj.get("bit") == 0) {
	                            f0 = obj.getString("value");
	                        }else if ((Integer)obj.get("bit") == 11) {
	                            f11 = obj.getString("value");
	                        } else if ((Integer)obj.get("bit") == 41) {
	                            f41 = obj.getString("value");
	                        } else if ((Integer)obj.get("bit") == 3) {
	                            f3 = obj.getString("value");
	                        }else if ((Integer)obj.get("bit") == 42) {
	                            f42 = obj.getString("value");
	                        }
	                    }
	                    String queueKey = f11 + f41;

	                    reqContext.put("REQUEST_MSG", reqObj);
	                    Space sp = SpaceFactory.getSpace();
	                    sp.out(queueNames[iValue], reqContext, queueTimeouts[iValue]);
	                    Context rspContext = (Context) sp.in("RESPONSE-" + queueKey, 60000);
	                    JSONObject rspObj = (JSONObject) rspContext.get("RESPONSE_MSG");
	                    log.info("Send message body : " + rspObj);
	                    response.header("Content-Type", request.contentType());

	                    JSONObject objResp = rspObj;
	                    if(f0.equals("0500")) {
	                    	objResp.put("listTrace", reqObj.getJSONArray("listTrace"));
	                    }else if(f0.equals("0200")) {
	                    	objResp.put("transaction_date", reqObj.getString("transaction_date"));
	                    }else if(f0.equals("0400")) {
	                    	objResp.put("transaction_date", reqObj.getString("transaction_date"));
	                    }
	                    return rspObj.toString();
	                }else if(reqObj.has("batchUpload")) {
	                	JSONArray array = reqObj.getJSONArray("batchUpload");
		                JSONObject result = new JSONObject();
		                JSONArray arrRespon = new JSONArray();
		                for(int x = 0; x < array.length(); x++) {
		                	JSONObject trx = array.getJSONObject(x);

			                Context reqContext = new Context();
		                	JSONArray fields = trx.getJSONArray("isoFields");
		                    String f11 = "";
		                    String f41 = "";
		                    for (int i = 0; i < fields.length(); i++) {
		                        JSONObject obj = fields.getJSONObject(i);
		                        if ((Integer)obj.get("bit") == 11) {
		                            f11 = obj.getString("value");
		                        } else if ((Integer)obj.get("bit") == 41) {
		                            f41 = obj.getString("value");
		                        }
		                    }
		                    String queueKey = f11 + f41;
		                    log.info("queue key = " + queueKey);
		                    reqContext.put("REQUEST_MSG", trx);
		                    Space sp = SpaceFactory.getSpace();
		                    sp.out(queueNames[iValue], reqContext, queueTimeouts[iValue]);
		                    Context rspContext = (Context) sp.in("RESPONSE-" + queueKey, 60000);
		                    JSONObject rspObj = (JSONObject) rspContext.get("RESPONSE_MSG");
			                arrRespon.put(rspObj);
		                }
		                result.put("batchUpload", arrRespon);
	                    log.info("Send message body : " + result);
	                    response.header("Content-Type", request.contentType());
		                return result.toString();
	                }else {
	                	return new JSONObject();
	                }
                }
            });
        }
    }

    @Override
    public void stopService() {
        log.info("Stopping Spark Services...");
        Spark.stop();
    }

    @Override
    public void destroyService() {
        NameRegistrar.unregister(getName());
    }

}
