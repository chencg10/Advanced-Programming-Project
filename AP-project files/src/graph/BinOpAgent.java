package graph;

import java.util.function.BinaryOperator;
import graph.TopicManagerSingleton.TopicManager;

/**
 * The BinOpAgent class implements the Agent interface to perform binary operations
 * on messages received from two subscribed topics and publish the result to a third topic.
 */
public class BinOpAgent implements Agent {
    /** The name of the agent. */
    private final String name;
    /** The binary operator to be applied to the received messages. */
    private final BinaryOperator<Double> operator;
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
     * Constructs a new BinOpAgent with specified name, topics, and binary operator.
     *
     * @param name The name of the agent.
     * @param firstTopicName The name of the first topic to subscribe to.
     * @param secondTopicName The name of the second topic to subscribe to.
     * @param resultTopicName The name of the topic to publish results to.
     * @param operator The binary operator to apply to received messages.
     */
    public BinOpAgent(String name, String firstTopicName, String secondTopicName, String resultTopicName, BinaryOperator<Double> operator) {
        this.name = name;
        this.operator = operator;
        this.firstTopicName = firstTopicName;
        this.secondTopicName = secondTopicName;
        this.resultTopicName = resultTopicName;

        // subscribe to the first topic
        TopicManager tm = TopicManagerSingleton.get();
        Topic firstTopic = tm.getTopic(firstTopicName);
        firstTopic.subscribe(this);

        // subscribe to the second topic
        Topic secondTopic = tm.getTopic(secondTopicName);
        secondTopic.subscribe(this);

        // add publisher to the result topic
        Topic resultTopic = tm.getTopic(resultTopicName);
        resultTopic.addPublisher(this);
    }

    /**
     * Returns the name of this agent.
     *
     * @return The name of the agent.
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Resets the agent's internal state by setting both input messages to 0.
     */
    @Override
    public void reset() {
        // reset inputs to 0
        msgFromFirstTopic = new Message(0);
        msgFromSecondTopic = new Message(0);
    }

    /**
     * Handles incoming messages from subscribed topics.
     * When messages from both subscribed topics are received, applies the binary operator
     * and publishes the result to the result topic.
     *
     * @param topic The name of the topic from which the message was received.
     * @param msg The received message.
     */
    @Override
    public void callback(String topic, Message msg) {
        // first identify which topic the message is from and store the message
        if (topic.equals(firstTopicName)) {
            // store the first message
            msgFromFirstTopic = msg;
        } else if (topic.equals(secondTopicName)) {
            // store the second message
            msgFromSecondTopic = msg;
        }
        // check if the two messages from the two topics are double values
        if (msgFromFirstTopic != null && msgFromSecondTopic != null) {
            // check that the messages are double values and not null
            double firstValue = msgFromFirstTopic.asDouble;
            double secondValue = msgFromSecondTopic.asDouble;

            if (Double.isNaN(firstValue) || Double.isNaN(secondValue)) {
                return;
            }
            // calculate the result of the binary operator
            double result = operator.apply(firstValue, secondValue);

            // publish the result to the result topic
            TopicManager tm = TopicManagerSingleton.get();
            Topic resultTopic = tm.getTopic(resultTopicName);
            resultTopic.publish(new Message(result));
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