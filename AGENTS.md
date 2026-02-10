# 🛡️ MANDATORY GIT WORKFLOW & VERSION CONTROL

## 1. Repository Initialization
- At the start of the session, check if a git repository exists.
- If not, initialize one (`git init`) and immediately create a `.gitignore` relevant to the project's language/framework.

## 2. Branch Management (CRITICAL)
- **Never work directly on `main`, `master`, or `dev`.**
- **Action:** Immediately create and switch to a new feature branch relevant to the current task.
    - Naming convention: `feature/your-feature-name` or `fix/your-bug-fix`.
    - Example: `feature/init-project-structure`, `fix/login-bug`.
- This ensures you do not get blocked by protected branch policies or require Pull Requests for simple updates.

## 3. Granular "Atomic" Commits
- **Rule:** You must treat git commits as "Save Points."
- **Constraint:** Do NOT wait until the end of a task to commit. Do NOT dump multiple features in one commit.
- **Trigger:** Perform a git commit immediately after finishing any single logical step.
    - *Good:* Commit 1: "feat: Create User model", Commit 2: "feat: Add Login route".
    - *Bad:* Commit 1: "feat: Build entire authentication system".

## 4. Commit Message Standards
- Use **Conventional Commits** format:
    - `feat: ...` for a new feature.
    - `fix: ...` for a bug fix.
    - `docs: ...` for documentation changes.
    - `style: ...` for formatting (white-space, formatting, missing semi-colons, etc).
    - `refactor: ...` for restructuring code without changing behavior.
    - `chore: ...` for updating build tasks, package manager configs, etc.

## 5. Safety Protocol
- Before committing, ensure the code is syntactically correct.
- If a previous step broke the build, fix it *before* moving to the next feature.
