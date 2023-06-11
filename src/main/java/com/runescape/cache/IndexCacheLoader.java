package com.runescape.cache;

public class IndexCacheLoader {

   final IndexCache indexCache;
   final String name;
   final int loadLength;
   int loadCount;

   IndexCacheLoader(IndexCache indexCache, String name) {
      this.indexCache = indexCache;
      this.name = name;
      loadCount = 0;
      loadLength = indexCache.recordsLength();
   }


   boolean completed() {
      loadCount = 0;

      for(int i = 0; i < loadLength; ++i) {
         if(!indexCache.hasRecord(i) || indexCache.archiveValid(i)) {
            ++loadCount;
         }
      }

      return loadCount >= loadLength;
   }

   @Override
   public String toString() {
      return "IndexCacheLoader{" +
              "name='" + name + '\'' +
              ", loadLength=" + loadLength +
              ", loadCount=" + loadCount +
              '}';
   }
}
