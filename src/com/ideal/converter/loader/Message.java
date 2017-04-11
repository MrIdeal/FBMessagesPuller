package com.ideal.converter.loader;

import java.io.File;

/**
 * The data represenation of a message.
 *
 * @author Steven Frizell on 3/26/17
 * @since 1.0
 * @version 1.0
 */
public class Message implements Comparable<Message> {

    /**
     * The user that sent the message.
     */
    private String user;

    /**
     * The content of the message.
     */
    private String content;

    /**
     * An image that may be associated with this message.
     */
    private File image;

    /**
     * A video that may be associated with this message.
     */
    private File video;

    /**
     * The date the message was sent.
     */
    private String date;

    /**
     * The time the message was sent.
     */
    private String time;

    /**
     * Constructor.
     */
    public Message(final String user) {
        this.user = user;
    }

    /**
     * Sets the date the message was sent.
     * @param date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Sets the time the message was sent.
     * @param time
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * Sets the content of the message, if it was a text message.
     * @param message
     */
    public void setContent(String message) {
        content = message;
    }

    /**
     * Sets the image of the message, if it was a photo message.
     * @param imgLoc
     */
    public void setImage(File imgLoc) {
        image = imgLoc;
    }

    /**
     * Sets the video of the message, if it was a video message.
     * @param vidLoc
     */
    public void setVideo(File vidLoc) {
        video = vidLoc;
    }


    public String getUser() {
        return user;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getContent() {
        return content;
    }

    /**
     * TODO
     * @param o
     * @return
     */
    @Override
    public int compareTo(Message o) {
        if (this == o) {
            return 1;
        } else {
            return 0;
        }
    }
}
