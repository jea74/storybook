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

package storybook.model.handler;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.text.JTextComponent;

import org.hibernate.Session;

import storybook.model.BookModel;
import storybook.model.hbn.dao.LocationDAOImpl;
import storybook.model.hbn.dao.SbGenericDAOImpl;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.Location;
import storybook.toolkit.swing.AutoCompleteComboBox;
import storybook.ui.MainFrame;
import storybook.ui.table.SbColumnFactory;
import storybook.ui.combobox.LocationListCellRenderer;

/**
 * @author martin
 *
 */
public class LocationEntityHandler extends AbstractEntityHandler {

	public LocationEntityHandler(MainFrame mainFrame) {
		super(mainFrame, SbColumnFactory.getInstance().getLocationColumns());
	}

	@Override
	public ListCellRenderer getListCellRenderer() {
		return new LocationListCellRenderer();
	}

	@Override
	public AbstractEntity createNewEntity() {
		Location location = new Location();
		return location;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Class<T> getDAOClass() {
		return (Class<T>) LocationDAOImpl.class;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Class<T> getEntityClass() {
		return (Class<T>) Location.class;
	}
	
	@Override
	public Boolean verifyNoDuplicates(ArrayList<JComponent> inputs) {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		LocationDAOImpl dao = new LocationDAOImpl(session);
		List<Location> locations = dao.findAll();
		
		JTextComponent name_input = (JTextComponent)inputs.get(1);
		String name = name_input.getText();
		JTextComponent address_input = (JTextComponent)inputs.get(2);
		String address = address_input.getText();
		AutoCompleteComboBox city_input = (AutoCompleteComboBox)inputs.get(3);
		String city = (String) city_input.getJComboBox().getSelectedItem();
		AutoCompleteComboBox country_input = (AutoCompleteComboBox)inputs.get(4);
		String country = (String) country_input.getJComboBox().getSelectedItem();
		
		for(Location loc: locations) {
			if(name.equals(loc.getName()) || (address.equals(loc.getAddress()) && city.equals(loc.getCity()) && country.equals(loc.getCountry()))) {
				JOptionPane.showMessageDialog (new Frame(),"Cannot update or add locations with duplicate names, or duplicate sites(address, city, country). Please change either the name, or " +
			 "address, city, and country.");
				return false;
			}
		}
		return true;
	}
}
