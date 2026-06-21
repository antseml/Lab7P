package com.client;

import com.client.asker.RouteAsker;
import com.client.commands.ExecuteScript;
import com.client.console.Console;
import com.client.network.UDPClient;
import com.client.util.InputProvider;
import com.common.model.Route;
import com.common.network.Request;
import com.common.network.RequestStatus;
import com.common.network.Response;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

public final class RequestManager {
    private final Console console;
    private final UDPClient udpClient;
    private final HashSet<String> clientCommands = new HashSet<>();
    private String login;
    private String password;

    public RequestManager(UDPClient udpClient, Console console) throws IOException {
        this.console = console;
        this.udpClient = udpClient;

        clientCommands.add("add");
        clientCommands.add("update");
        clientCommands.add("add_if_max");
        clientCommands.add("remove_lower");
    }

    public void processCommand(String userInput, InputProvider inputProvider) throws IOException, InterruptedException, ClassNotFoundException {
        String[] tokens = userInput.trim().split("\\s", 2);
        String command = tokens[0];
        String argument = (tokens.length > 1) ? tokens[1] : "";

        if (command.equals("exit")) {
            console.printByProgram("Р”Рѕ СЃРІРёРґР°РЅРёСЏ!");
            System.exit(0);
            return;
        }
        if (command.equals("execute_script")) {
            new ExecuteScript(this, console).execute(argument);
            return;
        }
        if (command.equals("login") || command.equals("register")) {
            processAuthCommand(command, argument);
            return;
        }

        Request request;
        if (clientCommands.contains(command)) {
            Route route = new RouteAsker(console, inputProvider).builder();
            request = authorizedRequest(command, argument, route);
        } else {
            request = authorizedRequest(command, argument, null);
        }

        printResponse(sendAndReceive(request));
    }

    private void processAuthCommand(String command, String argument) throws IOException, InterruptedException, ClassNotFoundException {
        String[] authArgs = argument.trim().split("\\s+", 2);
        if (authArgs.length < 2 || authArgs[0].isBlank() || authArgs[1].isBlank()) {
            console.printErr("Usage: " + command + " <login> <password>");
            return;
        }

        Request request = new Request(command, "", null, authArgs[0], authArgs[1]);
        Response response = sendAndReceive(request);
        if (response.getRequestStatus() == RequestStatus.SUCCESS) {
            login = authArgs[0];
            password = authArgs[1];
        }
        printResponse(response);
    }

    private Request authorizedRequest(String command, String argument, Route route) {
        return new Request(command, argument, route, login, password);
    }

    private Response sendAndReceive(Request request) throws IOException, InterruptedException, ClassNotFoundException {
        udpClient.sendRequest(request);
        return udpClient.receiveResponse();
    }

    private void printResponse(Response response) {
        switch (response.getRequestStatus()) {
            case ERROR -> console.printErr(response.getTextMessage());
            case SUCCESS -> {
                if (response.getTextMessage() != null) {
                    console.printSuccess(response.getTextMessage());
                }

                if (response.getResult() != null) {
                    if (response.getResult() instanceof List<?> resultList) {
                        resultList.forEach(console::println);
                    } else {
                        console.println(response.getResult());
                    }
                }
            }
        }
    }
}
