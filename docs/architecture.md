# Architecture

## Style
Feature-based package structure.

Each feature contains the layers it needs: controller, service, repository, dto, entity.

This keeps business logic isolated by domain and makes the project easier to scale.

## Shared packages
- config - Spring configuration
- security - JWT filters, authentication setup
- common - shared DTOs, exceptions, global handlers, pagination

## Features
- auth - registration, login, refresh token flow
- user - user persistence
- product - product CRUD
- order - order creation and status management