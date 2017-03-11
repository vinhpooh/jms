package fr.pham.vinh.jms;

import fr.pham.vinh.jms.push.pull.JmsPushPull;

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
        String response = new JmsPushPull().start(request);

        System.out.println(response);
    }

}
