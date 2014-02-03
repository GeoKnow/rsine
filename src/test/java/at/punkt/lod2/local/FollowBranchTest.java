package at.punkt.lod2.local;

import at.punkt.lod2.util.CountingNotifier;
import at.punkt.lod2.util.ExpectedCountReached;
import at.punkt.lod2.util.Helper;
import com.jayway.awaitility.Awaitility;
import eu.lod2.rsine.Rsine;
import eu.lod2.rsine.changesetservice.PersistAndNotifyProvider;
import eu.lod2.rsine.registrationservice.RegistrationService;
import eu.lod2.rsine.registrationservice.Subscription;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"LocalTest-context.xml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FollowBranchTest {

    @Autowired
    private Rsine rsine;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private PersistAndNotifyProvider persistAndNotifyProvider;

    @Autowired
    private RepositoryConnection managedStoreCon;

    private CountingNotifier countingNotifier;

    @Before
    public void setUp() throws IOException, RDFParseException, RDFHandlerException {
        subscribe();
        rsine.start();
    }

    @After
    public void tearDown() throws IOException, InterruptedException, RepositoryException {
        rsine.stop();
    }

    private void subscribe() throws RDFParseException, IOException, RDFHandlerException {
        countingNotifier = new CountingNotifier();

        Model subscriptionModel = Helper.createModelFromResourceFile("/wk/subscription_pp_follow_specific_branch.ttl", RDFFormat.TURTLE);
        Resource subscriptionId = registrationService.register(subscriptionModel);
        Subscription subscription = registrationService.getSubscription(subscriptionId);
        subscription.addNotifier(countingNotifier);
    }

    @Test
    public void followBranch() throws RepositoryException {
        //in reegle vocab: <http://reegle.info/glossary/676> skos:broader <http://reegle.info/glossary/1124>

        Helper.setAltLabel(managedStoreCon,
                new URIImpl("http://reegle.info/glossary/676"),
                new LiteralImpl("altlabel"),
                persistAndNotifyProvider);
        Awaitility.await().atMost(20, TimeUnit.SECONDS).until(new ExpectedCountReached(countingNotifier, 1));
    }

}