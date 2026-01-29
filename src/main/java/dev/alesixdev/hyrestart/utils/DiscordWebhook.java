package dev.alesixdev.hyrestart.utils;

import dev.alesixdev.hyrestart.config.ConfigData;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.logging.Logger;

public class DiscordWebhook {
    private static final Logger LOGGER = Logger.getLogger(DiscordWebhook.class.getName());

    private final String webhookUrl;
    private final ConfigData.MessagesConfig messages;

    public DiscordWebhook(String webhookUrl, ConfigData.MessagesConfig messages) {
        this.webhookUrl = webhookUrl;
        this.messages = messages;
    }

    /**
     * Sends an embed message to Discord webhook
     *
     * @param title       Embed title
     * @param description Embed description
     * @param color       Embed color (decimal format)
     */
    public void sendEmbed(String title, String description, int color) {
        if (webhookUrl == null || webhookUrl.isEmpty()) {
            return;
        }

        try {
            URL url = URI.create(webhookUrl).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("User-Agent", "HyRestart-Bot");
            connection.setDoOutput(true);

            String jsonPayload = buildEmbedJson(title, description, color);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                LOGGER.severe(messages.getDiscordWebhookFailed().replace("{code}", String.valueOf(responseCode)));
            }

            connection.disconnect();
        } catch (Exception e) {
            LOGGER.severe(messages.getDiscordWebhookError().replace("{error}", e.getMessage()));
            e.printStackTrace();
        }
    }

    private String buildEmbedJson(String title, String description, int color) {
        return String.format(
                "{\"embeds\":[{\"title\":\"%s\",\"description\":\"%s\",\"color\":%d,\"timestamp\":\"%s\"}]}",
                escapeJson(title),
                escapeJson(description),
                color,
                Instant.now().toString()
        );
    }

    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
