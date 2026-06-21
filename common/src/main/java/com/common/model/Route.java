package com.common.model;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Collection;

public final class Route implements Comparable<Route>, Serializable {
    private static long idCounter = 1;
    private long id;
    private String name;
    private Coordinates coordinates;
    private ZonedDateTime creationDate;
    private Location from;
    private Location to;
    private Float distance;
    private String owner;

    public Route(String name, Coordinates coordinates, Location from, Location to, Float distance) {
        if (name == null || name.isEmpty()) throw new IllegalArgumentException();
        if (coordinates == null) throw new IllegalArgumentException();
        if (to == null) throw new IllegalArgumentException();
        if (distance == null || distance <= 1) throw new IllegalArgumentException();

        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = ZonedDateTime.now();
        this.from = from;
        this.to = to;
        this.distance = distance;
    }

    public Route() {}

    public static void updateIdCounter(Collection<Route> routes) {
        if (routes == null || routes.isEmpty()) {
            idCounter = 1;
            return;
        }

        long maxId = routes.stream()
                .mapToLong(Route::getId)
                .max()
                .orElse(0);

        idCounter = maxId + 1;
    }

    public static long generateId() {
        return idCounter++;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) throw new IllegalArgumentException("Invalid name");
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        if (coordinates == null) throw new IllegalArgumentException("Coordinates cannot be null");
        this.coordinates = coordinates;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Location getFrom() {
        return from;
    }

    public void setFrom(Location from) {
        this.from = from;
    }

    public Location getTo() {
        return to;
    }

    public void setTo(Location to) {
        if (to == null) throw new IllegalArgumentException();
        this.to = to;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        if (distance == null || distance <= 1) throw new IllegalArgumentException();
        this.distance = distance;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "ID route - " + id
                + ";\nName - " + name
                + ";\nCoordinates: " + coordinates
                + ";\nCreation date - " + creationDate
                + ";\nFrom: " + from
                + ";\nTo: " + to
                + ";\nDistance - " + distance
                + ";\nOwner - " + owner
                + ";\n";
    }

    @Override
    public int compareTo(Route other) {
        if (other == null) return 1;
        if (this.distance == null || other.distance == null) return 0;
        return this.distance.compareTo(other.distance);
    }
}
