package configs;

import graph.Agent;
import graph.Message;
import graph.Topic;
import graph.TopicManagerSingleton;
import graph.TopicManagerSingleton.TopicManager;

/**
 * The MulAgent class implements the Agent interface and performs multiplication operations
 * on messages received from two subscribed topics, publishing the result to a third topic.
 */
public class MulAgent implements Agent {
    /** The first operand for addition. */
    private double x = 0;
    /** The second operand for addition. */
    private double y = 0;

    /** The name of the first topic this agent subscribes to. */
    private final String firstTopicName;
    /** The name of the second topic this agent subscribes to. */
    private final String secondTopicName;
    /** The name of the topic this agent publishes results to. */
    private final String resultTopicName;

    /** The last message received from the first topic. */
    private Message msgFromFirstTopic = null;
    /** The last message received from the second topic. */
    private Message msgFromSecondTopic = null;

    /**
     * Constructs a new MulAgent with specified subscription and publication topics.
     *
     * @param subs An array of topic names to subscribe to. The first two elements are used.
     * @param pubs An array of topic names to publish to. The first element is used.
     */
    public MulAgent(String[] subs, String[] pubs) {
        // subscribe to the first 2 topics from subs array:
        TopicManager tm = TopicManagerSingleton.get();
        tm.getTopic(subs[0]).subscribe(this);
        tm.getTopic(subs[1]).subscribe(this);
        // update firstTopicName and secondTopicName:
        firstTopicName = subs[0];
        secondTopicName = subs[1];

        // add the first publisher from pubs array:
        tm.getTopic(pubs[0]).addPublisher(this);
        // update resultTopicName:
        resultTopicName = pubs[0];
    }

    /**
     * Returns the name of this agent.
     *
     * @return The string "MulAgent".
     */
    @Override
    public String getName() {return "MulAgent";}

    /**
     * Resets the agent's internal state.
     */
    @Override
    public void reset() {
        x = 0;
        y = 0;
    }


    /**
     * Handles incoming messages from subscribed topics.
     * When messages from both subscribed topics are received, their values are multiplied
     * and the result is published to the result topic.
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
        // if it's the second topic, store the message
        else if (topic.equals(secondTopicName)) {
            msgFromSecondTopic = msg;
        }

        // if both messages are not null, add them and publish the result
        if (msgFromFirstTopic != null && msgFromSecondTopic != null) {
            x = msgFromFirstTopic.asDouble;
            y = msgFromSecondTopic.asDouble;

            if (Double.isNaN(x) || Double.isNaN(y)) {
                return;
            }
            // publish the result
            TopicManagerSingleton.get().getTopic(resultTopicName).publish(new Message(x * y));
        }

    }

     /**
     * Closes the agent, unsubscribing from topics and removing itself as a publisher.
     */
    @Override
    public void close() {
         // unsubscribe from the topics
        TopicManager tm = TopicManagerSingleton.get();
        Topic firstTopic = tm.getTopic(firstTopicName);
        firstTopic.unsubscribe(this);

        Topic secondTopic = tm.getTopic(secondTopicName);
        secondTopic.unsubscribe(this);

        Topic resultTopic = tm.getTopic(resultTopicName);
        resultTopic.removePublisher(this);
    }
}
