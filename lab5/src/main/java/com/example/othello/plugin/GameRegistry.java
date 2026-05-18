package com.example.othello.plugin;

import com.example.othello.GameType;
import com.example.othello.plugins.chess.ChessGamePlugin;
import com.example.othello.plugins.minesweeper.MinesweeperGamePlugin;
import com.example.othello.plugins.peace.PeaceGamePlugin;
import com.example.othello.plugins.reversi.ReversiGamePlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class GameRegistry {
    private final Map<GameType, GamePlugin> pluginsByType = new EnumMap<>(GameType.class);
    private final List<GamePlugin> pluginsInOrder = new ArrayList<>();

    public static GameRegistry createDefault() {
        GameRegistry registry = new GameRegistry();
        registry.register(new PeaceGamePlugin());
        registry.register(new ReversiGamePlugin());
        registry.register(new MinesweeperGamePlugin());
        registry.register(new ChessGamePlugin());
        return registry;
    }

    public void register(GamePlugin plugin) {
        pluginsByType.put(plugin.getType(), plugin);
        pluginsInOrder.add(plugin);
    }

    public GamePlugin get(GameType gameType) {
        GamePlugin plugin = pluginsByType.get(gameType);
        if (plugin == null) {
            throw new IllegalArgumentException("Unsupported game type: " + gameType);
        }
        return plugin;
    }

    public GamePlugin findByInput(String input) {
        GameType gameType = GameType.fromInput(input);
        return gameType == null ? null : pluginsByType.get(gameType);
    }

    public List<GamePlugin> orderedPlugins() {
        return Collections.unmodifiableList(pluginsInOrder);
    }

    public String creationCommands() {
        List<String> commands = new ArrayList<>();
        for (GamePlugin plugin : pluginsInOrder) {
            commands.add(plugin.getType().displayName());
        }
        return String.join("/", commands);
    }
}
