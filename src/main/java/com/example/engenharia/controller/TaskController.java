package com.example.engenharia.controller;

import com.example.engenharia.model.Task;
import com.example.engenharia.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<Task> criar(@Valid @RequestBody Task task) {
        Task novaTask = taskService.criar(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaTask);
    }

    @GetMapping
    public ResponseEntity<List<Task>> listarTodas() {
        return ResponseEntity.ok(taskService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> buscarPorId(@PathVariable Long id) {
        return taskService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> atualizar(@PathVariable Long id, @Valid @RequestBody Task task) {
        Task taskAtualizada = taskService.atualizar(id, task);
        return ResponseEntity.ok(taskAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        taskService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/concluir")
    public ResponseEntity<Task> marcarComoConcluida(@PathVariable Long id) {
        Task taskConcluida = taskService.marcarComoConcluida(id);
        return ResponseEntity.ok(taskConcluida);
    }

    @GetMapping("/status/{concluida}")
    public ResponseEntity<List<Task>> listarPorStatus(@PathVariable Boolean concluida) {
        return ResponseEntity.ok(taskService.listarPorStatus(concluida));
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Task>> listarPorCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(taskService.listarPorCategoria(categoria));
    }

    @GetMapping("/dificuldade/{dificuldade}")
    public ResponseEntity<List<Task>> listarPorDificuldade(@PathVariable String dificuldade) {
        return ResponseEntity.ok(taskService.listarPorDificuldade(dificuldade));
    }
}
