# 🌳 GitHub Branching Strategy Template

This is your reusable strategy for managing branches like a pro. It works for solo devs, small teams, and even scale-ups.

## 🔀 Main Branches

| Branch | Purpose                |
|--------|------------------------|
| `main` | Production-ready code  |
| `dev`  | Ongoing integration    |

## 🌿 Working Branches

| Prefix       | Purpose                   | Example                    |
|--------------|---------------------------|----------------------------|
| `feature/`   | New features               | `feature/login-flow`       |
| `bugfix/`    | Non-prod bug fixes        | `bugfix/null-pointer`      |
| `hotfix/`    | Emergency prod fixes      | `hotfix/payment-bug`       |
| `release/`   | Release preparation       | `release/v1.0.0`           |
| `chore/`     | Housekeeping tasks        | `chore/update-docs`        |

## 🚀 Git Workflow Example

```bash
# Pull latest main/dev
git checkout main
git pull origin main

# Start a new feature branch
git checkout -b feature/user-auth

# Do your thing
git add .
git commit -m "feat: add login page"

# Push to remote
git push origin feature/user-auth

# Open Pull Request (feature ➡ dev or main)
```

## 🧠 Commit Convention (Optional but Recommended)

Use [Conventional Commits](https://www.conventionalcommits.org):

```text
feat: add new auth service
fix: handle 500 error in controller
docs: update README
refactor: simplify service logic
```

## 📦 Tagging a Release

```bash
git tag -a v1.0.0 -m "first stable release"
git push origin v1.0.0
```

## 📁 Optional .github/ folder

```
.github/
├── PULL_REQUEST_TEMPLATE.md
├── ISSUE_TEMPLATE/
│   ├── bug_report.md
│   └── feature_request.md
└── workflows/
    └── ci.yml
```

## 💡 Git Aliases

Add to your global git config (`~/.gitconfig`):

```ini
[alias]
  co = checkout
  br = branch
  ci = commit
  st = status
  lg = log --oneline --graph --decorate --all
```

## ✅ Tips

- Never commit directly to `main`
- Always branch from `main` or `dev`
- Use draft PRs if the work isn't ready
- Use meaningful branch names

> Clone this strategy across all your projects and you'll never be lost in Git again. ✊

```
# Checkout and update main
git checkout main
git pull origin main

# Merge your feature branch
git merge feature/something
git push origin main

```