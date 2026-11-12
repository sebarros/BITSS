# bitss-catalogo

Microservicio **Catálogo de videojuegos** de BITSS. Spring Boot 4 / Java 21, puerto `8082`, base de datos `catalogo_db`.

Ruta publicada en el API Gateway de AWS: `/videojuegos`.

## Flujo del workflow

`.github/workflows/ci-cd.yml` se ejecuta en cada push a `main`:

1. **Pruebas**: levanta un MySQL de servicio y corre `mvn verify`.
2. **Imagen en Docker Hub**: construye y publica `<usuario>/bitss-catalogo:latest` y `<usuario>/bitss-catalogo:<sha>`.
3. **Despliegue en AWS**: por SSM, la instancia EC2 descarga la imagen y recrea el contenedor `catalogo` dentro de la red `bitss-net`.

En un pull request solo corre el job de pruebas.

## Secrets del repositorio

Settings > Secrets and variables > Actions > New repository secret.

| Secret | Descripción |
| --- | --- |
| `DOCKERHUB_USERNAME` | Usuario de Docker Hub |
| `DOCKERHUB_TOKEN` | Access token de Docker Hub (Read & Write) |
| `AWS_ACCESS_KEY_ID` | Credencial de AWS |
| `AWS_SECRET_ACCESS_KEY` | Credencial de AWS |
| `AWS_SESSION_TOKEN` | Solo si las credenciales son temporales (AWS Academy) |
| `AWS_REGION` | Por ejemplo `us-east-1` |
| `INSTANCE_ID` | ID de la instancia EC2 (`i-...`) |

El repositorio de Docker Hub `bitss-catalogo` debe ser público, o la instancia debe haber hecho `docker login` previamente.

## Ejecución local

```bash
docker run -d --name mysql -p 3307:3306 -e MYSQL_ROOT_PASSWORD=secret mysql:8.4
mvn spring-boot:run
```

Las variables `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` y `BITSS_JWT_SECRET` tienen valores por defecto para desarrollo local.
