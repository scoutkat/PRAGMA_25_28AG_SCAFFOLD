# Infrastructure Layer - Clean Architecture

Esta carpeta contiene los adaptadores de infraestructura organizados según los principios de Clean Architecture/Hexagonal Architecture.

## Estructura de Carpetas

### `driven-adapters/`
Contiene los adaptadores que implementan las interfaces del dominio para acceder a recursos externos.

#### `r2dbc-user-repository/`
- **R2DBCUserRepository**: Implementación reactiva del repositorio de usuarios
- Maneja la persistencia de datos de usuarios en PostgreSQL
- Utiliza R2DBC para acceso reactivo a la base de datos

#### `r2dbc-loan-repository/`
- **R2DBCLoanRequestRepository**: Implementación reactiva del repositorio de solicitudes de préstamo
- Maneja la persistencia de datos de préstamos en PostgreSQL
- Utiliza R2DBC para acceso reactivo a la base de datos

### `entry-points/`
Contiene los puntos de entrada de la aplicación (controladores REST).

#### `user-controller/`
- **UserController**: Controlador REST para operaciones de usuarios
- Maneja las peticiones HTTP para registro, validación y consulta de usuarios
- Implementa programación reactiva con WebFlux

#### `loan-controller/`
- **LoanRequestController**: Controlador REST para operaciones de solicitudes de préstamo
- Maneja las peticiones HTTP para creación y consulta de préstamos
- Implementa programación reactiva con WebFlux

### `helpers/`
Contiene clases auxiliares y utilidades compartidas.

#### `validation-helper/`
- **ValidationHelper**: Utilidades para validación de datos
- Métodos reutilizables para validación de email, teléfono, rangos, etc.
- Sigue el principio DRY (Don't Repeat Yourself)

#### `response-helper/`
- **ResponseHelper**: Utilidades para creación de respuestas API estandarizadas
- Métodos para crear respuestas de éxito y error consistentes
- Incluye versiones reactivas para WebFlux

#### `logging-helper/`
- **LoggingHelper**: Utilidades para logging estandarizado
- Manejo de MDC (Mapped Diagnostic Context)
- Patrones de logging consistentes en toda la aplicación

## Principios Aplicados

### 1. **Separación de Responsabilidades**
- Cada adaptador tiene una responsabilidad específica
- Los controladores solo manejan HTTP, no lógica de negocio
- Los repositorios solo manejan persistencia, no validaciones

### 2. **Inversión de Dependencias**
- Los adaptadores implementan interfaces del dominio
- El dominio no depende de la infraestructura
- La infraestructura depende del dominio

### 3. **Programación Reactiva**
- Todos los adaptadores utilizan WebFlux y R2DBC
- Manejo no bloqueante de operaciones I/O
- Uso de Mono y Flux para streams reactivos

### 4. **Reutilización de Código**
- Helpers compartidos para funcionalidades comunes
- Patrones consistentes en toda la aplicación
- Reducción de duplicación de código

## Uso en Microservicios

### Authentication Service
```java
// Usa el UserController y R2DBCUserRepository
@RestController
public class UserController {
    // Implementación del controlador
}
```

### Loan Requests Service
```java
// Usa el LoanRequestController y R2DBCLoanRequestRepository
@RestController
public class LoanRequestController {
    // Implementación del controlador
}
```

## Configuración

### Dependencias Maven
Cada adaptador debe incluir las dependencias necesarias:

```xml
<dependencies>
    <!-- Spring Boot WebFlux -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>
    
    <!-- R2DBC PostgreSQL -->
    <dependency>
        <groupId>io.r2dbc</groupId>
        <artifactId>r2dbc-postgresql</artifactId>
    </dependency>
    
    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
</dependencies>
```

### Configuración de Base de Datos
```yaml
spring:
  r2dbc:
    url: r2dbc:postgresql://localhost:5432/database_name
    username: username
    password: password
```

## Testing

### Unit Tests
Cada adaptador debe tener tests unitarios:

```java
@ExtendWith(MockitoExtension.class)
class R2DBCUserRepositoryTest {
    // Tests para el repositorio
}
```

### Integration Tests
Tests de integración para validar el comportamiento completo:

```java
@SpringBootTest
class UserControllerIntegrationTest {
    // Tests de integración
}
```

## Monitoreo y Logging

### Logging Estructurado
```java
// Usar LoggingHelper para logging consistente
LoggingHelper.logOperationStart(logger, "user-registration", "email: user@example.com");
```

### Métricas de Performance
```java
// Usar LoggingHelper para métricas
LoggingHelper.logPerformance(logger, "database-query", duration, "user-lookup");
```

## Mejores Prácticas

1. **Siempre usar interfaces del dominio** en lugar de implementaciones concretas
2. **Manejar errores de forma reactiva** usando `.onErrorResume()`
3. **Logging consistente** usando LoggingHelper
4. **Validación de entrada** usando ValidationHelper
5. **Respuestas estandarizadas** usando ResponseHelper
6. **Tests completos** para todos los adaptadores
7. **Documentación Swagger** para todos los endpoints
8. **Configuración externalizada** usando application.yml

## Extensibilidad

Para agregar nuevos adaptadores:

1. Crear nueva carpeta en `driven-adapters/` o `entry-points/`
2. Implementar la interfaz del dominio correspondiente
3. Agregar tests unitarios e integración
4. Documentar el nuevo adaptador
5. Actualizar la configuración si es necesario
