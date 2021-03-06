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
package io.yooksi.pz.zdoc.doc.detail;

import org.apache.logging.log4j.Level;

import io.yooksi.pz.zdoc.logger.Logger;

class SignatureParsingException extends DetailParsingException {

	SignatureParsingException(String signature, String message) {
		super(String.format("Failed to parse signature \"%s\" (%s)", signature, message));
	}

	SignatureParsingException(String message) {
		super(message);
	}

	Level getLogLevel() {
		return Logger.VERBOSE;
	}
}
