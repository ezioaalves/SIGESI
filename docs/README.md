# Documentacao do SIGESI

Este diretorio e o ponto central de documentacao do projeto SIGESI. Ele descreve como o sistema esta organizado hoje e como fazer mudancas futuras sem quebrar os contratos entre backend, frontend, deploy e infraestrutura.

## Mapa da documentacao

| Documento | Quando usar |
|-----------|-------------|
| [Arquitetura](ARCHITECTURE.md) | Para entender os modulos, fluxos principais e padroes de codigo. |
| [Desenvolvimento](DEVELOPMENT.md) | Para configurar ambiente local, rodar checks e criar novas funcionalidades. |
| [Guia de API](API_GUIDE.md) | Para entender autenticacao, DTOs, convencoes REST e onde consultar endpoints. |
| [Deploy e operacao](DEPLOYMENT.md) | Para operar GitHub Actions, VPS, Docker Compose, nginx e secrets. |
| [Proximos passos de deploy](NEXT_STEPS.md) | Para checklist direto de manutencao do deploy atual. |
| [Fluxo de solicitacao](FLUXO_SOLICITACAO.md) | Para entender o ciclo de vida de solicitacoes e demandas. |
| [Roteiro QA - Entradas do Frontend](QA_FRONTEND_INPUTS_2026-07-09.md) | Para executar a rodada manual de validacao de formularios, limites, persistencia e permissoes no ambiente de teste. |
| [DER](DER.md) | Para consultar o modelo de dados. |
| [Backlog de refatoracao](REFACTOR_BACKLOG.md) | Para priorizar melhorias estruturais sem mudar comportamento por acidente. |

## Repositorios

| Repositorio | Responsabilidade |
|-------------|------------------|
| `ezioaalves/SIGESI` | Backend Spring Boot, docs principais e arquivos de infraestrutura compartilhados. |
| `ezioaalves/sigesi-frontend` | Frontend React/Vite, nginx interno do container frontend e docs especificas do frontend. |

## Regras de manutencao

- Atualize esta documentacao junto com qualquer mudanca de fluxo, API, deploy ou padrao de codigo.
- Use o Swagger UI como fonte de verdade dos endpoints; Markdown deve explicar convencoes e fluxos, nao duplicar cada endpoint manualmente.
- Nunca coloque valores reais de secrets em arquivos versionados.
- Mudancas em `develop` devem ser validadas em `https://sigesi-test.ezioalves.cloud` antes de chegar em `main`.
- Mudancas estruturais devem consultar o [backlog de refatoracao](REFACTOR_BACKLOG.md) e registrar o que foi resolvido.

## Estado atual verificado

Ultima revisao desta documentacao:

- Backend: `./mvnw test` passou com `396` testes.
- Backend: compilacao passou com avisos conhecidos em MapStruct e auditoria.
- Frontend: `npm run build` passou.
- Frontend: `npm run lint` falhou com erros conhecidos listados no backlog.
