/*
Storybook: Open Source software for novelists and authors.
Copyright (C) 2008 - 2012 Martin Mustun

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package storybook.model.hbn.entity;

import org.w3c.dom.Node;

/**
 * @hibernate.subclass
 *   discriminator-value="1"
 */
public class ItemLink extends AbstractTagLink {

	private Item item;

	public ItemLink() {
	}

	public ItemLink(Item item, Integer type, Scene startScene, Scene endScene, Person person, Location location) {
		super(type, startScene, endScene, person, location);
		this.item = item;
	}

	public Item getItem() {
		return this.item;
	}

	public void setItem(Item item) {
		this.item = item;
	}
	
	public static ItemLink fromXml(Node node) {
		ItemLink p=new ItemLink();
		p.setId(getXmlLong(node,"id"));
		return(p);
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}
		ItemLink test = (ItemLink) obj;
		boolean ret = true;
		ret = ret && equalsLongNullValue(item.id, test.getItem().getId());
		return ret;
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = hash * 31 + item.hashCode();
		return hash;
	}
}
