# API Patient - Documentation

## Authentification

Tous les endpoints nécessitent une authentification JWT via cookie.

### Connexion
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "fatou.sall@email.com",
  "motDePasse": "123456"
}
```

**Réponse** (200 OK) :
```json
{
  "message": "Connexion réussie",
  "user": {
    "id": 5,
    "prenom": "Fatou",
    "nom": "Sall",
    "email": "fatou.sall@email.com",
    "role": "PATIENT"
  }
}
```

---

## 1. Cabinets

### 1.1 Liste des cabinets
```http
GET /api/patient/cabinets
```

**Réponse** (200 OK) :
```json
{
  "message": "Liste des cabinets récupérée avec succès",
  "data": [
    {
      "id": 1,
      "nom": "Cabinet Médical Medibook",
      "logo": null,
      "adresse": "123 Avenue de la Santé, Dakar Plateau",
      "telephone": "+221 33 123 45 67",
      "email": "contact@medibook.com",
      "couleurPrimaire": "#007bff",
      "couleurSecondaire": "#ffffff"
    }
  ]
}
```

### 1.2 Détails d'un cabinet
```http
GET /api/patient/cabinets/{id}
```

| Paramètre | Type | Description |
|-----------|------|-------------|
| id | Long | ID du cabinet |

**Réponse** (200 OK) :
```json
{
  "message": "Détails du cabinet récupérés avec succès",
  "data": {
    "id": 1,
    "nom": "Cabinet Médical Medibook",
    "logo": null,
    "adresse": "123 Avenue de la Santé, Dakar Plateau",
    "telephone": "+221 33 123 45 67",
    "email": "contact@medibook.com",
    "couleurPrimaire": "#007bff",
    "couleurSecondaire": "#ffffff"
  }
}
```

**Erreur** (404 Not Found) :
```json
{
  "error": "Cabinet non trouvé"
}
```

---

## 2. Spécialités

### 2.1 Liste des spécialités
```http
GET /api/patient/specialites
```

**Réponse** (200 OK) :
```json
{
  "message": "Liste des spécialités récupérée avec succès",
  "data": [
    {
      "id": 1,
      "nom": "Médecine Générale",
      "description": "Consultations de médecine générale pour tous les ages",
      "cabinetId": 1,
      "cabinetNom": "Cabinet Médical Medibook"
    }
  ]
}
```

### 2.2 Spécialités d'un cabinet
```http
GET /api/patient/specialites/cabinet/{id}
```

| Paramètre | Type | Description |
|-----------|------|-------------|
| id | Long | ID du cabinet |

**Réponse** (200 OK) :
```json
{
  "message": "Liste des spécialités récupérée avec succès",
  "data": [
    {
      "id": 1,
      "nom": "Médecine Générale",
      "description": "Consultations de médecine générale pour tous les ages",
      "cabinetId": 1,
      "cabinetNom": "Cabinet Médical Medibook"
    }
  ]
}
```

---

## 3. Médecins

### 3.1 Liste des médecins
```http
GET /api/patient/medecins
GET /api/patient/medecins?specialite_id=1
GET /api/patient/medecins?cabinet_id=1
GET /api/patient/medecins?specialite_id=1&cabinet_id=1
```

| Paramètre | Type | Requis | Description |
|-----------|------|--------|-------------|
| specialite_id | Long | Non | Filtrer par spécialité |
| cabinet_id | Long | Non | Filtrer par cabinet |

**Réponse** (200 OK) :
```json
{
  "message": "Liste des médecins récupérée avec succès",
  "data": [
    {
      "id": 3,
      "prenom": "Jean",
      "nom": "Dupont",
      "photo": null,
      "telephone": "+221330000002",
      "email": "jean.dupont@medibook.com",
      "specialiteId": 1,
      "specialiteNom": "Médecine Générale",
      "cabinetId": 1,
      "cabinetNom": "Cabinet Médical Medibook"
    }
  ]
}
```

### 3.2 Détails d'un médecin
```http
GET /api/patient/medecins/{id}
```

| Paramètre | Type | Description |
|-----------|------|-------------|
| id | Long | ID du médecin |

**Réponse** (200 OK) :
```json
{
  "message": "Détails du médecin récupérés avec succès",
  "data": {
    "id": 3,
    "prenom": "Jean",
    "nom": "Dupont",
    "photo": null,
    "telephone": "+221330000002",
    "email": "jean.dupont@medibook.com",
    "specialiteId": 1,
    "specialiteNom": "Médecine Générale",
    "cabinetId": 1,
    "cabinetNom": "Cabinet Médical Medibook"
  }
}
```

---

## 4. Disponibilités (Créneaux)

### 4.1 Créneaux disponibles d'un médecin
```http
GET /api/patient/medecins/{id}/disponibilites
GET /api/patient/medecins/{id}/disponibilites?date=2026-03-15
```

| Paramètre | Type | Requis | Description |
|-----------|------|--------|-------------|
| id | Long | Oui | ID du médecin |
| date | LocalDate (YYYY-MM-DD) | Non | Date spécifique. Si absent, retourne les 7 prochains jours |

**Réponse** (200 OK) :
```json
{
  "message": "Créneaux disponibles récupérés avec succès",
  "data": [
    {
      "id": 1,
      "date": "2026-03-10",
      "heureDebut": "08:00:00",
      "heureFin": "08:30:00",
      "disponible": true,
      "medecinId": 3,
      "medecinNom": "Dupont",
      "medecinPrenom": "Jean"
    },
    {
      "id": 2,
      "date": "2026-03-10",
      "heureDebut": "08:30:00",
      "heureFin": "09:00:00",
      "disponible": true,
      "medecinId": 3,
      "medecinNom": "Dupont",
      "medecinPrenom": "Jean"
    }
  ],
  "count": 2
}
```

---

## 5. Rendez-vous

### 5.1 Créer un rendez-vous
```http
POST /api/patient/rdv
Content-Type: application/json

{
  "creneauId": 1,
  "motif": "Consultation générale"
}
```

| Champ | Type | Requis | Description |
|-------|------|--------|-------------|
| creneauId | Long | Oui | ID du créneau choisi |
| motif | String | Non | Motif de la consultation |

**Réponse** (201 Created) :
```json
{
  "message": "Rendez-vous créé avec succès",
  "data": {
    "id": 7,
    "statut": "EN_ATTENTE",
    "motif": "Consultation générale",
    "date": "2026-03-10",
    "heureDebut": "08:00:00",
    "heureFin": "08:30:00",
    "medecinId": 3,
    "medecinNom": "Dupont",
    "medecinPrenom": "Jean",
    "medecinSpecialite": "Médecine Générale",
    "cabinetId": 1,
    "cabinetNom": "Cabinet Médical Medibook",
    "cabinetAdresse": "123 Avenue de la Santé, Dakar Plateau"
  }
}
```

**Erreurs possibles** :
- `404` : Créneau non trouvé
- `400` : Ce créneau n'est plus disponible

### 5.2 Liste de mes rendez-vous
```http
GET /api/patient/rdv
```

**Réponse** (200 OK) :
```json
{
  "message": "Liste des rendez-vous récupérée",
  "data": [
    {
      "id": 7,
      "statut": "EN_ATTENTE",
      "motif": "Consultation générale",
      "date": "2026-03-10",
      "heureDebut": "08:00:00",
      "heureFin": "08:30:00",
      "medecinId": 3,
      "medecinNom": "Dupont",
      "medecinPrenom": "Jean",
      "medecinSpecialite": "Médecine Générale",
      "cabinetId": 1,
      "cabinetNom": "Cabinet Médical Medibook",
      "cabinetAdresse": "123 Avenue de la Santé, Dakar Plateau"
    }
  ],
  "count": 1
}
```

### 5.3 Mes rendez-vous en attente
```http
GET /api/patient/rdv/en-attente
```

**Réponse** (200 OK) :
```json
{
  "message": "Liste des rendez-vous récupérée",
  "data": [/* RDV avec statut EN_ATTENTE */],
  "count": 2
}
```

### 5.4 Mes rendez-vous confirmés
```http
GET /api/patient/rdv/confirmes
```

**Réponse** (200 OK) :
```json
{
  "message": "Liste des rendez-vous récupérée",
  "data": [/* RDV avec statut CONFIRME */],
  "count": 1
}
```

### 5.5 Mon historique
```http
GET /api/patient/rdv/historique
```

Retourne les rendez-vous terminés et annulés.

**Réponse** (200 OK) :
```json
{
  "message": "Liste des rendez-vous récupérée",
  "data": [/* RDV avec statut TERMINE ou ANNULE */],
  "count": 1
}
```

### 5.6 Détails d'un rendez-vous
```http
GET /api/patient/rdv/{id}
```

| Paramètre | Type | Description |
|-----------|------|-------------|
| id | Long | ID du rendez-vous |

**Réponse** (200 OK) :
```json
{
  "message": "Détails du rendez-vous récupérés",
  "data": {
    "id": 7,
    "statut": "EN_ATTENTE",
    "motif": "Consultation générale",
    "date": "2026-03-10",
    "heureDebut": "08:00:00",
    "heureFin": "08:30:00",
    "medecinId": 3,
    "medecinNom": "Dupont",
    "medecinPrenom": "Jean",
    "medecinSpecialite": "Médecine Générale",
    "cabinetId": 1,
    "cabinetNom": "Cabinet Médical Medibook",
    "cabinetAdresse": "123 Avenue de la Santé, Dakar Plateau"
  }
}
```

**Erreur** (404 Not Found) :
```json
{
  "error": "Rendez-vous non trouvé"
}
```

### 5.7 Annuler un rendez-vous
```http
PATCH /api/patient/rdv/{id}/annuler
```

| Paramètre | Type | Description |
|-----------|------|-------------|
| id | Long | ID du rendez-vous à annuler |

**Réponse** (200 OK) :
```json
{
  "message": "Rendez-vous annulé avec succès",
  "data": {
    "id": 7,
    "statut": "ANNULE",
    "motif": "Consultation générale",
    "date": "2026-03-10",
    "heureDebut": "08:00:00",
    "heureFin": "08:30:00",
    "medecinId": 3,
    "medecinNom": "Dupont",
    "medecinPrenom": "Jean",
    "medecinSpecialite": "Médecine Générale",
    "cabinetId": 1,
    "cabinetNom": "Cabinet Médical Medibook",
    "cabinetAdresse": "123 Avenue de la Santé, Dakar Plateau"
  }
}
```

**Erreurs possibles** :
- `404` : Rendez-vous non trouvé
- `400` : Ce rendez-vous est déjà annulé
- `400` : Impossible d'annuler ce rendez-vous

> **Note** : L'annulation libère automatiquement le créneau pour d'autres patients.

---

## Statuts des rendez-vous

| Statut | Description |
|--------|-------------|
| `EN_ATTENTE` | RDV créé, en attente de confirmation par le médecin/secrétaire |
| `CONFIRME` | RDV confirmé par le cabinet |
| `TERMINE` | Consultation effectuée |
| `ANNULE` | RDV annulé par le patient ou le cabinet |

---

## Codes d'erreur HTTP

| Code | Description |
|------|-------------|
| 200 | Succès |
| 201 | Ressource créée |
| 400 | Requête invalide |
| 401 | Non authentifié |
| 403 | Accès refusé |
| 404 | Ressource non trouvée |
| 500 | Erreur serveur |

---

## Exemple de flux complet

```bash
# 1. Connexion
curl -X POST http://localhost:8085/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"fatou.sall@email.com","motDePasse":"123456"}' \
  -c cookies.txt

# 2. Lister les cabinets
curl -b cookies.txt http://localhost:8085/api/patient/cabinets

# 3. Voir les spécialités d'un cabinet
curl -b cookies.txt http://localhost:8085/api/patient/specialites/cabinet/1

# 4. Chercher un médecin par spécialité
curl -b cookies.txt "http://localhost:8085/api/patient/medecins?specialite_id=1"

# 5. Voir les disponibilités du médecin
curl -b cookies.txt http://localhost:8085/api/patient/medecins/3/disponibilites

# 6. Prendre rendez-vous
curl -X POST -b cookies.txt http://localhost:8085/api/patient/rdv \
  -H "Content-Type: application/json" \
  -d '{"creneauId": 1, "motif": "Consultation"}'

# 7. Voir mes rendez-vous
curl -b cookies.txt http://localhost:8085/api/patient/rdv

# 8. Annuler si besoin
curl -X PATCH -b cookies.txt http://localhost:8085/api/patient/rdv/7/annuler
```
