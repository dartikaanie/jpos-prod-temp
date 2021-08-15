package org.pcs.jpos.iso;

import java.io.IOException;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISORequestListener;
import org.jpos.iso.ISOSource;
import org.jpos.space.Space;
import org.jpos.space.SpaceFactory;
import org.jpos.transaction.Context;
import org.jpos.util.Log;
import org.jpos.util.LogSource;
import org.jpos.util.Logger;

/**
 *
 * @author ErwinSn
 */
public class SocketISORequestHandler implements ISORequestListener, LogSource, Configurable {

    Configuration cfg;
    Log log;
    Logger logger;
    String realm;
    String queueName;
    long queueTimeout;

    @Override
    public boolean process(ISOSource source, ISOMsg m) {
        
        log.info("SocketISORequestHandler-start");

        // Create new request context object
        Context reqContext = new Context();
        
        // Put request message object to context
        reqContext.put("REQUEST_MSG", m);
        
        // put request source to context
        reqContext.put("ISOSOURCE", source);
        
        // Call the space
        Space sp = SpaceFactory.getSpace();
        
        // Put the context to the space queue
        sp.out(queueName, reqContext, queueTimeout);
        
        // The process are async so it must be return true
        return true;
    }

    @Override
    public void setLogger(Logger logger, String realm) {
        this.logger = logger;
        this.realm = realm;
    }

    @Override
    public String getRealm() {
        return this.realm;
    }

    @Override
    public Logger getLogger() {
        return this.logger;
    }

    @Override
    public void setConfiguration(Configuration c) throws ConfigurationException {
        this.cfg = c;
        log = Log.getLog("Q2", getClass().getName());
        this.queueName = c.get("queue", "txnmgr-srv");
        this.queueTimeout = cfg.getLong("queue-timeout", 60000);
    }
}
