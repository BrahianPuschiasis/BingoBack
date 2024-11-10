# Bingo Project

Este es un proyecto de Bingo en tiempo real desarrollado con **Java** y **Spring Boot** que utiliza **Keycloak** para la autenticación y autorización. La aplicación emplea **WebSocket** para gestionar la comunicación en tiempo real entre los usuarios y está estructurada bajo el patrón **MVC (Model-View-Controller)**.

## Características

- **Autenticación y Autorización**: Configurada con **Keycloak** para la gestión de usuarios y roles.
- **WebSocket**: Comunicación en tiempo real entre los usuarios para un juego fluido y sin interrupciones.
- **Arquitectura MVC**: Implementación de la lógica de negocio, controladores y vistas siguiendo el patrón MVC.

## Requisitos Previos

- **Java** 17+
- **Docker** (para levantar el contenedor de Keycloak)
- **Maven** (para la gestión de dependencias)
- **DockerFile** (correr el DockerFile para instalar Keycloak, posteriormente importar el reino, ambos archivos estan dentro de la carpeta "KeyCloak")
