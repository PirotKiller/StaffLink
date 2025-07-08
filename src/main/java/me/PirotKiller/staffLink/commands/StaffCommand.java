package me.PirotKiller.staffLink.commands;

import me.PirotKiller.staffLink.Functions;
import me.PirotKiller.staffLink.StaffLink;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class StaffCommand implements TabExecutor {
    private StaffLink main;
    private Functions functions;

    public StaffCommand(StaffLink main) {
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

        if (cmd.getName().equalsIgnoreCase("reportstaff")) {
            if (args.length < 2) {
                reporter.sendMessage("§cUsage: /reportstaff <staff_member_name> <reason>");
                return true;
            }

            String staffMemberName = args[0];
            StringBuilder reasonBuilder = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                reasonBuilder.append(args[i]).append(" ");
            }
            String reason = reasonBuilder.toString().trim();

            if (main.getStaffReportWebhookUrl() == null || main.getStaffReportWebhookUrl().isEmpty()) {
                reporter.sendMessage("§cError: Discord Webhook URL is not configured. Report cannot be sent.");
                main.getLogger().severe("Attempted to send staff report, but webhook URL is missing!");
                return true;
            }

            functions.sendToDiscord(reporter.getName(), staffMemberName, reason);
            reporter.sendMessage("§aYour report against §e" + staffMemberName + "§a has been submitted.");
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1){
            return main.getStaffMembers();
        }
        return null;
    }
}
