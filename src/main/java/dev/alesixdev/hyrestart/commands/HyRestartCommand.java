package dev.alesixdev.hyrestart.commands;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import dev.alesixdev.hyrestart.HyRestartPlugin;
import dev.alesixdev.hyrestart.utils.MessageFormatter;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.LocalTime;

public class HyRestartCommand extends CommandBase {
    private final HyRestartPlugin plugin;
    private final RequiredArg<String> subcommandArg;

    public HyRestartCommand(HyRestartPlugin plugin) {
        super("hyrestart", "hyrestart.commands.description");
        this.plugin = plugin;

        this.subcommandArg = this.withRequiredArg(
            "subcommand",
            "reload | scheduler",
                ArgTypes.STRING
        );

        this.setPermissionGroups("hytale:WorldEditor");
        this.addAliases("hyr");
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        String subcommand = context.get(this.subcommandArg);

        if (subcommand == null) {
            context.sendMessage(MessageFormatter.createColoredMessage(
                "<yellow>[HyRestart] Usage: /hyrestart <reload|scheduler></yellow>"
            ));
            return;
        }

        switch (subcommand.toLowerCase()) {
            case "reload":
                executeReload(context);
                break;
            case "scheduler":
                executeScheduler(context);
                break;
            default:
                context.sendMessage(MessageFormatter.createColoredMessage(
                    "<red>[HyRestart] Unknown subcommand. Use: reload or scheduler</red>"
                ));
                break;
        }
    }

    private void executeReload(CommandContext context) {
        try {
            context.sendMessage(MessageFormatter.createColoredMessage(
                "<yellow>[HyRestart] Reloading configuration...</yellow>"
            ));

            plugin.reloadPlugin();

            context.sendMessage(MessageFormatter.createColoredMessage(
                "<green>[HyRestart] Configuration and scheduler reloaded successfully!</green>"
            ));
        } catch (Exception e) {
            context.sendMessage(MessageFormatter.createColoredMessage(
                "<red>[HyRestart] Error reloading: " + e.getMessage() + "</red>"
            ));
        }
    }

    private void executeScheduler(CommandContext context) {
        LocalTime nextRestart = plugin.getNextRestartTime();

        if (nextRestart == null) {
            context.sendMessage(MessageFormatter.createColoredMessage(
                "<yellow>[HyRestart] No restart scheduled.</yellow>"
            ));
            return;
        }

        LocalTime currentTime = LocalTime.now();
        long secondsUntil = Duration.between(currentTime, nextRestart).getSeconds();

        if (secondsUntil < 0) {
            secondsUntil += 86400;
        }

        long hours = secondsUntil / 3600;
        long minutes = (secondsUntil % 3600) / 60;
        long seconds = secondsUntil % 60;

        context.sendMessage(MessageFormatter.createColoredMessage(
            "<aqua>[HyRestart] ========== Restart Scheduler ==========</aqua>"
        ));
        context.sendMessage(MessageFormatter.createColoredMessage(
            "<yellow>[HyRestart] Next restart: " + nextRestart + "</yellow>"
        ));
        context.sendMessage(MessageFormatter.createColoredMessage(
            "<green>[HyRestart] Time remaining: " + hours + "h " + minutes + "m " + seconds + "s</green>"
        ));

        StringBuilder scheduledTimes = new StringBuilder("<blue>[HyRestart] Scheduled times: ");
        for (int i = 0; i < plugin.getConfigData().getRestartTimes().size(); i++) {
            if (i > 0) {
                scheduledTimes.append(", ");
            }
            scheduledTimes.append(plugin.getConfigData().getRestartTimes().get(i));
        }
        scheduledTimes.append("</blue>");

        context.sendMessage(MessageFormatter.createColoredMessage(
            scheduledTimes.toString()
        ));
        context.sendMessage(MessageFormatter.createColoredMessage(
            "<aqua>[HyRestart] ====================================</aqua>"
        ));
    }
}
