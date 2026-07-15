---
apply: always
---

# Regras obrigatórias do projeto

## Ambiente

- Minecraft: 1.21.1
- Loader: NeoForge
- Java: 21
- IDE: IntelliJ IDEA
- Build: Gradle
- Não aplicar soluções de Fabric, Forge antigo ou Minecraft 1.21.4+.
- Não inventar classes, métodos, eventos ou APIs.
- Antes de usar uma API, verificar se ela existe nas dependências atuais do projeto.

## Processo obrigatório

Antes de alterar código:

1. Inspecione a estrutura real do projeto.
2. Localize as classes relacionadas ao sistema.
3. Verifique build.gradle, gradle.properties e neoforge.mods.toml.
4. Identifique a causa real do problema.
5. Explique resumidamente o plano.
6. Só depois implemente a alteração.

## Regras de implementação

- Preserve sistemas que já funcionam.
- Evite reescrever classes inteiras sem necessidade.
- Faça alterações pequenas, localizadas e compatíveis.
- Não duplique eventos, registradores, capabilities, attachments ou tick handlers.
- Não crie soluções temporárias ou hardcoded quando uma solução estrutural for possível.
- Respeite client-side e server-side.
- Não acesse classes client-only pelo servidor dedicado.
- Evite executar lógica pesada a cada tick.
- Não use reflexão, mixins ou access transformers sem necessidade real.
- Ao usar mixin, justificar por que eventos ou APIs públicas não resolvem.

## Minecraft e NeoForge

- Usar ResourceLocation.fromNamespaceAndPath quando apropriado para 1.21.1.
- Validar nomes dos registries e caminhos de recursos.
- Manter mod_id consistente em código, JSON, assets, data e neoforge.mods.toml.
- Não confundir assets/<modid>/models/item com estruturas de versões posteriores.
- Verificar sincronização de dados entre cliente e servidor.
- Pacotes de rede devem validar lado, player, distância e dados recebidos.
- Toda ação importante deve ser controlada pelo servidor.
- Não confiar em dados enviados pelo cliente.

## Qualidade

- Não deixar imports não utilizados.
- Não deixar código comentado ou métodos abandonados.
- Não usar catch vazio.
- Registrar erros importantes de forma clara.
- Adicionar comentários somente em trechos realmente complexos.
- Preservar compatibilidade com mundos existentes sempre que possível.
- Configurações novas devem ter valores padrão seguros.

## Build e validação

Depois de implementar:

1. Execute `gradlew clean build`.
2. Corrija todos os erros de compilação.
3. Verifique warnings relevantes.
4. Revise os arquivos alterados.
5. Confirme que não foram adicionados arquivos gerados ao Git.
6. Informe qualquer parte que não pôde ser testada.

## Relatório final obrigatório

Ao terminar, apresentar:

- Causa do problema.
- Solução implementada.
- Arquivos criados.
- Arquivos alterados.
- Comandos executados.
- Resultado do build.
- Riscos ou limitações.
- O que testar dentro do Minecraft.