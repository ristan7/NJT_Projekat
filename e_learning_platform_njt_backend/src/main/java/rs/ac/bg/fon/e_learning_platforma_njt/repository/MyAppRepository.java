/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package rs.ac.bg.fon.e_learning_platforma_njt.repository;

import java.util.List;

/**
 *
 * @author mikir
 */
public interface MyAppRepository<E, ID> {
    //MyAppRepository<User, Long>

    List<E> findAll();

    E findById(ID id) throws Exception;

    void save(E entity); //za update imace ID za kreiranje nece jer je autoincrement

    void deleteById(ID id);
}
