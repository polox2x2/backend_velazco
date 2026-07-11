# velazco-back
Intranet - Dulceria y Pasteleria Velazco
This is just a university project close to reality.
#   b a c k e n d _ v e l a z c o  

## Variables de Railway

Para desplegar este backend en Railway y conectarlo a una base de datos PostgreSQL, debes crear las siguientes variables de entorno en el servicio de tu aplicación Java. 

Se recomienda agregarlas usando la opción **Add Variable Reference** de Railway para que enlacen con tu servicio de base de datos (sustituye `Postgres` por el nombre exacto de tu servicio de base de datos en Railway):

`DATABASE_URL=jdbc:postgresql://${{Postgres.PGHOST}}:${{Postgres.PGPORT}}/${{Postgres.PGDATABASE}}`

`POSTGRES_USER=${{Postgres.PGUSER}}`

`POSTGRES_PASSWORD=${{Postgres.PGPASSWORD}}`

`PGDATABASE=${{Postgres.PGDATABASE}}`

`SPRING_PROFILES_ACTIVE=prod`

`JPA_DDL_AUTO=update`