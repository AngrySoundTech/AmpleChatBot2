package com.drevelopment.amplechatbot.bukkit.permission;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.permissions.PermissionAttachmentInfo;

import com.drevelopment.amplechatbot.api.entity.Player;
import com.drevelopment.amplechatbot.api.permission.PermissionHandler;

public class SuperPermsPermissionHandler implements PermissionHandler {

	private static final String GROUP_PREFIX = "group.";

	@Override
	public String getName() {
		return "Default";
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean hasPermission(Player player, String node) {
		org.bukkit.entity.Player bukkitPlayer = Bukkit.getPlayer(UUID.fromString(player.getUUID()));
		return bukkitPlayer != null && bukkitPlayer.hasPermission(node);
	}

	@Override
	public Set<String> getGroups(Player player) {
		org.bukkit.entity.Player bukkitPlayer = Bukkit.getPlayer(UUID.fromString(player.getUUID()));
		Set<String> groups = new HashSet<String>();

		for (PermissionAttachmentInfo pai : bukkitPlayer.getEffectivePermissions()) {
			if (pai.getPermission().startsWith(GROUP_PREFIX)) {
				groups.add(pai.getPermission().substring(GROUP_PREFIX.length()));
			}
		}
		return groups;
	}

	@Override
	public void setPlayerGroup(Player player, String group) {

	}

	@Override
	public boolean groupSupport() {
		return false;
	}

}
