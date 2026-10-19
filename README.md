# ROCKET (SoulUp) – API RESTful

API em **Spring Boot 3.3 / Java 21** para missões diárias e vouchers de mobilidade, com Oracle via JDBC (padrões DAO, BO, Resource e Connection Factory).

## Executar localmente
1. Execute `sql/script.sql` no Oracle.
2. Abra no IntelliJ (File → Open → `pom.xml`) com JDK 21.
3. (Opcional) defina `DB_URL`, `DB_USER`, `DB_PASSWORD`; senão valem os padrões de `ConnectionFactory`.
4. Rode `RocketApplication` ou `mvn spring-boot:run` e acesse http://localhost:8080

## Endpoints
| Recurso | Rotas |
|---|---|
| `/usuarios` | POST, GET, GET `/{id}`, GET `/cpf/{cpf}`, PUT `/{id}`, DELETE `/{id}`, POST `/login` |
| `/missoes` | POST, GET `?cpf=`, GET `/{id}`, PUT `/{id}`, PATCH `/{id}/concluir`, DELETE `/{id}` |
| `/vouchers` | POST, GET `?cpf=`, GET `/{id}`, GET `/codigo/{codigo}`, PUT `/{id}`, PATCH `/{id}/resgatar`, DELETE `/{id}` |

Detalhes e códigos de status na documentação (PDF).

## Deploy (Render)
New → Web Service → repositório → runtime **Docker** → variáveis `DB_URL`, `DB_USER`, `DB_PASSWORD`.
