# Documentation CI/CD avec GitHub Actions

## Table des matières

1. [Architecture du pipeline](#architecture-du-pipeline)
2. [Partie CI - Build & Test](#partie-ci---build--test)
3. [Partie CD - Deploy to Render](#partie-cd---deploy-to-render)
4. [Logique de déclenchement](#logique-de-déclenchement)
5. [Configuration GitHub](#configuration-github)
6. [Configuration Render](#configuration-render)
7. [Dépannage](#dépannage)

---

## Architecture du pipeline

### Vue d'ensemble

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    PIPELINE CI/CD SÉPARÉ                                   │
└─────────────────────────────────────────────────────────────────────────────┘

  ╔═══════════════════════════════════════════════════════════════════════╗
  ║                            ÉTAPE CI                                      ║
  ║                    (Build & Test)                                       ║
  ╠═══════════════════════════════════════════════════════════════════════╣
  ║                                                                       ║
  ║   push sur main ──▶ Checkout ──▶ JDK 17 ──▶ Maven Build ──▶ Tests    ║
  ║                                                                       ║
  ║   Résultat: JAR compilée + Tests réussis                               ║
  ║            ↓ Artéfact upload                                           ║
  ╚═══════════════════════════════════════════════════════════════════════╝
                                    │
                                    │ Déclenchement automatique si succès
                                    ▼
  ╔═══════════════════════════════════════════════════════════════════════╗
  ║                            ÉTAPE CD                                     ║
  ║                    (Docker Hub + Render)                               ║
  ╠═══════════════════════════════════════════════════════════════════════╣
  ║                                                                       ║
  ║   trigger CI ──▶ Checkout ──▶ Docker Build ──▶ Docker Push ──▶ Render║
  ║                                                                       ║
  ╚═══════════════════════════════════════════════════════════════════════╝
```

### Logique de décision

```
push sur main
     │
     ▼
┌────────────────┐
│   CI Pipeline │
│  (Build+Test) │
└───────┬────────┘
        │
        ├── ✗ ÉCHEC → Pipeline s'arrête, pas de déploiement
        │
        └── ✓ SUCCÈS
                 │
                 ▼
┌────────────────┐
│   CD Pipeline  │
│   (Deploy)    │
└───────┬────────┘
         │
         ▼
   DOCKER HUB + RENDER
```

---

## Partie CI - Build & Test

### Fichier : `.github/workflows/ci-build-test.yml`

```yaml
name: CI — Build & Test

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]
```

### Déclencheurs expliqués

| Déclencheur | Description |
|------------|-------------|
| `push sur main` | Déclenche le CI à chaque push sur main |
| `push sur develop` | Déclenche le CI à chaque push sur develop |
| `pull_request vers main` | Déclenche le CI pour les PR vers main |

---

### Étape 1 : Checkout du code

```yaml
- name: Checkout code
  uses: actions/checkout@v4
```

**Explication** :
- Récupère le code source depuis le repository GitHub
- `@v4` = version stable et recommandée

---

### Étape 2 : Configuration JDK 17

```yaml
- name: Set up JDK 17
  uses: actions/setup-java@v4
  with:
    distribution: 'temurin'
    java-version: '17'
    cache: 'maven'
```

**Explication** :
- `distribution: 'temurin'` = OpenJDK gratuit et supporté
- `java-version: '17'` = Version requise par le projet
- `cache: 'maven'` = Active le cache Maven pour accélérer les builds

**Avantage du cache Maven** :
- Les dépendances sont téléchargées une seule fois
- Les builds suivants sont plus rapides (60-70% plus rapide)

---

### Étape 3 : Build et Tests

```yaml
- name: Build and test
  run: mvn clean package
```

**Ce que fait Maven** :
1. `clean` → Supprime le dossier target/
2. `compile` → Compile le code Java
3. `test` → Exécute les tests unitaires
4. `package` → Crée le JAR exécutable

**Résultat** : `target/medibook-api-1.0.0.jar`

---

### Étape 4 : Upload de l'artéfact

```yaml
- name: Upload artifact
  uses: actions/upload-artifact@v4
  with:
    name: app-jar
    path: target/*.jar
    retention-days: 1
```

**Explication** :
- `name: app-jar` = Nom de l'artéfact
- `path: target/*.jar` = Fichier JAR à uploader
- `retention-days: 1` = Supprime l'artéfact après 1 jour

**À quoi ça sert ?**
- L'artéfact est utilisé par le pipeline CD
- Le CD peut télécharger le JAR sans recompiler

---

## Partie CD - Deploy to Render

### Fichier : `.github/workflows/cd-deploy-render.yml`

```yaml
name: CD — Deploy to Render

on:
  workflow_run:
    workflows: ["CI — Build & Test"]
    types: [ completed ]
    branches: [ main ]
```

### Déclencheur expliquant

| Paramètre | Description |
|-----------|-------------|
| `workflow_run` | Se déclenche quand un autre workflow se termine |
| `workflows: ["CI — Build & Test"]` | Nom du workflow CI à surveiller |
| `types: [completed]` | Se déclenche quand le workflow est terminé |
| `branches: [main]` | Seulement pour la branche main |

---

### Condition de déploiement

```yaml
jobs:
  deploy:
    runs-on: ubuntu-latest
    if: ${{ github.event.workflow_run.conclusion == 'success' }}
```

**Explication** :
- `if: ${{ github.event.workflow_run.conclusion == 'success' }}` = Déploie seulement si le CI réussit
- Si le CI échoue, le CD ne se déclenche pas

---

### Étape 1 : Checkout du code

```yaml
- name: Checkout code
  uses: actions/checkout@v4
```

---

### Étape 2 : Configuration Docker Buildx

```yaml
- name: Set up Docker Buildx
  uses: docker/setup-buildx-action@v3
```

---

### Étape 3 : Login Docker Hub

```yaml
- name: Log in to Docker Hub
  uses: docker/login-action@v3
  with:
    username: ${{ secrets.DOCKERHUB_USERNAME }}
    password: ${{ secrets.DOCKERHUB_TOKEN }}
```

**Explication** :
- Authentification sur Docker Hub pour pousser l'image

---

### Étape 4 : Build et Push Docker

```yaml
- name: Build and push Docker image
  uses: docker/build-push-action@v5
  with:
    context: .
    push: true
    tags: ${{ secrets.DOCKERHUB_USERNAME }}/medibook-api:latest
```

**Explication** :
- Construit et pousse l'image vers Docker Hub
- Image: `votre-username/medibook-api:latest`

---

### Étape 5 : Trigger Render Deploy

```yaml
- name: Trigger Render Deploy
  run: |
    curl -X POST "${{ secrets.RENDER_DEPLOY_HOOK_URL }}" \
      -H "Content-Type: application/json" \
      -d '{"clearCache": true}'
```

**Explication** :
- Déclenche le rebuild sur Render via le Deploy Hook

---

### Étape 6 : Confirmation

```yaml
- name: Deployment triggered
  run: |
    echo "✅ Image Docker poussée vers Docker Hub"
    echo "✅ Déclenchement du déploiement Render effectué"
```

---

## Configuration GitHub

### Secrets requis

Allez dans **Settings → Secrets → New repository secret**

| Secret | Description | Exemple |
|--------|-------------|---------|
| `DOCKERHUB_USERNAME` | Nom d'utilisateur Docker Hub | `moncompte` |
| `DOCKERHUB_TOKEN` | Token Docker Hub | `dckr_xxx` |
| `RENDER_DEPLOY_HOOK_URL` | URL du webhook Render | `https://render.com/deploy/xxx` |

---

### Comment obtenir DOCKERHUB_TOKEN

1. Allez sur [Docker Hub](https://hub.docker.com)
2. Cliquez sur votre profil → **Account Settings**
3. Allez dans **Security**
4. Cliquez sur **New Access Token**
5. Donnez un nom au token (ex: "GitHub-Actions")
6. Sélectionnez les permissions (Read, Write, Delete)
7. Cliquez sur **Create**
8. **Copiez le token** immédiatement (il ne sera plus visible!)

---

### Comment obtenir RENDER_DEPLOY_HOOK_URL

1. Allez sur [Render Dashboard](https://dashboard.render.com)
2. Sélectionnez votre service
3. Settings → Deploy Hooks
4. Créez un nouveau hook
5. Copiez l'URL

---

## Flux de travail complet

```
1. Développeur: git push origin main
                │
2. GitHub Actions: Déclenche "CI — Build & Test"
                │
3. CI Pipeline:
   ├── Checkout code
   ├── Setup JDK 17 (avec cache Maven)
   ├── Maven build + tests
   └── Upload artifact (JAR)
                │
4. Si succès → Déclenche "CD — Deploy to Render"
   Si échec   → Pipeline s'arrête
                │
5. CD Pipeline:
   ├── Checkout code
   ├── Setup Docker Buildx
   ├── Docker login (Docker Hub)
   ├── Docker build + push (→ Docker Hub)
   └── Trigger Render hook (→ Render rebuild)
                │
6. Docker Hub: Image disponible
   Render: Rebuild automatique
                │
7. Application en production! ✅
```

---

## Dépannage

### Erreur : "Workflow not found"

→ Vérifiez que le nom du workflow CI est exact: `CI — Build & Test`

### Erreur : Docker login failed

→ Vérifiez DOCKERHUB_USERNAME et DOCKERHUB_TOKEN

### Erreur : Render hook failed

→ Vérifiez l'URL du Deploy Hook dans Render

### Le déploiement Render ne se déclenche pas

→ Vérifiez que le Deploy Hook est actif dans Render Dashboard

---

## Résumé des fichiers

| Fichier | Description | Type |
|---------|-------------|------|
| `.github/workflows/ci-build-test.yml` | Pipeline CI - Build et Tests | CI |
| `.github/workflows/cd-deploy-render.yml` | Pipeline CD - Docker Hub + Render | CD |
| `Dockerfile` | Image Docker de l'application | Config |
| `pom.xml` | Configuration Maven | Config |

---

## Commandes locales de test

```bash
# Compiler localement
mvn clean package

# Builder l'image Docker
docker build -t medibook-api .

# Pousser vers Docker Hub
docker tag medibook-api:latest username/medibook-api:latest
docker push username/medibook-api:latest

# Tester le container
docker run -p 8085:8080 medibook-api
```

---

## Liens utiles

- [Documentation GitHub Actions](https://docs.github.com/fr/actions)
- [Docker Hub](https://hub.docker.com)
- [Render Deploy Hooks](https://render.com/docs/deploy-hooks)
- [Docker Buildx Action](https://github.com/docker/build-push-action)

---

*Documentation générée pour le projet MediBook - Backend Spring Boot*
*CI/CD Séparé : CI (Build & Test) → CD (Docker Hub + Render)*