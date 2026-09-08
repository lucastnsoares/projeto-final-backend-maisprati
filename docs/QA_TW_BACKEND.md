# QA / Tech Writing — Back-end

## Escopo analisado
- Repositório: `lucastnsoares/projeto-final-backend-maispratie`
- Branch de referência: `feat/user-authentication`
- Snapshot analisado: commit `22b0a34` (2026-09-01)
- Tipo de análise: estática. **Nenhum teste foi marcado como executado.**

## Evidências encontradas
- Java 21 + Spring Boot.
- Spring Security.
- JWT (`java-jwt`) e sessão stateless.
- BCrypt para senha.
- PostgreSQL + Spring Data JPA.
- Flyway migrations.
- Swagger/OpenAPI annotations.
- Recuperação de senha com token e serviço de e-mail.
- Perfis no enum: `ADMIN`, `DOADOR`, `PONTO_COLETA_GERENTE`, `PONTO_COLETA_OPERADOR`.
- Área `/admin/**` protegida com `@PreAuthorize("hasRole('ADMIN')")`.

## Endpoints encontrados
| Método | Endpoint | Observação |
|---|---|---|
| POST | `/auth/login` | Login e JWT |
| POST | `/auth/forgot-password` | Solicitação de recuperação |
| GET | `/auth/forgot-password/check-token` | Validação de token |
| POST | `/auth/forgot-password/reset` | Redefinição |
| POST | `/register` | Auto-cadastro público; atribui `DOADOR` |
| GET | `/user` | Usuário autenticado |
| PATCH | `/user` | Editar perfil |
| PATCH | `/user/password` | Alterar senha |
| GET | `/admin/users` | ADMIN |
| POST | `/admin/users` | ADMIN |
| GET | `/admin/users/{id}` | ADMIN |
| PATCH | `/admin/users/{id}` | ADMIN |

## Pendências/achados
1. O Documento de Visão usa três perfis de negócio, mas o Back possui quatro roles técnicas. Formalizar essa decisão.
2. Auto-cadastro público atualmente atribui `DOADOR`; cadastro de Ponto de Coleta não está evidenciado neste snapshot.
3. US-010/011/012/017 não estão evidenciadas por entidades/endpoints específicos de ponto de coleta.
4. Estrutura de testes existe, porém cobertura mínima de 70% não está evidenciada.
5. Confirmar contratos com o Front para `GET /user` e recuperação de senha.
6. Executar posteriormente testes 401/403, token inválido/expirado e autorização por role.

## Casos QA preparados
| ID | Cenário | Status |
|---|---|---|
| BE-AUTH-01 | Login válido retorna JWT | NOT RUN |
| BE-AUTH-02 | Credenciais inválidas | NOT RUN |
| BE-AUTH-03 | Recurso protegido sem token | NOT RUN |
| BE-AUTH-04 | Token inválido/expirado | NOT RUN |
| BE-ADMIN-01 | ADMIN acessa `/admin/users` | NOT RUN |
| BE-ADMIN-02 | DOADOR tenta `/admin/users` | NOT RUN |
| BE-REG-01 | Cadastro válido de Doador | NOT RUN |
| BE-REG-02 | E-mail/documento duplicado | NOT RUN |
| BE-PASS-01 | Token de recuperação válido | NOT RUN |
| BE-PASS-02 | Token inválido/expirado/reutilizado | NOT RUN |

## Padrão de bug
Use no Jira:
- título `[BUG][US-XXX] ...`
- branch/commit
- ambiente
- pré-condições
- passos
- resultado atual
- resultado esperado
- severidade
- evidências
- status de reteste
