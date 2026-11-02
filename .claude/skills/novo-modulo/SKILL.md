---
name: novo-modulo
description: Cria um novo módulo Spring Boot já preenchido (model, controller, dto, service, repository, exception) a partir do nome do módulo e das letras das camadas desejadas. Use quando o usuário pedir para criar/gerar um módulo, CRUD ou entidade nova, ou invocar /novo-modulo.
argument-hint: <Nome> <letras>  (ex.: Nota mcdsre)
allowed-tools: Bash(python3 *)
---

# Novo módulo

Gera as classes de um módulo em `src/main/java/<pacote-base>/<modulo>/<camada>/`.
O pacote base é descoberto pela classe `@SpringBootApplication`.

## Opções (só a primeira letra)

| Letra | Camada       | Arquivo(s) gerado(s)                                   |
|-------|--------------|--------------------------------------------------------|
| `m`   | model        | `model/<Nome>.java` — entidade JPA com id, createdAt, updatedAt |
| `c`   | controller   | `controller/<Nome>Controller.java` — REST em `/api/<nomes>` |
| `d`   | dto          | `dto/<Nome>Request.java`, `dto/<Nome>Response.java` (records) |
| `s`   | service      | `service/<Nome>Service.java`                           |
| `r`   | repository   | `repository/<Nome>Repository.java` — `JpaRepository` (exige `m`) |
| `e`   | exception    | `exception/<Nome>NotFoundException.java` — 404         |

As letras podem vir juntas (`mcdsr`), separadas (`m c d`) ou com hífen (`-m -c`).

As classes se conectam conforme o que foi escolhido:
- `s` + `r` + `m` → service com CRUD completo (findAll, findById, create, update, delete).
- `c` + esse service → controller com GET/GET id/POST/PUT/DELETE.
- Com `d`, service/controller trabalham com `Request`/`Response`; sem `d`, com a entidade.
- Com `e`, o "não encontrado" usa `<Nome>NotFoundException`; sem `e`, usa `EntityNotFoundException`.
- Camadas sem as dependências necessárias saem como classe vazia anotada, com `TODO`.

## Como executar

Argumentos recebidos: `$ARGUMENTS`

1. Se faltar o nome ou as letras, pergunte ao usuário — não escolha as camadas por ele.
2. Rode na raiz do projeto:

   ```bash
   python3 "${CLAUDE_SKILL_DIR}/scripts/gerar_modulo.py" <Nome> <letras>
   ```

   - Arquivos que já existem não são sobrescritos; só passe `--force` se o usuário pedir.
   - Se o script der erro (ex.: `r` sem `m`), mostre a mensagem ao usuário e pergunte como seguir.
3. Mostre a lista de arquivos criados que o script imprimiu.
4. Se o usuário informou campos para o módulo, preencha-os na entidade (substituindo o `TODO`),
   nos records `Request`/`Response` e nos métodos `toEntity`, `applyTo` e `from`.
   Caso contrário, só lembre que os campos ficaram marcados com `TODO`.
