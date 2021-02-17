package ghostboi.tag;

import ghostboi.tag.models.Game;
import org.bukkit.plugin.java.JavaPlugin;

public final class Tag extends JavaPlugin {

    public Game game;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.game = new Game();

        this.getServer().getPluginManager().registerEvents(new TaggedEvent(this), this);

        this.getCommand("tag").setExecutor(new TagCommand(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if(game.isPlaying())
            game.end();
    }
}
