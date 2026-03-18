# 📋 MEDIBOOK API - Documentation Complète

> **Base URL** : `http://localhost:8085`  
> **Swagger UI** : `http://localhost:8085/swagger-ui.html`  
> **OpenAPI JSON** : `http://localhost:8085/api-docs`

---

## 🔑 Comptes de test (Seeder)

| Rôle | Email | Mot de passe | Cabinet |
|------|-------|-------------|---------|
| **SUPER_ADMIN** | `superadmin@medibook.com` | `123456` | — |
| **ADMIN** | `admin@medibook.com` | `123456` | Cabinet Médical Medibook |
| **ADMIN** | `admin.sud@medibook.com` | `123456` | Cabinet Médical Sud |
| **MEDECIN** | `jean.dupont@medibook.com` | `123456` | Cabinet Médical Medibook (Médecine Générale) |
| **MEDECIN** | `amadou.ba@medibook.com` | `123456` | Cabinet Médical Medibook (Cardiologie) |
| **MEDECIN** | `ibrahima.diop@medibook.com` | `123456` | Cabinet Médical Sud (Ophtalmologie) |
| **SECRETAIRE** | `marie.sarr@medibook.com` | `123456` | Cabinet Médical Medibook (Médecine Générale) |
| **SECRETAIRE** | `aissatou.fall@medibook.com` | `123456` | Cabinet Médical Sud (Ophtalmologie) |
| **PATIENT** | `fatou.sall@email.com` | `123456` | — |
| **PATIENT** | `aliou.diallo@email.com` | `123456` | — |

---

## 🔐 Authentification

L'API utilise **JWT (JSON Web Token)**. Le token est retourné :
- Dans un **cookie** `access_token` (httpOnly)
- Dans le **body** de la réponse (champ `token`)

Pour Swagger, utilisez le bouton **Authorize** 🔒 et collez : `Bearer <votre_token>`

---

## 📌 WORKFLOW DE TEST RECOMMANDÉ

```
1. Login SUPER_ADMIN → Créer un cabinet → Voir stats globales
2. Login ADMIN → Créer spécialités → Créer médecins → Créer secrétaires → Voir stats cabinet
3. Login SECRETAIRE → Créer plannings → Voir créneaux → Gérer RDV
4. Login MEDECIN → Voir plannings → Confirmer/Terminer RDV → Voir stats
5. Login PATIENT → Chercher médecins → Voir disponibilités → Prendre RDV
```

---

# 1️⃣ AUTH - Authentification (`/api/auth`) — PUBLIC

## 1.1 POST `/api/auth/login` — Connexion

**Accès** : Public

**Body JSON** :
```json
{
  "email": "superadmin@medibook.com",
  "motDePasse": "123456"
}
```

**Scripts de test par rôle** :

<details>
<summary>🔴 Login SUPER_ADMIN</summary>

```json
{
  "email": "superadmin@medibook.com",
  "motDePasse": "123456"
}
```
</details>

<details>
<summary>🟠 Login ADMIN (Cabinet Medibook)</summary>

```json
{
  "email": "admin@medibook.com",
  "motDePasse": "123456"
}
```
</details>

<details>
<summary>🟠 Login ADMIN (Cabinet Sud)</summary>

```json
{
  "email": "admin.sud@medibook.com",
  "motDePasse": "123456"
}
```
</details>

<details>
<summary>🔵 Login MEDECIN</summary>

```json
{
  "email": "jean.dupont@medibook.com",
  "motDePasse": "123456"
}
```
</details>

<details>
<summary>🟢 Login SECRETAIRE</summary>

```json
{
  "email": "marie.sarr@medibook.com",
  "motDePasse": "123456"
}
```
</details>

<details>
<summary>🟣 Login PATIENT</summary>

```json
{
  "email": "fatou.sall@email.com",
  "motDePasse": "123456"
}
```
</details>

**Réponse 200** :
```json
{
  "id": 1,
  "prenom": "Super",
  "nom": "Admin",
  "email": "superadmin@medibook.com",
  "role": "SUPER_ADMIN",
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

>  **Copiez le `token` retourné et collez-le dans Swagger Authorize : `Bearer <token>`**

---

## 1.2 POST `/api/auth/register` — Inscription Patient

**Accès** : Public

**Body JSON** :
```json
{
  "prenom": "Moussa",
  "nom": "Diagne",
  "email": "moussa.diagne@email.com",
  "telephone": "+221771112233",
  "motDePasse": "123456"
}
```

**Réponse 201** :
```json
{
  "id": 7,
  "prenom": "Moussa",
  "nom": "Diagne",
  "email": "moussa.diagne@email.com",
  "role": "PATIENT",
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

## 1.3 POST `/api/auth/logout` — Déconnexion

**Accès** : Authentifié  
**Body** : Aucun  
**Réponse 200** : Supprime le cookie `access_token`

---

## 1.4 GET `/api/auth/profile` — Mon profil

**Accès** : Authentifié (tout rôle)  
**Body** : Aucun

**Réponse 200** :
```json
{
  "id": 1,
  "prenom": "Super",
  "nom": "Admin",
  "email": "superadmin@medibook.com",
  "telephone": "+221330000000",
  "photo": null,
  "role": "SUPER_ADMIN"
}
```

---

## 1.5 PUT `/api/auth/profile` — Modifier mon profil

**Accès** : Authentifié (tout rôle)

**Body JSON** :
```json
{
  "prenom": "Super Modifié",
  "nom": "Admin Modifié",
  "telephone": "+221339999999"
}
```

---

## 1.6 POST `/api/auth/forgot-password` — Mot de passe oublié

**Accès** : Public

**Body JSON** :
```json
{
  "email": "fatou.sall@email.com"
}
```

**Réponse 200** :
```json
{
  "success": true,
  "message": "Un email de réinitialisation a été envoyé"
}
```

---

## 1.7 POST `/api/auth/reset-password` — Réinitialiser le mot de passe

**Accès** : Public

**Body JSON** :
```json
{
  "email": "fatou.sall@email.com",
  "code": "<code_reçu_par_email>",
  "newPassword": "nouveauMdp123"
}
```

**Réponse 200** :
```json
{
  "success": true,
  "message": "Mot de passe réinitialisé avec succès"
}
```

---

# 2️⃣ CABINETS (`/api/super-admin/cabinets`) — SUPER_ADMIN

> 🔴 **Connectez-vous d'abord avec `superadmin@medibook.com`**

## 2.1 POST `/api/super-admin/cabinets` — Créer un cabinet

**Accès** : SUPER_ADMIN  
**Content-Type** : `multipart/form-data`

**Champs du formulaire** :
```
dto (JSON) :
{
  "nom": "Cabinet Médical Nord",
  "adresse": "78 Boulevard du Nord, Saint-Louis",
  "telephone": "+221339876543",
  "email": "nord@medibook.com",
  "couleurPrimaire": "#dc3545",
  "couleurSecondaire": "#ffffff",
  "logoUrl": "",
  "adminNom": "Diop",
  "adminPrenom": "Mamadou",
  "adminEmail": "mamadou.diop@medibook.com",
  "adminTelephone": "+221771234568",
  "adminPassword": "12345678"
}

logo (fichier) : [optionnel - image PNG/JPG]
```

**Réponse 201** :
```json
{
  "id": 3,
  "nom": "Cabinet Médical Nord",
  "logo": null,
  "couleurPrimaire": "#dc3545",
  "couleurSecondaire": "#ffffff",
  "adresse": "78 Boulevard du Nord, Saint-Louis",
  "telephone": "+221339876543",
  "email": "nord@medibook.com",
  "status": "ACTIF",
  "admin": {
    "id": 11,
    "nom": "Diop",
    "prenom": "Mamadou",
    "email": "mamadou.diop@medibook.com",
    "telephone": "+221771234568"
  }
}
```

---

## 2.2 GET `/api/super-admin/cabinets` — Lister tous les cabinets

**Accès** : SUPER_ADMIN  
**Body** : Aucun

**Réponse 200** :
```json
[
  {
    "id": 1,
    "nom": "Cabinet Médical Medibook",
    "adresse": "123 Avenue de la Santé, Dakar Plateau",
    "telephone": "+221338001234",
    "email": "contact@medibook.com",
    "status": "ACTIF"
  },
  {
    "id": 2,
    "nom": "Cabinet Médical Sud",
    "adresse": "45 Rue des Médecins, Dakar Fann",
    "telephone": "+221338005678",
    "email": "sud@medibook.com",
    "status": "ACTIF"
  }
]
```

---

## 2.3 GET `/api/super-admin/cabinets/{id}` — Détails d'un cabinet

**Accès** : SUPER_ADMIN  
**Paramètre** : `id` = 1

---

## 2.4 PUT `/api/super-admin/cabinets/{id}` — Modifier un cabinet

**Accès** : SUPER_ADMIN  
**Paramètre** : `id` = 1  
**Content-Type** : `multipart/form-data`

**Champs** :
```
dto (JSON) :
{
  "nom": "Cabinet Médical Medibook (Modifié)",
  "adresse": "123 Avenue de la Santé, Dakar Plateau",
  "telephone": "+221338001234",
  "email": "contact@medibook.com",
  "couleurPrimaire": "#0056b3",
  "couleurSecondaire": "#f0f0f0",
  "logoUrl": "",
  "adminNom": "Admin",
  "adminPrenom": "System",
  "adminEmail": "admin@medibook.com",
  "adminTelephone": "+221330000001",
  "adminPassword": "123456"
}
```

---

## 2.5 DELETE `/api/super-admin/cabinets/{id}` — Supprimer un cabinet

**Accès** : SUPER_ADMIN  
**Paramètre** : `id` = 3

---

## 2.6 PATCH `/api/super-admin/cabinets/{id}/toggle-status` — Activer/Désactiver

**Accès** : SUPER_ADMIN  
**Paramètre** : `id` = 2  
**Body** : Aucun

**Réponse 200** : Le statut bascule entre `ACTIF` ↔ `INACTIF`

---

## 2.7 PATCH `/api/super-admin/cabinets/{id}/logo` — Mettre à jour le logo

**Accès** : SUPER_ADMIN  
**Paramètre** : `id` = 1  
**Content-Type** : `multipart/form-data`  
**Champ** : `logo` = fichier image

---

## 2.8 GET `/api/super-admin/dashboard/cabinets` — Dashboard cabinets

**Accès** : SUPER_ADMIN  
**Body** : Aucun

> Retourne la liste des cabinets avec informations détaillées pour le tableau de bord.

**Réponse 200** :
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "nom": "Cabinet Médical Medibook",
      "adresse": "123 Avenue de la Santé, Dakar Plateau",
      "telephone": "+221338001234",
      "email": "contact@medibook.com",
      "status": "ACTIF"
    }
  ]
}
```

---

## 2.9 GET `/api/super-admin/stats` — Statistiques globales

**Accès** : SUPER_ADMIN  
**Body** : Aucun

> Retourne les statistiques globales de la plateforme (nombre total de cabinets, médecins, patients, rendez-vous).

**Réponse 200** :
```json
{
  "success": true,
  "data": {
    "totalCabinets": 2,
    "totalMedecins": 5,
    "totalPatients": 10,
    "totalRendezVous": 25,
    "rdvEnAttente": 8,
    "rdvConfirmes": 12,
    "rdvTermines": 3,
    "rdvAnnules": 2
  }
}
```

---

# 3️⃣ SPÉCIALITÉS (`/api/admin/specialites`) — ADMIN

> 🟠 **Connectez-vous d'abord avec `admin@medibook.com`**

## 3.1 POST `/api/admin/specialites` — Créer une spécialité

**Accès** : ADMIN  
**Body JSON** :
```json
{
  "nom": "Neurologie",
  "description": "Spécialité traitant les maladies du système nerveux"
}
```

**Réponse 201** :
```json
{
  "success": true,
  "message": "Spécialité créée avec succès",
  "data": {
    "id": 8,
    "nom": "Neurologie",
    "description": "Spécialité traitant les maladies du système nerveux",
    "cabinetId": 1,
    "cabinetNom": "Cabinet Médical Medibook"
  }
}
```

**Autres exemples de spécialités à tester** :
```json
{ "nom": "ORL", "description": "Oto-rhino-laryngologie - nez, gorge, oreilles" }
```
```json
{ "nom": "Urologie", "description": "Spécialité de l'appareil urinaire" }
```
```json
{ "nom": "Pneumologie", "description": "Spécialité des voies respiratoires" }
```

---

## 3.2 GET `/api/admin/specialites` — Lister les spécialités

**Accès** : ADMIN  
**Body** : Aucun

**Réponse 200** :
```json
[
  { "id": 1, "nom": "Médecine Générale", "description": "Soins de santé primaires", "cabinetId": 1, "cabinetNom": "Cabinet Médical Medibook" },
  { "id": 2, "nom": "Cardiologie", "description": "Maladies du cœur et du système cardiovasculaire", "cabinetId": 1, "cabinetNom": "Cabinet Médical Medibook" },
  { "id": 3, "nom": "Pédiatrie", "description": "Médecine des enfants", "cabinetId": 1, "cabinetNom": "Cabinet Médical Medibook" },
  { "id": 4, "nom": "Dermatologie", "description": "Maladies de la peau", "cabinetId": 1, "cabinetNom": "Cabinet Médical Medibook" },
  { "id": 5, "nom": "Gynécologie", "description": "Santé féminine", "cabinetId": 1, "cabinetNom": "Cabinet Médical Medibook" }
]
```

---

## 3.3 GET `/api/admin/specialites/{specialiteId}` — Détails

**Accès** : ADMIN  
**Paramètre** : `specialiteId` = 1

---

## 3.4 PUT `/api/admin/specialites/{specialiteId}` — Modifier

**Accès** : ADMIN  
**Paramètre** : `specialiteId` = 1

**Body JSON** :
```json
{
  "nom": "Médecine Générale",
  "description": "Soins de santé primaires et consultations générales - mis à jour"
}
```

---

## 3.5 DELETE `/api/admin/specialites/{specialiteId}` — Supprimer

**Accès** : ADMIN  
**Paramètre** : `specialiteId` = 8  
*(supprimer la spécialité Neurologie créée plus tôt)*

---

# 4️⃣ MÉDECINS (`/api/admin/medecins`) — ADMIN

> 🟠 **Connectez-vous d'abord avec `admin@medibook.com`**

## 4.1 POST `/api/admin/medecins` — Créer un médecin

**Accès** : ADMIN  
**Content-Type** : `multipart/form-data`

**Champs du formulaire** :
```
request (JSON) :
{
  "prenom": "Cheikh",
  "nom": "Mbacke",
  "email": "cheikh.mbacke@medibook.com",
  "telephone": "+221771112244",
  "motDePasse": "12345678",
  "specialiteId": 3
}

photo (fichier) : [optionnel - image du médecin]
```

> ℹ️ `specialiteId: 3` = Pédiatrie. Un email de bienvenue est envoyé automatiquement.

**Réponse 201** :
```json
{
  "success": true,
  "message": "Médecin créé avec succès",
  "data": {
    "id": 11,
    "prenom": "Cheikh",
    "nom": "Mbacke",
    "email": "cheikh.mbacke@medibook.com",
    "telephone": "+221771112244",
    "photo": null,
    "role": "MEDECIN",
    "status": "ACTIF",
    "cabinetId": 1,
    "cabinetNom": "Cabinet Médical Medibook",
    "specialiteId": 3,
    "specialiteNom": "Pédiatrie"
  }
}
```

**Autres médecins à tester** :
```json
{
  "prenom": "Awa",
  "nom": "Gueye",
  "email": "awa.gueye@medibook.com",
  "telephone": "+221771112255",
  "motDePasse": "12345678",
  "specialiteId": 4
}
```

---

## 4.2 GET `/api/admin/medecins` — Lister les médecins du cabinet

**Accès** : ADMIN  
**Body** : Aucun

**Réponse 200** :
```json
[
  {
    "id": 3,
    "prenom": "Jean",
    "nom": "Dupont",
    "email": "jean.dupont@medibook.com",
    "telephone": "+221330000002",
    "role": "MEDECIN",
    "status": "ACTIF",
    "cabinetId": 1,
    "cabinetNom": "Cabinet Médical Medibook",
    "specialiteId": 1,
    "specialiteNom": "Médecine Générale"
  },
  {
    "id": 5,
    "prenom": "Amadou",
    "nom": "Ba",
    "email": "amadou.ba@medibook.com",
    "telephone": "+221330000004",
    "role": "MEDECIN",
    "status": "ACTIF",
    "cabinetId": 1,
    "cabinetNom": "Cabinet Médical Medibook",
    "specialiteId": 2,
    "specialiteNom": "Cardiologie"
  }
]
```

---

## 4.3 GET `/api/admin/medecins/{medecinId}` — Détails d'un médecin

**Accès** : ADMIN  
**Paramètre** : `medecinId` = 3

---

## 4.4 PUT `/api/admin/medecins/{medecinId}` — Modifier un médecin

**Accès** : ADMIN  
**Paramètre** : `medecinId` = 3  
**Content-Type** : `multipart/form-data`

**Champs** :
```
request (JSON) :
{
  "prenom": "Jean-Pierre",
  "nom": "Dupont",
  "email": "jean.dupont@medibook.com",
  "telephone": "+221330000002",
  "specialiteId": 1
}

photo (fichier) : [optionnel]
```

---

## 4.5 DELETE `/api/admin/medecins/{medecinId}` — Supprimer

**Accès** : ADMIN  
**Paramètre** : `medecinId` = 11

---

## 4.6 PATCH `/api/admin/medecins/{medecinId}/status` — Activer/Désactiver

**Accès** : ADMIN  
**Paramètre** : `medecinId` = 3  
**Body** : Aucun

**Réponse** : Le statut bascule entre `ACTIF` ↔ `INACTIF`

---

# 5️⃣ SECRÉTAIRES (`/api/admin/secretaires`) — ADMIN

> 🟠 **Connectez-vous d'abord avec `admin@medibook.com`**

## 5.1 POST `/api/admin/secretaires` — Créer une secrétaire

**Accès** : ADMIN  
**Content-Type** : `multipart/form-data`

**Champs du formulaire** :
```
request (JSON) :
{
  "prenom": "Ndeye",
  "nom": "Mbaye",
  "email": "ndeye.mbaye@medibook.com",
  "telephone": "+221771113366",
  "motDePasse": "12345678",
  "specialiteId": 2
}

photo (fichier) : [optionnel]
```

> ℹ️ `specialiteId: 2` = Cardiologie. La secrétaire verra les médecins de cette spécialité.

**Réponse 201** :
```json
{
  "success": true,
  "message": "Secrétaire créé(e) avec succès",
  "data": {
    "id": 12,
    "prenom": "Ndeye",
    "nom": "Mbaye",
    "email": "ndeye.mbaye@medibook.com",
    "telephone": "+221771113366",
    "role": "SECRETAIRE",
    "status": "ACTIF",
    "cabinetId": 1,
    "cabinetNom": "Cabinet Médical Medibook",
    "specialiteId": 2,
    "specialiteNom": "Cardiologie"
  }
}
```

---

## 5.2 GET `/api/admin/secretaires` — Lister les secrétaires

**Accès** : ADMIN  
**Body** : Aucun

---

## 5.3 GET `/api/admin/secretaires/{secretaireId}` — Détails

**Accès** : ADMIN  
**Paramètre** : `secretaireId` = 4

---

## 5.4 PUT `/api/admin/secretaires/{secretaireId}` — Modifier

**Accès** : ADMIN  
**Paramètre** : `secretaireId` = 4  
**Content-Type** : `multipart/form-data`

**Champs** :
```
request (JSON) :
{
  "prenom": "Marie",
  "nom": "Sarr-Ndiaye",
  "email": "marie.sarr@medibook.com",
  "telephone": "+221330000003",
  "specialiteId": 1
}

photo (fichier) : [optionnel]
```

---

## 5.5 DELETE `/api/admin/secretaires/{secretaireId}` — Supprimer

**Accès** : ADMIN  
**Paramètre** : `secretaireId` = 12

---

## 5.6 PATCH `/api/admin/secretaires/{secretaireId}/status` — Activer/Désactiver

**Accès** : ADMIN  
**Paramètre** : `secretaireId` = 4  
**Body** : Aucun

---

## 5.7 GET `/api/admin/stats` — Statistiques du cabinet

**Accès** : ADMIN  
**Body** : Aucun

> Retourne les statistiques du cabinet de l'admin connecté (nombre de médecins, secrétaires, patients, rendez-vous).

**Réponse 200** :
```json
{
  "success": true,
  "data": {
    "totalMedecins": 3,
    "totalSecretaires": 1,
    "totalPatients": 5,
    "totalRendezVous": 15,
    "rdvEnAttente": 5,
    "rdvConfirmes": 7,
    "rdvTermines": 2,
    "rdvAnnules": 1
  }
}
```

---

# 6️⃣ SECRÉTAIRE — Endpoints (`/api/secretaire`) — SECRETAIRE

> 🟢 **Connectez-vous d'abord avec `marie.sarr@medibook.com`**

## 6.1 GET `/api/secretaire/medecins` — Médecins de ma spécialité

**Accès** : SECRETAIRE  
**Body** : Aucun

> Retourne les médecins du cabinet ayant la même spécialité que la secrétaire.

**Réponse 200** :
```json
{
  "success": true,
  "message": "Médecins récupérés avec succès",
  "data": [
    {
      "id": 3,
      "prenom": "Jean",
      "nom": "Dupont",
      "email": "jean.dupont@medibook.com",
      "telephone": "+221330000002",
      "role": "MEDECIN",
      "status": "ACTIF",
      "specialiteId": 1,
      "specialiteNom": "Médecine Générale"
    }
  ]
}
```

---

## 6.2 POST `/api/secretaire/planning` — Créer un planning

**Accès** : SECRETAIRE

**Body JSON** :
```json
{
  "medecinId": 3,
  "jourSemaine": "LUNDI",
  "heureDebut": "09:00",
  "heureFin": "17:00",
  "dureeCreneau": 30
}
```

> ℹ️ `medecinId: 3` = Dr Jean Dupont. Crée un template hebdomadaire + génère les créneaux automatiquement.

**Autres jours à tester** :
```json
{ "medecinId": 3, "jourSemaine": "MARDI", "heureDebut": "08:00", "heureFin": "12:00", "dureeCreneau": 30 }
```
```json
{ "medecinId": 3, "jourSemaine": "MERCREDI", "heureDebut": "14:00", "heureFin": "18:00", "dureeCreneau": 45 }
```
```json
{ "medecinId": 3, "jourSemaine": "JEUDI", "heureDebut": "09:00", "heureFin": "13:00", "dureeCreneau": 20 }
```
```json
{ "medecinId": 3, "jourSemaine": "VENDREDI", "heureDebut": "08:00", "heureFin": "16:00", "dureeCreneau": 30 }
```

**Valeurs possibles pour `jourSemaine`** : `LUNDI`, `MARDI`, `MERCREDI`, `JEUDI`, `VENDREDI`, `SAMEDI`, `DIMANCHE`

**Réponse 201** :
```json
{
  "success": true,
  "message": "Planning créé avec succès",
  "data": {
    "id": 11,
    "medecinId": 3,
    "medecinNom": "Jean Dupont",
    "jourSemaine": "LUNDI",
    "heureDebut": "09:00",
    "heureFin": "17:00",
    "dureeCreneau": 30
  }
}
```

---

## 6.3 GET `/api/secretaire/planning/medecin/{medecinId}` — Plannings d'un médecin

**Accès** : SECRETAIRE  
**Paramètre** : `medecinId` = 3

> Retourne tous les plannings hebdomadaires configurés pour un médecin.

**Réponse 200** :
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "medecinId": 3,
      "medecinNom": "Jean Dupont",
      "jourSemaine": "LUNDI",
      "heureDebut": "09:00",
      "heureFin": "17:00",
      "dureeCreneau": 30
    },
    {
      "id": 2,
      "medecinId": 3,
      "medecinNom": "Jean Dupont",
      "jourSemaine": "MARDI",
      "heureDebut": "08:00",
      "heureFin": "12:00",
      "dureeCreneau": 30
    }
  ]
}
```

---

## 6.4 GET `/api/secretaire/creneaux/medecin/{medecinId}` — Créneaux d'un médecin

**Accès** : SECRETAIRE  
**Paramètre** : `medecinId` = 3

> Retourne tous les créneaux générés automatiquement pour un médecin.

**Réponse 200** :
```json
{
  "success": true,
  "data": [
    {
      "id": 15,
      "date": "2026-03-23",
      "heureDebut": "09:00:00",
      "heureFin": "09:30:00",
      "disponible": true,
      "medecinId": 3,
      "medecinNom": "Dupont",
      "medecinPrenom": "Jean"
    }
  ]
}
```

---

## 6.5 DELETE `/api/secretaire/creneaux/{id}` — Supprimer un créneau

**Accès** : SECRETAIRE  
**Paramètre** : `id` = 15

**Réponse 200** :
```json
{
  "success": true,
  "message": "Créneau supprimé avec succès"
}
```

---

## 6.6 GET `/api/secretaire/rdv` — Tous les RDV du cabinet

**Accès** : SECRETAIRE  
**Body** : Aucun

> Retourne tous les rendez-vous des médecins de la même spécialité dans le cabinet.

**Réponse 200** :
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "statut": "EN_ATTENTE",
      "motif": "Consultation générale",
      "date": "2026-03-20",
      "heureDebut": "09:00:00",
      "heureFin": "09:30:00",
      "medecinId": 3,
      "medecinNom": "Dupont",
      "medecinPrenom": "Jean",
      "medecinSpecialite": "Médecine Générale",
      "patientNom": "Sall",
      "patientPrenom": "Fatou"
    }
  ]
}
```

---

## 6.7 GET `/api/secretaire/rdv/en-attente` — RDV en attente du cabinet

**Accès** : SECRETAIRE  
**Body** : Aucun

**Réponse** : Liste des RDV avec `statut: "EN_ATTENTE"` pour le cabinet

---

## 6.8 PATCH `/api/secretaire/rdv/{id}/confirmer` — Confirmer un RDV

**Accès** : SECRETAIRE  
**Paramètre** : `id` = 1  
**Body** : Aucun

**Réponse 200** :
```json
{
  "success": true,
  "data": {
    "id": 1,
    "statut": "CONFIRME",
    "motif": "Consultation générale"
  }
}
```

---

## 6.9 PATCH `/api/secretaire/rdv/{id}/annuler` — Annuler un RDV

**Accès** : SECRETAIRE  
**Paramètre** : `id` = 1  
**Body** : Aucun

**Réponse 200** :
```json
{
  "success": true,
  "data": {
    "id": 1,
    "statut": "ANNULE",
    "motif": "Consultation générale"
  }
}
```

> ℹ️ Le créneau redevient disponible après annulation.

---

# 7️⃣ MÉDECIN — Endpoints (`/api/medecin`) — MEDECIN

> 🔵 **Connectez-vous d'abord avec `jean.dupont@medibook.com`**

## 7.1 GET `/api/medecin/plannings` — Mes plannings

**Accès** : MEDECIN  
**Body** : Aucun

> Retourne les plannings hebdomadaires du médecin connecté.

**Réponse 200** :
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "medecinId": 3,
      "medecinNom": "Jean Dupont",
      "jourSemaine": "LUNDI",
      "heureDebut": "09:00",
      "heureFin": "17:00",
      "dureeCreneau": 30
    }
  ]
}
```

---

## 7.2 GET `/api/medecin/rdv` — Tous mes rendez-vous

**Accès** : MEDECIN  
**Body** : Aucun

**Réponse 200** :
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "statut": "EN_ATTENTE",
      "motif": "Consultation générale",
      "date": "2026-03-20",
      "heureDebut": "09:00:00",
      "heureFin": "09:30:00",
      "patientNom": "Sall",
      "patientPrenom": "Fatou"
    }
  ]
}
```

---

## 7.3 GET `/api/medecin/rdv/en-attente` — Mes RDV en attente

**Accès** : MEDECIN  
**Body** : Aucun

**Réponse** : Liste des RDV avec `statut: "EN_ATTENTE"` du médecin connecté

---

## 7.4 PATCH `/api/medecin/rdv/{id}/confirmer` — Confirmer un RDV

**Accès** : MEDECIN  
**Paramètre** : `id` = 1  
**Body** : Aucun

**Réponse 200** :
```json
{
  "success": true,
  "data": {
    "id": 1,
    "statut": "CONFIRME",
    "motif": "Consultation générale"
  }
}
```

---

## 7.5 PATCH `/api/medecin/rdv/{id}/terminer` — Terminer un RDV

**Accès** : MEDECIN  
**Paramètre** : `id` = 1  
**Body** : Aucun

**Réponse 200** :
```json
{
  "success": true,
  "data": {
    "id": 1,
    "statut": "TERMINE",
    "motif": "Consultation générale"
  }
}
```

---

## 7.6 GET `/api/medecin/stats` — Mes statistiques

**Accès** : MEDECIN  
**Body** : Aucun

> Retourne les statistiques du médecin connecté.

**Réponse 200** :
```json
{
  "success": true,
  "data": {
    "totalRendezVous": 15,
    "rdvEnAttente": 5,
    "rdvConfirmes": 7,
    "rdvTermines": 2,
    "rdvAnnules": 1
  }
}
```

---

# 8️⃣ PATIENT — Recherche (`/api/patient`) — PATIENT

> 🟣 **Connectez-vous d'abord avec `fatou.sall@email.com`**

## 8.1 GET `/api/patient/cabinets` — Lister les cabinets

**Accès** : PATIENT  
**Body** : Aucun

**Réponse 200** :
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "nom": "Cabinet Médical Medibook",
      "logo": null,
      "adresse": "123 Avenue de la Santé, Dakar Plateau",
      "telephone": "+221338001234",
      "email": "contact@medibook.com",
      "couleurPrimaire": "#007bff",
      "couleurSecondaire": "#ffffff"
    },
    {
      "id": 2,
      "nom": "Cabinet Médical Sud",
      "logo": null,
      "adresse": "45 Rue des Médecins, Dakar Fann",
      "telephone": "+221338005678",
      "email": "sud@medibook.com",
      "couleurPrimaire": "#28a745",
      "couleurSecondaire": "#f8f9fa"
    }
  ]
}
```

---

## 8.2 GET `/api/patient/cabinets/{id}` — Détails d'un cabinet

**Accès** : PATIENT  
**Paramètre** : `id` = 1

---

## 8.3 GET `/api/patient/specialites` — Toutes les spécialités

**Accès** : PATIENT  
**Body** : Aucun

**Réponse 200** :
```json
{
  "success": true,
  "data": [
    { "id": 1, "nom": "Médecine Générale", "description": "Soins de santé primaires", "cabinetId": 1, "cabinetNom": "Cabinet Médical Medibook" },
    { "id": 2, "nom": "Cardiologie", "description": "Maladies du cœur", "cabinetId": 1, "cabinetNom": "Cabinet Médical Medibook" },
    { "id": 6, "nom": "Ophtalmologie", "description": "Maladies des yeux", "cabinetId": 2, "cabinetNom": "Cabinet Médical Sud" }
  ]
}
```

---

## 8.4 GET `/api/patient/specialites/cabinet/{id}` — Spécialités d'un cabinet

**Accès** : PATIENT  
**Paramètre** : `id` = 1

---

## 8.5 GET `/api/patient/medecins` — Rechercher des médecins

**Accès** : PATIENT  
**Paramètres query (optionnels)** :
- `specialite_id` = ID de la spécialité
- `cabinet_id` = ID du cabinet

**Exemples** :
```
GET /api/patient/medecins                           → Tous les médecins
GET /api/patient/medecins?specialite_id=1            → Médecins en Médecine Générale
GET /api/patient/medecins?cabinet_id=1               → Médecins du cabinet Medibook
GET /api/patient/medecins?specialite_id=2&cabinet_id=1 → Cardiologues du cabinet Medibook
```

**Réponse 200** :
```json
{
  "success": true,
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

---

## 8.6 GET `/api/patient/medecins/{id}` — Détails d'un médecin

**Accès** : PATIENT  
**Paramètre** : `id` = 3

---

## 8.7 GET `/api/patient/medecins/{id}/disponibilites` — Créneaux disponibles

**Accès** : PATIENT  
**Paramètre** : `id` = 3 (Dr Jean Dupont)  
**Paramètre query (optionnel)** : `date` = `2026-03-17` (format YYYY-MM-DD)

**Exemples** :
```
GET /api/patient/medecins/3/disponibilites               → Disponibilités d'aujourd'hui
GET /api/patient/medecins/3/disponibilites?date=2026-03-17 → Disponibilités du 17 mars
GET /api/patient/medecins/3/disponibilites?date=2026-03-18 → Disponibilités du 18 mars
```

**Réponse 200** :
```json
{
  "success": true,
  "data": [
    {
      "id": 15,
      "date": "2026-03-17",
      "heureDebut": "08:00:00",
      "heureFin": "08:30:00",
      "disponible": true,
      "medecinId": 3,
      "medecinNom": "Dupont",
      "medecinPrenom": "Jean"
    },
    {
      "id": 16,
      "date": "2026-03-17",
      "heureDebut": "08:30:00",
      "heureFin": "09:00:00",
      "disponible": true,
      "medecinId": 3,
      "medecinNom": "Dupont",
      "medecinPrenom": "Jean"
    }
  ]
}
```

> ⚠️ **Notez le `id` du créneau pour prendre un rendez-vous !**

---

# 9️⃣ PATIENT — Rendez-vous (`/api/patient/rdv`) — PATIENT

> 🟣 **Connectez-vous d'abord avec `fatou.sall@email.com`**

## 9.1 POST `/api/patient/rdv` — Prendre un rendez-vous

**Accès** : PATIENT

**Body JSON** :
```json
{
  "creneauId": 15,
  "motif": "Consultation générale - douleurs au dos"
}
```

> ℹ️ Remplacez `creneauId` par un ID valide obtenu depuis l'endpoint disponibilités.

**Autres motifs à tester** :
```json
{ "creneauId": 16, "motif": "Contrôle annuel - bilan de santé" }
```
```json
{ "creneauId": 17, "motif": "Renouvellement ordonnance" }
```
```json
{ "creneauId": 18, "motif": "Fièvre persistante depuis 3 jours" }
```

**Réponse 200** :
```json
{
  "success": true,
  "data": {
    "id": 8,
    "statut": "EN_ATTENTE",
    "motif": "Consultation générale - douleurs au dos",
    "date": "2026-03-17",
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

---

## 9.2 GET `/api/patient/rdv` — Tous mes rendez-vous

**Accès** : PATIENT  
**Body** : Aucun

---

## 9.3 GET `/api/patient/rdv/en-attente` — Mes RDV en attente

**Accès** : PATIENT  
**Body** : Aucun

**Réponse** : Liste des RDV avec `statut: "EN_ATTENTE"`

---

## 9.4 GET `/api/patient/rdv/confirmes` — Mes RDV confirmés

**Accès** : PATIENT  
**Body** : Aucun

**Réponse** : Liste des RDV avec `statut: "CONFIRME"`

---

## 9.5 GET `/api/patient/rdv/historique` — Mon historique

**Accès** : PATIENT  
**Body** : Aucun

**Réponse** : Liste des RDV avec `statut: "TERMINE"` ou `"ANNULE"`

---

## 9.6 GET `/api/patient/rdv/{id}` — Détails d'un RDV

**Accès** : PATIENT  
**Paramètre** : `id` = 1

---

## 9.7 PUT `/api/patient/rdv/{id}/annuler` — Annuler un RDV

**Accès** : PATIENT  
**Paramètre** : `id` = 8  
**Body** : Aucun

**Réponse 200** :
```json
{
  "success": true,
  "data": {
    "id": 8,
    "statut": "ANNULE",
    "motif": "Consultation générale - douleurs au dos"
  }
}
```

> ℹ️ Le créneau redevient disponible après annulation.

---

# 📊 RÉCAPITULATIF DES ENDPOINTS (63 au total)

| # | Méthode | URL | Rôle | Description |
|---|---------|-----|------|-------------|
| | **AUTH** | | | |
| 1 | POST | `/api/auth/login` | PUBLIC | Connexion |
| 2 | POST | `/api/auth/register` | PUBLIC | Inscription patient |
| 3 | POST | `/api/auth/forgot-password` | PUBLIC | Mot de passe oublié |
| 4 | POST | `/api/auth/reset-password` | PUBLIC | Réinitialiser mot de passe |
| 5 | POST | `/api/auth/logout` | AUTH | Déconnexion |
| 6 | GET | `/api/auth/profile` | AUTH | Mon profil |
| 7 | PUT | `/api/auth/profile` | AUTH | Modifier profil |
| | **SUPER ADMIN — Cabinets** | | | |
| 8 | POST | `/api/super-admin/cabinets` | SUPER_ADMIN | Créer cabinet |
| 9 | GET | `/api/super-admin/cabinets` | SUPER_ADMIN | Lister cabinets |
| 10 | GET | `/api/super-admin/cabinets/{id}` | SUPER_ADMIN | Détails cabinet |
| 11 | PUT | `/api/super-admin/cabinets/{id}` | SUPER_ADMIN | Modifier cabinet |
| 12 | DELETE | `/api/super-admin/cabinets/{id}` | SUPER_ADMIN | Supprimer cabinet |
| 13 | PATCH | `/api/super-admin/cabinets/{id}/toggle-status` | SUPER_ADMIN | Activer/Désactiver |
| 14 | PATCH | `/api/super-admin/cabinets/{id}/logo` | SUPER_ADMIN | MAJ logo |
| | **SUPER ADMIN — Dashboard & Stats** | | | |
| 15 | GET | `/api/super-admin/dashboard/cabinets` | SUPER_ADMIN | Dashboard cabinets |
| 16 | GET | `/api/super-admin/stats` | SUPER_ADMIN | Statistiques globales |
| | **ADMIN — Spécialités** | | | |
| 17 | POST | `/api/admin/specialites` | ADMIN | Créer spécialité |
| 18 | GET | `/api/admin/specialites` | ADMIN | Lister spécialités |
| 19 | GET | `/api/admin/specialites/{id}` | ADMIN | Détails spécialité |
| 20 | PUT | `/api/admin/specialites/{id}` | ADMIN | Modifier spécialité |
| 21 | DELETE | `/api/admin/specialites/{id}` | ADMIN | Supprimer spécialité |
| | **ADMIN — Médecins** | | | |
| 22 | POST | `/api/admin/medecins` | ADMIN | Créer médecin |
| 23 | GET | `/api/admin/medecins` | ADMIN | Lister médecins |
| 24 | GET | `/api/admin/medecins/{id}` | ADMIN | Détails médecin |
| 25 | PUT | `/api/admin/medecins/{id}` | ADMIN | Modifier médecin |
| 26 | DELETE | `/api/admin/medecins/{id}` | ADMIN | Supprimer médecin |
| 27 | PATCH | `/api/admin/medecins/{id}/status` | ADMIN | Activer/Désactiver |
| | **ADMIN — Secrétaires** | | | |
| 28 | POST | `/api/admin/secretaires` | ADMIN | Créer secrétaire |
| 29 | GET | `/api/admin/secretaires` | ADMIN | Lister secrétaires |
| 30 | GET | `/api/admin/secretaires/{id}` | ADMIN | Détails secrétaire |
| 31 | PUT | `/api/admin/secretaires/{id}` | ADMIN | Modifier secrétaire |
| 32 | DELETE | `/api/admin/secretaires/{id}` | ADMIN | Supprimer secrétaire |
| 33 | PATCH | `/api/admin/secretaires/{id}/status` | ADMIN | Activer/Désactiver |
| | **ADMIN — Stats** | | | |
| 34 | GET | `/api/admin/stats` | ADMIN | Statistiques du cabinet |
| | **SECRÉTAIRE** | | | |
| 35 | GET | `/api/secretaire/medecins` | SECRETAIRE | Médecins de ma spécialité |
| 36 | POST | `/api/secretaire/planning` | SECRETAIRE | Créer planning |
| 37 | GET | `/api/secretaire/planning/medecin/{medecinId}` | SECRETAIRE | Plannings d'un médecin |
| 38 | GET | `/api/secretaire/creneaux/medecin/{medecinId}` | SECRETAIRE | Créneaux d'un médecin |
| 39 | DELETE | `/api/secretaire/creneaux/{id}` | SECRETAIRE | Supprimer créneau |
| 40 | GET | `/api/secretaire/rdv` | SECRETAIRE | Tous les RDV du cabinet |
| 41 | GET | `/api/secretaire/rdv/en-attente` | SECRETAIRE | RDV en attente |
| 42 | PATCH | `/api/secretaire/rdv/{id}/confirmer` | SECRETAIRE | Confirmer RDV |
| 43 | PATCH | `/api/secretaire/rdv/{id}/annuler` | SECRETAIRE | Annuler RDV |
| | **MÉDECIN** | | | |
| 44 | GET | `/api/medecin/plannings` | MEDECIN | Mes plannings |
| 45 | GET | `/api/medecin/rdv` | MEDECIN | Mes rendez-vous |
| 46 | GET | `/api/medecin/rdv/en-attente` | MEDECIN | Mes RDV en attente |
| 47 | PATCH | `/api/medecin/rdv/{id}/confirmer` | MEDECIN | Confirmer RDV |
| 48 | PATCH | `/api/medecin/rdv/{id}/terminer` | MEDECIN | Terminer RDV |
| 49 | GET | `/api/medecin/stats` | MEDECIN | Mes statistiques |
| | **PATIENT — Recherche** | | | |
| 50 | GET | `/api/patient/cabinets` | PATIENT | Lister cabinets |
| 51 | GET | `/api/patient/cabinets/{id}` | PATIENT | Détails cabinet |
| 52 | GET | `/api/patient/specialites` | PATIENT | Toutes spécialités |
| 53 | GET | `/api/patient/specialites/cabinet/{id}` | PATIENT | Spécialités par cabinet |
| 54 | GET | `/api/patient/medecins` | PATIENT | Rechercher médecins |
| 55 | GET | `/api/patient/medecins/{id}` | PATIENT | Détails médecin |
| 56 | GET | `/api/patient/medecins/{id}/disponibilites` | PATIENT | Créneaux disponibles |
| | **PATIENT — Rendez-vous** | | | |
| 57 | POST | `/api/patient/rdv` | PATIENT | Prendre RDV |
| 58 | GET | `/api/patient/rdv` | PATIENT | Mes RDV |
| 59 | GET | `/api/patient/rdv/en-attente` | PATIENT | RDV en attente |
| 60 | GET | `/api/patient/rdv/confirmes` | PATIENT | RDV confirmés |
| 61 | GET | `/api/patient/rdv/historique` | PATIENT | Historique |
| 62 | GET | `/api/patient/rdv/{id}` | PATIENT | Détails RDV |
| 63 | PUT | `/api/patient/rdv/{id}/annuler` | PATIENT | Annuler RDV |

---

# 🧪 SCÉNARIO DE TEST COMPLET (copier-coller dans Swagger)

## Étape 1 : Login Super Admin
```json
POST /api/auth/login
{
  "email": "superadmin@medibook.com",
  "motDePasse": "123456"
}
```
→ Copiez le token → Authorize : `Bearer <token>`

## Étape 2 : Voir les cabinets existants
```
GET /api/super-admin/cabinets
```

## Étape 3 : Créer un nouveau cabinet
```
POST /api/super-admin/cabinets
dto: { "nom": "Clinique du Lac", "adresse": "12 Bd Lac Rose, Dakar", "telephone": "+221339991122", "email": "lac@medibook.com", "couleurPrimaire": "#ff6600", "couleurSecondaire": "#fff3e0", "logoUrl": "", "adminNom": "Thiam", "adminPrenom": "Ibra", "adminEmail": "ibra.thiam@medibook.com", "adminTelephone": "+221771445566", "adminPassword": "12345678" }
```

## Étape 4 : Voir les statistiques globales
```
GET /api/super-admin/stats
```

## Étape 5 : Login Admin
```json
POST /api/auth/login
{
  "email": "admin@medibook.com",
  "motDePasse": "123456"
}
```
→ Authorize avec le nouveau token

## Étape 6 : Créer une spécialité
```json
POST /api/admin/specialites
{
  "nom": "Rhumatologie",
  "description": "Maladies des articulations et des os"
}
```

## Étape 7 : Créer un médecin
```
POST /api/admin/medecins
request: { "prenom": "Papa", "nom": "Sow", "email": "papa.sow@medibook.com", "telephone": "+221772223344", "motDePasse": "12345678", "specialiteId": 1 }
```

## Étape 8 : Créer une secrétaire
```
POST /api/admin/secretaires
request: { "prenom": "Coumba", "nom": "Dieng", "email": "coumba.dieng@medibook.com", "telephone": "+221773334455", "motDePasse": "12345678", "specialiteId": 1 }
```

## Étape 9 : Voir les stats du cabinet
```
GET /api/admin/stats
```

## Étape 10 : Login Secrétaire
```json
POST /api/auth/login
{
  "email": "marie.sarr@medibook.com",
  "motDePasse": "123456"
}
```
→ Authorize avec le nouveau token

## Étape 11 : Voir les médecins de ma spécialité
```
GET /api/secretaire/medecins
```

## Étape 12 : Créer un planning pour un médecin
```json
POST /api/secretaire/planning
{
  "medecinId": 3,
  "jourSemaine": "SAMEDI",
  "heureDebut": "09:00",
  "heureFin": "13:00",
  "dureeCreneau": 30
}
```

## Étape 13 : Voir les plannings du médecin
```
GET /api/secretaire/planning/medecin/3
```

## Étape 14 : Voir les créneaux générés
```
GET /api/secretaire/creneaux/medecin/3
```

## Étape 15 : Voir les RDV en attente
```
GET /api/secretaire/rdv/en-attente
```

## Étape 16 : Login Médecin
```json
POST /api/auth/login
{
  "email": "jean.dupont@medibook.com",
  "motDePasse": "123456"
}
```
→ Authorize avec le nouveau token

## Étape 17 : Voir mes plannings
```
GET /api/medecin/plannings
```

## Étape 18 : Voir mes RDV en attente
```
GET /api/medecin/rdv/en-attente
```

## Étape 19 : Confirmer un RDV
```
PATCH /api/medecin/rdv/<ID_RDV>/confirmer
```

## Étape 20 : Voir mes statistiques
```
GET /api/medecin/stats
```

## Étape 21 : Login Patient
```json
POST /api/auth/login
{
  "email": "fatou.sall@email.com",
  "motDePasse": "123456"
}
```
→ Authorize avec le nouveau token

## Étape 22 : Chercher des médecins
```
GET /api/patient/medecins
GET /api/patient/medecins?specialite_id=1
```

## Étape 23 : Voir les disponibilités
```
GET /api/patient/medecins/3/disponibilites?date=2026-03-21
```

## Étape 24 : Prendre un rendez-vous
```json
POST /api/patient/rdv
{
  "creneauId": <ID_DU_CRENEAU_DISPONIBLE>,
  "motif": "Mal de tête persistant depuis une semaine"
}
```

## Étape 25 : Voir mes rendez-vous
```
GET /api/patient/rdv
GET /api/patient/rdv/en-attente
```

## Étape 26 : Annuler un rendez-vous
```
PUT /api/patient/rdv/<ID_RDV>/annuler
```

---

# ⚠️ CODES D'ERREUR COURANTS

| Code | Message | Cause |
|------|---------|-------|
| 401 | Non authentifié | Token JWT manquant ou expiré |
| 403 | Accès interdit | Rôle insuffisant |
| 400 | Données invalides | Champs requis manquants ou format incorrect |
| 404 | Ressource non trouvée | ID inexistant |
| 409 | Conflit | Email/téléphone déjà utilisé, spécialité déjà existante |
