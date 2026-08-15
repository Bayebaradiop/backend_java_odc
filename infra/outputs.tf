output "resource_group_name" {
  description = "Nom du Groupe de Ressources Azure"
  value       = azurerm_resource_group.rg.name
}

output "acr_login_server" {
  description = "Serveur de connexion pour Azure Container Registry"
  value       = azurerm_container_registry.acr.login_server
}

output "acr_admin_username" {
  description = "Nom d'utilisateur administrateur ACR (pour Jenkins)"
  value       = azurerm_container_registry.acr.admin_username
}

output "acr_admin_password" {
  description = "Mot de passe administrateur ACR (pour Jenkins)"
  value       = azurerm_container_registry.acr.admin_password
  sensitive   = true
}

output "container_app_fqdn" {
  description = "URL publique de l'application Azure Container App"
  value       = "https://${azurerm_container_app.app.ingress[0].fqdn}"
}

output "postgresql_fqdn" {
  description = "Hôte (FQDN) du serveur de base de données PostgreSQL"
  value       = azurerm_postgresql_flexible_server.postgres.fqdn
}
