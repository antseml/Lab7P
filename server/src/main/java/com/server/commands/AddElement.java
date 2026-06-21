package com.server.commands;

import com.common.model.Route;
import com.common.network.RequestStatus;
import com.common.network.Response;
import com.server.db.RouteRepository;
import com.server.manager.CollectionManager;

import java.sql.SQLException;

public final class AddElement extends Command {
    private final CollectionManager collectionManager;
    private final RouteRepository routeRepository;

    public AddElement(CollectionManager collectionManager, RouteRepository routeRepository) {
        super("add", "Create a route and add it to the collection");
        this.collectionManager = collectionManager;
        this.routeRepository = routeRepository;
    }

    @Override
    public Response execute(String primitiveArg, Route routeArg, String userLogin) {
        if (routeArg == null) return new Response(RequestStatus.ERROR, "Route was not provided", null);

        try {
            Route savedRoute = routeRepository.insert(routeArg, userLogin);
            collectionManager.inputElement(savedRoute);
            return new Response(RequestStatus.SUCCESS, "Route added", null);
        } catch (SQLException e) {
            return new Response(RequestStatus.ERROR, "Database error: " + e.getMessage(), null);
        }
    }
}
