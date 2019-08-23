package lu.crx.prototype.caller;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.leveldb.LevelDBAggregationRepository;
import org.springframework.stereotype.Component;

import static org.apache.camel.builder.PredicateBuilder.and;

@Component
public class CallerRoute extends RouteBuilder {

    /**
     * A sequential identifier of a fork/join request.
     */
    private int callerCorrelationId = 0;

    @Override
    public void configure() {

        // send a message every 2 seconds
        from("timer:caller?fixedRate=true&period=2s")
                // generating and setting a correlation identifier used later to aggregate the responses
                .setHeader("callerCorrelationId", () -> callerCorrelationId++)
                .setBody(simple("Please process message ${header.callerCorrelationId}"))
                // sending a message to each of the three callees
                .to("activemq:callee1Queue")
                .to("activemq:callee2Queue")
                .to("activemq:callee3Queue")
                .log("Sent message for callerCorrelationId=${header.callerCorrelationId}");

        // listening to the response queue
        from("activemq:result")
                // we aggregate the response messages based on callerCorrelationId provided by the caller
                .aggregate(header("callerCorrelationId"), ((oldExchange, newExchange) -> {
                    if (oldExchange == null) {
                        oldExchange = newExchange;
                    }
                    // for each message, we add a "callee*Finished=true" header to the aggregation exchange
                    oldExchange.getIn().setHeader(
                            newExchange.getIn().getHeader("calleeId", String.class) + "Finished", true);
                    return oldExchange;
                }))
                // we persist the current aggregation state in a LevelDB repository, keeping the state across relaunches of caller
                .aggregationRepository(new LevelDBAggregationRepository("callerRepo", "callerRepoDir"))
                // if some messages are lost, we close the aggregation group every 10 seconds anyway
                .completionTimeout(10000)
                // on a happy path, we close the aggregation group when all callees have responded with a message,
                // i.e. when all callee*Finished headers are present
                .completionPredicate(and(
                        header("callee1Finished").isEqualTo(true),
                        header("callee2Finished").isEqualTo(true),
                        header("callee3Finished").isEqualTo(true)
                ))
                .log("Finished processing of callerCorrelationId=${in.header.callerCorrelationId}");
    }

}
