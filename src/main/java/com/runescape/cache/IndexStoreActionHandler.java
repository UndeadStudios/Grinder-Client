package com.runescape.cache;

import com.grinder.client.util.Log;
import com.runescape.clock.Time;
import com.runescape.collection.NodeDeque;

public class IndexStoreActionHandler implements Runnable {

   static final NodeDeque IndexStoreActionHandler_requestQueue;
   static NodeDeque IndexStoreActionHandler_responseQueue;
   public static int sleepsTillRestart;
   public static final Object IndexStoreActionHandler_lock;

   static {
      IndexStoreActionHandler_requestQueue = new NodeDeque();
      IndexStoreActionHandler_responseQueue = new NodeDeque();
      sleepsTillRestart = 0;
      IndexStoreActionHandler_lock = new Object();
   }

   public void run() {
      try {
         while(true) {
            IndexStoreAction action;
            synchronized(IndexStoreActionHandler_requestQueue) {
               action = (IndexStoreAction)IndexStoreActionHandler_requestQueue.last();
            }

            if(action != null) {
               if(action.type == 0) {
                  action.indexStore.write((int)action.key, action.data, action.data.length);
                  synchronized(IndexStoreActionHandler_requestQueue) {
                     action.remove();
                  }
               } else if(action.type == 1) {
                  action.data = action.indexStore.read((int)action.key);
                  synchronized(IndexStoreActionHandler_requestQueue) {
                     IndexStoreActionHandler_responseQueue.addFirst(action);
                  }
               }

               synchronized(IndexStoreActionHandler_lock) {
                  if(sleepsTillRestart <= 1) {
                     sleepsTillRestart = 0;
                     IndexStoreActionHandler_lock.notifyAll();
                     return;
                  }

                  sleepsTillRestart = 600;
               }
            } else {
               Time.sleep(100L);
               synchronized(IndexStoreActionHandler_lock) {
                  if(sleepsTillRestart <= 1) {
                     sleepsTillRestart = 0;
                     IndexStoreActionHandler_lock.notifyAll();
                     return;
                  }

                  --sleepsTillRestart;
               }
            }
         }
      } catch (Exception e) {
         Log.error("Failed to parse index store actions", e);
      }
   }
}
