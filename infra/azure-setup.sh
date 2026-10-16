#!/usr/bin/env bash
# =====================================================================
# Provisiona a infraestrutura no Azure (rodar UMA vez).
# Recomendado: executar no Azure Cloud Shell (bash) em https://shell.azure.com
#
#   GITHUB_REPO=usuario/repositorio bash infra/azure-setup.sh
#
# Cria:
#   - Resource Group
#   - Cosmos DB (API MongoDB, free tier) com 2 bancos: esg_staging e esg_prod
#   - Container Apps Environment
#   - 2 Container Apps: staging e produção
#   - Service Principal para o GitHub Actions (saída = secret AZURE_CREDENTIALS)
#
# Para usar MongoDB Atlas em vez de Cosmos DB, defina antes de rodar:
#   MONGODB_URI_STAGING="mongodb+srv://..."  MONGODB_URI_PROD="mongodb+srv://..."
# =====================================================================
set -euo pipefail

: "${GITHUB_REPO:?Defina GITHUB_REPO=usuario/repositorio}"

PREFIX="${PREFIX:-terramove}"
LOCATION="${LOCATION:-eastus}"
RG="${RG:-rg-${PREFIX}-esg}"
ENV_NAME="${ENV_NAME:-cae-${PREFIX}}"
APP_STAGING="${APP_STAGING:-${PREFIX}-api-staging}"
APP_PROD="${APP_PROD:-${PREFIX}-api-prod}"
COSMOS_NAME="${COSMOS_NAME:-cosmos-${PREFIX}-$RANDOM}"
ADMIN_PASSWORD_STAGING="${ADMIN_PASSWORD_STAGING:-admin123}"
ADMIN_PASSWORD_PROD="${ADMIN_PASSWORD_PROD:-$(openssl rand -base64 12)}"
# Imagem temporária: o pipeline substitui pela imagem da API no primeiro deploy
PLACEHOLDER_IMAGE="mcr.microsoft.com/k8se/quickstart:latest"

echo ">> Registrando providers (pode demorar na primeira vez)"
az extension add --name containerapp --upgrade --only-show-errors
az provider register --namespace Microsoft.App --wait
az provider register --namespace Microsoft.OperationalInsights --wait
az provider register --namespace Microsoft.DocumentDB --wait

echo ">> Resource group $RG ($LOCATION)"
az group create --name "$RG" --location "$LOCATION" --output none

# ---------------------------------------------------------------------
# Banco de dados
# ---------------------------------------------------------------------
if [[ -z "${MONGODB_URI_STAGING:-}" || -z "${MONGODB_URI_PROD:-}" ]]; then
  echo ">> Cosmos DB (API MongoDB) $COSMOS_NAME"
  az cosmosdb create \
    --name "$COSMOS_NAME" --resource-group "$RG" \
    --kind MongoDB --server-version 4.2 \
    --enable-free-tier true \
    --default-consistency-level Session \
    --locations regionName="$LOCATION" failoverPriority=0 \
    --output none

  # 400 RU/s cada = 800 RU/s, dentro dos 1000 RU/s gratuitos
  for DB in esg_staging esg_prod; do
    az cosmosdb mongodb database create \
      --account-name "$COSMOS_NAME" --resource-group "$RG" \
      --name "$DB" --throughput 400 --output none
  done

  BASE_URI=$(az cosmosdb keys list --type connection-strings \
    --name "$COSMOS_NAME" --resource-group "$RG" \
    --query "connectionStrings[0].connectionString" -o tsv)
  # insere o nome do banco na connection string: ...:10255/<db>?ssl=true...
  MONGODB_URI_STAGING=$(echo "$BASE_URI" | sed 's|/?|/esg_staging?|')
  MONGODB_URI_PROD=$(echo "$BASE_URI" | sed 's|/?|/esg_prod?|')
fi

# ---------------------------------------------------------------------
# Container Apps
# ---------------------------------------------------------------------
echo ">> Container Apps Environment $ENV_NAME"
az containerapp env create \
  --name "$ENV_NAME" --resource-group "$RG" --location "$LOCATION" \
  --output none

create_app() {
  local NAME=$1 PROFILE=$2 URI=$3 ADMIN_PWD=$4
  echo ">> Container App $NAME (perfil $PROFILE)"
  az containerapp create \
    --name "$NAME" --resource-group "$RG" --environment "$ENV_NAME" \
    --image "$PLACEHOLDER_IMAGE" \
    --target-port 8080 --ingress external \
    --cpu 0.5 --memory 1.0Gi \
    --min-replicas 0 --max-replicas 2 \
    --secrets "mongodb-uri=$URI" "admin-password=$ADMIN_PWD" \
    --env-vars \
      "SPRING_PROFILES_ACTIVE=$PROFILE" \
      "MONGODB_URI=secretref:mongodb-uri" \
      "APP_ADMIN_PASSWORD=secretref:admin-password" \
    --output none
}

create_app "$APP_STAGING" staging "$MONGODB_URI_STAGING" "$ADMIN_PASSWORD_STAGING"
create_app "$APP_PROD"    prod    "$MONGODB_URI_PROD"    "$ADMIN_PASSWORD_PROD"

# Se o pacote no GHCR for privado, informe um token (PAT com read:packages)
if [[ -n "${GHCR_TOKEN:-}" ]]; then
  GH_USER="${GITHUB_REPO%%/*}"
  for APP in "$APP_STAGING" "$APP_PROD"; do
    az containerapp registry set --name "$APP" --resource-group "$RG" \
      --server ghcr.io --username "$GH_USER" --password "$GHCR_TOKEN" --output none
  done
fi

# ---------------------------------------------------------------------
# Service Principal para o GitHub Actions
# ---------------------------------------------------------------------
SUB_ID=$(az account show --query id -o tsv)
echo ">> Service principal (acesso só ao resource group $RG)"
SP_JSON=$(az ad sp create-for-rbac \
  --name "github-${PREFIX}-esg" \
  --role contributor \
  --scopes "/subscriptions/$SUB_ID/resourceGroups/$RG" \
  --json-auth)

STAGING_URL=$(az containerapp show -n "$APP_STAGING" -g "$RG" --query properties.configuration.ingress.fqdn -o tsv)
PROD_URL=$(az containerapp show -n "$APP_PROD" -g "$RG" --query properties.configuration.ingress.fqdn -o tsv)

cat <<EOF

=====================================================================
 PRONTO! Configure no GitHub: Settings > Secrets and variables > Actions
=====================================================================

[Secrets]  AZURE_CREDENTIALS  =
$SP_JSON

[Variables]
  AZURE_RESOURCE_GROUP       = $RG
  AZURE_CONTAINERAPP_STAGING = $APP_STAGING
  AZURE_CONTAINERAPP_PROD    = $APP_PROD

URLs:
  Staging : https://$STAGING_URL
  Produção: https://$PROD_URL

Senha do admin em produção: $ADMIN_PASSWORD_PROD   (guarde!)
=====================================================================
EOF
