package com.drevelopment.amplechatbot.bukkit.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.drevelopment.amplechatbot.api.Ample;
import com.drevelopment.amplechatbot.api.command.Command;
import com.drevelopment.amplechatbot.api.command.CommandException;
import com.drevelopment.amplechatbot.api.entity.Player;
import com.drevelopment.amplechatbot.bukkit.BukkitPlugin;

public class BukkitCommandListener implements Listener {

	private BukkitPlugin plugin;

	public BukkitCommandListener(BukkitPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
		Player player = Ample.getModTransformer().getPlayer(event.getPlayer().getUniqueId().toString());
		String command = event.getMessage();

		if (handleCommandEvent(Command.Sender.PLAYER, player, command)) {
			event.setCancelled(true);
		}
	}

	private boolean handleCommandEvent(Command.Sender type, Player sender, String message) {
		message = trimCommand(message);
		int indexOfSpace = message.indexOf(' ');

		try {
			if (indexOfSpace != -1) {
				String command = message.substring(0, indexOfSpace);
				String args[] = message.substring(indexOfSpace + 1).split(" ");

				return Ample.getCommandHandler().handleCommand(command, args, sender);
			} else {
				return Ample.getCommandHandler().handleCommand(message, sender);
			}
		} catch (CommandException e) {
			return false;
		}
	}

	private String trimCommand(String command) {
		if (command.startsWith("/")) {
			if (command.length() == 1) {
				return "";
			} else {
				command = command.substring(1);
			}
		}
		return command.trim();
	}

}
