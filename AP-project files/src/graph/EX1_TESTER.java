package graph;
import graph.TopicManagerSingleton.TopicManager;
import java.util.Random;

public class EX1_TESTER {

    public static void testMessage() {
        // Test String constructor
        String testString = "Hello";
        Message msgFromString = new Message(testString);
        if (!testString.equals(msgFromString.asText)) {
            System.out.println("Error: String constructor - asText does not match input string (-5)");
        }
        if (!java.util.Arrays.equals(testString.getBytes(), msgFromString.data)) {
            System.out.println("Error: String constructor - data does not match input string bytes (-5)");
        }
        if (!Double.isNaN(msgFromString.asDouble)) {
            System.out.println("Error: String constructor - asDouble should be NaN for non-numeric string (-5)");
        }
        if (msgFromString.date == null) {
            System.out.println("Error: String constructor - date should not be null (-5)");
        }

        // Test double constructor
        Double testNum = 123.45;
        Message msgNum = new Message(testNum);
        if (!testNum.toString().equals(msgNum.asText)) {
            System.out.println("Error: Double constructor - asText does not match input double (-5)");
        }
        if (!java.util.Arrays.equals(testNum.toString().getBytes(), msgNum.data)) {
            System.out.println("Error: Double constructor - data does not match input double bytes (-5)");
        }
        if (testNum != msgNum.asDouble) {
            System.out.println("Error: Double constructor - asDouble does not match input double (-5)");
        }
        if (msgNum.date == null) {
            System.out.println("Error: Double constructor - date should not be null (-5)");
        }

        // Test byte array constructor
        byte[] testBytes = new byte[]{1, 2, 3, 4, 5};
        Message msgBytes = new Message(testBytes);
        if (!new String(testBytes).equals(msgBytes.asText)) {
            System.out.println("Error: Byte array constructor - asText does not match input bytes (-5)");
        }
        if (!java.util.Arrays.equals(testBytes, msgBytes.data)) {
            System.out.println("Error: Byte array constructor - data does not match input bytes (-5)");
        }
        if (!Double.isNaN(msgBytes.asDouble)) {
            System.out.println("Error: Byte array constructor - asDouble should be NaN for non-numeric bytes (-5)");
        }
        if (msgBytes.date == null) {
            System.out.println("Error: Byte array constructor - date should not be null (-5)");
        }
    }

    public static abstract class AAgent implements Agent {
        public void reset() {}
        public void close() {}
        public String getName() {
            return getClass().getName();
        }
    }

    public static class TestAgent1 extends AAgent {
        double sum = 0;
        int count = 0;
        TopicManager tm = TopicManagerSingleton.get();

        public TestAgent1() {
            tm.getTopic("Numbers").subscribe(this);
        }

        @Override
        public void callback(String topic, Message msg) {
            count++;
            sum += msg.asDouble;

            if (count % 5 == 0) {
                tm.getTopic("Sum").publish(new Message(sum));
                count = 0;
            }
        }
    }

    public static class TestAgent2 extends AAgent {
        double sum = 0;
        TopicManager tm = TopicManagerSingleton.get();

        public TestAgent2() {
            tm.getTopic("Sum").subscribe(this);
        }

        @Override
        public void callback(String topic, Message msg) {
            sum = msg.asDouble;
        }

        public double getSum() {
            return sum;
        }
    }

    public static void testAgents() {
        TopicManager tm = TopicManagerSingleton.get();
        TestAgent1 a = new TestAgent1();
        TestAgent2 a2 = new TestAgent2();
        double sum = 0;
        for (int c = 0; c < 3; c++) {
            Topic num = tm.getTopic("Numbers");
            Random r = new Random();
            for (int i = 0; i < 5; i++) {
                int x = r.nextInt(-1000000, 1000000);
                num.publish(new Message(x));
                sum += x;
            }
            double result = a2.getSum();
            if (result != sum) {
                System.out.println("Error: Your code published a wrong result (-10)");
            }
        }
        a.close();
        a2.close();
    }

    public static void testTopic() {
        TopicManager tm = TopicManagerSingleton.get();

        // Test topic creation and retrieval
        Topic topic1 = tm.getTopic("Topic1");
        if (!"Topic1".equals(topic1.getName())) {
            System.out.println("Error: Topic name does not match (-5)");
        }

        // Test subscribe and unsubscribe
        TestAgent1 agent = new TestAgent1();
        topic1.subscribe(agent);
        if (!topic1.getSubscribers().contains(agent)) {
            System.out.println("Error: Agent was not subscribed (-5)");
        }

        topic1.unsubscribe(agent);
        if (topic1.getSubscribers().contains(agent)) {
            System.out.println("Error: Agent was not unsubscribed (-5)");
        }

        // Test addPublisher and removePublisher
        topic1.addPublisher(agent);
        if (!topic1.getPublishers().contains(agent)) {
            System.out.println("Error: Agent was not added as publisher (-5)");
        }

        topic1.removePublisher(agent);
        if (topic1.getPublishers().contains(agent)) {
            System.out.println("Error: Agent was not removed as publisher (-5)");
        }

        // Test publish and getLastMessage
        Message testMessage = new Message("Test Message");
        topic1.publish(testMessage);
        if (!testMessage.equals(topic1.getLastMessage())) {
            System.out.println("Error: Last message does not match published message (-5)");
        }
    }

    public static void main(String[] args) {
        testMessage();
        testAgents();
        testTopic();
        System.out.println("done");
    }
}


