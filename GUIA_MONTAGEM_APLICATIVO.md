# 🔧 GUIA DE MONTAGEM DO APLICATIVO

> **Versão:** 1.0  
> **Data:** 22/05/2026  
> **Objetivo:** Explicar passo a passo como montar cada parte do aplicativo (backend, web, Android, iOS)

---

## 📋 ÍNDICE

1. [Visão Geral da Arquitetura](#1-visão-geral-da-arquitetura)
2. [Backend (Ktor + PostgreSQL + Redis + MinIO)](#2-backend)
3. [Frontend Web (Next.js)](#3-frontend-web)
4. [App Android (Kotlin + Jetpack Compose)](#4-app-android)
5. [App iOS (Swift + SwiftUI) — Futuro](#5-app-ios)
6. [Integração entre as Partes](#6-integração)
7. [Deploy e Produção](#7-deploy)

---

## 1. VISÃO GERAL DA ARQUITETURA

```
┌─────────────────────────────────────────────────────────────────┐
│                         CLIENTES                                 │
├─────────────┬─────────────────┬─────────────────────────────────┤
│   iOS App   │   Android App   │           Web App               │
│   SwiftUI   │  Jetpack Compose│      Next.js 16 + React         │
│   (futuro)  │    (em andamento)│      (em andamento)            │
└──────┬──────┴────────┬────────┴─────────────┬───────────────────┘
       │               │                      │
       │      HTTPS/JSON (REST API)          │
       └───────────────┼──────────────────────┘
                       │
              ┌────────┴────────┐
              │  KTOR BACKEND   │
              │  Kotlin 2.1     │
              │  JWT Auth       │
              │  Porta 8080     │
              └────────┬────────┘
                       │
        ┌──────────────┼──────────────┐
        │              │              │
   ┌────┴────┐    ┌───┴───┐    ┌─────┴──────┐
   │PostgreSQL│    │ Redis │    │   MinIO    │
   │  5432   │    │ 6379  │    │ 9000/9001  │
   │ (Dados) │    │(Cache)│    │ (Storage)  │
   └─────────┘    └───────┘    └────────────┘
```

**Ordem de montagem recomendada:**
1. Infraestrutura (Docker)
2. Backend (Ktor)
3. Frontend Web (Next.js)
4. App Android
5. App iOS (futuro)

---

## 2. BACKEND

### 2.1 Tecnologias

| Componente | Tecnologia | Função |
|------------|-----------|--------|
| Linguagem | Kotlin 2.1 | Código do servidor |
| Framework | Ktor 3.0.3 | Servidor web async |
| ORM | Exposed 0.58.0 | Acesso ao banco de dados |
| DI | Koin 4.0.4 | Injeção de dependência |
| Auth | JWT (java-jwt) | Tokens de autenticação |
| Password | BCrypt (jbcrypt) | Hash de senhas |
| Email | SendGrid | Envio de emails |
| Metrics | Prometheus | Monitoramento |

### 2.2 Estrutura de Pastas do Backend

```
backend rede social esportiva/ktor-server/
├── src/main/kotlin/com/redesocial/
│   ├── Application.kt              ← Entry point (main)
│   ├── config/                     ← Configurações
│   │   ├── DatabaseConfig.kt       ← HikariCP + PostgreSQL
│   │   ├── RedisConfig.kt          ← Lettuce client
│   │   ├── MinioConfig.kt          ← S3 client
│   │   └── SecurityConfig.kt       ← JWT settings
│   ├── di/
│   │   └── AppModule.kt            ← Koin modules
│   ├── models/
│   │   ├── dto/                    ← Data Transfer Objects
│   │   │   ├── AuthDtos.kt         ← LoginRequest, RegisterRequest, TokenResponse
│   │   │   ├── ProfileDtos.kt      ← ProfileResponse, UpdateProfileRequest
│   │   │   ├── CheckInDtos.kt      ← CheckInRequest, CheckInResponse
│   │   │   ├── AcademyDtos.kt      ← AcademyRequest, AcademyResponse
│   │   │   ├── EventDtos.kt        ← EventRequest, EventResponse
│   │   │   ├── HighlightDtos.kt    ← HighlightRequest, HighlightResponse
│   │   │   ├── FollowDtos.kt       ← FollowRequest, FollowResponse
│   │   │   ├── CommentDtos.kt      ← CommentRequest, CommentResponse
│   │   │   ├── LikeDtos.kt         ← LikeRequest, LikeResponse
│   │   │   ├── FeedDtos.kt         ← FeedItemResponse, FeedPageResponse
│   │   │   ├── NotificationDtos.kt ← NotificationResponse
│   │   │   └── SearchDtos.kt       ← SearchRequest, SearchResponse
│   │   └── enums/
│   │       ├── UserRole.kt         ← ADMIN, ATHLETE, ACADEMY_OWNER
│   │       ├── EventStatus.kt      ← DRAFT, PUBLISHED, CANCELLED, COMPLETED
│   │       ├── AcademyStatus.kt    ← PENDING, ACTIVE, SUSPENDED
│   │       ├── MediaType.kt        ← IMAGE, VIDEO, DOCUMENT
│   │       └── ParticipantStatus.kt ← REGISTERED, CHECKED_IN, NO_SHOW
│   ├── repositories/               ← Acesso ao banco (Exposed)
│   │   ├── UserRepository.kt       ← CRUD de usuários
│   │   ├── ProfileRepository.kt    ← CRUD de perfis
│   │   ├── CheckInRepository.kt    ← CRUD de check-ins
│   │   ├── AcademyRepository.kt    ← CRUD de academias
│   │   ├── EventRepository.kt      ← CRUD de eventos
│   │   ├── HighlightRepository.kt  ← CRUD de highlights
│   │   ├── FollowRepository.kt     ← CRUD de seguidores
│   │   ├── CommentRepository.kt    ← CRUD de comentários
│   │   ├── LikeRepository.kt       ← CRUD de curtidas
│   │   ├── FeedRepository.kt       ← Queries do feed
│   │   ├── NotificationRepository.kt ← CRUD de notificações
│   │   └── SearchRepository.kt     ← Queries de busca
│   ├── routes/                     ← Rotas REST (controllers)
│   │   ├── AuthRoutes.kt           ← POST /auth/login, /auth/register, /auth/refresh
│   │   ├── ProfileRoutes.kt        ← GET/PUT /profiles/{id}, /profiles/me
│   │   ├── CheckInRoutes.kt        ← POST /checkins, GET /checkins/{id}
│   │   ├── AcademyRoutes.kt        ← CRUD /academies
│   │   ├── EventRoutes.kt          ← CRUD /events, /events/{id}/participants
│   │   ├── HighlightRoutes.kt      ← CRUD /highlights, upload de mídia
│   │   ├── FollowRoutes.kt         ← POST /follows, DELETE /follows
│   │   ├── CommentRoutes.kt        ← CRUD /comments
│   │   ├── LikeRoutes.kt           ← POST /likes, DELETE /likes
│   │   ├── FeedRoutes.kt           ← GET /feed, /feed/me
│   │   ├── NotificationRoutes.kt   ← GET /notifications, PUT /notifications/read
│   │   └── SearchRoutes.kt         ← GET /search?query=&type=
│   ├── services/                   ← Lógica de negócio
│   │   ├── AuthService.kt          ← Validação de credenciais, geração de tokens
│   │   ├── ProfileService.kt       ← Gerenciamento de perfis, upload de avatar
│   │   ├── CheckInService.kt       ← Regras de check-in (limite 3/dia, streak)
│   │   ├── AcademyService.kt       ← Validação de academias
│   │   ├── EventService.kt         ← Gestão de eventos e participantes
│   │   ├── HighlightService.kt     ← Destaques e conquistas
│   │   ├── FollowService.kt        ← Seguir/deixar de seguir
│   │   ├── CommentService.kt       ← Comentários em posts
│   │   ├── LikeService.kt          ← Curtidas
│   │   ├── FeedService.kt          ← Algoritmo do feed
│   │   ├── NotificationService.kt  ← Criação e envio de notificações
│   │   ├── SearchService.kt        ← Lógica de busca
│   │   └── EmailService.kt         ← Envio de emails (SendGrid)
│   ├── plugins/                    ← Configurações do Ktor
│   │   ├── Routing.kt              ← Registro de todas as rotas
│   │   ├── Security.kt             ← JWT, CORS, Rate Limit
│   │   ├── Monitoring.kt           ← Métricas Prometheus
│   │   ├── Serialization.kt        ← JSON (Kotlinx Serialization)
│   │   └── StatusPages.kt          ← Tratamento de erros HTTP
│   └── utils/
│       └── Exceptions.kt           ← Exceções customizadas
└── src/main/resources/
    ├── application.conf            ← Configuração HOCON (porta, db, jwt)
    └── logback.xml                 ← Configuração de logs
```

### 2.3 Como Montar o Backend (Passo a Passo)

#### Passo 1: Subir a Infraestrutura

```bash
cd "backend rede social esportiva/infrastructure"

# Copiar template de variáveis
cp .env.example .env

# Editar .env com valores reais
# (veja seção 2.4 abaixo)

# Subir containers
docker-compose up -d

# Verificar status
docker-compose ps
```

#### Passo 2: Compilar o Backend

```bash
cd "backend rede social esportiva/ktor-server"

# Build
./gradlew build

# Se der erro, verifique:
# - JDK 21 está instalado? (java -version)
# - Docker está rodando?
# - Variáveis de ambiente estão configuradas?
```

#### Passo 3: Rodar o Backend

```bash
# Opção 1: Via Gradle (desenvolvimento)
./gradlew run

# Opção 2: Via Docker (produção)
docker build -t rede-social-backend .
docker run -p 8080:8080 --env-file ../infrastructure/.env rede-social-backend

# O servidor estará em http://localhost:8080
```

#### Passo 4: Testar

```bash
# Health check
curl http://localhost:8080/health

# Deve retornar: {"status":"ok"}

# Testar registro
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"teste","email":"teste@email.com","password":"123456","fullName":"Teste"}'

# Testar login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"teste@email.com","password":"123456"}'
```

### 2.4 Variáveis de Ambiente do Backend

```env
# ============================================
# DATABASE (PostgreSQL)
# ============================================
DB_URL=jdbc:postgresql://postgres:5432/redesocial
DB_USER=postgres
DB_PASSWORD=sua_senha_segura_aqui

# ============================================
# REDIS (Cache + Sessões + Blacklist JWT)
# ============================================
REDIS_URL=redis://redis:6379

# ============================================
# MINIO (Storage S3-compatible)
# ============================================
MINIO_ENDPOINT=http://minio:9000
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin
MINIO_BUCKET=redesocial

# ============================================
# JWT (Autenticação)
# ============================================
# Mínimo 32 caracteres! Use: openssl rand -base64 32
JWT_SECRET=sua_chave_jwt_super_secreta_com_no_minimo_32_caracteres
JWT_ISSUER=redesocial
JWT_AUDIENCE=redesocial-users

# ============================================
# EMAIL (SendGrid)
# ============================================
SENDGRID_API_KEY=SG.sua_chave_aqui
EMAIL_FROM=nao-responda@redesocial.com

# ============================================
# APP
# ============================================
ENV=development          # development | staging | production
SERVER_PORT=8080
```

### 2.5 API Endpoints (Resumo)

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | `/auth/register` | Cadastro de usuário | ❌ |
| POST | `/auth/login` | Login | ❌ |
| POST | `/auth/refresh` | Refresh token | ❌ |
| POST | `/auth/logout` | Logout | ✅ |
| GET | `/profiles/me` | Meu perfil | ✅ |
| GET | `/profiles/{id}` | Perfil de usuário | ✅ |
| PUT | `/profiles/me` | Atualizar perfil | ✅ |
| POST | `/profiles/me/avatar` | Upload avatar | ✅ |
| POST | `/checkins` | Fazer check-in | ✅ |
| GET | `/checkins` | Meus check-ins | ✅ |
| GET | `/feed` | Feed público | ✅ |
| GET | `/feed/me` | Feed pessoal | ✅ |
| POST | `/follows` | Seguir usuário | ✅ |
| DELETE | `/follows/{id}` | Deixar de seguir | ✅ |
| GET | `/search` | Buscar | ✅ |
| GET | `/notifications` | Notificações | ✅ |
| PUT | `/notifications/read` | Marcar como lida | ✅ |

### 2.6 Decisões Técnicas do Backend

| Decisão | Justificativa |
|---------|---------------|
| **Ktor em vez de Spring** | Mais leve, Kotlin-first, coroutines nativas |
| **Exposed em vez de JPA** | DSL Kotlin idiomático, mais controle |
| **JWT com refresh token** | Segurança + UX (não pede login a cada 15min) |
| **Redis para blacklist** | Invalidação instantânea de tokens |
| **MinIO em vez de S3 AWS** | Custo zero em desenvolvimento, API compatível |
| **Koin em vez de Dagger** | Mais simples, DSL Kotlin-friendly |

---

## 3. FRONTEND WEB

### 3.1 Tecnologias

| Componente | Tecnologia | Função |
|------------|-----------|--------|
| Framework | Next.js 16 | SSR, App Router, API Routes |
| UI Library | React 19 | Componentes |
| Linguagem | TypeScript | Tipagem estática |
| Estilos | Tailwind CSS v4 | Utility-first CSS |
| Ícones | Lucide React | Ícones SVG |
| Fonte | Geist | Tipografia (Vercel) |

### 3.2 Estrutura de Pastas

```
rede-social-esportiva/
├── app/                          ← App Router (Next.js 16)
│   ├── page.tsx                  ← Landing page / Login
│   ├── layout.tsx                ← Root layout (metadata, fonte)
│   ├── globals.css               ← Tailwind + tema dark
│   ├── cadastro/
│   │   └── page.tsx              ← Tela de cadastro
│   ├── feed/
│   │   └── page.tsx              ← Feed principal
│   └── perfil/                   ← (vazio — implementar)
│
├── lib/
│   └── api.ts                    ← Cliente HTTP
├── public/                       ← Assets estáticos
├── package.json
├── next.config.ts
└── tsconfig.json
```

### 3.3 Como Montar o Frontend

```bash
cd "rede-social-esportiva"

# Instalar dependências
npm install

# Configurar variáveis de ambiente
# Crie/editar .env.local:
echo "NEXT_PUBLIC_API_URL=http://localhost:8080" > .env.local

# Rodar em desenvolvimento
npm run dev

# Acessar http://localhost:3000
```

### 3.4 Cliente API (`lib/api.ts`)

```typescript
// Resumo da implementação:
class ApiClient {
  private baseUrl = process.env.NEXT_PUBLIC_API_URL;
  
  // Métodos principais:
  async login(email: string, password: string): Promise<TokenResponse>
  async register(data: RegisterRequest): Promise<UserResponse>
  async refreshToken(refreshToken: string): Promise<TokenResponse>
  async getProfile(): Promise<ProfileResponse>
  async getFeed(): Promise<FeedItem[]>
  
  // Interceptores:
  // - Adiciona Bearer token automaticamente
  // - Faz refresh token quando 401
  // - Lida com erros HTTP
}

export const api = new ApiClient();
```

### 3.5 Telas a Implementar (Web)

| Tela | Status | Prioridade |
|------|--------|------------|
| Login | ✅ Pronta | — |
| Cadastro | ✅ Pronta | — |
| Feed | ⚠️ Mockado | Alta |
| Perfil | ❌ Vazio | Alta |
| Busca | ❌ Não existe | Alta |
| Check-in | ❌ Não existe | Alta |
| Notificações | ❌ Não existe | Média |
| Configurações | ❌ Não existe | Média |

### 3.6 Tema Visual (Cores)

```css
/* Tailwind config (globals.css) */
--background: #0A0A0F;      /* Fundo principal */
--surface: #111118;         /* Cards */
--surface-light: #16161F;   /* Elementos elevados */
--primary: #7C3AED;         /* Roxo principal */
--primary-light: #A78BFA;   /* Roxo claro */
--accent: #3B82F6;          /* Azul */
--text-primary: #F5F5F5;    /* Texto principal */
--text-secondary: #9CA3AF;  /* Texto secundário */
--border: #2A2A3A;          /* Bordas */
--error: #EF4444;           /* Erros */
--success: #22C55E;         /* Sucesso */
```

---

## 4. APP ANDROID

### 4.1 Tecnologias

| Componente | Tecnologia | Função |
|------------|-----------|--------|
| Linguagem | Kotlin 2.2.10 | Código nativo Android |
| UI | Jetpack Compose | UI declarativa |
| Design | Material Design 3 | Componentes de UI |
| Navegação | Navigation Compose | Navegação entre telas |
| HTTP | Ktor Client 2.3.7 | Comunicação com API |
| JSON | Kotlinx Serialization | Parse de JSON |
| Imagens | Coil 2.5.0 | Carregamento de imagens |
| Storage | DataStore 1.0.0 | Tokens e preferências |
| Estado | ViewModel Compose | Gerenciamento de estado |

### 4.2 Estrutura de Pastas

```
redesocialesportiva/
├── app/src/main/java/com/jaime/rede_social_esportiva/
│   ├── MainActivity.kt           ← Entry point + NavHost
│   ├── data/
│   │   ├── local/
│   │   │   └── TokenDataStore.kt ← Salvar/recuperar tokens
│   │   └── remote/
│   │       └── ApiClient.kt      ← Ktor client HTTP
│   ├── presentation/
│   │   ├── screens/
│   │   │   ├── LoginScreen.kt    ← Tela de login
│   │   │   ├── RegisterScreen.kt ← Tela de cadastro
│   │   │   ├── FeedScreen.kt     ← Feed principal
│   │   │   └── ProfileScreen.kt  ← Perfil do atleta
│   │   └── viewmodel/
│   │       ├── LoginViewModel.kt ← Lógica de login
│   │       └── RegisterViewModel.kt ← Lógica de cadastro
│   └── ui/theme/
│       ├── Color.kt              ← Paleta de cores
│       ├── Theme.kt              ← MaterialTheme custom
│       └── Type.kt               ← Tipografia
```

### 4.3 Como Montar o App Android

#### Pré-requisitos
- Android Studio (com suporte a AGP 9.2.1)
- JDK 11+
- Emulador Android ou dispositivo físico

#### Passo 1: Abrir no Android Studio
```
File → Open → selecione a pasta "redesocialesportiva"
Aguarde o Gradle sincronizar (pode demorar na primeira vez)
```

#### Passo 2: Configurar o Emulador
```
Tools → Device Manager → Create Device
Recomendado: Pixel 7 API 36
```

#### Passo 3: Rodar
```
Clique no botão "Run" (▶️) ou pressione Shift+F10
```

#### Passo 4: Testar
- **Sem backend:** Clique em "⚡ Entrar sem login (dev)"
- **Com backend:** Use o emulador (aponta para `10.0.2.2:8080`)

### 4.4 Arquitetura do App

```
┌─────────────────────────────────────────┐
│           UI Layer (Screens)            │
│  LoginScreen, RegisterScreen,           │
│  FeedScreen, ProfileScreen              │
│  → Composables @Composable              │
├─────────────────────────────────────────┤
│       Presentation Layer (ViewModel)    │
│  LoginViewModel, RegisterViewModel      │
│  → Estado com mutableStateOf            │
│  → Lógica de validação                  │
├─────────────────────────────────────────┤
│           Data Layer                    │
│  ┌─────────────┐  ┌─────────────────┐  │
│  │   Remote    │  │     Local       │  │
│  │  ApiClient  │  │  TokenDataStore │  │
│  │  (Ktor)     │  │  (DataStore)    │  │
│  │  HTTP calls │  │  Key-Value      │  │
│  └─────────────┘  └─────────────────┘  │
└─────────────────────────────────────────┘
```

### 4.5 Telas Implementadas

| Tela | Arquivo | Funcionalidades |
|------|---------|----------------|
| **Login** | `LoginScreen.kt` (367 linhas) | Email, senha, toggle visibilidade, validação, dev bypass, login social placeholder, navegação para cadastro |
| **Cadastro** | `RegisterScreen.kt` (393 linhas) | Username, email, senha, confirmação, validações, login social placeholder, navegação para login |
| **Feed** | `FeedScreen.kt` (362 linhas) | TopAppBar com logo, BottomAppBar (Home/Buscar/Perfil), FAB novo post, posts mockados com avatar, conteúdo, foto placeholder, curtidas, comentários |
| **Perfil** | `ProfileScreen.kt` (1216 linhas) | Header com foto/borda gradiente, badge verificado, status atividade, card patrocínio, estatísticas, 4 abas (Visão Geral/Conquistas/Highlights/Sobre), gráfico frequência, atividade recente, bottom bar |

### 4.6 Telas Pendentes

| Tela | Prioridade | Descrição |
|------|------------|-----------|
| Busca | 🔴 Alta | Buscar atletas, academias, eventos |
| Notificações | 🟡 Média | Lista de notificações |
| Novo Post | 🟡 Média | Criar post/check-in |
| Configurações | 🟡 Média | Editar perfil, privacidade |
| Esqueci Senha | 🟢 Baixa | Recuperação de senha |
| Chat/Mensagens | 🟢 Baixa | Conversas entre atletas |

### 4.7 Paleta de Cores (Android)

```kotlin
// ui/theme/Color.kt
val Background    = Color(0xFF0A0A0F)  // Fundo
val Surface       = Color(0xFF111118)  // Cards
val SurfaceLight  = Color(0xFF16161F)  // Elementos elevados
val Primary       = Color(0xFF7C3AED)  // Roxo
val PrimaryLight  = Color(0xFFA78BFA)  // Roxo claro
val Accent        = Color(0xFF3B82F6)  // Azul
val TextPrimary   = Color(0xFFF5F5F5)  // Texto principal
val TextSecondary = Color(0xFF9CA3AF)  // Texto secundário
val Border        = Color(0xFF2A2A3A)  // Bordas
val Error         = Color(0xFFEF4444)  // Erros
val Success       = Color(0xFF22C55E)  // Sucesso

// Gradientes
val GradientStart = Color(0xFF7C3AED)  // Roxo
val GradientEnd   = Color(0xFF3B82F6)  // Azul
```

### 4.8 Configurações Importantes

```kotlin
// AndroidManifest.xml
android:usesCleartextTraffic="true"  // Permite HTTP (desenvolvimento)

// ApiClient.kt
val API_BASE_URL = "http://10.0.2.2:8080"  // localhost do emulador
// Para device físico, use o IP da máquina: "http://192.168.x.x:8080"
```

### 4.9 Navegação

```kotlin
// MainActivity.kt → NavHost
"login" → LoginScreen
  ├── "register" → RegisterScreen
  └── "feed" → FeedScreen
        └── "profile" → ProfileScreen
```

---

## 5. APP iOS (FUTURO)

### 5.1 Tecnologias Planejadas

| Componente | Tecnologia | Função |
|------------|-----------|--------|
| Linguagem | Swift 5.9+ | Código nativo iOS |
| UI | SwiftUI | UI declarativa |
| HTTP | URLSession / Alamofire | Comunicação com API |
| JSON | Codable | Parse de JSON |
| Imagens | Kingfisher / AsyncImage | Carregamento de imagens |
| Storage | Keychain / UserDefaults | Tokens e preferências |
| Estado | @State / @ObservedObject / @StateObject | Gerenciamento de estado |

### 5.2 Estrutura Planejada

```
RedeSocialEsportiva/
├── App/
│   ├── RedeSocialEsportivaApp.swift    ← Entry point
│   └── ContentView.swift               ← Root view
├── Features/
│   ├── Auth/
│   │   ├── LoginView.swift
│   │   ├── RegisterView.swift
│   │   └── AuthViewModel.swift
│   ├── Feed/
│   │   ├── FeedView.swift
│   │   └── FeedViewModel.swift
│   ├── Profile/
│   │   ├── ProfileView.swift
│   │   └── ProfileViewModel.swift
│   └── Search/
│       ├── SearchView.swift
│       └── SearchViewModel.swift
├── Core/
│   ├── API/
│   │   └── APIClient.swift             ← URLSession wrapper
│   ├── Storage/
│   │   └── TokenStorage.swift          ← Keychain wrapper
│   └── Models/
│       └── DTOs.swift                  ← Structs Codable
├── DesignSystem/
│   ├── Colors.swift
│   ├── Fonts.swift
│   └── Components/
│       └── CustomButton.swift
└── Resources/
    └── Assets.xcassets
```

### 5.3 Telas a Implementar (iOS)

| Tela | Prioridade |
|------|------------|
| Login | 🔴 Alta |
| Cadastro | 🔴 Alta |
| Feed | 🔴 Alta |
| Perfil | 🔴 Alta |
| Busca | 🟡 Média |
| Check-in | 🟡 Média |
| Notificações | 🟡 Média |

### 5.4 Como Montar (Quando For Implementar)

```bash
# Criar projeto no Xcode
# File → New → Project → App
# Nome: RedeSocialEsportiva
# Interface: SwiftUI
# Language: Swift

# Ou via Swift Package Manager:
# Adicionar dependências no Package.swift
```

---

## 6. INTEGRAÇÃO ENTRE AS PARTES

### 6.1 Fluxo de Autenticação

```
┌─────────┐     ┌─────────┐     ┌─────────┐     ┌─────────┐
│  Client │────→│  Ktor   │────→│PostgreSQL│     │  Redis  │
│(Web/And)│     │ Backend │     │  (User) │     │(Tokens) │
└─────────┘     └─────────┘     └─────────┘     └─────────┘
     │               │               │               │
     │ 1. Register   │               │               │
     │──────────────→│               │               │
     │               │ 2. Hash pwd   │               │
     │               │ 3. Save user  │               │
     │               │──────────────→│               │
     │               │               │               │
     │ 4. TokenResponse              │               │
     │←──────────────│               │               │
     │ (access + refresh)            │               │
     │               │               │               │
     │ 5. Store tokens locally       │               │
     │ (Web: localStorage / Android: DataStore)     │
     │               │               │               │
     │ 6. API calls with Bearer      │               │
     │──────────────→│ 7. Validate JWT              │
     │               │──────────────→│               │
     │               │               │               │
     │ 8. Response   │               │               │
     │←──────────────│               │               │
```

### 6.2 Comunicação Cliente ↔ Backend

| Cliente | Base URL | Notas |
|---------|----------|-------|
| Web (dev) | `http://localhost:8080` | Mesma máquina |
| Web (prod) | `https://api.redesocial.com` | HTTPS |
| Android (emulador) | `http://10.0.2.2:8080` | localhost do emulador |
| Android (device) | `http://192.168.x.x:8080` | IP da máquina |
| iOS (simulador) | `http://localhost:8080` | Mesma máquina |
| iOS (device) | `http://192.168.x.x:8080` | IP da máquina |

### 6.3 Headers Padrão

```http
GET /api/feed HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
Content-Type: application/json
Accept: application/json
```

### 6.4 Respostas de Erro Padrão

```json
{
  "error": "Unauthorized",
  "message": "Token inválido ou expirado",
  "status": 401
}
```

---

## 7. DEPLOY E PRODUÇÃO

### 7.1 Backend (Docker)

```bash
# Build da imagem
cd "backend rede social esportiva/ktor-server"
docker build -t rede-social-backend:latest .

# Push para registry
docker tag rede-social-backend:latest seu-registry/rede-social-backend:latest
docker push seu-registry/rede-social-backend:latest

# Deploy no servidor
# (usando docker-compose.prod.yml ou Kubernetes)
```

### 7.2 Frontend Web (Vercel/Netlify)

```bash
# Build de produção
cd "rede-social-esportiva"
npm run build

# Deploy na Vercel
vercel --prod

# Ou na Netlify
netlify deploy --prod --dir=out
```

### 7.3 App Android (Google Play)

```bash
# Gerar APK de release
./gradlew assembleRelease

# Ou App Bundle (recomendado para Play Store)
./gradlew bundleRelease

# Sign the APK/AAB
# Upload na Google Play Console
```

### 7.4 App iOS (App Store)

```bash
# Build no Xcode
# Product → Archive
# Distribute App → App Store Connect
# Upload via Transporter ou Xcode
```

### 7.5 Checklist de Produção

- [ ] SSL/HTTPS configurado (Let's Encrypt)
- [ ] Variáveis de ambiente de produção
- [ ] Banco de dados migrado
- [ ] Redis configurado
- [ ] MinIO/S3 configurado
- [ ] Backups automáticos
- [ ] Monitoramento (Prometheus + Grafana)
- [ ] Logs centralizados
- [ ] Rate limiting ativo
- [ ] CORS configurado para domínios específicos
- [ ] JWT secrets trocados (não usar os de desenvolvimento)
- [ ] Senhas de banco fortes
- [ ] Firewall configurado

---

## 📚 RECURSOS ADICIONAIS

### Documentação Oficial
- [Ktor](https://ktor.io/docs/)
- [Exposed](https://github.com/JetBrains/Exposed/wiki)
- [Koin](https://insert-koin.io/docs/)
- [Next.js](https://nextjs.org/docs)
- [Tailwind CSS](https://tailwindcss.com/docs)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [SwiftUI](https://developer.apple.com/documentation/swiftui)

### Repositórios
- Web: https://github.com/JMancini92/rede-social-esportiva
- Android: https://github.com/JMancini92/redesocialesportiva

---

*Documento gerado em 22/05/2026. Atualize conforme o projeto evolui.*
