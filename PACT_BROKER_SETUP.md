# Pact Broker Configuration

This project is configured to use Pact Broker exclusively for contract verification. All contracts are loaded from the Pact Broker.

## Configuration

### Application Properties

Pact Broker configuration is defined in `src/main/resources/application.yml`:

```yaml
pactbroker:
  url: ${PACT_BROKER_URL:}
  auth:
    token: ${PACT_BROKER_TOKEN:}
```

### Test Class

**PactProviderVerificationTest** - Uses Pact Broker
   - Loads contracts from Pact Broker (requires `PACT_BROKER_URL` to be set)
   - Automatically publishes verification results in CI environments
   - Uses Git commit SHA as provider version

## Local Development

For local development, ensure you have `PACT_BROKER_URL` and `PACT_BROKER_TOKEN` environment variables set:

```bash
export PACT_BROKER_URL=https://your-pact-broker-url
export PACT_BROKER_TOKEN=your-token
./mvnw test -Dtest=PactProviderVerificationTest
```

Or run all tests:

```bash
./mvnw test
```

## CI/CD Configuration

### GitHub Actions

The workflow (`.github/workflows/build.yml`) is configured to:

1. **Automatically run on push/PR**: Uses Pact Broker if `PACT_BROKER_URL` secret is configured
2. **Manual trigger with webhook support**: Can be triggered manually with Pact URL and version information

### Required Secrets

Configure the following secrets in GitHub repository settings:

- `PACT_BROKER_URL`: URL of your Pact Broker instance (e.g., `https://pact-broker.example.com`)
- `PACT_BROKER_TOKEN`: Authentication token for Pact Broker

### Environment Variables

The workflow sets the following environment variables:

- `PACT_BROKER_URL`: From GitHub secrets
- `PACT_BROKER_TOKEN`: From GitHub secrets
- `CI`: Set to `'true'` to enable result publishing
- `GITHUB_SHA`: Git commit SHA (used as provider version)

## Verification Results Publishing

Verification results are automatically published to Pact Broker when:

- Running in CI environment (`CI` environment variable is set)
- `PACT_BROKER_URL` is configured
- Provider version is set (uses Git commit SHA in CI, or `pact.provider.version` system property)

## Webhook Configuration

To set up webhooks in Pact Broker to trigger verification:

1. Go to your Pact Broker provider settings
2. Add a webhook with:
   - URL: `https://api.github.com/repos/{owner}/{repo}/actions/workflows/build.yml/dispatches`
   - Method: `POST`
   - Headers: `Authorization: Bearer {GITHUB_TOKEN}`, `Accept: application/vnd.github.v3+json`
   - Body: JSON with workflow inputs (pact_url, consumer_version_tags, etc.)
   - Trigger: `contract:published`

## Manual Workflow Trigger

You can manually trigger the verification workflow from GitHub Actions UI with:

- `pact_url`: URL of the specific pact to verify
- `consumer_version_tags`: Tags for the consumer version
- `consumer_version_number`: Consumer version number
- `provider_version_tags`: Tags for the provider version

