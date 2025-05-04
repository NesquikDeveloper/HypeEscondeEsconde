# 📌 HypeEscondeEsconde — Minigame de Esconde-Esconde (Open Source)

**HypeEscondeEsconde** é um minigame de *Esconde-Esconde* para servidores de Minecraft, inspirado no estilo da SkyCraft, com o código-fonte totalmente gratuito e disponível no GitHub.
Este projeto tem como base o plugin [kSkyWars](https://github.com/NesquikDeveloper/kSkyWars) e está sendo convertido para um sistema funcional de Hide and Seek com mecânicas exclusivas inacabado.

---

## 🎯 Objetivo do Projeto

Converter o plugin **kSkyWars** em um minigame de Esconde-Esconde, mantendo a estrutura base do projeto e adaptando as funcionalidades necessárias para o novo modo de jogo.

---

## ⚙️ Fases do Jogo

### 🟢 Fase de Esconderijo

* **Duração:** 60 segundos
* **Objetivo:** Jogadores do time *Escondedor* devem se esconder pelo mapa.

### 🔴 Fase de Caça

* **Duração:** 540 segundos (9 minutos)
* **Objetivo:** Jogadores do time *Pegador* devem encontrar e eliminar os escondedores.

---

## 🏁 Condições de Vitória

* **Pegadores vencem:** Quando **todos os escondedores forem pegos**.
* **Escondedores vencem:** Se **ao menos um sobrevivente permanecer até o final do tempo**.

---

## 🔁 Mecânica de Conversão

* Quando um escondedor é pego (morto), ele é **automaticamente transferido para o time dos pegadores** e recebe o kit de pegador.
* Isso cria uma dinâmica progressiva onde o time dos pegadores vai crescendo ao longo da partida.

---

## 🧰 Kits

### Kit dos Pegadores

* Arco com **Flecha Infinita**
* Vara com **dano máximo**
* Peitoral, calça e botas de **couro vermelhos**

### Kit dos Escondedores

* Vara com **Knockback III**
* Peitoral, calça e botas de **couro verdes**

---

## ⏱ Temporizadores

* **Fase de Esconderijo:** `60 segundos`
* **Fase de Caça:** `540 segundos`
* No fim da contagem, a lógica de vitória é automaticamente aplicada.

---

## ⚙️ Configurações

Todas as configurações serão definidas no arquivo `config.yml`, incluindo:

* Duração das fases
* Quantidade inicial de pegadores
* Mensagens personalizadas
* Itens dos kits

---

## 📝 Tarefas do Projeto

* [ ] Clonar o repositório [kSkyWars](https://github.com/NesquikDeveloper/kSkyWars)
* [ ] Renomear o projeto para **kHideAndSeek**
* [ ] Remover lógica relacionada a SkyWars
* [ ] Implementar fases de jogo e kits
* [ ] Implementar mecânica de conversão
* [ ] Adicionar scoreboard dinâmica no lobby e durante o jogo
* [ ] Exibir ranking de pegadores com contador de kills (ex: Pegadores: Player1 (3), Player2 (1))
* [ ] Testar localmente em servidor Spigot 1.8.8

---

## 🧪 Requisitos Técnicos

* Compatibilidade com **Minecraft 1.8.8**
* **Scoreboard** com informações em tempo real (tempo restante, número de vivos, etc.)
* Suporte a **PlaceholderAPI** para futuras expansões
* Estrutura de código baseada em eventos e modular, fácil de manter

---

## 📎 Link do Projeto Base

* GitHub: [https://github.com/NesquikDeveloper/kSkyWars](https://github.com/NesquikDeveloper/kSkyWars)

---

## 💡 Observação Final

O **HypeEscondeEsconde** será um minigame leve, responsivo e divertido, com suporte para servidores focados em jogos rápidos e dinâmicos. Seu código estará disponível publicamente, permitindo que outros desenvolvedores contribuam e adaptem o projeto para suas próprias redes.
