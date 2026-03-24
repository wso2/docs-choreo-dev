# Release Management

Release management is the process of managing, planning, scheduling and controlling a software build through different stages and environments; it includes testing and deploying software releases.

## Release

A Release represents a uniquely identifiable, environment-independent bundle of runtime artifacts and configurations created when a build is first deployed.

## Release Deployment

A Release Deployment represents a Release deployed to a specific environment with environment-dependent configuration. A given release can be deployed multiple times to the same or different environments, and each deployment produces a distinct release deployment due to differences in environment configuration.

## Release Naming Convention

A structured naming format (`<date>-<release number>.<config-revision>.<deployment-count>`, e.g., 20250404-1.1.1) is being used for maintaining clarity and consistency across different environments.

Ex: 20260319-1.1.1

- Date: Calendar date on which the Release is created (format YYYYMMDD, default UTC  timezone).
- Counter: Sequential counter indicating how many Releases were created on the same Date
- Configuration revision: Version identifier of the environment‑specific configuration snapshot applied to the deployment.
- Deployment number: Counter that increments each time the same deployment is deployed (or re‑deployed) to a specific environment.

The `<date>-<release number>` (which is identified as the release) will not be changed during promotion, allowing clear identification of the release being deployed in all environments.
