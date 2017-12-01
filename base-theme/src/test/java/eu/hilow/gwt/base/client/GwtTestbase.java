/*
 * GwtTestbase.java
 *
 * Tigase XMPP Web Client
 * Copyright (C) 2012-2017 "Tigase, Inc." <office@tigase.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. Look for COPYING file in the top folder.
 * If not, see http://www.gnu.org/licenses/.
 */

package eu.hilow.gwt.base.client;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * GWT JUnit <b>integration</b> tests must extend GWTTestCase.
 * Using <code>"GwtTest*"</code> naming pattern exclude them from running with
 * surefire during the test phase.
 * <p>
 * If you run the tests using the Maven command line, you will have to
 * navigate with your browser to a specific url given by Maven.
 * See http://mojo.codehaus.org/gwt-maven-plugin/user-guide/testing.html
 * for details.
 */
public class GwtTestbase
		extends GWTTestCase {

	/**
	 * Must refer to a valid module that sources this class.
	 */
	public String getModuleName() {
		return "eu.hilow.gwt.base.baseJUnit";
	}

}
