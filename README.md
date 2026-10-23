# 🔐 Spring Boot 3 + JWT Security & Role Authorization

Proyecto práctico de laboratorio para la implementación de un sistema de **Autenticación y Autorización** utilizando **Spring Boot 3**, **Spring Security**, **JSON Web Tokens (JWT)** y **MySQL**.

---

## 🚀 Características del Proyecto

- **Autenticación Stateless:** Control de sesiones mediante tokens JWT (sin estado en servidor).
- **Control de Acceso Basado en Roles (RBAC):** Gestión de permisos para roles `ROLE_USER`, `ROLE_ADMIN` y `ROLE_MODERATOR`.
- **Encriptación de Contraseñas:** Algoritmo `BCryptPasswordEncoder` para el almacenamiento seguro de credenciales.
- **Persistencia de Datos:** Integración con MySQL mediante Spring Data JPA e Hibernate ORM.
- **Validación de Entradas:** DTOs protegidos con Spring Validation.

---

## 🛠️ Tecnologías Utilizadas

- **Lenguaje:** Java 21
- **Framework:** Spring Boot 3.2.5
- **Seguridad:** Spring Security + JJWT (`io.jsonwebtoken` v0.11.5)
- **Base de Datos:** MySQL 8.x
- **ORM:** Spring Data JPA
- **Herramientas:** Maven, Lombok, Postman

---

## 🗄️ Configuración de la Base de Datos

Antes de ejecutar la aplicación, asegúrate de crear la base de datos e insertar los roles iniciales en MySQL:

```sql
CREATE DATABASE security_db;
USE security_db;

INSERT INTO roles (name) VALUES ('USER');
INSERT INTO roles (name) VALUES ('ADMIN');
INSERT INTO roles (name) VALUES ('MODERATOR');
