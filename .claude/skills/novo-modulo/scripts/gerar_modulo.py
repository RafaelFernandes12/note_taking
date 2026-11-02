#!/usr/bin/env python3
"""Gera um módulo Spring Boot com as camadas escolhidas por letra.

Uso:
    gerar_modulo.py <Nome> <letras> [--root <dir>] [--force]

Letras:
    m = model (entidade JPA)
    c = controller (REST)
    d = dto (Request/Response)
    s = service
    r = repository
    e = exception (NotFound)

Exemplos:
    gerar_modulo.py Nota mcdsre
    gerar_modulo.py ItemPedido m r s
    gerar_modulo.py categoria -m -r
"""
import re
import sys
from pathlib import Path
from typing import NoReturn

OPCOES = {
    "m": "model",
    "c": "controller",
    "d": "dto",
    "s": "service",
    "r": "repository",
    "e": "exception",
}


def erro(msg: str) -> NoReturn:
    print(f"ERRO: {msg}", file=sys.stderr)
    sys.exit(1)


# ---------------------------------------------------------------- argumentos
def parse_args(argv):
    root = Path.cwd()
    force = False
    pos = []
    i = 0
    while i < len(argv):
        a = argv[i]
        if a == "--root":
            if i + 1 >= len(argv):
                erro("--root precisa de um diretório")
            root = Path(argv[i + 1])
            i += 2
            continue
        if a == "--force":
            force = True
        else:
            pos.append(a)
        i += 1

    if len(pos) < 2:
        erro("uso: gerar_modulo.py <Nome> <letras>  (ex.: Nota mcdsre)\n"
             "letras: " + ", ".join(f"{k}={v}" for k, v in OPCOES.items()))

    nome = pos[0]
    letras = "".join(pos[1:]).replace("-", "").replace(",", "").lower()
    invalidas = sorted({c for c in letras if c not in OPCOES})
    if invalidas:
        erro(f"opção(ões) inválida(s): {', '.join(invalidas)}. "
             "Válidas: " + ", ".join(f"{k}={v}" for k, v in OPCOES.items()))
    return nome, set(letras), root.resolve(), force


# ---------------------------------------------------------------- nomes
def palavras(nome: str):
    ws = []
    for parte in re.split(r"[-_\s]+", nome.strip()):
        ws += re.findall(r"[A-Z]+(?![a-z])|[A-Z]?[a-z0-9]+", parte)
    ws = [w.lower() for w in ws if w]
    if not ws or not re.match(r"[a-z]", ws[0]):
        erro(f"nome de módulo inválido: {nome!r}")
    return ws


def plural(w: str) -> str:
    if w.endswith(("r", "z")):
        return w + "es"
    if w.endswith("s"):
        return w
    if w.endswith("m"):
        return w[:-1] + "ns"
    return w + "s"


# ---------------------------------------------------------------- projeto
def pacote_base(root: Path) -> tuple[str, Path]:
    java = root / "src" / "main" / "java"
    if not java.is_dir():
        erro(f"não achei {java} — rode na raiz do projeto ou use --root")
    for f in java.rglob("*.java"):
        txt = f.read_text(encoding="utf-8", errors="ignore")
        if "@SpringBootApplication" in txt:
            m = re.search(r"^\s*package\s+([\w.]+)\s*;", txt, re.M)
            if m:
                return m.group(1), java
    erro("não achei a classe com @SpringBootApplication para descobrir o pacote base")


def bloco_imports(imports) -> str:
    return "\n".join(f"import {i};" for i in sorted(set(imports)))


def arquivo(pacote: str, imports, corpo: str) -> str:
    imps = bloco_imports(imports)
    return f"package {pacote};\n\n" + (imps + "\n\n" if imps else "") + corpo.strip("\n") + "\n"


# ---------------------------------------------------------------- geradores
def gerar(nome: str, op: set, base: str):
    ws = palavras(nome)
    C = "".join(w.capitalize() for w in ws)               # Nota / ItemPedido
    v = C[0].lower() + C[1:]                              # nota / itemPedido
    mod = "".join(ws)                                     # pacote: nota / itempedido
    tabela = "_".join(ws[:-1] + [plural(ws[-1])])         # notas / item_pedidos
    rota = "/api/" + "-".join(ws[:-1] + [plural(ws[-1])])  # /api/notas

    P = f"{base}.{mod}"
    has = op.__contains__

    if has("r") and not has("m"):
        erro("repository (r) precisa do model (m) — JpaRepository exige a entidade. Inclua 'm'.")

    ENT, REQ, RES = f"{P}.model.{C}", f"{P}.dto.{C}Request", f"{P}.dto.{C}Response"
    REPO, SERV, EXC = f"{P}.repository.{C}Repository", f"{P}.service.{C}Service", f"{P}.exception.{C}NotFoundException"

    # tipos de entrada/saída do CRUD
    crud_service = has("s") and has("r") and has("m")
    if has("d"):
        IN, OUT, in_imp, out_imp = f"{C}Request", f"{C}Response", [REQ], [RES]
    else:
        IN, OUT, in_imp, out_imp = C, C, [ENT], [ENT]

    files = {}

    # ---------------- model
    if has("m"):
        files[f"model/{C}.java"] = arquivo(f"{P}.model", [
            "jakarta.persistence.Column", "jakarta.persistence.Entity", "jakarta.persistence.GeneratedValue",
            "jakarta.persistence.GenerationType", "jakarta.persistence.Id", "jakarta.persistence.Table",
            "lombok.AllArgsConstructor", "lombok.Builder", "lombok.Getter", "lombok.NoArgsConstructor",
            "lombok.Setter", "org.hibernate.annotations.CreationTimestamp",
            "org.hibernate.annotations.UpdateTimestamp", "java.time.LocalDateTime",
        ], f"""
@Entity
@Table(name = "{tabela}")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class {C} {{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: adicionar os campos de {C}

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}}
""")

    # ---------------- dto
    if has("d"):
        if has("m"):
            files[f"dto/{C}Request.java"] = arquivo(f"{P}.dto", [ENT], f"""
public record {C}Request(
        // TODO: adicionar os campos de entrada de {C}
) {{

    public {C} toEntity() {{
        return {C}.builder()
                // TODO: mapear os campos
                .build();
    }}

    public void applyTo({C} {v}) {{
        // TODO: copiar os campos para a entidade, ex.: {v}.setNome(nome());
    }}
}}
""")
            files[f"dto/{C}Response.java"] = arquivo(f"{P}.dto", [ENT, "java.time.LocalDateTime"], f"""
public record {C}Response(
        Long id,
        // TODO: adicionar os campos de saída de {C}
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {{

    public static {C}Response from({C} {v}) {{
        return new {C}Response(
                {v}.getId(),
                {v}.getCreatedAt(),
                {v}.getUpdatedAt()
        );
    }}
}}
""")
        else:
            files[f"dto/{C}Request.java"] = arquivo(f"{P}.dto", [], f"""
public record {C}Request(
        // TODO: adicionar os campos de entrada de {C}
) {{
}}
""")
            files[f"dto/{C}Response.java"] = arquivo(f"{P}.dto", [], f"""
public record {C}Response(
        Long id
        // TODO: adicionar os campos de saída de {C}
) {{
}}
""")

    # ---------------- exception
    if has("e"):
        files[f"exception/{C}NotFoundException.java"] = arquivo(f"{P}.exception", [
            "org.springframework.http.HttpStatus", "org.springframework.web.bind.annotation.ResponseStatus",
        ], f"""
@ResponseStatus(HttpStatus.NOT_FOUND)
public class {C}NotFoundException extends RuntimeException {{

    public {C}NotFoundException(Long id) {{
        super("{C} com id " + id + " não encontrado(a)");
    }}
}}
""")

    # ---------------- repository
    if has("r"):
        files[f"repository/{C}Repository.java"] = arquivo(f"{P}.repository", [
            ENT, "org.springframework.data.jpa.repository.JpaRepository",
            "org.springframework.stereotype.Repository",
        ], f"""
@Repository
public interface {C}Repository extends JpaRepository<{C}, Long> {{
}}
""")

    # ---------------- service
    if has("s"):
        if crud_service:
            imps = [ENT, REPO, "lombok.RequiredArgsConstructor", "org.springframework.stereotype.Service",
                    "org.springframework.transaction.annotation.Transactional", "java.util.List"]
            imps += in_imp + out_imp
            if has("e"):
                imps.append(EXC)
                not_found = f"new {C}NotFoundException(id)"
            else:
                imps.append("jakarta.persistence.EntityNotFoundException")
                not_found = f'new EntityNotFoundException("{C} com id " + id + " não encontrado(a)")'

            if has("d"):
                find_all = f"return {v}Repository.findAll().stream().map({C}Response::from).toList();"
                find_by = f"return {C}Response.from(buscar(id));"
                create = (f"{C} {v} = request.toEntity();\n"
                          f"        return {C}Response.from({v}Repository.save({v}));")
                update = (f"{C} {v} = buscar(id);\n"
                          f"        request.applyTo({v});\n"
                          f"        return {C}Response.from({v}Repository.save({v}));")
                p_in = "request"
            else:
                find_all = f"return {v}Repository.findAll();"
                find_by = "return buscar(id);"
                create = f"return {v}Repository.save(dados);"
                update = (f"{C} {v} = buscar(id);\n"
                          f"        // TODO: copiar os campos de 'dados' para '{v}'\n"
                          f"        return {v}Repository.save({v});")
                p_in = "dados"

            corpo = f"""
@Service
@RequiredArgsConstructor
public class {C}Service {{

    private final {C}Repository {v}Repository;

    @Transactional(readOnly = true)
    public List<{OUT}> findAll() {{
        {find_all}
    }}

    @Transactional(readOnly = true)
    public {OUT} findById(Long id) {{
        {find_by}
    }}

    @Transactional
    public {OUT} create({IN} {p_in}) {{
        {create}
    }}

    @Transactional
    public {OUT} update(Long id, {IN} {p_in}) {{
        {update}
    }}

    @Transactional
    public void delete(Long id) {{
        {v}Repository.delete(buscar(id));
    }}

    private {C} buscar(Long id) {{
        return {v}Repository.findById(id)
                .orElseThrow(() -> {not_found});
    }}
}}
"""
            files[f"service/{C}Service.java"] = arquivo(f"{P}.service", imps, corpo)
        else:
            files[f"service/{C}Service.java"] = arquivo(f"{P}.service", [
                "lombok.RequiredArgsConstructor", "org.springframework.stereotype.Service",
            ], f"""
@Service
@RequiredArgsConstructor
public class {C}Service {{

    // TODO: implementar as regras de negócio de {C}
}}
""")

    # ---------------- controller
    if has("c"):
        base_imps = ["org.springframework.web.bind.annotation.RequestMapping",
                     "org.springframework.web.bind.annotation.RestController"]
        if crud_service:
            p_in = "request" if has("d") else "dados"
            imps = base_imps + [SERV, "lombok.RequiredArgsConstructor", "java.util.List",
                                "org.springframework.http.HttpStatus",
                                "org.springframework.web.bind.annotation.DeleteMapping",
                                "org.springframework.web.bind.annotation.GetMapping",
                                "org.springframework.web.bind.annotation.PathVariable",
                                "org.springframework.web.bind.annotation.PostMapping",
                                "org.springframework.web.bind.annotation.PutMapping",
                                "org.springframework.web.bind.annotation.RequestBody",
                                "org.springframework.web.bind.annotation.ResponseStatus"] + in_imp + out_imp
            corpo = f"""
@RestController
@RequestMapping("{rota}")
@RequiredArgsConstructor
public class {C}Controller {{

    private final {C}Service {v}Service;

    @GetMapping
    public List<{OUT}> findAll() {{
        return {v}Service.findAll();
    }}

    @GetMapping("/{{id}}")
    public {OUT} findById(@PathVariable Long id) {{
        return {v}Service.findById(id);
    }}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public {OUT} create(@RequestBody {IN} {p_in}) {{
        return {v}Service.create({p_in});
    }}

    @PutMapping("/{{id}}")
    public {OUT} update(@PathVariable Long id, @RequestBody {IN} {p_in}) {{
        return {v}Service.update(id, {p_in});
    }}

    @DeleteMapping("/{{id}}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {{
        {v}Service.delete(id);
    }}
}}
"""
        elif has("s"):
            imps = base_imps + [SERV, "lombok.RequiredArgsConstructor"]
            corpo = f"""
@RestController
@RequestMapping("{rota}")
@RequiredArgsConstructor
public class {C}Controller {{

    private final {C}Service {v}Service;

    // TODO: adicionar os endpoints de {C}
}}
"""
        else:
            imps = base_imps
            corpo = f"""
@RestController
@RequestMapping("{rota}")
public class {C}Controller {{

    // TODO: adicionar os endpoints de {C}
}}
"""
        files[f"controller/{C}Controller.java"] = arquivo(f"{P}.controller", imps, corpo)

    return mod, files


# ---------------------------------------------------------------- main
def main():
    nome, op, root, force = parse_args(sys.argv[1:])
    base, java = pacote_base(root)
    mod, files = gerar(nome, op, base)

    dir_mod = java / Path(*base.split(".")) / mod
    criados, pulados = [], []
    for rel, conteudo in files.items():
        alvo = dir_mod / rel
        if alvo.exists() and not force:
            pulados.append(alvo)
            continue
        alvo.parent.mkdir(parents=True, exist_ok=True)
        alvo.write_text(conteudo, encoding="utf-8")
        criados.append(alvo)

    camadas = ", ".join(OPCOES[k] for k in OPCOES if k in op)
    print(f"Módulo '{mod}' em {dir_mod.relative_to(root)}  [{camadas}]")
    for f in criados:
        print(f"  + {f.relative_to(root)}")
    for f in pulados:
        print(f"  = {f.relative_to(root)} (já existe, não sobrescrito — use --force)")


if __name__ == "__main__":
    main()
