package dev.alesixdev.hyrestart.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigData {
    private List<String> restartTimes = Arrays.asList("03:00", "09:00", "15:00", "21:00");
    private int restartCooldownSeconds = 60;
    private List<WarningConfig> warnings = createDefaultWarnings();
    private String finalRestartMessage = "<red>[Restart] Restarting server NOW!</red>";
    private DiscordConfig discord = new DiscordConfig();
    private MessagesConfig messages = new MessagesConfig();
    private ColorConfig colors = new ColorConfig();

    private static List<WarningConfig> createDefaultWarnings() {
        List<WarningConfig> warnings = new ArrayList<>();
        warnings.add(new WarningConfig(1800, "<blue>[Restart] The server will restart in 30 minutes.</blue>", "30 minutes"));
        warnings.add(new WarningConfig(900, "<yellow>[Restart] The server will restart in 15 minutes.</yellow>", "15 minutes"));
        warnings.add(new WarningConfig(300, "<gold>[Restart] The server will restart in 5 minutes. Get ready!</gold>", "5 minutes"));
        warnings.add(new WarningConfig(60, "<red>[Restart]</red> <white>The server will restart in</white> <yellow>1 minute</yellow><red>. DISCONNECT NOW!</red>", "1 minute"));
        return warnings;
    }

    public ConfigData() {
    }

    public List<String> getRestartTimes() {
        return restartTimes;
    }

    public void setRestartTimes(List<String> restartTimes) {
        this.restartTimes = restartTimes;
    }

    public int getRestartCooldownSeconds() {
        return restartCooldownSeconds;
    }

    public void setRestartCooldownSeconds(int restartCooldownSeconds) {
        this.restartCooldownSeconds = restartCooldownSeconds;
    }

    public List<WarningConfig> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<WarningConfig> warnings) {
        this.warnings = warnings;
    }

    public String getFinalRestartMessage() {
        return finalRestartMessage;
    }

    public void setFinalRestartMessage(String finalRestartMessage) {
        this.finalRestartMessage = finalRestartMessage;
    }

    public DiscordConfig getDiscord() {
        return discord;
    }

    public void setDiscord(DiscordConfig discord) {
        this.discord = discord;
    }

    public MessagesConfig getMessages() {
        return messages;
    }

    public void setMessages(MessagesConfig messages) {
        this.messages = messages;
    }

    public ColorConfig getColors() {
        return colors;
    }

    public void setColors(ColorConfig colors) {
        this.colors = colors;
    }

    public static class DiscordConfig {
        private boolean enabled = true;
        private String webhookUrl = "";
        private String embedTitle = "Server Restart";
        private String embedDescription = "The server will restart in **{time}**.\n\nPlease save your progress and disconnect.";
        private int embedColor = 16711680;
        private String shutdownEmbedTitle = "SERVER SHUTTING DOWN";
        private String shutdownEmbedDescription = "The server is shutting down now.\n\nThe server will be back in a few minutes.";
        private int shutdownEmbedColor = 16711680;
        private String startupEmbedTitle = "SERVER ONLINE";
        private String startupEmbedDescription = "The server is now online and ready to accept connections!";
        private int startupEmbedColor = 65280;

        public DiscordConfig() {
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getWebhookUrl() {
            return webhookUrl;
        }

        public void setWebhookUrl(String webhookUrl) {
            this.webhookUrl = webhookUrl;
        }

        public String getEmbedTitle() {
            return embedTitle;
        }

        public void setEmbedTitle(String embedTitle) {
            this.embedTitle = embedTitle;
        }

        public String getEmbedDescription() {
            return embedDescription;
        }

        public void setEmbedDescription(String embedDescription) {
            this.embedDescription = embedDescription;
        }

        public int getEmbedColor() {
            return embedColor;
        }

        public void setEmbedColor(int embedColor) {
            this.embedColor = embedColor;
        }

        public String getShutdownEmbedTitle() {
            return shutdownEmbedTitle;
        }

        public void setShutdownEmbedTitle(String shutdownEmbedTitle) {
            this.shutdownEmbedTitle = shutdownEmbedTitle;
        }

        public String getShutdownEmbedDescription() {
            return shutdownEmbedDescription;
        }

        public void setShutdownEmbedDescription(String shutdownEmbedDescription) {
            this.shutdownEmbedDescription = shutdownEmbedDescription;
        }

        public int getShutdownEmbedColor() {
            return shutdownEmbedColor;
        }

        public void setShutdownEmbedColor(int shutdownEmbedColor) {
            this.shutdownEmbedColor = shutdownEmbedColor;
        }

        public String getStartupEmbedTitle() {
            return startupEmbedTitle;
        }

        public void setStartupEmbedTitle(String startupEmbedTitle) {
            this.startupEmbedTitle = startupEmbedTitle;
        }

        public String getStartupEmbedDescription() {
            return startupEmbedDescription;
        }

        public void setStartupEmbedDescription(String startupEmbedDescription) {
            this.startupEmbedDescription = startupEmbedDescription;
        }

        public int getStartupEmbedColor() {
            return startupEmbedColor;
        }

        public void setStartupEmbedColor(int startupEmbedColor) {
            this.startupEmbedColor = startupEmbedColor;
        }
    }

    public static class MessagesConfig {
        private String pluginEnabled = "[HyRestart] Plugin enabled successfully!";
        private String nextRestart = "[HyRestart] Next restart: {time}";
        private String defaultConfigCreated = "[HyRestart] Default config.yml created";
        private String errorCreatingConfig = "[HyRestart] Error creating config file, generating default";
        private String configLoaded = "[HyRestart] Configuration loaded successfully";
        private String errorLoadingConfig = "[HyRestart] Error loading configuration: {error}";
        private String configSaved = "[HyRestart] Configuration saved successfully";
        private String errorSavingConfig = "[HyRestart] Error saving configuration: {error}";
        private String schedulerStarted = "[HyRestart] Scheduler started. Next restart: {time}";
        private String invalidTimeFormat = "[HyRestart] Invalid time format: {time}";
        private String errorInScheduler = "[HyRestart] Error in scheduler: {error}";
        private String sentRestartWarning = "[HyRestart] Sent restart warning: {discordTime}";
        private String broadcastingToPlayers = "[HyRestart] Broadcasting to {count} players: {message}";
        private String errorBroadcasting = "[HyRestart] Error broadcasting message: {error}";
        private String errorSendingToPlayer = "[HyRestart] Error sending message to {username}";
        private String errorSchedulingForPlayer = "[HyRestart] Error scheduling message for {username}";
        private String startingRestart = "[HyRestart] Starting restart sequence...";
        private String shuttingDown = "[HyRestart] Shutting down server...";
        private String discordWebhookFailed = "[HyRestart] Failed to send Discord webhook. Response code: {code}";
        private String discordWebhookError = "[HyRestart] Error sending Discord webhook: {error}";

        public MessagesConfig() {
        }

        public String getPluginEnabled() {
            return pluginEnabled;
        }

        public void setPluginEnabled(String pluginEnabled) {
            this.pluginEnabled = pluginEnabled;
        }

        public String getNextRestart() {
            return nextRestart;
        }

        public void setNextRestart(String nextRestart) {
            this.nextRestart = nextRestart;
        }

        public String getDefaultConfigCreated() {
            return defaultConfigCreated;
        }

        public void setDefaultConfigCreated(String defaultConfigCreated) {
            this.defaultConfigCreated = defaultConfigCreated;
        }

        public String getErrorCreatingConfig() {
            return errorCreatingConfig;
        }

        public void setErrorCreatingConfig(String errorCreatingConfig) {
            this.errorCreatingConfig = errorCreatingConfig;
        }

        public String getConfigLoaded() {
            return configLoaded;
        }

        public void setConfigLoaded(String configLoaded) {
            this.configLoaded = configLoaded;
        }

        public String getErrorLoadingConfig() {
            return errorLoadingConfig;
        }

        public void setErrorLoadingConfig(String errorLoadingConfig) {
            this.errorLoadingConfig = errorLoadingConfig;
        }

        public String getConfigSaved() {
            return configSaved;
        }

        public void setConfigSaved(String configSaved) {
            this.configSaved = configSaved;
        }

        public String getErrorSavingConfig() {
            return errorSavingConfig;
        }

        public void setErrorSavingConfig(String errorSavingConfig) {
            this.errorSavingConfig = errorSavingConfig;
        }

        public String getSchedulerStarted() {
            return schedulerStarted;
        }

        public void setSchedulerStarted(String schedulerStarted) {
            this.schedulerStarted = schedulerStarted;
        }

        public String getInvalidTimeFormat() {
            return invalidTimeFormat;
        }

        public void setInvalidTimeFormat(String invalidTimeFormat) {
            this.invalidTimeFormat = invalidTimeFormat;
        }

        public String getErrorInScheduler() {
            return errorInScheduler;
        }

        public void setErrorInScheduler(String errorInScheduler) {
            this.errorInScheduler = errorInScheduler;
        }

        public String getSentRestartWarning() {
            return sentRestartWarning;
        }

        public void setSentRestartWarning(String sentRestartWarning) {
            this.sentRestartWarning = sentRestartWarning;
        }

        public String getBroadcastingToPlayers() {
            return broadcastingToPlayers;
        }

        public void setBroadcastingToPlayers(String broadcastingToPlayers) {
            this.broadcastingToPlayers = broadcastingToPlayers;
        }

        public String getErrorBroadcasting() {
            return errorBroadcasting;
        }

        public void setErrorBroadcasting(String errorBroadcasting) {
            this.errorBroadcasting = errorBroadcasting;
        }

        public String getErrorSendingToPlayer() {
            return errorSendingToPlayer;
        }

        public void setErrorSendingToPlayer(String errorSendingToPlayer) {
            this.errorSendingToPlayer = errorSendingToPlayer;
        }

        public String getErrorSchedulingForPlayer() {
            return errorSchedulingForPlayer;
        }

        public void setErrorSchedulingForPlayer(String errorSchedulingForPlayer) {
            this.errorSchedulingForPlayer = errorSchedulingForPlayer;
        }

        public String getStartingRestart() {
            return startingRestart;
        }

        public void setStartingRestart(String startingRestart) {
            this.startingRestart = startingRestart;
        }

        public String getShuttingDown() {
            return shuttingDown;
        }

        public void setShuttingDown(String shuttingDown) {
            this.shuttingDown = shuttingDown;
        }

        public String getDiscordWebhookFailed() {
            return discordWebhookFailed;
        }

        public void setDiscordWebhookFailed(String discordWebhookFailed) {
            this.discordWebhookFailed = discordWebhookFailed;
        }

        public String getDiscordWebhookError() {
            return discordWebhookError;
        }

        public void setDiscordWebhookError(String discordWebhookError) {
            this.discordWebhookError = discordWebhookError;
        }
    }

    public static class ColorConfig {
        private String errorColor = "red";
        private String successColor = "green";
        private String infoColor = "blue";
        private String warningColor = "yellow";

        public ColorConfig() {
        }

        public String getErrorColor() {
            return errorColor;
        }

        public void setErrorColor(String errorColor) {
            this.errorColor = errorColor;
        }

        public String getSuccessColor() {
            return successColor;
        }

        public void setSuccessColor(String successColor) {
            this.successColor = successColor;
        }

        public String getInfoColor() {
            return infoColor;
        }

        public void setInfoColor(String infoColor) {
            this.infoColor = infoColor;
        }

        public String getWarningColor() {
            return warningColor;
        }

        public void setWarningColor(String warningColor) {
            this.warningColor = warningColor;
        }
    }
}
