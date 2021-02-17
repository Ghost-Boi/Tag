package ghostboi.tag.models;

import com.sun.org.apache.xpath.internal.operations.Bool;
import ghostboi.tag.Tag;
import ghostboi.tag.TaggedEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class Game {
    private int task; //runnables id

    private Player it;

    public Player _it;

    private boolean isPlaying;

    public boolean frozen;

    public Game() {
        this.task = -1;
        this.isPlaying = false;
    }

    public int getTask() {
        return task;
    }

    public void setTask(int task) {
        this.task = task;
    }

    public Player getIt() {
        return it;
    }

    public void setIt(Player it) {
        this.it = it;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public Player pickFirstIt() {
        return Bukkit.getOnlinePlayers().stream().skip((int)
                (Bukkit.getOnlinePlayers().size() * Math.random())).findFirst().orElse(null);
    }

    public Scoreboard getboard() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        Objective obj = board.registerNewObjective("tagScoreboard", "dummy",
                ChatColor.translateAlternateColorCodes('&', "&a&lSeconds left: "));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        Score score = obj.getScore(ChatColor.GOLD + it.getName() + " is it!");
        score.setScore(0);
        return board;
    }

    public void end() {
        setPlaying(false);

        Bukkit.getScheduler().cancelTask(getTask());

        it.sendMessage(ChatColor.RED + "You lost :(");
        it.setGlowing(false);

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            if (online != it) {
                online.sendMessage(ChatColor.GREEN + "Congrats! You won tag :)");
            }
        }
    }

    public void tagged(Player player) {
        setPlaying(true);

        if (getTask() != -1)
            Bukkit.getScheduler().cancelTask(task);
        setIt(player);
        it.setGlowing(true);
        it.getInventory().clear();

        for (Player online : Bukkit.getOnlinePlayers())
            online.setScoreboard(getboard());
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Tag.getPlugin(Tag.class),
                new Runnable() {

                    int timeLeft = 300;
                    int freezeTime = 6;
                    Location itLoc = it.getLocation();

                    @Override
                    public void run() {
                        if (timeLeft <= 0) {
                            end();
                            return;
                        }

                        for (Player online : Bukkit.getOnlinePlayers())
                            online.getScoreboard().getObjective(DisplaySlot.SIDEBAR)
                                    .setDisplayName(ChatColor.translateAlternateColorCodes('&',
                                            "&a&lSeconds left: " + timeLeft));



                        if (freezeTime > 0 && frozen) {
                            it.teleport(itLoc);
                        } else frozen = false;


                        freezeTime--;
                        timeLeft--;
                    }
                }, 0, 20);
    }
}
