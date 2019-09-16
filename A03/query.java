/* EDSON YUDI TOMA - 9791305 */

import java.io.FileInputStream;
import java.io.File;

/* https://jena.apache.org/documentation/javadoc/jena/index.html */
/* https://jena.apache.org/documentation/javadoc/arq/org/apache/jena/query/QueryExecution.html */
/* https://jena.apache.org/documentation/javadoc/arq/org/apache/jena/query/QueryExecutionFactory.html */
/* https://jena.apache.org/documentation/javadoc/arq/org/apache/jena/query/ResultSetFormatter.html */
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileUtils;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryExecutionFactory;

public class query {
	private Model model;
	private String queryString;

	/*
	* static OntModel ModelFactory.createOntologyModel()
	*	Answer a new ontology model which will process in-memory models of ontologies expressed the default ontology language (OWL).
	* Model Model.read(InputStream in, String base)
	*	Add statements from a document. This method assumes the concrete syntax is RDF/XML.
	* static String FileUtils.guessLang(String urlStr)
	*	Guess the language/type of model data 
	*	If the URI ends ".rdf", it is assumed to be RDF/XML
	*	If the URI ends ".nt", it is assumed to be N-Triples
	*	If the URI ends ".ttl", it is assumed to be Turtle
	*	If the URI ends ".owl", it is assumed to be RDF/XML
	*/
	public query(String rdfFile) {
		//model = ModelFactory.createDefaultModel();
		model = ModelFactory.createOntologyModel();
		model.read(rdfFile, FileUtils.guessLang(rdfFile));
	}

	public void loadQuery(String queryFile) {
		try {
			File file = new File(queryFile);
			FileInputStream fis = new FileInputStream(file);
			byte[] data = new byte[(int) file.length()];
			fis.read(data);
			fis.close();

			queryString = new String(data, "UTF-8");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/*
	* static Query QueryFactory.create(String queryString)
	*	Create a SPARQL query from the given string.
	* static QueryExecution QueryExecutionFactory.create(Query query, Model model)
	*	Create a QueryExecution to execute over the Model.
	* boolean Query.isSelectType(), boolean Query.isAskType(), boolean Query.isConstructType()
	*	Checks if the query is a select query, ask query or construct query, respectively.
	* boolean QueryExecution.execSelect(), boolean QueryExecution.execAsk(), boolean QueryExecution.execConstruct()
	*	run the query on select mode, ask mode, construct mode, respectively.
	* static void ResultSetFormatter.out(ResultSet results, Query query)
	*	Output a result set in a text format.
	* static void ResultSetFormatter.out(boolean results)
	*	Output a boolean in a text format.
	* Model Model.write(OutputStream out, String lang)
	*	Write a serialized representation of this model in a specified language.
	*/
	public void executeQuery() {
		Query queryFactory = QueryFactory.create(queryString);
		QueryExecution executeQuery = QueryExecutionFactory.create(queryFactory, model);
		if (queryFactory.isSelectType()) {
			ResultSet results = executeQuery.execSelect();
			ResultSetFormatter.out(results, queryFactory);
		} else if (queryFactory.isAskType()) {
			boolean result = executeQuery.execAsk();
			ResultSetFormatter.out(result);
		} else if (queryFactory.isConstructType()) {
			Model result = executeQuery.execConstruct();
			result.write(System.out, "TTL");
		}
	}

	public static void main(String[] args) {
		String rdfFile, queryFile;
		query q;
		
		/* Check inputs */
		try {
			rdfFile = args[0];
			queryFile = args[1];
		} catch (Exception e) {
			System.out.println("Please supply the input and exercise_X.rq files.");
			return;
		}

		q = new query(rdfFile);
		q.loadQuery(queryFile);
		q.executeQuery();
	}
}