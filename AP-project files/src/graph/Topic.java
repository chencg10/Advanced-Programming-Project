package graph;

import java.util.ArrayList;

/**
 * Represents a topic that agents can subscribe to or publish messages on.
 */
public class Topic {

    /** The name of the topic. */
    public final String name;

    /** List of subscribers to the topic. */
    private ArrayList<Agent> subs;

    /** List of publishers to the topic. */
    private ArrayList<Agent> pubs;

    /** The last message published to the topic. */
    private Message lastMessage = new Message(0);

    /**
     * Constructs a Topic with the specified name.
     *
     * @param name The name of the topic.
     */
    Topic(String name) {
        this.name = name;
        this.subs = new ArrayList<>();
        this.pubs = new ArrayList<>();
    }

    /**
     * Gets the name of the topic.
     *
     * @return The name of the topic.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Subscribes an agent to this topic.
     *
     * @param agent The agent to subscribe.
     */
    public void subscribe(Agent agent) {
        subs.add(agent);
    }

    /**
     * Unsubscribes an agent from this topic.
     *
     * @param agent The agent to unsubscribe.
     */
    public void unsubscribe(Agent agent) {
        subs.remove(agent);
    }

    /**
     * Publishes a message to all subscribers of this topic.
     *
     * @param msg The message to be published.
     */
    public void publish(Message msg) {
        // Set the last message using the message passed in
        lastMessage = msg;

        for (Agent agent : subs) {
            agent.callback(this.name, msg);
        }
    }

    /**
     * Adds a publisher to this topic.
     *
     * @param agent The agent to add as a publisher.
     */
    public void addPublisher(Agent agent) {
        pubs.add(agent);
    }

    /**
     * Removes a publisher from this topic.
     *
     * @param agent The agent to remove as a publisher.
     */
    public void removePublisher(Agent agent) {
        pubs.remove(agent);
    }

    /**
     * Gets the list of subscribers to this topic.
     *
     * @return The list of subscribers.
     */
    public ArrayList<Agent> getSubscribers() {
        return subs;
    }

    /**
     * Gets the list of publishers to this topic.
     *
     * @return The list of publishers.
     */
    public ArrayList<Agent> getPublishers() {
        return pubs;
    }

    /**
     * Gets the last message published to this topic.
     *
     * @return The last message.
     */
    public Message getLastMessage() {
        return lastMessage;
    }
}
