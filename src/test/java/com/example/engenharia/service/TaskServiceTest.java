package com.example.engenharia.service;

import com.example.engenharia.model.Task;
import com.example.engenharia.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1L);
        task.setTitulo("Teste Python");
        task.setDescricao("Descrição de teste");
        task.setCategoria("Basico");
        task.setDificuldade("Facil");
        task.setTempoEstimadoMinutos(30);
        task.setConcluida(false);
        task.setDataCriacao(LocalDateTime.now());
    }

    @Test
    void criar_DeveRetornarTaskSalva() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task result = taskService.criar(task);

        assertNotNull(result);
        assertEquals("Teste Python", result.getTitulo());
        assertFalse(result.getConcluida());
        verify(taskRepository).save(task);
    }

    @Test
    void listarTodas_DeveRetornarListaDeTasks() {
        Task task2 = new Task();
        task2.setId(2L);
        task2.setTitulo("Teste 2");
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task, task2));

        List<Task> result = taskService.listarTodas();

        assertEquals(2, result.size());
        verify(taskRepository).findAll();
    }

    @Test
    void buscarPorId_QuandoExiste_DeveRetornarTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Optional<Task> result = taskService.buscarPorId(1L);

        assertTrue(result.isPresent());
        assertEquals("Teste Python", result.get().getTitulo());
    }

    @Test
    void buscarPorId_QuandoNaoExiste_DeveRetornarVazio() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Task> result = taskService.buscarPorId(99L);

        assertFalse(result.isPresent());
    }

    @Test
    void atualizar_DeveRetornarTaskAtualizada() {
        Task atualizada = new Task();
        atualizada.setTitulo("Novo Título");
        atualizada.setDescricao("Nova Descrição");
        atualizada.setCategoria("Intermediario");
        atualizada.setDificuldade("Medio");
        atualizada.setTempoEstimadoMinutos(60);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task result = taskService.atualizar(1L, atualizada);

        assertNotNull(result);
        verify(taskRepository).findById(1L);
        verify(taskRepository).save(task);
    }

    @Test
    void atualizar_QuandoNaoExiste_DeveLancarExcecao() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> taskService.atualizar(99L, task));
    }

    @Test
    void deletar_DeveChamarDeleteById() {
        doNothing().when(taskRepository).deleteById(1L);

        taskService.deletar(1L);

        verify(taskRepository).deleteById(1L);
    }

    @Test
    void marcarComoConcluida_DeveRetornarTaskConcluida() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task result = taskService.marcarComoConcluida(1L);

        assertTrue(result.getConcluida());
        assertNotNull(result.getDataConclusao());
    }

    @Test
    void marcarComoConcluida_QuandoNaoExiste_DeveLancarExcecao() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> taskService.marcarComoConcluida(99L));
    }

    @Test
    void listarPorStatus_DeveRetornarTasksFiltradas() {
        when(taskRepository.findByConcluida(false)).thenReturn(Arrays.asList(task));

        List<Task> result = taskService.listarPorStatus(false);

        assertEquals(1, result.size());
        assertFalse(result.get(0).getConcluida());
    }

    @Test
    void listarPorCategoria_DeveRetornarTasksDaCategoria() {
        when(taskRepository.findByCategoria("Basico")).thenReturn(Arrays.asList(task));

        List<Task> result = taskService.listarPorCategoria("Basico");

        assertEquals(1, result.size());
        assertEquals("Basico", result.get(0).getCategoria());
    }

    @Test
    void listarPorDificuldade_DeveRetornarTasksDaDificuldade() {
        when(taskRepository.findByDificuldade("Facil")).thenReturn(Arrays.asList(task));

        List<Task> result = taskService.listarPorDificuldade("Facil");

        assertEquals(1, result.size());
        assertEquals("Facil", result.get(0).getDificuldade());
    }
}
