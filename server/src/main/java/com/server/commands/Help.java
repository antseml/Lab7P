package com.server.commands;

import com.common.model.Route;
import com.common.network.RequestStatus;
import com.common.network.Response;
import com.server.manager.ServerCommandManager;

public final class Help extends Command {
    private final ServerCommandManager serverCommandManager;

    public Help(ServerCommandManager serverCommandManager) {
        super("help", "Show command help");
        this.serverCommandManager = serverCommandManager;
    }

    @Override
    public Response execute(String primitiveArg, Route routeArg, String userLogin) {
        StringBuilder helpText = new StringBuilder();

        helpText.append(String.format("%-45s%-1s%n", "register <login> <password>", "Create a new user"));
        helpText.append(String.format("%-45s%-1s%n", "login <login> <password>", "Authorize as an existing user"));
        helpText.append(String.format("%-45s%-1s%n", "execute_script {file}", "Read and execute script"));
        helpText.append(String.format("%-45s%-1s%n", "exit", "Exit client"));

        serverCommandManager.getCommands().values().forEach(command ->
                helpText.append(String.format("%-45s%-1s%n", command.getName(), command.getDescription()))
        );

        return new Response(RequestStatus.SUCCESS, null, helpText.toString().trim());
    }
}
