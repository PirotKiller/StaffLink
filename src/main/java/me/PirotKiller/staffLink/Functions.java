package me.PirotKiller.staffLink;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Functions {
    private StaffLink main;

    public Functions(StaffLink main) {
        this.main = main;
    }
    public void sendToDiscord(String reporterName, String staffMemberName, String reason) {
        new Thread(() -> {
            try {
                URL url = new URL(main.getStaffReportWebhookUrl());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");

                // Constructing the Discord Embed payload
                String jsonPayload = "{\n" +
                        "  \"embeds\": [\n" +
                        "    {\n" +
                        "      \"title\": \"StaffLink\",\n" +
                        "      \"color\": 16763904, \n" + // A nice orange color for reports (decimal value)
                        "      \"fields\": [\n" +
                        "        {\n" +
                        "          \"name\": \"Reported Staff\",\n" +
                        "          \"value\": \"" + staffMemberName + "\",\n" +
                        "          \"inline\": true\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"name\": \"Reported By\",\n" +
                        "          \"value\": \"" + reporterName + "\",\n" +
                        "          \"inline\": true\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"name\": \"Reason\",\n" +
                        "          \"value\": \"" + reason.replace("\"", "\\\"") + "\"\n" + // Escape quotes in reason
                        "        },\n" +
                        "        {\n" +
                        "          \"name\": \"Time\",\n" +
                        "          \"value\": \"<t:" + (System.currentTimeMillis() / 1000) + ":F>\",\n" + // Unix timestamp for Discord
                        "          \"inline\": false\n" +
                        "        }\n" +
                        "      ],\n" +
                        "      \"footer\": {\n" +
                        "        \"text\": \"StaffLink Plugin\"\n" +
                        "      }\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}";


                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_NO_CONTENT || responseCode == HttpURLConnection.HTTP_OK) {
                    main.getLogger().info("Successfully sent staff report to Discord webhook.");
                } else {
                    main.getLogger().warning("Failed to send staff report to Discord webhook. Response code: " + responseCode);
                    main.getLogger().warning("Response message: " + connection.getResponseMessage());
                }

                connection.disconnect();

            } catch (Exception e) {
                main.getLogger().severe("Error sending staff report to Discord webhook: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    public void sendFeedbackToDiscord(String reporterName, String reporterUUID, String feedbackType, String message, boolean anonymous) {
        new Thread(() -> {
            try {
                URL url = new URL(main.getStaffReportWebhookUrl());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");

                String reportedByValue = anonymous ? "Anonymous" : reporterName;
                String reporterUUIDField = anonymous ? "" :
                        "        {\n" +
                                "          \"name\": \"Reporter UUID\",\n" +
                                "          \"value\": \"" + reporterUUID + "\",\n" +
                                "          \"inline\": false\n" +
                                "        },"; // Only include if not anonymous

                int embedColor;
                String emoji;
                String title;

                switch (feedbackType) {
                    case "suggestion":
                        embedColor = 3447003; // Blue
                        emoji = "💡 ";
                        title = "New Suggestion";
                        break;
                    case "bug":
                        embedColor = 15158332; // Red
                        emoji = "🐞 ";
                        title = "Bug Report";
                        break;
                    case "general":
                        embedColor = 5763719; // Green
                        emoji = "✅ ";
                        title = "General Feedback";
                        break;
                    default:
                        embedColor = 9807270; // Grey
                        emoji = "💬 ";
                        title = "Other Feedback";
                        break;
                }

                // Add server name (Optional, uncomment if you have a way to get it, e.g., for BungeeCord)
                // String serverNameField = "        {\n" +
                //                            "          \"name\": \"Server\",\n" +
                //                            "          \"value\": \"" + getServer().getServerName() + "\",\n" + // Requires a method to get server name
                //                            "          \"inline\": true\n" +
                //                            "        },";

                String jsonPayload = "{\n" +
                        "  \"embeds\": [\n" +
                        "    {\n" +
                        "      \"title\": \"" + emoji + title + "\",\n" +
                        "      \"color\": " + embedColor + ", \n" +
                        "      \"fields\": [\n" +
                        "        {\n" +
                        "          \"name\": \"Feedback Type\",\n" +
                        "          \"value\": \"" + feedbackType.substring(0, 1).toUpperCase() + feedbackType.substring(1) + "\",\n" + // Capitalize type
                        "          \"inline\": true\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"name\": \"Reported By\",\n" +
                        "          \"value\": \"" + reportedByValue + "\",\n" +
                        "          \"inline\": true\n" +
                        "        },\n" +
                        // serverNameField + // Uncomment if you add server name logic
                        reporterUUIDField + // This field is conditionally added
                        "        {\n" +
                        "          \"name\": \"Message\",\n" +
                        "          \"value\": \"" + message.replace("\"", "\\\"") + "\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"name\": \"Time\",\n" +
                        "          \"value\": \"<t:" + (System.currentTimeMillis() / 1000) + ":F>\",\n" +
                        "          \"inline\": false\n" +
                        "        }\n" +
                        "      ],\n" +
                        "      \"footer\": {\n" +
                        "        \"text\": \"StaffLink Plugin - Feedback System\"\n" +
                        "      }\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}";

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_NO_CONTENT || responseCode == HttpURLConnection.HTTP_OK) {
                    main.getLogger().info("Successfully sent feedback to Discord webhook.");
                } else {
                    main.getLogger().warning("Failed to send feedback to Discord webhook. Response code: " + responseCode);
                    main.getLogger().warning("Response message: " + connection.getResponseMessage());
                }

                connection.disconnect();

            } catch (Exception e) {
                main.getLogger().severe("Error sending feedback to Discord webhook: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

}
