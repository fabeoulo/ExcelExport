/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.webservice;

import static com.google.common.base.Preconditions.checkState;
import java.io.IOException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.tempuri.ObjectFactory;
import org.tempuri.Rv;
import org.tempuri.RvResponse;
import org.tempuri.Tx;
import org.tempuri.TxResponse;

/**
 *
 * @author Wei.Cheng
 */
@Component
public class MultiWsClient {

    @Autowired
    @Qualifier("resourceMap")
    private Map<Factory, WebServiceTemplate> resourceMap;

    public TxResponse simpleTxSendAndReceive(String v, UploadType type, final Factory f) throws IOException {
        ObjectFactory factory = new ObjectFactory();
        Tx tx = factory.createTx();
        tx.setSParam(v);
        tx.setSType(type.toString());
        return (TxResponse) marshalSendAndReceive(tx, f);
    }

    public RvResponse simpleRvSendAndReceive(String v, final Factory f) {
        ObjectFactory factory = new ObjectFactory();
        Rv rv = factory.createRv();
        rv.setSParam(v);
        return (RvResponse) marshalSendAndReceive(rv, f);
    }

    private Object marshalSendAndReceive(Object request, final Factory f) {
        WebServiceTemplate t = this.getWebServiceTemplate(f);
        checkState(t != null, f.getName() + " webService template is not inject");
        return t.marshalSendAndReceive(request);
    }

    private WebServiceTemplate getWebServiceTemplate(Factory f) {
        WebServiceTemplate t = resourceMap.get(f);
        return t;
    }
}
