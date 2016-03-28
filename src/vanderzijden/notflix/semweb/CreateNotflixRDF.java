package vanderzijden.notflix.semweb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.reasoner.ValidityReport.Report;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.PrintUtil;
import org.apache.jena.vocabulary.RDF;
import org.json.JSONArray;
import org.json.JSONObject;

import vanderzijden.notflix.model.Movie;

public class CreateNotflixRDF
{
	
	private static final String ONTOLOGY = "project/notflix_ontology.xml";
	private static final String DATA_FILE = "project/notflix_data.xml";
	private static final String ONTOLOGY_NS = "http://www.notflix.vanderzijden.nl/ontology#";
	private static final String DATA_NS = "http://www.notflix.vanderzijden.nl/movies#";
	private static final String JSON_MOVIES = "project/top250.json";

	public static void main(String[] args) throws IOException
	{
		Model schema = FileManager.get().loadModel(ONTOLOGY, ONTOLOGY_NS, null);
		Property movieType = schema.getProperty(ONTOLOGY_NS, "Movie");
		Property imdbID = schema.getProperty(ONTOLOGY_NS, "imdbID");
		Property title = schema.getProperty(ONTOLOGY_NS, "title");
		
		
		Model data = ModelFactory.createDefaultModel();
		
		JSONArray jsonMovies = getJSONArray(JSON_MOVIES);
		for (int n = 0; n < jsonMovies.length(); n++)
		{
			JSONObject jsonMovie = jsonMovies.getJSONObject(n);
			Movie movie = Movie.parseFromOmdbApi(jsonMovie);
			Resource aFilm = data.createResource(DATA_NS + movie.getImdbID());
			aFilm.addProperty(RDF.type, movieType);
			aFilm.addProperty(imdbID, movie.getImdbID());
			aFilm.addProperty(title, movie.getTitle());
			
		}
		
		
		// Model data = FileManager.get().loadModel("project/notflix_data.xml", DATA_NS, null );
		
		Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
		reasoner = reasoner.bindSchema(schema);
		InfModel infmodel = ModelFactory.createInfModel(reasoner, data);

		printStatements(data, null, null, null);
		
		checkValidity(infmodel);
		
		FileWriter fw = new FileWriter(DATA_FILE); 
		data.write(fw);
		
		fw.close();
		schema.close();
		data.close();
	}
	
	public static JSONArray getJSONArray(String filename) throws FileNotFoundException
	{
		Scanner scanner = new Scanner(new File(filename)).useDelimiter("!@#endoffile#@!");
		String s = scanner.next();
		return new JSONArray(s);
	}

	public static void checkValidity(InfModel infmodel)
	{
		ValidityReport validity = infmodel.validate();
		if (validity.isValid()) {
		    System.out.println("OK");
		} else {
		    System.out.println("Conflicts");
		    for (Iterator<Report> i = validity.getReports(); i.hasNext(); ) {
		        ValidityReport.Report report = i.next();
		        System.out.println(" - " + report);
		    }
		}
	}
	
	public static void printStatements(Model m, Resource s, Property p, Resource o) {
	    for (StmtIterator i = m.listStatements(s,p,o); i.hasNext(); ) {
	        Statement stmt = i.nextStatement();
	        System.out.println(" - " + PrintUtil.print(stmt));
	    }
	}

}
