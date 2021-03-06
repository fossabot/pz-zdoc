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
package io.yooksi.pz.zdoc.compile;

import java.util.Set;

import io.yooksi.pz.zdoc.doc.ZomboidDoc;

interface ICompiler<T extends ZomboidDoc> {

	/**
	 * Compile code represented by {@code ZomboidDoc} documents.
	 *
	 * @return {@code Set} of compiled documents.
	 *
	 * @throws CompilerException if an error occurred during compilation.
	 */
	Set<T> compile() throws CompilerException;
}
