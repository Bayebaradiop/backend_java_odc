resource "random_string" "suffix" {
  length  = 6
  special = false
  upper   = false
}

resource "azurerm_resource_group" "rg" {
  name     = var.resource_group_name
  location = var.location

  tags = {
    Environment = "Production"
    Project     = "MediBook"
  }
}

resource "azurerm_container_registry" "acr" {
  name                = var.acr_name
  resource_group_name = azurerm_resource_group.rg.name
  location            = azurerm_resource_group.rg.location
  sku                 = "Basic"
  admin_enabled       = true

  tags = {
    Environment = "Production"
    Project     = "MediBook"
  }
}

resource "azurerm_postgresql_flexible_server" "postgres" {
  name                   = "medibook-db-${random_string.suffix.result}"
  resource_group_name    = azurerm_resource_group.rg.name
  location               = azurerm_resource_group.rg.location
  version                = "15"
  administrator_login    = var.db_admin_username
  administrator_password = var.db_admin_password
  storage_mb             = 32768
  sku_name               = "B_Standard_B1ms"
  zone                   = "1"

  tags = {
    Environment = "Production"
    Project     = "MediBook"
    FreeTier    = "750h-Eligible"
  }
}

resource "azurerm_postgresql_flexible_server_firewall_rule" "allow_all_azure_ips" {
  name             = "AllowAllAzureServices"
  server_id        = azurerm_postgresql_flexible_server.postgres.id
  start_ip_address = "0.0.0.0"
  end_ip_address   = "0.0.0.0"
}

resource "azurerm_postgresql_flexible_server_database" "db" {
  name      = var.db_name
  server_id = azurerm_postgresql_flexible_server.postgres.id
  collation = "en_US.utf8"
  charset   = "utf8"
}

resource "azurerm_log_analytics_workspace" "logs" {
  name                = "medibook-logs-${random_string.suffix.result}"
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name
  sku                 = "PerGB2018"
  retention_in_days   = 30
}

resource "azurerm_container_app_environment" "cae" {
  name                       = "medibook-env"
  location                   = azurerm_resource_group.rg.location
  resource_group_name        = azurerm_resource_group.rg.name
  log_analytics_workspace_id = azurerm_log_analytics_workspace.logs.id
}

resource "azurerm_container_app" "app" {
  name                         = var.container_app_name
  container_app_environment_id = azurerm_container_app_environment.cae.id
  resource_group_name          = azurerm_resource_group.rg.name
  revision_mode                = "Single"

  secret {
    name  = "acr-password"
    value = azurerm_container_registry.acr.admin_password
  }

  secret {
    name  = "db-password"
    value = var.db_admin_password
  }

  secret {
    name  = "jwt-secret"
    value = var.jwt_secret
  }

  secret {
    name  = "cloudinary-secret"
    value = var.cloudinary_api_secret
  }

  secret {
    name  = "brevo-api-key"
    value = var.brevo_api_key
  }

  registry {
    server               = azurerm_container_registry.acr.login_server
    username             = azurerm_container_registry.acr.admin_username
    password_secret_name = "acr-password"
  }

  ingress {
    external_enabled = true
    target_port      = 8080
    transport        = "auto"
    traffic_weight {
      latest_revision = true
      percentage      = 100
    }
  }

  template {
    min_replicas = 1
    max_replicas = 1

    container {
      name   = "medibook-backend"
      image  = "mcr.microsoft.com/azuredocs/aci-helloworld:latest"
      cpu    = 0.25
      memory = "0.5Gi"

      env {
        name  = "SERVER_PORT"
        value = "8080"
      }
      env {
        name  = "SPRING_PROFILES_ACTIVE"
        value = "prod"
      }
      env {
        name  = "DB_HOST"
        value = azurerm_postgresql_flexible_server.postgres.fqdn
      }
      env {
        name  = "DB_PORT"
        value = "5432"
      }
      env {
        name  = "DB_NAME"
        value = var.db_name
      }
      env {
        name  = "DB_USERNAME"
        value = var.db_admin_username
      }
      env {
        name        = "DB_PASSWORD"
        secret_name = "db-password"
      }
      env {
        name        = "JWT_SECRET"
        secret_name = "jwt-secret"
      }
      env {
        name  = "CLOUDINARY_CLOUD_NAME"
        value = var.cloudinary_cloud_name
      }
      env {
        name  = "CLOUDINARY_API_KEY"
        value = var.cloudinary_api_key
      }
      env {
        name        = "CLOUDINARY_API_SECRET"
        secret_name = "cloudinary-secret"
      }
      env {
        name        = "BREVO_API_KEY"
        secret_name = "brevo-api-key"
      }
      env {
        name  = "BREVO_SENDER_EMAIL"
        value = var.brevo_sender_email
      }
      env {
        name  = "BREVO_SENDER_NAME"
        value = var.brevo_sender_name
      }
      env {
        name  = "FIREBASE_ENABLED"
        value = "false"
      }
    }
  }

  tags = {
    Environment = "Production"
    Project     = "MediBook"
  }
}
