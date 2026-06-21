package com.server.manager;

import com.common.model.Route;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class CollectionManager {
    private final ZonedDateTime initialTime;
    private final Collection<Route> routes = Collections.synchronizedSet(new HashSet<>());

    public CollectionManager() {
        initialTime = ZonedDateTime.now();
    }

    public Route findById(long id) {
        synchronized (routes) {
            return routes.stream()
                    .filter(route -> route.getId() == id)
                    .findFirst()
                    .orElse(null);
        }
    }

    public String findByName(String name) {
        Pattern pattern = Pattern.compile(name, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        synchronized (routes) {
            return routes.stream()
                    .filter(route -> {
                        Matcher matcher = pattern.matcher(route.getName());
                        return matcher.find();
                    })
                    .map(Route::toString)
                    .collect(Collectors.joining("\n"));
        }
    }

    public Double averageDistance() {
        synchronized (routes) {
            return routes.stream()
                    .mapToDouble(Route::getDistance)
                    .average()
                    .orElse(0);
        }
    }

    public String descendSort() {
        synchronized (routes) {
            return routes.stream()
                    .sorted(Comparator.reverseOrder())
                    .map(Route::toString)
                    .collect(Collectors.joining("\n"));
        }
    }

    public Route maxElement() {
        synchronized (routes) {
            return routes.stream()
                    .max(Route::compareTo)
                    .orElse(null);
        }
    }

    public void inputElement(Route route) {
        routes.add(route);
    }

    public void removeElement(Route route) {
        routes.remove(route);
    }

    public void removeByIds(Collection<Long> ids) {
        routes.removeIf(route -> ids.contains(route.getId()));
    }

    public void clearOwned(String owner) {
        routes.removeIf(route -> Objects.equals(route.getOwner(), owner));
    }

    public String getInfo() {
        return "Collection type: " + routes.getClass().getSimpleName()
                + "\nInitialization time: " + initialTime
                + "\nSize: " + routes.size();
    }

    public void setRoutes(Collection<Route> routes) {
        synchronized (this.routes) {
            this.routes.clear();
            this.routes.addAll(routes);
        }
    }

    public Collection<Route> getRoutes() {
        return routes;
    }

    public void clearCollection() {
        routes.clear();
    }

    @Override
    public String toString() {
        synchronized (routes) {
            if (routes.isEmpty()) return "Collection is empty\n";

            return routes.stream()
                    .sorted(Comparator.comparing(Route::getId))
                    .map(Route::toString)
                    .collect(Collectors.joining("\n"));
        }
    }
}
