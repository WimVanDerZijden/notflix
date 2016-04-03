package vanderzijden.notflix.semweb;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.util.FileManager;

public class RDFModel {

	public static final String DBPEDIA_EP = "http://dbpedia.org/sparql";
	public static final String ONTOLOGY_NS = "http://www.notflix.vanderzijden.nl/ontology#";
	public static final String DATA_NS = "http://www.notflix.vanderzijden.nl/movies#";

	protected Model ontology;
	protected Model model;

	protected final Resource movieType;
	protected final Property imdbID;
	protected final Property title;
	protected final Property plot;
	protected final Property poster;
	protected final Property imdbVotes;
	protected final Property imdbRating;
	protected final Property released;
	protected final Property runtime;

	public RDFModel(String ontologyLocation, String modelLocation) {
		ontology = FileManager.get().loadModel(ontologyLocation, ONTOLOGY_NS, null);
		if (modelLocation == null)
			model = ModelFactory.createDefaultModel();
		else
			model = FileManager.get().loadModel(modelLocation, DATA_NS, null);
		movieType = ontology.createResource(ONTOLOGY_NS + "Movie");
		imdbID = ontology.createProperty(ONTOLOGY_NS, "imdbID");
		title = ontology.createProperty(ONTOLOGY_NS, "title");
		plot = ontology.createProperty(ONTOLOGY_NS, "plot");
		poster = ontology.createProperty(ONTOLOGY_NS, "poster");
		imdbVotes = ontology.createProperty(ONTOLOGY_NS, "imdbVotes");
		imdbRating = ontology.createProperty(ONTOLOGY_NS, "imdbRating");
		released = ontology.createProperty(ONTOLOGY_NS, "released");
		runtime = ontology.createProperty(ONTOLOGY_NS, "runtime");
	}

	public RDFModel(String ontologyLocation) {
		this(ontologyLocation, null);
	}

	public String getString(Resource resource, Property property) {
		Statement st = model.getProperty(resource, property);
		if (st == null)
			return null;
		return st.getLiteral().getLexicalForm();
	}

	public String getString(Resource resource, Property property, String lang) {
		NodeIterator iter = model.listObjectsOfProperty(resource, property);
		while (iter.hasNext()) {
			RDFNode node = iter.next();
			if (node.asLiteral().getLanguage().equals(lang))
				return node.asLiteral().getLexicalForm();
		}
		return null;
	}
	
	public int getInt(Resource resource, Property property)
	{
		try
		{
		return Integer.parseInt(getString(resource, property));
		}
		catch (NumberFormatException e)
		{
			return 0;
		}
	}
	
	public long getLong(Resource resource, Property property)
	{
		try
		{
		return Long.parseLong(getString(resource, property));
		}
		catch (NumberFormatException e)
		{
			return 0L;
		}
	}
	
	public double getDouble(Resource resource, Property property)
	{
		try
		{
		return Double.parseDouble(getString(resource, property));
		}
		catch (NumberFormatException e)
		{
			return 0D;
		}
	}
	
	public String getUriString(Resource resource, Property property) {
		Statement st = model.getProperty(resource, property);
		if (st == null)
			return null;
		return st.getResource().getURI();
	}

}
