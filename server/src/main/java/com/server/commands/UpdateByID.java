package com.server.commands;

import com.common.model.Route;
import com.common.network.RequestStatus;
import com.common.network.Response;
import com.server.db.RouteRepository;
import com.server.manager.CollectionManager;

import java.sql.SQLException;

public final class UpdateByID extends Command {
    private final CollectionManager collectionManager;
    private final RouteRepository routeRepository;

    public UpdateByID(CollectionManager collectionManager, RouteRepository routeRepository) {
        super("update {ID}", "Update your route by ID");
        this.collectionManager = collectionManager;
        this.routeRepository = routeRepository;
    }

    @Override
    public Response execute(String primitiveArg, Route routeArg, String userLogin) {
        try {
            if (primitiveArg.isEmpty()) return new Response(RequestStatus.ERROR, "Enter ID", null);
            if (routeArg == null) return new Response(RequestStatus.ERROR, "Route was not provided", null);

            long id = Long.parseLong(primitiveArg);
            Route oldRoute = collectionManager.findById(id);
            if (oldRoute == null) return new Response(RequestStatus.ERROR, "Route with this ID does not exist", null);
            if (!userLogin.equals(oldRoute.getOwner())) {
                return new Response(RequestStatus.ERROR, "You can modify only your own routes", null);
            }

            if (!routeRepository.update(id, routeArg, userLogin)) {
                return new Response(RequestStatus.ERROR, "Route was not updated", null);
            }

            oldRoute.setName(routeArg.getName());
            oldRoute.setCoordinates(routeArg.getCoordinates());
            oldRoute.setFrom(routeArg.getFrom());
            oldRoute.setTo(routeArg.getTo());
            oldRoute.setDistance(routeArg.getDistance());
            return new Response(RequestStatus.SUCCESS, "Route updated", null);
        } catch (NumberFormatException e) {
            return new Response(RequestStatus.ERROR, "ID must be a number", null);
        } catch (SQLException e) {
            return new Response(RequestStatus.ERROR, "Database error: " + e.getMessage(), null);
        }
    }
}
