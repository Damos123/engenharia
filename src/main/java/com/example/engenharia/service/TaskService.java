package com.example.engenharia.service;

import com.example.engenharia.model.Task;
import com.example.engenharia.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task criar(Task task) {
        task.setConcluida(false);
        return taskRepository.save(task);
    }

    public List<Task> listarTodas() {
        return taskRepository.findAll();
    }

    public Optional<Task> buscarPorId(Long id) {
        return taskRepository.findById(id);
    }

    public Task atualizar(Long id, Task taskAtualizada) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada com id: " + id));

        task.setTitulo(taskAtualizada.getTitulo());
        task.setDescricao(taskAtualizada.getDescricao());
        task.setCategoria(taskAtualizada.getCategoria());
        task.setDificuldade(taskAtualizada.getDificuldade());
        task.setTempoEstimadoMinutos(taskAtualizada.getTempoEstimadoMinutos());

        return taskRepository.save(task);
    }

    public void deletar(Long id) {
        taskRepository.deleteById(id);
    }

    public Task marcarComoConcluida(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada com id: " + id));
        task.setConcluida(true);
        return taskRepository.save(task);
    }

    public List<Task> listarPorStatus(Boolean concluida) {
        return taskRepository.findByConcluida(concluida);
    }

    public List<Task> listarPorCategoria(String categoria) {
        return taskRepository.findByCategoria(categoria);
    }

    public List<Task> listarPorDificuldade(String dificuldade) {
        return taskRepository.findByDificuldade(dificuldade);
    }
}
