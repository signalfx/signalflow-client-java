import com.signalfx.signalflow.client.ChannelMessage;
import com.signalfx.signalflow.client.Computation;
import com.signalfx.signalflow.client.SignalFlowClient;
import com.signalfx.signalflow.client.SignalFlowTransport;
import com.signalfx.signalflow.client.WebSocketTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Example {
  private static final Logger logger = LoggerFactory.getLogger(Example.class);

  public static void main(String[] args) {
    String token = readOption("SPLUNK_ACCESS_TOKEN", null);
    String realm = readOption("SPLUNK_REALM", "us0");

    if (token == null || token.isBlank()) {
      logger.error("SPLUNK_ACCESS_TOKEN not provided, exiting");
      return;
    }

    logger.info("Using realm {}", realm);

    SignalFlowTransport transport = new WebSocketTransport.TransportBuilder(token)
        .setHost("stream." + realm + ".signalfx.com").build();

    String program = "data('cpu.utilization').publish()";
    long now = System.currentTimeMillis();

    try (SignalFlowClient client = new SignalFlowClient(transport)) {
      Computation computation = client.execute(program, now - 60000, now + 60000, null, null, null, null, null);

      computation.forEach((message) -> {
        if (message instanceof ChannelMessage.DataMessage dataMessage) {
          dataMessage.getData().forEach((key, value) -> {
            System.out.println("received: " + key + " = " + value);
          });
        }
      });
    } catch (Exception e) {
      logger.error("Exception from SignalFlow client", e);
    }
  }

  private static String readOption(String name, String defaultValue) {
    String property = System.getProperty(name);
    if (property != null) {
      return property;
    }
    String env = System.getenv(name);
    if (env != null) {
      return env;
    }
    return defaultValue;
  }
}
