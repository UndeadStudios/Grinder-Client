package com.runescape.cache;

import com.runescape.collection.DualNode;
import com.runescape.collection.EvictingDualNodeHashTable;
import com.runescape.collection.NodeDeque;

public class Frames extends DualNode {

   public static EvictingDualNodeHashTable frameCache;

   public Animation[] frames;

   public Frames(AbstractIndexCache animationsCache, AbstractIndexCache skeletonsCache, int frameIndex, boolean var4) {
      NodeDeque deque = new NodeDeque();
      int length = animationsCache.getRecordLength(frameIndex);
      this.frames = new Animation[length];
      int[] indices = animationsCache.getRedcordOrNull(frameIndex);

      for (int i : indices) {
         byte[] animationData = animationsCache.takeRecord(frameIndex, i);
         Skeleton skeleton = null;
         int skeletonId = (animationData[0] & 255) << 8 | animationData[1] & 255;

         for (Skeleton loaded = (Skeleton) deque.last(); loaded != null; loaded = (Skeleton) deque.previous()) {
            if (skeletonId == loaded.id) {
               skeleton = loaded;
               break;
            }
         }

         if (skeleton == null) {
            byte[] var13 = skeletonsCache.getRecord(skeletonId, 0);
            skeleton = new Skeleton(skeletonId, var13);
            deque.addFirst(skeleton);
         }

         this.frames[i] = new Animation(animationData, skeleton);
      }
   }

   public static Frames getFrames(int frameIndex) {
      Frames var1 = (Frames) frameCache.get((long)frameIndex);
      if(var1 != null) {
         return var1;
      } else {
         boolean var5 = true;
         int[] record = Js5.skins.getRedcordOrNull(frameIndex);

         for (int i : record) {
            byte[] animationData = Js5.skins.getRecord(frameIndex, i);
            if (animationData == null) {
               var5 = false;
            } else {
               int var9 = (animationData[0] & 255) << 8 | animationData[1] & 255;
               byte[] var10 = Js5.skeletons.getRecord(var9, 0);
               if (var10 == null) {
                  var5 = false;
               }
            }
         }

         Frames frames;
         if(!var5) {
            frames = null;
         } else {
            try {
               frames = new Frames(Js5.skins, Js5.skeletons, frameIndex, false);
            } catch (Exception var12) {
               frames = null;
            }
         }

         if(frames != null) {
            frameCache.put(frames, frameIndex);
         }

         return frames;
      }
   }
   public boolean hasAlphaTransform(int var1) {
      return this.frames[var1].hasAlphaTransform;
   }
}
