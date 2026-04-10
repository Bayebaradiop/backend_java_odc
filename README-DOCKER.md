# Docker - Guide de déploiement pour Render

## Prérequis

- Docker installé sur votre machine
- Un compte Render (gratuit)
- Une base de données PostgreSQL sur Render

## Fichiers créés

| Fichier | Description |
|---------|-------------|
| `Dockerfile` | Image Docker multi-stages pour l'application Spring Boot |
| `.dockerignore` | Fichiers à exclure du build Docker |

## Configuration pour Render

### Variables d'environnement critiques

Le fichier [`application.yml`](src/main/resources/application.yml) a été configuré pour utiliser les variables d'environnement Render :

```yaml
datasource:
  url: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
  username: ${DB_USERNAME}
  password: ${DB_PASSWORD}
```

### Variables à configurer sur Render

| Clé | Valeur |
|-----|--------|
| `SERVER_PORT` | `8080` |
| `DB_HOST` | `dpg-d72rf4c2kvos7382prng-a` |
| `DB_PORT` | `5432` |
| `DB_NAME` | `medibook_9tzd` |
| `DB_USERNAME` | `medibook_9tzd_user` |
| `DB_PASSWORD` | `M1RKm7TlXd88JRLHeEG9smrAG0fSIiYv` |
| `JWT_SECRET` | Clé sécurisée |

## Déploiement sur Render

### Étape 1 : Créer une base de données PostgreSQL

1. Connectez-vous à [Render Dashboard](https://dashboard.render.com)
2. Cliquez sur **New +** → **PostgreSQL**
3. Configurez :
   - Name : `medibook-db`
   - Database : `medibook_9tzd`
   - User : `medibook_9tzd_user`
4. Cliquez sur **Create Database**

### Étape 2 : Créer le Web Service

1. Cliquez sur **New +** → **Web Service**
2. Connectez votre repository GitHub
3. Configurez :
   - Name : `medibook-api`
   - Environment : `Docker`
   - Region : Paris (ou la plus proche)
   - Branch : `main`
   - Build Command : (laisser vide)
   - Start Command : (laisser vide)
4. Cliquez sur **Create Web Service**

### Étape 3 : Configurer les variables d'environnement

Dans la page de votre service Render, allez dans **Environment** et ajoutez les variables ci-dessus.

### Étape 4 : Déployer

1. Cliquez sur **Deploy**
2. Attendez que le build se termine
3. Votre API sera disponible sur : `https://medibook-api-xxxx.onrender.com`

## Commandes locales de test

```bash
# Construire l'image
docker build -t medibook-api .

# Lancer avec les variables d'environnement
docker run -p 8085:8080 \
  -e DB_HOST=localhost \
  -e DB_PORT=5432 \
  -e DB_NAME=medibook \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=postgres \
  -e JWT_SECRET=votre_secret \
  medibook-api