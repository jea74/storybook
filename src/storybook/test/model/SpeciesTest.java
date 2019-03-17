package storybook.test.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.hibernate.Session;
import org.junit.Test;
import junit.framework.TestCase;
import storybook.SbConstants.ClientPropertyName;
import storybook.model.BookModel;
import storybook.model.EntityUtil;
import storybook.model.handler.SpeciesEntityHandler;
import storybook.model.hbn.dao.SpeciesDAOImpl;
import storybook.model.hbn.entity.Species;
import storybook.ui.MainFrame;

public class SpeciesTest extends TestCase {
   protected SpeciesEntityHandler tester;
   protected SpeciesEntityHandler origEntity;
   private ArrayList<JComponent> inputComponents;
   protected MainFrame mainFrame;
   private JComponent comp = new JTextField();
   
   protected void setUp(){
	   mainFrame = new MainFrame();
	   tester = new SpeciesEntityHandler(mainFrame);
	   origEntity = new SpeciesEntityHandler(mainFrame);
	   inputComponents = new ArrayList<>();
   }

   @Test
   public void testSpeciesAdded(){
	   Species specie = new Species();
	   specie.setName("test");
	   specie.setId(1L);
	   comp.putClientProperty(1, specie.getName());
	   
	   inputComponents.add(comp);
	   tester.newEntity(specie);
	   tester.createNewEntity();
	   
		for (JComponent comp1 : inputComponents) {
			comp1.putClientProperty(ClientPropertyName.ENTITY.toString(), tester);
			comp1.putClientProperty(ClientPropertyName.DAO.toString(), tester.createDAO());
		}
		
		BookModel model = mainFrame.getBookModel();
		Session session = model.beginTransaction();
		SpeciesDAOImpl speciesDao = new SpeciesDAOImpl(session);
		List<Species> species = speciesDao.findAll();
				
	   assertEquals(specie.getName(), species.get(0).getName());
   }
}