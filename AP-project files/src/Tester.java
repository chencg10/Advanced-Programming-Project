//import graph.*;
//
//
//public class Tester {
//
//    public static void main(String[] args)
//    {
//        Message m = new Message("4.72");
//        TopicManagerSingleton.TopicManager tm = TopicManagerSingleton.get();
//        Topic t = tm.getTopic("test");
//        Agent a = new Agent() {
//            @Override
//            public String getName() {
//                return "test";
//            }
//
//            @Override
//            public void reset() {
//            }
//
//            @Override
//            public void callback(String topic, Message msg) {
//                System.out.println("Received message: " + msg);
//            }
//
//            @Override
//            public void close() {
//            }
//        };
//        t.subscribe(a);
//        t.unsubscribe(a);
//        t.publish(new Message("test"));
//        t.addPublisher(a);
//        t.removePublisher(a);
//
//        // test clear
//        Topic t2 = tm.getTopic("test2");
//        t2.subscribe(a);
//        tm.clear();
//
//    }
//}
