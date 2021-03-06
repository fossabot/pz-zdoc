/*
 * ZomboidDoc - Lua library compiler for Project Zomboid
 * Copyright (C) 2021 Matthew Cain
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package io.yooksi.pz.zdoc.logger;

import java.util.Arrays;

public enum LoggerType {

	INFO("info", "StandardLogger"),
	DEBUG("debug", "DebugLogger"),
	DEV("dev", "DevLogger");

	final String key, name;

	LoggerType(String key, String name) {
		this.key = key;
		this.name = name;
	}

	public static LoggerType get(String key, LoggerType defaultType) {
		return Arrays.stream(LoggerType.values()).filter(l ->
				l.key.equals(key)).findFirst().orElse(defaultType);
	}
}
