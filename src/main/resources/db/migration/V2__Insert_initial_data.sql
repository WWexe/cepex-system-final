-- =====================================================
-- MIGRAÇÃO V2 - DADOS INICIAIS DO SISTEMA CEPEX
-- Inserção de dados essenciais para o funcionamento
-- =====================================================

-- =====================================================
-- 1. INSERÇÃO DE USUÁRIOS INICIAIS
-- =====================================================

-- Usuário Administrador
INSERT INTO users (id, login, password, email, status, role) VALUES 
(UUID_TO_BIN('550e8400-e29b-41d4-a716-446655440001'), 'admin', '$2a$10$xdzWvkaG4xDIH85rpqQUKu3/sOYE8ZIjQ/EnEVKV4TozB9jNbckG6', 'admin@biopark.edu.br', 1, 'ADMIN');

-- Usuário Coordenador
INSERT INTO users (id, login, password, email, status, role) VALUES 
(UUID_TO_BIN('550e8400-e29b-41d4-a716-446655440002'), 'coordenador', '$2a$10$xdzWvkaG4xDIH85rpqQUKu3/sOYE8ZIjQ/EnEVKV4TozB9jNbckG6', 'coordenador@biopark.edu.br', 1, 'COORDENATION');

-- Usuário Secretária
INSERT INTO users (id, login, password, email, status, role) VALUES 
(UUID_TO_BIN('550e8400-e29b-41d4-a716-446655440003'), 'secretaria', '$2a$10$xdzWvkaG4xDIH85rpqQUKu3/sOYE8ZIjQ/EnEVKV4TozB9jNbckG6', 'secretaria@biopark.edu.br', 1, 'SECRETARY');

-- Usuário Professor Exemplo
INSERT INTO users (id, login, password, email, status, role) VALUES 
(UUID_TO_BIN('550e8400-e29b-41d4-a716-446655440004'), 'professor1', '$2a$10$xdzWvkaG4xDIH85rpqQUKu3/sOYE8ZIjQ/EnEVKV4TozB9jNbckG6', 'professor1@biopark.edu.br', 1, 'PROFESSOR');

-- Usuário Estudante Exemplo
INSERT INTO users (id, login, password, email, status, role) VALUES 
(UUID_TO_BIN('550e8400-e29b-41d4-a716-446655440005'), 'estudante1', '$2a$10$xdzWvkaG4xDIH85rpqQUKu3/sOYE8ZIjQ/EnEVKV4TozB9jNbckG6', 'estudante1@biopark.edu.br', 1, 'STUDENT');

-- =====================================================
-- 2. INSERÇÃO DE CURSOS
-- =====================================================

-- Curso de Engenharia de Software
INSERT INTO course (id, name, semesters, st_course, created_at) VALUES 
(UUID_TO_BIN('660e8400-e29b-41d4-a716-446655440001'), 'Engenharia de Software', 8, 1, NOW());

-- Curso de Ciência da Computação
INSERT INTO course (id, name, semesters, st_course, created_at) VALUES 
(UUID_TO_BIN('660e8400-e29b-41d4-a716-446655440002'), 'Ciência da Computação', 8, 1, NOW());

-- Curso de Sistemas de Informação
INSERT INTO course (id, name, semesters, st_course, created_at) VALUES 
(UUID_TO_BIN('660e8400-e29b-41d4-a716-446655440003'), 'Sistemas de Informação', 8, 1, NOW());

-- Curso de Análise e Desenvolvimento de Sistemas
INSERT INTO course (id, name, semesters, st_course, created_at) VALUES 
(UUID_TO_BIN('660e8400-e29b-41d4-a716-446655440004'), 'Análise e Desenvolvimento de Sistemas', 6, 1, NOW());

-- =====================================================
-- 3. INSERÇÃO DE DISCIPLINAS
-- =====================================================

-- Disciplinas para Engenharia de Software
INSERT INTO discipline (id, name, st_discipline, created_at, course_id) VALUES 
(UUID_TO_BIN('770e8400-e29b-41d4-a716-446655440001'), 'Programação Orientada a Objetos', 1, NOW(), UUID_TO_BIN('660e8400-e29b-41d4-a716-446655440001'));

INSERT INTO discipline (id, name, st_discipline, created_at, course_id) VALUES 
(UUID_TO_BIN('770e8400-e29b-41d4-a716-446655440002'), 'Estruturas de Dados', 1, NOW(), UUID_TO_BIN('660e8400-e29b-41d4-a716-446655440001'));

INSERT INTO discipline (id, name, st_discipline, created_at, course_id) VALUES 
(UUID_TO_BIN('770e8400-e29b-41d4-a716-446655440003'), 'Banco de Dados', 1, NOW(), UUID_TO_BIN('660e8400-e29b-41d4-a716-446655440001'));

-- Disciplinas para Ciência da Computação
INSERT INTO discipline (id, name, st_discipline, created_at, course_id) VALUES 
(UUID_TO_BIN('770e8400-e29b-41d4-a716-446655440004'), 'Algoritmos e Programação', 1, NOW(), UUID_TO_BIN('660e8400-e29b-41d4-a716-446655440002'));

INSERT INTO discipline (id, name, st_discipline, created_at, course_id) VALUES 
(UUID_TO_BIN('770e8400-e29b-41d4-a716-446655440005'), 'Matemática Discreta', 1, NOW(), UUID_TO_BIN('660e8400-e29b-41d4-a716-446655440002'));

-- Disciplinas para Sistemas de Informação
INSERT INTO discipline (id, name, st_discipline, created_at, course_id) VALUES 
(UUID_TO_BIN('770e8400-e29b-41d4-a716-446655440006'), 'Fundamentos de Sistemas de Informação', 1, NOW(), UUID_TO_BIN('660e8400-e29b-41d4-a716-446655440003'));

-- =====================================================
-- 4. INSERÇÃO DE PROFESSORES
-- =====================================================

-- Professor João Silva
INSERT INTO professor (id, first_name, last_name, email, ra, cpf, number, st_user, created_at, user_id) VALUES 
(UUID_TO_BIN('880e8400-e29b-41d4-a716-446655440001'), 'João', 'Silva', 'joao.silva@biopark.edu.br', 'P2024001', '12345678901', '(11) 99999-9999', 1, NOW(), UUID_TO_BIN('550e8400-e29b-41d4-a716-446655440004'));

-- Professor Maria Santos
INSERT INTO professor (id, first_name, last_name, email, ra, cpf, number, st_user, created_at, user_id) VALUES 
(UUID_TO_BIN('880e8400-e29b-41d4-a716-446655440002'), 'Maria', 'Santos', 'maria.santos@biopark.edu.br', 'P2024002', '98765432100', '(11) 88888-8888', 1, NOW(), UUID_TO_BIN('550e8400-e29b-41d4-a716-446655440004'));

-- =====================================================
-- 5. INSERÇÃO DE ESTUDANTES
-- =====================================================

-- Estudante Pedro Oliveira
INSERT INTO student (id, first_name, last_name, email, number, RA, cpf, st_user, created_at, user_id, course_id) VALUES 
(UUID_TO_BIN('990e8400-e29b-41d4-a716-446655440001'), 'Pedro', 'Oliveira', 'pedro.oliveira@biopark.edu.br', '(11) 77777-7777', '20240001', '11122233344', 1, NOW(), UUID_TO_BIN('550e8400-e29b-41d4-a716-446655440005'), UUID_TO_BIN('660e8400-e29b-41d4-a716-446655440001'));

-- Estudante Ana Costa
INSERT INTO student (id, first_name, last_name, email, number, RA, cpf, st_user, created_at, user_id, course_id) VALUES 
(UUID_TO_BIN('990e8400-e29b-41d4-a716-446655440002'), 'Ana', 'Costa', 'ana.costa@biopark.edu.br', '(11) 66666-6666', '20240002', '55566677788', 1, NOW(), UUID_TO_BIN('550e8400-e29b-41d4-a716-446655440005'), UUID_TO_BIN('660e8400-e29b-41d4-a716-446655440002'));

-- =====================================================
-- 6. RELACIONAMENTO PROFESSOR-DISCIPLINA
-- =====================================================

-- Professor João Silva - Programação Orientada a Objetos
INSERT INTO professor_discipline (professor_id, discipline_id) VALUES 
(UUID_TO_BIN('880e8400-e29b-41d4-a716-446655440001'), UUID_TO_BIN('770e8400-e29b-41d4-a716-446655440001'));

-- Professor João Silva - Estruturas de Dados
INSERT INTO professor_discipline (professor_id, discipline_id) VALUES 
(UUID_TO_BIN('880e8400-e29b-41d4-a716-446655440001'), UUID_TO_BIN('770e8400-e29b-41d4-a716-446655440002'));

-- Professor Maria Santos - Banco de Dados
INSERT INTO professor_discipline (professor_id, discipline_id) VALUES 
(UUID_TO_BIN('880e8400-e29b-41d4-a716-446655440002'), UUID_TO_BIN('770e8400-e29b-41d4-a716-446655440003'));

-- =====================================================
-- 7. INSERÇÃO DE MONITORIAS EXEMPLO
-- =====================================================

-- Monitoria de Programação Orientada a Objetos
INSERT INTO monitoria (id, title, description, is_remote, location, vacancies, workload, inicial_date, final_date, inicial_ingress, final_ingress, selection_type, selection_date, selection_time, divulgation_date, status, course_id, discipline_id, professor_id) VALUES 
(UUID_TO_BIN('aa0e8400-e29b-41d4-a716-446655440001'), 'Monitoria de POO', 'Monitoria para auxiliar estudantes na disciplina de Programação Orientada a Objetos', 0, 'Laboratório 101', 2, 20, '2024-02-01', '2024-06-30', '2024-01-15', '2024-01-30', 'ENTREVISTA', '2024-02-05', '14:00', '2024-02-03', 'PENDENTE', UUID_TO_BIN('660e8400-e29b-41d4-a716-446655440001'), UUID_TO_BIN('770e8400-e29b-41d4-a716-446655440001'), UUID_TO_BIN('880e8400-e29b-41d4-a716-446655440001'));

-- Monitoria de Banco de Dados
INSERT INTO monitoria (id, title, description, is_remote, location, vacancies, workload, inicial_date, final_date, inicial_ingress, final_ingress, selection_type, selection_date, selection_time, divulgation_date, status, course_id, discipline_id, professor_id) VALUES 
(UUID_TO_BIN('aa0e8400-e29b-41d4-a716-446655440002'), 'Monitoria de BD', 'Monitoria para auxiliar estudantes na disciplina de Banco de Dados', 1, 'Online', 1, 15, '2024-02-01', '2024-06-30', '2024-01-15', '2024-01-30', 'ANALISE_HISTORICO', '2024-02-05', '15:00', '2024-02-03', 'PENDENTE', UUID_TO_BIN('660e8400-e29b-41d4-a716-446655440001'), UUID_TO_BIN('770e8400-e29b-41d4-a716-446655440003'), UUID_TO_BIN('880e8400-e29b-41d4-a716-446655440002'));

-- =====================================================
-- 8. INSERÇÃO DE PROJETOS EXEMPLO
-- =====================================================

-- Projeto de Pesquisa
INSERT INTO research_projects (id, title, description, start_date, end_date, status, lead_researcher_id) VALUES 
(UUID_TO_BIN('bb0e8400-e29b-41d4-a716-446655440001'), 'Inteligência Artificial na Educação', 'Pesquisa sobre aplicação de IA para melhorar o aprendizado', '2024-02-01', '2024-12-31', 'ABERTO', UUID_TO_BIN('550e8400-e29b-41d4-a716-446655440004'));

-- Projeto de Extensão
INSERT INTO extension_projects (id, title, description, location, target_beneficiaries, start_date, end_date, status, coordinator_id) VALUES 
(UUID_TO_BIN('cc0e8400-e29b-41d4-a716-446655440001'), 'Programação para Jovens', 'Ensino de programação básica para jovens da comunidade', 'Centro Comunitário', 'Jovens de 14-18 anos', '2024-03-01', '2024-11-30', 'ABERTO', UUID_TO_BIN('550e8400-e29b-41d4-a716-446655440004'));

-- =====================================================
-- 9. INSERÇÃO DE AVALIAÇÕES EXEMPLO
-- =====================================================

-- Avaliação da plataforma
INSERT INTO avaliacao_plataforma (id, rating, user_id, avaliation_date) VALUES 
(UUID_TO_BIN('dd0e8400-e29b-41d4-a716-446655440001'), 5, UUID_TO_BIN('550e8400-e29b-41d4-a716-446655440001'), NOW());

INSERT INTO avaliacao_plataforma (id, rating, user_id, avaliation_date) VALUES 
(UUID_TO_BIN('dd0e8400-e29b-41d4-a716-446655440002'), 4, UUID_TO_BIN('550e8400-e29b-41d4-a716-446655440005'), NOW());

-- =====================================================
-- COMENTÁRIOS FINAIS
-- =====================================================

-- Dados iniciais inseridos com sucesso!
-- Senha padrão para todos os usuários: 'admin123' (criptografada com BCrypt)
-- IDs fixos para facilitar testes e desenvolvimento 