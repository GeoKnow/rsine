package at.punkt.lod2.local;

import at.punkt.lod2.util.CountingNotifier;
import at.punkt.lod2.util.ExpectedCountReached;
import at.punkt.lod2.util.Helper;
import com.jayway.awaitility.Awaitility;
import eu.lod2.rsine.Rsine;
import eu.lod2.rsine.changesetservice.ChangeTripleHandler;
import eu.lod2.rsine.changesetservice.PersistAndNotifyProvider;
import eu.lod2.rsine.registrationservice.RegistrationService;
import eu.lod2.rsine.registrationservice.Subscription;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.SKOS;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"LocalTest-context.xml"})
public class QualityNotificationsTest {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private PersistAndNotifyProvider persistAndNotifyProvider;

    @Autowired
    private Repository managedStoreRepo;

    private CountingNotifier countingNotifier;

    @Before
    public void setUp() throws IOException, RDFParseException, RDFHandlerException, RepositoryException {
        countingNotifier = new CountingNotifier();
        if (managedStoreRepo.getConnection().isEmpty()) {
            managedStoreRepo.getConnection().add(Rsine.class.getResource("/reegle.rdf"), "", RDFFormat.RDFXML);
        }
    }

    @Test
    public void hierarchicalCycles() throws RDFParseException, IOException, RDFHandlerException, RepositoryException {
        subscribe("/quality/cyclic_hierarchical_relations.ttl");
        addTriple(new URIImpl("http://reegle.info/glossary/1124"),
            SKOS.BROADER,
            new URIImpl("http://reegle.info/glossary/676"));

        Assert.assertEquals(1, countingNotifier.getNotificationCount());
    }

    private void subscribe(String subscriptionFileLocation) throws RDFParseException, IOException, RDFHandlerException {
        Model subscriptionModel = Helper.createModelFromResourceFile(subscriptionFileLocation, RDFFormat.TURTLE);
        Resource subscriptionId = registrationService.register(subscriptionModel);
        Subscription subscription = registrationService.getSubscription(subscriptionId);
        subscription.addNotifier(countingNotifier);
    }

    public void addTriple(URI subject, URI predicate, URI object) throws RepositoryException {
        managedStoreRepo.getConnection().add(subject, predicate, object);

        persistAndNotifyProvider.persistAndNotify(
                Helper.createChangeSetModel(subject.stringValue(),
                        predicate.stringValue(),
                        object,
                        ChangeTripleHandler.CHANGETYPE_ADD),
                true);
    }

    @Test
    public void multiHierarchicalCycles()
        throws RDFParseException, IOException, RDFHandlerException, RepositoryException
    {
        subscribe("/quality/cyclic_hierarchical_relations.ttl");

        addTriple(new URIImpl("http://reegle.info/glossary/1124"),
                SKOS.BROADER,
                new URIImpl("http://reegle.info/newConcept"));
        addTriple(new URIImpl("http://reegle.info/newConcept"),
            SKOS.BROADER,
            new URIImpl("http://reegle.info/glossary/676"));
        addTriple(new URIImpl("http://reegle.info/glossary/676"),
            SKOS.BROADER,
            new URIImpl("http://reegle.info/glossary/1124"));

        Awaitility.await().atMost(10, TimeUnit.SECONDS).until(new ExpectedCountReached(countingNotifier, 2));
    }

    @Test
    public void disjointLabelViolations_withPrefLabel()
        throws RDFParseException, IOException, RDFHandlerException, RepositoryException
    {
        subscribe("/quality/disjoint_labels_violation.ttl");
        Helper.setAltLabel(managedStoreRepo.getConnection(),
            new URIImpl("http://reegle.info/glossary/682"),
            new LiteralImpl("energy efficiency", "en"),
            persistAndNotifyProvider);

        Awaitility.await().atMost(20, TimeUnit.SECONDS).until(new ExpectedCountReached(countingNotifier, 1));
    }

    @Test
    public void disjointLabelViolations_withAltLabel()
        throws RDFParseException, IOException, RDFHandlerException, RepositoryException
    {
        subscribe("/quality/disjoint_labels_violation.ttl");
        Helper.setAltLabel(managedStoreRepo.getConnection(),
            new URIImpl("http://reegle.info/glossary/1063"),
            new LiteralImpl("emission", "en"),
            persistAndNotifyProvider);
        Awaitility.await().atMost(20, TimeUnit.SECONDS).until(new ExpectedCountReached(countingNotifier, 1));
    }

    @Test
    public void valuelessAssociativeRelations()
        throws RDFParseException, IOException, RDFHandlerException, RepositoryException
    {
        subscribe("/quality/valueless_associative_relations.ttl");
        String sibling1 = "http://reegle.info/glossary/1676";
        String sibling2 = "http://reegle.info/glossary/1252";
        addTriple(new URIImpl(sibling1), SKOS.RELATED, new URIImpl(sibling2));

        Awaitility.await().atMost(20, TimeUnit.SECONDS).until(new ExpectedCountReached(countingNotifier, 1));
    }

    @Test
    public void hierarchicalRedundancies()
        throws RDFParseException, IOException, RDFHandlerException, RepositoryException
    {
        subscribe("/quality/hierarchical_redundancy.ttl");

        String level1Concept = "http://reegle.info/glossary/1056";
        String level3Concept = "http://reegle.info/glossary/196";
        addTriple(new URIImpl(level3Concept), SKOS.BROADER, new URIImpl(level1Concept));
        addTriple(new URIImpl(level3Concept), SKOS.NARROWER, new URIImpl(level1Concept));

        Awaitility.await().atMost(20, TimeUnit.SECONDS).until(new ExpectedCountReached(countingNotifier, 1));
    }

    @Test
    public void overlappingLabels() throws RDFParseException, IOException, RDFHandlerException, RepositoryException {
        subscribe("/quality/overlapping_labels.ttl");

        Helper.setAltLabel(managedStoreRepo.getConnection(),
            new URIImpl("http://reegle.info/glossary/357"),
            new LiteralImpl("Biogas", "en"),
            persistAndNotifyProvider);
        Awaitility.await().atMost(20, TimeUnit.SECONDS).until(new ExpectedCountReached(countingNotifier, 1));
    }

    @Test
    public void relationClashes() throws RDFParseException, IOException, RDFHandlerException, RepositoryException {
        subscribe("/quality/relation_clashes.ttl");

        String level1Concept = "http://reegle.info/glossary/1056";
        String level3Concept = "http://reegle.info/glossary/196";
        addTriple(new URIImpl(level3Concept), SKOS.RELATED, new URIImpl(level1Concept));

        Awaitility.await().atMost(20, TimeUnit.SECONDS).until(new ExpectedCountReached(countingNotifier, 1));
    }

    @Test
    public void mappingClashes() throws RDFParseException, IOException, RDFHandlerException, RepositoryException {
        subscribe("/quality/mapping_clashes.ttl");

        String concept = "http://reegle.info/glossary/1912";
        String relatedMappedConcept = "http://dbpedia.org/resource/Vulnerability";

        // error
        addTriple(new URIImpl(concept), SKOS.BROAD_MATCH, new URIImpl(relatedMappedConcept));

        // error
        addTriple(new URIImpl(relatedMappedConcept), SKOS.EXACT_MATCH, new URIImpl(concept));

        // ok
        addTriple(new URIImpl(concept), SKOS.BROAD_MATCH, new URIImpl("http://reegle.info/glossary/1674"));

        Awaitility.await().atMost(20, TimeUnit.SECONDS).until(new ExpectedCountReached(countingNotifier, 2));
    }

    @Test
    public void mappingMisues_sameScheme()
        throws IOException, RDFHandlerException, RDFParseException, RepositoryException
    {
        subscribe("/quality/mapping_relations_misuse.ttl");
        String[] conceptsInSameScheme = {"http://reegle.info/glossary/676", "http://reegle.info/glossary/1620"};
        addTriple(new URIImpl(conceptsInSameScheme[0]), SKOS.BROAD_MATCH, new URIImpl(conceptsInSameScheme[1]));

        Awaitility.await().atMost(20, TimeUnit.SECONDS).until(new ExpectedCountReached(countingNotifier, 1));
    }

    @Test
    public void topConceptsHavingBroaderConcepts()
        throws RDFParseException, IOException, RDFHandlerException, RepositoryException
    {
        subscribe("/quality/top_concepts_having_broader_concepts.ttl");

        String topConcept = "http://reegle.info/glossary/1127";

        // error (should count as one because of inverse relation)
        addTriple(new URIImpl(topConcept), SKOS.BROADER, new URIImpl("http://some.concept"));
        addTriple(new URIImpl("http://some.other.concept"), SKOS.NARROWER, new URIImpl(topConcept));

        // no error
        addTriple(new URIImpl(topConcept), SKOS.NARROWER, new URIImpl("http://some.completely.other.concept"));

        addTriple(new URIImpl("http://some.completely.other.concept"), SKOS.BROADER, new URIImpl("http://some.completely.other.concept2"));

        Awaitility.await().atMost(20, TimeUnit.SECONDS).until(new ExpectedCountReached(countingNotifier, 1));
    }

}