/*
 * Copyright (c) 2012 Sean Porter <glitchkey@gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.nopreload;

//* IMPORTS: JDK/JRE
	import java.io.File;
	import java.io.FileInputStream;
	import java.io.FileOutputStream;
	import java.io.ObjectInputStream;
	import java.io.ObjectOutputStream;
	import java.lang.String;
//* IMPORTS: BUKKIT
	import org.bukkit.Bukkit;
	import org.bukkit.ChatColor;
	import org.bukkit.command.Command;
	import org.bukkit.command.CommandExecutor;
	import org.bukkit.command.CommandSender;
	import org.bukkit.entity.Player;
	import org.bukkit.event.EventHandler;
	import org.bukkit.event.EventPriority;
	import org.bukkit.event.Listener;
	import org.bukkit.event.world.WorldLoadEvent;
	import org.bukkit.plugin.java.JavaPlugin;
	import org.bukkit.World;
//* IMPORTS: OTHER
	//* NOT NEEDED

public class NoPreload extends JavaPlugin implements Listener, CommandExecutor
{
	Settings settings;
	public void onDisable() {
		saveSettings();
	}
	public void onEnable() {
		loadSettings();

		if(settings == null)
			return;

		for(World world : Bukkit.getWorlds()) {
			boolean disabled = false;
			for(String worldName : settings.worldsToDisable) {
				if(!world.getName().equalsIgnoreCase(worldName))
					continue;

				Bukkit.getServer().unloadWorld(world, true);
				disabled = true;
				break;
			}

			if(disabled)
				continue;

			for(String worldName : settings.worldsToUnload) {
				if(!world.getName().equalsIgnoreCase(worldName))
					continue;

				world.setKeepSpawnInMemory(false);
				break;
			}
			
		}
		getServer().getPluginManager().registerEvents(this, this);
		getCommand("nopreload").setExecutor(this);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onWorldLoad(WorldLoadEvent event) {
		if(settings == null)
			return;

		if(event == null || event.getWorld() == null)
			return;

		World world = event.getWorld();

		boolean disabled = false;
		for(String worldName : settings.worldsToDisable) {
			if(!world.getName().equalsIgnoreCase(worldName))
				continue;

			Bukkit.getServer().unloadWorld(world, true);
			disabled = true;
			break;
		}

		if(disabled)
			return;

		for(String worldName : settings.worldsToUnload) {
			if(!world.getName().equalsIgnoreCase(worldName))
				continue;

			world.setKeepSpawnInMemory(false);
			break;
		}
	}

	public boolean onCommand(
		CommandSender sender,
		Command command,
		String label,
		String[] args)
	{
		if(sender == null || command == null)
			return false;

		if(label == null || args == null)
			return false;

		if(!sender.hasPermission("nopreload.admin")) {
			String msg = "NoPreload";
			msg = String.format("%s[%s]", ChatColor.DARK_AQUA, msg);
			msg = String.format("%s%s ", msg, ChatColor.RED);
			msg += "You don't have permission to do that!";
			sender.sendMessage(msg);
			return true;
		}

		if(args.length != 2)
			return false;

		if(args[0].equalsIgnoreCase("unload"))
			return unloadWorld(sender, args[1]);
		else if(args[0].equalsIgnoreCase("disable"))
			return disableWorld(sender, args[1]);
		else if(args[0].equalsIgnoreCase("reset"))
			return resetWorld(sender, args[1]);

		return false;
	}

	private boolean unloadWorld(CommandSender sender, String worldName) {
		if(worldName == null)
			return false;

		for(World world : Bukkit.getWorlds()) {
			if(!world.getName().equalsIgnoreCase(worldName))
				continue;

			if(settings == null)
				settings = new Settings();

			settings.worldsToUnload.add(worldName);
			settings.worldsToDisable.remove(worldName);

			world.setKeepSpawnInMemory(false);

			String msg = "NoPreload";
			msg = String.format("%s[%s]", ChatColor.DARK_AQUA, msg);
			msg = String.format("%s%s ", msg, ChatColor.WHITE);
			msg += worldName + " has been set to unload spawn.";
			sender.sendMessage(msg);

			return true;
		}

		if(settings == null) {
			String msg = "NoPreload";
			msg = String.format("%s[%s]", ChatColor.DARK_AQUA, msg);
			msg = String.format("%s%s ", msg, ChatColor.WHITE);
			msg += worldName + " does not appear to exist.";
			sender.sendMessage(msg);
			return true;
		}
		
		for(String world : settings.worldsToDisable) {
			if(!world.equalsIgnoreCase(worldName))
				continue;

			settings.worldsToUnload.add(worldName);
			settings.worldsToDisable.remove(worldName);

			String msg = "NoPreload";
			msg = String.format("%s[%s]", ChatColor.DARK_AQUA, msg);
			msg = String.format("%s%s ", msg, ChatColor.WHITE);
			String msg1 = msg;
			msg1 += worldName + " has been set to unload spawn.";
			sender.sendMessage(msg1);
			String msg2 = msg;
			msg2 += "However, the server needs to be restarted ";
			msg2 += "to load the world.";
			sender.sendMessage(msg2);

			return true;
		}

		String msg = "NoPreload";
		msg = String.format("%s[%s]", ChatColor.DARK_AQUA, msg);
		msg = String.format("%s%s ", msg, ChatColor.WHITE);
		msg += worldName + " does not appear to exist.";
		sender.sendMessage(msg);
		return true;
	}

	private boolean disableWorld(CommandSender sender, String worldName) {
		if(worldName == null)
			return false;

		for(World world : Bukkit.getWorlds()) {
			if(!world.getName().equalsIgnoreCase(worldName))
				continue;

			if(settings == null)
				settings = new Settings();

			settings.worldsToUnload.remove(worldName);
			settings.worldsToDisable.add(worldName);

			Bukkit.getServer().unloadWorld(world, true);

			String msg = "NoPreload";
			msg = String.format("%s[%s]", ChatColor.DARK_AQUA, msg);
			msg = String.format("%s%s ", msg, ChatColor.WHITE);
			msg += worldName + " has been disabled.";
			sender.sendMessage(msg);

			return true;
		}

		String msg = "NoPreload";
		msg = String.format("%s[%s]", ChatColor.DARK_AQUA, msg);
		msg = String.format("%s%s ", msg, ChatColor.WHITE);
		msg += worldName + " does not appear to exist.";
		sender.sendMessage(msg);
		return true;
	}

	private boolean resetWorld(CommandSender sender, String worldName) {
		if(worldName == null)
			return false;

		if(settings == null) {
			String msg = "NoPreload";
			msg = String.format("%s[%s]", ChatColor.DARK_AQUA, msg);
			msg = String.format("%s%s ", msg, ChatColor.WHITE);
			msg += "No worlds appear to have been modified.";
			sender.sendMessage(msg);
			return true;
		}
		
		for(String world : settings.worldsToDisable) {
			if(!world.equalsIgnoreCase(worldName))
				continue;

			settings.worldsToUnload.remove(worldName);
			settings.worldsToDisable.remove(worldName);

			String msg = "NoPreload";
			msg = String.format("%s[%s]", ChatColor.DARK_AQUA, msg);
			msg = String.format("%s%s ", msg, ChatColor.WHITE);
			String msg1 = msg;
			msg1 += worldName + " has been reset.";
			sender.sendMessage(msg1);
			String msg2 = msg;
			msg2 += "However, the server needs to be restarted ";
			msg2 += "to load the world.";
			sender.sendMessage(msg2);

			return true;
		}

		for(String world : settings.worldsToUnload) {
			if(!world.equalsIgnoreCase(worldName))
				continue;

			settings.worldsToUnload.remove(worldName);
			settings.worldsToDisable.remove(worldName);

			String msg = "NoPreload";
			msg = String.format("%s[%s]", ChatColor.DARK_AQUA, msg);
			msg = String.format("%s%s ", msg, ChatColor.WHITE);
			msg += worldName + " has been reset.";
			sender.sendMessage(msg);

			return true;
		}

		String msg = "NoPreload";
		msg = String.format("%s[%s]", ChatColor.DARK_AQUA, msg);
		msg = String.format("%s%s ", msg, ChatColor.WHITE);
		msg += worldName + " does not appear to have been modified.";
		sender.sendMessage(msg);
		return true;
	}

	private void loadSettings() {
		File fSettings = new File(getDataFolder(), "settings.npr");

		if(!fSettings.exists())
			return;

		FileInputStream fIn = null;
		ObjectInputStream in = null;

		try {
			fIn = new FileInputStream(fSettings);
			in = new ObjectInputStream(fIn);
			settings = (Settings) in.readObject();
		}
		catch(Exception e) {}
		finally {
			try {
				if(fIn != null)
					fIn.close();

				if(in != null)
					in.close();
			}
			catch(Exception e) {}
		}
	}

	private void saveSettings() {
		if(settings == null)
			return;

		File fSettings = new File(getDataFolder(), "settings.npr");

		try {
			getDataFolder().mkdirs();
		}
		catch(Exception e) {
			return;
		}

		FileOutputStream fOut = null;
		ObjectOutputStream out = null;

		try {
			fOut = new FileOutputStream(fSettings);
			out = new ObjectOutputStream(fOut);
			out.writeObject(settings);
		}
		catch(Exception e) {}
		finally {
			try {
				if(fOut != null)
					fOut.close();

				if(out != null)
					out.close();
			}
			catch(Exception e) {}
		}
	}
}
