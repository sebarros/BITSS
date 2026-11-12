# BITSS

Cada carpeta es un repositorio independiente de GitHub.

| Carpeta / repo | Descripción | Puerto | Ruta en API Gateway |
| --- | --- | --- | --- |
| `bitss-usuario` | Registro, login y gestión de usuarios (emite el JWT) | 8081 | `/usuarios` |
| `bitss-catalogo` | Catálogo de videojuegos | 8082 | `/videojuegos` |
| `bitss-carro` | Carrito de compras | 8083 | `/carrito` |
| `bitss-gestion` | Gestión de pedidos | 8084 | `/pedidos` |
| `bitss-pagos` | Pagos | 8085 | `/pagos` |
| `bitss-coleccion` | Colección de videojuegos del usuario | 8086 | `/coleccion` |
| `bitss-notificaciones` | Notificaciones | 8087 | `/notificaciones` |
| `bitss-infra` | API Gateway (CloudFormation), setup de EC2, compose local | - | - |

## Arquitectura

```
Cliente -> AWS API Gateway (HTTP API) -> EC2 (Docker, red bitss-net) -> usuario / catalogo / carro / gestion / pagos / coleccion / notificaciones -> MySQL
```

Cada microservicio, al hacer push a `main`, ejecuta su workflow: pruebas, imagen en Docker Hub y despliegue en la EC2 vía SSM.

## Orden de puesta en marcha

1. Crear en GitHub 8 repositorios vacíos con los mismos nombres de las carpetas.
2. Crear el repositorio público en Docker Hub para cada imagen, o dejar que se cree al primer push.
3. Lanzar la EC2 y ejecutar `bitss-infra/ec2/setup.sh` (ver `bitss-infra/README.md`).
4. Cargar los secrets en cada repo (tabla en el README de cada microservicio).
5. Subir el código:

```bash
./subir-repos.sh <tu_usuario_github>
```

6. Revisar la pestaña Actions de cada repo. Al terminar `bitss-infra` aparece la URL del API Gateway.

## Prueba rápida

```bash
curl -X POST https://<api-id>.execute-api.<region>.amazonaws.com/usuarios/login \
  -H "Content-Type: application/json" \
  -d '{"correo":"a@a.cl","password":"123456"}'
```
