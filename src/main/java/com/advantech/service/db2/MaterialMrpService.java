/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.service.db2;

import com.advantech.model.db2.MaterialMrp;
import com.advantech.repo.db2.MaterialMrpRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Justin.Yeh
 */
@Service
@Transactional("tx2")
public class MaterialMrpService {

    @Autowired
    private MaterialMrpRepository repo;

    @Autowired
    private OrderResponseOwnersService ownerService;

    public List<MaterialMrp> findAll() {
        return repo.findAll();
    }

    public Optional<MaterialMrp> findById(Integer id) {
        return repo.findById(id);
    }

    public <S extends MaterialMrp> List<S> saveAll(Iterable<S> entities) {
        return repo.saveAll(entities);
    }

    public Map<String, String> getMrpMap(List<String> plants, List<String> materials) {
        List<MaterialMrp> l = repo.findByPlantInAndMatNameIn(plants, materials);
        List<String> mrpCodes = l.stream().map(MaterialMrp::getMrpCode).collect(Collectors.toList());
        Map<String, String> mapOwner = ownerService.getMrpOwnerMap(mrpCodes);
        return l.stream().filter(mm -> mapOwner.containsKey(mm.getMrpCode()))
                .collect(Collectors.toMap(
                        mm -> mm.getPlant() + mm.getMatName(),
                        mm -> mapOwner.get(mm.getMrpCode()),
                        (oldValue, newValue) -> newValue
                ));
    }
}
