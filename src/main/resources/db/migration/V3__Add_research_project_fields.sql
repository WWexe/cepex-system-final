-- =====================================================
-- MIGRAÇÃO V3 - ADICIONAR CAMPOS AO RESEARCH_PROJECTS
-- Adiciona novos campos para informações detalhadas do projeto
-- =====================================================

-- Adicionar novos campos à tabela research_projects
ALTER TABLE research_projects 
ADD COLUMN material_usage TEXT COMMENT 'Utilização de material do projeto',
ADD COLUMN research_line TEXT COMMENT 'Linha de pesquisa do projeto',
ADD COLUMN subject_theme TEXT COMMENT 'Assunto/Tema do projeto',
ADD COLUMN justification TEXT COMMENT 'Justificativa do projeto',
ADD COLUMN problem_formulation TEXT COMMENT 'Formulação do problema',
ADD COLUMN hypothesis_formulation TEXT COMMENT 'Formulação da hipótese',
ADD COLUMN general_objective TEXT COMMENT 'Objetivo geral',
ADD COLUMN specific_objective TEXT COMMENT 'Objetivo específico',
ADD COLUMN theoretical_foundation TEXT COMMENT 'Fundamentação teórica',
ADD COLUMN methodological_approaches TEXT COMMENT 'Encaminhamentos metodológicos',
ADD COLUMN project_references TEXT COMMENT 'Referências bibliográficas';

-- =====================================================
-- COMENTÁRIOS FINAIS
-- =====================================================

-- Novos campos adicionados com sucesso!
-- Todos os campos são opcionais (nullable) para não quebrar dados existentes
-- Campos TEXT permitem textos longos para descrições detalhadas 