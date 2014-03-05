![rsine](https://raw.github.com/rsine/rsine/devel/src/main/resources/rsine_transparent.png "rsine")

## About

**Rsine** (Resource SubscrIption and Notification sErvice) is a service that allows users to subscribe for notification
on specific changes to triples in an RDF dataset. It is developed as part of work package 5 in the [LOD2 project](http://lod2.eu/WikiArticle/Project.html)
 by [Semantic Web Company](http://www.semantic-web.at/).

Rsine is implemented as a service that listens for changes of triples (additions/removals) stored in an RDF triple store
(the *managed store*) which is accessibly by a SPARQL endpoint. Users can express their interest in data changes as subscriptions
 which are essentially SPARQL queries that are run against the history of changes and the managed store. A subscription
 consists of

 * definition of the type of change (e.g., addition of a triple with property skos:broader)
 * an *optional* condition that must be fulfilled for receiving the notification
 * a pattern that defines the text and data values contained in the notification message
 * a notifier that specifies the way the notification is disseminated to the subscriber (e.g, by email)

Rsine takes care of issuing the queries, assembling the notification message and disseminating it to the subscriber. Rsine
relies on getting information about the triples written to or removed from the managed store. Currently we support two storage
solutions for RDF data:

 * [Virtuoso](http://virtuoso.openlinksw.com/): To establish interoperability with rsine, the provided
 [vad package](https://github.com/rsine/rsineVad) must installed
 * [openRDF](http://www.openrdf.org/): We developed a reference implementation of a <tt>RepositoryConnectionListener</tt> that forwards
 triple changes to rsine. The implementation is not published yet.

### Example Use-case

Development of controlled vocabularies (e.g., thesauri) is typically a manual and error-prone process. [SKOS](http://www.w3.org/2004/02/skos/)
has been widely accepted as a schema to to publish thesauri on the Web of Data. During development, vocabularies tend to become
increasingly complex and in many cases they are edited and curated by more than one person. However, the [SKOS reference](http://www.w3.org/TR/skos-reference/)
defines a number of basic integrity constraints that should be met and which can easily be missed. Such constraints encompass, e.g.,

 * Non-disjoint labels (One concept has identical preferred and alternative labels)
 * Relation clashes (Hierarchically related concepts are also associatively related)
 * Mapping clashes (Concepts mapped by skos:exactMatch must not be also related by a hierarchical mapping property)

Furthermore, custom vocabulary or use-case-specific constraints could be specified. Rsine is able to cover the constraints
outlined above and is, of course, capable to notify subscribers of any number of additional custom-defined constraints.
Also see the section *Integration Examples* below for additional information on rsine usage scenarios.

## Installation

Requirements:

 * Verify that Java version 1.7 or greater is installed: <tt>javac -version</tt>
 * Make sure Maven version 3.0 or greater is installed: <tt>mvn -v</tt>
 * Make sure you have the current version of the [git version control system](http://git-scm.com/) installed on your system

### Build from Source

#### Configuration before building (optional)

If you know in advance for what SPARQL endpoint you want to configure rsine, you can set this information in the
<tt>application.properties</tt> file, located in the <tt>rsine/src/main/resources</tt> directory. However, you can also
skip this step and set the relevant parameters at runtime (i.e. when starting the rsine service).

#### Performing the Build

1. Get the sourcecode by cloning the rsine repository: <tt>git clone https://github.com/rsine/rsine.git</tt>)
2. Change into the newly created <tt>rsine</tt> directory and build the application: <tt>mvn -DskipTests=true package</tt>

The file <tt>rsine-cmd.jar</tt> is now available in the directory <tt>rsine/target</tt>

Known Issues:

 * When building rsine with tests enabled (i.e. without the <tt>-DskipTests=true</tt> switch) it can happen that the build
fails due to non-successful tests. This happens when all tests are run consecutively and seems to be caused by some
concurrency issue in the test setup. As far as we know, these failing tests do not affect the functionality of the rsine
application.

## Usage

1. Change to the <tt>rsine/target</tt> directory
2. Run the tool using <tt>java -jar rsine-cmd.jar</tt>

### Configuration

As describe above, rsine reads it's configuration data from the file <tt>application.properties</tt> which is provided
at compile time. If you decided not to edit this file before compilation (see description above) you can set the most
essential parameters on the command line:

### Commandline Parameters

To get a synopsis on the supported parameters, type <tt>java -jar rsine-cmd.jar --help</tt>.

 * <tt>-s, --sparql-endpoint</tt>
 * <tt>-a, --authoritative-uri</tt>
 * <tt>-p, --port</tt>

### Configuration

## Subscriptions

## Notifications

## Integration Examples

### qSKOS

### LOD2 Project

## Future Work

## Publications

## Contributors

* Christian Mader ([@cmader](https://github.com/cmader))
* Jürgen Jakobitsch ([@turnguard](https://github.com/turnguard))
* Helmut Nagy ([@nagyhel](https://github.com/nagyhel))

## Copyright

Please see our [Contributor Agreement](https://raw.github.com/rsine/rsine/devel/rsine_contribution_agreement.pdf).