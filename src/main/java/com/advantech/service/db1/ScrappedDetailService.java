/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.service.db1;

import com.advantech.model.db1.Floor;
import com.advantech.model.db1.MaterialNumberSum;
import com.advantech.model.db1.ScrappedDetail;
import com.advantech.model.db1.ScrappedDetailCount;
import com.advantech.repo.db1.ScrappedDetailRepository;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Wei.Cheng
 */
@Service
@Transactional("tx1")
public class ScrappedDetailService {

    @Autowired
    private ScrappedDetailRepository repo;

    public List<ScrappedDetail> findByFloor(Floor floor) {
        return repo.findByFloor(floor);
    }

    public List<ScrappedDetail> findByCreateDateBetween(DateTime sD, DateTime eD) {
        return repo.findByCreateDateBetween(sD.toDate(), eD.toDate());
    }

    public List<ScrappedDetail> findAll() {
        return repo.findAll();
    }

    public List<MaterialNumberSum> findMaterialNumberSum(Date sD, Date eD) {
        return repo.findMaterialNumberSum(sD, eD);
    }

    public List<ScrappedDetailCount> findUserScrappedDetailCount(Date sD, Date eD, Date startDateOfYear) {
        return repo.findUserScrappedDetailCount(sD, eD, startDateOfYear);
    }

    public List<ScrappedDetail> findByFloorAndYk(List<Floor> floors, int yk) {
        return repo.findByFloorInAndYk(floors, yk);
    }

    public DataTablesOutput<ScrappedDetail> findAll(DataTablesInput dti) {
        return repo.findAll(dti);
    }

    public DataTablesOutput<ScrappedDetail> findAll(DataTablesInput dti, Specification<ScrappedDetail> s) {
        return repo.findAll(dti, s);
    }

    public DataTablesOutput<ScrappedDetail> findAll(DataTablesInput dti, Specification<ScrappedDetail> s, Specification<ScrappedDetail> s1) {
        return repo.findAll(dti, s, s1);
    }

    public <R> DataTablesOutput<R> findAll(DataTablesInput dti, Function<ScrappedDetail, R> fnctn) {
        return repo.findAll(dti, fnctn);
    }

    public <R> DataTablesOutput<R> findAll(DataTablesInput dti, Specification<ScrappedDetail> s, Specification<ScrappedDetail> s1, Function<ScrappedDetail, R> fnctn) {
        return repo.findAll(dti, s, s1, fnctn);
    }

    public <S extends ScrappedDetail> List<S> saveAll(Iterable<S> itrbl) {
        return repo.saveAll(itrbl);
    }

    public <S extends ScrappedDetail> S save(S s) {
        return repo.save(s);
    }

    public Optional<ScrappedDetail> findById(Integer id) {
        return repo.findById(id);
    }

    public void delete(ScrappedDetail t) {
        repo.delete(t);
    }

    public void deleteAll() {
        repo.deleteAll();
    }

}
