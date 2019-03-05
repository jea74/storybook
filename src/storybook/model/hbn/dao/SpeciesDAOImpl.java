package storybook.model.hbn.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import storybook.model.hbn.entity.Person;
import storybook.model.hbn.entity.Species;

public class SpeciesDAOImpl extends SbGenericDAOImpl<Species, Long> implements
	SpeciesDAO { // New SpeciesDAOImpl class
	
	public SpeciesDAOImpl() {
		super();
	}
	
	public SpeciesDAOImpl(Session session) {
		super(session);
	}
	
	public Species findTitle(String str) {
		String nstr=str.trim();
		List<Species> list = findAll();
		for (Species p:list) {
			if (p.getName().trim().equals(nstr)) return(p);
		}
		return(null);
	}
	
	public Species findHuman() {
		return (Species) session.get(Species.class, 1L);
	}
	
	@SuppressWarnings("unchecked")
	public List<Person> findPersons(Species species) {
		Criteria crit = session.createCriteria(Person.class);
		crit.add(Restrictions.eq("species", species));
		crit.addOrder(Order.asc("firstname"));
		crit.addOrder(Order.asc("lastname"));
		List<Person> persons = (List<Person>) crit.list();
		return persons;
	}
}
