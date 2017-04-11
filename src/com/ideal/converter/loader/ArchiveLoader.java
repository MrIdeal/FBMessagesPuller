package com.ideal.converter.loader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Class ArchiveLoader loads the specified facebook archive. The archive must not be in a .zip/.rar or equivalent file,
 * but must be stored in a folder. The user will specify the location of the archive and the converter application will
 * begin it's work from there.
 * <br>
 * This will only load actual text messages. Through testing and reading through the messages html file, messenger photos aren't
 * downloaded/saved so there will be blank messages.
 * <br>
 * This is split into two different classes because I had ideas for something else, but I decided to just upload it
 * like this.
 *
 * @author Steven Frizell on 3/26/17
 * @since 1.0
 * @version 1.0
 */
public class ArchiveLoader {

    /**
     * The location of the FB archive.
     * The original archive .zip file must be extracted to
     * a folder. The process will not work if the data is still in a .zip file!
     */
    private final File archiveLocation;

    /**
     * The HTML document. This must be the messages file.
     */
    private Document doc;

    /**
     * A list of all the users in the doc.
     */
    private final List<String> allUsers;

    /**
     * Constructs a ArchiveLoader instance with the archive directory specified and parses the messages.html
     * file into a Document instance.
     * @param loc
     */
    public ArchiveLoader(File loc) {

        archiveLocation = loc;

        try {
            doc = Jsoup.parse(archiveLocation, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        allUsers = loadAllUsers();
    }

    /**
     * This will load all the messages between the FB archive owner and a specified person.
     * Specifying multiple people will produce a random conversation timeline.
     * It is best to only use the same people in both arrays.
     * <br>
     * It should be noted the messages do not end up in 100% order by date/time. For some reason,
     * the html file stores everything in chunks by month/year. The messages themselves should be in order,
     * but in chunks.
     * <br>
     * This algorithm should produce all the messages between two people. It could be off by a few messages.
     * <br>
     * Arrays are used and iterated through because FB stores the different usernames. I myself had two different usernames saved.
     * @param fromPerson The message be sent from a recipient, not the FB archive owner.
     * @param receiver The message being sent from the FB archive owner.
     * @return Returns a list of messages that has all the relevant information needed.
     */
    public List<Message> loadMessagesToFrom(String[] fromPerson, String[] receiver) {

        //A list of messages added.
        List<Message> messagesList = new ArrayList<Message>();

        Elements elements = doc.select("div.message"); //get message user names/message headers
        Elements messages = doc.select("p"); //get the actual message. This should be the same size as elements

        /**
         * This is used to determine if we should add the next receiver message available.
         * If from is false, the next receiver message is assumed to be going to a different fromPerson.
         */
        boolean from = false;

        for (int i = 0; i < elements.size(); i++) {
            Document eDoc = Jsoup.parse(elements.get(i).html()); //Parses the element html into a separate doc for easier info retrieval
            Document mDoc = Jsoup.parse(messages.get(i).html()); //Same situation as above

            String user = eDoc.getElementsByClass("user").get(0).text(); //Gets the user
            String lower = user.toLowerCase();

            //If a message isn't added this remains false for this iteration
            boolean added = false;

            //Iterate through fromPerson array
            for (int a = 0; a < fromPerson.length; a++) {
                if (fromPerson[a].toLowerCase().equals(lower)) {
                    from = true;
                    added = true;
                    Message sms = new Message(user); //New message instance
                    String[] date = parseDate(eDoc.getElementsByClass("meta").get(0).text()); //Gets the date
                    String message = mDoc.text(); //Gets the message

                    sms.setDate(date[0]); //Set date
                    sms.setTime(date[1]); //Set time
                    sms.setContent(message); //Set message
                    messagesList.add(sms); //Add to list
                }
            }

            //Iterate through the receiver array
            for (int b = 0; b < receiver.length; b++) {
                if (receiver[b].toLowerCase().equals(lower)) {
                    if (from) {
                        added = true;
                        Message sms = new Message(user); //New message instance
                        String[] date = parseDate(eDoc.getElementsByClass("meta").get(0).text()); //Gets the date
                        String message = mDoc.text(); //Gets the message

                        sms.setDate(date[0]); //Set date
                        sms.setTime(date[1]); //Set time
                        sms.setContent(message); //Set message
                        messagesList.add(sms); //Add to list
                    }
                }
            }
            /*
             * This tests if a fromPerson message was added to the messages list.
             * If it is false, the next message is assumed to from a different fromPerson or to a different fromPerson.
             */
            if (!added) {
                from = false;
            }
        }

        /**
         * Reverse the messages so they're in semi order.
         *
         */
        Collections.reverse(messagesList);

        File file = new File("test.txt");

        PrintWriter print = null;
        try {
            print = new PrintWriter(new FileWriter(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < messagesList.size(); i++) {
            Message m = messagesList.get(i);

            print.println(m.getUser());
            print.println(m.getDate() + " at " + m.getTime());
            print.println(m.getContent());
            print.println("");
        }

        print.println("");
        print.println(messagesList.size() + " " + "messages.");

        print.close();

        return messagesList;
    }

    /**
     * Loads meta data.
     * The meta data in the messages.html file is the date headers for messages.
     * @return
     */
    public List<String> loadMetaData() {

        List<String> metaData = new ArrayList<String>();

        Elements metas = doc.getElementsByClass("meta");

        for (int i = 0; i < metas.size(); i++) {
            Element e = metas.get(i);

            if (e.hasText()) {
                String text = e.text();

                if (!metaData.contains(text)) {
                    metaData.add(text);
                }
             } else {
                System.out.println("No text");
            }
        }

        Collections.sort(metaData);

        Iterator<String> it = metaData.iterator();

        while (it.hasNext()) {
            System.out.println(it.next());
        }

        return metaData;
    }

    /**
     * This will load all the users that have sent/received messages in the fB archive.
     * The FB archive owner will also show up in here.
     * @return A list of all the users residing in the messages file.
     */
    public List<String> loadAllUsers() {
        List<String> users = new ArrayList<String>();

        Elements spans = doc.getElementsByClass("user"); //all user elements

        for (int i = 0; i < spans.size(); i++) {
            Element e = spans.get(i);

            if (e.hasText()) {
               String text = e.text();

               if (!users.contains(text)) {
                   users.add(text);
               }
            } else {
                System.out.println("No text");
            }
        }

        Collections.sort(users);
        return users;
    }

    /**
     * Main
     * @param args
     */
    public static void main(String args[]) {
        String f = File.separator;
        ArchiveLoader load =
                new ArchiveLoader(new File("C:" + f + "Users" + f + "Steven Frizell" + f + "Desktop" + f + "Facebook Archive" + f + "html" + f + "messages.htm"));

        //call loadMessagesToFrom here with the respective arrays specified.
    }

    /**
     * Parses the date to split it the time from alpha date/month/numeric date/year.
     * @param date The date we need to split.
     * @return The date and time in a string array.
     */
    private String[] parseDate(String date) {
        String[] params = new String[2];

        int lastIndex = date.toLowerCase().lastIndexOf("at");

        params[0] = date.substring(0, lastIndex - 1);
        params[1] = date.substring(lastIndex + 3, date.length());
        return params;
    }
}
