```markdown
# Dynamic DTO API

Este projeto é uma aplicação Spring Boot que permite a criação e recuperação de objetos dinâmicos (DTOs) com base em JSON. Os DTOs são gerados dinamicamente em tempo de execução utilizando a biblioteca ByteBuddy.

## Tecnologias Utilizadas

- **Java 17**
- **Spring Boot**
- **H2 Database**
- **ByteBuddy**
- **Jackson**
- **SpringDoc OpenAPI (Swagger)**

## Configuração

### Pré-requisitos

- Java 17+
- Maven

### Executando o Projeto

1. Clone o repositório:
   ```bash
   git clone https://github.com/darlanpj/dynamic-dto.git
   cd dynamic-dto
   ```

2. Compile e execute o projeto:
   ```bash
   mvn spring-boot:run
   ```

3. Acesse o Swagger UI para testar a API:
   ```
   http://localhost:8080/swagger-ui.html
   ```

## Endpoints

### 1. Criar um DTO Dinâmico

**Descrição:** Cria um DTO dinâmico com base no JSON enviado no corpo da requisição e o salva no banco de dados H2.

- **URL:** `POST /api/dto/{id}`
- **Parâmetros:**
  - `id` (Path Variable): Identificador único do DTO.
- **Corpo da Requisição:** JSON representando os campos e valores do DTO.
- **Exemplo de Requisição:**
  ```bash
  curl -X POST http://localhost:8080/api/dto/123 \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João",
    "idade": 30,
    "endereco": {
      "rua": "Rua A",
      "numero": 123
    }
  }'
  ```
- **Resposta de Sucesso:**
  ```json
  DTO gerado e salvo no H2 com ID: 123
  ```
- **Resposta de Erro:**
  - `400 Bad Request`: Erro ao processar o JSON ou salvar no banco.

---

### 2. Recuperar um DTO Dinâmico

**Descrição:** Recupera o JSON de um DTO dinâmico salvo no banco de dados.

- **URL:** `GET /api/dto/{id}`
- **Parâmetros:**
  - `id` (Path Variable): Identificador único do DTO.
- **Exemplo de Requisição:**
  ```bash
  curl -X GET http://localhost:8080/api/dto/123
  ```
- **Resposta de Sucesso:**
  ```json
  {
    "nome": "João",
    "idade": 30,
    "endereco": {
      "rua": "Rua A",
      "numero": 123
    }
  }
  ```
- **Respostas de Erro:**
  - `404 Not Found`: DTO não encontrado.
  - `500 Internal Server Error`: Erro ao processar o JSON.

---

## Banco de Dados H2

A aplicação utiliza um banco de dados H2 em memória. Para acessar o console do H2:

1. Acesse: `http://localhost:8080/h2-console`
2. Use as seguintes credenciais:
   - **JDBC URL:** `jdbc:h2:mem:dynamicdb`
   - **Username:** `sa`
   - **Password:** *(deixe em branco)*

---

## Estrutura do Projeto

- `controller/DTOController.java`: Controlador REST para gerenciar os DTOs.
- `service/DynamicDTOService.java`: Serviço responsável pela lógica de geração e manipulação de DTOs dinâmicos.
- `model/JsonEntity.java`: Entidade que representa o JSON salvo no banco.
- `repository/JsonEntityRepository.java`: Repositório JPA para persistência de dados.
- `helper/ToStringInterceptor.java`: Interceptor para sobrescrever o método `toString` dos DTOs gerados.

---

## Testando com Swagger

1. Acesse o Swagger UI: `http://localhost:8080/swagger-ui.html`
2. Utilize a interface para testar os endpoints `POST /api/dto/{id}` e `GET /api/dto/{id}`.

---

## Exemplos de Uso

### Criar um DTO com JSON Simples
```bash
curl -X POST http://localhost:8080/api/dto/1 \
-H "Content-Type: application/json" \
-d '{"nome": "Maria", "idade": 25}'
```

### Criar um DTO com JSON Aninhado
```bash
curl -X POST http://localhost:8080/api/dto/2 \
-H "Content-Type: application/json" \
-d '{
  "produto": "Notebook",
  "preco": 3500.50,
  "especificacoes": {
    "marca": "Dell",
    "modelo": "Inspiron"
  }
}'
```

### Recuperar um DTO
```bash
curl -X GET http://localhost:8080/api/dto/1
```

---

## Licença

Este projeto é distribuído sob a licença MIT. Consulte o arquivo `LICENSE` para mais detalhes.
```
