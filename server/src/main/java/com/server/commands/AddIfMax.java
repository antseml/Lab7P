package com.server.commands;

import com.common.model.Route;
import com.common.network.RequestStatus;
import com.common.network.Response;
import com.server.db.RouteRepository;
import com.server.manager.CollectionManager;

import java.sql.SQLException;

public final class AddIfMax extends Command {
    private final CollectionManager collectionManager;
    private final RouteRepository routeRepository;

    public AddIfMax(CollectionManager collectionManager, RouteRepository routeRepository) {
        super("add_if_max", "Add a route if it is greater than the current maximum");
        this.collectionManager = collectionManager;
        this.routeRepository = routeRepository;
    }

    @Override
    public Response execute(String primitiveArg, Route routeArg, String userLogin) {
        if (routeArg == null) return new Response(RequestStatus.ERROR, "Route was not provided", null);

        Route maxRoute = collectionManager.maxElement();
        if (maxRoute != null && routeArg.compareTo(maxRoute) <= 0) {
            return new Response(RequestStatus.ERROR, "Route was not added because it is not greater than max", null);
        }

        try {
            Route savedRoute = routeRepository.insert(routeArg, userLogin);
            collectionManager.inputElement(savedRoute);
            return new Response(RequestStatus.SUCCESS, "Route added", null);
        } catch (SQLException e) {
            return new Response(RequestStatus.ERROR, "Database error: " + e.getMessage(), null);
        }
    }
}
