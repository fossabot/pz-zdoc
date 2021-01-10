/*
 * ZomboidDoc - Project Zomboid API parser and lua compiler.
 * Copyright (C) 2020 Matthew Cain
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

import io.yooksi.pz.zdoc.doc.ZomboidAPIDoc;
import io.yooksi.pz.zdoc.element.IMember;
import io.yooksi.pz.zdoc.logger.Logger;
import io.yooksi.pz.zdoc.util.Utils;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.util.ClassUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class Detail<T extends IMember> {

	final String name;
	protected final ZomboidAPIDoc document;
	private final List<T> entries;

	public Detail(String name, ZomboidAPIDoc document) throws DetailParsingException {
		this.name = name;
		this.document = document;
		this.entries = Collections.unmodifiableList(parse());
	}

	protected Elements getDetail() {

		Optional<Element> detailElement = document.getDocument().getElementsByTag("a")
				.stream().filter(e -> e.hasAttr("name") &&
						e.attr("name").equals(name)).findFirst();

		if (detailElement.isPresent()) {
			return detailElement.get().parent().getElementsByTag("ul");
		}
		else return new Elements();
	}

	protected Element qualifyZomboidClassElements(Element element) throws DetailParsingException {

		for (Element e : element.getElementsByTag("a"))
		{
			List<TextNode> textNodes = e.textNodes();
			if (textNodes.size() != 1)
			{
				String format = "Unexpected number of hyperlink text nodes (%s)";
				throw new DetailParsingException(this, String.format(format, textNodes.size()));
			}
			TextNode textNode = textNodes.get(0);
			String absUrl = e.absUrl("href");
			if (absUrl.isBlank())
			{
				String format = "Missing href for node \"%s\"";
				throw new DetailParsingException(this, String.format(format, textNode.text()));
			}
			String urlPath = ZomboidAPIDoc.resolveURLPath(absUrl).toString();
			/*
			 * remove file extension from API URL
			 * "/zombie/Zombie.html" -> "/zombie/Zombie"
			 */
			urlPath = urlPath.substring(0, FilenameUtils.indexOfExtension(urlPath));
			/*
			 * ClassUtils#convertResourcePathToClassName path parameter
			 * needs to be in Unix format, otherwise we get a malformed return value
			 */
			String packagePath = urlPath.replace('\\', '/');
			textNode.text(ClassUtils.convertResourcePathToClassName(packagePath));
		}
		return element;
	}

	public @Nullable T getEntry(String name) {
		return entries.stream().filter(e -> e.getName().equals(name)).findFirst().orElse(null);
	}

	public @UnmodifiableView List<T> getEntries() {
		return entries;
	}

	protected abstract List<T> parse() throws DetailParsingException;
}
