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

### Renato Dias

Responsavel pela implementacao das funcionalidades de autenticacao/controle de acesso e das user stories do perfil Secretario.

#### Login e controle de acesso

- Implementacao da autenticacao com Spring Security.
- Criacao dos perfis de usuario e controle de acesso por role.
- Redirecionamento pos-login conforme o perfil do usuario.
- Telas de login, acesso negado e dashboard inicial.
- Carga inicial de usuarios/perfis para testes.

Commits e PR associados:

- [f12d4ed0c041b59d6628c82521966678b05a9e00](https://github.com/Hakirius/PESCD/commit/f12d4ed0c041b59d6628c82521966678b05a9e00) - Implementando login e autenticacao para os diferentes usuarios.
- [f2014f347272be607d715312acd403d4df9198e3](https://github.com/Hakirius/PESCD/commit/f2014f347272be607d715312acd403d4df9198e3) - Merge da pull request de login e controle de acesso.
- [Pull Request #1](https://github.com/Hakirius/PESCD/pull/1) - `feature/u01-login-controle-acesso`.

#### User stories do Secretario: S1, S2, S3 e S4

- Criacao e listagem de ofertas pelo secretario.
- Definicao do professor responsavel pela oferta.
- Visualizacao de detalhes da oferta, alunos vinculados e status de acompanhamento.
- Associacao, edicao e remocao de alunos em ofertas.
- Importacao de alunos por CSV, com criacao automatica de usuario aluno quando necessario.
- Validacoes de negocio para evitar duplicidade de matricula, RA/e-mail duplicado e edicao de oferta encerrada.
- Registro de historico/status da matricula.
- Encerramento de oferta pelo secretario apos conclusao pelo professor responsavel.
- Exportacao dos resultados finais da oferta em CSV.
- Criacao das telas do modulo Secretario para ofertas e alunos.
- Internacionalizacao das mensagens do fluxo em portugues e ingles.

Commit associado:

- [08f4da10d1f942cdc3bd6843fc8b6102d75bb11c](https://github.com/Hakirius/PESCD/commit/08f4da10d1f942cdc3bd6843fc8b6102d75bb11c) - Implementacao das user stories S1, S2, S3 e S4.

### Pietro Scaglione

Responsável pela implementação das funcionalidades do perfil Aluno, englobando o fluxo completo de estágio e pedidos de dispensa, e pelo módulo de gerenciamento de usuários do perfil Administrador.

#### User stories do Aluno: AL.01, AL.02, AL.03 e AL.04

- **Visualização de Ofertas (AL.01):** Criação de listagem dinâmica de ofertas vinculadas estritamente ao aluno autenticado na sessão, exibindo semestre, datas, professor responsável e status.
- **Envio de Plano de Trabalho (AL.02):** Implementação de formulário para upload de plano de trabalho em PDF (validação de limite de 5MB), com seleção dinâmica de professor supervisor e transição automática do status da matrícula para "Plano Enviado".
- **Dispensa por Docência (AL.03):** Desenvolvimento do fluxo de envio de documentação comprobatória de docência no ensino superior para fins de obtenção de créditos/dispensa, com validação de arquivo e alteração de status para "Documentação Enviada".
- **Relatório Final de Estágio (AL.04):** Implementação da tela de encerramento do estágio contendo área de leitura dos dados anteriores (oferta e plano) e formulário para envio do relatório em PDF junto ao indicador de frequência (0 a 100%), avançando o status para "Relatório Enviado".
- **Internacionalização:** Adaptação de todas as views do módulo Aluno para suporte a múltiplos idiomas (português/inglês) via chaves de mensagens.


#### User stories do Administrador: AD.01

- **Gerenciamento de Usuários:** Criação do CRUD completo (Listar, Criar, Editar e Excluir) para controle de acessos dos perfis Administrador, Secretário e Professor.
- **Validações de Segurança:** Implementação de restrição de unicidade para e-mails e bloqueio lógico para impedir que o administrador logado exclua a própria conta.
- **Correção de Bug Crítico (Exclusão de Usuários):** Resolução de crash do sistema (HTTP 500) ao deletar usuários com vínculos ativos no banco de dados. Ajuste nos mapeamentos de relacionamentos e tratamento de `DataIntegrityViolationException` com feedback amigável via interface.
- **Interface e i18n:** Telas administrativas padronizadas em Bootstrap e totalmente internacionalizadas.

Commits associados:

- `a1281cf5f1cd550ddad59afcd8892155e57eca14` - Implementação AL.01, AL.02, AL.03, AL.04 e AD.01.
- `1b1eb52faef164fb670e908732612e944b10b952` - Hotfix: Correção de integridade referencial e crash na exclusão de usuários.

### Samuel Gerga Martins

Responsável pela implementação das funcionalidades do perfil Professor Supervisor (PS)

Responsável por orquestrar o fluxo de acompanhamento, avaliação e aprovação de alunos em estágio, além da implementação de uma funcionalidade extra de geração de relatórios.

**User stories do Professor Supervisor: PS.01, PS.02, PS.03 **

- **Visualização de Ofertas e Supervisionados (PS.01):** Criação de *dashboard* dinâmico (listagem) vinculando as ofertas aos alunos sob a supervisão do professor autenticado. Implementação de lógica de agrupamento de dados no *back-end* (via DTOs e `Map`) e exibição condicional de botões de ação no *front-end* (Thymeleaf) baseada no status atual da matrícula do aluno.
- **Aprovação de Plano de Trabalho (PS.02):** Desenvolvimento do fluxo de avaliação inicial do estágio. Implementação de tela com carregamento de dados em modo leitura (detalhes do plano e visualização do arquivo PDF) e formulário para captura do parecer do supervisor. Inclui transição atômica de status para "Plano Aprovado", persistência no banco de dados e geração automatizada do *timestamp* da operação.
- **Aprovação de Relatório Final (PS.03):** Construção do formulário de avaliação de encerramento, consolidando dados do plano, relatório final em PDF e histórico de mudanças de status. Desenvolvimento da lógica de aprovação exigindo preenchimento obrigatório de parecer, validação/edição de frequência (0 a 100%) e atribuição de sugestão de nota (A, B, C, D ou E), avançando o status para "Relatório Aprovado pelo Supervisor" com registro no log de auditoria.
- **Segurança e Internacionalização:** Adaptação de todas as *views* do módulo do Supervisor para suporte a múltiplos idiomas (português/inglês) utilizando arquivos de *properties*. Implementação de validações de segurança no *back-end* (Spring Security) para garantir que o professor logado tenha acesso e permissão de aprovação restritos apenas aos alunos sob sua própria supervisão.
Commits associados:
- `feat: Professor Supervisor` : Implementação do painel de listagem de alunos e dos fluxos de aprovação de planos de trabalho e relatórios finais (PS.01, PS.02 e PS.03).
- `feat: EXPORT CSV` : : Implementação da estória surpresa para exportação dos resultados finais dos alunos em formato de arquivo CSV.

### Leonardo Shoji Ishiy

Responsável pela implementação das funcionalidades do perfil Professor Responsável, englobando a aprovação final dos alunos e o fechamento das turmas, além do módulo de acesso público para Visitantes.

#### User stories do Professor Responsável e Visitante: PR.01, PR.02, PR.03, PR.04 e V.01

- **Acesso de Visitante (V.01):** Configuração de rota pública (`/ofertas`) com liberação de acesso no Spring Security, exibindo uma listagem das ofertas ativas ordenadas de forma decrescente pelo semestre para usuários não logados.
- **Acompanhamento de Ofertas (PR.04):** Criação do painel (dashboard) do Professor Responsável, exibindo estritamente as turmas vinculadas ao usuário logado. Implementação de cálculo dinâmico para o status de exibição da oferta (Em andamento, Em atraso, Concluída) e renderização dos alunos inscritos.
- **Conclusão de Relatório de Estágio (PR.01):** Implementação da tela de aprovação final do estágio regular. Integração de lógica no Controller para buscar e pré-carregar automaticamente no formulário a frequência e a nota sugeridas anteriormente pelo Professor Supervisor.
- **Análise de Documentação de Docência (PR.02):** Criação do fluxo de aprovação direta para alunos com pedido de dispensa por docência no ensino superior, permitindo a leitura dos comprovantes e a inserção do parecer, nota e frequência finais, alterando o status da matrícula para "Concluído pelo responsável".
- **Encerramento de Oferta e Estatísticas (PR.03):** Desenvolvimento do algoritmo de fechamento de turma. Implementação de validações na camada de serviço (via `ValidacaoNegocioException`) para garantir que a oferta só possa ser encerrada se 100% dos alunos estiverem avaliados.
- **Segurança e Renderização Dinâmica:** Resolução de restrições de segurança do Thymeleaf (bloqueio de expressões SpEL em classes utilitárias como `DoubleSummaryStatistics`) centralizando o cálculo de médias e estatísticas da turma no back-end. Uso avançado de diretivas `th:if` na interface para ocultar botões indevidos (ex: esconder a opção de encerramento de uma oferta que já foi finalizada).

Commits e PR associados:

Commits e PR associados:

- [05caecbeaf1a9b3a30c889ab645e6e475ea776ca](https://github.com/Hakirius/PESCD/commit/05caecbeaf1a9b3a30c889ab645e6e475ea776ca) - Implementadas as User Stories de Professor Responsável (PR.01 a PR.04) e Visitante (V.01).

### Grupo
- **Exportação de Resultados em CSV (Estória Surpresa):** Criação de funcionalidade para extração dos dados consolidados do estágio. Implementação de *endpoint* HTTP com manipulação de cabeçalhos (`Content-Disposition: attachment`) para gerar e acionar o download automático do arquivo `.csv` na máquina do usuário.



