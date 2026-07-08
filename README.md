# Easy Reminder 📝

App Android de gerenciamento de lembretes com categorias, desenvolvido como projeto de portfólio.

##  Funcionalidades

- Criar, editar e excluir lembretes
- Categorizar lembretes com cores personalizadas
- Tema claro e escuro
- Persistência local de dados

## Arquitetura & Tecnologias

- **Linguagem:** Kotlin
- **Arquitetura:** MVVM (Model-View-ViewModel)
- **Banco de dados:** Room
- **Navegação:** Navigation Component + Safe Args
- **UI:** XML Views + Material Design 3
- **Ciclo de vida:** ViewModel + LiveData
- **Controle de versão:** Git + GitHub

## Estrutura do projeto

- **data/local** — DAOs e AppDatabase
- **data/repository** — Repositories
- **model** — Entidades Room
- **ui/reminder** — Fragments e Adapter de lembretes
- **ui/settings** — Fragment e Adapter de configurações
- **viewmodel** — ViewModels