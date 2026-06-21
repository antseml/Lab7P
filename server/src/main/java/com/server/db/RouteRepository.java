package com.server.db;

import com.common.model.Coordinates;
import com.common.model.Location;
import com.common.model.Route;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class RouteRepository {
    private final DatabaseManager databaseManager;

    public RouteRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public Collection<Route> loadAll() throws SQLException {
        List<Route> routes = new ArrayList<>();
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM lab7_routes ORDER BY id");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                routes.add(mapRoute(resultSet));
            }
        }
        return routes;
    }

    public Route insert(Route route, String owner) throws SQLException {
        String sql = """
                INSERT INTO lab7_routes (
                    name, coordinates_x, coordinates_y, creation_date,
                    from_x, from_y, from_z, to_x, to_y, to_z, distance, owner_login
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING *
                """;
        route.setCreationDate(ZonedDateTime.now());
        route.setOwner(owner);

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            fillRouteFields(statement, route);
            statement.setString(12, owner);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return mapRoute(resultSet);
            }
        }
    }

    public boolean update(long id, Route route, String owner) throws SQLException {
        String sql = """
                UPDATE lab7_routes SET
                    name = ?, coordinates_x = ?, coordinates_y = ?,
                    from_x = ?, from_y = ?, from_z = ?,
                    to_x = ?, to_y = ?, to_z = ?, distance = ?
                WHERE id = ? AND owner_login = ?
                """;
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, route.getName());
            statement.setDouble(2, route.getCoordinates().getX());
            statement.setInt(3, route.getCoordinates().getY());
            setLocation(statement, 4, route.getFrom());
            statement.setLong(7, route.getTo().getX());
            statement.setInt(8, route.getTo().getY());
            statement.setFloat(9, route.getTo().getZ());
            statement.setFloat(10, route.getDistance());
            statement.setLong(11, id);
            statement.setString(12, owner);
            return statement.executeUpdate() == 1;
        }
    }

    public boolean deleteById(long id, String owner) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM lab7_routes WHERE id = ? AND owner_login = ?")) {
            statement.setLong(1, id);
            statement.setString(2, owner);
            return statement.executeUpdate() == 1;
        }
    }

    public List<Long> deleteLowerThan(Route route, String owner) throws SQLException {
        List<Long> ids = new ArrayList<>();
        String sql = "DELETE FROM lab7_routes WHERE distance < ? AND owner_login = ? RETURNING id";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setFloat(1, route.getDistance());
            statement.setString(2, owner);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ids.add(resultSet.getLong("id"));
                }
            }
        }
        return ids;
    }

    public int clearOwned(String owner) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM lab7_routes WHERE owner_login = ?")) {
            statement.setString(1, owner);
            return statement.executeUpdate();
        }
    }

    private void fillRouteFields(PreparedStatement statement, Route route) throws SQLException {
        statement.setString(1, route.getName());
        statement.setDouble(2, route.getCoordinates().getX());
        statement.setInt(3, route.getCoordinates().getY());
        statement.setTimestamp(4, Timestamp.from(route.getCreationDate().toInstant()));
        setLocation(statement, 5, route.getFrom());
        statement.setLong(8, route.getTo().getX());
        statement.setInt(9, route.getTo().getY());
        statement.setFloat(10, route.getTo().getZ());
        statement.setFloat(11, route.getDistance());
    }

    private void setLocation(PreparedStatement statement, int startIndex, Location location) throws SQLException {
        if (location == null) {
            statement.setNull(startIndex, Types.BIGINT);
            statement.setNull(startIndex + 1, Types.INTEGER);
            statement.setNull(startIndex + 2, Types.REAL);
            return;
        }
        statement.setLong(startIndex, location.getX());
        statement.setInt(startIndex + 1, location.getY());
        statement.setFloat(startIndex + 2, location.getZ());
    }

    private Route mapRoute(ResultSet resultSet) throws SQLException {
        Route route = new Route();
        route.setId(resultSet.getLong("id"));
        route.setName(resultSet.getString("name"));
        route.setCoordinates(new Coordinates(
                resultSet.getDouble("coordinates_x"),
                resultSet.getInt("coordinates_y")
        ));
        route.setCreationDate(resultSet.getTimestamp("creation_date").toInstant().atZone(ZoneId.systemDefault()));
        route.setFrom(readNullableLocation(resultSet, "from_x", "from_y", "from_z"));
        route.setTo(new Location(
                resultSet.getLong("to_x"),
                resultSet.getInt("to_y"),
                resultSet.getFloat("to_z")
        ));
        route.setDistance(resultSet.getFloat("distance"));
        route.setOwner(resultSet.getString("owner_login"));
        return route;
    }

    private Location readNullableLocation(ResultSet resultSet, String xColumn, String yColumn, String zColumn) throws SQLException {
        long x = resultSet.getLong(xColumn);
        if (resultSet.wasNull()) {
            return null;
        }
        int y = resultSet.getInt(yColumn);
        float z = resultSet.getFloat(zColumn);
        return new Location(x, y, z);
    }
}
