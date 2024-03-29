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
	import java.io.Serializable;
	import java.lang.String;
	import java.util.ArrayList;
	import java.util.List;
//* IMPORTS: BUKKIT
	//* NOT NEEDED
//* IMPORTS: OTHER
	//* NOT NEEDED

public class Settings implements Serializable
{
	private static final long serialVersionUID = 1L;
	public List<String> worldsToDisable = new ArrayList<String>();
	public List<String> worldsToUnload = new ArrayList<String>();
}