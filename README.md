# PESCD

Sistema para gerenciamento do Programa de Estagio Supervisionado de Capacitacao Docente.

## Como executar

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

No Linux/macOS:

```bash
./mvnw spring-boot:run
```

Acesse:

```text
http://localhost:8080
```

## Como fazer build e testar

No Windows:

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd clean package
```

No Linux/macOS:

```bash
./mvnw clean test
./mvnw clean package
```

O artefato gerado fica em `target/PESCD-0.0.1-SNAPSHOT.jar`.

Para executar o JAR:

```powershell
java -jar target/PESCD-0.0.1-SNAPSHOT.jar
```

## Usuarios de demonstracao

Ao iniciar a aplicacao com o banco vazio, `PescdApplication` cria dados iniciais para demonstracao.

| Perfil | Usuario | Senha |
| --- | --- | --- |
| Administrador | `admin` | `admin` |
| Secretario | `secretario` | `secretario` |
| Professor Responsavel | `responsavel` | `responsavel` |
| Professor Supervisor | `supervisor` | `supervisor` |
| Aluno | `aluno.estagio` | `123456` |
| Aluno | `aluno.documentacao` | `654321` |

## Contribuicoes por integrante

# Distribuição de Responsabilidades da Equipe

## Renato Dias

Responsável pela implementação das funcionalidades de autenticação, controle de acesso e das User Stories do perfil **Secretário**.

### Login e Controle de Acesso

- Implementação da autenticação **Stateless** com Spring Security e geração de Tokens JWT.
- Criação dos perfis de usuário e controle de acesso por atributos e anotações `@PreAuthorize("hasRole(...)")`.
- Resposta padronizada em formato JSON para sucesso ou falha na autenticação via Handlers customizados.
- Implementação dos endpoints REST:
  - `/api/auth/login`
  - `/api/auth/me`
  - `/api/auth/logout`
- Carga inicial de usuários e perfis para testes.

#### Commits e Pull Request

- `f12d4ed0c041b59d6628c82521966678b05a9e00` — Implementando login e autenticação para os diferentes usuários.
- `f2014f347272be607d715312acd403d4df9198e3` — Merge da pull request de login e controle de acesso.
- **Pull Request #1** — `feature/u01-login-controle-acesso`.

---

### User Stories do Secretário (S1, S2, S3 e S4)

- Criação e listagem de ofertas através de endpoints REST utilizando os verbos **POST** e **GET**.
- Definição do professor responsável pela oferta via payload JSON.
- Visualização dos detalhes da oferta, alunos vinculados e status utilizando DTOs.
- Associação, edição e remoção de alunos nas ofertas utilizando **POST**, **PUT** e **DELETE**.
- Importação de alunos por upload de arquivo CSV (`multipart/form-data`), criando automaticamente usuários do tipo aluno quando necessário.
- Implementação de validações para:
  - impedir duplicidade de matrícula;
  - impedir RA duplicado;
  - impedir e-mail duplicado;
  - impedir edição de ofertas encerradas.
- Tratamento das exceções através de `RestControllerAdvice`.
- Registro de histórico e status das matrículas.
- Encerramento de ofertas pelo secretário após conclusão pelo professor responsável.
- Exportação dos resultados finais em formato CSV.
- Estruturação dos controladores REST do módulo Secretário.
- Internacionalização das mensagens de erro e validação (Português e Inglês).

#### Commit

- `08f4da10d1f942cdc3bd6843fc8b6102d75bb11c` — Implementação das User Stories S1, S2, S3 e S4.

---

# Pietro Scaglione

Responsável pela implementação das funcionalidades do perfil **Aluno**, abrangendo todo o fluxo de estágio e pedidos de dispensa, além do módulo de gerenciamento de usuários do perfil **Administrador**.

## User Stories do Aluno (AL.01, AL.02, AL.03 e AL.04)

### AL.01 — Visualização de Ofertas

- Endpoint para listagem dinâmica das ofertas vinculadas exclusivamente ao aluno autenticado via Token JWT (`@AuthenticationPrincipal`).
- Retorno estruturado em JSON.

### AL.02 — Envio de Plano de Trabalho

- Endpoint `multipart/form-data` para upload do Plano de Trabalho em PDF.
- Validação de tamanho máximo de **5 MB**.
- Recepção conjunta de arquivo e parâmetros textuais.
- Alteração automática do status da matrícula para **Plano Enviado**.

### AL.03 — Dispensa por Docência

- Implementação do fluxo REST para envio da documentação comprobatória em PDF.
- Validação do tipo de mídia.
- Atualização automática do status para **Documentação Enviada**.

### AL.04 — Relatório Final de Estágio

- Endpoint para envio do relatório final em PDF.
- Validação do arquivo.
- Recepção da frequência do aluno.
- Atualização automática do status para **Relatório Enviado**.

### Internacionalização

- Adaptação das respostas de erro do módulo Aluno para múltiplos idiomas.

---

## User Story do Administrador (AD.01)

### Gerenciamento de Usuários

- Desenvolvimento de um CRUD totalmente RESTful:
  - GET
  - POST
  - PUT
  - DELETE
- Comunicação utilizando `application/json`.

### Validações de Segurança

- Restrição de e-mails duplicados.
- Impedimento de exclusão do próprio administrador autenticado.

### Correção de Bug Crítico

- Correção da exclusão de usuários com vínculos ativos.
- Tratamento de `DataIntegrityViolationException`.
- Retorno de **HTTP 409 Conflict** com mensagem amigável em JSON.

### Interface e Internacionalização

- Padronização das respostas JSON.
- Internacionalização completa do módulo administrativo.

#### Commits

- `a1281cf5f1cd550ddad59afcd8892155e57eca14` — Implementação AL.01, AL.02, AL.03, AL.04 e AD.01.
- `1b1eb52faef164fb670e908732612e944b10b952` — Hotfix de integridade referencial na exclusão de usuários.

---

# Samuel Gerga Martins

Responsável pela implementação das funcionalidades do perfil **Professor Supervisor**, incluindo o fluxo completo de acompanhamento e avaliação dos alunos.

## User Stories (PS.01, PS.02 e PS.03)

### PS.01 — Visualização de Ofertas e Supervisionados

- Desenvolvimento do controlador REST:
  - `/api/supervisor/supervisao`
- Listagem dinâmica das ofertas e alunos supervisionados.
- Utilização de DTOs aninhados.
- Associação automática ao professor autenticado via Token JWT.

### PS.02 — Aprovação do Plano de Trabalho

- Endpoint:
  - `POST .../aprovar-plano`
- Consulta em modo leitura via GET.
- Recepção do parecer em JSON.
- Atualização automática do status para **Plano Aprovado**.
- Registro automático do timestamp.

### PS.03 — Aprovação do Relatório Final

- Endpoint:
  - `POST .../aprovar-relatorio`
- Validação obrigatória do parecer.
- Validação da frequência entre **0 e 100%**.
- Validação da nota baseada no Enum `Nota`.
- Registro em histórico de auditoria.

### Segurança e Internacionalização

- Internacionalização das mensagens do módulo Supervisor.
- Garantia de que cada supervisor possa acessar apenas alunos sob sua responsabilidade.

#### Commits

- `feat: Professor Supervisor` — Implementação do painel de listagem e dos fluxos PS.01, PS.02 e PS.03.
- `feat: EXPORT CSV` — Implementação da funcionalidade extra de exportação dos resultados em CSV.

---

# Leonardo Shoji Ishiy

Responsável pela implementação das funcionalidades do perfil **Professor Responsável** e do módulo público de **Visitantes**.

## User Stories (PR.01, PR.02, PR.03, PR.04 e V.01)

### V.01 — Acesso de Visitante

- Configuração da rota pública:
  - `/api/ofertas`
- Liberação explícita na configuração do Spring Security.
- Listagem das ofertas ativas ordenadas por semestre.
- Resposta em JSON.

### PR.04 — Acompanhamento de Ofertas

- Desenvolvimento dos endpoints para acompanhamento das turmas.
- Retorno apenas das ofertas vinculadas ao professor autenticado.
- Cálculo dinâmico do status:
  - Em andamento
  - Em atraso
  - Concluída
- Serialização dos alunos vinculados.

### PR.01 — Conclusão do Relatório de Estágio

- Aprovação final do estágio.
- Reaproveitamento automático da frequência e nota atribuídas pelo Professor Supervisor.

### PR.02 — Análise da Documentação de Docência

- Homologação dos pedidos de dispensa.
- Recepção de parecer técnico.
- Registro da nota final.
- Consolidação da carga horária.

### PR.03 — Encerramento da Oferta

- Implementação do algoritmo de encerramento da turma.
- Validação para impedir encerramento enquanto existirem alunos pendentes.

### Segurança e Renderização Dinâmica

- Centralização do cálculo de médias e estatísticas no back-end.
- Eliminação de regras anteriormente executadas no front-end.
- Inclusão de campos booleanos e estados que orientam a interface do cliente quanto às ações disponíveis.

### Grupo
- **Exportação de Resultados em CSV (Estória Surpresa):** Criação de funcionalidade para extração dos dados consolidados do estágio. Implementação de *endpoint* HTTP com manipulação de cabeçalhos (`Content-Disposition: attachment`) para gerar e acionar o download automático do arquivo `.csv` na máquina do usuário.



