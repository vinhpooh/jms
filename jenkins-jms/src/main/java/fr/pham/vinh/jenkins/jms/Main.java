package fr.pham.vinh.jenkins.jms;

import fr.pham.vinh.jms.commons.JmsPushPull;

/**
 * Created by Vinh PHAM on 11/03/2017.
 */
public class Main {

    public static void main(String args[]) {
        // Create the message
        // TODO : créer la requête
        String request = "{" +
                "type:\"request\"," +
                "application:\"DALI\"," +
                "environment:\"PIC\"" +
                "}";

        // Execute a push pull
        JmsPushPull jenkins = new JmsPushPull(10 * 1000, "admin", "admin123");
        String response = jenkins.run(request);

        System.out.println(response);
    }

}
