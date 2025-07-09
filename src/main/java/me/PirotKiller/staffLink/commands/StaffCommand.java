package me.PirotKiller.staffLink.commands;

import me.PirotKiller.staffLink.Functions;
import me.PirotKiller.staffLink.StaffLink;
import me.PirotKiller.staffLink.utils.UChat;
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
                new UChat().sendMessage(reporter,"§cUsage: /reportstaff <staff_member_name> <reason>");
                return true;
            }

            String staffMemberName = args[0];
            if (main.getStaffMembers().contains(staffMemberName)) {
                StringBuilder reasonBuilder = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    reasonBuilder.append(args[i]).append(" ");
                }
                String reason = reasonBuilder.toString().trim();

                if (main.getStaffReportWebhookUrl() == null || main.getStaffReportWebhookUrl().isEmpty()) {
                    new UChat().sendMessage(reporter, "§cError: Discord Webhook URL is not configured. Report cannot be sent.");
                    main.getLogger().severe("Attempted to send staff report, but webhook URL is missing!");
                    return true;
                }

                functions.sendToDiscord(reporter.getName(), staffMemberName, reason);
                new UChat().sendMessage(reporter, "§aYour report against §e" + staffMemberName + "§a has been submitted.");
                return true;
            }else{
            new UChat().sendMessage(reporter,"§cInvalid Staff member does not exist.");
        }
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