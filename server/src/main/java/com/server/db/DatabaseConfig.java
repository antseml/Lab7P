package com.server.db;

public record DatabaseConfig(String host, String database, String user, String password, int serverPort) {
    public static DatabaseConfig fromArgs(String[] args) {
        String user = firstNonBlank(System.getenv("DB_USER"), args.length > 0 ? args[0] : null);
        String password = firstNonBlank(System.getenv("DB_PASSWORD"), args.length > 1 ? args[1] : null);
        String host = firstNonBlank(System.getenv("DB_HOST"), args.length > 3 ? args[3] : "pg");
        String database = firstNonBlank(System.getenv("DB_NAME"), args.length > 4 ? args[4] : "studs");
        int serverPort = parsePort(firstNonBlank(System.getenv("SERVER_PORT"), args.length > 2 ? args[2] : "8080"));

        if (isBlank(user) || isBlank(password)) {
            throw new IllegalArgumentException("Usage: server <db-user> <db-password> [server-port] [db-host] [db-name]");
        }

        return new DatabaseConfig(host, database, user, password, serverPort);
    }

    public String jdbcUrl() {
        return "jdbc:postgresql://" + host + ":5432/" + database;
    }

    private static String firstNonBlank(String first, String second) {
        return !isBlank(first) ? first : second;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static int parsePort(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 8080;
        }
    }
}
