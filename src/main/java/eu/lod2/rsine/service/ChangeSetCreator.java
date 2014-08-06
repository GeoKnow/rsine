package eu.lod2.rsine.service;

import eu.lod2.util.Namespaces;
import org.openrdf.model.*;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.TreeModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

@Component
public class ChangeSetCreator {

    private ValueFactory valueFactory = ValueFactoryImpl.getInstance();

    public Model assembleChangeset(Statement affectedStatement, Statement secondaryStatement, String changeType) {
        Model model = new TreeModel(new HashSet<Namespace>(Arrays.asList(Namespaces.RSINE_NAMESPACE, Namespaces.CS_NAMESPACE)));

        URI changeSet = valueFactory.createURI(
            Namespaces.RSINE_NAMESPACE.getName(),
            "cs" +System.currentTimeMillis()+"_" +Math.round(Math.random() * 1000));

        model.add(new StatementImpl(changeSet,
            RDF.TYPE,
            valueFactory.createURI(Namespaces.CS_NAMESPACE.getName(), "ChangeSet")));
        model.add(new StatementImpl(changeSet,
            valueFactory.createURI(Namespaces.CS_NAMESPACE.getName(), "createdDate"),
            valueFactory.createLiteral(new Date())));

        if (changeType.equals(ChangeTripleService.CHANGETYPE_REMOVE)) {
            addActionStatement(model, changeSet, affectedStatement, "removal");
        }
        else if (changeType.equals(ChangeTripleService.CHANGETYPE_ADD)) {
            addActionStatement(model, changeSet, affectedStatement, "addition");
        }
        else if (changeType.equals(ChangeTripleService.CHANGETYPE_UPDATE)) {
            addActionStatement(model, changeSet, affectedStatement, "removal");
            addActionStatement(model, changeSet, secondaryStatement, "addition");
        }

        return model;
    }

    private void addActionStatement(Graph graph, Resource changeSet, Statement statement, String action) {
        graph.add(new StatementImpl(changeSet,
            valueFactory.createURI(Namespaces.CS_NAMESPACE.getName(), action),
            createStatementNode(statement, graph)));
    }

    private URI createStatementNode(Statement affectedStatement, Graph graph) {
        URI statementUri = valueFactory.createURI(Namespaces.CS_NAMESPACE.getName(),
                "stmnode" + System.currentTimeMillis() + "_" + Math.round(Math.random() * 1000));

        graph.add(new StatementImpl(statementUri, RDF.TYPE, RDF.STATEMENT));
        graph.add(new StatementImpl(statementUri, RDF.SUBJECT, affectedStatement.getSubject()));
        graph.add(new StatementImpl(statementUri, RDF.PREDICATE, affectedStatement.getPredicate()));
        graph.add(new StatementImpl(statementUri, RDF.OBJECT, affectedStatement.getObject()));

        return statementUri;
    }

}
