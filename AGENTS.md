# Agent Instructions

When the user asks to correct or fix something in SIGESI:

1. Make the backend or infrastructure fix on `develop`.
2. Run relevant checks before publishing. Prefer targeted tests first, then `./mvnw test` when practical.
3. Commit and push `develop` to `origin`.
4. Let GitHub Actions deploy `develop` to `https://sigesi-test.ezioalves.cloud`.
5. Report the pushed commit, checks run, and test-server verification status.

Use the existing GitHub Actions pipeline for normal test-server deploys. Do not deploy directly to the VPS unless the user explicitly asks for an emergency/manual deploy.
