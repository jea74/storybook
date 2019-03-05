package storybook.ui.table.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.hibernate.Session;
import org.miginfocom.swing.MigLayout;

import storybook.SbConstants.ClientPropertyName;
import storybook.model.BookModel;
import storybook.model.hbn.entity.Species;
import storybook.ui.MainFrame;

@SuppressWarnings("serial")
public class SpeciesTableCellRenderer extends DefaultTableCellRenderer { // New class for rendering Species Table cells

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		MainFrame mainFrame = (MainFrame) table
				.getClientProperty(ClientPropertyName.MAIN_FRAME.toString());
		JLabel lbText = (JLabel) super.getTableCellRendererComponent(table,
				null, isSelected, hasFocus, row, column);
		JPanel panel = new JPanel(new MigLayout("insets 2"));
		panel.setOpaque(true);
		panel.setBackground(lbText.getBackground());
		if (value instanceof Species) {
			BookModel model = mainFrame.getBookModel();
			Session session = model.beginTransaction();
			Long id = ((Species) value).getId();
			Species species = (Species) session.get(Species.class, id);
			model.commit();
			panel.add(new JLabel(species.toString()));
		} else {
			panel.add(new JLabel(value.toString()));
		}
		return panel;
	}

}
