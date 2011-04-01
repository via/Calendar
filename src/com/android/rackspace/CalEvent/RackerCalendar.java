package com.android.rackspace.CalEvent;

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;


public class RackerCalendar {

  private String urlString;
  private LinkedList eventList = null;


  RackerCalendar(String url) {
    urlString = url;
    eventList = new LinkedList();
  }

  public LinkedList getEvents() {
    return eventList;
  }

  private RackerEvent parseEvent(String start, String stop,
      String desc, String summ, String loc) {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'kkmmss");

    String[] startsplit = start.split(":");
    String[] stopsplit = stop.split(":");
    Date startDate;
    Date stopDate;

    try {
      startDate = sdf.parse(startsplit[startsplit.length - 1]);
      stopDate = sdf.parse(stopsplit[stopsplit.length - 1]);
    } catch (ParseException e) {
      System.out.println("Failed to parse");
      return null;
    }

    RackerEvent re = new RackerEvent(startDate, stopDate, desc.substring(11),
        summ.substring(8), loc.substring(9));

    return re;


  }

  public int getFeed() {

    URL url;
    HttpURLConnection conn = null;
    BufferedReader br = null;

    Boolean inEvent = false;
    String start = null;
    String stop = null; 
    String desc = null; 
    String loc = null;
    String summ = null;
    LinkedList eventList = new LinkedList();

    try {
      url = new URL(urlString);
      conn= (HttpURLConnection)url.openConnection();
      InputStream istream = conn.getInputStream();

      br = new BufferedReader(new InputStreamReader(istream));

      String line = null;
      while ((line = br.readLine()) != null) {
        if (!inEvent) {
          if (line.equals("BEGIN:VEVENT")) {
            inEvent = true;
            start = stop = desc = loc = summ = null;
          }
        } else {
          if (line.equals("END:VEVENT")) {
            inEvent = false;
            RackerEvent re = parseEvent(start, stop, desc, summ, loc);
            if (re != null) {
              eventList.add(re);
            }
          } else if (line.startsWith("DTSTART")) {
            start = line;
          } else if (line.startsWith("DTEND")) {
            stop = line;
          } else if (line.startsWith("DESCRIPTION")) {
            desc = line;
          } else if (line.startsWith("LOCATION")) {
            loc = line;
          } else if (line.startsWith("SUMMARY")) {
            summ = line;
          }
        }
      }

      this.eventList = eventList;

      return 0;

    } catch (Exception e) {

      System.out.println("Oh no!");
      return -1;

    }


  }

  public RackerEvent getCurrent() {

    for (Object o : eventList) {
      RackerEvent re = (RackerEvent)o;
      Date start = re.getStartTime();
      Date stop = re.getStopTime();
      Date now = new Date();

      if (now.after(start) && now.before(stop)) {
        return re;
      }
    }
    return null;
  }


  public static void main(String[] args) {
    RackerCalendar rc = new RackerCalendar("http://apps.rackspace.com/a/feeds/ical/starwars@mailtrust.com/3::personal::633811/public/");

    rc.getFeed();

    LinkedList blah = rc.getEvents();

    for (Object o : blah) {
      RackerEvent  re = (RackerEvent)o;
      System.out.println(re.getStartTime().toString() + " to " + re.getStopTime().toString());
    }

    RackerEvent current = rc.getCurrent();

    if (current != null) {
      System.out.println("Current Event: " + current.getSummary() + " " +current.getLocation());
    }


  }

}



