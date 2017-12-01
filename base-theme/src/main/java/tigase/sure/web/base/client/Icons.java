/*
 * Icons.java
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

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author andrzej
 */
public interface Icons
		extends ClientBundle {

	@Source("icons/1-navigation-accept.png")
	ImageResource navigationAccept();

	@Source("icons/1-navigation-back.png")
	ImageResource navigationBack();

	@Source("icons/1-navigation-cancel.png")
	ImageResource navigationCancel();

	@Source("icons/1-navigation-forward.png")
	ImageResource navigationForward();

	@Source("icons/1-navigation-next-item.png")
	ImageResource navigationNextItem();

	@Source("icons/1-navigation-previous-item.png")
	ImageResource navigationPreviousItem();

	@Source("icons/1-navigation-refresh.png")
	ImageResource navigationRefresh();

	@Source("icons/6-social-person.png")
	ImageResource socialPerson();

	@Source("icons/status-available.png")
	ImageResource statusAvailable();

	@Source("icons/status-away.png")
	ImageResource statusAway();

	@Source("icons/status-busy.png")
	ImageResource statusBusy();

	@Source("icons/status-unavailable.png")
	ImageResource statusUnavailable();

	@Source("icons/2-action-settings.png")
	ImageResource settings();

	@Source("icons/4-collections-cloud.png")
	ImageResource collectionsCloud();

	@Source("icons/5-content-import-export.png")
	ImageResource gateway();

	@Source("icons/6-social-add-person.png")
	ImageResource addPerson();

	@Source("icons/6-social-cc-bcc.png")
	ImageResource muc();

	@Source("icons/4-collections-labels.png")
	ImageResource bookmarks();

	@Source("icons/10-device-access-mic.png")
	ImageResource microfoneOn();

	@Source("icons/10-device-access-mic-muted.png")
	ImageResource microfoneOff();

	@Source("icons/6-social-share.png")
	ImageResource socialShare();

}
