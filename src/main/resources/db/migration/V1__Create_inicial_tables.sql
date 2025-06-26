-- =====================================================
-- MIGRAÇÃO INICIAL - CEPEX SYSTEM
-- Criação de todas as tabelas do sistema
-- =====================================================

-- Tabela de Usuários (Base para todos)
CREATE TABLE users (
    id BINARY(16) NOT NULL,
    login VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    status BIT(1) NOT NULL,
    role VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

-- Tabela de Cursos
CREATE TABLE course (
    id BINARY(16) NOT NULL,
    name VARCHAR(255) NOT NULL,
    semesters INT NOT NULL,
    st_course BIT(1) NOT NULL,
    created_at DATETIME(6),
    PRIMARY KEY (id)
);

-- Tabela de Disciplinas
CREATE TABLE discipline (
    id BINARY(16) NOT NULL,
    name VARCHAR(255) NOT NULL,
    st_discipline BIT(1) DEFAULT TRUE,
    created_at DATETIME(6),
    course_id BINARY(16),
    PRIMARY KEY (id),
    FOREIGN KEY (course_id) REFERENCES course(id)
);

-- Tabela de Professores
CREATE TABLE professor (
    id BINARY(16) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    ra VARCHAR(255) NOT NULL UNIQUE,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    number VARCHAR(20),
    st_user BIT(1) NOT NULL,
    created_at DATETIME(6),
    user_id BINARY(16),
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tabela de Estudantes
CREATE TABLE student (
    id BINARY(16) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    number VARCHAR(20),
    RA VARCHAR(8) NOT NULL UNIQUE,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    st_user BIT(1) NOT NULL,
    created_at DATETIME(6),
    user_id BINARY(16),
    course_id BINARY(16),
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (course_id) REFERENCES course(id)
);

-- Tabela de ligação entre Professores e Disciplinas
CREATE TABLE professor_discipline (
    professor_id BINARY(16) NOT NULL,
    discipline_id BINARY(16) NOT NULL,
    PRIMARY KEY (professor_id, discipline_id),
    FOREIGN KEY (professor_id) REFERENCES professor(id),
    FOREIGN KEY (discipline_id) REFERENCES discipline(id)
);

-- Tabela de Monitorias
CREATE TABLE monitoria (
    id BINARY(16) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    is_remote BIT(1) NOT NULL,
    location VARCHAR(255),
    vacancies INT NOT NULL,
    workload INT NOT NULL,
    inicial_date DATE NOT NULL,
    final_date DATE NOT NULL,
    inicial_ingress DATE NOT NULL,
    final_ingress DATE NOT NULL,
    selection_type VARCHAR(50) NOT NULL,
    selection_date DATE,
    selection_time VARCHAR(255),
    divulgation_date DATE,
    status VARCHAR(20) NOT NULL,
    course_id BINARY(16),
    discipline_id BINARY(16),
    professor_id BINARY(16),
    PRIMARY KEY (id),
    FOREIGN KEY (course_id) REFERENCES course(id),
    FOREIGN KEY (discipline_id) REFERENCES discipline(id),
    FOREIGN KEY (professor_id) REFERENCES professor(id)
);

-- Tabela de Candidaturas para Monitoria
CREATE TABLE candidatura_monitoria (
    id BINARY(16) NOT NULL,
    monitoria_id BINARY(16) NOT NULL,
    aluno_id BINARY(16) NOT NULL,
    status VARCHAR(20),
    data_candidatura DATETIME(6),
    PRIMARY KEY (id),
    FOREIGN KEY (monitoria_id) REFERENCES monitoria(id),
    FOREIGN KEY (aluno_id) REFERENCES users(id)
);

-- Tabela de Projetos de Pesquisa
CREATE TABLE research_projects (
    id BINARY(16) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    start_date DATE,
    end_date DATE,
    status VARCHAR(20),
    lead_researcher_id BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (lead_researcher_id) REFERENCES users(id)
);

-- Tabela de Projetos de Extensão
CREATE TABLE extension_projects (
    id BINARY(16) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    location VARCHAR(255) NOT NULL,
    target_beneficiaries VARCHAR(255),
    start_date DATE,
    end_date DATE,
    status VARCHAR(20),
    coordinator_id BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (coordinator_id) REFERENCES users(id)
);

-- Tabela de Inscrições em Projetos de Pesquisa
CREATE TABLE inscricao_research_project (
    id BINARY(16) NOT NULL,
    research_project_id BINARY(16) NOT NULL,
    aluno_id BINARY(16) NOT NULL,
    status VARCHAR(20),
    data_inscricao DATETIME(6),
    PRIMARY KEY (id),
    FOREIGN KEY (research_project_id) REFERENCES research_projects(id),
    FOREIGN KEY (aluno_id) REFERENCES users(id)
);

-- Tabela de Inscrições em Projetos de Extensão
CREATE TABLE inscricao_extension_project (
    id BINARY(16) NOT NULL,
    extension_project_id BINARY(16) NOT NULL,
    aluno_id BINARY(16) NOT NULL,
    status VARCHAR(20),
    data_inscricao DATETIME(6),
    PRIMARY KEY (id),
    FOREIGN KEY (extension_project_id) REFERENCES extension_projects(id),
    FOREIGN KEY (aluno_id) REFERENCES users(id)
);

-- Tabela de Colaboradores de Projetos de Pesquisa
CREATE TABLE project_collaborators (
    project_id BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    PRIMARY KEY (project_id, user_id),
    FOREIGN KEY (project_id) REFERENCES research_projects(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tabela de Equipe de Projetos de Extensão
CREATE TABLE extension_project_team (
    project_id BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    PRIMARY KEY (project_id, user_id),
    FOREIGN KEY (project_id) REFERENCES extension_projects(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tabela de Avaliações da Plataforma
CREATE TABLE avaliacao_plataforma (
    id BINARY(16) NOT NULL,
    rating INT NOT NULL,
    user_id BINARY(16) NOT NULL,
    avaliation_date DATETIME(6),
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- =====================================================
-- ÍNDICES PARA MELHOR PERFORMANCE
-- =====================================================

-- Índices para busca por email
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_professor_email ON professor(email);
CREATE INDEX idx_student_email ON student(email);

-- Índices para busca por RA
CREATE INDEX idx_professor_ra ON professor(ra);
CREATE INDEX idx_student_ra ON student(RA);

-- Índices para busca por CPF
CREATE INDEX idx_professor_cpf ON professor(cpf);
CREATE INDEX idx_student_cpf ON student(cpf);

-- Índices para busca por login
CREATE INDEX idx_users_login ON users(login);

-- Índices para relacionamentos
CREATE INDEX idx_discipline_course ON discipline(course_id);
CREATE INDEX idx_professor_user ON professor(user_id);
CREATE INDEX idx_student_user ON student(user_id);
CREATE INDEX idx_student_course ON student(course_id);
CREATE INDEX idx_monitoria_course ON monitoria(course_id);
CREATE INDEX idx_monitoria_discipline ON monitoria(discipline_id);
CREATE INDEX idx_monitoria_professor ON monitoria(professor_id);
CREATE INDEX idx_candidatura_monitoria ON candidatura_monitoria(monitoria_id);
CREATE INDEX idx_candidatura_aluno ON candidatura_monitoria(aluno_id);
CREATE INDEX idx_inscricao_research_project ON inscricao_research_project(research_project_id);
CREATE INDEX idx_inscricao_research_aluno ON inscricao_research_project(aluno_id);
CREATE INDEX idx_inscricao_extension_project ON inscricao_extension_project(extension_project_id);
CREATE INDEX idx_inscricao_extension_aluno ON inscricao_extension_project(aluno_id);
CREATE INDEX idx_avaliacao_user ON avaliacao_plataforma(user_id);

-- =====================================================
-- COMENTÁRIOS DAS TABELAS
-- =====================================================

-- Comentários para documentação
ALTER TABLE users COMMENT = 'Tabela de usuários do sistema';
ALTER TABLE course COMMENT = 'Tabela de cursos disponíveis';
ALTER TABLE discipline COMMENT = 'Tabela de disciplinas por curso';
ALTER TABLE professor COMMENT = 'Tabela de professores';
ALTER TABLE student COMMENT = 'Tabela de estudantes';
ALTER TABLE monitoria COMMENT = 'Tabela de monitorias disponíveis';
ALTER TABLE candidatura_monitoria COMMENT = 'Tabela de candidaturas para monitorias';
ALTER TABLE research_projects COMMENT = 'Tabela de projetos de pesquisa';
ALTER TABLE extension_projects COMMENT = 'Tabela de projetos de extensão';
ALTER TABLE inscricao_research_project COMMENT = 'Tabela de inscrições em projetos de pesquisa';
ALTER TABLE inscricao_extension_project COMMENT = 'Tabela de inscrições em projetos de extensão';
ALTER TABLE avaliacao_plataforma COMMENT = 'Tabela de avaliações da plataforma';