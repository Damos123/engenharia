# 🔄 Como Backend e Frontend se Comunicam

## Resumo Visual da Conexão

```
┌──────────────────┐         ┌──────────────────┐         ┌──────────────────┐
│     BROWSER      │  HTTP   │   SPRING BOOT    │   SQL   │      MYSQL       │
│                  │ ───────→ │                  │ ───────→│                  │
│  dashboard.html  │         │   TaskController │         │   python_studies │
│  dashboard.js    │ ←─────── │   TaskService    │ ←───────│                  │
│  dashboard.css   │  JSON   │   TaskRepository │         │                  │
└──────────────────┘         └──────────────────┘         └──────────────────┘
       ↓                              ↓
   Usuário visualiza              Regras de negócio
   gráficos e interage            e validações
```

## Fluxo de Dados

### 1️⃣ Criar Tarefa (Frontend → Backend)
```
Usuário preenche formulário
       ↓
dashboard.js: fetch POST /api/tasks
       ↓
TaskController.criar(@Valid Task)
       ↓
TaskService.criar() - aplica regras
       ↓
TaskRepository.save() → MySQL
       ↓
Retorna JSON com a tarefa criada
       ↓
Dashboard atualiza gráficos automaticamente
```

### 2️⃣ Listar Tarefas (Backend → Frontend)
```
Dashboard carrega
       ↓
dashboard.js: fetch GET /api/tasks
       ↓
TaskController.listarTodas()
       ↓
TaskService.listarTodas()
       ↓
TaskRepository.findAll() ← MySQL
       ↓
Retorna JSON array
       ↓
dashboard.js calcula estatísticas
       ↓
Atualiza gráficos e lista
```

### 3️⃣ Marcar Concluída
```
Usuário clica no checkbox
       ↓
dashboard.js: fetch PATCH /api/tasks/{id}/concluir
       ↓
TaskController.marcarComoConcluida()
       ↓
Task atualizada no banco
       ↓
Retorna tarefa com concluida=true
       ↓
Progresso recalculado automaticamente
```

## Tecnologias de Conexão

| Componente | Tecnologia | Função |
|------------|------------|--------|
| Protocolo | HTTP/1.1 | Comunicação web |
| Formato | JSON | Troca de dados |
| API Style | REST | Padrão de endpoints |
| Auth | HTTP Basic | Segurança |
| Client | Fetch API | JavaScript nativo |
| Server | Spring Web | Controller REST |

## Exemplo Real de Código

### Frontend (JavaScript)
```javascript
// dashboard.js - Linha 31-49
async function loadTasks() {
    const response = await fetch('http://localhost:8080/api/tasks', {
        headers: {
            'Authorization': 'Basic ' + btoa('estudante:python2024')
        }
    });
    const tasks = await response.json(); // ← Recebe do backend
    updateDashboard(tasks);
}
```

### Backend (Java)
```java
// TaskController.java - Linha 28-31
@GetMapping
public ResponseEntity<List<Task>> listarTodas() {
    return ResponseEntity.ok(taskService.listarTodas()); // ← Envia para frontend
}
```

## URLs Importantes

| URL | Descrição |
|-----|-----------|
| `http://localhost:8080` | Dashboard (Frontend) |
| `http://localhost:8080/api/tasks` | API REST (Backend) |
| `mysql://localhost:3306/python_studies` | Banco de Dados |

## Segurança na Conexão

```
[Browser] ──(HTTPS recomendado)──→ [Spring Security Filter]
                                         ↓
                                    Verifica Authorization Header
                                         ↓
                                    Usuário: estudante
                                    Senha: python2024
                                         ↓
                                    Permite acesso à API
```

## Para Testar a Conexão

```bash
# 1. Iniciar aplicação
./mvnw spring-boot:run

# 2. Testar API via curl (equivalente ao que o frontend faz)
curl -u estudante:python2024 \
  -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Teste","categoria":"Basico","dificuldade":"Facil","tempoEstimadoMinutos":30}'

# 3. Listar tarefas
curl -u estudante:python2024 \
  http://localhost:8080/api/tasks
```

## Erros Comuns de Conexão

| Erro | Causa | Solução |
|------|-------|---------|
| 401 Unauthorized | Senha incorreta | Verificar credenciais em dashboard.js |
| 404 Not Found | Endpoint errado | Verificar URL da API |
| 400 Bad Request | Dados inválidos | Verificar validações no modelo |
| CORS error | Origem bloqueada | Verificar @CrossOrigin no controller |
| Connection refused | Backend não rodando | Iniciar com `./mvnw spring-boot:run` |

---

**A conexão já está totalmente funcional!** Abra `http://localhost:8080` após iniciar o backend.
