/*
    Unigrid Hedgehog
    Copyright © 2021-2022 The Unigrid Foundation, UGD Software AB

    This program is free software: you can redistribute it and/or modify it under the terms of the
    addended GNU Affero General Public License as published by the The Unigrid Foundation and
    the Free Software Foundation, version 3 of the License (see COPYING and COPYING.addendum).

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
    even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU Affero General Public License and the addendum for more details.

    You should have received an addended copy of the GNU Affero General Public License with this program.
    If not, see <http://www.gnu.org/licenses/> and <https://github.com/unigrid-project/hedgehog>.
*/

package org.unigrid.hedgehog.model;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.geronimo.arthur.api.RegisterClass;
import picocli.CommandLine.IVersionProvider;

@Slf4j
@RegisterClass(all = true)
public class VersionProvider implements IVersionProvider {
	public static final String VERSION_PROPERTY_NAME = "HEDGEHOG_VERSION";
	public static final String VERSION_PAD_PROPERTY_NAME = "HEDGEHOG_VERSION_PAD";
	private static final int VERSION_PAD_WIDTH = 42;
	private static final String DEFAULT_AUTHOR = "Unigrid";
	private static final String DEFAULT_NAME = "Hedgehog";
	private static final String DEFAULT_VERSION = "0.0.0-BASTARD";

	@Override
	public String[] getVersion() throws Exception {
		final Properties properties = new Properties();
		String name = String.format("%s %s", DEFAULT_AUTHOR, DEFAULT_NAME);
		String version  = DEFAULT_VERSION;

		try {
			properties.load(getClass().getResourceAsStream("application.properties"));
			name = Objects.requireNonNull(properties.getProperty("project.name"));
			version = Objects.requireNonNull(properties.getProperty("project.version"));
		} catch (IOException ex) {
			log.atError().log("Unable to determine version via property file: {}", ex);
		}

		final String completeVersion = String.format("%s %s", name, version);

		/* Right-aligns the output of the version string for the header output. With this implementation, the maximum
		   length of completeVersion is VERSION_PAD_WIDTH. */

		System.setProperty(VERSION_PROPERTY_NAME, completeVersion);
		System.setProperty(VERSION_PAD_PROPERTY_NAME,
			StringUtils.rightPad(" ", VERSION_PAD_WIDTH - completeVersion.length())
		);

		return new String[] { completeVersion };
	}

	public static String getAtIndex(int i) throws Exception {
		return new VersionProvider().getVersion()[0].split(" ")[i];
	}

	@SneakyThrows
	public static String getAuthor() {
		try {
			return getAtIndex(0);
		} catch (IndexOutOfBoundsException ex) {
			return DEFAULT_AUTHOR;
		}
	}

	@SneakyThrows
	public static String getName() {
		try {
			return getAtIndex(1);
		} catch (IndexOutOfBoundsException ex) {
			return DEFAULT_NAME;
		}
	}
}