-- Script H2 para criar uma oferta vinculada ao usuario "responsavel".
-- Execute no console H2 depois que a aplicacao estiver iniciada.

INSERT INTO TB_OFERTA (
    id,
    nome,
    semestre,
    data_inicio,
    data_fim,
    status,
    criado_em,
    professor_responsavel_id,
    criado_por_id
)
SELECT
    RANDOM_UUID(),
    'PESCD Teste Responsavel SQL',
    '2026/2',
    DATE '2026-08-01',
    DATE '2026-12-15',
    'EM_ANDAMENTO',
    CURRENT_TIMESTAMP,
    responsavel.id,
    criador.id
FROM TB_USUARIO responsavel
JOIN TB_USUARIO criador
    ON criador.username = 'secretario'
WHERE responsavel.username = 'responsavel'
  AND NOT EXISTS (
      SELECT 1
      FROM TB_OFERTA oferta
      WHERE oferta.nome = 'PESCD Teste Responsavel SQL'
  );
