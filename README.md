# 🐍 Python Studies Tracker

## O que é este programa?

O **Python Studies Tracker** é um microserviço completo para gerenciar e acompanhar seu progresso nos estudos de Python. Ele permite criar tarefas de estudo organizadas por categoria e dificuldade, visualizar sua evolução através de gráficos e estatísticas, e manter um histórico de tudo que você já aprendeu.

### Funcionalidades principais:

- **📊 Dashboard Visual**: Acompanhe seu progresso geral com gráficos interativos
- **📝 Gestão de Tarefas**: Crie, edite, conclua e exclua tarefas de estudo
- **🏷️ Categorização**: Organize por temas (Básico, Intermediário, Avançado, Frameworks, Data Science, etc.)
- **📈 Níveis de Dificuldade**: Classifique como Fácil, Médio, Difícil ou Especialista
- **⏱️ Controle de Tempo**: Estime e acompanhe o tempo dedicado a cada tópico
- **🔒 Segurança**: Autenticação protege seus dados de estudo

---

## 🏗️ Arquitetura do Sistema

O sistema segue uma arquitetura de **microserviço** com separação clara entre frontend e backend:

```
┌─────────────────────────────────────────────────────────────┐
│                      CLIENTE (Browser)                      │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  Dashboard HTML/CSS/JS  ──────────────────────┐       │  │
│  │  (Interface visual interativa)               │       │  │
│  │                                              │       │  │
│  │  http://localhost:8080 ─────────────────────┘       │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                            │
                            │ HTTP + JSON
                            │
┌─────────────────────────────────────────────────────────────┐
│                   SPRING BOOT BACKEND                         │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  REST API (/api/tasks)  ←──  TaskController           │  │
│  │                              ↓                        │  │
│  │                         TaskService (regras)        │  │
│  │                              ↓                        │  │
│  │                         TaskRepository (JPA)            │  │
│  │                              ↓                        │  │
│  │                         MySQL Database                │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                              │
│  🔐 Spring Security (HTTP Basic Auth)                         │
│  ✅ Bean Validation (validação de dados)                      │
│  ⚠️ Global Exception Handler                                  │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔌 Como o Backend se Conecta com o Frontend

### 1. **API RESTful**
O backend expõe uma API REST na URL `http://localhost:8080/api/tasks` que o frontend consome via JavaScript fetch API.

**Endpoints disponíveis:**
```
POST   /api/tasks              → Criar nova tarefa
GET    /api/tasks              → Listar todas
GET    /api/tasks/{id}         → Buscar específica
PUT    /api/tasks/{id}         → Atualizar
DELETE /api/tasks/{id}         → Excluir
PATCH  /api/tasks/{id}/concluir→ Marcar como concluída
GET    /api/tasks/status/{bool}→ Filtrar por status
GET    /api/tasks/categoria/{cat}  → Filtrar por categoria
GET    /api/tasks/dificuldade/{dif}→ Filtrar por dificuldade
```

### 2. **Comunicação em Tempo Real**
```javascript
// Exemplo de como o frontend chama o backend
criarNovaTarefa(dados) → fetch POST /api/tasks
                       → Backend valida e salva no MySQL
                       → Retorna JSON com a tarefa criada
                       → Dashboard atualiza automaticamente
```

### 3. **Autenticação Segura**
Todas as requisições incluem credenciais HTTP Basic:
- **Usuário:** `estudante`
- **Senha:** `python2024`

```javascript
headers: {
    'Authorization': 'Basic ' + btoa('estudante:python2024')
}
```

### 4. **Troca de Dados (JSON)**
**Frontend envia:**
```json
{
  "titulo": "Decoradores em Python",
  "descricao": "Aprender @staticmethod, @classmethod",
  "categoria": "Intermediario",
  "dificuldade": "Medio",
  "tempoEstimadoMinutos": 45
}
```

**Backend responde:**
```json
{
  "id": 1,
  "titulo": "Decoradores em Python",
  "concluida": false,
  "dataCriacao": "2026-03-24T20:00:00"
}
```

---

## 🚀 Como Executar

### Pré-requisitos:
- Java 17+
- Maven
- MySQL rodando na porta 3306

### 1. Configurar o Banco de Dados
```sql
-- O Spring cria automaticamente o banco 'python_studies'
-- Ajuste usuário/senha em application.properties se necessário
```

### 2. Iniciar o Backend
```bash
./mvnw spring-boot:run
```

### 3. Acessar o Frontend
Abra no navegador: `http://localhost:8080`

**Login:**
- Usuário: `estudante`
- Senha: ``

---

## 🧪 Executar Testes

```bash
# Testes unitários e de integração
./mvnw test

# Testes com relatório detalhado
./mvnw test -Dtest=TaskServiceTest
./mvnw test -Dtest=TaskControllerIntegrationTest
```

---

## 📁 Estrutura do Projeto

```
engenharia/
├── src/main/java/com/example/engenharia/
│   ├── config/
│   │   └── SecurityConfig.java          # Configuração de segurança
│   ├── controller/
│   │   └── TaskController.java          # API REST endpoints
│   ├── exception/
│   │   └── GlobalExceptionHandler.java  # Tratamento de erros
│   ├── model/
│   │   └── Task.java                    # Entidade JPA com validações
│   ├── repository/
│   │   └── TaskRepository.java          # Acesso ao banco
│   └── service/
│       └── TaskService.java             # Regras de negócio
│
├── src/main/resources/
│   ├── static/                          # Frontend
│   │   ├── index.html                   # Página principal
│   │   ├── css/dashboard.css            # Estilos modernos
│   │   └── js/dashboard.js              # Lógica e chamadas API
│   └── application.properties           # Config MySQL
│
├── src/test/java/com/example/engenharia/
│   ├── service/TaskServiceTest.java     # Testes unitários
│   └── controller/TaskControllerIntegrationTest.java  # Testes integração
│
└── pom.xml                              # Dependências Maven
```

---

## 🔒 Segurança Implementada

| Camada | Implementação |
|--------|---------------|
| Autenticação | Spring Security - HTTP Basic |
| Senhas | BCryptPasswordEncoder |
| Validação | Bean Validation (Jakarta) |
| Proteção CSRF | Desabilitada para API REST |
| CORS | Configurado para frontend |

---

## 📊 Métricas do Dashboard

O frontend calcula automaticamente:
- **Progresso Geral**: Porcentagem de tarefas concluídas
- **Distribuição por Categoria**: Gráfico de barras por tema
- **Distribuição por Dificuldade**: Visualização do nível dos estudos
- **Tempo Total**: Soma de todas as horas estimadas
- **Status**: Contadores de pendentes vs concluídas

---

## 🎯 Próximos Passos Sugeridos

1. Adicionar autenticação JWT para maior segurança
2. Implementar exportação de relatórios PDF
3. Adicionar agendamento de lembretes
4. Criar modo escuro/claro no dashboard
5. Implementar gamificação (badges, streaks)

---

**Desenvolvido para acompanhamento de estudos Python com arquitetura moderna de microserviços.**
