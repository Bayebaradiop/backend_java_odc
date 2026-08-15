variable "subscription_id" {
  description = "ID de souscription Azure"
  type        = string
  default     = "77a533fe-dd68-4f86-b3bd-f13adaaa286a"
}

variable "location" {
  description = "Région Azure autorisée pour Azure for Students"
  type        = string
  default     = "spaincentral"
}

variable "resource_group_name" {
  description = "Nom du Groupe de Ressources Azure"
  type        = string
  default     = "medibook-rg"
}

variable "acr_name" {
  description = "Nom du registre Azure Container Registry"
  type        = string
  default     = "medibookregistry"
}

variable "container_app_name" {
  description = "Nom de l'application Azure Container App"
  type        = string
  default     = "medibook-app"
}

variable "db_admin_username" {
  description = "Nom d'utilisateur administrateur PostgreSQL"
  type        = string
  default     = "postgres"
}

variable "db_admin_password" {
  description = "Mot de passe administrateur PostgreSQL"
  type        = string
  default     = ""
  sensitive   = true
}

variable "db_name" {
  description = "Nom de la base de données PostgreSQL"
  type        = string
  default     = "medibook"
}

variable "jwt_secret" {
  description = "Clé secrète JWT"
  type        = string
  default     = ""
  sensitive   = true
}

variable "cloudinary_cloud_name" {
  description = "Nom Cloudinary"
  type        = string
  default     = ""
}

variable "cloudinary_api_key" {
  description = "Clé API Cloudinary"
  type        = string
  default     = ""
}

variable "cloudinary_api_secret" {
  description = "Secret API Cloudinary"
  type        = string
  default     = ""
  sensitive   = true
}

variable "brevo_api_key" {
  description = "Clé API Brevo"
  type        = string
  default     = ""
  sensitive   = true
}

variable "brevo_sender_email" {
  description = "Email expéditeur Brevo"
  type        = string
  default     = "bayebara2000@gmail.com"
}

variable "brevo_sender_name" {
  description = "Nom expéditeur Brevo"
  type        = string
  default     = "MediBook"
}
