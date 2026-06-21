# Lab 7 notes

The server now stores the collection in PostgreSQL instead of XML.

## Server

```bash
java -jar server/target/server-1.0-SNAPSHOT.jar <db-user> <db-password> [server-port] [db-host] [db-name]
```

Defaults:

- `server-port=8080`
- `db-host=pg`
- `db-name=studs`

The same values can be supplied through environment variables:

- `DB_USER`
- `DB_PASSWORD`
- `SERVER_PORT`
- `DB_HOST`
- `DB_NAME`

## Client

```bash
java -jar client/target/client-1.0-SNAPSHOT.jar [host] [port]
```

Before regular commands use one of:

```text
register <login> <password>
login <login> <password>
```

After successful registration or login the client sends the saved login and password with each request.
