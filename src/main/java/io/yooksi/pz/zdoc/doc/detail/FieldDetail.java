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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.list.SetUniqueList;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.collect.Sets;

import io.yooksi.pz.zdoc.doc.ZomboidAPIDoc;
import io.yooksi.pz.zdoc.element.java.JavaClass;
import io.yooksi.pz.zdoc.element.java.JavaField;
import io.yooksi.pz.zdoc.element.mod.AccessModifierKey;
import io.yooksi.pz.zdoc.element.mod.MemberModifier;
import io.yooksi.pz.zdoc.element.mod.ModifierKey;
import io.yooksi.pz.zdoc.logger.Logger;

public class FieldDetail extends Detail<JavaField> {

	public FieldDetail(ZomboidAPIDoc document) throws DetailParsingException {
		super("field.detail", document);
	}

	@Override
	protected List<JavaField> parse() {

		Elements detail = getDetail();
		List<JavaField> result = new ArrayList<>();

		for (Element blockList : detail)
		{
			Element header = blockList.getElementsByTag("h4").first();
			String name = header != null ? header.text() : "unknown";

			Signature signature;
			try {
				Element eSignature = blockList.getElementsByTag("pre").first();
				if (eSignature == null) {
					throw new DetailParsingException("Unable to find field signature for field: " + name);
				}
				signature = new Signature(qualifyZomboidClassElements(eSignature));
			}
			catch (DetailParsingException e)
			{
				Logger.error(e.getMessage());
				continue;
			}
			JavaClass type = TypeSignatureParser.parse(signature.type);
			if (type != null) {
				result.add(new JavaField(type, signature.name, signature.modifier, signature.comment));
			}
			else Logger.detail(String.format("Excluding field (%s) from detail, " +
					"class %s does not exist", signature.toString(), signature.type));
		}
		return result;
	}

	public @Nullable JavaField getEntry(String name) {
		return getEntries().stream().filter(e -> e.getName().equals(name)).findFirst().orElse(null);
	}

	@Override
	public Set<JavaField> getEntries(String name) {
		return Sets.newHashSet(getEntry(name));
	}

	static class Signature extends DetailSignature {

		final MemberModifier modifier;
		final String type, name, comment;

		Signature(String signatureText) throws SignatureParsingException {
			super(signatureText);
			Logger.debug("Parsing field signature: " + signature);

			String[] elements = signature.split("\\s+");
			if (elements.length < 2) {
				throw new SignatureParsingException(signature, "missing one or more elements");
			}
			int index = 0;
			/*
			 * parse signature access modifier (optional)
			 */
			AccessModifierKey access = AccessModifierKey.get(elements[0]);
			if (access != AccessModifierKey.DEFAULT) {
				index = 1;
			}
			/*
			 * parse signature non-access modifier (optional)
			 */
			SetUniqueList<ModifierKey> modifierKeys = SetUniqueList.setUniqueList(new ArrayList<>());
			for (; index < elements.length; index++)
			{
				Collection<ModifierKey> foundKeys = ModifierKey.get(elements[index]);
				if (!foundKeys.contains(ModifierKey.UNDECLARED)) {
					modifierKeys.addAll(foundKeys);
				}
				else break;
			}
			if (modifierKeys.isEmpty()) {
				modifierKeys.add(ModifierKey.UNDECLARED);
			}
			this.modifier = new MemberModifier(access, modifierKeys);
			/*
			 * parse signature type and name
			 */
			String[] data = new String[]{ "type", "name" };
			for (int i = 0; i < data.length; i++, index++)
			{
				if (index < elements.length) {
					data[i] = elements[index];
				}
				else throw new SignatureParsingException(signature, "missing element " + data[0]);
			}
			this.type = data[0];
			this.name = data[1];
			/*
			 * parse signature comment (optional)
			 */
			if (index < elements.length)
			{
				StringBuilder sb = new StringBuilder();
				for (; index < elements.length; index++) {
					sb.append(elements[index]).append(" ");
				}
				sb.deleteCharAt(sb.length() - 1);
				this.comment = sb.toString();
			}
			else this.comment = "";
		}

		private Signature(Element element) throws SignatureParsingException {
			this(element.text());
		}
	}
}
