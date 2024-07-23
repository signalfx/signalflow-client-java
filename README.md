 # SignalFlow Java Client

This is a client for [SignalFlow](https://dev.splunk.com/observability/docs/signalflow),
which enables you to stream and analyze your organization's metric data in real time.


## Run SignalFlow computations

SignalFlow is the SignalFx real-time analytics computation language. The
SignalFlow API allows SignalFx users to execute real-time streaming analytics
computations on the SignalFx platform. For more information, see the Splunk
Observability Cloud developer documentation:

* [SignalFlow Overview](https://dev.splunk.com/observability/docs/signalflow/)
* [SignalFlow API Reference](https://dev.splunk.com/observability/reference/api/signalflow/latest)

The following example demonstrates how to run a SignalFlow program using the
Java client library:

```java
String program = "data('cpu.utilization').mean().publish()";
SignalFlowClient flow = new SignalFlowClient("<token>");
System.out.println("Running " + program);
Computation computation = flow.execute(program);
for (ChannelMessage message : computation) {
    switch (message.getType()) {
    case DATA_MESSAGE:
        DataMessage dataMessage = (DataMessage) message;
        System.out.printf("%d: %s%n",
                dataMessage.getLogicalTimestampMs(), dataMessage.getData());
        break;

    case EVENT_MESSAGE:
        EventMessage eventMessage = (EventMessage) message;
        System.out.printf("%d: %s%n",
                eventMessage.getTimestampMs(),
                eventMessage.getProperties());
        break;
    }
}
```

If you need to receive timeseries metadata, you can use the Java client library 
to provide access to this information using the ``Computation`` object returned
by the ``execute()`` method. To receive timeseries metadata, change the
``DATA_MESSAGE`` case to the following:

```java
case DATA_MESSAGE:
    DataMessage dataMessage = (DataMessage) message;
    for (Map<String, Number> datum : dataMessage.getData()) {
        Map<String,Object> metadata = computation.getMetadata(datum.getKey());
        // ...
    }
```
