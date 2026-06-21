package com.server.commands;

import com.common.model.Route;
import com.common.network.RequestStatus;
import com.common.network.Response;
import com.server.db.RouteRepository;
import com.server.manager.CollectionManager;

import java.sql.SQLException;

public final class RemoveByID extends Command {
    private final CollectionManager collectionManager;
    private final RouteRepository routeRepository;

    public RemoveByID(CollectionManager collectionManager, RouteRepository routeRepository) {
        super("remove_by_id {ID}", "Remove your route by ID");
        this.collectionManager = collectionManager;
        this.routeRepository = routeRepository;
    }

    @Override
    public Response execute(String primitiveArg, Route routeArg, String userLogin) {
        try {
            if (primitiveArg.isEmpty()) return new Response(RequestStatus.ERROR, "Enter ID", null);
            long id = Long.parseLong(primitiveArg);

            Route route = collectionManager.findById(id);
            if (route == null) return new Response(RequestStatus.ERROR, "Route with this ID does not exist", null);
            if (!userLogin.equals(route.getOwner())) {
                return new Response(RequestStatus.ERROR, "You can modify only your own routes", null);
            }

            if (!routeRepository.deleteById(id, userLogin)) {
                return new Response(RequestStatus.ERROR, "Route was not deleted", null);
            }
            collectionManager.removeElement(route);
            return new Response(RequestStatus.SUCCESS, "Route deleted", null);
        } catch (NumberFormatException e) {
            return new Response(RequestStatus.ERROR, "ID must be a number", null);
        } catch (SQLException e) {
            return new Response(RequestStatus.ERROR, "Database error: " + e.getMessage(), null);
        }
    }
}
