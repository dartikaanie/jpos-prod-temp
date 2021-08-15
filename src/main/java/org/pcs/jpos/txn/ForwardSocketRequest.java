package org.pcs.jpos.txn;

import java.io.Serializable;

import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.channel.NACChannel;
import org.jpos.q2.iso.QMUX;
import org.jpos.transaction.Context;
import org.jpos.transaction.TransactionParticipant;
import org.jpos.util.Log;
import org.jpos.util.NameRegistrar;

import javax.print.attribute.standard.MediaSize;

public class ForwardSocketRequest implements TransactionParticipant, Configurable  {
    
    Log log = Log.getLog("Q2", getClass().getName());
    Configuration cfg;
    Long timeout;

    @Override
    public int prepare(long id, Serializable context) {
        log.info("ForwardSocketRequest-start: ");
        
        Context ctx = (Context)context;
        ISOMsg reqMsg = (ISOMsg) ctx.get("REQUEST_MSG");

        // Select Host Backend
        String destination = "";
        if(reqMsg.getString(24).equalsIgnoreCase("041")){
            destination = cfg.get("destination", "bri-host-41");
        } else if(reqMsg.getString(24).equalsIgnoreCase("042")){
            destination = cfg.get("destination", "bri-host-42");
        } else if(reqMsg.getString(24).equalsIgnoreCase("043")){
            destination = cfg.get("destination", "bri-host-43");
        } else if(reqMsg.getString(24).equalsIgnoreCase("044")){
            destination = cfg.get("destination", "bri-host-44");
        } else if(reqMsg.getString(24).equalsIgnoreCase("045")){
            destination = cfg.get("destination", "bri-host-45");
        } else if(reqMsg.getString(24).equalsIgnoreCase("048")){
            destination = cfg.get("destination", "bri-host-48");
        } else if(reqMsg.getString(24).equalsIgnoreCase("052")){
            destination = cfg.get("destination", "bri-host-52");
        } else if(reqMsg.getString(24).equalsIgnoreCase("006")){
            destination = cfg.get("destination", "bri-host-06");
        }  else if(reqMsg.getString(24).equalsIgnoreCase("007")){
            destination = cfg.get("destination", "bri-host-07");
        } else if(reqMsg.getString(24).equalsIgnoreCase("015")){
            destination = cfg.get("destination", "bri-host-15");
        } else if(reqMsg.getString(24).equalsIgnoreCase("023")){
            destination = cfg.get("destination", "bri-host-23");
        } else if(reqMsg.getString(24).equalsIgnoreCase("095")){
            destination = cfg.get("destination", "bri-host-95");
        }

        ISOMsg rsp = new ISOMsg();
        log.info("reqString 60 :" + reqMsg.getString(60) + "reqString 63 :" + reqMsg.getString(63));
        if(!reqMsg.getString(63).equalsIgnoreCase("null") && !reqMsg.getString(60).equalsIgnoreCase("null")){
          log.info("settle : yes");
//           if(reqMsg.getString(3).equalsIgnoreCase("920000")){
//                 reqMsg.set(60, reqMsg.getString(60).substring(2));
//             } else if(reqMsg.getString(3).equalsIgnoreCase("960000")){
//                 reqMsg.set(60, reqMsg.getString(60).substring(2));
//             }
        }

        try {
        	NACChannel chn = (NACChannel) NameRegistrar.get("channel." + destination);
            if (!chn.isConnected()) {
                log.info("ForwardSocketRequest-prepare: not Connected");
            } else {
                log.info("ForwardSocketRequest-prepare: is Connected");
                
                QMUX qmux = (QMUX) NameRegistrar.get("mux." + destination + "-mux");
                reqMsg.setHeader(ISOUtil.hex2byte("600" + reqMsg.getString(24) + "0000"));
                rsp = qmux.request(reqMsg, timeout);
                
                log.info("Send iso Message : \n rsp: " + rsp + "\n hex: " + ISOUtil.byte2hex(rsp.pack()));
            }
            rsp.setHeader("".getBytes());
            ctx.put("RESPONSE_MSG", rsp);
        } catch (ISOException isoe) {
            isoe.printStackTrace();
            log.info("Log ISOException - error : " + isoe.getMessage());
        } catch (NameRegistrar.NotFoundException ex) {
            ex.printStackTrace();
            log.info("Log NotFoundException - error : " + ex.getMessage());
        } 
    	return PREPARED | NO_JOIN;
    }

    private static byte[] hexToBytes(final String hex) {
        final byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
        }
        return bytes;
    }

    @Override
    public void commit(long id, Serializable context) {}

    @Override
    public void abort(long id, Serializable context) {}

	@Override
	public void setConfiguration(Configuration cfg) throws ConfigurationException {
        this.cfg = cfg;
        timeout = cfg.getLong("timeout", 60000);
	}
}
