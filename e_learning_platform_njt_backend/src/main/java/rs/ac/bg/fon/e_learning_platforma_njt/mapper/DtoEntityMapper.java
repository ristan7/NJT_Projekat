/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package rs.ac.bg.fon.e_learning_platforma_njt.mapper;

/**
 *
 * @author mikir
 */
public interface DtoEntityMapper<T, E> {

    T toDto(E e);

    E toEntity(T t);

    void apply(T t, E e);
}
