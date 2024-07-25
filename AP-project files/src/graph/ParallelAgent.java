package graph;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * A parallel agent that wraps another agent and processes messages in a separate thread.
 */
public class ParallelAgent implements Agent {

    /** The agent being wrapped. */
    private Agent agent;

    /** The queue for storing messages. */
    private BlockingQueue<Message> queue;

    /** Flag indicating whether the thread is running. */
    private volatile boolean running = true;

    /** The thread that processes messages from the queue. */
    private Thread readingFromQueueThread;

    /**
     * Constructs a ParallelAgent with the specified agent and queue capacity.
     *
     * @param agent The agent to be wrapped.
     * @param capacity The capacity of the message queue.
     */
    ParallelAgent(Agent agent, int capacity) {
        this.agent = agent;
        this.queue = new ArrayBlockingQueue<>(capacity);

        // Run a thread that will take messages from the queue
        // and activate the callback method of the agent:
        this.readingFromQueueThread = new Thread(() -> {
            // While running is true, the thread will keep running
            while (this.running) {
                try {
                    // Thread will sleep until a message is available in the queue
                    Message msgFromQueue = queue.take();
                    // Split the topic from the message using the ":" separator
                    String topic = msgFromQueue.asText.split(":")[0];
                    String message = "";
                    // If the message is not empty
                    if (msgFromQueue.asText.split(":").length > 1) {
                        // Get the message from the message (if it exists)
                        message = msgFromQueue.asText.split(":")[1];
                    }

                    // Call the callback method of the agent
                    agent.callback(topic, new Message(message));
                } catch (InterruptedException e) {
                    // No need to print the exception message
                    // e.printStackTrace();
                }
            }
        });
        // Start the thread
        this.readingFromQueueThread.start();
    }

    /**
     * Gets the name of the agent.
     *
     * @return The name of the agent.
     */
    @Override
    public String getName() {
        return agent.getName();
    }

    /**
     * Resets the agent.
     */
    @Override
    public void reset() {
        agent.reset();
    }

    /**
     * Adds a message to the queue to be processed by the agent.
     *
     * @param topic The topic of the message.
     * @param msg The message to be processed.
     */
    @Override
    public void callback(String topic, Message msg) {
        try {
            // Add a message and the topic to the queue
            this.queue.put(new Message(topic + ":" + msg.asText));
        } catch (InterruptedException e) {
            // No need to print the exception message
            // e.printStackTrace();
        }
    }

    /**
     * Closes the agent and stops the processing thread.
     */
    @Override
    public void close() {
        // Change the running flag to false
        this.running = false;
        // Stop the thread from the constructor
        this.readingFromQueueThread.interrupt();
        agent.close();
    }
}
