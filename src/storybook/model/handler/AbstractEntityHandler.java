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

import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.ListCellRenderer;

import storybook.model.hbn.dao.SbGenericDAOImpl;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.ui.MainFrame;
import storybook.ui.table.SbColumn;

/**
 * @author martin
 *
 */
public abstract class AbstractEntityHandler {

	protected MainFrame mainFrame;
	protected Vector<SbColumn> columns;

	public AbstractEntityHandler(MainFrame mainFrame, Vector<SbColumn> columns) {
		this.mainFrame = mainFrame;
		this.columns = columns;
	}

	public abstract <T> Class<T> getEntityClass();

	public abstract AbstractEntity createNewEntity();

	public AbstractEntity newEntity(AbstractEntity entity) {
		return createNewEntity();
	}

	public abstract <T> Class<T> getDAOClass();

	public SbGenericDAOImpl<?, ?> createDAO() {
		try {
			return (SbGenericDAOImpl<?, ?>) getDAOClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			System.err.println("AbstractEntityHandler.createDAO() Exception:"+e.getMessage());
		}
		return null;
	}

	public Vector<SbColumn> getColumns() {
		return columns;
	}

	public boolean hasListCellRenderer() {
		return getListCellRenderer() != null;
	}

	public ListCellRenderer getListCellRenderer() {
		return null;
	}
	
	public Boolean verifyNoDuplicates(ArrayList<JComponent> inputs) {
		return true;
	}
}
