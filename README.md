# BITSS

## Requisitos
- Docker Desktop o Docker Engine con Docker Compose
- Puertos 3307 y 8080-8087 disponibles

## Iniciar todos los servicios
Desde la carpeta raíz del proyecto:

```bash
docker compose up --build
```

La API Gateway queda disponible en `http://localhost:8080`. Los microservicios también quedan publicados individualmente en los puertos 8081 a 8087. MySQL queda publicado en el puerto 3307.

## Detener los servicios
```bash
docker compose down
```

Para detenerlos y borrar también los datos persistidos de MySQL:
```bash
docker compose down -v
```

Las URLs entre microservicios usan los nombres de servicio de Docker Compose. Las configuraciones mantienen valores predeterminados para ejecutar los servicios localmente fuera de Docker.
