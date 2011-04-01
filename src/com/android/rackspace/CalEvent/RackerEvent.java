package com.android.rackspace.CalEvent;

import java.util.*;

public class RackerEvent {

  private Date startTime;
  private Date stopTime;
  private String description;
  private String summary;
  private String location;

  RackerEvent(Date start, Date stop, String desc, String summ, String loc) {
    startTime = start;
    stopTime = stop;
    description = desc;
    summary = summ;
    location = loc;
  }

  Date getStartTime() {
    return startTime;
  }

  Date getStopTime() {
    return stopTime;
  }

  String getDescription() {
    return description;
  }

  String getSummary() {
    return summary;
  }

  String getLocation() {
    return location;
  }
}
