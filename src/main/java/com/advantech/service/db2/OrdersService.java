/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.service.db2;

import com.advantech.model.db1.User;
import com.advantech.model.db2.Items;
import com.advantech.model.db2.Orders;
import com.advantech.model.db2.QryWipAtt;
import com.advantech.model.db2.Users;
import com.advantech.repo.db2.ItemsRepository;
import com.advantech.repo.db2.OrdersRepository;
import com.advantech.repo.db2.UsersRepository;
import com.advantech.sap.SapQueryPort;
import com.advantech.webservice.Factory;
import com.advantech.webservice.port.QryWipAttQueryPort;
import static com.google.common.base.Preconditions.checkState;
import com.google.common.collect.Lists;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Wei.Cheng
 */
@Service
@Transactional("tx2")
public class OrdersService {

    @Autowired
    private OrdersRepository repo;

    @Autowired
    private ItemsRepository itemRepo;

    @Autowired
    private UsersRepository usersRepo;

    @Autowired
    private SapQueryPort port;

    public List<Orders> findAll() {
        return repo.findAll();
    }

    public List<Orders> findAllLackWithUserItem(Integer teamsId) {
        return repo.findAllLackWithUserItem(teamsId);
    }

    public List<Orders> findAllOpenWithoutReply() {
        return repo.findAllOpenWithoutReply();
    }

    public Optional<Orders> findById(Integer id) {
        return repo.findById(id);
    }

    public void saveAll(List<Orders> l) {
        if (!l.isEmpty()) {
            List<Orders> result = repo.saveAll(l);
            List<Integer> ids = result.stream().map(Orders::getId).collect(Collectors.toList());
            repo.updateTimeStampToZeroByIdIn(ids);
        }
    }

    public <S extends Orders> S save(S s, Items i) throws Exception {
        //Retrive modelName from MES port(M3, M2, M6)
        String po = i.getLabel1();
        JCoFunction function = port.getMaterialInfo(po, null);
        JCoTable master = function.getTableParameterList().getTable("ZWOMASTER");//工單機種對應明細

        for (int r = 0; r < master.getNumRows(); r++) {
            master.setRow(r);
            i.setLabel2(master.getString("MATNR"));
            break;
        }

        checkState(i.getLabel2() != null && !"".equals(i.getLabel2()), "Can't find modelName in current po");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userInSession = (User) auth.getPrincipal();

        //Auto change user's identity when ADMIN is testing data.
        Users remoteUser = usersRepo.findById(userInSession.getJobnumber()).orElse(null);
        if (remoteUser == null && auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            remoteUser = usersRepo.getOne("test4");
        }

        checkState(remoteUser != null, "Remote user's account not found");
        s.setUsers(remoteUser);
        s.setTeams(remoteUser.getTeams());

        repo.save(s);
        i.setOrders(s);
        itemRepo.save(i);

        repo.updateTimeStampToZeroByIdIn(Lists.newArrayList(s.getId()));
        return null;
    }

}
