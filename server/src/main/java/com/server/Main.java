package com.server;

import com.common.model.Route;
import com.server.commands.AddElement;
import com.server.commands.AddIfMax;
import com.server.commands.AverageOfDistance;
import com.server.commands.Clear;
import com.server.commands.FilterContainsName;
import com.server.commands.Help;
import com.server.commands.History;
import com.server.commands.Info;
import com.server.commands.PrintDescending;
import com.server.commands.RemoveByID;
import com.server.commands.RemoveLower;
import com.server.commands.Show;
import com.server.commands.UpdateByID;
import com.server.db.DatabaseConfig;
import com.server.db.DatabaseManager;
import com.server.db.RouteRepository;
import com.server.db.SchemaInitializer;
import com.server.db.UserRepository;
import com.server.manager.CollectionManager;
import com.server.manager.ServerCommandManager;
import com.server.network.UDPServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.HashMap;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            DatabaseConfig config = DatabaseConfig.fromArgs(args);
            DatabaseManager databaseManager = new DatabaseManager(config);
            new SchemaInitializer(databaseManager).initialize();

            UserRepository userRepository = new UserRepository(databaseManager);
            RouteRepository routeRepository = new RouteRepository(databaseManager);
            CollectionManager collectionManager = new CollectionManager();
            ServerCommandManager serverCommandManager = new ServerCommandManager(new HashMap<>(), userRepository);

            Collection<Route> loadedCollection = routeRepository.loadAll();
            collectionManager.setRoutes(loadedCollection);

            serverCommandManager.registerCommand("info", new Info(collectionManager));
            serverCommandManager.registerCommand("help", new Help(serverCommandManager));
            serverCommandManager.registerCommand("show", new Show(collectionManager));
            serverCommandManager.registerCommand("add", new AddElement(collectionManager, routeRepository));
            serverCommandManager.registerCommand("update", new UpdateByID(collectionManager, routeRepository));
            serverCommandManager.registerCommand("remove_by_id", new RemoveByID(collectionManager, routeRepository));
            serverCommandManager.registerCommand("clear", new Clear(collectionManager, routeRepository));
            serverCommandManager.registerCommand("add_if_max", new AddIfMax(collectionManager, routeRepository));
            serverCommandManager.registerCommand("remove_lower", new RemoveLower(collectionManager, routeRepository));
            serverCommandManager.registerCommand("history", new History(serverCommandManager));
            serverCommandManager.registerCommand("average_of_distance", new AverageOfDistance(collectionManager));
            serverCommandManager.registerCommand("filter_contains_name", new FilterContainsName(collectionManager));
            serverCommandManager.registerCommand("print_descending", new PrintDescending(collectionManager));

            UDPServer udpServer = new UDPServer(config.serverPort());
            logger.info("Server started on port {}", config.serverPort());
            logger.info("Loaded routes from database: {}", loadedCollection.size());

            new ServerApp(serverCommandManager, udpServer).run();
        } catch (Exception e) {
            logger.fatal("Critical server error: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
}
