/* EDSON YUDI TOMA - 9791305 */

import java.io.PrintWriter;
import java.util.Iterator;

/* https://jena.apache.org/documentation/javadoc/jena/index.html */
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.*;
import org.apache.jena.sparql.vocabulary.FOAF;

public class Simpsons {
	private Model model;
	/* private String rdfPrefix, xsdPrefix, foafPrefix; */
	private String famPrefix, simPrefix;

	public Simpsons() {
		model = ModelFactory.createDefaultModel();
	}

	/*
	* Model Model.read(String url, String lang)
	*	Add statements from a serializion in language lang to the model.
	* String FileUtils.guessLang(String urlStr)
	*	Guess the language/type of model data 
	*	If the URI ends ".rdf", it is assumed to be RDF/XML 
	*	If the URI ends ".nt", it is assumed to be N-Triples 
	*	If the URI ends ".ttl", it is assumed to be Turtle 
	*	If the URI ends ".owl", it is assumed to be RDF/XML
	* String PrefixMapping.getNsPrefixURI(String prefix)
	*	Get the URI bound to a specific prefix, null if there isn't one.
	*/
	public void readFile(String name) {
		model.read(name, FileUtils.guessLang(name));
		//rdfPrefix = model.getNsPrefixURI("rdf"); // use "RDF" instead
		//xsdPrefix = model.getNsPrefixURI("xsd"); // use "XSDDatatype" instead
		//foafPrefix = model.getNsPrefixURI("foaf"); // use "FOAF" instead
		famPrefix = model.getNsPrefixURI("fam");
		simPrefix = model.getNsPrefixURI("sim");
	}

	/*
	* Resource Model.createResource(String uri)
	*	Create a new resource associated with this model.
	* Property Model.createProperty(String nameSpace, String localName)
	*	Create a property with a given URI composed from a namespace part and a localname part by concatenating the strings.
	* Resource Resource.addProperty(Property p, String o)
	*	Add a property to this resource.
	*	A statement with this resource as the subject, 
	*	p as the predicate and 
	*	o as the object is added to the model associated with this resource.
	* Resource Resource.addProperty(Property p, String lexicalForm, RDFDatatype datatype)
	*	Add a property to this resource.
	*	A statement with this resource as the subject, 
	*	p as the predicate and 
	*	lexicalForm as the lexical form of the literal
	*	datatype as the datatype.
	*/
	private Resource addPerson(String name, Integer age) {		
		Resource person = model.createResource(simPrefix + name.split(" ")[0]);
		person.addProperty(RDF.type, FOAF.Person);
		person.addProperty(FOAF.name, name);
		person.addProperty(FOAF.age, Integer.toString(age), XSDDatatype.XSDint);
		return person;
	}

	private Resource addPerson(String name) {
		Resource person = model.createResource(simPrefix + name.split(" ")[0]);
		person.addProperty(RDF.type, FOAF.Person);
		person.addProperty(FOAF.name, name);
		return person;
	}

	private void marriage(Resource person1, Resource person2) {
		Property hasSpouse = model.createProperty(famPrefix, "hasSpouse");
		person1.addProperty(hasSpouse, person2);
		person2.addProperty(hasSpouse, person1);
	}

	private void linkFather(Resource person1, Resource person2) {
		Property hasFather = model.createProperty(famPrefix, "hasFather");
		person1.addProperty(hasFather, person2);
	}

	public void addInfo() {
		Resource maggie = addPerson("Maggie Simpson", 1);
		Resource mona = addPerson("Mona Simpson", 70);
		Resource abraham = addPerson("Abraham Simpson", 78);
		Resource herb = addPerson("Herb");

		marriage(mona, abraham);

		linkFather(herb, model.createResource(/* Blank Node */));
	}

	/*
	* StmtIterator Model.listStatements(Resource s, Property p, RDFNode o)
	*	Find all the statements matching a pattern.
	*	Return an iterator over all the statements in a model that match a pattern. 
	*	The statements selected are those whose subject matches the subject argument, 
	*	whose predicate matches the predicate argument and whose object matches the object argument. 
	*	If an argument is null it matches anything.
	* Resource Statement.getSubject()
	*	An accessor method to return the subject of the statements.
	* RDFNode Statement.getObject()
	*	An accessor funtion to return the object of the statement.
	* Literal RDFNode.asLiteral()
	*	If this node is a Literal, answer that literal; otherwise throw an exception.
	* int Literal.getInt()
	*	If the literal is interpretable as a Integer return its value.
	*/
	public void modifyInfo() {
		Property infant = model.createProperty(famPrefix, "Infant");
		Property minor = model.createProperty(famPrefix, "Minor");
		Property old = model.createProperty(famPrefix, "Old");
		Iterator<Statement> iterator = model.listStatements((Resource) null, FOAF.age, (RDFNode) null);
		while (iterator.hasNext()) {
			Statement statement = iterator.next();
			Resource person = statement.getSubject();
			Integer age = statement.getObject().asLiteral().getInt();
			if (age < 2) {
				person.addProperty(RDF.type, infant);
			}
			if (age < 18) {
				person.addProperty(RDF.type, minor);
			}
			if (age > 70) {
				person.addProperty(RDF.type, old);
			}
		}
	}

	public void writeFile(String name) {
		try(PrintWriter file = new PrintWriter(name)) {
			model.write(file, FileUtils.guessLang(name));
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void main(String[] args) {
		String inputFile, outputFile;
		Simpsons simpsons = new Simpsons();
		
		/* Check inputs */
		try {
			inputFile = args[0];
			outputFile = args[1];
		} catch (Exception e) {
			System.out.println("Please supply an input and output file.");
			return;
		}

		/* 1 Read input */
		simpsons.readFile(inputFile);

		/* 2 Adding information */
		simpsons.addInfo();

		/* 3 Locate, read and write information */
		simpsons.modifyInfo();

		/* 4 Write to file */
		simpsons.writeFile(outputFile);
	}
}