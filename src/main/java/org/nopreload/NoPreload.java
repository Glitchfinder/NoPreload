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
	//* NOT NEEDED
//* IMPORTS: BUKKIT
	import org.bukkit.Bukkit;
	import org.bukkit.event.EventHandler;
	import org.bukkit.event.EventPriority;
	import org.bukkit.event.Listener;
	import org.bukkit.event.world.WorldLoadEvent;
	import org.bukkit.plugin.java.JavaPlugin;
	import org.bukkit.World;
//* IMPORTS: OTHER
	//* NOT NEEDED

public class NoPreload extends JavaPlugin implements Listener
{
	public void onDisable() {}
	public void onEnable() {
		for(World world : Bukkit.getWorlds()) {
			world.setKeepSpawnInMemory(false);
		}
		getServer().getPluginManager().registerEvents(this, this);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onWorldLoad(WorldLoadEvent event) {
		if(event.getWorld() == null)
			return;

		event.getWorld().setKeepSpawnInMemory(false);
	}
}
