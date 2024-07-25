package graph;

/**
 * The Agent interface defines the structure for agent objects in a messaging system.
 * Agents can receive messages from topics, process them, and perform actions.
 */
public interface Agent {

    /**
     * Retrieves the name of the agent.
     *
     * @return A String representing the name of the agent.
     */
    String getName();

    /**
     * Resets the agent to its initial state.
     * This method should clear any stored data and return the agent to its default configuration.
     */
    void reset();

    /**
     * Handles incoming messages from subscribed topics.
     * This method is called when a message is received on a topic the agent is subscribed to.
     *
     * @param topic The name of the topic from which the message was received.
     * @param msg The Message object containing the received data.
     */
    void callback(String topic, Message msg);

    /**
     * Performs cleanup operations and releases resources held by the agent.
     * This method should be called when the agent is no longer needed or when shutting down the system.
     */
    void close();
}