package test;

import java.util.ArrayList;

public class Topic  {
    // the topic name
    public final String name;
    // list of subscribers
    private ArrayList<Agent> subs;
    // list of publishers
    private ArrayList<Agent> pubs ;

    // Constructor
    Topic(String name) {
        this.name = name;
        this.subs = new ArrayList<>();
        this.pubs = new ArrayList<>();
    }

    // subscribe an agent to this list
    public void subscribe(Agent agent) {
        subs.add(agent);
    }

    // unsubscribe an agent from this list
    public void unsubscribe(Agent agent) {
        subs.remove(agent);
    }

    // publish a message to all subscribers
    public void publish(Message msg) {
        for (Agent agent : subs) {
            agent.callback(this.name, msg);
        }
    }

    // add a publisher to this list
    public void addPublisher(Agent agent) {
        pubs.add(agent);
    }

    // remove a publisher from this list
    public void removePublisher(Agent agent) {
        pubs.remove(agent);
    }
}
