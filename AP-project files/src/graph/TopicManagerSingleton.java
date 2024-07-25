package graph;

import java.util.concurrent.ConcurrentHashMap;

/**
 * A singleton class for managing topics.
 */
public class TopicManagerSingleton {

    /**
     * Inner static class for the TopicManager which handles the topics.
     */
    public static class TopicManager {

        /** Static instance of TopicManager. */
        private static final TopicManager instance = new TopicManager();

        /** A concurrent hashmap to store topics by name. */
        private ConcurrentHashMap<String, Topic> topics = new ConcurrentHashMap<>();

        /** Private constructor to prevent instantiation. */
        private TopicManager() {}

        /**
         * Gets a topic by its name, creating it if it does not exist.
         *
         * @param name The name of the topic.
         * @return The topic with the specified name.
         */
        public Topic getTopic(String name) {
            return topics.computeIfAbsent(name, Topic::new);
        }

        /**
         * Gets the map of topics.
         *
         * @return The concurrent hashmap of topics.
         */
        public ConcurrentHashMap<String, Topic> getTopics() {
            return topics;
        }

        /**
         * Clears all topics from the map.
         */
        public void clear() {
            topics.clear();
        }

        /**
         * Gets the names of all topics.
         *
         * @return An array of topic names.
         */
        public String[] getTopicsNames() {
            return topics.keySet().toArray(new String[0]);
        }
    }

    /**
     * Gets the singleton instance of TopicManager.
     *
     * @return The singleton instance of TopicManager.
     */
    public static TopicManager get() {
        return TopicManager.instance;
    }
}
