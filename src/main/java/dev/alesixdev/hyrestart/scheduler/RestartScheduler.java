package dev.alesixdev.hyrestart.scheduler;

import com.hypixel.hytale.server.core.HytaleServer;
import dev.alesixdev.hyrestart.config.ConfigData;
import dev.alesixdev.hyrestart.config.WarningConfig;
import dev.alesixdev.hyrestart.utils.DiscordWebhook;
import dev.alesixdev.hyrestart.utils.MessageFormatter;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.Message;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class RestartScheduler {
    private static final Logger LOGGER = Logger.getLogger(RestartScheduler.class.getName());
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final int CHECK_INTERVAL_SECONDS = 30;
    private static final int WARNING_WINDOW_SECONDS = 30;
    private static final int RESTART_THRESHOLD_SECONDS = 5;
    private static final int RESTART_SAFETY_MARGIN_SECONDS = 180;

    private final ConfigData config;
    private final ScheduledExecutorService scheduler;
    private final DiscordWebhook discordWebhook;
    private final Set<String> sentWarnings;
    private final File lastRestartFile;
    private long lastRestartEpochMillis;

    private LocalTime nextRestartTime;
    private boolean restartInProgress;

    public RestartScheduler(ConfigData config) {
        this.config = config;
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.discordWebhook = new DiscordWebhook(config.getDiscord().getWebhookUrl(), config.getMessages());
        this.sentWarnings = new HashSet<>();
        this.restartInProgress = false;
        this.lastRestartFile = new File("mods/HyRestart", "last-restart.txt");
        this.lastRestartEpochMillis = 0L;
    }

    public void start() {
        loadLastRestartTimestamp();
        calculateNextRestart();
        scheduler.scheduleAtFixedRate(
            this::checkRestartTime,
            0,
            CHECK_INTERVAL_SECONDS,
            TimeUnit.SECONDS
        );
        LOGGER.info(config.getMessages().getSchedulerStarted().replace("{time}", String.valueOf(nextRestartTime)));
    }

    public void stop() {
        if (scheduler.isShutdown()) {
            LOGGER.info("[HyRestart] Scheduler already stopped, skipping...");
            return;
        }

        LOGGER.info("[HyRestart] Stopping scheduler...");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void calculateNextRestart() {
        LocalTime currentTime = LocalTime.now();
        LocalTime closestTime = null;

        for (String timeStr : config.getRestartTimes()) {
            try {
                String normalizedTime = normalizeTimeFormat(timeStr);
                LocalTime restartTime = LocalTime.parse(normalizedTime, TIME_FORMATTER);
                long secondsUntil = java.time.Duration.between(currentTime, restartTime).getSeconds();

                if (secondsUntil > RESTART_SAFETY_MARGIN_SECONDS) {
                    if (closestTime == null || restartTime.isBefore(closestTime)) {
                        closestTime = restartTime;
                    }
                }
            } catch (Exception e) {
                LOGGER.severe(config.getMessages().getInvalidTimeFormat().replace("{time}", timeStr));
            }
        }

        if (closestTime == null && !config.getRestartTimes().isEmpty()) {
            try {
                String normalizedTime = normalizeTimeFormat(config.getRestartTimes().get(0));
                closestTime = LocalTime.parse(normalizedTime, TIME_FORMATTER);
                LOGGER.info("[HyRestart] No valid restart times available today, scheduling for tomorrow: " + closestTime);
            } catch (Exception e) {
                LOGGER.severe("[HyRestart] Invalid time format: " + config.getRestartTimes().get(0));
            }
        }

        nextRestartTime = closestTime;
        sentWarnings.clear();
        restartInProgress = false;

        if (nextRestartTime != null) {
            long secondsUntil = calculateSecondsUntilRestart();
            long hours = secondsUntil / 3600;
            long minutes = (secondsUntil % 3600) / 60;
            LOGGER.info("[HyRestart] Next restart scheduled at: " + nextRestartTime + " (in " + hours + "h " + minutes + "m)");
        }
    }

    private String normalizeTimeFormat(String timeStr) {
        String trimmed = timeStr.trim();
        long colonCount = trimmed.chars().filter(ch -> ch == ':').count();
        return colonCount == 1 ? trimmed + ":00" : trimmed;
    }

    private void checkRestartTime() {
        if (nextRestartTime == null || restartInProgress) {
            return;
        }

        try {
            LocalTime currentTime = LocalTime.now();
            long rawSeconds = java.time.Duration.between(currentTime, nextRestartTime).getSeconds();

            if (rawSeconds <= RESTART_THRESHOLD_SECONDS && rawSeconds >= -CHECK_INTERVAL_SECONDS) {
                if (!sentWarnings.contains("restart")) {
                    if (isRestartCooldownActive()) {
                        LOGGER.info("[HyRestart] Skipping restart due to recent restart cooldown.");
                        calculateNextRestart();
                        return;
                    }
                    sentWarnings.add("restart");
                    restartInProgress = true;
                    performRestart();
                    return;
                }
            }

            long secondsUntilRestart = rawSeconds < 0 ? rawSeconds + 86400 : rawSeconds;
            processWarnings(secondsUntilRestart);
        } catch (Exception e) {
            LOGGER.severe(config.getMessages().getErrorInScheduler().replace("{error}", e.getMessage()));
        }
    }

    private long calculateSecondsUntilRestart() {
        LocalTime currentTime = LocalTime.now();
        long seconds = java.time.Duration.between(currentTime, nextRestartTime).getSeconds();
        return seconds < 0 ? seconds + 86400 : seconds;
    }

    private void processWarnings(long secondsUntilRestart) {
        for (WarningConfig warning : config.getWarnings()) {
            String warningKey = warning.getWarningKey();

            if (shouldSendWarning(secondsUntilRestart, warning.getSeconds(), warningKey)) {
                sendWarning(warning);
                sentWarnings.add(warningKey);
            }
        }
    }

    private boolean shouldSendWarning(long secondsUntilRestart, int warningSeconds, String warningKey) {
        return secondsUntilRestart <= warningSeconds
            && secondsUntilRestart > (warningSeconds - WARNING_WINDOW_SECONDS)
            && !sentWarnings.contains(warningKey);
    }

    private void sendWarning(WarningConfig warning) {
        broadcastMessage(warning.getMessage());

        if (config.getDiscord().isEnabled() && !config.getDiscord().getWebhookUrl().isEmpty()) {
            String description = config.getDiscord().getEmbedDescription()
                .replace("{time}", warning.getDiscordTime());
            discordWebhook.sendEmbed(
                config.getDiscord().getEmbedTitle(),
                description,
                config.getDiscord().getEmbedColor()
            );
        }

        LOGGER.info(config.getMessages().getSentRestartWarning().replace("{discordTime}", warning.getDiscordTime()));
    }

    private void broadcastMessage(String message) {
        try {
            List<PlayerRef> players = Universe.get().getPlayers();
            LOGGER.info(config.getMessages().getBroadcastingToPlayers()
                .replace("{count}", String.valueOf(players.size()))
                .replace("{message}", message));

            if (players.isEmpty()) {
                return;
            }

            Message msg = MessageFormatter.createColoredMessage(message);

            for (PlayerRef playerRef : players) {
                sendMessageToPlayer(playerRef, msg);
            }
        } catch (Exception e) {
            LOGGER.severe(config.getMessages().getErrorBroadcasting().replace("{error}", e.getMessage()));
        }
    }

    private void sendMessageToPlayer(PlayerRef playerRef, Message msg) {
        try {
            Ref<EntityStore> ref = playerRef.getReference();
            if (ref != null) {
                Store<EntityStore> store = ref.getStore();
                store.getExternalData().getWorld().execute(() -> {
                    try {
                        Player player = store.getComponent(ref, Player.getComponentType());
                        if (player != null) {
                            player.sendMessage(msg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        LOGGER.severe(config.getMessages().getErrorSendingToPlayer().replace("{username}", playerRef.getUsername()));
                    }
                });
            }
        } catch (Exception e) {
            LOGGER.severe(config.getMessages().getErrorSchedulingForPlayer().replace("{username}", playerRef.getUsername()));
        }
    }

    private void performRestart() {
        LOGGER.info(config.getMessages().getStartingRestart());
        broadcastMessage(config.getFinalRestartMessage());

        stop();
        saveLastRestartTimestamp();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        LOGGER.info(config.getMessages().getShuttingDown());
        HytaleServer.get().shutdownServer();
    }

    public LocalTime getNextRestartTime() {
        return nextRestartTime;
    }

    private void loadLastRestartTimestamp() {
        if (!lastRestartFile.exists()) {
            return;
        }

        try {
            String value = Files.readString(lastRestartFile.toPath(), StandardCharsets.UTF_8).trim();
            if (!value.isEmpty()) {
                lastRestartEpochMillis = Long.parseLong(value);
            }
        } catch (Exception e) {
            LOGGER.warning("[HyRestart] Failed to read last restart timestamp: " + e.getMessage());
        }
    }

    private void saveLastRestartTimestamp() {
        try {
            File parent = lastRestartFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            lastRestartEpochMillis = System.currentTimeMillis();
            Files.writeString(lastRestartFile.toPath(), String.valueOf(lastRestartEpochMillis), StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOGGER.warning("[HyRestart] Failed to save last restart timestamp: " + e.getMessage());
        }
    }

    private boolean isRestartCooldownActive() {
        int cooldownSeconds = config.getRestartCooldownSeconds();
        if (cooldownSeconds <= 0 || lastRestartEpochMillis <= 0) {
            return false;
        }
        long elapsedMillis = System.currentTimeMillis() - lastRestartEpochMillis;
        return elapsedMillis >= 0 && elapsedMillis < (cooldownSeconds * 1000L);
    }
}
