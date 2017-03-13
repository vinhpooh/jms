package fr.pham.vinh.jenkins.jms;

import fr.pham.vinh.jms.commons.JmsPushPull;

/**
 * Created by Vinh PHAM on 11/03/2017.
 */
public class JenkinsJms extends JmsPushPull {

    /**
     * Default constructor.
     *
     * @param timeout  the timeout in ms to use
     * @param user     the user to use
     * @param password the password to use
     */
    public JenkinsJms(int timeout, String user, String password) {
        super(timeout, user, password);
    }

    public static void main(String args[]) {
        // Create the message
        // TODO : créer la requête
        String request = "{" +
                "type:\"request\"," +
                "application:\"DALI\"," +
                "environment:\"PIC\"" +
                "}";

        // Execute a push pull
        JenkinsJms jenkins = new JenkinsJms(10 * 1000, "admin", "admin123");
        String response = jenkins.process(request);

        System.out.println(response);
    }

}
