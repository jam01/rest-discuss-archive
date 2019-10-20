package rdf;

import java.util.HashSet;
import java.util.Set;

import org.restlet.Component;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.ext.rdf.Literal;
import org.restlet.ext.rdf.RdfClientResource;
import org.restlet.util.Couple;

public class LinkedDataCrawler extends Component {

    public static void main(String[] args) {
        LinkedDataCrawler crawler = new LinkedDataCrawler(
                "http://bblfish.net/people/henry/card#me");
        crawler.start();
    }

    private Reference rootRef;

    private Set<Reference> crawledRefs;

    private int limit;

    private int count;

    /**
     * Constructor.
     * 
     * @param rootUri
     *            The root URI to crawl.
     */
    public LinkedDataCrawler(String rootUri) {
        getClients().add(Protocol.HTTP);
        getClients().add(Protocol.HTTPS);

        this.crawledRefs = new HashSet<Reference>();
        this.rootRef = new Reference(rootUri);
        this.limit = 100;
        this.count = 0;
    }

    public void start() {
        System.out.println("---------------------------");
        System.out.println("Restlet Linked Data Crawler");
        System.out.println("---------------------------\n");
        crawl(new RdfClientResource(getContext(), rootRef));
    }

    private void crawl(RdfClientResource resource) {
        if (!this.crawledRefs.contains(resource.getReference())) {
            this.crawledRefs.add(resource.getReference());

            if (this.count++ < this.limit) {
                resource.getReference().setBaseRef(this.rootRef);
                String relativePart = resource.getReference().getRelativePart();

                if (relativePart == null) {
                    System.out.println("Resource: " + resource.getReference());
                } else {
                    System.out.println("Resource: " + relativePart);
                }

                Set<Couple<Reference, Literal>> couples = resource
                        .getLiterals();

                if (couples != null) {
                    for (Couple<Reference, Literal> couple : couples) {
                        System.out.println(couple.getFirst() + " = "
                                + couple.getSecond());
                    }
                }

                System.out.println();

                // Recurse over linked resources
                Set<RdfClientResource> linked = resource.getLinked();

                if (linked != null) {
                    for (RdfClientResource current : linked) {
                        crawl(current);
                    }
                }
            }
        }
    }
}
