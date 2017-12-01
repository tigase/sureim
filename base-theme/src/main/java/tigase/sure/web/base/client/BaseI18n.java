/*
 * BaseI18n.java
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
package tigase.sure.web.base.client;

import com.google.gwt.i18n.client.Constants;

/**
 * @author andrzej
 */
public interface BaseI18n
		extends Constants {

	@DefaultStringValue("Authenticate")
	String authenticate();

	@DefaultStringValue("XMPP ID")
	String jid();

	@DefaultStringValue("Username")
	String username();

	@DefaultStringValue("Login")
	String login();

	@DefaultStringValue("Domain")
	String domain();

	@DefaultStringValue("Password")
	String password();

	@DefaultStringValue("2016 Tigase, Inc. All rights reserved")
	String copyright();

	@DefaultStringValue("Version:")
	String version();

	@DefaultStringValue("Terms of Service")
	String termsOfService();

	@DefaultStringValue("Privacy Policy")
	String privacyPolicy();

	@DefaultStringValue("Contact")
	String contactForm();

	@DefaultStringValue("Support")
	String supportForm();

	@DefaultStringValue("Accept")
	String accept();

	@DefaultStringValue("Add")
	String add();

	@DefaultStringValue("Modify")
	String modify();

	@DefaultStringValue("Delete")
	String delete();

	@DefaultStringValue("Error")
	String error();

	@DefaultStringValue("Success")
	String success();

	@DefaultStringValue("Cancel")
	String cancel();

	@DefaultStringValue("Information")
	String info();

	@DefaultStringValue("Confirm")
	String confirm();

	@DefaultStringValue("Close")
	String close();

	@DefaultStringValue("Name")
	String name();

	@DefaultStringValue("Group")
	String group();

	@DefaultStringValue("Advanced")
	String advanced();

	@DefaultStringValue("Connection URL (Bosh/WebSocket)")
	String connectionUrlBoshWs();

	@DefaultStringValue("Next")
	String next();

	@DefaultStringValue("Other")
	String other();

}
