/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.webservice.port;

import com.advantech.webservice.root.FqcKanBanQueryRoot;
import com.advantech.model.db1.FqcKanBan;
import com.advantech.webservice.Factory;
import com.advantech.webservice.unmarshallclass.FqcKanBans;
import java.util.List;
import javax.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author Wei.Cheng
 */
@Component
public class FqcKanBanQueryPort extends BasicQueryPort {

    private static final Logger logger = LoggerFactory.getLogger(FqcKanBanQueryPort.class);

    @Override
    protected void initJaxb() {
        try {
            super.initJaxb(FqcKanBanQueryRoot.class, FqcKanBans.class);
        } catch (JAXBException e) {
            logger.error(e.toString());
        }
    }

    public List<FqcKanBan> query(Factory f) throws Exception {
        FqcKanBanQueryRoot root = new FqcKanBanQueryRoot();
        List<FqcKanBan> l = this.query(root, f);
        return l;
    }

}
