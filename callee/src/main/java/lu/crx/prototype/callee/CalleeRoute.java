package lu.crx.prototype.callee;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CalleeRoute extends RouteBuilder {

    /**
     * The name of the queue to listen to
     */
    @Value("${queueName}")
    private String queueName;

    /**
     * This caller instance id
     */
    @Value("${calleeId}")
    private String calleeId;

    @Override
    public void configure() throws Exception {
        // the transacted=true argument is important to consume the messages from the queue transactionally
        from("activemq:" + queueName + "?transacted=true")
                // we log the callerCorrelationId, but the callee service may not know about this parameter,
                // since it is only relevant for the caller
                .log("Received message with callerCorrelationId=${in.header.callerCorrelationId}")
                // imitate load by delaying randomly for 1-3 seconds
                .delay(simple("${random(1000,3000)}"))
                .setBody(simple("Processed message ${in.header.callerCorrelationId}"))
                // this header is needed to aggregate the messages, so it may as well be set on the caller side
                .setHeader("calleeId", constant(calleeId))
                .to("activemq:result");
    }

}