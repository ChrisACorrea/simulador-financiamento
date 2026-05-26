# Simulador de Financiamento

API backend em Java 25 + Quarkus para simular juros compostos, gerar memoria de calculo mensal e persistir simulacoes em H2 embutido.

## Requisitos

- Java 25
- Maven Wrapper (incluido no projeto)

## Executar a aplicacao

No Windows (PowerShell):

```powershell
.\mvnw.cmd quarkus:dev
```

A API inicia em `http://localhost:8080`.

## OpenAPI / Swagger

- Especificacao OpenAPI: `http://localhost:8080/openapi`
- Swagger UI: `http://localhost:8080/q/swagger-ui`

## Executar testes e validar cobertura (80%+)

Comando oficial para CI/local:

```powershell
.\mvnw.cmd clean verify
```

Esse comando executa:

- Testes de unidade e integracao
- Relatorio JaCoCo
- Regra de cobertura minima (falha build abaixo de 80% de linhas)

## Artefatos de cobertura

Gerados apos `clean verify`:

- `target/jacoco.exec`
- `target/site/jacoco/jacoco.xml`
- `target/site/jacoco/index.html`

## Scripts de cobertura

Atalhos em PowerShell para visualizar a cobertura apos gerar o relatorio:

```powershell
.\scripts\coverage-html.ps1
.\scripts\coverage-summary.ps1
```

Para mandar o script gerar o relatorio antes de abrir/exibir:

```powershell
.\scripts\coverage-html.ps1 -Generate
.\scripts\coverage-summary.ps1 -Generate
```

Comportamento dos scripts:

- `coverage-html.ps1`: abre `target/site/jacoco/index.html` no navegador padrao
- `coverage-summary.ps1`: mostra no terminal o resumo agregado de cobertura de linhas e branches

## Observacoes de persistencia

- Banco: H2 embutido em memoria
- Schema: criado automaticamente pela aplicacao (`drop-and-create`)
- Nao utiliza Docker ou Docker Compose
