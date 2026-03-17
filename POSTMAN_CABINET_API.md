# Guide Postman - Endpoints Cabinet API

## Configuration de base

- **Base URL**: `http://localhost:8085`
- **Port**: 8085

---

## 1. Authentification

### Login (requis avant toute action admin)
```
POST http://localhost:8085/api/auth/login
Content-Type: application/json

{
  "email": "superadmin@medibook.com",
  "motDePasse": "123456"
}
```

> Le JWT est automatiquement stocké dans un cookie `Authentication`

---

## 2. Endpoints Cabinet

### GET - Liste tous les cabinets
```
GET http://localhost:8085/api/admin/cabinets
```
Aucun body requis.

---

### GET - Détails d'un cabinet
```
GET http://localhost:8085/api/admin/cabinets/{id}
```
Exemple: `GET http://localhost:8085/api/admin/cabinets/1`

---

### POST - Créer un cabinet (Super Admin only)
```
POST http://localhost:8085/api/admin/cabinets
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "nom": "Cabinet Medical Centrale",
  "logo": "https://exemple.com/logo.png",
  "couleurPrimaire": "#007bff",
  "couleurSecondaire": "#ffffff",
  "adresse": "123 Avenue de la Santé, Dakar",
  "telephone": "+221 33 123 45 67",
  "email": "contact@cabinet-centrale.com"
}
```

**Champs obligatoires**: nom, adresse, telephone, email

---

### PUT - Modifier un cabinet (Super Admin only)
```
PUT http://localhost:8085/api/admin/cabinets/{id}
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "nom": "Nouveau Nom",
  "adresse": "Nouvelle adresse",
  "telephone": "+221 33 000 00 00",
  "email": "nouveau@email.com"
}
```

---

### DELETE - Supprimer un cabinet (Super Admin only)
```
DELETE http://localhost:8085/api/admin/cabinets/{id}
Authorization: Bearer <jwt_token>
```

---

### PATCH - Basculer le statut (Activer/Désactiver)
```
PATCH http://localhost:8085/api/admin/cabinets/{id}/toggle-status
Authorization: Bearer <jwt_token>
```

---

### POST - Créer avec logo (multipart)
```
POST http://localhost:8085/api/admin/cabinets/with-logo
Authorization: Bearer <jwt_token>
Content-Type: multipart/form-data

Form Data:
- nom: "Mon Cabinet"
- adresse: "Dakar"
- telephone: "+221 77 123 45 67"
- email: "test@cabinet.com"
- logo: [fichier image]
```

---

## 3. Codes de réponse

| Code | Signification |
|------|---------------|
| 200 | Succès |
| 201 | Créé avec succès |
| 400 | Erreur de validation |
| 401 | Non authentifié |
| 403 | Accès refusé (pas Super Admin) |
| 404 | Cabinet non trouvé |

---

## 4. Messages d'erreur courants

- `Seul le Super Admin peut créer un cabinet` (403)
- `Un cabinet avec ce nom existe déjà` (400)
- `Cet email est déjà utilisé` (400)
- `Cabinet non trouvé` (404)
