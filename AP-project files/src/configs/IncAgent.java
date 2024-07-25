package configs;

import graph.Agent;
import graph.Message;
import graph.Topic;
import graph.TopicManagerSingleton;

/**
 * The IncAgent class implements the Agent interface,It subscribes to a topic, increments the received value, and publishes the result to another topic.
 */
public class IncAgent implements Agent {
    /** The current value held by the agent. */
    private double value = 0;

    /** The name of the topic this agent subscribes to. */
    private final String firstTopicName;
    /** The name of the topic this agent publishes results to. */
    private final String resultTopicName;

    /** The last message received from the subscribed topic. */
    private Message msgFromFirstTopic = null;

    /**
     * Constructs a new IncAgent with specified subscription and publication topics.
     *
     * @param subs An array of topic names to subscribe to. The first element is used.
     * @param pubs An array of topic names to publish to. The first element is used.
     */
    public IncAgent(String[] subs, String[] pubs) {
        // subscribe to the first topic from subs array:
        TopicManagerSingleton.get().getTopic(subs[0]).subscribe(this);
        // update firstTopicName:
        firstTopicName = subs[0];

        // add the first publisher from pubs array:
        TopicManagerSingleton.get().getTopic(pubs[0]).addPublisher(this);
        // update resultTopicName:
        resultTopicName = pubs[0];
    }

    /**
     * Returns the name of this agent.
     *
     * @return The string "IncAgent".
     */
    @Override
    public String getName() {
        return "IncAgent";
    }

    /**
     * Resets the agent's internal state.
     */
    @Override
    public void reset() {
        this.value = 0;
    }

    /**
     * Handles incoming messages from the subscribed topic.
     * Increments the received value and publishes the result to the result topic.
     *
     * @param topic The name of the topic from which the message was received.
     * @param msg The received message.
     */
    @Override
    public void callback(String topic, Message msg) {
        // if it's the first topic, store the message
        if (topic.equals(firstTopicName)) {
            msgFromFirstTopic = msg;
        }

        if (msgFromFirstTopic != null && !Double.isNaN(msgFromFirstTopic.asDouble)) {
            // get the value from the message
            value = msgFromFirstTopic.asDouble;
            // increment the value
            value++;
            // publish the new value
            TopicManagerSingleton.get().getTopic(resultTopicName).publish(new Message(value));
        }
    }

    /**
     * Closes the agent, unsubscribing from topics and removing itself as a publisher.
     */
    @Override
    public void close() {
        // unsubscribe from the topics
        TopicManagerSingleton.TopicManager tm = TopicManagerSingleton.get();
        Topic firstTopic = tm.getTopic(firstTopicName);
        firstTopic.unsubscribe(this);

        Topic resultTopic = tm.getTopic(resultTopicName);
        resultTopic.removePublisher(this);
    }
}