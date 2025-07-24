# GitHub Workflows

This directory contains GitHub Actions workflows for the flowrapp_backend project.

## Available Workflows

### 1. Pull Request Verification (`pr-verify.yml`)
Runs build and test processes on pull requests to ensure code quality.

### 2. SonarQube Analysis (`sonar.yml`)
Runs SonarQube analysis on the codebase to detect code quality issues.

### 3. Secret Detection (`gitleaks.yml`)
Scans the codebase for accidentally committed secrets or credentials.
