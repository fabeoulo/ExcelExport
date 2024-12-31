/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.service.db1;

import com.advantech.model.db1.Floor;
import com.advantech.model.db1.Floor_;
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
import com.advantech.model.db1.RequisitionState_;
import com.advantech.model.db1.Requisition_;
import static com.google.common.collect.Lists.newArrayList;
import java.util.ArrayList;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.joda.time.DateTime;

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

    private Date sD, eD;

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
        return repo.getOne(id);
    }

    public List<Requisition> findAllByIdWithUserAndState(List<Integer> ids) {
        return repo.findAllByIdIn(ids);
    }

    public List<Requisition> findAllByPoAndMatNoWithLazy(List<String> pos, List<String> matNos) {
        return repo.findAllByPoInAndMaterialNumberIn(pos, matNos);
    }

    public List<Requisition> findAllByHalfdayWithUserAndState() {
        return repo.findAll((Root<Requisition> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            root.fetch(Requisition_.USER, JoinType.LEFT);
            root.fetch(Requisition_.REQUISITION_STATE, JoinType.LEFT);

            setDatetime();
            Path<Date> dateEntryPath = root.get(Requisition_.receiveDate);
            Predicate datePredicate = cb.between(dateEntryPath, sD, eD);

            Path<RequisitionState> rsEntryPath = root.get(Requisition_.requisitionState);
            Predicate statePredicate = cb.equal(rsEntryPath.get(RequisitionState_.id), 5);

            cq.where(cb.and(datePredicate, statePredicate));

            cq.orderBy(cb.asc(root.get(Requisition_.werk)));
            return cq.getRestriction();
        });
    }

    private void setDatetime() {
        DateTime dt = new DateTime();
        DateTime sdt, edt;
        if (dt.getHourOfDay() < 17) {
            sdt = dt.minusDays(1).withTime(17, 0, 0, 1);
            edt = dt.withTime(12, 0, 0, 0);
        } else {
            sdt = dt.withTime(12, 0, 0, 1);
            edt = dt.withTime(17, 0, 0, 0);
        }
        sD = sdt.toDate();
        eD = edt.toDate();
    }

    public List<Requisition> findAllByCreateAndStateAndFloor(DateTime sdt, int state, List<Integer> floorIds) {
        return repo.findAll((Root<Requisition> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            root.fetch(Requisition_.FLOOR, JoinType.LEFT);
            root.fetch(Requisition_.USER, JoinType.LEFT);
            root.fetch(Requisition_.REQUISITION_STATE, JoinType.LEFT);

            Path<Date> dateEntryPath = root.get(Requisition_.createDate);
            Predicate datePredicate = cb.between(dateEntryPath, sdt.toDate(), sdt.plusDays(1).toDate());

            Path<RequisitionState> rsEntryPath = root.get(Requisition_.requisitionState);
            Predicate statePredicate = cb.equal(rsEntryPath.get(RequisitionState_.id), state);

            Path<Floor> floorPath = root.get(Requisition_.floor);
            Predicate floorPredicate = floorPath.get(Floor_.id).in(floorIds);

            return cb.and(datePredicate, statePredicate, floorPredicate);
        });
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
        return batchInsert(l, user);
    }

    public <S extends Requisition> int batchInsert(List<S> l, User user) {

        RequisitionState defaultState = stateRepo.getOne(4);
        RequisitionType defaultType = typeRepo.getOne(1);
        List<RequisitionReason> reasonL = reasonRepo.findAll();
        RequisitionReason defaultReason = reasonL.stream().filter(rl -> rl.getId() == 2).findFirst().get();

        for (Requisition r : l) {
            RequisitionReason reason = reasonL.stream().filter(rl -> rl.getId() == r.getRequisitionReason().getId())
                    .findFirst().orElse(defaultReason);

            r.setRequisitionState(defaultState);
            r.setRequisitionType(defaultType);
            r.setRequisitionReason(reason);
            r.setUser(user);
            repo.save(r);
            RequisitionEvent e = new RequisitionEvent(r, user, defaultState, r.getRemark());
            eventRepo.save(e);
        }

        return 1;
    }

    public void changeState(int requisition_id, int state_id) {
        Requisition r = this.findByIdWithLazy(requisition_id);
        updateWithStateAndEvent(newArrayList(r), state_id);
    }

    public int updateWithStateAndEvent(List<Requisition> l, int state_id) {

        RequisitionState state = stateRepo.getOne(state_id);
        User user = SecurityPropertiesUtils.retrieveAndCheckUserInSession();
        List<RequisitionEvent> reLists = new ArrayList<>();
        for (Requisition r : l) {
            r.setRequisitionState(state);
            Date now = new Date();
            if (state_id == 4 || state_id == 5) {
                r.setReceiveDate(now);
            } else if (state_id == 6 || state_id == 7) {
                r.setReturnDate(now);
            }

            RequisitionEvent e = new RequisitionEvent(r, user, state, r.getRemark());
            reLists.add(e);
        }
        repo.saveAll(l);
        eventRepo.saveAll(reLists);

        return 1;
    }

    public void delete(Requisition t) {
        repo.delete(t);
    }

    public void deleteAll(Iterable<? extends Requisition> itrbl) {
        repo.deleteAll(itrbl);
    }

}
