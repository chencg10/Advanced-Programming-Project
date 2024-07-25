package test;

import java.util.concurrent.ConcurrentHashMap;

public class TopicManagerSingleton {

    public static class TopicManager {
        // static instance of TopicManager
        private static final TopicManager instance = new TopicManager();

        private ConcurrentHashMap<String, Topic> topics = new ConcurrentHashMap<>();
        // private constructor
        private TopicManager() {}

        // get a topic by name
        public Topic getTopic(String name) {
            return topics.computeIfAbsent(name, Topic::new);
        }

        // get topics method
        public ConcurrentHashMap<String, Topic> getTopics() {
            // return a copy of the topics
            //return new ConcurrentHashMap<>(topics); ???
            return topics;
        }

        // clear all topics from the hashmap
        public void clear() {
            topics.clear();
        }
    }

    // get the instance of TopicManager
    public static TopicManager get() {
        return TopicManager.instance;
    }

}
