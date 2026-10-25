# Contributing

Thanks for interest in improving **platform-open**. This is a Spring Cloud Alibaba microservice scaffold (mall / seckill sample). Small, focused changes are easiest to review.

## Before you start

- Use JDK 8 and Maven (versions match the root `pom.xml`).
- Do not commit secrets. Copy `.env.example` and keep real values in `.env` or `application-local.yml` (both gitignored).
- Prefer one concern per pull request (bugfix, docs, or a single feature).

## Development setup

1. Clone the repo and install dependencies: `mvn -q -DskipTests install` from the repo root (requires local MySQL / Redis / Nacos / RocketMQ only when you run the apps).
2. Configure env vars from `.env.example` (or IDE run configuration).
3. Run unit tests for the module you touch: `mvn -pl <module> -am test`.

## Pull requests

- Base your branch on the latest `master`.
- Describe **what** changed and **why** (link related issues if any).
- Keep the PR focused; update README / examples when behavior or setup changes.
- New code should include or update tests when practical (especially order, stock, and idempotency paths).

## Issues

- Use the Bug / Feature issue templates when they fit.
- For security reports, follow [SECURITY.md](SECURITY.md) instead of a public issue.

## Code style

- Follow existing package layout and naming in the module you edit.
- Prefer clear names and small methods over clever one-liners.
- Do not bump Spring Boot / Cloud versions in drive-by PRs unless the change is explicitly about upgrades.
