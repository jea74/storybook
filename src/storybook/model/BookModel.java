/*
Storybook: Open Source software for novelists and authors.
Copyright (C) 2008 - 2012 Martin Mustun, 2015 FaVdB

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
package storybook.model;

import java.awt.Component;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;

import storybook.SbApp;
import storybook.SbConstants.ViewName;
import storybook.controller.BookController;
import storybook.model.hbn.dao.AttributeDAOImpl;
import storybook.model.hbn.dao.CategoryDAOImpl;
import storybook.model.hbn.dao.ChapterDAOImpl;
import storybook.model.hbn.dao.GenderDAOImpl;
import storybook.model.hbn.dao.IdeaDAOImpl;
import storybook.model.hbn.dao.InternalDAOImpl;
import storybook.model.hbn.dao.ItemDAOImpl;
import storybook.model.hbn.dao.ItemLinkDAOImpl;
import storybook.model.hbn.dao.LocationDAOImpl;
import storybook.model.hbn.dao.MemoDAOImpl;
import storybook.model.hbn.dao.PartDAOImpl;
import storybook.model.hbn.dao.PersonDAOImpl;
import storybook.model.hbn.dao.RelationshipDAOImpl;
import storybook.model.hbn.dao.SceneDAOImpl;
import storybook.model.hbn.dao.SpeciesDAOImpl;
import storybook.model.hbn.dao.StrandDAOImpl;
import storybook.model.hbn.dao.TagDAOImpl;
import storybook.model.hbn.dao.TagLinkDAOImpl;
import storybook.model.hbn.dao.TimeEventDAOImpl;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.Attribute;
import storybook.model.hbn.entity.Category;
import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Gender;
import storybook.model.hbn.entity.Idea;
import storybook.model.hbn.entity.Internal;
import storybook.model.hbn.entity.Item;
import storybook.model.hbn.entity.ItemLink;
import storybook.model.hbn.entity.Location;
import storybook.model.hbn.entity.Memo;
import storybook.model.hbn.entity.Part;
import storybook.model.hbn.entity.Person;
import storybook.model.hbn.entity.Relationship;
import storybook.model.hbn.entity.Scene;
import storybook.model.hbn.entity.Species;
import storybook.model.hbn.entity.Strand;
import storybook.model.hbn.entity.Tag;
import storybook.model.hbn.entity.TagLink;
import storybook.model.hbn.entity.TimeEvent;
import storybook.model.state.SceneState;
import storybook.i18n.I18N;
import storybook.toolkit.swing.ColorUtil;
import storybook.ui.MainFrame;
import storybook.ui.SbView;
import storybook.ui.panel.book.BookPanel;
import storybook.ui.panel.chrono.ChronoPanel;
import storybook.ui.panel.manage.ManagePanel;
import storybook.ui.panel.reading.ReadingPanel;
import storybook.ui.panel.storyboard.StoryboardPanel;

/**
 * @author martin
 *
 */
public class BookModel extends AbstractModel {

	public BookModel(MainFrame m) {
		super(m);
	}

	public synchronized void initEntites() {
		SbApp.trace("BookModel.initEntities()");
		Session session = beginTransaction();

		// default strand
		Strand strand = new Strand();
		strand.setName(I18N.getMsg("strand.name.init_value"));
		strand.setAbbreviation(I18N.getMsg("strand.abbr.init_value"));
		strand.setSort(1);
		strand.setJColor(ColorUtil.getNiceBlue());
		strand.setNotes("");
		session_save(session,strand);

		// default part
		Part part = new Part(1, I18N.getMsg("part.name.init_value"), "", null,
			new Timestamp(new Date().getTime()), null, null);
		session_save(session,part);

		// first chapter
		Chapter chapter = new Chapter();
		chapter.setPart(part);
		chapter.setChapterno(1);
		chapter.setTitle(I18N.getMsg("chapter") + " 1");
		chapter.setDescription("");
		chapter.setNotes("");
		chapter.setCreationTime(new Timestamp(new Date().getTime()));
		chapter.setObjectiveTime(null);
		chapter.setDoneTime(null);
		session_save(session,chapter);

		// first scene
		Scene scene = EntityUtil.createScene(strand, chapter);
		session_save(session,scene);

		// default genders
		Gender male = new Gender(I18N.getMsg("person.gender.male"), 12, 6, 47, 14);
		session_save(session,male);
		Gender female = new Gender(I18N.getMsg("person.gender.female"), 12, 6, 47, 14);
		session_save(session,female);

		// default categories
		Category major = new Category(1, I18N.getMsg("category.central_character"), null);
		session_save(session,major);
		Category minor = new Category(2, I18N.getMsg("category.minor_character"), null);
		session_save(session,minor);

		// default species - Human
		Species human = new Species(I18N.getMsg("person.species.human"));
		session_save(session,human);
		
		commit();
	}

	@Override
	public synchronized void initSession(String dbName) {
		SbApp.trace("BookModel.initSession(" + dbName + ")");
		try {
			super.initSession(dbName);
			Session session = beginTransaction();
			// test queries
			sessionFactory.query(new PartDAOImpl(session));
			sessionFactory.query(new ChapterDAOImpl(session));
			commit();
			SbApp.trace("test query OK");
		} catch (Exception e) {
			SbApp.trace("test query not OK");
		}
	}

	@Override
	public void fireAgain() {
		SbApp.trace("BookModel.fireAgain()");

		fireAgainCategories();
		fireAgainChapters();
		fireAgainGenders();
		fireAgainIdeas();
		fireAgainInternals();
		fireAgainItems();
		fireAgainItemLinks();
		fireAgainLocations();
		fireAgainParts();
		fireAgainPersons();
		fireAgainPlan();
		fireAgainRelationships();
		fireAgainScenes();
		fireAgainSpecies(); // New fireAgainSpecies() method
		fireAgainStrands();
		fireAgainTags();
		fireAgainTagLinks();
		fireAgainTimeEvent();
	}

	public void fireAgain(SbView view) {
		SbApp.trace("BookModel.fireAgain(" + view.getName() + ")");
		if (ViewName.CHRONO.compare(view)) {
			fireAgainScenes();
		} else if (ViewName.STORYBOARD.compare(view)) {
			fireAgainScenes();
		} else if (ViewName.BOOK.compare(view)) {
			fireAgainScenes();
		} else if (ViewName.READING.compare(view)) {
			fireAgainChapters();
		} else if (ViewName.MANAGE.compare(view)) {
			fireAgainChapters();
		} else if (ViewName.SCENES.compare(view)) {
			fireAgainScenes();
		} else if (ViewName.CHAPTERS.compare(view)) {
			fireAgainChapters();
		} else if (ViewName.PARTS.compare(view)) {
			fireAgainParts();
		} else if (ViewName.LOCATIONS.compare(view)) {
			fireAgainLocations();
		} else if (ViewName.PERSONS.compare(view)) {
			fireAgainPersons();
		} else if (ViewName.RELATIONSHIPS.compare(view)) {
			fireAgainRelationships();
		} else if (ViewName.GENDERS.compare(view)) {
			fireAgainGenders();
		} else if (ViewName.SPECIES.compare(view)) { // New else if statement for comparing Species views
			fireAgainSpecies();
		} else if (ViewName.CATEGORIES.compare(view)) {
			fireAgainCategories();
		} else if (ViewName.ATTRIBUTES.compare(view)) {
			fireAgainAttributes();
		} else if (ViewName.ATTRIBUTESLIST.compare(view)) {
			fireAgainAttributesList();
		} else if (ViewName.STRANDS.compare(view)) {
			fireAgainStrands();
		} else if (ViewName.IDEAS.compare(view)) {
			fireAgainIdeas();
		} else if (ViewName.MEMOS.compare(view)) {
			fireAgainMemos();
		} else if (ViewName.TAGS.compare(view)) {
			fireAgainTags();
		} else if (ViewName.ITEMS.compare(view)) {
			fireAgainItems();
		} else if (ViewName.TAGLINKS.compare(view)) {
			fireAgainTagLinks();
		} else if (ViewName.ITEMLINKS.compare(view)) {
			fireAgainItemLinks();
		} else if (ViewName.INTERNALS.compare(view)) {
			fireAgainInternals();
		} else if (ViewName.PLAN.compare(view)) {
			fireAgainPlan();
		} else if (ViewName.TIMEEVENT.compare(view)) {
			fireAgainTimeEvent();
		}
	}

	private void fireAgainScenes() {
		SbApp.trace("BookModel.fireAgainScenes()");
		Session session = beginTransaction();
		SceneDAOImpl dao = new SceneDAOImpl(session);
		List<Scene> scenes = dao.findAll();
		commit();
		firePropertyChange(BookController.SceneProps.INIT.toString(), null, scenes);
	}

	private void fireAgainChapters() {
		SbApp.trace("BookModel.fireAgainChapters()");
		Session session = beginTransaction();
		ChapterDAOImpl dao = new ChapterDAOImpl(session);
		List<Chapter> chapters = dao.findAll();
		commit();
		firePropertyChange(BookController.ChapterProps.INIT.toString(), null, chapters);
	}

	private void fireAgainParts() {
		SbApp.trace("BookModel.fireAgainParts()");
		Session session = beginTransaction();
		PartDAOImpl dao = new PartDAOImpl(session);
		List<Part> parts = dao.findAll();
		commit();
		firePropertyChange(BookController.PartProps.INIT.toString(), null, parts);
	}

	private void fireAgainLocations() {
		SbApp.trace("BookModel.fireAgainLocations()");
		Session session = beginTransaction();
		LocationDAOImpl dao = new LocationDAOImpl(session);
		List<Location> locations = dao.findAll();
		commit();
		firePropertyChange(BookController.LocationProps.INIT.toString(), null, locations);
	}

	private void fireAgainPersons() {
		SbApp.trace("BookModel.fireAgainPersons()");
		Session session = beginTransaction();
		PersonDAOImpl dao = new PersonDAOImpl(session);
		List<Person> persons = dao.findAll();
		commit();
		firePropertyChange(BookController.PersonProps.INIT.toString(), null, persons);
	}

	private void fireAgainRelationships() {
		SbApp.trace("BookModel.fireAgainRelationships()");
		Session session = beginTransaction();
		RelationshipDAOImpl dao = new RelationshipDAOImpl(session);
		List<Relationship> relationships = dao.findAll();
		commit();
		firePropertyChange(BookController.RelationshipProps.INIT.toString(), null, relationships);
	}

	private void fireAgainGenders() {
		SbApp.trace("BookModel.fireAgainGenders()");
		Session session = beginTransaction();
		GenderDAOImpl dao = new GenderDAOImpl(session);
		List<Gender> genders = dao.findAll();
		commit();
		firePropertyChange(BookController.GenderProps.INIT.toString(), null, genders);
	}

	private void fireAgainSpecies() { // New method for firing Species
		SbApp.trace("BookModel.fireAgainSpecies()");
		Session session = beginTransaction();
		SpeciesDAOImpl dao = new SpeciesDAOImpl(session);
		List<Species> species = dao.findAll();
		commit();
		firePropertyChange(BookController.SpeciesProps.INIT.toString(), null, species);
	}
	
	private void fireAgainCategories() {
		SbApp.trace("BookModel.fireAgainCategories()");
		Session session = beginTransaction();
		CategoryDAOImpl dao = new CategoryDAOImpl(session);
		List<Category> categories = dao.findAll();
		commit();
		firePropertyChange(BookController.CategoryProps.INIT.toString(), null, categories);
	}

	private void fireAgainAttributes() {
		SbApp.trace("BookModel.fireAgainAttributes()");
		Session session = beginTransaction();
		AttributeDAOImpl dao = new AttributeDAOImpl(session);
		List<Attribute> attributes = dao.findAll();
		commit();
		firePropertyChange(BookController.AttributeProps.INIT.toString(), null, attributes);
	}

	private void fireAgainAttributesList() {
		SbApp.trace("BookModel.fireAgainAttributesList()");
		Session session = beginTransaction();
		AttributeDAOImpl dao = new AttributeDAOImpl(session);
		List<Attribute> attributes = dao.findAll();
		commit();
		firePropertyChange(BookController.AttributeListProps.INIT.toString(), null, attributes);
	}

	private void fireAgainStrands() {
		SbApp.trace("BookModel.fireAgainStrands()");
		Session session = beginTransaction();
		StrandDAOImpl dao = new StrandDAOImpl(session);
		List<Strand> strands = dao.findAll();
		commit();
		firePropertyChange(BookController.StrandProps.INIT.toString(), null, strands);
	}

	private void fireAgainIdeas() {
		SbApp.trace("BookModel.fireAgainIdeas()");
		Session session = beginTransaction();
		IdeaDAOImpl dao = new IdeaDAOImpl(session);
		List<Idea> ideas = dao.findAll();
		commit();
		firePropertyChange(BookController.IdeaProps.INIT.toString(), null, ideas);
	}

	private void fireAgainMemos() {
		SbApp.trace("BookModel.fireAgainMemos()");
		Session session = beginTransaction();
		MemoDAOImpl dao = new MemoDAOImpl(session);
		List<Memo> memos = dao.findAll();
		commit();
		firePropertyChange(BookController.MemoProps.INIT.toString(), null, memos);
	}

	private void fireAgainTags() {
		SbApp.trace("BookModel.fireAgainTags()");
		Session session = beginTransaction();
		TagDAOImpl dao = new TagDAOImpl(session);
		List<Tag> tags = dao.findAll();
		commit();
		firePropertyChange(BookController.TagProps.INIT.toString(), null, tags);
	}

	private void fireAgainItems() {
		SbApp.trace("BookModel.fireAgainItems()");
		Session session = beginTransaction();
		ItemDAOImpl dao = new ItemDAOImpl(session);
		List<Item> items = dao.findAll();
		commit();
		firePropertyChange(BookController.ItemProps.INIT.toString(), null, items);
	}

	private void fireAgainTagLinks() {
		SbApp.trace("BookModel.fireAgainTagLinks()");
		Session session = beginTransaction();
		TagLinkDAOImpl dao = new TagLinkDAOImpl(session);
		List<TagLink> tagLinks = dao.findAll();
		commit();
		firePropertyChange(BookController.TagLinkProps.INIT.toString(), null, tagLinks);
	}

	private void fireAgainItemLinks() {
		SbApp.trace("BookModel.fireAgainItemLinks()");
		Session session = beginTransaction();
		ItemLinkDAOImpl dao = new ItemLinkDAOImpl(session);
		List<ItemLink> itemLinks = dao.findAll();
		commit();
		firePropertyChange(BookController.ItemLinkProps.INIT.toString(), null, itemLinks);
	}

	private void fireAgainInternals() {
		SbApp.trace("BookModel.fireAgainInternals()");
		Session session = beginTransaction();
		InternalDAOImpl dao = new InternalDAOImpl(session);
		List<Internal> internals = dao.findAll();
		commit();
		firePropertyChange(BookController.InternalProps.INIT.toString(), null, internals);
	}

	private void fireAgainPlan() {
	}

	private void fireAgainTimeEvent() {
		SbApp.trace("BookModel.fireAgainTimeEvent()");
		Session session = beginTransaction();
		TimeEventDAOImpl dao = new TimeEventDAOImpl(session);
		List<TimeEvent> internals = dao.findAll();
		commit();
		firePropertyChange(BookController.TimeEventProps.INIT.toString(), null, internals);
	}

	// common
	public void setRefresh(SbView view) {
		SbApp.trace("BookModel.setRefresh(" + view.getName() + ")");
		firePropertyChange(BookController.CommonProps.REFRESH.toString(), null, view);
		try {
			if (view.getComponentCount() == 0) {
				return;
			}
			Component comp = view.getComponent();
			if (comp instanceof ChronoPanel || comp instanceof BookPanel
				|| comp instanceof ManagePanel
				|| comp instanceof ReadingPanel
				|| comp instanceof StoryboardPanel) {
				// these views don't need a "fire again"
				return;
			}
			fireAgain(view);
		} catch (ArrayIndexOutOfBoundsException e) {
			// ignore
		}
	}

	public void setShowOptions(SbView view) {
		firePropertyChange(BookController.CommonProps.SHOW_OPTIONS.toString(), null, view);
	}

	public void setShowInfo(Scene scene) {
		setShowInfo((AbstractEntity) scene);
	}

	public void setShowInfo(Chapter chapter) {
		setShowInfo((AbstractEntity) chapter);
	}

	public void setShowInfo(Part part) {
		setShowInfo((AbstractEntity) part);
	}

	public void setShowInfo(Person person) {
		setShowInfo((AbstractEntity) person);
	}

	public void setShowInfo(Relationship entity) {
		setShowInfo((AbstractEntity) entity);
	}

	public void setShowInfo(Category category) {
		setShowInfo((AbstractEntity) category);
	}

	public void setShowInfo(Attribute attribute) {
		setShowInfo((AbstractEntity) attribute);
	}

	public void setShowInfo(Gender gender) {
		setShowInfo((AbstractEntity) gender);
	}

	public void setShowInfo(Species species) {
		setShowInfo((AbstractEntity) species);
	}
	
	public void setShowInfo(Location location) {
		setShowInfo((AbstractEntity) location);
	}

	public void setShowInfo(Tag tag) {
		setShowInfo((AbstractEntity) tag);
	}

	public void setShowInfo(TagLink tagLink) {
		setShowInfo((AbstractEntity) tagLink);
	}

	public void setShowInfo(Item item) {
		setShowInfo((AbstractEntity) item);
	}

	public void setShowInfo(ItemLink itemLink) {
		setShowInfo((AbstractEntity) itemLink);
	}

	public void setShowInfo(Strand strand) {
		setShowInfo((AbstractEntity) strand);
	}

	public void setShowInfo(Idea idea) {
		setShowInfo((AbstractEntity) idea);
	}

	public void setShowInfo(Memo memo) {
		setShowInfo((AbstractEntity) memo);
	}

	public void setShowInfo(AbstractEntity entity) {
		firePropertyChange(BookController.CommonProps.SHOW_INFO.toString(), null, entity);
	}

	public void setShowInfo(TimeEvent event) {
		setShowInfo((AbstractEntity) event);
	}

	public void setShowInfo(DbFile dbFile) {
		firePropertyChange(BookController.CommonProps.SHOW_INFO.toString(), null, dbFile);
	}

	public void setShowMemo(AbstractEntity entity) {
		firePropertyChange(BookController.CommonProps.SHOW_MEMO.toString(), null, entity);
	}

	public void setShowInMemoria(Person person) {
		setShowInMemoria((AbstractEntity) person);
	}

	public void setShowInMemoria(Relationship p) {
		setShowInMemoria((AbstractEntity) p);
	}

	public void setShowInMemoria(Location location) {
		setShowInMemoria((AbstractEntity) location);
	}

	public void setShowInMemoria(Scene scene) {
		setShowInMemoria((AbstractEntity) scene);
	}

	public void setShowInMemoria(Tag tag) {
		setShowInMemoria((AbstractEntity) tag);
	}

	public void setShowInMemoria(Item item) {
		setShowInMemoria((AbstractEntity) item);
	}

	public void setShowInMemoria(AbstractEntity entity) {
		firePropertyChange(BookController.CommonProps.SHOW_IN_MEMORIA.toString(), null, entity);
	}

	public void setUnloadEditor() {
		firePropertyChange(BookController.CommonProps.UNLOAD_EDITOR.toString(), null, null);
	}

	public void setFilterStatus(SceneState state) {
		firePropertyChange(BookController.SceneProps.FILTER_STATUS.toString(), null, state);
	}

	public void setFilterStrand(String strand) {
		firePropertyChange(BookController.SceneProps.FILTER_STRAND.toString(), null, strand);
	}

	public void setFilterNarrator(String person) {
		firePropertyChange(BookController.SceneProps.FILTER_NARRATOR.toString(), null, person);
	}

	public void setPrint(SbView view) {
		firePropertyChange(BookController.CommonProps.PRINT.toString(), null, view);
	}

	public void setExport(SbView view) {
		firePropertyChange(BookController.CommonProps.EXPORT.toString(), null, view);
	}

	// chrono view
	public void setChronoZoom(Integer val) {
		firePropertyChange(BookController.ChronoViewProps.ZOOM.toString(), null, val);
	}

	public void setChronoLayoutDirection(Boolean val) {
		firePropertyChange(BookController.ChronoViewProps.LAYOUT_DIRECTION.toString(), null, val);
	}

	public void setChronoShowDateDifference(Boolean val) {
		firePropertyChange(BookController.ChronoViewProps.SHOW_DATE_DIFFERENCE.toString(), null, val);
	}

	public void setChronoShowEntity(Scene scene) {
		firePropertyChange(BookController.ChronoViewProps.SHOW_ENTITY.toString(), null, scene);
	}

	public void setChronoShowEntity(Chapter chapter) {
		firePropertyChange(BookController.ChronoViewProps.SHOW_ENTITY.toString(), null, chapter);
	}

	// book view
	public void setBookZoom(Integer val) {
		firePropertyChange(BookController.BookViewProps.ZOOM.toString(), null, val);
	}

	public void setBookHeightFactor(Integer val) {
		firePropertyChange(BookController.BookViewProps.HEIGHT_FACTOR.toString(), null, val);
	}

	public void setBookShowEntity(Scene scene) {
		firePropertyChange(BookController.BookViewProps.SHOW_ENTITY.toString(), null, scene);
	}

	public void setBookShowEntity(Chapter chapter) {
		firePropertyChange(BookController.BookViewProps.SHOW_ENTITY.toString(), null, chapter);
	}

	// manage view
	public void setManageZoom(Integer val) {
		firePropertyChange(BookController.ManageViewProps.ZOOM.toString(), null, val);
	}

	public void setManageColumns(Integer val) {
		firePropertyChange(BookController.ManageViewProps.COLUMNS.toString(), null, val);
	}

	public void setManageHideUnassigned(Boolean val) {
		firePropertyChange(BookController.ManageViewProps.HIDE_UNASSIGNED.toString(), null, val);
	}

	public void setManageShowEntity(Scene scene) {
		firePropertyChange(BookController.ManageViewProps.SHOW_ENTITY.toString(), null, scene);
	}

	public void setManageShowEntity(Chapter chapter) {
		firePropertyChange(BookController.ManageViewProps.SHOW_ENTITY.toString(), null, chapter);
	}

	// reading view
	public void setReadingZoom(Integer val) {
		firePropertyChange(BookController.ReadingViewProps.ZOOM.toString(), null, val);
	}

	public void setReadingFontSize(Integer val) {
		firePropertyChange(BookController.ReadingViewProps.FONT_SIZE.toString(), null, val);
	}

	// memo view
	public void setMemoLayout(Boolean val) {
		firePropertyChange(BookController.MemoProps.LAYOUT_DIRECTION.toString(), null, val);
	}

	// memoria view
	public void setMemoriaBalloon(Boolean val) {
		firePropertyChange(BookController.MemoriaViewProps.BALLOON.toString(), null, val);
	}

	// chapter
	public void setEditChapter(Chapter entity) {
		//firePropertyChange(BookController.ChapterProps.EDIT.toString(), null, entity);
		editEntity((AbstractEntity) entity);
	}

	public synchronized void setUpdateChapter(Chapter entity) {
		Session session = beginTransaction();
		ChapterDAOImpl dao = new ChapterDAOImpl(session);
		Chapter old = dao.find(entity.getId());
		commit();
		session = beginTransaction();
		session_update(session,entity);
		commit();
		mainFrame.setUpdated(true);
		firePropertyChange(BookController.ChapterProps.UPDATE.toString(), old, entity);
	}

	public synchronized void setNewChapter(Chapter entity) {
		Session session = beginTransaction();
		session_save(session,entity);
		commit();
		firePropertyChange(BookController.ChapterProps.NEW.toString(), null, entity);
	}

	public synchronized void setDeleteChapter(Chapter entity) {
		if (entity.getId() == null) {
			return;
		}
		Session session = beginTransaction();
		// find scenes, set chapter to null
		ChapterDAOImpl dao = new ChapterDAOImpl(session);
		List<Scene> scenes = dao.findScenes(entity);
		commit();
		for (Scene scene : scenes) {
			scene.setChapter();
			setUpdateScene(scene);
		}
		// delete chapter
		session = beginTransaction();
		session.delete(entity);
		commit();
		firePropertyChange(BookController.ChapterProps.DELETE.toString(), entity, null);
	}

	public synchronized void setDeleteMultiChapters(ArrayList<Long> ids) {
		for (Long id : ids) {
			Session session = beginTransaction();
			ChapterDAOImpl dao = new ChapterDAOImpl(session);
			Chapter old = dao.find(id);
			commit();
			session = beginTransaction();
			dao = new ChapterDAOImpl(session);
			dao.removeById(id);
			commit();
			firePropertyChange(BookController.ChapterProps.DELETE.toString(), old, null);
		}
	}

	// part
	public void setEditPart(Part entity) {
		//firePropertyChange(BookController.PartProps.EDIT.toString(), null, entity);
		editEntity((AbstractEntity) entity);
	}

	public synchronized void setUpdatePart(Part entity) {
		Session session = beginTransaction();
		PartDAOImpl dao = new PartDAOImpl(session);
		Part old = dao.find(entity.getId());
		commit();
		session = beginTransaction();
		session_update(session,entity);
		commit();
		mainFrame.setUpdated(true);
		firePropertyChange(BookController.PartProps.UPDATE.toString(), old, entity);
	}

	public synchronized void setNewPart(Part entity) {
		Session session = beginTransaction();
		session_save(session,entity);
		commit();
		firePropertyChange(BookController.PartProps.NEW.toString(), null, entity);
	}

	public synchronized void setDeletePart(Part entity) {
		if (entity.getId() == null) {
			return;
		}
		Session session = beginTransaction();
		// delete chapters
		PartDAOImpl dao = new PartDAOImpl(session);
		List<Chapter> chapters = dao.findChapters(entity);
		commit();
		for (Chapter chapter : chapters) {
			setDeleteChapter(chapter);
		}
		// delete part
		session = beginTransaction();
		session.delete(entity);
		commit();
		firePropertyChange(BookController.PartProps.DELETE.toString(), entity, null);
	}

	public synchronized void setDeleteMultiParts(ArrayList<Long> ids) {
		for (Long id : ids) {
			Session session = beginTransaction();
			PartDAOImpl dao = new PartDAOImpl(session);
			Part old = dao.find(id);
			commit();
			setDeletePart(old);
		}
	}

	public synchronized void setChangePart(Part entity) {
		firePropertyChange(BookController.PartProps.CHANGE.toString(), null, entity);
	}

	// location
	public void setEditLocation(Location entity) {
		//firePropertyChange(BookController.LocationProps.EDIT.toString(), null, entity);
		editEntity((AbstractEntity) entity);
	}

	public synchronized void setUpdateLocation(Location entity) {
		Session session = beginTransaction();
		LocationDAOImpl dao = new LocationDAOImpl(session);
		Location old = dao.find(entity.getId());
		commit();
		session = beginTransaction();
		session_update(session,entity);
		commit();
		mainFrame.setUpdated(true);
		firePropertyChange(BookController.LocationProps.UPDATE.toString(), old, entity);
	}

	public synchronized void setNewLocation(Location entity) {
		Session session = beginTransaction();
		session_save(session,entity);
		commit();
		firePropertyChange(BookController.LocationProps.NEW.toString(), null, entity);
	}

	public synchronized void setDeleteLocation(Location entity) {
		if (entity.getId() == null) {
			return;
		}
		try {
			// delete scene links
			Session session = beginTransaction();
			SceneDAOImpl dao = new SceneDAOImpl(session);
			List<Scene> scenes = dao.findByLocationLink(entity);
			for (Scene scene : scenes) {
				scene.getLocations().remove(entity);
				session_update(session,scene);
			}
			commit();
			for (Scene scene : scenes) {
				setUpdateScene(scene);
			}
			// delete tag / item links
			EntityUtil.deleteTagAndItemLinks(this, entity);
			// delete relationship
			session = beginTransaction();
			RelationshipDAOImpl daoR = new RelationshipDAOImpl(session);
			List<Relationship> relations = daoR.findByLocationLink(entity);
			commit();
			for (Relationship relation : relations) {
				relation.getLocations().remove(entity);
				session_update(session,relation);
			}
			// delete location
			session = beginTransaction();
			session.delete(entity);
			commit();
		} catch (ConstraintViolationException e) {
			SbApp.error("BookModel.setDeleteLocation(" + entity.getName() + ")", e);
		}
		firePropertyChange(BookController.LocationProps.DELETE.toString(), entity, null);
	}

	public synchronized void setDeleteMultiLocations(ArrayList<Long> ids) {
		for (Long id : ids) {
			Session session = beginTransaction();
			LocationDAOImpl dao = new LocationDAOImpl(session);
			Location old = dao.find(id);
			commit();
			setDeleteLocation(old);
		}
	}

	// person
	public void setEditPerson(Person entity) {
		//firePropertyChange(BookController.PersonProps.EDIT.toString(),null, entity);
		editEntity((AbstractEntity) entity);
	}

	public synchronized void setUpdatePerson(Person entity) {
		Session session = beginTransaction();
		PersonDAOImpl dao = new PersonDAOImpl(session);
		Person old = dao.find(entity.getId());
		commit();
		session = beginTransaction();
		session_update(session,entity);
		commit();
		mainFrame.setUpdated(true);
		firePropertyChange(BookController.PersonProps.UPDATE.toString(), old, entity);
	}

	public synchronized void setNewPerson(Person entity) {
		try {
			Session session = beginTransaction();
			
			session_save(session,entity);
			commit();
			firePropertyChange(BookController.PersonProps.NEW.toString(), null, entity);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public synchronized void setDeletePerson(Person entity) {
		if (entity.getId() == null) {
			return;
		}
		try {
			// delete scene links
			Session session = beginTransaction();
			SceneDAOImpl dao = new SceneDAOImpl(session);
			List<Scene> scenes = dao.findByPersonLink(entity);
			for (Scene scene : scenes) {
				scene.getPersons().remove(entity);
				session_update(session,scene);
			}
			commit();
			for (Scene scene : scenes) {
				setUpdateScene(scene);
			}
			// delete tag / item links
			EntityUtil.deleteTagAndItemLinks(this, entity);
			// delete relationship
			session = beginTransaction();
			RelationshipDAOImpl daoR = new RelationshipDAOImpl(session);
			List<Relationship> relations = daoR.findByPersonLink(entity);
			commit();
			for (Relationship relation : relations) {
				relation.getPersons().remove(entity);
				session_update(session,relation);
			}
			// delete person
			session = beginTransaction();
			session.delete(entity);
			commit();
		} catch (ConstraintViolationException e) {
			SbApp.error("BookModel.setDeletePerson(" + entity.getFullName() + ")", e);
		}
		firePropertyChange(BookController.PersonProps.DELETE.toString(), entity, null);
	}

	public synchronized void setDeleteMultiPersons(ArrayList<Long> ids) {
		for (Long id : ids) {
			Session session = beginTransaction();
			PersonDAOImpl dao = new PersonDAOImpl(session);
			Person old = dao.find(id);
			commit();
			setDeletePerson(old);
		}
	}

	// relationship
	public void setEditRelationship(Relationship entity) {
		//firePropertyChange(BookController.RelationshipProps.EDIT.toString(),null, entity);
		editEntity((AbstractEntity) entity);
	}

	public synchronized void setUpdateRelationship(Relationship entity) {
		Session session = beginTransaction();
		RelationshipDAOImpl dao = new RelationshipDAOImpl(session);
		Relationship old = dao.find(entity.getId());
		commit();
		session = beginTransaction();
		session_update(session,entity);
		commit();
		mainFrame.setUpdated(true);
		firePropertyChange(BookController.RelationshipProps.UPDATE.toString(), old, entity);
	}

	public synchronized void setNewRelationship(Relationship entity) {
		Session session = beginTransaction();
		session_save(session,entity);
		commit();
		firePropertyChange(BookController.RelationshipProps.NEW.toString(), null, entity);
	}

	public synchronized void setDeleteRelationship(Relationship entity) {
		if (entity.getId() == null) {
			return;
		}
		try {
			// delete scene links
			// delete Relationship
			Session session = beginTransaction();
			session.delete(entity);
			commit();
		} catch (ConstraintViolationException e) {
			SbApp.error("BookModel.setDeleteRelationship(" + entity.getPerson1() + "-" + entity.getPerson2() + ")", e);
		}
		firePropertyChange(BookController.RelationshipProps.DELETE.toString(), entity, null);
	}

	public synchronized void setDeleteMultiRelationships(ArrayList<Long> ids) {
		for (Long id : ids) {
			Session session = beginTransaction();
			RelationshipDAOImpl dao = new RelationshipDAOImpl(session);
			Relationship old = dao.find(id);
			commit();
			setDeleteRelationship(old);
		}
	}

	// gender
	public void setEditGender(Gender entity) {
		//firePropertyChange(BookController.GenderProps.EDIT.toString(), null, entity);
		editEntity((AbstractEntity) entity);
	}

	public synchronized void setUpdateGender(Gender entity) {
		Session session = beginTransaction();
		GenderDAOImpl dao = new GenderDAOImpl(session);
		Gender old = dao.find(entity.getId());
		commit();
		session = beginTransaction();
		session_update(session,entity);
		commit();
		mainFrame.setUpdated(true);
		firePropertyChange(BookController.GenderProps.UPDATE.toString(), old, entity);
	}

	public synchronized void setNewGender(Gender entity) {
		Session session = beginTransaction();
		session_save(session,entity);
		commit();
		firePropertyChange(BookController.GenderProps.NEW.toString(), null, entity);
	}

	public synchronized void setDeleteGender(Gender entity) {
		if (entity.getId() == null) {
			return;
		}
		// set gender of affected persons to "male"
		Session session = beginTransaction();
		GenderDAOImpl dao = new GenderDAOImpl(session);
		Gender male = dao.findMale();
		List<Person> persons = dao.findPersons(entity);
		commit();
		for (Person person : persons) {
			person.setGender(male);
			setUpdatePerson(person);
		}
		// delete gender
		session = beginTransaction();
		session.delete(entity);
		commit();

		firePropertyChange(BookController.GenderProps.DELETE.toString(), entity, null);
	}

	public synchronized void setDeleteMultiGenders(ArrayList<Long> ids) {
		for (Long id : ids) {
			Session session = beginTransaction();
			GenderDAOImpl dao = new GenderDAOImpl(session);
			Gender old = dao.find(id);
			commit();
			session = beginTransaction();
			dao = new GenderDAOImpl(session);
			dao.removeById(id);
			commit();
			firePropertyChange(BookController.GenderProps.DELETE.toString(), old, null);
		}
	}

	// attributes
	public void setEditAttribute(Attribute entity) {
		editEntity((AbstractEntity) entity);
	}

	public synchronized void setUpdateAttribute(Attribute entity) {
		Session session = beginTransaction();
		AttributeDAOImpl dao = new AttributeDAOImpl(session);
		Attribute old = dao.find(entity.getId());
		commit();
		session = beginTransaction();
		session_update(session,entity);
		commit();
		mainFrame.setUpdated(true);
		firePropertyChange(BookController.AttributeProps.UPDATE.toString(), old, entity);
	}

	public synchronized void setNewAttribute(Attribute entity) {
		Session session = beginTransaction();
		session_save(session,entity);
		commit();
		firePropertyChange(BookController.AttributeProps.NEW.toString(), null, entity);
	}

	public synchronized void setDeleteAttribute(Attribute entity) {
		if (entity.getId() == null) {
			return;
		}
		Attribute attribute = (Attribute) entity;
		// set entity of affected persons to "male"
		Session session = beginTransaction();
		AttributeDAOImpl dao = new AttributeDAOImpl(session);
		// delete this attribute from all persons
		PersonDAOImpl daoPerson = new PersonDAOImpl(session);
		List<Person> persons = daoPerson.findAll();
		for (Person person : persons) {
			if (!person.getAttributes().isEmpty()) {
				List<Attribute> lattr = person.getAttributes();
				for (Attribute attr : lattr) {
					if (attr.equals(attribute)) {
						lattr.remove(attribute);
						person.setAttributes(lattr);
						session_update(session,person);
						SbApp.trace("attribute remove for=" + person.getFullName());
						break;
					}
				}
			}
		}
		commit();
		SbApp.trace("clean attribute OK");
		// delete entity
		session = beginTransaction();
		session.delete(entity);
		commit();
		firePropertyChange(BookController.AttributeProps.DELETE.toString(), entity, null);
	}

	public synchronized void setDeleteMultiAttributes(ArrayList<Long> ids) {
		Session session = beginTransaction();
		AttributeDAOImpl dao = new AttributeDAOImpl(session);
		for (Long id : ids) {
			Attribute old = dao.find(id);
			commit();
			setDeleteAttribute(old);
			firePropertyChange(BookController.AttributeProps.DELETE.toString(), old, null);
		}
	}
	
	// species - New methods to edit, update, create, and delete a Species entity
	public void setEditSpecies(Species entity) {
		editEntity((AbstractEntity) entity);
	}
	
	public synchronized void setUpdateSpecies(Species entity) {
		Session session = beginTransaction();
		SpeciesDAOImpl dao = new SpeciesDAOImpl(session);
		Species old = dao.find(entity.getId());
		commit();
		session = beginTransaction();
		session_update(session,entity);
		commit();
		mainFrame.setUpdated(true);
		firePropertyChange(BookController.SpeciesProps.UPDATE.toString(), old, entity);
	}
	
	public synchronized void setNewSpecies(Species entity) {
		Session session = beginTransaction();
		session_save(session,entity);
		commit();
		firePropertyChange(BookController.SpeciesProps.NEW.toString(), null, entity);
	}

	public synchronized void setDeleteSpecies(Species entity) {
		if (entity.getId() == null) {
			return;
		}
		// set species of affected persons to "human"
		Session session = beginTransaction();
		SpeciesDAOImpl dao = new SpeciesDAOImpl(session);
		Species human = dao.findHuman();
		List<Person> persons = dao.findPersons(entity);
		commit();
		for (Person person : persons) {
			person.setSpecies(human);
			setUpdatePerson(person);
		}
		// delete species
		session = beginTransaction();
		session.delete(entity);
		commit();
		firePropertyChange(BookController.SpeciesProps.DELETE.toString(), entity, null);
	}
	
	public synchronized void setDeleteMultiSpecies(ArrayList<Long> ids) {
		for (Long id : ids) {
			Session session = beginTransaction();
			SpeciesDAOImpl dao = new SpeciesDAOImpl(session);
			Species old = dao.find(id);
			commit();
			session = beginTransaction();
			dao = new SpeciesDAOImpl(session);
			dao.removeById(id);
			commit();
			firePropertyChange(BookController.SpeciesProps.DELETE.toString(), old, null);
		}
	}
	
	// category
	public void setEditCategory(Category entity) {
		editEntity((AbstractEntity) entity);
	}

	public synchronized void setUpdateCategory(Category entity) {
		Session session = beginTransaction();
		CategoryDAOImpl dao = new CategoryDAOImpl(session);
		Category old = dao.find(entity.getId());
		commit();
		session = beginTransaction();
		session_update(session,entity);
		commit();
		mainFrame.setUpdated(true);
		firePropertyChange(BookController.CategoryProps.UPDATE.toString(), old, entity);
	}

	public synchronized void setNewCategory(Category entity) {
		Session session = beginTransaction();
		session_save(session,entity);
		commit();
		firePropertyChange(BookController.CategoryProps.NEW.toString(), null, entity);
	}

	public synchronized void setDeleteCategory(Category category) {
		if (category.getId() == null) {
			return;
		}
		// set category of affected persons to "minor"
		Session session = beginTransaction();
		CategoryDAOImpl dao = new CategoryDAOImpl(session);
		Category minor = dao.findMinor();
		List<Person> persons = dao.findPersons(category);
		commit();
		for (Person person : persons) {
			person.setCategory(minor);
			setUpdatePerson(person);
		}
		// delete category
		session = beginTransaction();
		session.delete(category);
		commit();
		firePropertyChange(BookController.CategoryProps.DELETE.toString(), category, null);
	}

	public synchronized void setDeleteMultiCategories(ArrayList<Long> ids) {
		for (Long id : ids) {
			Session session = beginTransaction();
			CategoryDAOImpl dao = new CategoryDAOImpl(session);
			Category old = dao.find(id);
			commit();
			session = beginTransaction();
			dao = new CategoryDAOImpl(session);
			dao.removeById(id);
			commit();
			firePropertyChange(BookController.CategoryProps.DELETE.toString(), old, null);
		}
	}

	public synchronized void setOrderUpCategory(Category category) {
		firePropertyChange(BookController.CategoryProps.ORDER_UP.toString(), null, category);
	}

	public synchronized void setOrderDownCategory(Category category) {
		firePropertyChange(BookController.CategoryProps.ORDER_DOWN.toString(), null, category);
	}

	// strand
	public void setEditStrand(Strand entity) {
		editEntity((AbstractEntity) entity);
	}

	public synchronized void setUpdateStrand(Strand entity) {
		Session session = beginTransaction();
		StrandDAOImpl dao = new StrandDAOImpl(session);
		Strand old = dao.find(entity.getId());
		commit();
		session = beginTransaction();
		session_update(session,entity);
		commit();
		mainFrame.setUpdated(true);
		firePropertyChange(BookController.StrandProps.UPDATE.toString(), old, entity);
	}

	public synchronized void setNewStrand(Strand entity) {
		Session session = beginTransaction();
		session_save(session,entity);
		commit();
		firePropertyChange(BookController.StrandProps.NEW.toString(), null, entity);
	}

	public synchronized void setDeleteStrand(Strand entity) {
		if (entity.getId() == null) {
			return;
		}
		try {
			// delete scene links
			Session session = beginTransaction();
			SceneDAOImpl sceneDao = new SceneDAOImpl(session);
			List<Scene> scenes = sceneDao.findByStrandLink(entity);
			for (Scene scene : scenes) {
				scene.getStrands().remove(entity);
				session_update(session,scene);
			}
			commit();
			for (Scene scene : scenes) {
				setUpdateScene(scene);
			}
			// delete scenes
			session = beginTransaction();
			StrandDAOImpl strandDao = new StrandDAOImpl(session);
			scenes = strandDao.findScenes(entity);
			commit();
			for (Scene scene : scenes) {
				setDeleteScene(scene);
			}
			// delete strand
			session = beginTransaction();
			session.delete(entity);
			commit();
		} catch (ConstraintViolationException e) {
			SbApp.error("BookModel.setDeleteStrand(" + entity.getName() + ")", e);
		}
		firePropertyChange(BookController.StrandProps.DELETE.toString(), entity, null);
	}

	public synchronized void setDeleteMultiStrands(ArrayList<Long> ids) {
		for (Long id : ids) {
			Session session = beginTransaction();
			StrandDAOImpl dao = new StrandDAOImpl(session);
			Strand old = dao.find(id);
			commit();
			setDeleteStrand(old);
		}
	}

	public synchronized void setOrderUpStrand(Strand strand) {
		firePropertyChange(BookController.StrandProps.ORDER_UP.toString(), null, strand);
	}

	public synchronized void setOrderDownStrand(Strand strand) {
		firePropertyChange(BookController.StrandProps.ORDER_DOWN.toString(), null, strand);
	}

	// idea
	public void setEditIdea(Idea entity) {
		editEntity((AbstractEntity) entity);
	}

	public synchronized void setUpdateIdea(Idea entity) {
		Session session = beginTransaction();
		IdeaDAOImpl dao = new IdeaDAOImpl(session);
		Idea old = dao.find(entity.getId());
		commit();
		session = beginTransaction();
		session_update(session,entity);
		commit();
		mainFrame.setUpdated(true);
		firePropertyChange(BookController.IdeaProps.UPDATE.toString(), old, entity);
	}

	public synchronized void setNewIdea(Idea entity) {
		Session session = beginTransaction();
		session_save(session,entity);
		commit();
		firePropertyChange(BookController.IdeaProps.NEW.toString(), null, entity);
	}

	public synchronized void setDeleteIdea(Idea entity) {
		if (entity.getId() == null) {
			return;
		}
		Session session = beginTransaction();
		session.delete(entity);
		commit();
		firePropertyChange(BookController.IdeaProps.DELETE.toString(), entity, null);
	}

	public synchronized void setDeleteMultiIdeas(ArrayList<Long> ids) {
		for (Long id : ids) {
			Session session = beginTransaction();
			IdeaDAOImpl dao = new IdeaDAOImpl(session);
			Idea old = dao.find(id);
			commit();
			session = beginTransaction();
			dao = new IdeaDAOImpl(session);
			dao.removeById(id);
			commit();
			firePropertyChange(BookController.IdeaProps.DELETE.toString(), old, null);
		}
	}

	// tags
	public void setEditTag(Tag entity) {
		editEntity((AbstractEntity) entity);
	}

	public synchronized void setUpdateTag(Tag entity) {
		Session session = beginTransaction();
		TagDAOImpl dao = new TagDAOImpl(session);
		Tag old = dao.find(entity.getId());
		commit();
		session = beginTransaction();
		session_update(session,entity);
		commit();
		mainFrame.setUpdated(true);
		firePropertyChange(BookController.TagProps.UPDATE.toString(), old, entity);
	}

	public synchronized void setNewTag(Tag entity) {
		Session session = beginTransaction();
		session_save(session,entity);
		commit();
		firePropertyChange(BookController.TagProps.NEW.toString(), null, entity);
	}

	public synchronized void setDeleteTag(Tag entity) {
		if (entity.getId() == null) {
			return;
		}
		// delete tag assignments
		Session session = beginTransaction();
		TagLinkDAOImpl dao = new TagLinkDAOImpl(session);
		List<TagLink> links = dao.findByTag(entity);
		commit();
		for (TagLink link : links) {
			setDeleteTagLink(link);
		}
		// delete tag
		session = beginTransaction();
		session.delete(entity);
		commit();
		firePropertyChange(BookController.TagProps.DELETE.toString(), entity, null);
	}

	public synchronized void setDeleteMultiTags(ArrayList<Long> ids) {
		for (Long id : ids) {
			Session session = beginTransaction();
			TagDAOImpl dao = new TagDAOImpl(session);
			Tag old = dao.find(id);
			commit();
			setDeleteTag(old);
		}
	}

	// memos
	public void setEditMemo(Memo entity) {
		editEntity((AbstractEntity) entity);
	}

	public synchronized void setUpdateMemo(Memo entity) {
		Session session = beginTransaction();
		MemoDAOImpl dao = new MemoDAOImpl(session);
		Memo old = dao.find(entity.getId());
		commit();
		session = beginTransaction();
		session_update(session,entity);
		commit();
		mainFrame.setUpdated(true);
		firePropertyChange(BookController.MemoProps.UPDATE.toString(), old, entity);
	}

	public synchronized void setNewMemo(Memo entity) {
		Session session = beginTransaction();
		session_save(session,entity);
		commit();
		firePropertyChange(BookController.MemoProps.NEW.toString(), null, entity);
	}

	public synchronized void setDeleteMemo(Memo entity) {
		if (entity.getId() == null) {
			return;
		}
		// delete memo
		Session session = beginTransaction();
		session.delete(entity);
		commit();
		firePropertyChange(BookController.MemoProps.DELETE.toString(), entity, null);
	}

	public synchronized void setDeleteMultiMemos(ArrayList<Long> ids) {
		for (Long id : ids) {
			Session session = beginTransaction();
			MemoDAOImpl dao = new MemoDAOImpl(session);
			Memo old = dao.find(id);
			commit();
			setDeleteMemo(old);
		}
	}

	// items
	public void setEditItem(Item entity) {
		editEntity((AbstractEntity) entity);
	}

	public synchronized void setUpdateItem(Item entity) {
		Session session = beginTransaction();
		ItemDAOImpl dao = new ItemDAOImpl(session);
		Item old = dao.find(entity.getId());
		commit();
		session = beginTransaction();
		session_update(session,entity);
		commit();
		mainFrame.setUpdated(true);
		firePropertyChange(BookController.ItemProps.UPDATE.toString(), old, entity);
	}

	public synchronized void setNewItem(Item entity) {
		Session session = beginTransaction();
		session_save(session,entity);
		commit();
		firePropertyChange(BookController.ItemProps.NEW.toString(), null, entity);
	}

	public synchronized void setDeleteItem(Item entity) {
		if (entity.getId() == null) {
			return;
		}
		// delete item assignments
		Session session = beginTransaction();
		ItemLinkDAOImpl dao = new ItemLinkDAOImpl(session);
		List<ItemLink> links = dao.findByItem(entity);
		commit();
		for (ItemLink link : links) {
			setDeleteItemLink(link);
		}
		// delete relationship
		session = beginTransaction();
		RelationshipDAOImpl daoR = new RelationshipDAOImpl(session);
		List<Relationship> relations = daoR.findByItemLink(entity);
		commit();
		for (Relationship relation : relations) {
			relation.getItems().remove(entity);
			session_update(session,relation);
		}
		// delete item
		session = beginTransaction();
		session.delete(entity);
		commit();
		firePropertyChange(BookController.ItemProps.DELETE.toString(), entity, null);
	}

	public synchronized void setDeleteMultiItems(ArrayList<Long> ids) {
		for (Long id : ids) {
			Session session = beginTransaction();
			ItemDAOImpl dao = new ItemDAOImpl(session);
			Item old = dao.find(id);
			commit();
			setDeleteItem(old);
		}
	}

	// tag links
	public void setEditTagLink(TagLink entity) {
		editEntity((AbstractEntity) entity);
	}

	public synchronized void setUpdateTagLink(TagLink entity) {
		Session session = beginTransaction();
		TagLinkDAOImpl dao = new TagLinkDAOImpl(session);
		TagLink old = dao.find(entity.getId());
		commit();
		session = beginTransaction();
		session_update(session,entity);
		commit();
		mainFrame.setUpdated(true);
		firePropertyChange(BookController.TagLinkProps.UPDATE.toString(), old, entity);
	}

	public synchronized void setNewTagLink(TagLink entity) {
		Session session = beginTransaction();
		session_save(session,entity);
		commit();
		firePropertyChange(BookController.TagLinkProps.NEW.toString(), null, entity);
	}

	public synchronized void setDeleteTagLink(TagLink entity) {
		if (entity.getId() == null) {
			return;
		}
		Session session = beginTransaction();
		session.delete(entity);
		commit();
		firePropertyChange(BookController.TagLinkProps.DELETE.toString(), entity, null);
	}

	public synchronized void setDeleteMultiTagLinks(ArrayList<Long> ids) {
		for (Long id : ids) {
			Session session = beginTransaction();
			TagLinkDAOImpl dao = new TagLinkDAOImpl(session);
			TagLink old = dao.find(id);
			commit();
			setDeleteTagLink(old);
		}
	}

	// item links
	public void setEditItemLink(ItemLink entity) {
		editEntity((AbstractEntity) entity);
	}

	public synchronized void setUpdateItemLink(ItemLink entity) {
		Session session = beginTransaction();
		ItemLinkDAOImpl dao = new ItemLinkDAOImpl(session);
		ItemLink old = dao.find(entity.getId());
		commit();
		session = beginTransaction();
		session_update(session,entity);
		commit();
		mainFrame.setUpdated(true);
		firePropertyChange(BookController.ItemLinkProps.UPDATE.toString(), old, entity);
	}

	public synchronized void setNewItemLink(ItemLink entity) {
		Session session = beginTransaction();
		session_save(session,entity);
		commit();
		firePropertyChange(BookController.ItemLinkProps.NEW.toString(), null, entity);
	}

	public synchronized void setDeleteItemLink(ItemLink entity) {
		if (entity.getId() == null) {
			return;
		}
		Session session = beginTransaction();
		session.delete(entity);
		commit();
		firePropertyChange(BookController.ItemLinkProps.DELETE.toString(), entity, null);
	}

	public synchronized void setDeleteMultiItemLinks(ArrayList<Long> ids) {
		for (Long id : ids) {
			Session session = beginTransaction();
			ItemLinkDAOImpl dao = new ItemLinkDAOImpl(session);
			ItemLink old = dao.find(id);
			commit();
			setDeleteItemLink(old);
		}
	}

	// scenes
	public void setEditScene(Scene entity) {
		editEntity((AbstractEntity) entity);
	}

	public synchronized void setUpdateScene(Scene entity) {
		// needed, see ChronoPanel.modelPropertyChange()
		Session session = beginTransaction();
		Scene old = (Scene) session.get(Scene.class, entity.getId());
		commit();
		try {
			session = beginTransaction();
			
			session_update(session,entity);
			commit();
			mainFrame.setUpdated(true);
		} catch (ConstraintViolationException e) {
			SbApp.error("BookModel.setUpdateScene(" + entity.getTitle() + ")", e);
		}
		firePropertyChange(BookController.SceneProps.UPDATE.toString(), old, entity);
	}

	public synchronized void setNewScene(Scene entity) {
		Session session = beginTransaction();
		session_save(session,entity);
		commit();
		firePropertyChange(BookController.SceneProps.NEW.toString(), null, entity);
	}

	public synchronized void setDeleteScene(Scene entity) {
		if (entity.getId() == null) {
			return;
		}
		// delete tag / item links
		EntityUtil.deleteTagAndItemLinks(this, entity);
		// remove relative scene of affected scenes
		Session session = beginTransaction();
		SceneDAOImpl dao = new SceneDAOImpl(session);
		List<Scene> scenes = dao.findScenesWithRelativeSceneId(entity);
		commit();
		for (Scene scene2 : scenes) {
			scene2.removeRelativeScene();
			setUpdateScene(scene2);
		}
		// delete scene
		session = beginTransaction();
		session.delete(entity);
		commit();
		firePropertyChange(BookController.SceneProps.DELETE.toString(), entity, null);
	}

	public synchronized void setDeleteMultiScenes(ArrayList<Long> ids) {
		for (Long id : ids) {
			Session session = beginTransaction();
			SceneDAOImpl dao = new SceneDAOImpl(session);
			Scene old = dao.find(id);
			commit();
			setDeleteScene(old);
		}
	}

	// internals
	public void setEditInternal(Internal internal) {
		editEntity((AbstractEntity) internal);
	}

	public synchronized void setUpdateInternal(Internal entity) {
		Session session = beginTransaction();
		InternalDAOImpl dao = new InternalDAOImpl(session);
		Internal old = dao.find(entity.getId());
		commit();
		session = beginTransaction();
		session_update(session,entity);
		commit();
		mainFrame.setUpdated(true);
		firePropertyChange(BookController.InternalProps.UPDATE.toString(), old, entity);
	}

	public synchronized void setNewInternal(Internal entity) {
		Session session = beginTransaction();
		session_save(session,entity);
		commit();
		firePropertyChange(BookController.InternalProps.NEW.toString(), null, entity);
	}

	public synchronized void setDeleteInternal(Internal entity) {
		if (entity.getId() == null) {
			return;
		}
		Session session = beginTransaction();
		session.delete(entity);
		commit();
		firePropertyChange(BookController.InternalProps.DELETE.toString(), entity, null);
	}

	public synchronized void setDeleteMultiInternals(ArrayList<Long> ids) {
		for (Long id : ids) {
			Session session = beginTransaction();
			InternalDAOImpl dao = new InternalDAOImpl(session);
			Internal old = dao.find(id);
			commit();
			setDeleteInternal(old);
		}
	}

	// chapter
	public void setEditTimeEvent(TimeEvent entity) {
		editEntity((AbstractEntity) entity);
	}

	public synchronized void setUpdateTimeEvent(TimeEvent entity) {
		Session session = beginTransaction();
		TimeEventDAOImpl dao = new TimeEventDAOImpl(session);
		TimeEvent old = dao.find(entity.getId());
		commit();
		session = beginTransaction();
		session_update(session,entity);
		commit();
		mainFrame.setUpdated(true);
		firePropertyChange(BookController.TimeEventProps.UPDATE.toString(), old, entity);
	}

	public synchronized void setNewTimeEvent(TimeEvent entity) {
		Session session = beginTransaction();
		session_save(session,entity);
		commit();
		firePropertyChange(BookController.TimeEventProps.NEW.toString(), null, entity);
	}

	public synchronized void setDeleteTimeEvent(TimeEvent entity) {
		if (entity.getId() == null) {
			return;
		}
		// delete chapter
		Session session = beginTransaction();
		session.delete(entity);
		commit();
		firePropertyChange(BookController.TimeEventProps.DELETE.toString(), entity, null);
	}

	private void session_update(Session session, AbstractEntity e) {
		e.setMaj();
		session.update(e);
	}

	private void session_save(Session session, AbstractEntity e) {
		e.setMaj();
		session.save(e);
	}
}
