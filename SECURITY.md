# Security Policy

## Supported versions

This repository is a learning / scaffold project. Security fixes are applied on the default branch (`master`) on a best-effort basis. There are no long-term support (LTS) release lines yet.

## Reporting a vulnerability

Please **do not** open a public issue for security-sensitive findings.

Preferred options:

1. Use [GitHub Private Vulnerability Reporting](https://github.com/wangjia2016/platform-open/security/advisories/new) if enabled for this repository.
2. Otherwise, email the maintainer via the address on the [GitHub profile](https://github.com/wangjia2016) (or open a private security advisory once available).

Include:

- Affected module / path and approximate commit or tag
- Impact (e.g. auth bypass, secret exposure, remote code execution)
- Steps to reproduce or a minimal proof of concept
- Whether a fix is already known

You should receive an acknowledgement within a few days. We will coordinate disclosure after a fix is available when that is reasonable.

## Secrets and local config

Never commit real passwords, JWT secrets, cloud keys, or `.env` files. Copy `.env.example` and use `application-local.yml` (gitignored) for local overrides. See the README for environment variable names.
