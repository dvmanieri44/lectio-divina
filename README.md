# Liturgia Diária ✝

Aplicativo Android para acompanhar a liturgia católica diária, guiar a Lectio Divina e manter-se conectado com a Palavra de Deus.

---

## Funcionalidades

### Liturgia do Dia
- Consulta automática da liturgia diária via API pública
- Tabs separadas: **Evangelho**, **1ª Leitura**, **Salmo** e **2ª Leitura** (quando houver)
- Indicador de cor litúrgica do dia (branco, vermelho, roxo, verde...)
- Atualização manual com botão de refresh

### Lectio Divina
- Guia completo dos 5 passos: *Lectio, Meditatio, Oratio, Contemplatio, Actio*
- Campos de escrita para cada etapa, com visual acolhedor
- Salvo localmente no banco de dados do aparelho (Room)
- Histórico de todas as entradas, somente leitura para dias anteriores
- Vibração e retorno automático ao salvar

### Citações Grifadas
- Grifo de trechos do Evangelho que te marcaram
- Tela "Minhas Citações" com todas as passagens salvas
- Referência discreta do Evangelho e data em cada citação
- Opção de remover citações individuais

### Notificações
- **Lembrete diário** da Lectio com horário configurável
- Texto personalizado com o nome do usuário: *"E aí {Nome}, não esquece da sua Lectio não!"*
- Referência do Evangelho do dia no lembrete
- **Notificações motivacionais aleatórias** entre 07h-21h, em dias aleatórios da semana (4/7 de chance), com a mensagem *"Ei {nome}, Jesus se lembrou de você"* + uma citação grifada (sem repetição)

### Compartilhar
- Compartilhe o Evangelho do dia via WhatsApp, Telegram, e-mail ou qualquer app instalado

### Onboarding
- Tela de boas-vindas ao primeiro acesso
- Cadastro de nome, idade e gênero para personalização

---

## Stack Técnica

| Camada | Tecnologia |
|---|---|
| UI | Jetpack Compose + Material3 |
| Arquitetura | MVVM + StateFlow |
| Banco de dados | Room (KSP) |
| Rede | Retrofit2 + Gson |
| Notificações | WorkManager + NotificationCompat |
| Linguagem | Kotlin |
| Min SDK | 24 |
| Target SDK | 36 |

---

## API

O app consome a API pública:

```
https://liturgia.up.railway.app/v2/
```

Retorna a liturgia católica do dia atual em português (Brasil).

---

## Estrutura do Projeto

```
app/src/main/java/com/daviapps/liturgiadiaria/
├── data/
│   ├── api/          # Retrofit (LiturgiaApi, RetrofitClient)
│   ├── local/        # Room (LectioDivinaEntity, CitacaoEntity, DAOs, AppDatabase)
│   ├── model/        # Modelos de resposta da API
│   └── repository/   # Repositórios (Liturgia, LectioDivina, Citacao)
├── notification/     # NotificationHelper
├── ui/
│   ├── screen/       # Telas Compose (Liturgia, LectioDivina, Citacoes, Onboarding...)
│   └── theme/        # Tema dourado (Color, Theme, Type)
├── viewmodel/        # ViewModels
├── worker/           # WorkManager (LectioReminder, MotivationalScheduler, MotivationalSend)
└── MainActivity.kt   # Navegação via backstack simples
```

---

## Como Rodar

1. Clone o repositório:
   ```bash
   git clone https://github.com/dvmanieri44/lectio-divina.git
   ```
2. Abra no **Android Studio Hedgehog** ou superior
3. Aguarde a sincronização do Gradle
4. Rode em um emulador ou dispositivo físico (Android 7.0+)

> Nenhuma chave de API ou configuração adicional é necessária.

---

## Licença

Feito de forma altruísta para ajudar todos a se manterem conectados com Deus.  
Dúvidas, sugestões ou elogios: [WhatsApp](https://wa.me/5516997037115)
