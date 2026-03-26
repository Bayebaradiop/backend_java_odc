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

## Développement local (sans Docker Compose)

### Tester le build Docker localement

```bash
# Construire l'image Docker
docker build -t medibook-api .

# Lancer le container
docker run -p 8085:8080 \
  -e DB_HOST=localhost \
  -e DB_PORT=5432 \
  -e DB_NAME=medibook \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=postgres \
  -e JWT_SECRET=votre_secret \
  medibook-api
```

## Déploiement sur Render (Plan Gratuit)

### Étape 1 : Créer une base de données PostgreSQL

1. Connectez-vous à [Render Dashboard](https://dashboard.render.com)
2. Cliquez sur **New +** → **PostgreSQL**
3. Configurez :
   - Name : `medibook-db`
   - Database : `medibook`
   - User : `postgres`
4. Cliquez sur **Create Database**

### Étape 2 : Créer le Web Service

1. Cliquez sur **New +** → **Web Service**
2. Connectez votre repository GitHub
3. Configurez :
   - Name : `medibook-api`
   - Environment : `Docker`
   - Region : Paris (ou la plus proche)
   - Branch : `main`
   - Build Command : (laisser vide - utilise le Dockerfile)
   - Start Command : (laisser vide)
4. Cliquez sur **Create Web Service**

### Étape 3 : Configurer les variables d'environnement

Dans la page de votre service Render, allez dans **Environment** et ajoutez :

| Clé | Valeur | Description |
|-----|--------|-------------|
| `SERVER_PORT` | `8080` | Port exposé par Render |
| `DB_HOST` | URL de votre PostgreSQL | Endpoint interne Render |
| `DB_PORT` | `5432` | Port PostgreSQL |
| `DB_NAME` | `medibook` | Nom de la base |
| `DB_USERNAME` | `postgres` | Utilisateur PostgreSQL |
| `DB_PASSWORD` | Mot de passe PostgreSQL | (depuis Render) |
| `JWT_SECRET` | Clé aléatoire | Générez une clé longue et sécurisée |
| `CLOUDINARY_CLOUD_NAME` | Votre cloud name | Depuis Cloudinary |
| `CLOUDINARY_API_KEY` | Votre API key | Depuis Cloudinary |
| `CLOUDINARY_API_SECRET` | Votre API secret | Depuis Cloudinary |

Pour obtenir l'URL de votre DB PostgreSQL :
1. Allez dans votre base de données PostgreSQL sur Render
2. Dans **Connections**, copiez l'**Internal Connection URL**
3. Extrayez le host (remplacez `postgres://` par ``)

### Étape 4 : Déployer

1. Cliquez sur **Deploy**
2. Attendez que le build se termine (plusieurs minutes)
3. Votre API sera disponible sur : `https://medibook-api-xxxx.onrender.com`

## Notes importantes

- **Plan gratuit** : Le service se met en veille après 15 minutes d'inactivité. Le premier accès peut prendre 30-60 secondes.
- **Health check** : Render fait un ping sur `/` pour garder le service actif.
- **Logs** : Consultez les logs dans le dashboard Render pour le débogage.
- **Port** : Utilisez `8080` comme port d'application pour Render (pas 8085)

## Commandes Docker utiles

```bash
# Construire l'image
docker build -t medibook-api .

# Voir les images
docker images

# Lancer un container
docker run -p 8085:8080 -e DB_HOST=localhost medibook-api

# Voir les containers en cours
docker ps

# Voir les logs
docker logs <container_id>

# Supprimer les images inutiles
docker system prune