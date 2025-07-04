/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.helper;

import com.advantech.model.db1.ScrappedDetail;
import com.advantech.model.db1.User;
import com.advantech.model.db2.Users;
import com.advantech.repo.db1.FqcKanBanRepository;
import com.advantech.repo.db1.ScrappedDetailRepository;
import com.advantech.repo.db1.UserRepository;
import com.advantech.repo.db2.OrderTypesRepository;
import com.advantech.repo.db2.UsersRepository;
import com.advantech.webservice.Factory;
import com.advantech.webservice.port.FqcKanBanQueryPort;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Wei.Cheng
 */
@WebAppConfiguration
@ContextConfiguration(locations = {
    "classpath:servlet-context_test.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
public class TestHibernate {

    @Autowired
    private ScrappedDetailRepository repo;

    @Autowired
    @Qualifier("entityManagerFactory")
    private EntityManagerFactory factory;

    @Autowired
    @Qualifier("entityManagerFactory2")
    private EntityManagerFactory factory2;

    @Autowired
    @Qualifier("entityManagerFactory3")
    private EntityManagerFactory factory3;
    
    @Autowired
    private UserRepository userRepo;

//    @Test
    @Transactional
    @Rollback(true)
    public void testPojo() {
        ScrappedDetail o = repo.findById(1).get();
        assertNotNull(o);
        HibernateObjectPrinter.print(o);
    }

//    @Test
    public void testPojo2() {
        EntityManager manager = factory.createEntityManager();
        ScrappedDetail o = manager.find(ScrappedDetail.class, 1);
        assertNotNull(o);
        HibernateObjectPrinter.print(o);
    }

//    @Test
//    @Transactional("transactionManager2")
    @Rollback(true)
    public void testFactory2Pojo() {
        EntityManager manager = factory2.createEntityManager();
        List l = manager.createNativeQuery("select * from T_OutputValueSummaryDaily").setMaxResults(1).getResultList();
        HibernateObjectPrinter.print(l);

    }

//    @Test
//    @Transactional("transactionManager3")
    @Rollback(true)
    public void testFactory3Pojo() {
        EntityManager manager = factory3.createEntityManager();
        List l = manager.createNativeQuery("SELECT \"ZACTCST\", \"ERDAT\", \"SWERK\" FROM rv_biprd_ztpp_zrpp87s").setMaxResults(1).getResultList();
        HibernateObjectPrinter.print(l);

    }
    
//    @Test
    @Rollback(false)
    public void testResetPassword() {
        List<User> users = userRepo.findAll();
        CustomPasswordEncoder e = new CustomPasswordEncoder();
        users.forEach(u -> {
            u.setPassword(e.encode(u.getJobnumber()));
        });
        userRepo.saveAll(users);
    }

    @Autowired
    private FqcKanBanQueryPort kanbanPort;

    @Autowired
    private FqcKanBanRepository fqcKanBanRepo;

//    @Test
    @Rollback(false)
    public void testFqcKanban() throws Exception {
        List l = kanbanPort.query(Factory.TWM3);

        assertTrue(!l.isEmpty());

        fqcKanBanRepo.saveAll(l);
    }

//    @Test
    @Transactional("tx1")
    @Rollback(true)
    public void testDb1() throws Exception {
        ScrappedDetail o = repo.findById(2958).get();
        assertNotNull(o);
        HibernateObjectPrinter.print(o);
    }

    @Autowired
    private UsersRepository usersRepo;

//    @Test
    @Transactional("tx2")
    @Rollback(true)
    public void testDb2() throws Exception {
        Users u = usersRepo.findById("A-0023").get();
        assertNotNull(u);
        HibernateObjectPrinter.print(u);
    }
    
    @Autowired
    private OrderTypesRepository otRepo;

    @Test
    @Transactional("tx2")
    @Rollback(false)
    public void testOrderType() throws Exception {
        List l = otRepo.findAll();
        assertTrue(!l.isEmpty());
        HibernateObjectPrinter.print(l);
    }

}
