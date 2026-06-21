package com.server.commands;

import com.common.model.Route;
import com.common.network.RequestStatus;
import com.common.network.Response;
import com.server.db.RouteRepository;
import com.server.manager.CollectionManager;

import java.sql.SQLException;
import java.util.List;

public final class RemoveLower extends Command {
    private final CollectionManager collectionManager;
    private final RouteRepository routeRepository;

    public RemoveLower(CollectionManager collectionManager, RouteRepository routeRepository) {
        super("remove_lower", "Remove your routes lower than the specified route");
        this.collectionManager = collectionManager;
        this.routeRepository = routeRepository;
    }

    @Override
    public Response execute(String primitiveArg, Route routeArg, String userLogin) {
        if (routeArg == null) return new Response(RequestStatus.ERROR, "Route was not provided", null);

        try {
            List<Long> deletedIds = routeRepository.deleteLowerThan(routeArg, userLogin);
            collectionManager.removeByIds(deletedIds);
            return new Response(RequestStatus.SUCCESS, "Deleted routes: " + deletedIds.size(), null);
        } catch (SQLException e) {
            return new Response(RequestStatus.ERROR, "Database error: " + e.getMessage(), null);
        }
    }
}
