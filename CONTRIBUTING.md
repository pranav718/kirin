# Contributing to Kirin

Thank you for your interest in contributing to Kirin!

## Development Workflow

1. Fork and clone the repository.
2. Ensure you have **Android Studio Ladybug (2024.2+)** or newer installed.
3. Open the project and sync Gradle dependencies.
4. Create a descriptive feature branch:
   ```bash
   git checkout -b feat/your-feature-name
   ```

## Commit Message Guidelines

This project strictly follows the **Conventional Commits** specification:
- `feat:` A new user-facing feature
- `fix:` A bug fix
- `docs:` Documentation only changes
- `style:` Code style changes (formatting, missing semi-colons, etc.)
- `refactor:` Code change that neither fixes a bug nor adds a feature
- `perf:` Performance improvements
- `test:` Adding or refactoring tests
- `build:` Build system or external dependency changes
- `ci:` Continuous integration configuration
- `chore:` Maintenance tasks

## Code Style

- Format Kotlin code following the `.editorconfig` rules.
- Maintain Material Design 3 guidelines and keep views accessible.
