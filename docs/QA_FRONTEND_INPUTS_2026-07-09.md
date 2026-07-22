# Roteiro QA - Entradas do Frontend

Data da rodada: 09/07/2026  
Ambiente: https://sigesi-test.ezioalves.cloud  
Objetivo: validar entradas, limites, persistencia e permissoes antes de liberar o sistema para publico externo.

## Preparacao

- Use somente dados ficticios. Nao cadastre CPF, nomes, enderecos ou documentos reais.
- Cada testador deve prefixar os dados com `QA-[NOME]-20260709`, por exemplo `QA-MARIA-20260709`.
- Separe quatro contas de teste: `ADMIN`, `OPERADOR`, `AGENTE` e `CIDADAO`.
- Apos cada cadastro valido, atualize a pagina e confirme que o dado continua salvo.
- Para cada falha, registre perfil, tela, dado usado, resultado esperado, resultado obtido, horario e print.
- Classifique a falha como:
  - `Bloqueante`: impede login, criacao de solicitacao ou uso basico do sistema.
  - `Alta`: aceita dado critico invalido, perde dado salvo ou permite acesso indevido.
  - `Media`: erro de validacao confuso, comportamento inconsistente ou filtro incorreto.
  - `Baixa`: texto, layout, alinhamento ou melhoria de usabilidade.

## Dados Padrao

| Campo | Valor sugerido |
| --- | --- |
| Nome | `QA-[NOME]-20260709 Cidadao` |
| CPF sem mascara | `90000000001`, `90000000002`, `90000000003` |
| CPF com mascara | `900.000.000-04` |
| Endereco | `Rua QA Teste`, numero `123` ou `S/N`, bairro `Centro QA` |
| Referencia | `Perto da praca QA` |
| Texto curto | `Teste QA 20260709` |
| Texto longo | Repetir `Teste de limite SIGESI 20260709` ate passar de 1000 caracteres |
| Materiais | `QA Cimento`, `QA Areia`, `QA Lampada LED` |
| Documento | `QA Memorando de Teste`, interessado `Setor QA`, assinante `Responsavel QA` |

## Checklist por Perfil

### Cidadao

- [ ] Acessar `/portal` autenticado como `CIDADAO`.
- [ ] Criar solicitacao em `/solicitacao` para cada categoria: Iluminacao, Esgoto, Buraco na via, Limpeza urbana e Outros.
- [ ] Enviar solicitacao com descricao curta, descricao longa, acentos e quebras de linha.
- [ ] Tentar enviar sem descricao, sem logradouro, sem numero e sem bairro.
- [ ] Se a conta nao tiver pessoa vinculada, preencher o bloco `Seu Cadastro` com CPF ficticio unico.
- [ ] Tentar cadastrar CPF duplicado e confirmar erro claro.
- [ ] Anexar 0, 1 e 10 arquivos validos.
- [ ] Tentar anexar 11 arquivos.
- [ ] Testar anexos `.jpg`, `.png`, `.pdf`, `.doc` e `.docx`.
- [ ] Tentar anexar `.exe`, arquivo vazio e arquivo maior que 5 MB.
- [ ] Confirmar que a solicitacao aparece em `Minhas Solicitacoes` depois de atualizar a pagina.

### Operador

- [ ] Em `/solicitacoes`, criar solicitacao para cidadao existente.
- [ ] Em `/solicitacoes`, criar novo cidadao dentro do modal de nova solicitacao.
- [ ] Filtrar solicitacoes por todos os status.
- [ ] Buscar solicitacoes por bairro, CPF, protocolo/ID e assunto.
- [ ] Alterar status para `ABERTA`, `EM_ANDAMENTO`, `CONCLUIDA`, `ENCERRADA` e `REJEITADA`.
- [ ] Abrir detalhes e baixar anexos de solicitacao.
- [ ] Excluir uma solicitacao criada apenas para teste.
- [ ] Em `/pessoas`, criar, editar, buscar e excluir cidadao ficticio.
- [ ] Em `/pessoas`, tentar nome vazio, CPF vazio, CPF duplicado, CPF com letras e campos so com espacos.
- [ ] Em `/enderecos`, criar, editar, buscar e excluir endereco ficticio.
- [ ] Em `/enderecos`, testar numero comum, `S/N`, referencia vazia e campos obrigatorios vazios.
- [ ] Em `/materiais`, criar, editar, buscar e excluir material ficticio.
- [ ] Em `/materiais`, testar preco normal, `0`, valor muito alto, nome vazio e preco vazio.
- [ ] Em `/demandas`, criar demanda a partir de solicitacao aberta ou em andamento.
- [ ] Em `/demandas`, tentar criar sem solicitacao e sem prazo.
- [ ] Em `/demandas`, testar prazo passado, prazo de hoje e prazo futuro.
- [ ] Em `/demandas`, adicionar materiais com quantidade `1`, `0`, negativa e muito alta.
- [ ] Finalizar uma demanda e confirmar que a solicitacao vinculada muda conforme esperado.

### Agente

- [ ] Acessar `/agente` e confirmar que aparecem somente demandas atribuidas ao agente logado.
- [ ] Abrir demanda atribuida e conferir descricao, localizacao, prazo e status.
- [ ] Adicionar comentario valido.
- [ ] Tentar enviar comentario vazio.
- [ ] Alterar status para `PENDENTE`, `EM_ANDAMENTO`, `CONCLUIDA` e `CANCELADA`.
- [ ] Adicionar material existente a demanda.
- [ ] Alterar quantidade de material para `1`, `0`, negativa e valor alto.
- [ ] Criar material pelo perfil agente e confirmar que aparece para operador.
- [ ] Confirmar no perfil operador que comentarios, status e materiais do agente foram persistidos.

### Admin

- [ ] Em `/usuarios`, buscar usuario por nome e email.
- [ ] Alterar perfil de usuario de teste entre `CIDADAO`, `AGENTE`, `OPERADOR` e `ADMIN`.
- [ ] Ativar e desativar usuario de teste.
- [ ] Confirmar que usuario sem perfil adequado nao acessa rotas protegidas.
- [ ] Em `/auditoria`, consultar entidade e ID depois de alterar solicitacoes, materiais, usuarios e enderecos.
- [ ] Confirmar que auditoria mostra usuario, acao e horario coerentes.

## Cadastros Complementares

### Documentos

- [ ] Criar documento `OFICIO`.
- [ ] Criar documento `MEMORANDO`.
- [ ] Tentar salvar sem assunto, corpo, assinante e interessado.
- [ ] Testar campos opcionais vazios e preenchidos: numero, destino, honorifico e portaria.
- [ ] Editar documento existente.
- [ ] Gerar PDF de documento existente.
- [ ] Excluir documento criado para teste.

### Cemiterios

- [ ] Criar cemiterio com nome e endereco completo.
- [ ] Criar jazigo com quadra, rua, lote, largura e comprimento.
- [ ] Testar quadra, largura e comprimento com zero, negativo, decimal e valor muito alto.
- [ ] Editar jazigo existente.
- [ ] Criar gaveta com ocupante ficticio.
- [ ] Tentar ocupante com CPF duplicado.
- [ ] Conferir ocupacao na lista e no mapa visual.

### Telas Somente Smoke

As telas `Remedios` e `Combustivel` usam estado local no frontend nesta versao. Elas devem ser testadas apenas para navegacao, layout e calculos visuais, sem considerar persistencia apos atualizar a pagina.

- [ ] Abrir tela sem erro.
- [ ] Criar registro visual.
- [ ] Editar registro visual.
- [ ] Excluir registro visual.
- [ ] Atualizar a pagina e registrar que os dados voltam ao estado inicial.

## Testes de Robustez

- [ ] Dar duplo clique no botao salvar/enviar em cadastros principais.
- [ ] Fechar modal com dados preenchidos e reabrir.
- [ ] Atualizar a pagina logo depois de salvar.
- [ ] Testar em desktop e celular.
- [ ] Abrir duas sessoes e alterar o mesmo cadastro para observar conflito ou sobrescrita.
- [ ] Testar buscas com letras minusculas, maiusculas, acentos, numeros e texto inexistente.
- [ ] Confirmar que mensagens de erro nao expõem detalhes tecnicos desnecessarios.

## Registro de Falhas

Copie uma linha para cada problema encontrado.

| Severidade | Perfil | Tela | Dado usado | Esperado | Obtido | Horario | Responsavel | Evidencia |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
|  |  |  |  |  |  |  |  |  |

## Criterios de Liberacao

- Nenhuma falha `Bloqueante` aberta.
- Nenhuma falha `Alta` sem decisao explicita de aceitar risco.
- Cidadao consegue criar solicitacao e acompanhar no portal.
- Operador consegue consultar, filtrar, editar status e criar demanda.
- Agente consegue atualizar demanda atribuida.
- Admin consegue controlar perfis e consultar auditoria.
- Dados validos persistem apos atualizar pagina.
- Dados invalidos retornam erro claro e nao quebram a tela.
