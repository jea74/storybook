package storybook.model.handler;



import storybook.model.hbn.dao.SpeciesDAOImpl;
import storybook.model.hbn.entity.AbstractEntity;

import storybook.model.hbn.entity.Species;
import storybook.ui.MainFrame;
import storybook.ui.table.SbColumnFactory;

public class SpeciesEntityHandler extends AbstractEntityHandler {

	public SpeciesEntityHandler(MainFrame mainFrame) {
		super(mainFrame, SbColumnFactory.getInstance().getSpeciesColumns());
	}

	@Override
	public AbstractEntity createNewEntity() {
		Species species = new Species();
		return species;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Class<T> getDAOClass() {
		return (Class<T>) SpeciesDAOImpl.class;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Class<T> getEntityClass() {
		return (Class<T>) Species.class;
	}
}
