package vanderzijden.notflix.semweb;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.resultset.OutputFormatter;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.PrintUtil;

public class RDFModel
{

	public static final String DBPEDIA_EP = "http://dbpedia.org/sparql";
	public static final String ONTOLOGY_NS = "http://www.notflix.vanderzijden.nl/ontology#";
	public static final String DATA_NS = "http://www.notflix.vanderzijden.nl/movies#";

	protected Model ontology;
	protected Model model;

	protected Resource movieType;
	protected Property imdbID;
	protected Property title;

	public RDFModel(String ontologyLocation, String modelLocation)
	{
		ontology = FileManager.get().loadModel(ontologyLocation, ONTOLOGY_NS, null);
		if (modelLocation == null)
			model = ModelFactory.createDefaultModel();
		else
			model = FileManager.get().loadModel(modelLocation, DATA_NS, null);
		movieType = ontology.createResource(ONTOLOGY_NS + "Movie");
		imdbID = ontology.getProperty(ONTOLOGY_NS, "imdbID");
		title = ontology.getProperty(ONTOLOGY_NS, "title");
	}

	public RDFModel(String ontologyLocation)
	{
		this(ontologyLocation, null);
	}

	public String getString(Resource resource, Property property)
	{
		Statement st = model.getProperty(resource, property);
		if (st == null)
			return null;
		return st.getLiteral().toString();
	}
	
	public String getUriString(Resource resource, Property property)
	{
		Statement st = model.getProperty(resource, property);
		PrintUtil.print(st);
		if (st == null)
			return null;
		return st.getResource().toString();		
	}

}
