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

import storybook.model.hbn.dao.MemoDAOImpl;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.Memo;
import storybook.ui.MainFrame;
import storybook.ui.table.SbColumnFactory;

/**
 * @author martin
 *
 */
public class MemoEntityHandler extends AbstractEntityHandler {

	public MemoEntityHandler(MainFrame mainFrame) {
		super(mainFrame, SbColumnFactory.getInstance().getMemoColumns());
	}

	@Override
	public AbstractEntity createNewEntity() {
		Memo memo = new Memo();
		return memo;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Class<T> getDAOClass() {
		return (Class<T>) MemoDAOImpl.class;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Class<T> getEntityClass() {
		return (Class<T>) Memo.class;
	}
}
