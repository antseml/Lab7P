package com.server.commands;

import com.common.model.Route;
import com.common.network.RequestStatus;
import com.common.network.Response;
import com.server.db.RouteRepository;
import com.server.manager.CollectionManager;

import java.sql.SQLException;

public final class Clear extends Command {
    private final CollectionManager collectionManager;
    private final RouteRepository routeRepository;

    public Clear(CollectionManager collectionManager, RouteRepository routeRepository) {
        super("clear", "Remove all your routes from the collection");
        this.collectionManager = collectionManager;
        this.routeRepository = routeRepository;
    }

    @Override
    public Response execute(String primitiveArg, Route routeArg, String userLogin) {
        try {
            int deleted = routeRepository.clearOwned(userLogin);
            collectionManager.clearOwned(userLogin);
            return new Response(RequestStatus.SUCCESS, "Deleted your routes: " + deleted, null);
        } catch (SQLException e) {
            return new Response(RequestStatus.ERROR, "Database error: " + e.getMessage(), null);
        }
    }
}
