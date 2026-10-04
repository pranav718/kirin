# Kirin

> Native Android Developer Portfolio & Project Telemetry Client

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple.svg)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Platform-Android_15_--_API_36-green.svg)](https://developer.android.com)
[![Gradle](https://img.shields.io/badge/Gradle-8.13-blue.svg)](https://gradle.org)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**Kirin** is a high-performance native Android application built in **Kotlin** and **XML Views** adhering to Google's Recommended Architecture (MVVM, StateFlow, Coroutines, Repository pattern, Material Design 3). It serves as an offline-first mobile client and telemetry hub for software engineer **Pranav Ray** ([portfolio.knightkun.codes](https://portfolio.knightkun.codes)).

## Key Features

- **Home Dashboard:** Collapsing hero header, developer status metrics, quick CTA actions, and featured systems carousel.
- **Projects Explorer:** Real-time debounced search, status filter chips (`All`, `Live`, `In Progress`), and card list for 11 production repositories.
- **Project Detail View:** In-depth architectural narratives, complete tech stack tag cloud, and direct action intents for GitHub and live demos.
- **Blog & Publications:** Curated technical writing feed with Medium clap metrics and sub-second in-app Chrome Custom Tabs.
- **Developer Profile & Skill Matrix:** 29 categorized skills across languages, backend, databases, frontend, and devops, alongside platform connections.

## Technology Stack

- **Language:** Kotlin 2.0.21
- **UI Toolkit:** Android XML Views + Material Design 3 (`com.google.android.material:1.14.0`)
- **Architecture:** MVVM + Clean Architecture + Repository Pattern
- **Async & Reactive Streams:** Kotlin Coroutines + StateFlow
- **Image Pipeline:** Coil 3 with OkHttp disk caching
- **Serialization:** kotlinx-serialization-json
- **External Web:** AndroidX Browser (Chrome Custom Tabs)

## Author

- **Pranav Ray** ([@pranav718](https://github.com/pranav718))
- Web Portfolio: [portfolio.knightkun.codes](https://portfolio.knightkun.codes)
