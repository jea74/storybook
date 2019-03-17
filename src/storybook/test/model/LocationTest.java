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
import storybook.model.handler.LocationEntityHandler;
import storybook.model.handler.SpeciesEntityHandler;
import storybook.model.hbn.dao.SpeciesDAOImpl;
import storybook.model.hbn.entity.Location;
import storybook.model.hbn.entity.Species;
import storybook.ui.MainFrame;

public class LocationTest extends TestCase {
   protected LocationEntityHandler tester;
   protected LocationEntityHandler origEntity;
   private ArrayList<JComponent> inputComponents;
   protected MainFrame mainFrame;
   private JComponent comp = new JTextField();

   protected void setUp(){
	   mainFrame = new MainFrame();
	   tester = new LocationEntityHandler(mainFrame);
	   origEntity = new LocationEntityHandler(mainFrame);
	   inputComponents = new ArrayList<>();
   }

   @Test
   public void testLocationAlreadyExists(){
   	Location location = new Location();
   	location.setName("test");
   	location.setAddress("test");
   	location.setCity("test");
   	location.setCountry("test");
   	comp.putClientProperty(1,location.getName());
   	comp.putClientProperty(2,location.getAddress());
   	comp.putClientProperty(3,location.getCity());
   	comp.putClientProperty(4,location.getCountry());

   	inputComponents.add(comp);
   	tester.newEntity(location);
   	tester.createNewEntity();

   	for(JComponent comp1 : inputComponents){
   		comp1.putClientProperty(ClientPropertyName.ENTITY.toString(),tester);
   		comp1.putClientProperty(ClientPropertyName.DAO.toString(),tester.createDAO());
   	}
   	assertFalse(tester.verifyNoDuplicates(inputComponents));

   	if(!tester.verifyNoDuplicates(inputComponents)){
   		return;
   	}

   }
}
