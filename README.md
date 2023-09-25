<div align="center">
  <img alt="Logo" src="./img/logo.svg" width="100">
  <h2>MSBuild DevKit for Rider</h2>

  <a href="https://plugins.jetbrains.com/plugin/18147-entity-framework-core-ui"><img src="https://img.shields.io/jetbrains/plugin/v/18147.svg?label=Rider&logoColor=black&colorB=0A7BBB&logo=data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4KPHN2ZyB2aWV3Qm94PSIwIDAgMjQgMjQiIHdpZHRoPSIyNCIgaGVpZ2h0PSIyNCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KICA8cmVjdCB3aWR0aD0iMjMuOTUxIiBoZWlnaHQ9IjIzLjk1MiIgc3R5bGU9InN0cm9rZS13aWR0aDogMHB4OyBzdHJva2U6IHJnYigyNTUsIDI1NSwgMjU1KTsgcGFpbnQtb3JkZXI6IGZpbGw7IGZpbGw6IHJnYigyNTUsIDI1NSwgMjU1KTsiLz4KICA8cGF0aCBkPSJNMCAwdjI0aDI0VjB6bTcuMDMxIDMuMTEzQTQuMDYzIDQuMDYzIDAgMCAxIDkuNzIgNC4xNGEzLjIzIDMuMjMgMCAwIDEgLjg0IDIuMjhBMy4xNiAzLjE2IDAgMCAxIDguNCA5LjU0bDIuNDYgMy42SDguMjhMNi4xMiA5LjlINC4zOHYzLjI0SDIuMTZWMy4xMmMxLjYxLS4wMDQgMy4yODEuMDA5IDQuODcxLS4wMDd6bTUuNTA5LjAwN2gzLjk2YzMuMTggMCA1LjM0IDIuMTYgNS4zNCA1LjA0IDAgMi44Mi0yLjE2IDUuMDQtNS4zNCA1LjA0aC0zLjk2em00LjA2OSAxLjk3NmMtLjYwNy4wMS0xLjIzNS4wMDQtMS44NDkuMDA0djYuMDZoMS43NGEyLjg4MiAyLjg4MiAwIDAgMCAzLjA2LTMgMi44OTcgMi44OTcgMCAwIDAtMi45NTEtMy4wNjR6TTQuMzE5IDUuMXYyLjg4SDYuNmMxLjA4IDAgMS42OC0uNiAxLjY4LTEuNDQgMC0uOTYtLjY2LTEuNDQtMS43NC0xLjQ0ek0yLjE2IDE5LjVoOVYyMWgtOVoiLz4KPC9zdmc+" alt="Version"></a>
  <a href="https://github.com/seclerp/rider-efcore/actions/workflows/build.yml"><img src="https://img.shields.io/github/actions/workflow/status/JetBrains/rider-efcore/build.yml?logo=github" alt="Build"></a>
</div>

---

<!-- Plugin description --> 

A plugin for Rider that enhances the MSBuild development experience

### Features

- Run/Debug MSBuild tasks using new Run Configuration
- "MSBuild Library" C# project template
- "MSBuild Item" (`.props`/`.targets`) templates

<!-- Plugin description end -->

### How to install

#### Using marketplace

1. Go to `Settings` / `Plugins` / `Marketplace`
1. Search for "MSBuild DevKit"
1. Click `Install`, then `Save`
1. After saving restart Rider

#### Using `.zip` file
1. Go to [**Releases**](https://github.com/seclerp/rider-efcore/releases)
2. Download the latest release of plugin for your edition of JetBrains Rider (Stable or EAP)
3. Proceed to `Settings` / `Plugins` / `⚙` / `Install plugin from disk`
4. Click `Save`
5. After saving restart Rider

### How to use

TODO

### Requirements

- IDE
  - JetBrains Rider **2023.2+ or latest EAP**

### Development

> **Note**: You should have JDK 17 and .NET SDK 7.0+ installed and configured.

#### Preparing

`./gradlew prepare` - generates RD protocol data for plugin internal communication and prepares sources for build.

#### Building plugin parts

`./gradlew buildPlugin`

It will build both frontend and backend parts.

#### Running

Next command will start instance of JetBrains Rider with plugin attached to it:

`./gradlew runIde`

### Contributing

Contributions are welcome! 🎉

It's better to create an issue with description of your bug/feature before creating pull requests.

### See also

- [**Marketplace page**](https://plugins.jetbrains.com/plugin/18147-entity-framework-core-ui)
- [**Changelog**](CHANGELOG.md)
