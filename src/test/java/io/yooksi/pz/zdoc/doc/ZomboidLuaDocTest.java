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
package io.yooksi.pz.zdoc.doc;

import java.io.IOException;
import java.util.*;

import org.jetbrains.annotations.TestOnly;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

import io.yooksi.pz.zdoc.TestWorkspace;
import io.yooksi.pz.zdoc.compile.JavaCompiler;
import io.yooksi.pz.zdoc.element.lua.*;
import io.yooksi.pz.zdoc.element.mod.AccessModifierKey;
import io.yooksi.pz.zdoc.element.mod.MemberModifier;

class ZomboidLuaDocTest extends TestWorkspace {

	private static final LuaType LUA_ARRAY_LIST_OBJECT = new LuaType(
			"ArrayList", new LuaType("Object")
	);
	private static final LuaType LUA_ARRAY_LIST_STRING_OBJECT = new LuaType(
			"ArrayList", ImmutableList.of(new LuaType("String"), new LuaType("Object"))
	);
	private static final LuaType LUA_ARRAY_LIST_INNER_CLASS = new LuaType(
			"ArrayList", new LuaType("ZomboidLuaDocTest.InnerClass")
	);
	private static final LuaType LUA_ARRAY_LIST_UNKNOWN = new LuaType(
			"ArrayList", new LuaType("Unknown")
	);
	private static final LuaClass TEST_LUA_CLASS = new LuaClass(
			ZomboidLuaDocTest.class.getSimpleName(), ZomboidLuaDocTest.class.getName()
	);

	ZomboidLuaDocTest() {
		super("zomboidDoc.lua");
	}

	@Test
	void shouldWriteZomboidLuaDocClassNameToFile() throws IOException {

		List<String> expectedResult = ImmutableList.of(
				"---@class ZomboidLuaDocTest : io.yooksi.pz.zdoc.doc.ZomboidLuaDocTest",
				"ZomboidLuaDocTest = {}"
		);
		ZomboidLuaDoc zDoc = new ZomboidLuaDoc(TEST_LUA_CLASS);
		Assertions.assertEquals(expectedResult, writeToFileAndRead(zDoc));
	}

	@Test
	void shouldWriteZomboidLuaDocFieldsToFile() throws IOException {

		List<LuaField> luaFields = ImmutableList.of(
				new LuaField(new LuaType(Object.class.getSimpleName()),
						"object", new MemberModifier(AccessModifierKey.PUBLIC)
				),
				new LuaField(new LuaType(String.class.getSimpleName()),
						"text", new MemberModifier(AccessModifierKey.PRIVATE)
				),
				new LuaField(new LuaType(Integer.class.getSimpleName()),
						"number", new MemberModifier(AccessModifierKey.PROTECTED)
				),
				new LuaField(new LuaType(Class.class.getSimpleName()),
						"clazz", new MemberModifier(AccessModifierKey.DEFAULT)
				)
		);
		ZomboidLuaDoc zDoc = new ZomboidLuaDoc(
				TEST_LUA_CLASS, luaFields, new HashSet<>()
		);
		List<String> expectedResult = ImmutableList.of(
				"---@class ZomboidLuaDocTest : io.yooksi.pz.zdoc.doc.ZomboidLuaDocTest",
				"---@field public object Object",
				"---@field private text String",
				"---@field protected _number Integer",
				"---@field clazz Class",
				"ZomboidLuaDocTest = {}"
		);
		Assertions.assertEquals(expectedResult, writeToFileAndRead(zDoc));
	}

	@Test
	void shouldWriteZomboidLuaDocParameterizedFieldsToFile() throws IOException {

		List<LuaField> luaFields = ImmutableList.of(
				new LuaField(LUA_ARRAY_LIST_OBJECT,
						"object", new MemberModifier(AccessModifierKey.PUBLIC)
				),
				new LuaField(LUA_ARRAY_LIST_STRING_OBJECT,
						"text", new MemberModifier(AccessModifierKey.PRIVATE)
				),
				new LuaField(LUA_ARRAY_LIST_INNER_CLASS,
						"inner", new MemberModifier(AccessModifierKey.PROTECTED)
				),
				new LuaField(LUA_ARRAY_LIST_UNKNOWN,
						"anything", new MemberModifier(AccessModifierKey.DEFAULT)
				)
		);
		ZomboidLuaDoc zDoc = new ZomboidLuaDoc(
				TEST_LUA_CLASS, luaFields, new HashSet<>()
		);
		List<String> expectedResult = ImmutableList.of(
				"---@class ZomboidLuaDocTest : io.yooksi.pz.zdoc.doc.ZomboidLuaDocTest",
				"---@field public object ArrayList|Object",
				"---@field private text ArrayList|String|Object",
				"---@field protected inner ArrayList|ZomboidLuaDocTest.InnerClass",
				"---@field anything ArrayList|Unknown",
				"ZomboidLuaDocTest = {}"
		);
		Assertions.assertEquals(expectedResult, writeToFileAndRead(zDoc));
	}

	@Test
	void shouldWriteZomboidLuaDocMethodsToFile() throws IOException {

		Set<LuaMethod> luaMethods = new LinkedHashSet<>();
		luaMethods.add(LuaMethod.Builder.create("getText").withOwner(TEST_LUA_CLASS)
				.withModifier(new MemberModifier(AccessModifierKey.PUBLIC))
				.withReturnType(new LuaType("String")).withParams(ImmutableList.of(
						new LuaParameter(new LuaType("Object"), "param0"))).build()
		);
		luaMethods.add(LuaMethod.Builder.create("getNumber").withOwner(TEST_LUA_CLASS)
				.withModifier(new MemberModifier(AccessModifierKey.PROTECTED))
				.withReturnType(new LuaType("Integer")).withParams(
						ImmutableList.of(new LuaParameter(new LuaType("Object"), "param1"),
								new LuaParameter(new LuaType("int"), "param2"))).build()
		);
		luaMethods.add(LuaMethod.Builder.create("getInnerClass").withOwner(TEST_LUA_CLASS)
				.withModifier(new MemberModifier(AccessModifierKey.PRIVATE))
				.withReturnType(new LuaType("ZomboidLuaDocTest.InnerClass")).withParams(
						ImmutableList.of(new LuaParameter(new LuaType("Object"), "param1"),
								new LuaParameter(new LuaType("boolean"), "param2"),
								new LuaParameter(new LuaType("Object[]"), "param3"))).build()
		);
		luaMethods.add(LuaMethod.Builder.create("getArray").withOwner(TEST_LUA_CLASS)
				.withModifier(new MemberModifier(AccessModifierKey.DEFAULT))
				.withReturnType(new LuaType("Object[]")).build()
		);
		ZomboidLuaDoc zDoc = new ZomboidLuaDoc(
				TEST_LUA_CLASS, new ArrayList<>(), luaMethods
		);
		List<String> expectedResult = ImmutableList.of(
				"---@class ZomboidLuaDocTest : io.yooksi.pz.zdoc.doc.ZomboidLuaDocTest",
				"ZomboidLuaDocTest = {}",
				"",
				"---@public",
				"---@param param0 Object",
				"---@return String",
				"function ZomboidLuaDocTest:getText(param0) end",
				"",
				"---@protected",
				"---@param param1 Object",
				"---@param param2 int",
				"---@return Integer",
				"function ZomboidLuaDocTest:getNumber(param1, param2) end",
				"",
				"---@private",
				"---@param param1 Object",
				"---@param param2 boolean",
				"---@param param3 Object[]",
				"---@return ZomboidLuaDocTest.InnerClass",
				"function ZomboidLuaDocTest:getInnerClass(param1, param2, param3) end",
				"",
				"---@return Object[]",
				"function ZomboidLuaDocTest:getArray() end"
		);
		Assertions.assertEquals(expectedResult, writeToFileAndRead(zDoc));
	}

	@Test
	void shouldWriteZomboidLuaDocMethodsWithCommentsToFile() throws IOException {

		Set<LuaMethod> luaMethods = new LinkedHashSet<>();
		luaMethods.add(LuaMethod.Builder.create("getText").withOwner(TEST_LUA_CLASS)
				.withReturnType(new LuaType("String"))
				.withParams(ImmutableList.of(new LuaParameter(new LuaType("Object"), "param0")))
				.withComment("this method has a single-line comment").build()
		);
		luaMethods.add(LuaMethod.Builder.create("getNumber").withOwner(TEST_LUA_CLASS)
				.withReturnType(new LuaType("Integer")).withParams(ImmutableList.of(
						new LuaParameter(new LuaType("Object"), "param1"),
						new LuaParameter(new LuaType("int"), "param2"))
				).withComment("this method has a\nmulti-line comment").build()
		);
		luaMethods.add(LuaMethod.Builder.create("getInnerClass").withOwner(TEST_LUA_CLASS)
				.withReturnType(new LuaType("ZomboidLuaDocTest.InnerClass"))
				.withParams(ImmutableList.of(new LuaParameter(new LuaType("Object"), "param1"),
						new LuaParameter(new LuaType("boolean"), "param2"),
						new LuaParameter(new LuaType("Object[]"), "param3"))
				).withComment("this method has a\rmulti-line comment").build()
		);
		luaMethods.add(LuaMethod.Builder.create("getArray").withOwner(TEST_LUA_CLASS)
				.withModifier(new MemberModifier(AccessModifierKey.DEFAULT))
				.withReturnType(new LuaType("Object[]"))
				.withComment("this method has a\r\nmulti-line comment").build()
		);
		ZomboidLuaDoc zDoc = new ZomboidLuaDoc(
				TEST_LUA_CLASS, new ArrayList<>(), luaMethods
		);
		List<String> expectedResult = ImmutableList.of(
				"---@class ZomboidLuaDocTest : io.yooksi.pz.zdoc.doc.ZomboidLuaDocTest",
				"ZomboidLuaDocTest = {}",
				"",
				"---this method has a single-line comment",
				"---@param param0 Object",
				"---@return String",
				"function ZomboidLuaDocTest:getText(param0) end",
				"",
				"---this method has a",
				"---",
				"---multi-line comment",
				"---@param param1 Object",
				"---@param param2 int",
				"---@return Integer",
				"function ZomboidLuaDocTest:getNumber(param1, param2) end",
				"",
				"---this method has a",
				"---",
				"---multi-line comment",
				"---@param param1 Object",
				"---@param param2 boolean",
				"---@param param3 Object[]",
				"---@return ZomboidLuaDocTest.InnerClass",
				"function ZomboidLuaDocTest:getInnerClass(param1, param2, param3) end",
				"",
				"---this method has a",
				"---",
				"---multi-line comment",
				"---@return Object[]",
				"function ZomboidLuaDocTest:getArray() end"
		);
		Assertions.assertEquals(expectedResult, writeToFileAndRead(zDoc));
	}

	@Test
	void shouldWriteZomboidLuaDocMethodsWithParameterizedTypesToFile() throws IOException {

		Set<LuaMethod> luaMethods = new LinkedHashSet<>();
		luaMethods.add(LuaMethod.Builder.create("getObjectList").withOwner(TEST_LUA_CLASS)
				.withModifier(new MemberModifier(AccessModifierKey.PUBLIC))
				.withReturnType(LUA_ARRAY_LIST_OBJECT).withParams(ImmutableList.of(
						new LuaParameter(LUA_ARRAY_LIST_STRING_OBJECT, "param"))).build()
		);
		luaMethods.add(LuaMethod.Builder.create("getStringObjectList").withOwner(TEST_LUA_CLASS)
				.withModifier(new MemberModifier(AccessModifierKey.PROTECTED))
				.withReturnType(LUA_ARRAY_LIST_STRING_OBJECT).withParams(ImmutableList.of(
						new LuaParameter(new LuaType("Object"), "param1"),
						new LuaParameter(LUA_ARRAY_LIST_OBJECT, "param2"))).build()
		);
		luaMethods.add(LuaMethod.Builder.create("getInnerClassList").withOwner(TEST_LUA_CLASS)
				.withModifier(new MemberModifier(AccessModifierKey.PRIVATE))
				.withReturnType(LUA_ARRAY_LIST_INNER_CLASS).withParams(ImmutableList.of(
						new LuaParameter(new LuaType("Object"), "param1"),
						new LuaParameter(LUA_ARRAY_LIST_STRING_OBJECT, "param2"),
						new LuaParameter(new LuaType("Object[]"), "param3"))).build()
		);
		luaMethods.add(LuaMethod.Builder.create("getUnknownList").withOwner(TEST_LUA_CLASS)
				.withModifier(new MemberModifier(AccessModifierKey.DEFAULT))
				.withReturnType(LUA_ARRAY_LIST_UNKNOWN).withParams(ImmutableList.of(
						new LuaParameter(LUA_ARRAY_LIST_UNKNOWN, "param1"))).build()
		);
		ZomboidLuaDoc zDoc = new ZomboidLuaDoc(
				TEST_LUA_CLASS, new ArrayList<>(), luaMethods
		);
		List<String> expectedResult = ImmutableList.of(
				"---@class ZomboidLuaDocTest : io.yooksi.pz.zdoc.doc.ZomboidLuaDocTest",
				"ZomboidLuaDocTest = {}",
				"",
				"---@public",
				"---@param param ArrayList|String|Object",
				"---@return ArrayList|Object",
				"function ZomboidLuaDocTest:getObjectList(param) end",
				"",
				"---@protected",
				"---@param param1 Object",
				"---@param param2 ArrayList|Object",
				"---@return ArrayList|String|Object",
				"function ZomboidLuaDocTest:getStringObjectList(param1, param2) end",
				"",
				"---@private",
				"---@param param1 Object",
				"---@param param2 ArrayList|String|Object",
				"---@param param3 Object[]",
				"---@return ArrayList|ZomboidLuaDocTest.InnerClass",
				"function ZomboidLuaDocTest:getInnerClassList(param1, param2, param3) end",
				"",
				"---@param param1 ArrayList|Unknown",
				"---@return ArrayList|Unknown",
				"function ZomboidLuaDocTest:getUnknownList(param1) end"
		);
		Assertions.assertEquals(expectedResult, writeToFileAndRead(zDoc));
	}

	@Test
	void shouldWriteZomboidLuaDocMethodsWithVariadicArgumentsToFile() throws IOException {

		Set<LuaMethod> luaMethods = new LinkedHashSet<>();
		luaMethods.add(LuaMethod.Builder.create("getObject").withOwner(TEST_LUA_CLASS)
				.withReturnType(new LuaType("Object")).withVarArg(true).withParams(ImmutableList.of(
						new LuaParameter(new LuaType("Object"), "varargs"))).build()
		);
		luaMethods.add(LuaMethod.Builder.create("getText").withOwner(TEST_LUA_CLASS)
				.withReturnType(new LuaType("String")).withVarArg(true).withParams(ImmutableList.of(
						new LuaParameter(new LuaType("Object[]"), "param"),
						new LuaParameter(new LuaType("String"), "varargs"))).build()
		);
		luaMethods.add(LuaMethod.Builder.create("getNumber").withOwner(TEST_LUA_CLASS)
				.withReturnType(new LuaType("Integer")).withVarArg(true).withParams(ImmutableList.of(
						new LuaParameter(new LuaType("Object"), "param0"),
						new LuaParameter(new LuaType("Integer"), "param1"),
						new LuaParameter(new LuaType("String"), "varargs"))).build()
		);
		luaMethods.add(LuaMethod.Builder.create("getObjectList").withOwner(TEST_LUA_CLASS)
				.withReturnType(LUA_ARRAY_LIST_OBJECT).withVarArg(true).withParams(ImmutableList.of(
						new LuaParameter(LUA_ARRAY_LIST_STRING_OBJECT, "varargs"))).build()
		);
		luaMethods.add(LuaMethod.Builder.create("getStringObjectList").withOwner(TEST_LUA_CLASS)
				.withReturnType(LUA_ARRAY_LIST_STRING_OBJECT).withVarArg(true).withParams(ImmutableList.of(
						new LuaParameter(new LuaType("Object"), "param1"),
						new LuaParameter(LUA_ARRAY_LIST_OBJECT, "varargs"))).build()
		);
		luaMethods.add(LuaMethod.Builder.create("getInnerClassList").withOwner(TEST_LUA_CLASS)
				.withReturnType(LUA_ARRAY_LIST_INNER_CLASS).withVarArg(true).withParams(ImmutableList.of(
						new LuaParameter(new LuaType("Object"), "param1"),
						new LuaParameter(LUA_ARRAY_LIST_STRING_OBJECT, "param2"),
						new LuaParameter(new LuaType("Object[]"), "varargs"))).build()
		);
		luaMethods.add(LuaMethod.Builder.create("getUnknownList").withOwner(TEST_LUA_CLASS)
				.withReturnType(LUA_ARRAY_LIST_UNKNOWN).withVarArg(true).withParams(
						ImmutableList.of(new LuaParameter(LUA_ARRAY_LIST_UNKNOWN, "varargs"))).build()
		);
		ZomboidLuaDoc zDoc = new ZomboidLuaDoc(
				TEST_LUA_CLASS, new ArrayList<>(), luaMethods
		);
		List<String> expectedResult = ImmutableList.of(
				"---@class ZomboidLuaDocTest : io.yooksi.pz.zdoc.doc.ZomboidLuaDocTest",
				"ZomboidLuaDocTest = {}",
				"",
				"---@vararg Object",
				"---@return Object",
				"function ZomboidLuaDocTest:getObject(...) end",
				"",
				"---@param param Object[]",
				"---@vararg String",
				"---@return String",
				"function ZomboidLuaDocTest:getText(param, ...) end",
				"",
				"---@param param0 Object",
				"---@param param1 Integer",
				"---@vararg String",
				"---@return Integer",
				"function ZomboidLuaDocTest:getNumber(param0, param1, ...) end",
				"",
				"---@vararg ArrayList",
				"---@return ArrayList|Object",
				"function ZomboidLuaDocTest:getObjectList(...) end",
				"",
				"---@param param1 Object",
				"---@vararg ArrayList",
				"---@return ArrayList|String|Object",
				"function ZomboidLuaDocTest:getStringObjectList(param1, ...) end",
				"",
				"---@param param1 Object",
				"---@param param2 ArrayList|String|Object",
				"---@vararg Object[]",
				"---@return ArrayList|ZomboidLuaDocTest.InnerClass",
				"function ZomboidLuaDocTest:getInnerClassList(param1, param2, ...) end",
				"",
				"---@vararg ArrayList",
				"---@return ArrayList|Unknown",
				"function ZomboidLuaDocTest:getUnknownList(...) end"
		);
		Assertions.assertEquals(expectedResult, writeToFileAndRead(zDoc));
	}

	@Test
	void shouldNotWriteGlobalZomboidLuaDocMethodsAsPartOfTableToFile() throws IOException {

		Set<LuaMethod> luaMethods = new LinkedHashSet<>();
		luaMethods.add(LuaMethod.Builder.create("firstMethod").build()
		);
		luaMethods.add(LuaMethod.Builder.create("secondMethod")
				.withModifier(new MemberModifier(AccessModifierKey.PUBLIC)).build()
		);
		luaMethods.add(LuaMethod.Builder.create("thirdMethod")
				.withModifier(new MemberModifier(AccessModifierKey.PRIVATE))
				.withReturnType(new LuaType("String")).build()
		);
		luaMethods.add(LuaMethod.Builder.create("fourthMethod").withOwner(TEST_LUA_CLASS)
				.withModifier(new MemberModifier(AccessModifierKey.DEFAULT))
				.withReturnType(new LuaType("Object")).withParams(ImmutableList.of(
						new LuaParameter(new LuaType("int"), "param1"),
						new LuaParameter(new LuaType("boolean"), "param2"))).build()
		);
		ZomboidLuaDoc zDoc = new ZomboidLuaDoc(
				new LuaClass("LuaManager_GlobalObject", JavaCompiler.GLOBAL_OBJECT_CLASS),
				new ArrayList<>(), luaMethods
		);

		List<String> expectedResult = ImmutableList.of(
				"---@class LuaManager_GlobalObject : " + JavaCompiler.GLOBAL_OBJECT_CLASS,
				"LuaManager_GlobalObject = {}",
				"",
				"---@return void",
				"function firstMethod() end",
				"",
				"---@public",
				"---@return void",
				"function secondMethod() end",
				"",
				"---@private",
				"---@return String",
				"function thirdMethod() end",
				"",
				"---@param param1 int",
				"---@param param2 boolean",
				"---@return Object",
				"function fourthMethod(param1, param2) end"
		);
		Assertions.assertEquals(expectedResult, writeToFileAndRead(zDoc));
	}

	@Test
	@SuppressWarnings("ConstantConditions")
	void shouldThrowExceptionWhenModifyingImmutableFields() {

		LuaType type = new LuaType(Object.class.getName());
		ZomboidLuaDoc zDoc = new ZomboidLuaDoc(new LuaClass(type.getName()));

		Assertions.assertThrows(UnsupportedOperationException.class, () ->
				zDoc.getFields().add(new LuaField(type, "field", MemberModifier.UNDECLARED))
		);
		Assertions.assertThrows(UnsupportedOperationException.class, () ->
				zDoc.getMethods().add(LuaMethod.Builder.create("testMethod").withReturnType(type).build())
		);
	}

	@TestOnly
	private List<String> writeToFileAndRead(ZomboidLuaDoc zDoc) throws IOException {

		zDoc.writeToFile(file);
		return readFile();
	}

	@TestOnly
	@SuppressWarnings("unused")
	private static class InnerClass {
	}
}
