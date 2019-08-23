## Description
This is a PoC of fork/join functionality across microservices based on Camel.
The `caller` service sends a message to the three instances of a `callee` service to perform some work.
The `caller` then needs to aggregate the results of these three services. The interaction between
the services happens via ActiveMQ.

The aggregation state should be persisted across `caller` service restart. The processing of
messages in the `callee` services should be transactional in relation to the queue
(i.e. if we kill the service, all messages in flight should return to the queue and be processed 
again upon restart).

## Running
Run the caller application with the following arguments:
```
--spring.activemq.broker-url=<activemq broker url> \
  --spring.activemq.user=<activemq user> \
  --spring.activemq.password=<activemq password> 
```

Run three instances of the callee application with the following arguments:
```
--spring.activemq.broker-url=<activemq broker url> \
  --spring.activemq.user=<activemq user> \
  --spring.activemq.password=<activemq password> 
  --queueName=<queue> 
  --calleeId=<id>>
```
Where `queueName` argument should be `callee1Queue`, `callee2Queue` and `callee3Queue`,
and `calleeId` argument should be `callee1`, `callee2` and `callee3` for the respective instances.