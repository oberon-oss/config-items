# Config-items

### Quality status
[![Quality gate status](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_config-items&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=oberon-oss_config-items)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_config-items&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=oberon-oss_config-items)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_config-items&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=oberon-oss_config-items)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_config-items&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=oberon-oss_config-items)

[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_config-items&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=oberon-oss_config-items)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_config-items&metric=coverage)](https://sonarcloud.io/summary/new_code?id=oberon-oss_config-items)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_config-items&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=oberon-oss_config-items)

[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_config-items&metric=bugs)](https://sonarcloud.io/summary/new_code?id=oberon-oss_config-items)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_config-items&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=oberon-oss_config-items)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_config-items&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=oberon-oss_config-items)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_config-items&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=oberon-oss_config-items)

## Overview

Config-items is a Java library designed to manage application configuration settings with built-in support for persistence, type conversion, and default values. It provides a fluent API for defining configuration items and handles the complexities of mapping between application-level data types and storage representations.

## Core Concepts

### ConfigurationItem
The base interface `ConfigurationItem<I, A>` represents a single configuration entry.
- `I`: The type of the key (identifier).
- `A`: The type of the value used by the application.

### StorableConfigurationItem
Extends `ConfigurationItem` with persistence capabilities. It includes `load()` and `save()` methods that interact with a `StorageProvider`.

### StorageProvider
An interface for the underlying persistence mechanism. The library provides `PreferencesStorageProvider`, which uses the Java Preferences API.

### StorableConfigurationItemsRegistry
A thread-safe registry for managing multiple `StorableConfigurationItem` instances. It allows for bulk operations like `loadItems()` and `saveItems()`.

### ConfigurationItemKey
A typed record `ConfigurationItemKey<A>` used to identify a configuration item in the registry and provide type-safe access to its value.

## Getting Started

### 1. Initialize a Storage Provider and Factory

```java
Preferences prefs = Preferences.userNodeForPackage(MyClass.class);
StorageProvider provider = new PreferencesStorageProvider(prefs);
StorableConfigurationItemBuilderFactory factory = StorableConfigurationItemBuilderFactory.create(provider);
```

### 2. Define and Register Configuration Items

```java
StorableConfigurationItemsRegistry registry = new StorableConfigurationItemsRegistry();

// Define a key for type-safe access
ConfigurationItemKey<Integer> PORT_KEY = ConfigurationItemKey.of("server.port", Integer.class);

// Create and register the item
StorableConfigurationItem<String, String, Integer, Integer> portConfig = factory.<String, String, Integer, Integer>getInstance()
        .setItemID("server.port")
        .setDefaultValue(8080)
        .build();

registry.register(portConfig);
```

### 3. Accessing and Persisting Values

```java
// Load all registered items from storage
registry.loadItems();

// Type-safe access via the registry
int port = registry.getRequiredCurrentValue(PORT_KEY);

// Update value
registry.setCurrentValue(PORT_KEY, 9090);

// Save all changes to storage
registry.saveItems();
```

## Advanced Features

### Type Conversions

If your application data type differs from your storage type (e.g., storing an `Enum` as a `String`), you can provide conversion functions:

```java
StorableConfigurationItem<String, String, LogLevel, String> logLevelConfig = factory.<String, String, LogLevel, String>getInstance()
        .setItemID("log.level")
        .setApplicationDataType(LogLevel.class)
        .setStorageType(String.class)
        .setDefaultValue(LogLevel.INFO)
        .setToStorageType(level -> level.name())
        .setToDataType(name -> LogLevel.valueOf(name))
        .build();
```

### Auto-generation of Converters

The library can automatically generate converters for standard types (String, Integer, Boolean, etc.) when `autoGeneration` is enabled (default is `true`). It uses an internal registry to find appropriate converters, especially when converting to/from `String` for storage.

```java
// Builder will attempt to find a converter from String to Integer automatically
StorableConfigurationItem<String, String, Integer, String> timeoutConfig = factory.<String, String, Integer, String>getInstance()
        .setItemID("request.timeout")
        .setApplicationDataType(Integer.class)
        .setStorageType(String.class)
        .setDefaultValue(5000)
        .build();
```

## Supported Storage Types (PreferencesStorageProvider)

- `String`
- `Integer`
- `Long`
- `Float`
- `Double`
- `Boolean`
- `byte[]`

