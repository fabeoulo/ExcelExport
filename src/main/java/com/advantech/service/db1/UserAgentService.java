/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.service.db1;

import com.advantech.model.db1.UserAgent;
import com.advantech.model.db1.UserAgent_;
import com.advantech.repo.db1.UserAgentRepository;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Justin.Yeh
 */
@Service
@Transactional("tx1")
public class UserAgentService {

    @Autowired
    private UserAgentRepository repo;

    public List<UserAgent> findAll() {
        return repo.findAll();
    }

    public DataTablesOutput<UserAgent> findAll(DataTablesInput dti) {
        return repo.findAll(dti);
    }

    public DataTablesOutput<UserAgent> findAllUserAgent(DataTablesInput dti) { 
        Specification<UserAgent> s = (Root<UserAgent> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            Predicate labelAgentCodeOne = cb.notEqual(root.get(UserAgent_.LABEL_AGENT_CODE), 1);

            return cb.and(labelAgentCodeOne);
        };
        return repo.findAll(dti, s);
    }

    public DataTablesOutput<UserAgent> findAllLabelAgent(DataTablesInput dti) {
        Specification<UserAgent> s = (Root<UserAgent> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            Predicate labelAgentCodeOne = cb.equal(root.get(UserAgent_.LABEL_AGENT_CODE), 1);

            return cb.and(labelAgentCodeOne);
        };
        return repo.findAll(dti, s);
    }

    public List<UserAgent> findAllInDateWithUser(Date today) {
        return repo.findAll((Root<UserAgent> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            root.fetch(UserAgent_.USER, JoinType.LEFT);
            Path<Date> sdPath = root.get(UserAgent_.beginDate);

            return cb.equal(sdPath, today);
        });
    }

    public List<UserAgent> findAllLabelAgentWithUser() {
        return repo.findAll((Root<UserAgent> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            root.fetch(UserAgent_.USER, JoinType.LEFT);
            Path<Integer> agentCodePath = root.get(UserAgent_.labelAgentCode);

            return cb.equal(agentCodePath, 1);
        });
    }

    public Optional<UserAgent> findByBeginDate(Date d) {
        return repo.findByBeginDate(d);
    }

    public <S extends UserAgent> S save(S s) {
        return repo.save(s);
    }

    public void delete(UserAgent t) {
        repo.delete(t);
    }
}
