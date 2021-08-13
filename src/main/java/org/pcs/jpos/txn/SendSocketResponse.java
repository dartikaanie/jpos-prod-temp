package org.pcs.jpos.txn;

import java.io.IOException;
import java.io.Serializable;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOFilter;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOSource;
import org.jpos.transaction.AbortParticipant;
import org.jpos.transaction.Context;
import org.jpos.util.Log;

/**
 *
 * @author ErwinSn
 */

public class SendSocketResponse implements AbortParticipant {
    
    Log log = Log.getLog("Q2", getClass().getName());

    @Override
    public int prepareForAbort(long id, Serializable context) {
        return PREPARED;
    }

    @Override
    public int prepare(long id, Serializable srlzbl) {
        
        log.info("SendSocketResponse-prepare");

        Context ctx = (Context) srlzbl;
        ISOSource source = (ISOSource) ctx.get("ISOSOURCE");
        if (source == null || !source.isConnected()) {
            return ABORTED;
        }
        return PREPARED;
    }

    @Override
    public void commit(long id, Serializable srlzbl) {
        sendResponse(srlzbl);
    }

    @Override
    public void abort(long id, Serializable srlzbl) {
        sendResponse(srlzbl);
    }
    
    private void sendResponse(Serializable srlzbl) {
        Context ctx = (Context) srlzbl;
        ISOSource source = (ISOSource) ctx.get("ISOSOURCE");
        ISOMsg out = (ISOMsg) ctx.get("RESPONSE_MSG");
    	log.info("SendSocketResponse-sendResponse: "+out);
        try {
            source.send(out);
        } catch (IOException ex) {
            log.error("SendSocketResponse-ISOFilter.VetoException-error"+ex);
        } catch (ISOFilter.VetoException ex) {
            log.error("SendSocketResponse-ISOFilter.VetoException-error"+ex);
        } catch (ISOException isoe) {
            log.error("SendSocketResponse-ISOException-error"+isoe);
        }
    }
    
}
