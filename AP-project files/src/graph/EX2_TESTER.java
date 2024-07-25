package graph;
import graph.TopicManagerSingleton.TopicManager;
import java.util.concurrent.atomic.AtomicInteger;

public class EX2_TESTER {
    static String tn = null;
    static AtomicInteger counter = new AtomicInteger(0);

    public static class TestAgent1 implements Agent {
        @Override
        public void reset() {
        }

        @Override
        public void close() {
        }

        @Override
        public String getName() {
            return getClass().getName();
        }

        @Override
        public void callback(String topic, Message msg) {
            tn = Thread.currentThread().getName();
            counter.incrementAndGet();
        }
    }

    public static void testParallelAgentBasic() {
        TopicManager tm = TopicManagerSingleton.get();
        int tc = Thread.activeCount();
        ParallelAgent pa = new ParallelAgent(new TestAgent1(), 10);
        tm.getTopic("A").subscribe(pa);

        if (Thread.activeCount() != tc + 1) {
            System.out.println("your ParallelAgent does not open a thread (-10)");
        }

        tm.getTopic("A").publish(new Message("a"));
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }

        if (tn == null) {
            System.out.println("your ParallelAgent didn't run the wrapped agent callback (-20)");
        } else {
            if (tn.equals(Thread.currentThread().getName())) {
                System.out.println("the ParallelAgent does not run the wrapped agent in a different thread (-10)");
            }
            String last = tn;
            tm.getTopic("A").publish(new Message("a"));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            if (!last.equals(tn))
                System.out.println("all messages should be processed in the same thread of ParallelAgent (-10)");
        }

        pa.close();
        System.out.println("done");
    }

    public static void testParallelAgentEdgeCases() {
        TopicManager tm = TopicManagerSingleton.get();
        ParallelAgent pa = new ParallelAgent(new TestAgent1(), 10);
        tm.getTopic("Edge").subscribe(pa);

        // Test with empty message
        tm.getTopic("Edge").publish(new Message(""));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        //
        if (counter.get() != 3) {
            System.out.println("ParallelAgent did not process empty message (-5)");
        }

        // Test with long message
        String longMessage = new String(new char[1000]).replace("\0", "a");
        tm.getTopic("Edge").publish(new Message(longMessage));
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
        }
        if (counter.get() != 4) {
            System.out.println("ParallelAgent did not process long message (-5)");
        }

        // Test with special characters
        tm.getTopic("Edge").publish(new Message("!@#$%^&*()_+"));
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
        }
        if (counter.get() != 5) {
            System.out.println("ParallelAgent did not process special characters message (-5)");
        }

        pa.close();
    }

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

        System.out.println("Message tests done");
    }

    public static void testTopic() {
        Topic topic = new Topic("TestTopic");
        if (!"TestTopic".equals(topic.getName())) {
            System.out.println("Error: Topic name does not match (-5)");
        }

        TestAgent1 agent = new TestAgent1();
        topic.subscribe(agent);
        if (topic.getSubscribers().size() != 1) {
            System.out.println("Error: Agent not subscribed correctly (-5)");
        }

        topic.unsubscribe(agent);
        if (topic.getSubscribers().size() != 0) {
            System.out.println("Error: Agent not unsubscribed correctly (-5)");
        }

        topic.addPublisher(agent);
        if (topic.getPublishers().size() != 1) {
            System.out.println("Error: Publisher not added correctly (-5)");
        }

        topic.removePublisher(agent);
        if (topic.getPublishers().size() != 0) {
            System.out.println("Error: Publisher not removed correctly (-5)");
        }

        Message msg = new Message("test");
        topic.publish(msg);
        if (!msg.equals(topic.getLastMessage())) {
            System.out.println("Error: Last message not updated correctly (-5)");
        }

        System.out.println("Topic tests done");
    }

    public static void main(String[] args) {
        testParallelAgentBasic();
        testParallelAgentEdgeCases();
//        testMessage();
//        testTopic();
        System.out.println("All tests done");
    }
}