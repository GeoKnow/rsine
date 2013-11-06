package eu.lod2.rsine.registrationservice;

import eu.lod2.rsine.dissemination.messageformatting.BindingSetFormatter;
import eu.lod2.rsine.dissemination.notifier.INotifier;
import eu.lod2.util.Namespaces;
import org.openrdf.model.Resource;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import java.util.*;

public class Subscription {

    private static int id = 0;

    private Resource subscriptionId;
    private Collection<NotificationQuery> queries = new HashSet<NotificationQuery>();
    private Collection<INotifier> notifiers;

    public Subscription() {
        ValueFactory valueFactory = ValueFactoryImpl.getInstance();
        subscriptionId = valueFactory.createURI(
            Namespaces.RSINE_NAMESPACE.getName(),
            "subscriber_" +id);
        notifiers = new ArrayList<INotifier>();
        id++;
    }

    public Resource getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Resource subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Iterator<NotificationQuery> getQueries() {
        return queries.iterator();
    }

    public void addQuery(NotificationQuery query) {
        queries.add(query);
    }

    public void addQuery(String query, BindingSetFormatter formatter) {
        queries.add(new NotificationQuery(query, formatter, this));
    }

    public void addQuery(String query, BindingSetFormatter formatter, Condition condition) {
        queries.add(new NotificationQuery(query, formatter, Arrays.asList(condition), this));
    }

    public void addNotifier(INotifier notifier) {
        notifiers.add(notifier);
    }

    public Iterator<INotifier> getNotifierIterator() {
        return notifiers.iterator();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Subscription && subscriptionId.equals(((Subscription) obj).subscriptionId);
    }

    @Override
    public int hashCode() {
        return subscriptionId.hashCode();
    }
}
