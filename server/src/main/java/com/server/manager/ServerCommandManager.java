package com.server.manager;

import com.common.exceptions.UnknownCommandException;
import com.common.model.Route;
import com.common.network.Request;
import com.common.network.RequestStatus;
import com.common.network.Response;
import com.server.commands.Command;
import com.server.db.UserRepository;

import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Map;

public final class ServerCommandManager {
    private final Map<String, Command> commands;
    private final UserRepository userRepository;
    private final ArrayDeque<String> commandList = new ArrayDeque<>(7);

    public ServerCommandManager(Map<String, Command> commands, UserRepository userRepository) {
        this.commands = commands;
        this.userRepository = userRepository;
    }

    public Response handleRequest(Request request) {
        String commandName = request.getCommandName();
        String argument = request.getPrimitiveArg();
        Route route = request.getRouteArg();

        try {
            if ("register".equals(commandName)) {
                return register(request);
            }
            if ("login".equals(commandName)) {
                return login(request);
            }

            if (!hasCredentials(request) || !userRepository.authenticate(request.getLogin(), request.getPassword())) {
                return new Response(RequestStatus.ERROR, "Authorize first: login <login> <password> or register <login> <password>", null);
            }

            Command command = commands.get(commandName);
            if (command == null) {
                return new Response(RequestStatus.ERROR, "Unknown command " + commandName, null);
            }

            Response response = command.execute(argument, route, request.getLogin());
            rememberCommand(commandName);
            return response;
        } catch (UnknownCommandException e) {
            return new Response(RequestStatus.ERROR, "Execution error: " + e.getMessage(), null);
        } catch (SQLException e) {
            return new Response(RequestStatus.ERROR, "Auth database error: " + e.getMessage(), null);
        }
    }

    public void registerCommand(String commandName, Command command) {
        commands.put(commandName, command);
    }

    public Map<String, Command> getCommands() {
        return commands;
    }

    public ArrayDeque<String> getHistory() {
        synchronized (commandList) {
            return new ArrayDeque<>(commandList);
        }
    }

    private Response register(Request request) throws SQLException {
        if (!hasCredentials(request)) {
            return new Response(RequestStatus.ERROR, "Usage: register <login> <password>", null);
        }
        boolean created = userRepository.register(request.getLogin(), request.getPassword());
        if (!created) {
            return new Response(RequestStatus.ERROR, "User already exists", null);
        }
        return new Response(RequestStatus.SUCCESS, "User registered", null);
    }

    private Response login(Request request) throws SQLException {
        if (!hasCredentials(request)) {
            return new Response(RequestStatus.ERROR, "Usage: login <login> <password>", null);
        }
        if (!userRepository.authenticate(request.getLogin(), request.getPassword())) {
            return new Response(RequestStatus.ERROR, "Invalid login or password", null);
        }
        return new Response(RequestStatus.SUCCESS, "Authorized", null);
    }

    private boolean hasCredentials(Request request) {
        return request.getLogin() != null && !request.getLogin().isBlank()
                && request.getPassword() != null && !request.getPassword().isBlank();
    }

    private void rememberCommand(String commandName) {
        synchronized (commandList) {
            commandList.addLast(commandName);
            if (commandList.size() > 7) {
                commandList.removeFirst();
            }
        }
    }

    @Override
    public String toString() {
        return commands.toString();
    }
}
