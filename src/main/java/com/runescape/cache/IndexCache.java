package com.runescape.cache;

import com.grinder.client.util.Log;
import com.runescape.io.Buffer;
import com.runescape.util.ByteArrayUtil;

import java.util.zip.CRC32;

public class IndexCache extends AbstractIndexCache {

   private final static boolean DEBUG = false;

   static CRC32 IndexCache_crc;

   IndexStore indexStore;
   IndexStore referenceStore;
   int index;
   volatile boolean error;
   boolean __ag;
   volatile boolean[] validArchives;
   int indexReferenceCrc;
   int indexReferenceVersion;
   int lastLoadedArchiveId;

   static {
      IndexCache_crc = new CRC32();
   }

   public IndexCache(IndexStore indexStore, IndexStore referenceStore, int index, boolean releaseArchives, boolean shallowRecords, boolean var6) {
      super(releaseArchives, shallowRecords);
      error = false;
      __ag = false;
      lastLoadedArchiveId = -1;
      this.indexStore = indexStore;
      this.referenceStore = referenceStore;
      this.index = index;
      __ag = var6;
      if(OsCache.refrence != null) {
         OsCache.refrence.index = index * 8 + 5;
         int crc = OsCache.refrence.readInt();
         int version = OsCache.refrence.readInt();
         loadIndexReference(crc, version);
      } else {
         OsCache.requestNetFile(null, 255, 255, 0, (byte)0, true);
         NetCache.indexCaches[index] = this;
      }
   }

   void requestFile(int var1) {
      int var2 = index;
      long var3 = (var2 << 16) + var1;
      NetFileRequest var5 = (NetFileRequest)NetCache.pendingWrites.get(var3);
      if(var5 != null) {
         NetCache.pendingWritesQueue.addLast(var5);
      }
   }

   int archiveLoadPercent(int archiveId) {
      return super.archives[archiveId] != null
              ? 100
              :(validArchives[archiveId]
                  ? 100
                  : Js5Cache.getLoadPercent(index, archiveId));
   }

   void loadArchive(int archiveId) {
      if(indexStore != null && validArchives != null && validArchives[archiveId]) {
         processRequestQueue(archiveId, indexStore, null);
      } else {
         OsCache.requestNetFile(this, index, archiveId, super.archiveCrcs[archiveId], (byte)2, true);
      }
   }

   public boolean failed() {
      return error;
   }

   public int indexLoadPercent() {
      if(error) {
         return 100;
      } else if(super.archives != null) {
         return 99;
      } else {
         int var1 = Js5Cache.getLoadPercent(255, index);
         if(var1 >= 100) {
            var1 = 99;
         }

         return var1;
      }
   }

   public void loadIndexReference(int crc, int version) {
      indexReferenceCrc = crc;
      indexReferenceVersion = version;
      if(referenceStore != null)
         processRequestQueue(index, referenceStore, null);
      else {
         OsCache.requestNetFile(this, 255, index, indexReferenceCrc, (byte)0, true);
      }
   }

   private void processRequestQueue(int archiveId, IndexStore indexStore, byte[] data) {
      synchronized(IndexStoreActionHandler.IndexStoreActionHandler_requestQueue) {
         for(IndexStoreAction storeAction = (IndexStoreAction)IndexStoreActionHandler.IndexStoreActionHandler_requestQueue.last(); storeAction != null; storeAction = (IndexStoreAction)IndexStoreActionHandler.IndexStoreActionHandler_requestQueue.previous()) {
            if(storeAction.key == (long)archiveId && indexStore == storeAction.indexStore && storeAction.type == 0) {
               data = storeAction.data;
               break;
            }
         }
      }
      if(data != null) {
         load(indexStore, archiveId, data, true);
      } else {
         byte[] bytes = indexStore.read(archiveId);
         load(indexStore, archiveId, bytes, true);
      }
   }

   public void write(int archiveId, byte[] data, boolean var3, boolean var4) {
      if(var3) {

         if(error)
            throw new RuntimeException();

         if(referenceStore != null)
            request(index, data, referenceStore);

         setIndexReference(data);
         loadAllLocal();
      } else {
         data[data.length - 2] = (byte)(super.archiveVersions[archiveId] >> 8);
         data[data.length - 1] = (byte)super.archiveVersions[archiveId];
         if(indexStore != null) {
            request(archiveId, data, indexStore);
            validArchives[archiveId] = true;
         }

         if(var4) {
            super.archives[archiveId] = ByteArrayUtil.byteArrayToObject(data, false);
         }
      }
   }

   void load(IndexStore store, int archiveId, byte[] data, boolean var4) {

//      System.out.println("IndexCache.load -> store = " + store + ", archiveId = " + archiveId + ", data = " + Arrays.toString(data) + ", var4 = " + var4);
      int var5;

      if(store == referenceStore) {
         if(error) {
            throw new RuntimeException();
         }

         if(data == null) {
            OsCache.requestNetFile(this, 255, index, indexReferenceCrc, (byte)0, true);
            return;
         }

         IndexCache_crc.reset();
         IndexCache_crc.update(data, 0, data.length);
         var5 = (int)IndexCache_crc.getValue();
         if(var5 != indexReferenceCrc) {
            OsCache.requestNetFile(this, 255, index, indexReferenceCrc, (byte)0, true);
            return;
         }

         Buffer buffer = new Buffer(ByteArrayUtil.decompressBytes(data));
         int var7 = buffer.getUnsignedByte();
         if(var7 != 5 && var7 != 6) {
            throw new RuntimeException(var7 + "," + index + "," + archiveId);
         }

         int var8 = 0;
         if(var7 >= 6) {
            var8 = buffer.readInt();
         }

         if(var8 != indexReferenceVersion) {
            OsCache.requestNetFile(this, 255, index, indexReferenceCrc, (byte)0, true);
            return;
         }

         setIndexReference(data);
         loadAllLocal();
      } else {
         if(!var4 && archiveId == lastLoadedArchiveId) {
            error = true;
         }

         if(data == null || data.length <= 2) {
            validArchives[archiveId] = false;
            if(__ag || var4) {
               OsCache.requestNetFile(this, index, archiveId, super.archiveCrcs[archiveId], (byte)2, var4);
            }
            return;
         }

         IndexCache_crc.reset();
         IndexCache_crc.update(data, 0, data.length - 2);
         var5 = (int)IndexCache_crc.getValue();
         int var6 = ((data[data.length - 2] & 255) << 8) + (data[data.length - 1] & 255);
         if(var5 != super.archiveCrcs[archiveId] || var6 != super.archiveVersions[archiveId]) {
            validArchives[archiveId] = false;
            if(__ag || var4)
               OsCache.requestNetFile(this, index, archiveId, super.archiveCrcs[archiveId], (byte)2, var4);
            return;
         }

         validArchives[archiveId] = true;
         if(var4) {
            super.archives[archiveId] = ByteArrayUtil.byteArrayToObject(data, false);
         }
      }

   }

   void loadAllLocal() {
      validArchives = new boolean[super.archives.length];

      int archiveId;
      for(archiveId = 0; archiveId < validArchives.length; ++archiveId) {
         validArchives[archiveId] = false;
      }

      if(indexStore == null) {
         error = true;
      } else {
         lastLoadedArchiveId = -1;

         for(archiveId = 0; archiveId < validArchives.length; ++archiveId) {
            if(super.recordCounts[archiveId] > 0) {
               requestEntry(archiveId, indexStore, this);
               lastLoadedArchiveId = archiveId;
            }
         }

         if(lastLoadedArchiveId == -1) {
            error = true;
         }
      }
   }

   public boolean archiveValid(int archiveId) {
      return validArchives[archiveId];
   }

   public boolean hasRecord(int recordId) {
      return getRedcordOrNull(recordId) != null;
   }

   public int loadPercent() {
      int totalDivisor = 0;
      int totalLoadPercent = 0;

      for(int archiveId = 0; archiveId < super.archives.length; ++archiveId) {
         if(super.recordCounts[archiveId] > 0) {
            totalDivisor += 100;
            totalLoadPercent += archiveLoadPercent(archiveId);
         }
      }

      if(totalDivisor == 0)
         return 100;
      else
         return totalLoadPercent * 100 / totalDivisor;
   }

   static void request(int key, byte[] data, IndexStore store) {
      IndexStoreAction action = new IndexStoreAction();
      action.type = 0;
      action.key = key;
      action.data = data;
      action.indexStore = store;

      if(DEBUG)
         Log.info("Requesting-0 entry "+key+" from store "+store.index+" (buffer size = "+data.length+')');

      synchronized(IndexStoreActionHandler.IndexStoreActionHandler_requestQueue) {
         IndexStoreActionHandler.IndexStoreActionHandler_requestQueue.addFirst(action);
      }

      createIndexStoreActionHandlerThread();
   }

   static void requestEntry(int entryIndex, IndexStore store, IndexCache cache) {
      IndexStoreAction action = new IndexStoreAction();
      action.type = 1;
      action.key = entryIndex;
      action.indexStore = store;
      action.indexCache = cache;

      if(DEBUG)
         Log.info("Requesting-1 entry "+entryIndex+" from store "+store.index+" in cache "+cache.index);

      synchronized(IndexStoreActionHandler.IndexStoreActionHandler_requestQueue) {
         IndexStoreActionHandler.IndexStoreActionHandler_requestQueue.addFirst(action);
      }

      createIndexStoreActionHandlerThread();
   }


   private static void createIndexStoreActionHandlerThread() {
      synchronized(IndexStoreActionHandler.IndexStoreActionHandler_lock) {
         if(IndexStoreActionHandler.sleepsTillRestart == 0) {
            OsCache.IndexStoreActionHandler_thread = new Thread(new IndexStoreActionHandler());
            OsCache.IndexStoreActionHandler_thread.setDaemon(true);
            OsCache.IndexStoreActionHandler_thread.start();
            OsCache.IndexStoreActionHandler_thread.setPriority(5);
         }
         IndexStoreActionHandler.sleepsTillRestart = 600;
      }
   }
}
