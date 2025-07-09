package me.PirotKiller.staffLink;

import me.PirotKiller.staffLink.commands.FeedbackCommand;
import me.PirotKiller.staffLink.commands.StaffCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class StaffLink extends JavaPlugin {

    private String staffReportWebhookUrl;
    private List<String> staffMembers;
    private String feedbackWebhookUrl;

    @Override
    public void onEnable() {
        // Save default config if not exists and load it
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        this.staffReportWebhookUrl = config.getString("discord-report-webhook-url");
        this.feedbackWebhookUrl = config.getString("discord-feedback-webhook-url");
        this.staffMembers = (List<String>) config.getList("Staff-Member");

        if (this.staffReportWebhookUrl == null || this.staffReportWebhookUrl.isEmpty()) {
            getLogger().warning("Discord Webhook URL is not set in config.yml! Staff reports will not be sent to Discord.");
        }
        if (this.feedbackWebhookUrl == null || this.feedbackWebhookUrl.isEmpty()) {
            getLogger().warning("Discord Webhook URL for feedback is not set in config.yml! Feedback will not be sent to Discord.");
        }
        getCommand("reportstaff").setExecutor(new StaffCommand(this));
        getCommand("feedback").setExecutor(new FeedbackCommand(this));
        getLogger().info("StaffReport plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("StaffReport plugin disabled!");
    }

    public String getStaffReportWebhookUrl() {
        return staffReportWebhookUrl;
    }

    public String getFeedbackWebhookUrl() {
        return feedbackWebhookUrl;
    }

    public List<String> getStaffMembers() {
        return staffMembers;
    }

}

