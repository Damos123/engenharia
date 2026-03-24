package com.example.engenharia.controller;

import com.example.engenharia.model.Task;
import com.example.engenharia.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TaskControllerIntegrationTest {

    @LocalServerPort
    private int port;

    private RestTemplate restTemplate;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        // Usar HttpComponentsClientHttpRequestFactory para suportar PATCH
        restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        // Não lançar exceções em erros HTTP (4xx, 5xx)
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false;
            }
        });
    }

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth("estudante", "python2024");
        return headers;
    }

    @Test
    void criar_DeveRetornarTaskCriada() throws Exception {
        Task task = new Task();
        task.setTitulo("Teste de Integração");
        task.setDescricao("Descrição do teste");
        task.setCategoria("Basico");
        task.setDificuldade("Facil");
        task.setTempoEstimadoMinutos(30);

        HttpEntity<Task> request = new HttpEntity<>(task, createAuthHeaders());
        ResponseEntity<Task> response = restTemplate.exchange(getBaseUrl() + "/api/tasks", HttpMethod.POST, request, Task.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitulo()).isEqualTo("Teste de Integração");
        assertThat(response.getBody().getConcluida()).isFalse();
    }

    @Test
    void criar_QuandoTituloInvalido_DeveRetornarBadRequest() throws Exception {
        Task task = new Task();
        task.setTitulo("AB");
        task.setCategoria("Basico");
        task.setDificuldade("Facil");
        task.setTempoEstimadoMinutos(30);

        HttpEntity<Task> request = new HttpEntity<>(task, createAuthHeaders());
        ResponseEntity<String> response = restTemplate.exchange(getBaseUrl() + "/api/tasks", HttpMethod.POST, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void criar_QuandoTempoInvalido_DeveRetornarBadRequest() throws Exception {
        Task task = new Task();
        task.setTitulo("Teste Válido");
        task.setCategoria("Basico");
        task.setDificuldade("Facil");
        task.setTempoEstimadoMinutos(2);

        HttpEntity<Task> request = new HttpEntity<>(task, createAuthHeaders());
        ResponseEntity<String> response = restTemplate.exchange(getBaseUrl() + "/api/tasks", HttpMethod.POST, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void listarTodas_DeveRetornarListaDeTasks() throws Exception {
        Task task1 = new Task();
        task1.setTitulo("Task 1");
        task1.setCategoria("Basico");
        task1.setDificuldade("Facil");
        task1.setTempoEstimadoMinutos(30);
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setTitulo("Task 2");
        task2.setCategoria("Avancado");
        task2.setDificuldade("Dificil");
        task2.setTempoEstimadoMinutos(60);
        taskRepository.save(task2);

        HttpEntity<Void> request = new HttpEntity<>(createAuthHeaders());
        ResponseEntity<Task[]> response = restTemplate.exchange(getBaseUrl() + "/api/tasks", HttpMethod.GET, request, Task[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void buscarPorId_QuandoExiste_DeveRetornarTask() throws Exception {
        Task task = new Task();
        task.setTitulo("Buscar Teste");
        task.setCategoria("Basico");
        task.setDificuldade("Facil");
        task.setTempoEstimadoMinutos(30);
        Task saved = taskRepository.save(task);

        HttpEntity<Void> request = new HttpEntity<>(createAuthHeaders());
        ResponseEntity<Task> response = restTemplate.exchange(getBaseUrl() + "/api/tasks/" + saved.getId(), HttpMethod.GET, request, Task.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getTitulo()).isEqualTo("Buscar Teste");
    }

    @Test
    void buscarPorId_QuandoNaoExiste_DeveRetornarNotFound() throws Exception {
        HttpEntity<Void> request = new HttpEntity<>(createAuthHeaders());
        ResponseEntity<String> response = restTemplate.exchange(getBaseUrl() + "/api/tasks/999", HttpMethod.GET, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void atualizar_DeveRetornarTaskAtualizada() throws Exception {
        Task task = new Task();
        task.setTitulo("Original");
        task.setCategoria("Basico");
        task.setDificuldade("Facil");
        task.setTempoEstimadoMinutos(30);
        Task saved = taskRepository.save(task);

        Task atualizada = new Task();
        atualizada.setTitulo("Atualizado");
        atualizada.setDescricao("Nova descrição");
        atualizada.setCategoria("Intermediario");
        atualizada.setDificuldade("Medio");
        atualizada.setTempoEstimadoMinutos(45);

        HttpEntity<Task> request = new HttpEntity<>(atualizada, createAuthHeaders());
        ResponseEntity<Task> response = restTemplate.exchange(getBaseUrl() + "/api/tasks/" + saved.getId(), HttpMethod.PUT, request, Task.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getTitulo()).isEqualTo("Atualizado");
    }

    @Test
    void marcarComoConcluida_DeveRetornarTaskConcluida() throws Exception {
        Task task = new Task();
        task.setTitulo("Para Concluir");
        task.setCategoria("Basico");
        task.setDificuldade("Facil");
        task.setTempoEstimadoMinutos(30);
        Task saved = taskRepository.save(task);

        HttpEntity<Void> request = new HttpEntity<>(createAuthHeaders());
        ResponseEntity<Task> response = restTemplate.exchange(getBaseUrl() + "/api/tasks/" + saved.getId() + "/concluir", HttpMethod.PATCH, request, Task.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getConcluida()).isTrue();
    }

    @Test
    void deletar_DeveRetornarNoContent() throws Exception {
        Task task = new Task();
        task.setTitulo("Para Deletar");
        task.setCategoria("Basico");
        task.setDificuldade("Facil");
        task.setTempoEstimadoMinutos(30);
        Task saved = taskRepository.save(task);

        HttpEntity<Void> request = new HttpEntity<>(createAuthHeaders());
        ResponseEntity<Void> response = restTemplate.exchange(getBaseUrl() + "/api/tasks/" + saved.getId(), HttpMethod.DELETE, request, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void listarPorStatus_DeveRetornarTasksFiltradas() throws Exception {
        Task task1 = new Task();
        task1.setTitulo("Pendente");
        task1.setCategoria("Basico");
        task1.setDificuldade("Facil");
        task1.setTempoEstimadoMinutos(30);
        task1.setConcluida(false);
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setTitulo("Concluida");
        task2.setCategoria("Basico");
        task2.setDificuldade("Facil");
        task2.setTempoEstimadoMinutos(30);
        task2.setConcluida(true);
        taskRepository.save(task2);

        HttpEntity<Void> request = new HttpEntity<>(createAuthHeaders());
        ResponseEntity<Task[]> response = restTemplate.exchange(getBaseUrl() + "/api/tasks/status/false", HttpMethod.GET, request, Task[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody()[0].getTitulo()).isEqualTo("Pendente");
    }

    @Test
    void listarPorCategoria_DeveRetornarTasksDaCategoria() throws Exception {
        Task task = new Task();
        task.setTitulo("Task Data Science");
        task.setCategoria("DataScience");
        task.setDificuldade("Dificil");
        task.setTempoEstimadoMinutos(60);
        taskRepository.save(task);

        HttpEntity<Void> request = new HttpEntity<>(createAuthHeaders());
        ResponseEntity<Task[]> response = restTemplate.exchange(getBaseUrl() + "/api/tasks/categoria/DataScience", HttpMethod.GET, request, Task[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody()[0].getCategoria()).isEqualTo("DataScience");
    }

    @Test
    void listarPorDificuldade_DeveRetornarTasksDaDificuldade() throws Exception {
        Task task = new Task();
        task.setTitulo("Task Dificil");
        task.setCategoria("Avancado");
        task.setDificuldade("Dificil");
        task.setTempoEstimadoMinutos(90);
        taskRepository.save(task);

        HttpEntity<Void> request = new HttpEntity<>(createAuthHeaders());
        ResponseEntity<Task[]> response = restTemplate.exchange(getBaseUrl() + "/api/tasks/dificuldade/Dificil", HttpMethod.GET, request, Task[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody()[0].getDificuldade()).isEqualTo("Dificil");
    }

    @Test
    void acessarSemAutenticacao_DeveRetornarUnauthorized() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl() + "/api/tasks", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
