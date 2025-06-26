# 📚 DOCUMENTAÇÃO SWAGGER/OPENAPI - CEPEX SYSTEM

## 🚀 Como Acessar a Documentação

### **1. Interface Swagger UI**

Após iniciar a aplicação, acesse:

```
http://localhost:8080/swagger-ui/index.html
```

### **2. Especificação OpenAPI (JSON)**

Para obter a especificação em formato JSON:

```
http://localhost:8080/api-docs
```

## 📋 Endpoints Documentados

### **🔐 Autenticação (`/auth`)**

- `POST /auth/login` - Realizar login
- `POST /auth/register` - Registrar novo usuário
- `POST /auth/forgot-password` - Solicitar recuperação de senha
- `POST /auth/reset-password` - Redefinir senha

### **👥 Usuários (`/users`)**

- `GET /users` - Listar todos os usuários
- `GET /users/{id}` - Buscar usuário por ID
- `POST /users` - Criar novo usuário
- `PUT /users/{id}` - Atualizar usuário
- `DELETE /users/{id}` - Deletar usuário
- `GET /users/stats` - Estatísticas de usuários
- `PATCH /users/{id}/status` - Atualizar status do usuário
- `PATCH /users/{id}/role` - Atualizar role do usuário

### **🎓 Cursos (`/courses`)**

- `GET /courses` - Listar todos os cursos
- `GET /courses/{id}` - Buscar curso por ID
- `POST /courses` - Criar novo curso
- `PUT /courses/{id}` - Atualizar curso
- `DELETE /courses/{id}` - Deletar curso
- `GET /courses/active` - Listar cursos ativos

## 🔑 Autenticação JWT

### **Como obter o token:**

1. Acesse o endpoint `POST /auth/login`
2. Use as credenciais:
   ```json
   {
     "login": "admin",
     "password": "admin123"
   }
   ```
3. Copie o token retornado

### **Como usar o token:**

1. No Swagger UI, clique no botão **"Authorize"** (🔒)
2. No campo **"Bearer Authentication"**, insira: `Bearer SEU_TOKEN_AQUI`
3. Clique em **"Authorize"**
4. Agora você pode testar todos os endpoints protegidos

## 📝 Exemplos de Uso

### **Login:**

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"admin","password":"admin123"}'
```

### **Listar Cursos (com autenticação):**

```bash
curl -X GET http://localhost:8080/courses \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

### **Criar Novo Curso:**

```bash
curl -X POST http://localhost:8080/courses \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -d '{
    "name": "Engenharia de Software",
    "semesters": 8,
    "stCourse": true
  }'
```

## 🛠️ Configurações do Swagger

### **Arquivo de Configuração:**

- `OpenApiConfig.java` - Configuração principal do OpenAPI
- `application.properties` - Configurações do SpringDoc

### **Anotações Utilizadas:**

- `@Tag` - Agrupa endpoints por categoria
- `@Operation` - Descreve a operação
- `@ApiResponses` - Documenta as respostas possíveis
- `@Parameter` - Documenta parâmetros
- `@Schema` - Define o schema dos objetos

## 🎯 Próximos Passos

### **1. Documentar Mais Controllers**

- [ ] ProfessorController
- [ ] StudentController
- [ ] DisciplineController
- [ ] MonitoriaController
- [ ] ResearchProjectController
- [ ] ExtensionProjectController

### **2. Melhorar Documentação**

- [ ] Adicionar exemplos mais detalhados
- [ ] Documentar códigos de erro
- [ ] Adicionar descrições de campos
- [ ] Criar guias de uso

### **3. Integração com Frontend**

- [ ] Configurar CORS adequadamente
- [ ] Testar integração com React
- [ ] Documentar endpoints específicos do frontend

## 🔧 Troubleshooting

### **Problema: Swagger não carrega**

- Verifique se a aplicação está rodando na porta 8080
- Confirme se as dependências do SpringDoc estão no pom.xml
- Verifique os logs da aplicação

### **Problema: Endpoints não aparecem**

- Verifique se os controllers têm as anotações `@RestController`
- Confirme se os métodos têm as anotações do Swagger
- Verifique se não há erros de compilação

### **Problema: Autenticação não funciona**

- Verifique se o token está no formato correto: `Bearer TOKEN`
- Confirme se o token não expirou
- Teste o login novamente para obter um novo token

## 📞 Suporte

Para dúvidas ou problemas:

- Email: contato@biopark.edu.br
- Documentação: https://biopark.edu.br
- Repositório: [Link do repositório]
