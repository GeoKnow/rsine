package at.punkt.lod2.util;

import java.util.Collection;

import eu.lod2.rsine.dissemination.notifier.INotifier;

public class CountingNotifier implements INotifier {

  private int notificationCount = 0;

  public synchronized void notify(Collection<String> messages) {
    notificationCount++;
  }

  public synchronized int getNotificationCount() {
    return notificationCount;
  }

}
