# bitss-infra

Infraestructura compartida de BITSS: API Gateway de AWS, preparación de la instancia EC2 y stack local con Docker Compose.

## Rutas del API Gateway

| Ruta | Microservicio | Puerto en EC2 |
| --- | --- | --- |
| `/usuarios` | bitss-usuario | 8081 |
| `/videojuegos` | bitss-catalogo | 8082 |
| `/carrito` | bitss-carro | 8083 |
| `/pedidos` | bitss-gestion | 8084 |
| `/pagos` | bitss-pagos | 8085 |
| `/coleccion` | bitss-coleccion | 8086 |
| `/notificaciones` | bitss-notificaciones | 8087 |

El API Gateway es un HTTP API (`cloudformation/api-gateway.yaml`) que hace proxy a `http://<EC2_PUBLIC_HOST>:<puerto>`. Reemplaza al microservicio Spring Cloud Gateway anterior.

## Preparar la instancia EC2 (una sola vez)

Requisitos: Amazon Linux 2023, rol de instancia con `AmazonSSMManagedInstanceCore`, IP elástica asociada y 4 GB de RAM o más (7 servicios Java más MySQL).

Security group de entrada: puertos TCP 8081 a 8087 hacia el origen que corresponda (API Gateway HTTP no tiene IPs fijas, por lo que normalmente se abre a `0.0.0.0/0`; el acceso lo protege el JWT).

```bash
sudo MYSQL_ROOT_PASSWORD='clave_mysql' BITSS_JWT_SECRET='secreto_largo_aleatorio' bash setup.sh
```

Esto instala Docker, crea la red `bitss-net`, deja las variables compartidas en `/opt/bitss/.env` y levanta MySQL. Los workflows de cada microservicio hacen el resto.

## Desplegar el API Gateway

Secrets del repositorio: `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `AWS_SESSION_TOKEN` (si son temporales), `AWS_REGION`, `EC2_PUBLIC_HOST`.

El workflow `api-gateway` corre al hacer push a `main` y deja la URL final en el resumen de la ejecución.

## Stack local

```bash
cp .env.example .env
docker compose up
```

Usa las imágenes publicadas en Docker Hub.
