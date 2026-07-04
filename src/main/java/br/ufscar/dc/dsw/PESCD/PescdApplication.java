package br.ufscar.dc.dsw.PESCD;

import br.ufscar.dc.dsw.PESCD.models.AlunoOfertaModel;
import br.ufscar.dc.dsw.PESCD.models.AprovacaoPlanoModel;
import br.ufscar.dc.dsw.PESCD.models.AprovacaoRelatorioSupervisorModel;
import br.ufscar.dc.dsw.PESCD.models.ConfiguracaoSistemaModel;
import br.ufscar.dc.dsw.PESCD.models.DocumentacaoDocenciaModel;
import br.ufscar.dc.dsw.PESCD.models.LogStatusAlunoOfertaModel;
import br.ufscar.dc.dsw.PESCD.models.Nota;
import br.ufscar.dc.dsw.PESCD.models.OfertaModel;
import br.ufscar.dc.dsw.PESCD.models.PerfilModel;
import br.ufscar.dc.dsw.PESCD.models.PerfilUsuario;
import br.ufscar.dc.dsw.PESCD.models.PlanoTrabalhoModel;
import br.ufscar.dc.dsw.PESCD.models.RelatorioFinalModel;
import br.ufscar.dc.dsw.PESCD.models.StatusAlunoOferta;
import br.ufscar.dc.dsw.PESCD.models.StatusOferta;
import br.ufscar.dc.dsw.PESCD.models.TipoCredito;
import br.ufscar.dc.dsw.PESCD.models.UsuarioModel;
import br.ufscar.dc.dsw.PESCD.repositories.AlunoOfertaRepository;
import br.ufscar.dc.dsw.PESCD.repositories.AprovacaoPlanoRepository;
import br.ufscar.dc.dsw.PESCD.repositories.AprovacaoRelatorioSupervisorRepository;
import br.ufscar.dc.dsw.PESCD.repositories.ConfiguracaoSistemaRepository;
import br.ufscar.dc.dsw.PESCD.repositories.DocumentacaoDocenciaRepository;
import br.ufscar.dc.dsw.PESCD.repositories.LogStatusAlunoOfertaRepository;
import br.ufscar.dc.dsw.PESCD.repositories.OfertaRepository;
import br.ufscar.dc.dsw.PESCD.repositories.PerfilRepository;
import br.ufscar.dc.dsw.PESCD.repositories.PlanoTrabalhoRepository;
import br.ufscar.dc.dsw.PESCD.repositories.RelatorioFinalRepository;
import br.ufscar.dc.dsw.PESCD.repositories.UsuarioRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class PescdApplication {

	public static void main(String[] args) {
		var context = SpringApplication.run(PescdApplication.class, args);

		var usuarioRepository = context.getBean(UsuarioRepository.class);
		var ofertaRepository = context.getBean(OfertaRepository.class);
		var alunoOfertaRepository = context.getBean(AlunoOfertaRepository.class);
		var planoTrabalhoRepository = context.getBean(PlanoTrabalhoRepository.class);
		var documentacaoDocenciaRepository = context.getBean(DocumentacaoDocenciaRepository.class);
		var relatorioFinalRepository = context.getBean(RelatorioFinalRepository.class);
		var aprovacaoPlanoRepository = context.getBean(AprovacaoPlanoRepository.class);
		var aprovacaoRelatorioSupervisorRepository = context.getBean(AprovacaoRelatorioSupervisorRepository.class);
		var logStatusRepository = context.getBean(LogStatusAlunoOfertaRepository.class);
		var configuracaoSistemaRepository = context.getBean(ConfiguracaoSistemaRepository.class);
		var perfilRepository = context.getBean(PerfilRepository.class);
		var passwordEncoder = context.getBean(PasswordEncoder.class);

		var perfilAdmin = obterOuCriarPerfil(perfilRepository, PerfilUsuario.ROLE_ADMIN);
		var perfilSecretario = obterOuCriarPerfil(perfilRepository, PerfilUsuario.ROLE_SECRETARIO);
		var perfilAluno = obterOuCriarPerfil(perfilRepository, PerfilUsuario.ROLE_ALUNO);
		var perfilSupervisor = obterOuCriarPerfil(perfilRepository, PerfilUsuario.ROLE_SUPERVISOR);
		var perfilResponsavel = obterOuCriarPerfil(perfilRepository, PerfilUsuario.ROLE_RESPONSAVEL);

		if (usuarioRepository.count() == 0) {
			var administrador = criarUsuario("Administrador PESCD", "admin@ufscar.br", null, "admin", "admin", passwordEncoder, perfilAdmin);
			var secretario = criarUsuario("Secretario PESCD", "secretario@ufscar.br", null, "secretario", "secretario", passwordEncoder, perfilSecretario);
			var professorResponsavel = criarUsuario("Professor Responsavel", "responsavel@ufscar.br", null, "responsavel", "responsavel", passwordEncoder, perfilResponsavel);
			var professorSupervisor = criarUsuario("Professor Supervisor", "supervisor@ufscar.br", null, "supervisor", "supervisor", passwordEncoder, perfilSupervisor);
			var alunoEstagio = criarUsuario("Aluno Estagio", "aluno.estagio@ufscar.br", "123456", "aluno.estagio", "123456", passwordEncoder, perfilAluno);
			var alunoDocumentacao = criarUsuario("Aluno Documentacao", "aluno.documentacao@ufscar.br", "654321", "aluno.documentacao", "654321", passwordEncoder, perfilAluno);

			usuarioRepository.save(administrador);
			usuarioRepository.save(secretario);
			usuarioRepository.save(professorResponsavel);
			usuarioRepository.save(professorSupervisor);
			usuarioRepository.save(alunoEstagio);
			usuarioRepository.save(alunoDocumentacao);

			var oferta = new OfertaModel();
			oferta.setNome("PESCD 2026/1");
			oferta.setSemestre("2026/1");
			oferta.setDataInicio(LocalDate.of(2026, 3, 1));
			oferta.setDataFim(LocalDate.of(2026, 7, 15));
			oferta.setStatus(StatusOferta.EM_ANDAMENTO);
			oferta.setProfessorResponsavel(professorResponsavel);
			oferta.setCriadoPor(secretario);
			oferta.setCriadoEm(LocalDateTime.now());
			ofertaRepository.save(oferta);

			var ofertaAguardando = new OfertaModel();
			ofertaAguardando.setNome("PESCD 2025/1");
			ofertaAguardando.setSemestre("2025/1");
			ofertaAguardando.setDataInicio(LocalDate.of(2025, 3, 1));
			ofertaAguardando.setDataFim(LocalDate.of(2025, 7, 15));
			ofertaAguardando.setStatus(StatusOferta.AGUARDANDO_ENCERRAMENTO_SECRETARIO);
			ofertaAguardando.setProfessorResponsavel(professorResponsavel);
			ofertaAguardando.setCriadoPor(secretario);
			ofertaAguardando.setCriadoEm(LocalDateTime.now().minusMonths(12));
			ofertaAguardando.setEncerradoResponsavelEm(LocalDateTime.now().minusDays(5));
			ofertaRepository.save(ofertaAguardando);

			var matriculaEstagio = criarMatricula(alunoEstagio, oferta, StatusAlunoOferta.PLANO_ENVIADO, TipoCredito.ESTAGIO);
			alunoOfertaRepository.save(matriculaEstagio);

			var plano = new PlanoTrabalhoModel();
			plano.setAlunoOferta(matriculaEstagio);
			plano.setCodigoDisciplina("1001497");
			plano.setNomeDisciplina("Desenvolvimento de Software para Web 1");
			plano.setCursoDisciplina("Ciencia da Computacao");
			plano.setProfessorSupervisor(professorSupervisor);
			plano.setArquivoPlano("uploads/planos/plano-aluno-estagio.pdf");
			plano.setEnviadoEm(LocalDateTime.now());
			planoTrabalhoRepository.save(plano);

			var matriculaDocumentacao = criarMatricula(alunoDocumentacao, oferta, StatusAlunoOferta.DOCUMENTACAO_ENVIADA, TipoCredito.DOCUMENTACAO);
			alunoOfertaRepository.save(matriculaDocumentacao);

			var documentacao = new DocumentacaoDocenciaModel();
			documentacao.setAlunoOferta(matriculaDocumentacao);
			documentacao.setNomeInstituicao("Universidade Federal de Sao Carlos");
			documentacao.setNomeDisciplina("Programacao Orientada a Objetos");
			documentacao.setCursoDisciplina("Engenharia de Computacao");
			documentacao.setCargaHoraria(60);
			documentacao.setArquivoDocumentacao("uploads/documentos/documentacao-aluno.pdf");
			documentacao.setEnviadoEm(LocalDateTime.now());
			documentacaoDocenciaRepository.save(documentacao);

			logStatusRepository.save(criarLog(matriculaEstagio, StatusAlunoOferta.NAO_ENVIADO, StatusAlunoOferta.PLANO_ENVIADO, alunoEstagio, "Plano enviado pelo aluno."));
			logStatusRepository.save(criarLog(matriculaDocumentacao, StatusAlunoOferta.NAO_ENVIADO, StatusAlunoOferta.DOCUMENTACAO_ENVIADA, alunoDocumentacao, "Documentacao enviada pelo aluno."));

			if (configuracaoSistemaRepository.findByChave("instrucoes_encerramento_secretario").isEmpty()) {
				var configuracao = new ConfiguracaoSistemaModel();
				configuracao.setChave("instrucoes_encerramento_secretario");
				configuracao.setValor("""
						Conferir notas e frequencias finais de todos os alunos.
						Verificar se todos os planos e relatorios foram aprovados.
						Validar documentacao de docencia enviada.
						Registrar licoes aprendidas antes de encerrar definitivamente.
						""");
				configuracaoSistemaRepository.save(configuracao);
			}
		}

		criarDadosComplementaresTeste(
				usuarioRepository,
				ofertaRepository,
				alunoOfertaRepository,
				planoTrabalhoRepository,
				documentacaoDocenciaRepository,
				relatorioFinalRepository,
				aprovacaoPlanoRepository,
				aprovacaoRelatorioSupervisorRepository,
				logStatusRepository,
				passwordEncoder,
				perfilAdmin,
				perfilSecretario,
				perfilAluno,
				perfilSupervisor,
				perfilResponsavel);
	}

	private static void criarDadosComplementaresTeste(
			UsuarioRepository usuarioRepository,
			OfertaRepository ofertaRepository,
			AlunoOfertaRepository alunoOfertaRepository,
			PlanoTrabalhoRepository planoTrabalhoRepository,
			DocumentacaoDocenciaRepository documentacaoDocenciaRepository,
			RelatorioFinalRepository relatorioFinalRepository,
			AprovacaoPlanoRepository aprovacaoPlanoRepository,
			AprovacaoRelatorioSupervisorRepository aprovacaoRelatorioSupervisorRepository,
			LogStatusAlunoOfertaRepository logStatusRepository,
			PasswordEncoder passwordEncoder,
			PerfilModel perfilAdmin,
			PerfilModel perfilSecretario,
			PerfilModel perfilAluno,
			PerfilModel perfilSupervisor,
			PerfilModel perfilResponsavel) {

		var admins = criarUsuariosTeste(usuarioRepository, passwordEncoder, perfilAdmin, "admin.teste", "Administrador Teste", "admin.teste", null);
		var secretarios = criarUsuariosTeste(usuarioRepository, passwordEncoder, perfilSecretario, "secretario.teste", "Secretario Teste", "secretario.teste", null);
		var alunos = criarUsuariosTeste(usuarioRepository, passwordEncoder, perfilAluno, "aluno.teste", "Aluno Teste", "aluno.teste", "20260");
		var supervisores = criarUsuariosTeste(usuarioRepository, passwordEncoder, perfilSupervisor, "supervisor.teste", "Professor Supervisor Teste", "supervisor.teste", null);
		var responsaveis = criarUsuariosTeste(usuarioRepository, passwordEncoder, perfilResponsavel, "responsavel.teste", "Professor Responsavel Teste", "responsavel.teste", null);

		var secretario = secretarios.get(0);
		var responsavel = responsaveis.get(0);
		var supervisor = supervisores.get(0);
		var secretarioPrincipal = obterOuCriarUsuario(
				usuarioRepository,
				passwordEncoder,
				"Secretario PESCD",
				"secretario@ufscar.br",
				null,
				"secretario",
				"secretario",
				perfilSecretario);
		var responsavelPrincipal = obterOuCriarUsuario(
				usuarioRepository,
				passwordEncoder,
				"Professor Responsavel",
				"responsavel@ufscar.br",
				null,
				"responsavel",
				"responsavel",
				perfilResponsavel);
		var supervisorPrincipal = obterOuCriarUsuario(
				usuarioRepository,
				passwordEncoder,
				"Professor Supervisor",
				"supervisor@ufscar.br",
				null,
				"supervisor",
				"supervisor",
				perfilSupervisor);

		var ofertaPrincipal = obterOuCriarOfertaTeste(
				ofertaRepository,
				"PESCD 2026/1",
				"2026/1",
				LocalDate.of(2026, 3, 1),
				LocalDate.of(2026, 7, 15),
				StatusOferta.EM_ANDAMENTO,
				responsavelPrincipal,
				secretarioPrincipal);
		criarCenariosProfessorResponsavelNaOfertaPrincipal(
				usuarioRepository,
				alunoOfertaRepository,
				planoTrabalhoRepository,
				documentacaoDocenciaRepository,
				relatorioFinalRepository,
				aprovacaoPlanoRepository,
				aprovacaoRelatorioSupervisorRepository,
				logStatusRepository,
				passwordEncoder,
				perfilAluno,
				ofertaPrincipal,
				supervisorPrincipal);

		var ofertaEtapas = obterOuCriarOfertaTeste(
				ofertaRepository,
				"PESCD Testes Etapas 2026/2",
				"2026/2",
				LocalDate.of(2026, 8, 1),
				LocalDate.of(2026, 12, 15),
				StatusOferta.EM_ANDAMENTO,
				responsavel,
				secretario);

		criarMatriculaComArtefatos(
				alunoOfertaRepository,
				planoTrabalhoRepository,
				documentacaoDocenciaRepository,
				relatorioFinalRepository,
				aprovacaoPlanoRepository,
				aprovacaoRelatorioSupervisorRepository,
				logStatusRepository,
				alunos.get(0),
				ofertaEtapas,
				StatusAlunoOferta.NAO_ENVIADO,
				TipoCredito.ESTAGIO,
				supervisor);
		criarMatriculaComArtefatos(alunoOfertaRepository, planoTrabalhoRepository, documentacaoDocenciaRepository, relatorioFinalRepository, aprovacaoPlanoRepository, aprovacaoRelatorioSupervisorRepository, logStatusRepository, alunos.get(1), ofertaEtapas, StatusAlunoOferta.PLANO_ENVIADO, TipoCredito.ESTAGIO, supervisor);
		criarMatriculaComArtefatos(alunoOfertaRepository, planoTrabalhoRepository, documentacaoDocenciaRepository, relatorioFinalRepository, aprovacaoPlanoRepository, aprovacaoRelatorioSupervisorRepository, logStatusRepository, alunos.get(2), ofertaEtapas, StatusAlunoOferta.PLANO_APROVADO, TipoCredito.ESTAGIO, supervisor);
		criarMatriculaComArtefatos(alunoOfertaRepository, planoTrabalhoRepository, documentacaoDocenciaRepository, relatorioFinalRepository, aprovacaoPlanoRepository, aprovacaoRelatorioSupervisorRepository, logStatusRepository, alunos.get(3), ofertaEtapas, StatusAlunoOferta.DOCUMENTACAO_ENVIADA, TipoCredito.DOCUMENTACAO, supervisor);
		criarMatriculaComArtefatos(alunoOfertaRepository, planoTrabalhoRepository, documentacaoDocenciaRepository, relatorioFinalRepository, aprovacaoPlanoRepository, aprovacaoRelatorioSupervisorRepository, logStatusRepository, alunos.get(4), ofertaEtapas, StatusAlunoOferta.RELATORIO_ENVIADO, TipoCredito.ESTAGIO, supervisor);

		var alunoRelatorioAprovado = obterOuCriarUsuario(
				usuarioRepository,
				passwordEncoder,
				"Aluno Relatorio Aprovado",
				"aluno.relatorio.aprovado@teste.ufscar.br",
				"202606",
				"aluno.relatorio.aprovado",
				"123456",
				perfilAluno);
		var alunoConcluido = obterOuCriarUsuario(
				usuarioRepository,
				passwordEncoder,
				"Aluno Concluido Responsavel",
				"aluno.concluido.responsavel@teste.ufscar.br",
				"202607",
				"aluno.concluido.responsavel",
				"123456",
				perfilAluno);
		criarMatriculaComArtefatos(alunoOfertaRepository, planoTrabalhoRepository, documentacaoDocenciaRepository, relatorioFinalRepository, aprovacaoPlanoRepository, aprovacaoRelatorioSupervisorRepository, logStatusRepository, alunoRelatorioAprovado, ofertaEtapas, StatusAlunoOferta.RELATORIO_APROVADO_PELO_SUPERVISOR, TipoCredito.ESTAGIO, supervisor);
		criarMatriculaComArtefatos(alunoOfertaRepository, planoTrabalhoRepository, documentacaoDocenciaRepository, relatorioFinalRepository, aprovacaoPlanoRepository, aprovacaoRelatorioSupervisorRepository, logStatusRepository, alunoConcluido, ofertaEtapas, StatusAlunoOferta.CONCLUIDO_PELO_RESPONSAVEL, TipoCredito.ESTAGIO, supervisor);

		var ofertaProntaEncerrar = obterOuCriarOfertaTeste(
				ofertaRepository,
				"PESCD Testes Pronta Para Encerrar 2026/1",
				"2026/1",
				LocalDate.of(2026, 3, 1),
				LocalDate.of(2026, 7, 15),
				StatusOferta.EM_ANDAMENTO,
				responsavel,
				secretario);
		for (int i = 1; i <= 3; i++) {
			var aluno = obterOuCriarUsuario(
					usuarioRepository,
					passwordEncoder,
					"Aluno Oferta Encerravel " + i,
					"aluno.encerravel." + i + "@teste.ufscar.br",
					"20261" + i,
					"aluno.encerravel." + i,
					"123456",
					perfilAluno);
			criarMatriculaComArtefatos(alunoOfertaRepository, planoTrabalhoRepository, documentacaoDocenciaRepository, relatorioFinalRepository, aprovacaoPlanoRepository, aprovacaoRelatorioSupervisorRepository, logStatusRepository, aluno, ofertaProntaEncerrar, StatusAlunoOferta.CONCLUIDO_PELO_RESPONSAVEL, TipoCredito.ESTAGIO, supervisor);
		}
	}

	private static void criarCenariosProfessorResponsavelNaOfertaPrincipal(
			UsuarioRepository usuarioRepository,
			AlunoOfertaRepository alunoOfertaRepository,
			PlanoTrabalhoRepository planoTrabalhoRepository,
			DocumentacaoDocenciaRepository documentacaoDocenciaRepository,
			RelatorioFinalRepository relatorioFinalRepository,
			AprovacaoPlanoRepository aprovacaoPlanoRepository,
			AprovacaoRelatorioSupervisorRepository aprovacaoRelatorioSupervisorRepository,
			LogStatusAlunoOfertaRepository logStatusRepository,
			PasswordEncoder passwordEncoder,
			PerfilModel perfilAluno,
			OfertaModel oferta,
			UsuarioModel supervisor) {
		criarAlunoCenarioProfessorResponsavel(
				usuarioRepository,
				alunoOfertaRepository,
				planoTrabalhoRepository,
				documentacaoDocenciaRepository,
				relatorioFinalRepository,
				aprovacaoPlanoRepository,
				aprovacaoRelatorioSupervisorRepository,
				logStatusRepository,
				passwordEncoder,
				perfilAluno,
				"Aluno PR04 Nao Enviado",
				"aluno.pr04.nao.enviado",
				"2026201",
				oferta,
				StatusAlunoOferta.NAO_ENVIADO,
				TipoCredito.ESTAGIO,
				supervisor);
		criarAlunoCenarioProfessorResponsavel(usuarioRepository, alunoOfertaRepository, planoTrabalhoRepository, documentacaoDocenciaRepository, relatorioFinalRepository, aprovacaoPlanoRepository, aprovacaoRelatorioSupervisorRepository, logStatusRepository, passwordEncoder, perfilAluno, "Aluno PR04 Plano Enviado", "aluno.pr04.plano.enviado", "2026202", oferta, StatusAlunoOferta.PLANO_ENVIADO, TipoCredito.ESTAGIO, supervisor);
		criarAlunoCenarioProfessorResponsavel(usuarioRepository, alunoOfertaRepository, planoTrabalhoRepository, documentacaoDocenciaRepository, relatorioFinalRepository, aprovacaoPlanoRepository, aprovacaoRelatorioSupervisorRepository, logStatusRepository, passwordEncoder, perfilAluno, "Aluno PR04 Plano Aprovado", "aluno.pr04.plano.aprovado", "2026203", oferta, StatusAlunoOferta.PLANO_APROVADO, TipoCredito.ESTAGIO, supervisor);
		criarAlunoCenarioProfessorResponsavel(usuarioRepository, alunoOfertaRepository, planoTrabalhoRepository, documentacaoDocenciaRepository, relatorioFinalRepository, aprovacaoPlanoRepository, aprovacaoRelatorioSupervisorRepository, logStatusRepository, passwordEncoder, perfilAluno, "Aluno PR02 Documentacao Enviada", "aluno.pr02.documentacao.enviada", "2026204", oferta, StatusAlunoOferta.DOCUMENTACAO_ENVIADA, TipoCredito.DOCUMENTACAO, supervisor);
		criarAlunoCenarioProfessorResponsavel(usuarioRepository, alunoOfertaRepository, planoTrabalhoRepository, documentacaoDocenciaRepository, relatorioFinalRepository, aprovacaoPlanoRepository, aprovacaoRelatorioSupervisorRepository, logStatusRepository, passwordEncoder, perfilAluno, "Aluno PR04 Relatorio Enviado", "aluno.pr04.relatorio.enviado", "2026205", oferta, StatusAlunoOferta.RELATORIO_ENVIADO, TipoCredito.ESTAGIO, supervisor);
		criarAlunoCenarioProfessorResponsavel(usuarioRepository, alunoOfertaRepository, planoTrabalhoRepository, documentacaoDocenciaRepository, relatorioFinalRepository, aprovacaoPlanoRepository, aprovacaoRelatorioSupervisorRepository, logStatusRepository, passwordEncoder, perfilAluno, "Aluno PR01 Relatorio Aprovado", "aluno.pr01.relatorio.aprovado", "2026206", oferta, StatusAlunoOferta.RELATORIO_APROVADO_PELO_SUPERVISOR, TipoCredito.ESTAGIO, supervisor);
		criarAlunoCenarioProfessorResponsavel(usuarioRepository, alunoOfertaRepository, planoTrabalhoRepository, documentacaoDocenciaRepository, relatorioFinalRepository, aprovacaoPlanoRepository, aprovacaoRelatorioSupervisorRepository, logStatusRepository, passwordEncoder, perfilAluno, "Aluno PR04 Concluido Estagio", "aluno.pr04.concluido.estagio", "2026207", oferta, StatusAlunoOferta.CONCLUIDO_PELO_RESPONSAVEL, TipoCredito.ESTAGIO, supervisor);
		criarAlunoCenarioProfessorResponsavel(usuarioRepository, alunoOfertaRepository, planoTrabalhoRepository, documentacaoDocenciaRepository, relatorioFinalRepository, aprovacaoPlanoRepository, aprovacaoRelatorioSupervisorRepository, logStatusRepository, passwordEncoder, perfilAluno, "Aluno PR04 Concluido Documentacao", "aluno.pr04.concluido.documentacao", "2026208", oferta, StatusAlunoOferta.CONCLUIDO_PELO_RESPONSAVEL, TipoCredito.DOCUMENTACAO, supervisor);
	}

	private static void criarAlunoCenarioProfessorResponsavel(
			UsuarioRepository usuarioRepository,
			AlunoOfertaRepository alunoOfertaRepository,
			PlanoTrabalhoRepository planoTrabalhoRepository,
			DocumentacaoDocenciaRepository documentacaoDocenciaRepository,
			RelatorioFinalRepository relatorioFinalRepository,
			AprovacaoPlanoRepository aprovacaoPlanoRepository,
			AprovacaoRelatorioSupervisorRepository aprovacaoRelatorioSupervisorRepository,
			LogStatusAlunoOfertaRepository logStatusRepository,
			PasswordEncoder passwordEncoder,
			PerfilModel perfilAluno,
			String nome,
			String username,
			String ra,
			OfertaModel oferta,
			StatusAlunoOferta status,
			TipoCredito tipoCredito,
			UsuarioModel supervisor) {
		var aluno = obterOuCriarUsuario(
				usuarioRepository,
				passwordEncoder,
				nome,
				username + "@teste.ufscar.br",
				ra,
				username,
				"123456",
				perfilAluno);
		criarMatriculaComArtefatos(
				alunoOfertaRepository,
				planoTrabalhoRepository,
				documentacaoDocenciaRepository,
				relatorioFinalRepository,
				aprovacaoPlanoRepository,
				aprovacaoRelatorioSupervisorRepository,
				logStatusRepository,
				aluno,
				oferta,
				status,
				tipoCredito,
				supervisor);
	}

	private static List<UsuarioModel> criarUsuariosTeste(
			UsuarioRepository usuarioRepository,
			PasswordEncoder passwordEncoder,
			PerfilModel perfil,
			String usernamePrefix,
			String nomePrefix,
			String emailPrefix,
			String raPrefix) {
		var usuarios = new ArrayList<UsuarioModel>();
		for (int i = 1; i <= 5; i++) {
			var ra = raPrefix == null ? null : raPrefix + i;
			usuarios.add(obterOuCriarUsuario(
					usuarioRepository,
					passwordEncoder,
					nomePrefix + " " + i,
					emailPrefix + "." + i + "@teste.ufscar.br",
					ra,
					usernamePrefix + "." + i,
					"123456",
					perfil));
		}
		return usuarios;
	}

	private static UsuarioModel obterOuCriarUsuario(
			UsuarioRepository usuarioRepository,
			PasswordEncoder passwordEncoder,
			String nomeCompleto,
			String email,
			String ra,
			String username,
			String password,
			PerfilModel perfil) {
		return usuarioRepository.findByUsername(username)
				.orElseGet(() -> usuarioRepository.save(criarUsuario(
						nomeCompleto,
						email,
						ra,
						username,
						password,
						passwordEncoder,
						perfil)));
	}

	private static OfertaModel obterOuCriarOfertaTeste(
			OfertaRepository ofertaRepository,
			String nome,
			String semestre,
			LocalDate dataInicio,
			LocalDate dataFim,
			StatusOferta status,
			UsuarioModel professorResponsavel,
			UsuarioModel criadoPor) {
		return ofertaRepository.findByOrderBySemestreDesc().stream()
				.filter(oferta -> oferta.getNome().equals(nome))
				.findFirst()
				.orElseGet(() -> {
					var oferta = new OfertaModel();
					oferta.setNome(nome);
					oferta.setSemestre(semestre);
					oferta.setDataInicio(dataInicio);
					oferta.setDataFim(dataFim);
					oferta.setStatus(status);
					oferta.setProfessorResponsavel(professorResponsavel);
					oferta.setCriadoPor(criadoPor);
					oferta.setCriadoEm(LocalDateTime.now());
					return ofertaRepository.save(oferta);
				});
	}

	private static void criarMatriculaComArtefatos(
			AlunoOfertaRepository alunoOfertaRepository,
			PlanoTrabalhoRepository planoTrabalhoRepository,
			DocumentacaoDocenciaRepository documentacaoDocenciaRepository,
			RelatorioFinalRepository relatorioFinalRepository,
			AprovacaoPlanoRepository aprovacaoPlanoRepository,
			AprovacaoRelatorioSupervisorRepository aprovacaoRelatorioSupervisorRepository,
			LogStatusAlunoOfertaRepository logStatusRepository,
			UsuarioModel aluno,
			OfertaModel oferta,
			StatusAlunoOferta status,
			TipoCredito tipoCredito,
			UsuarioModel supervisor) {
		if (alunoOfertaRepository.existsByAlunoIdAndOfertaId(aluno.getId(), oferta.getId())) {
			return;
		}

		var matricula = criarMatricula(aluno, oferta, status, tipoCredito);
		if (status == StatusAlunoOferta.CONCLUIDO_PELO_RESPONSAVEL) {
			matricula.setFrequenciaFinal(90);
			matricula.setNotaFinal(Nota.A);
		}
		alunoOfertaRepository.save(matricula);

		if (tipoCredito == TipoCredito.ESTAGIO && status != StatusAlunoOferta.NAO_ENVIADO) {
			var plano = criarPlano(matricula, supervisor);
			planoTrabalhoRepository.save(plano);
			if (status == StatusAlunoOferta.PLANO_APROVADO
					|| status == StatusAlunoOferta.RELATORIO_ENVIADO
					|| status == StatusAlunoOferta.RELATORIO_APROVADO_PELO_SUPERVISOR
					|| status == StatusAlunoOferta.CONCLUIDO_PELO_RESPONSAVEL) {
				var aprovacaoPlano = new AprovacaoPlanoModel();
				aprovacaoPlano.setPlanoTrabalho(plano);
				aprovacaoPlano.setAprovadoPor(supervisor);
				aprovacaoPlano.setParecer("Plano aprovado para dados de teste.");
				aprovacaoPlano.setAprovadoEm(LocalDateTime.now().minusDays(10));
				aprovacaoPlanoRepository.save(aprovacaoPlano);
			}
		}

		if (tipoCredito == TipoCredito.DOCUMENTACAO) {
			documentacaoDocenciaRepository.save(criarDocumentacao(matricula));
		}

		if (status == StatusAlunoOferta.RELATORIO_ENVIADO
				|| status == StatusAlunoOferta.RELATORIO_APROVADO_PELO_SUPERVISOR
				|| status == StatusAlunoOferta.CONCLUIDO_PELO_RESPONSAVEL) {
			var relatorio = criarRelatorio(matricula);
			relatorioFinalRepository.save(relatorio);
			if (status == StatusAlunoOferta.RELATORIO_APROVADO_PELO_SUPERVISOR
					|| status == StatusAlunoOferta.CONCLUIDO_PELO_RESPONSAVEL) {
				var aprovacaoRelatorio = new AprovacaoRelatorioSupervisorModel();
				aprovacaoRelatorio.setRelatorioFinal(relatorio);
				aprovacaoRelatorio.setAprovadoPor(supervisor);
				aprovacaoRelatorio.setParecer("Relatorio aprovado para dados de teste.");
				aprovacaoRelatorio.setFrequencia(90);
				aprovacaoRelatorio.setSugestaoNota(Nota.A);
				aprovacaoRelatorio.setAprovadoEm(LocalDateTime.now().minusDays(3));
				aprovacaoRelatorioSupervisorRepository.save(aprovacaoRelatorio);
			}
		}

		logStatusRepository.save(criarLog(matricula, StatusAlunoOferta.NAO_ENVIADO, status, aluno, "Status inicial criado para testes."));
	}

	private static PlanoTrabalhoModel criarPlano(AlunoOfertaModel matricula, UsuarioModel supervisor) {
		var plano = new PlanoTrabalhoModel();
		plano.setAlunoOferta(matricula);
		plano.setCodigoDisciplina("TESTE-" + matricula.getAluno().getUsername());
		plano.setNomeDisciplina("Disciplina de Teste PESCD");
		plano.setCursoDisciplina("Curso de Teste");
		plano.setProfessorSupervisor(supervisor);
		plano.setArquivoPlano("uploads/planos/plano-teste.pdf");
		plano.setEnviadoEm(LocalDateTime.now().minusDays(15));
		return plano;
	}

	private static DocumentacaoDocenciaModel criarDocumentacao(AlunoOfertaModel matricula) {
		var documentacao = new DocumentacaoDocenciaModel();
		documentacao.setAlunoOferta(matricula);
		documentacao.setNomeInstituicao("Instituicao de Teste");
		documentacao.setNomeDisciplina("Docencia de Teste");
		documentacao.setCursoDisciplina("Curso de Teste");
		documentacao.setCargaHoraria(60);
		documentacao.setArquivoDocumentacao("uploads/documentacao-docencia/documentacao-teste.pdf");
		documentacao.setEnviadoEm(LocalDateTime.now().minusDays(5));
		return documentacao;
	}

	private static RelatorioFinalModel criarRelatorio(AlunoOfertaModel matricula) {
		var relatorio = new RelatorioFinalModel();
		relatorio.setAlunoOferta(matricula);
		relatorio.setFrequenciaInformada(90);
		relatorio.setArquivoRelatorio("uploads/relatorios-finais/relatorio-teste.pdf");
		relatorio.setEnviadoEm(LocalDateTime.now().minusDays(4));
		return relatorio;
	}

	private static PerfilModel obterOuCriarPerfil(PerfilRepository perfilRepository, PerfilUsuario perfil) {
		return perfilRepository.findByNome(perfil)
				.orElseGet(() -> perfilRepository.save(new PerfilModel(perfil)));
	}

	private static UsuarioModel criarUsuario(
			String nomeCompleto,
			String email,
			String ra,
			String username,
			String password,
			PasswordEncoder passwordEncoder,
			PerfilModel... perfis) {
		var usuario = new UsuarioModel();
		usuario.setNomeCompleto(nomeCompleto);
		usuario.setEmail(email);
		usuario.setRa(ra);
		usuario.setUsername(username);
		usuario.setPassword(passwordEncoder.encode(password));
		usuario.setEnabled(true);
		for (PerfilModel perfil : perfis) {
			usuario.adicionarPerfil(perfil);
		}
		return usuario;
	}

	private static AlunoOfertaModel criarMatricula(UsuarioModel aluno, OfertaModel oferta, StatusAlunoOferta status, TipoCredito tipoCredito) {
		var matricula = new AlunoOfertaModel();
		matricula.setAluno(aluno);
		matricula.setOferta(oferta);
		matricula.setStatus(status);
		matricula.setTipoCredito(tipoCredito);
		matricula.setCriadoEm(LocalDateTime.now());
		return matricula;
	}

	private static LogStatusAlunoOfertaModel criarLog(
			AlunoOfertaModel alunoOferta,
			StatusAlunoOferta statusAnterior,
			StatusAlunoOferta statusNovo,
			UsuarioModel alteradoPor,
			String observacao) {
		var log = new LogStatusAlunoOfertaModel();
		log.setAlunoOferta(alunoOferta);
		log.setStatusAnterior(statusAnterior);
		log.setStatusNovo(statusNovo);
		log.setAlteradoPor(alteradoPor);
		log.setAlteradoEm(LocalDateTime.now());
		log.setObservacao(observacao);
		return log;
	}
}
