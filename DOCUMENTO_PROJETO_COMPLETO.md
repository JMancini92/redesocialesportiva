# 📘 DOCUMENTO MESTRE — Rede Social Esportiva

> **Versão:** 1.0  
> **Data:** 22/05/2026  
> **Autor:** Jaime Mancini  
> **Objetivo:** Contextualizar qualquer IA ou desenvolvedor sobre o projeto completo

---

## 📌 1. VISÃO GERAL DO PROJETO

### 1.1 O que é?
Uma **rede social exclusiva para atletas de artes marciais** (inicialmente focada em Karate Kyokushin, mas expansível para outras modalidades).

### 1.2 Problema que resolve
Atletas amadores e profissionais de artes marciais não têm uma plataforma dedicada para:
- Registrar e compartilhar seus treinos (check-ins)
- Acompanhar streaks de treinos consecutivos
- Encontrar academias e eventos
- Conectar-se com outros atletas da mesma modalidade
- Conquistar reconhecimento através de highlights e conquistas

### 1.3 Público-alvo
| Segmento | Descrição |
|----------|-----------|
| **Atletas amadores** | Treinam regularmente, buscam motivação e comunidade |
| **Atletas profissionais** | Competições, patrocínios, visibilidade |
| **Academias** | Divulgação, gestão de alunos, eventos |
| **Organizadores** | Criação e promoção de campeonatos |

### 1.4 Objetivo Final (MVP)
Uma plataforma funcional com:
- ✅ Cadastro/login de atletas
- ✅ Perfil completo com estatísticas
- ✅ Check-in de treinos com geolocalização
- ✅ Feed social com posts e highlights
- ✅ Sistema de seguidores
- ✅ Busca por modalidade e localização
- ✅ Eventos e competições
- ✅ Notificações

---

## 🏗️ 2. ARQUITETURA TÉCNICA

### 2.1 Stack Completa

```
┌─────────────────────────────────────────────────────────────┐
│                        CLIENTES                              │
├──────────────┬──────────────┬───────────────────────────────┤
│   iOS App    │ Android App  │         Web App               │
│    Swift     │    Kotlin    │        Next.js 16             │
│   SwiftUI    │   Jetpack    │      React + TypeScript       │
│   (futuro)   │   Compose    │      Tailwind CSS v4          │
└──────┬───────┴──────┬───────┴──────────┬────────────────────┘
       │              │                  │
       │         HTTPS/JSON (REST API)   │
       └──────────────┼──────────────────┘
                      │
              ┌───────┴───────┐
              │     KTOR      │
              │    Backend    │
              │  Kotlin 2.1   │
              │   JWT Auth    │
              └───────┬───────┘
                      │
        ┌─────────────┼─────────────┐
        │             │             │
   ┌────┴────┐   ┌───┴───┐   ┌─────┴──────┐
   │PostgreSQL│   │ Redis │   │   MinIO    │
   │   15+   │   │  7+   │   │ (S3-like)  │
   │  Dados  │   │ Cache │   │  Storage   │
   │+ Full-text│  │+ Auth │   │+ Avatars   │
   │  search  │   │tokens │   │+ Media     │
   └─────────┘   └───────┘   └────────────┘
```

### 2.2 Tecnologias Detalhadas

| Camada | Tecnologia | Versão | Função |
|--------|-----------|--------|--------|
| **Frontend Web** | Next.js | 16.2.6 | SSR, App Router, API Routes |
| | React | 19.2.4 | UI library |
| | TypeScript | ^5 | Tipagem estática |
| | Tailwind CSS | v4 | Estilização utilitária |
| | Lucide React | ^1.16.0 | Ícones |
| **Backend API** | Kotlin | 2.1.0 | Linguagem JVM |
| | Ktor | 3.0.3 | Framework web async |
| | Exposed | 0.58.0 | ORM SQL (DSL + DAO) |
| | Koin | 4.0.4 | Injeção de dependência |
| | JWT (java-jwt) | 4.5.0 | Tokens de autenticação |
| | BCrypt (jbcrypt) | 0.4 | Hash de senhas |
| | SendGrid | 4.10.3 | Envio de emails |
| | Prometheus | 1.14.4 | Métricas e monitoramento |
| **Banco de Dados** | PostgreSQL | 15+ | Dados persistentes |
| | Redis | 7+ | Cache + sessões + blacklist JWT |
| **Storage** | MinIO | latest | Arquivos (avatars, highlights) |
| **Proxy** | Nginx | latest | Reverse proxy + SSL |
| **Container** | Docker + Compose | latest | Orquestração local |
| **Mobile Android** | Kotlin | 2.2.10 | Linguagem |
| | Jetpack Compose | BOM 2026.02.01 | UI declarativa |
| | Ktor Client | 2.3.7 | HTTP client |
| | DataStore | 1.0.0 | Persistência local de tokens |
| | Coil | 2.5.0 | Carregamento de imagens |
| | Navigation Compose | 2.7.6 | Navegação entre telas |

---

## 📂 3. ESTRUTURA DE PASTAS

### 3.1 Repositório Web (`rede-social-esportiva`)

```
rede-social-esportiva/
├── 📁 app/                          # Next.js 16 App Router
│   ├── 📄 page.tsx                  # Landing page / Login
│   ├── 📄 layout.tsx                # Root layout (fonte Geist, metadata)
│   ├── 📄 globals.css               # Tailwind v4 + tema dark custom
│   ├── 📁 cadastro/
│   │   └── 📄 page.tsx              # Tela de cadastro
│   ├── 📁 feed/
│   │   └── 📄 page.tsx              # Feed principal
│   └── 📁 perfil/                   # (vazio — telas futuras)
│
├── 📁 lib/
│   └── 📄 api.ts                    # Cliente HTTP para Ktor
│
├── 📁 public/                       # Assets estáticos
│
├── 📁 backend rede social esportiva/
│   ├── 📁 infrastructure/
│   │   ├── 📄 docker-compose.yml    # Postgres + Redis + MinIO + Nginx
│   │   ├── 📄 .env.example          # Template de variáveis
│   │   ├── 📁 migrations/
│   │   │   └── 📄 V1__initial_schema.sql  # Schema completo
│   │   └── 📁 nginx/
│   │       └── 📄 nginx.conf        # Reverse proxy
│   │
│   └── 📁 ktor-server/
│       ├── 📄 build.gradle.kts      # Dependências Gradle
│       ├── 📄 Dockerfile            # Multi-stage build
│       ├── 📄 gradlew / gradlew.bat # Gradle wrapper
│       └── 📁 src/main/kotlin/com/redesocial/
│           ├── 📄 Application.kt    # Entry point
│           ├── 📁 config/
│           │   ├── 📄 DatabaseConfig.kt
│           │   ├── 📄 RedisConfig.kt
│           │   ├── 📄 MinioConfig.kt
│           │   └── 📄 SecurityConfig.kt
│           ├── 📁 di/
│           │   └── 📄 AppModule.kt  # Koin DI modules
│           ├── 📁 models/
│           │   ├── 📁 dto/          # Request/Response DTOs
│           │   │   ├── 📄 AuthDtos.kt
│           │   │   ├── 📄 ProfileDtos.kt
│           │   │   ├── 📄 CheckInDtos.kt
│           │   │   ├── 📄 AcademyDtos.kt
│           │   │   ├── 📄 EventDtos.kt
│           │   │   ├── 📄 HighlightDtos.kt
│           │   │   ├── 📄 FollowDtos.kt
│           │   │   ├── 📄 CommentDtos.kt
│           │   │   ├── 📄 LikeDtos.kt
│           │   │   ├── 📄 FeedDtos.kt
│           │   │   ├── 📄 NotificationDtos.kt
│           │   │   └── 📄 SearchDtos.kt
│           │   └── 📁 enums/        # Enums
│           │       ├── 📄 UserRole.kt
│           │       ├── 📄 EventStatus.kt
│           │       ├── 📄 AcademyStatus.kt
│           │       ├── 📄 MediaType.kt
│           │       └── 📄 ParticipantStatus.kt
│           ├── 📁 repositories/     # Interfaces + Implementações Exposed
│           │   ├── 📄 UserRepository.kt
│           │   ├── 📄 ProfileRepository.kt
│           │   ├── 📄 CheckInRepository.kt
│           │   ├── 📄 AcademyRepository.kt
│           │   ├── 📄 EventRepository.kt
│           │   ├── 📄 HighlightRepository.kt
│           │   ├── 📄 FollowRepository.kt
│           │   ├── 📄 CommentRepository.kt
│           │   ├── 📄 LikeRepository.kt
│           │   ├── 📄 FeedRepository.kt
│           │   ├── 📄 NotificationRepository.kt
│           │   └── 📄 SearchRepository.kt
│           ├── 📁 routes/           # Rotas REST
│           │   ├── 📄 AuthRoutes.kt
│           │   ├── 📄 ProfileRoutes.kt
│           │   ├── 📄 CheckInRoutes.kt
│           │   ├── 📄 AcademyRoutes.kt
│           │   ├── 📄 EventRoutes.kt
│           │   ├── 📄 HighlightRoutes.kt
│           │   ├── 📄 FollowRoutes.kt
│           │   ├── 📄 CommentRoutes.kt
│           │   ├── 📄 LikeRoutes.kt
│           │   ├── 📄 FeedRoutes.kt
│           │   ├── 📄 NotificationRoutes.kt
│           │   └── 📄 SearchRoutes.kt
│           ├── 📁 services/         # Interfaces + Implementações
│           │   ├── 📄 AuthService.kt
│           │   ├── 📄 ProfileService.kt
│           │   ├── 📄 CheckInService.kt
│           │   ├── 📄 AcademyService.kt
│           │   ├── 📄 EventService.kt
│           │   ├── 📄 HighlightService.kt
│           │   ├── 📄 FollowService.kt
│           │   ├── 📄 CommentService.kt
│           │   ├── 📄 LikeService.kt
│           │   ├── 📄 FeedService.kt
│           │   ├── 📄 NotificationService.kt
│           │   ├── 📄 SearchService.kt
│           │   └── 📄 EmailService.kt
│           ├── 📁 plugins/
│           │   ├── 📄 Routing.kt    # Configuração de rotas
│           │   ├── 📄 Security.kt   # JWT, CORS, Rate Limit
│           │   ├── 📄 Monitoring.kt # Prometheus metrics
│           │   ├── 📄 Serialization.kt
│           │   └── 📄 StatusPages.kt # Error handling
│           └── 📁 utils/
│               └── 📄 Exceptions.kt
│
├── 📁 mobile android/               # (apenas build.gradle.kts inicial)
├── 📁 imagens/                      # Referências de UI
├── 📁 resumos/                      # Documentos resumidos
├── 📄 package.json
├── 📄 next.config.ts
├── 📄 tsconfig.json
├── 📄 .env.local                    # Variáveis de ambiente
├── 📄 AGENTS.md                     # Instruções para IAs
├── 📄 README.md
├── 📄 DOCUMENTO_COMPLETO_PARA_IA.md
├── 📄 DOCUMENTO_MESTRE_REDE_SOCIAL_ESPORTIVA.md
└── 📄 PLANEJAMENTO.md
```

### 3.2 Repositório Android (`redesocialesportiva`)

```
redesocialesportiva/
├── 📄 build.gradle.kts              # Build do projeto (root)
├── 📄 settings.gradle.kts           # Configuração do Gradle
├── 📄 gradle.properties             # Propriedades
├── 📁 gradle/
│   └── 📁 wrapper/
│       ├── 📄 gradle-wrapper.jar
│       └── 📄 gradle-wrapper.properties
├── 📄 gradlew / gradlew.bat         # Gradle wrapper
│
└── 📁 app/
    ├── 📄 build.gradle.kts          # Build do módulo app
    ├── 📄 proguard-rules.pro        # Regras de obfuscação
    └── 📁 src/main/
        ├── 📄 AndroidManifest.xml   # Manifesto
        │
        ├── 📁 java/com/jaime/rede_social_esportiva/
        │   ├── 📄 MainActivity.kt   # Activity principal + NavHost
        │   │
        │   ├── 📁 data/
        │   │   ├── 📁 local/
        │   │   │   └── 📄 TokenDataStore.kt   # Persistência de tokens
        │   │   └── 📁 remote/
        │   │       └── 📄 ApiClient.kt        # Ktor client HTTP
        │   │
        │   ├── 📁 presentation/
        │   │   ├── 📁 screens/
        │   │   │   ├── 📄 LoginScreen.kt       # Tela de login
        │   │   │   ├── 📄 RegisterScreen.kt    # Tela de cadastro
        │   │   │   ├── 📄 FeedScreen.kt        # Feed principal
        │   │   │   └── 📄 ProfileScreen.kt     # Perfil do atleta
        │   │   └── 📁 viewmodel/
        │   │       ├── 📄 LoginViewModel.kt
        │   │       └── 📄 RegisterViewModel.kt
        │   │
        │   └── 📁 ui/theme/
        │       ├── 📄 Color.kt      # Paleta de cores
        │       ├── 📄 Theme.kt      # MaterialTheme customizado
        │       └── 📄 Type.kt       # Tipografia
        │
        └── 📁 res/
            ├── 📁 drawable/         # Ícones do launcher
            ├── 📁 mipmap-*/         # Ícones do app (hdpi, mdpi, xhdpi, etc.)
            ├── 📁 values/
            │   ├── 📄 colors.xml
            │   ├── 📄 strings.xml
            │   └── 📄 themes.xml
            └── 📁 xml/              # Backup rules, data extraction rules
```

---

## ✅ 4. ETAPA ATUAL (Status do Projeto)

### 4.1 Fase do Projeto
> **Fase 0 — Fundação (EM ANDAMENTO)**

O backend tem estrutura completa, o frontend web tem telas visuais prontas, e o app Android tem 4 telas implementadas. Ainda há integrações pendentes entre as camadas.

### 4.2 O que já está PRONTO ✅

#### Backend Ktor
| Funcionalidade | Status | Detalhes |
|----------------|--------|----------|
| Estrutura do projeto | ✅ 100% | Todas as pastas, classes, interfaces |
| Configuração de banco | ✅ 100% | HikariCP + Exposed |
| Configuração Redis | ✅ 100% | Lettuce client |
| Configuração MinIO | ✅ 100% | S3-compatible storage |
| Injeção de dependência (Koin) | ✅ 100% | Todos os módulos configurados |
| Schema do banco (SQL) | ✅ 100% | 12 tabelas + índices + função streak |
| Docker Compose | ✅ 100% | Postgres + Redis + MinIO + Nginx |
| Rotas REST | ✅ 100% | Todas as 12 rotas mapeadas |
| DTOs | ✅ 100% | Todos os request/response |
| Repositories | ✅ 100% | Interfaces + implementações Exposed |
| Services | ⚠️ ~80% | Estrutura pronta, alguns TODOs |
| JWT Auth | ⚠️ ~90% | Access + refresh tokens, blacklist no Redis |
| Email service | ✅ 100% | SendGrid configurado |
| Monitoramento | ✅ 100% | Prometheus metrics |

#### Frontend Web (Next.js)
| Funcionalidade | Status | Detalhes |
|----------------|--------|----------|
| Tema dark custom | ✅ 100% | Tailwind v4, cores definidas |
| Tela de Login | ✅ 100% | Design completo, integração com API |
| Tela de Cadastro | ✅ 100% | Design completo, validações |
| Tela de Feed | ⚠️ ~60% | Visual pronto, dados mockados |
| Cliente API | ✅ 100% | `lib/api.ts` com fetch + Bearer token |
| Tela de Perfil | ❌ 0% | Pasta existe mas está vazia |

#### App Android
| Funcionalidade | Status | Detalhes |
|----------------|--------|----------|
| Tema dark custom | ✅ 100% | Cores roxo/azul, MaterialTheme |
| Tela de Login | ✅ 100% | Email, senha, validação, dev bypass |
| Tela de Cadastro | ✅ 100% | Username, email, senha, confirmação |
| Tela de Feed | ✅ 100% | Posts mockados, bottom bar, FAB |
| Tela de Perfil | ✅ 100% | Header, stats, abas, gráfico, atividade |
| Navegação | ✅ 100% | NavHost com 4 telas |
| Armazenamento de tokens | ⚠️ ~50% | DataStore criado, não integrado nos ViewModels |
| Integração com API | ⚠️ ~30% | Login/Register chamam API, resto mockado |

### 4.3 O que está PENDENTE 🔴

#### Crítico (bloqueia lançamento)
| # | Pendência | Por que é crítico |
|---|-----------|-------------------|
| 1 | **Tabela `auth_credentials`** | Login não armazena senhas corretamente |
| 2 | **Permissões nos Services** | Qualquer usuário pode editar/deletar qualquer recurso |
| 3 | **Upload de avatar** | `ProfileServiceImpl.uploadAvatar()` = `NotImplementedError` |
| 4 | **Integração real Feed** | Feed web e Android usam dados mockados |
| 5 | **TokenDataStore nos ViewModels** | Tokens não são salvos no Android |

#### Alto (importante para MVP)
| # | Pendência | Impacto |
|---|-----------|---------|
| 6 | Tela de Busca (web + Android) | Core feature da rede social |
| 7 | Tela de Perfil (web) | Pasta vazia |
| 8 | Check-in (tela + integração) | Feature principal do app |
| 9 | Upload de highlights | Compartilhamento de conquistas |
| 10 | Notificações push/email | Engajamento do usuário |

#### Médio (pós-MVP)
| # | Pendência | Impacto |
|---|-----------|---------|
| 11 | App iOS (SwiftUI) | Expansão de mercado |
| 12 | Algoritmo de sugestão | Descoberta de atletas |
| 13 | Sistema de patrocínios | Monetização |
| 14 | Admin panel | Gestão da plataforma |
| 15 | SSL/Let's Encrypt | Produção segura |

---

## 🚀 5. COMO RODAR O PROJETO (Do Zero)

### 5.1 Pré-requisitos

| Ferramenta | Versão | Download |
|------------|--------|----------|
| Node.js | 18+ | https://nodejs.org |
| Java JDK | 21 | https://adoptium.net |
| Docker + Compose | latest | https://docker.com |
| Android Studio | latest | https://developer.android.com/studio |
| Git | latest | https://git-scm.com |

### 5.2 Passo 1: Clonar os Repositórios

```bash
# Crie a pasta central
mkdir "central rede social esportiva"
cd "central rede social esportiva"

# Clone o projeto web
git clone https://github.com/JMancini92/rede-social-esportiva.git

# Clone o projeto Android
git clone https://github.com/JMancini92/redesocialesportiva.git
```

### 5.3 Passo 2: Rodar a Infraestrutura (Docker)

```bash
cd "rede-social-esportiva/backend rede social esportiva/infrastructure"

# Copie o template de variáveis
cp .env.example .env

# Edite o .env com valores reais (veja seção 5.7)
nano .env  # ou notepad .env no Windows

# Suba os containers
docker-compose up -d

# Verifique se estão rodando
docker-compose ps
```

**Serviços que serão iniciados:**
| Serviço | Porta | Função |
|---------|-------|--------|
| PostgreSQL | 5432 | Banco de dados |
| Redis | 6379 | Cache e sessões |
| MinIO | 9000 / 9001 | Storage de arquivos |
| Nginx | 80 / 443 | Reverse proxy |

### 5.4 Passo 3: Rodar o Backend Ktor

```bash
cd "rede-social-esportiva/backend rede social esportiva/ktor-server"

# Build
./gradlew build

# Rodar (precisa do Docker rodando)
./gradlew run

# O servidor iniciará em http://localhost:8080
```

**Endpoints de health check:**
- `GET http://localhost:8080/health` → deve retornar `{"status":"ok"}`

### 5.5 Passo 4: Rodar o Frontend Web

```bash
cd "rede-social-esportiva"

# Instalar dependências
npm install

# Rodar em modo desenvolvimento
npm run dev

# Acessar http://localhost:3000
```

### 5.6 Passo 5: Rodar o App Android

```bash
# Abra o Android Studio
# File → Open → selecione a pasta "redesocialesportiva"
# Aguarde o Gradle sincronizar
# Clique no botão "Run" (▶️) ou pressione Shift+F10
```

**Para testar sem o backend:**
- Na tela de login, clique em **"⚡ Entrar sem login (dev)"**
- Isso navega direto para o feed com dados mockados

**Para testar com o backend:**
- O backend deve estar rodando em `localhost:8080`
- O app aponta para `http://10.0.2.2:8080` (localhost do emulador)
- Use o emulador Android, não funciona em device físico sem ajustar IP

### 5.7 Variáveis de Ambiente (.env)

```env
# Database
DB_URL=jdbc:postgresql://postgres:5432/redesocial
DB_USER=postgres
DB_PASSWORD=sua_senha_segura

# Redis
REDIS_URL=redis://redis:6379

# MinIO
MINIO_ENDPOINT=http://minio:9000
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin
MINIO_BUCKET=redesocial

# JWT
JWT_SECRET=sua_chave_jwt_super_secreta_minimo_32_caracteres
JWT_ISSUER=redesocial
JWT_AUDIENCE=redesocial-users

# Email (SendGrid)
SENDGRID_API_KEY=SG.sua_chave_aqui
EMAIL_FROM=nao-responda@redesocial.com

# App
ENV=development
```

---

## 🎯 6. DECISÕES ARQUITETURAIS IMPORTANTES

### 6.1 Autenticação
- **JWT custom** com access token (15 min) + refresh token (7 dias)
- **Blacklist no Redis** para invalidação de tokens
- **Sem Supabase Auth** — migramos para auth própria no Ktor
- **BCrypt** para hash de senhas

### 6.2 Segurança
- **Sem RLS do Supabase** — regras de acesso nos Services Ktor
- **CORS configurado** para origens específicas
- **Rate limiting** em endpoints sensíveis
- **Geo protegida** — coordenadas de check-in nunca expostas publicamente

### 6.3 Storage
- **MinIO** (S3-compatible) para arquivos privados
- **URLs presigned** para acesso temporário (não públicas)
- **Avatares e highlights** armazenados no MinIO

### 6.4 Banco de Dados
- **PostgreSQL** com schema relacional completo
- **Exposed ORM** com DSL Kotlin (não SQL raw)
- **Migrations** com Flyway (arquivos V1__, V2__, etc.)
- **Índices** otimizados para buscas frequentes
- **Full-text search** preparado no schema (não implementado ainda)

### 6.5 Mobile
- **Tema dark fixo** — não segue sistema (decisão de design)
- **Cleartext habilitado** — para desenvolvimento local com HTTP
- **DataStore** para tokens (preferido sobre SharedPreferences)
- **Ktor Client** para comunicação com backend

---

## 📊 7. SCHEMA DO BANCO DE DADOS (Resumo)

### 7.1 Tabelas Principais

| Tabela | Função |
|--------|--------|
| `users` | Dados básicos de autenticação |
| `auth_credentials` | Senhas hash (TODO: verificar se existe) |
| `profiles` | Perfil público do atleta |
| `academies` | Academias cadastradas |
| `events` | Eventos e competições |
| `check_ins` | Check-ins de treino |
| `highlights` | Conquistas e destaques |
| `follows` | Relacionamentos (seguidores) |
| `comments` | Comentários em posts |
| `likes` | Curtidas em posts |
| `notifications` | Notificações do usuário |
| `media` | Arquivos de mídia |

### 7.2 Funções do Banco

| Função | Descrição |
|--------|-----------|
| `calculate_streak(user_id)` | Calcula dias consecutivos de treino |

---

## 🔗 8. LINKS ÚTEIS

| Recurso | URL |
|---------|-----|
| Repo Web | https://github.com/JMancini92/rede-social-esportiva |
| Repo Android | https://github.com/JMancini92/redesocialesportiva |
| Documento de hoje | `ALTERACOES_HOJE_22_05_2026.md` no repo Android |

---

## 💬 COMO CONTINUAR ESTE PROJETO COM UMA IA

Cole o seguinte texto no início da conversa:

> "Estou trabalhando na Rede Social Esportiva, um projeto para atletas de artes marciais. O projeto tem 3 partes: (1) Frontend Web em Next.js 16 + React + TypeScript + Tailwind v4, (2) Backend API em Kotlin + Ktor + PostgreSQL + Redis + MinIO, (3) App Android em Kotlin + Jetpack Compose. Os repositórios estão em github.com/JMancini92/rede-social-esportiva (web) e github.com/JMancini92/redesocialesportiva (Android). Leia o DOCUMENTO_PROJETO_COMPLETO.md para contexto completo."

---

*Documento gerado em 22/05/2026. Atualize conforme o projeto evolui.*
