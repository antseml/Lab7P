package com.common.network;

import com.common.model.Route;

import java.io.Serializable;

public final class Request implements Serializable {
    private String commandName;
    private String stringArg;
    private Route routeArg;
    private String login;
    private String password;

    public Request(String commandName, String primitiveArg, Route routeArg) {
        this.commandName = commandName;
        this.stringArg = primitiveArg;
        this.routeArg = routeArg;
    }

    public Request(String commandName, String primitiveArg, Route routeArg, String login, String password) {
        this(commandName, primitiveArg, routeArg);
        this.login = login;
        this.password = password;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public String getPrimitiveArg() {
        return stringArg;
    }

    public void setPrimitiveArg(String stringArg) {
        this.stringArg = stringArg;
    }

    public Route getRouteArg() {
        return routeArg;
    }

    public void setObjArg(Route routeArg) {
        this.routeArg = routeArg;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "{" + commandName + "}" + "{" + stringArg + "}" + "{" + routeArg + "}" + "{" + login + "}";
    }
}
