package configs;

/**
 * The Config interface defines the structure for configuration objects.
 * Implementations of this interface should provide methods for creating,
 * naming, versioning, and closing configurations.
 */
public interface Config {

    /**
     * Creates or initializes the configuration.
     * This method should set up any necessary resources or data structures.
     */
    void create();

    /**
     * Retrieves the name of the configuration.
     *
     * @return A String representing the name of the configuration.
     */
    String getName();

    /**
     * Retrieves the version of the configuration.
     *
     * @return An integer representing the version number of the configuration.
     */
    int getVersion();

    /**
     * Closes the configuration and performs any necessary cleanup.
     * This method should release any resources held by the configuration.
     */
    void close();
}