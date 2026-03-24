package com.example.engenharia.repository;

import com.example.engenharia.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByConcluida(Boolean concluida);

    List<Task> findByCategoria(String categoria);

    List<Task> findByDificuldade(String dificuldade);

    List<Task> findByConcluidaAndCategoria(Boolean concluida, String categoria);
}
