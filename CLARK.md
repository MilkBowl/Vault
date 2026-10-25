# ClarkVault

Fork completo de https://github.com/MilkBowl/Vault (inclui o histórico Git),
com chat, permissões, comandos, conversão e integrações do projeto original.
Os adaptadores legados em lib/ são dependências de compilação do próprio projeto;
não são instalados no servidor. As fontes da dependência VaultAPI também foram
incluídas, conforme o contrato e a licença utilizados pelo Vault original.

Convertido para Gradle Wrapper 8.7, Java 8/Spigot 1.8.8; o build usa JDK 17.
Execute `./gradlew build`. O JAR final é `build/libs/ClarkVault-1.7.3.jar`, copiado
automaticamente para `../../servers/rankup/plugins/`.

O plugin chama-se ClarkVault e usa plugins/ClarkVault. A economia própria e os
saldos ficam no ClarkAPI; este fork mantém o papel original de ponte Vault.
A API `net.milkbowl.vault.*` e os comandos `/vault-info` e `/vault-convert`
foram preservados. Plugins que verificam literalmente getPlugin("Vault")
precisam aceitar ClarkVault; não há injeção de aliases no PluginManager.

Licença e créditos originais preservados em license.txt e nos cabeçalhos.
As verificações de atualização do Vault original ficam desativadas por padrão.
