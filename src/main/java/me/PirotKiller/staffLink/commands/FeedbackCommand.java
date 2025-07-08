package me.PirotKiller.staffLink.commands;

import me.PirotKiller.staffLink.Functions;
import me.PirotKiller.staffLink.StaffLink;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FeedbackCommand implements TabExecutor {
    private StaffLink main;
    private Functions functions;

    public FeedbackCommand(StaffLink main) {
        this.main = main;
        this.functions = new Functions(main);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        Player reporter = (Player) sender;
            if (args.length < 2 || args[0].equalsIgnoreCase("help")) {
                reporter.sendMessage("§cUsage: /feedback <type> [anonymous] <message>");
                reporter.sendMessage("§cTypes: suggestion, bug, general, other");
                reporter.sendMessage("§cExample: /feedback suggestion Add more minigames.");
                reporter.sendMessage("§cExample: /feedback bug anonymous Lobby is lagging.");
                return true;
            }

            String feedbackType = args[0].toLowerCase();
            boolean anonymous = false;
            String message;

            int messageStartIndex = 1;
            if (args.length > 2 && args[1].equalsIgnoreCase("anonymous")) {
                anonymous = true;
                messageStartIndex = 2;
            }

            if (args.length < messageStartIndex + 1) { // Check if message is present after type and optional anonymous
                reporter.sendMessage("§cUsage: /feedback <type> [anonymous] <message>");
                reporter.sendMessage("§cPlease provide a message for your feedback.");
                return true;
            }

            StringBuilder messageBuilder = new StringBuilder();
            for (int i = messageStartIndex; i < args.length; i++) {
                messageBuilder.append(args[i]).append(" ");
            }
            message = messageBuilder.toString().trim();

            if (main.getStaffReportWebhookUrl() == null || main.getStaffReportWebhookUrl().isEmpty()) {
                reporter.sendMessage("§cError: Discord Webhook URL for feedback is not configured. Feedback cannot be sent.");
                main.getLogger().severe("Attempted to send feedback, but webhook URL is missing!");
                return true;
            }

            functions.sendFeedbackToDiscord(reporter.getName(), reporter.getUniqueId().toString(), feedbackType, message, anonymous);
            reporter.sendMessage("§aYour feedback has been submitted successfully.");
            return false;

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> type = new ArrayList<>();
        if (args.length == 1){
            type.add("help");
            type.add("suggestion");
            type.add("bug");
            type.add("general");
            type.add("other");
            return type;
        }
        if (args.length == 2){
            type.add("anonymous");
            return type;
        }
        return null;
    }
}
