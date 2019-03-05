package storybook.ui.table;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

import org.hibernate.Session;

import storybook.controller.BookController;
import storybook.exim.exporter.TableExporter;
import storybook.model.BookModel;
import storybook.model.hbn.dao.SpeciesDAOImpl;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.Species;
import storybook.ui.MainFrame;
import storybook.ui.SbView;

@SuppressWarnings("serial")
public class SpeciesTable extends AbstractTable { // New class for Species Table 

	public SpeciesTable(MainFrame mainFrame) {
		super(mainFrame);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void init() {
		columns = SbColumnFactory.getInstance().getSpeciesColumns();
	}


	@Override
	protected void modelPropertyChangeLocal(PropertyChangeEvent evt) {
		try {
			String propName = evt.getPropertyName();
			if (BookController.SpeciesProps.INIT.check(propName)) {
				initTableModel(evt);
			} else if (BookController.SpeciesProps.UPDATE.check(propName)) {
				updateEntity(evt);
			} else if (BookController.SpeciesProps.NEW.check(propName)) {
				newEntity(evt);
			} else if (BookController.SpeciesProps.DELETE.check(propName)) {
				deleteEntity(evt);
			} else if (BookController.CommonProps.EXPORT.check(propName) 
				&& ((SbView)evt.getNewValue()).getName().equals("Species")) {
				TableExporter.exportTable(mainFrame,(SbView)evt.getNewValue());
			}
		} catch (Exception e) {
		}
	}
	
	@Override
	protected void sendSetEntityToEdit(int row) {
		if (row == -1) {
			return;
		}
		Species species = (Species) getEntityFromRow(row);
//		mainFrame.showView(ViewName.EDITOR);
		mainFrame.showEditorAsDialog(species);
	}
	
	@Override
	protected void sendSetNewEntityToEdit(AbstractEntity entity) {
//		mainFrame.showView(ViewName.EDITOR);
		mainFrame.showEditorAsDialog(entity);
	}

	@Override
	protected synchronized void sendDeleteEntity(int row) {
		Species species = (Species) getEntityFromRow(row);
		ctrl.deleteSpecies(species);
	}
	
	@Override
	protected synchronized void sendDeleteEntities(int[] rows) {
		ArrayList<Long> ids = new ArrayList<>();
		for (int row : rows) {
			Species species = (Species) getEntityFromRow(row);
			ids.add(species.getId());
		}
		ctrl.deleteMultiSpecies(ids);
	}
	
	@Override
	protected AbstractEntity getEntity(Long id) {
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		SpeciesDAOImpl dao = new SpeciesDAOImpl(session);
		Species species = dao.find(id);
		model.commit();
		return species;
	}

	@Override
	protected AbstractEntity getNewEntity() {
		return new Species();
	}

	@Override
	public String getTableName() {
		return("Species");
	}
}
