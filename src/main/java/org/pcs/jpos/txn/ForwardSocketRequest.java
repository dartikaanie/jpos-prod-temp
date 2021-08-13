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

public class ForwardSocketRequest implements TransactionParticipant, Configurable  {
    
    Log log = Log.getLog("Q2", getClass().getName());
    Configuration cfg;
    Long timeout;

    @Override
    public int prepare(long id, Serializable context) {
        log.info("ForwardSocketRequest-start: ");
        
        Context ctx = (Context)context;
        ISOMsg reqMsg = (ISOMsg) ctx.get("REQUEST_MSG");
        log.info("ForwardSocketRequest-start: " + reqMsg);

        // Select Host Backend
        String destination = "";
        if(reqMsg.getString(24).equalsIgnoreCase("032")){
            destination = cfg.get("destination", "bri-host-32");
        } else if(reqMsg.getString(24).equalsIgnoreCase("033")){
            destination = cfg.get("destination", "bri-host-33");
        } else if(reqMsg.getString(24).equalsIgnoreCase("034")){
            destination = cfg.get("destination", "bri-host-34");
        } else if(reqMsg.getString(24).equalsIgnoreCase("036")){
            destination = cfg.get("destination", "bri-host-36");
        } else if(reqMsg.getString(24).equalsIgnoreCase("037")){
            destination = cfg.get("destination", "bri-host-37");
        } else if(reqMsg.getString(24).equalsIgnoreCase("038")){
            destination = cfg.get("destination", "bri-host-38");
        }else if(reqMsg.getString(24).equalsIgnoreCase("030")){
            destination = cfg.get("destination", "bri-host-30");
        }
        
         log.info("destination : " + destination);
        ISOMsg rsp = new ISOMsg();

        try {
        	NACChannel chn = (NACChannel) NameRegistrar.get("channel." + destination);
            log.info("chn : " + chn);
            if (!chn.isConnected()) {
                log.info("ForwardSocketRequest-prepare: not Connected");
            } else {
                log.info("ForwardSocketRequest-prepare: is Connected");
                
                QMUX qmux = (QMUX) NameRegistrar.get("mux." + destination + "-mux");
                reqMsg.setHeader(ISOUtil.hex2byte("600" + reqMsg.getString(24) + "0000"));
                
                log.info("timeout: "+ timeout);
                log.info("reqMsg: "+ reqMsg);
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
            log.info("24 : " + reqMsg.getString(24));
            log.info("24 if : " + reqMsg.getString(24).equalsIgnoreCase("030"));
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
