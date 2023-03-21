/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.service.db1;

import com.advantech.security.SecurityPropertiesUtils;
import com.advantech.model.db1.Requisition;
import com.advantech.model.db1.RequisitionEvent;
import com.advantech.model.db1.RequisitionReason;
import com.advantech.model.db1.RequisitionState;
import com.advantech.model.db1.RequisitionType;
import com.advantech.model.db1.User;
import com.advantech.repo.db1.RequisitionEventRepository;
import com.advantech.repo.db1.RequisitionReasonRepository;
import com.advantech.repo.db1.RequisitionRepository;
import com.advantech.repo.db1.RequisitionStateRepository;
import com.advantech.repo.db1.RequisitionTypeRepository;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.advantech.model.db1.ModelMaterialDetails;
import org.hibernate.Hibernate;

/**
 *
 * @author Wei.Cheng
 */
@Service
@Transactional("tx1")
public class RequisitionService {

    @Autowired
    private RequisitionRepository repo;

    @Autowired
    private RequisitionEventRepository eventRepo;

    @Autowired
    private RequisitionStateRepository stateRepo;

    @Autowired
    private RequisitionTypeRepository typeRepo;

    @Autowired
    private RequisitionReasonRepository reasonRepo;

    public DataTablesOutput<Requisition> findAll(DataTablesInput dti) {
        return repo.findAll(dti);
    }

    public DataTablesOutput<Requisition> findAll(DataTablesInput dti, Specification<Requisition> s) {
        return repo.findAll(dti, s);
    }

    public DataTablesOutput<Requisition> findAll(DataTablesInput dti, Specification<Requisition> s, Specification<Requisition> s1) {
        return repo.findAll(dti, s, s1);
    }

    public <R> DataTablesOutput<R> findAll(DataTablesInput dti, Function<Requisition, R> fnctn) {
        return repo.findAll(dti, fnctn);
    }

    public <R> DataTablesOutput<R> findAll(DataTablesInput dti, Specification<Requisition> s, Specification<Requisition> s1, Function<Requisition, R> fnctn) {
        return repo.findAll(dti, s, s1, fnctn);
    }

    public Optional<Requisition> findById(Integer id) {
        return repo.findById(id);
    }

    public Requisition findByIdWithLazy(Integer id) {
        Requisition i = repo.findById(id).orElse(null);//.get();
        
        if (i == null) {
            return null;
        }

        //Initialize the lazy loading relative object
        Hibernate.initialize(i.getUser());
        Hibernate.initialize(i.getRequisitionState());
        return i;
    }

    public List<ModelMaterialDetails> findModelMaterialDetails(String modelName) {
        return repo.findModelMaterialDetails(modelName);
    }

    public <S extends Requisition> S save(S s, String remark) {
        RequisitionState stat;
        User user = SecurityPropertiesUtils.retrieveAndCheckUserInSession();
        if (s.getId() == 0) {
            stat = stateRepo.getOne(4);
            RequisitionType rType = typeRepo.getOne(1);

            s.setRequisitionState(stat);
            s.setRequisitionType(rType);
            s.setUser(user);
        } else {
            stat = s.getRequisitionState();
            int stateId = stat.getId();
            Date now = new Date();
            if (stateId == 4 || stateId == 5) {
                s.setReceiveDate(now);
            } else if (stateId == 6 || stateId == 7) {
                s.setReturnDate(now);
            }
        }

        S result = repo.save(s);

        RequisitionEvent e = new RequisitionEvent(s, user, stat, remark);
        eventRepo.save(e);

        return result;
    }

    public <S extends Requisition> int batchInsert(List<S> l) {
        User user = SecurityPropertiesUtils.retrieveAndCheckUserInSession();

        RequisitionState defaultState = stateRepo.getOne(4);
        RequisitionType defaultType = typeRepo.getOne(1);
        RequisitionReason defaultReason = reasonRepo.getOne(2);

        for (Requisition r : l) {
            r.setRequisitionState(defaultState);
            r.setRequisitionType(defaultType);
            r.setRequisitionReason(defaultReason);
            r.setUser(user);
            repo.save(r);
            RequisitionEvent e = new RequisitionEvent(r, user, defaultState, r.getRemark());
            eventRepo.save(e);
        }

        return 1;
    }

    public void changeState(int requisition_id, int state_id) {
        Requisition r = this.findById(requisition_id).get();
        User user = SecurityPropertiesUtils.retrieveAndCheckUserInSession();
        RequisitionState state = stateRepo.getOne(state_id);
        r.setRequisitionState(state);
        RequisitionEvent e = new RequisitionEvent(r, user, state, "");

        repo.save(r);
        eventRepo.save(e);
    }

    public void delete(Requisition t) {
        repo.delete(t);
    }

    public void deleteAll(Iterable<? extends Requisition> itrbl) {
        repo.deleteAll(itrbl);
    }

}
