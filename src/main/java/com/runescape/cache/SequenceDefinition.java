package com.runescape.cache;


import com.runescape.collection.DualNode;
import com.runescape.collection.EvictingDualNodeHashTable;
import com.runescape.entity.model.Model;
import com.runescape.io.Buffer;

import java.util.Arrays;

public class SequenceDefinition extends DualNode {

   public static EvictingDualNodeHashTable SequenceDefinition_cached;

   public int[] frameIds;
   int[] frameIds2;
   public int[] frameLengths;
   public int[] soundsEffects;
   public int frameCount;
   int[] interleaveOrder;
   public boolean stretches;
   public int forcedPriority;
   public int shield;
   public int weapon;
   public int maximumLoops;
   public int animatingPrecedence;
   public int priority;
   public int replayMode;

   static {
      SequenceDefinition_cached = new EvictingDualNodeHashTable(64);
      Frames.frameCache = new EvictingDualNodeHashTable(100);
   }

   SequenceDefinition() {
      this.frameCount = -1;
      this.stretches = false;
      this.forcedPriority = 5;
      this.shield = -1;
      this.weapon = -1;
      this.maximumLoops = 99;
      this.animatingPrecedence = -1;
      this.priority = -1;
      this.replayMode = 2;
   }

   public static SequenceDefinition getSequenceDefinition(int sequence) {
      SequenceDefinition var1 = (SequenceDefinition) SequenceDefinition_cached.get((long)sequence);
      if(var1 != null) {
         return var1;
      } else {
         byte[] var2 = Js5.configs.takeRecord(12, sequence);
         var1 = new SequenceDefinition();
         if(var2 != null) {
            var1.read(new Buffer(var2));
         }

         var1.init();
         SequenceDefinition_cached.put(var1, (long)sequence);
         return var1;
      }
   }

   void read(Buffer var1) {
      while(true) {
         int var2 = var1.getUnsignedByte();
         if(var2 == 0) {
            return;
         }

         this.readNext(var1, var2);
      }
   }

   void readNext(Buffer var1, int var2) {
      int var3;
      int var4;
      if(var2 == 1) {
         var3 = var1.getUnsignedLEShort();
         this.frameLengths = new int[var3];

         for(var4 = 0; var4 < var3; ++var4) {
            this.frameLengths[var4] = var1.getUnsignedLEShort();
         }

         this.frameIds = new int[var3];

         for(var4 = 0; var4 < var3; ++var4) {
            this.frameIds[var4] = var1.getUnsignedLEShort();
         }

         for(var4 = 0; var4 < var3; ++var4) {
            this.frameIds[var4] += var1.getUnsignedLEShort() << 16;
         }
      } else if(var2 == 2) {
         this.frameCount = var1.getUnsignedLEShort();
      } else if(var2 == 3) {
         var3 = var1.getUnsignedByte();
         this.interleaveOrder = new int[var3 + 1];

         for(var4 = 0; var4 < var3; ++var4) {
            this.interleaveOrder[var4] = var1.getUnsignedByte();
         }

         this.interleaveOrder[var3] = 9999999;
      } else if(var2 == 4) {
         this.stretches = true;
      } else if(var2 == 5) {
         this.forcedPriority = var1.getUnsignedByte();
      } else if(var2 == 6) {
         this.shield = var1.getUnsignedLEShort();
      } else if(var2 == 7) {
         this.weapon = var1.getUnsignedLEShort();
      } else if(var2 == 8) {
         this.maximumLoops = var1.getUnsignedByte();
      } else if(var2 == 9) {
         this.animatingPrecedence = var1.getUnsignedByte();
      } else if(var2 == 10) {
         this.priority = var1.getUnsignedByte();
      } else if(var2 == 11) {
         this.replayMode = var1.getUnsignedByte();
      } else if(var2 == 12) {
         var3 = var1.getUnsignedByte();
         this.frameIds2 = new int[var3];

         for(var4 = 0; var4 < var3; ++var4) {
            this.frameIds2[var4] = var1.getUnsignedLEShort();
         }

         for(var4 = 0; var4 < var3; ++var4) {
            this.frameIds2[var4] += var1.getUnsignedLEShort() << 16;
         }
      } else if(var2 == 13) {
         var3 = var1.getUnsignedByte();
         this.soundsEffects = new int[var3];

         for(var4 = 0; var4 < var3; ++var4) {
            this.soundsEffects[var4] = var1.readMedium();
         }
      }

   }

   void init() {
      if(this.animatingPrecedence == -1) {
         if(this.interleaveOrder != null) {
            this.animatingPrecedence = 2;
         } else {
            this.animatingPrecedence = 0;
         }
      }

      if(this.priority == -1) {
         if(this.interleaveOrder != null) {
            this.priority = 2;
         } else {
            this.priority = 0;
         }
      }

   }


   @Override
   public String toString() {
      return "SequenceDefinition{" +
              "frameIds=" + Arrays.toString(frameIds) +
              ", frameIds2=" + Arrays.toString(frameIds2) +
              ", frameLengths=" + Arrays.toString(frameLengths) +
              ", soundsEffects=" + Arrays.toString(soundsEffects) +
              ", frameCount=" + frameCount +
              ", interleaveOrder=" + Arrays.toString(interleaveOrder) +
              ", stretches=" + stretches +
              ", forcedPriority=" + forcedPriority +
              ", shield=" + shield +
              ", weapon=" + weapon +
              ", maximumLoops=" + maximumLoops +
              ", animatingPrecedence=" + animatingPrecedence +
              ", priority=" + priority +
              ", replayMode=" + replayMode +
              '}';
   }

   public Model animateSequence(Model var1, int var2) {
      var2 = this.frameIds[var2];
      Frames var3 = Frames.getFrames(var2 >> 16);
      var2 &= 65535;
      if(var3 == null) {
         return var1.toSharedSequenceModel(true);
      } else {
         Model var4 = var1.toSharedSequenceModel(!var3.hasAlphaTransform(var2));
         var4.animate(var3, var2);
         return var4;
      }
   }
//
public Model animateObject(Model var1, int var2, int var3) {
      var2 = this.frameIds[var2];
      Frames var4 = Frames.getFrames(var2 >> 16);
      var2 &= 65535;
      if(var4 == null) {
         return var1.toSharedSequenceModel(true);
      } else {
         Model var5 = var1.toSharedSequenceModel(!var4.hasAlphaTransform(var2));
         var3 &= 3;
         if(var3 == 1) {
            var5.rotateY270Ccw();
         } else if(var3 == 2) {
            var5.rotateY180();
         } else if(var3 == 3) {
            var5.rotateY90Ccw();
         }

         var5.animate(var4, var2);
         if(var3 == 1) {
            var5.rotateY90Ccw();
         } else if(var3 == 2) {
            var5.rotateY180();
         } else if(var3 == 3) {
            var5.rotateY270Ccw();
         }

         return var5;
      }
   }
//
   Model animateSpotAnimation(Model var1, int var2) {
      var2 = this.frameIds[var2];
      Frames var3 = Frames.getFrames(var2 >> 16);
      var2 &= 65535;
      if(var3 == null) {
         return var1.toSharedSpotAnimationModel(true);
      } else {
         Model var4 = var1.toSharedSpotAnimationModel(!var3.hasAlphaTransform(var2));
         var4.animate(var3, var2);
         return var4;
      }
   }
//
   public Model animateSequence2(Model var1, int var2, SequenceDefinition var3, int var4) {
      var2 = this.frameIds[var2];
      Frames var5 = Frames.getFrames(var2 >> 16);
      var2 &= 65535;
      if(var5 == null) {
         return var3.animateSequence(var1, var4);
      } else {
         var4 = var3.frameIds[var4];
         Frames var6 = Frames.getFrames(var4 >> 16);
         var4 &= 65535;
         Model var7;
         if(var6 == null) {
            var7 = var1.toSharedSequenceModel(!var5.hasAlphaTransform(var2));
            var7.animate(var5, var2);
            return var7;
         } else {
            var7 = var1.toSharedSequenceModel(!var5.hasAlphaTransform(var2) & !var6.hasAlphaTransform(var4));
            var7.animate2(var5, var2, var6, var4, this.interleaveOrder);
            return var7;
         }
      }
   }
//
   public Model animateWidget(Model var1, int var2) {
      int var3 = this.frameIds[var2];
      Frames var4 = Frames.getFrames(var3 >> 16);
      var3 &= 65535;
      if(var4 == null) {
         return var1.toSharedSequenceModel(true);
      } else {
         Frames var5 = null;
         int var6 = 0;
         if(this.frameIds2 != null && var2 < this.frameIds2.length) {
            var6 = this.frameIds2[var2];
            var5 = Frames.getFrames(var6 >> 16);
            var6 &= 65535;
         }

         Model var7;
         if(var5 != null && var6 != 65535) {
            var7 = var1.toSharedSequenceModel(!var4.hasAlphaTransform(var3) & !var5.hasAlphaTransform(var6));
            var7.animate(var4, var3);
            var7.animate(var5, var6);
            return var7;
         } else {
            var7 = var1.toSharedSequenceModel(!var4.hasAlphaTransform(var3));
            var7.animate(var4, var3);
            return var7;
         }
      }
   }
}
