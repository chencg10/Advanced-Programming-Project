package graph;

import configs.Config;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The GenericConfig class implements the Config interface to create and manage
 * a configuration of agents based on a configuration file.
 */
public class GenericConfig implements Config {
    /** List to store lines read from the configuration file. */
    private List<String> lines;
    /** List to store created agents. */
    private List<Agent> agents = new ArrayList<>();

    /**
     * Sets the configuration file path and reads its contents.
     *
     * @param file_path The path to the configuration file.
     */
    public void setConfFile(String file_path) {
        try {
            // read all the lines from the file
            lines = Files.readAllLines(Paths.get(file_path));
        } catch (IOException e) {
            // e.printStackTrace();
        }
    }

    /**
     * Creates agents based on the configuration file contents.
     * Each agent is defined by three consecutive lines in the file:
     * agent name, subscriptions, and publications.
     */
    @Override
    public void create() {

        // if the length of the lines is not divisible by 3 do nothing
        if (lines.size() % 3 != 0) {
            return;
        }

        // iterate over the lines and for each 3 lines, create the proper agent
        for (int i = 0; i < lines.size(); i += 3) {
            // first line is agent full name
            String agentName = lines.get(i);
            // filter out the project name
            agentName = agentName.substring(agentName.indexOf('.') + 1);
            // second line is agent subscriptions
            String[] subs = lines.get(i + 1).split(",");
            // third line is agent publications
            String[] pubs = lines.get(i + 2).split(",");

            // use Class<?> to create the agent
            Agent temp = null;
            try {
                Class<?> agentClass = Class.forName(agentName);
                // call the right constructor using the agent class name
                temp = (Agent) agentClass.getConstructor(String[].class, String[].class).newInstance((Object) subs, (Object) pubs);
            } catch (Exception e) {
                //e.printStackTrace();
            }

            // wrap it with parallel agent and add the agent to the list of agents
            if (temp != null) {
                agents.add(new ParallelAgent(temp, 10));
            }
        }
    }

    /**
     * Returns the name of this configuration.
     *
     * @return The string "GenericConfig".
     */
    @Override
    public String getName() {
        return "GenericConfig";
    }

    /**
     * Returns the version of this configuration.
     *
     * @return The version number, currently 0.
     */
    @Override
    public int getVersion() {
        return 0;
    }

    /**
     * Closes all agents created by this configuration.
     */
    @Override
    public void close() {
        // close all the agents
        for (Agent agent : agents) {
            agent.close();
        }
    }
}