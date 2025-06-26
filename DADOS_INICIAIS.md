# 📋 DADOS INICIAIS DO SISTEMA CEPEX

## 🔐 CREDENCIAIS DE ACESSO

### **Usuários Criados:**

| Login         | Senha      | Role         | Email                      | Descrição                |
| ------------- | ---------- | ------------ | -------------------------- | ------------------------ |
| `admin`       | `admin123` | ADMIN        | admin@biopark.edu.br       | Administrador do sistema |
| `coordenador` | `admin123` | COORDENATION | coordenador@biopark.edu.br | Coordenador acadêmico    |
| `secretaria`  | `admin123` | SECRETARY    | secretaria@biopark.edu.br  | Secretária acadêmica     |
| `professor1`  | `admin123` | PROFESSOR    | professor1@biopark.edu.br  | Professor exemplo        |
| `estudante1`  | `admin123` | STUDENT      | estudante1@biopark.edu.br  | Estudante exemplo        |

## 🎓 CURSOS CRIADOS

1. **Engenharia de Software** (8 semestres)
2. **Ciência da Computação** (8 semestres)
3. **Sistemas de Informação** (8 semestres)
4. **Análise e Desenvolvimento de Sistemas** (6 semestres)

## 📚 DISCIPLINAS CRIADAS

### **Engenharia de Software:**

- Programação Orientada a Objetos
- Estruturas de Dados
- Banco de Dados

### **Ciência da Computação:**

- Algoritmos e Programação
- Matemática Discreta

### **Sistemas de Informação:**

- Fundamentos de Sistemas de Informação

## 👨‍🏫 PROFESSORES CRIADOS

1. **João Silva**

   - Email: joao.silva@biopark.edu.br
   - RA: P2024001
   - CPF: 12345678901
   - Disciplinas: POO, Estruturas de Dados

2. **Maria Santos**
   - Email: maria.santos@biopark.edu.br
   - RA: P2024002
   - CPF: 98765432100
   - Disciplinas: Banco de Dados

## 👨‍🎓 ESTUDANTES CRIADOS

1. **Pedro Oliveira**

   - Email: pedro.oliveira@biopark.edu.br
   - RA: 20240001
   - CPF: 11122233344
   - Curso: Engenharia de Software

2. **Ana Costa**
   - Email: ana.costa@biopark.edu.br
   - RA: 20240002
   - CPF: 55566677788
   - Curso: Ciência da Computação

## 📖 MONITORIAS CRIADAS

1. **Monitoria de POO**

   - Professor: João Silva
   - Vagas: 2
   - Local: Laboratório 101
   - Tipo de Seleção: Entrevista

2. **Monitoria de BD**
   - Professor: Maria Santos
   - Vagas: 1
   - Local: Online
   - Tipo de Seleção: Análise de Histórico

## 🔬 PROJETOS CRIADOS

### **Projeto de Pesquisa:**

- **Título:** Inteligência Artificial na Educação
- **Descrição:** Pesquisa sobre aplicação de IA para melhorar o aprendizado
- **Status:** ABERTO

### **Projeto de Extensão:**

- **Título:** Programação para Jovens
- **Descrição:** Ensino de programação básica para jovens da comunidade
- **Local:** Centro Comunitário
- **Status:** ABERTO

## ⭐ AVALIAÇÕES CRIADAS

- 2 avaliações da plataforma (5 e 4 estrelas)

## 🚀 COMO EXECUTAR A MIGRAÇÃO

1. **Parar a aplicação** (se estiver rodando)
2. **Reiniciar a aplicação:**
   ```bash
   cd cepex-system
   ./mvnw spring-boot:run
   ```
3. **O Flyway executará automaticamente** a migração V2
4. **Verificar os logs** para confirmar a execução

## 🔍 COMO TESTAR

### **1. Testar Login:**

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"admin","password":"admin123"}'
```

### **2. Testar Listagem de Cursos:**

```bash
curl -X GET http://localhost:8080/courses \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

### **3. Testar Listagem de Usuários:**

```bash
curl -X GET http://localhost:8080/users \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

## 📝 NOTAS IMPORTANTES

- **Senha padrão:** `admin123` para todos os usuários
- **IDs fixos:** Utilizados para facilitar testes e desenvolvimento
- **Relacionamentos:** Todos os relacionamentos entre entidades estão configurados
- **Dados realistas:** Dados baseados em cenários reais de uma instituição de ensino

## 🛠️ PRÓXIMOS PASSOS

1. **Testar todos os endpoints** da API
2. **Configurar o frontend** para usar os novos dados
3. **Criar mais dados** conforme necessário
4. **Implementar funcionalidades específicas** do sistema
