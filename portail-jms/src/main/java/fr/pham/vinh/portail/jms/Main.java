package fr.pham.vinh.portail.jms;


import fr.pham.vinh.jms.commons.JmsPullPush;

/**
 * Created by Vinh PHAM on 11/03/2017.
 */
public class Main {

    public static void main(String args[]) {
        new JmsPullPush().start();
    }

}
