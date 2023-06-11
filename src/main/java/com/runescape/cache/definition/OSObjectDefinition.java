package com.runescape.cache.definition;

import com.grinder.client.util.Log;
import com.runescape.cache.*;
import com.runescape.collection.*;
import com.runescape.entity.Renderable;
import com.runescape.entity.model.Model;
import com.runescape.io.Buffer;

public class OSObjectDefinition extends DualNode implements TempObjectDefinition {

   public static boolean USE_OSRS = false;
   public static boolean USE_OSRS2 = false;

   public static boolean ObjectDefinition_isLowDetail;

   public static AbstractIndexCache ObjectDefinition_indexCache;
   public static AbstractIndexCache __jr_q;
   public static EvictingDualNodeHashTable ObjectDefinition_cached;
   public static EvictingDualNodeHashTable baseModels;
   static EvictingDualNodeHashTable __jr_u;
   static EvictingDualNodeHashTable ObjectDefinition_cachedModels;
   static ModelData[] __jr_l;

   public int id;
   int[] modelIds;
   int[] modelTypes;
   public String name;
   short[] recolorFrom;
   short[] recolorTo;
   short[] retextureFrom;
   short[] retextureTo;
   public int sizeX;
   public int sizeY;
   public int interactType;
   public boolean impenetrable;
   public int interactiveState;
   int clipType;
   boolean nonFlatShading;
   public boolean modelClipped;
   public int animationId;
   public int decorDisplacement;
   int ambient;
   int contrast;
   public String[] actions;
   public int mapIconId;
   public int mapSceneId;
   public int surroundings;
   boolean isRotated;
   public boolean clipped;
   int modelSizeX;
   int modelHeight;
   int modelSizeY;
   int offsetX;
   int offsetHeight;
   int offsetY;
   public boolean obstructsGround;
   public boolean isSolid;
   public int supportItems;
   public int[] transforms;
   int transformVarbit;
   int transformConfigId;
   public int ambientSoundId;
   public int int4;
   public int int5;
   public int int6;
   public int[] someSoundStuff;

   IterableNodeHashTable params;

   static {
      ObjectDefinition_isLowDetail = false;
      ObjectDefinition_cached = new EvictingDualNodeHashTable(4096);
      baseModels = new EvictingDualNodeHashTable(500);
      __jr_u = new EvictingDualNodeHashTable(30);
      ObjectDefinition_cachedModels = new EvictingDualNodeHashTable(30);
      __jr_l = new ModelData[4];
   }

   public static void clearCaches() {
      ObjectDefinition_cached.clear();
      baseModels.clear();
      __jr_u.clear();
      ObjectDefinition_cachedModels.clear();
   }

   public OSObjectDefinition() {
      this.name = "null";
      this.sizeX = 1;
      this.sizeY = 1;
      this.interactType = 2;
      this.impenetrable = true;
      this.interactiveState = -1;
      this.clipType = -1;
      this.nonFlatShading = false;
      this.modelClipped = false;
      this.animationId = -1;
      this.decorDisplacement = 16;
      this.ambient = 0;
      this.contrast = 0;
      this.actions = new String[5];
      this.mapIconId = -1;
      this.mapSceneId = -1;
      this.isRotated = false;
      this.clipped = true;
      this.modelSizeX = 128;
      this.modelHeight = 128;
      this.modelSizeY = 128;
      this.offsetX = 0;
      this.offsetHeight = 0;
      this.offsetY = 0;
      this.obstructsGround = false;
      this.isSolid = false;
      this.supportItems = -1;
      this.transformVarbit = -1;
      this.transformConfigId = -1;
      this.ambientSoundId = -1;
      this.int4 = 0;
      this.int5 = 0;
      this.int6 = 0;
   }

   public static int getObjectDefinitionCount(){
       return ObjectDefinition_indexCache.getRecordLength(6);
   }

   public static void setCaches(AbstractIndexCache defCache, AbstractIndexCache modelCache, boolean var2) {
       ObjectDefinition_indexCache = defCache;
       __jr_q = modelCache;
       ObjectDefinition_isLowDetail = var2;
   }

   public static OSObjectDefinition[] getObjectDefinitions(){
       final OSObjectDefinition[] array = new OSObjectDefinition[getObjectDefinitionCount()];
       for(int i = 0; i < array.length; i++){
           array[i] = lookup(i);
       }
       return array;
   }

   public static OSObjectDefinition lookup(int objectId) {
       OSObjectDefinition definition = ObjectDefinitionHardCode.transform((OSObjectDefinition) ObjectDefinition_cached.get(objectId));
       if(definition != null) {
           return definition;
       } else {
          try {
             byte[] var2 = ObjectDefinition_indexCache.takeRecord(6, objectId);
             definition = new OSObjectDefinition();
             definition.id = objectId;
             if (var2 != null) {
                definition.read(new Buffer(var2));
             }

             definition.init();
             if (definition.isSolid) {
                definition.interactType = 0;
                definition.impenetrable = false;
             }

             ObjectDefinition_cached.put(definition, objectId);
             return definition;
          } catch (Exception e){
             e.printStackTrace();
             System.err.println("Failed to dump "+objectId);
             return null;
          }
       }
   }

   public void init() {
      if(this.interactiveState == -1) {
         this.interactiveState = 0;
         if(this.modelIds != null && (this.modelTypes == null || this.modelTypes[0] == 10)) {
            this.interactiveState = 1;
         }

         for(int var1 = 0; var1 < 5; ++var1) {
            if(this.actions[var1] != null) {
               this.interactiveState = 1;
            }
         }
      }

      if(this.supportItems == -1) {
         this.supportItems = this.interactType != 0?1:0;
      }

   }

   public void read(Buffer var1) {
      while(true) {
         int var2 = var1.readUnsignedByte();
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
         var3 = var1.readUnsignedByte();
         if(var3 > 0) {
            if(this.modelIds != null && !ObjectDefinition_isLowDetail) {
               var1.index += var3 * 3;
            } else {
               this.modelTypes = new int[var3];
               this.modelIds = new int[var3];

               for(var4 = 0; var4 < var3; ++var4) {
                  this.modelIds[var4] = var1.readUnsignedShort();
                  this.modelTypes[var4] = var1.readUnsignedByte();
               }
            }
         }
      } else if(var2 == 2) {
         this.name = var1.readStringCp1252NullTerminated();
      } else if(var2 == 5) {
         var3 = var1.readUnsignedByte();
         if(var3 > 0) {
            if(this.modelIds != null && !ObjectDefinition_isLowDetail) {
               var1.index += var3 * 2;
            } else {
               this.modelTypes = null;
               this.modelIds = new int[var3];

               for(var4 = 0; var4 < var3; ++var4) {
                  this.modelIds[var4] = var1.readUnsignedShort();
               }
            }
         }
      } else if(var2 == 14) {
         this.sizeX = var1.readUnsignedByte();
      } else if(var2 == 15) {
         this.sizeY = var1.readUnsignedByte();
      } else if(var2 == 17) {
         this.interactType = 0;
         this.impenetrable = false;
      } else if(var2 == 18) {
         this.impenetrable = false;
      } else if(var2 == 19) {
         this.interactiveState = var1.readUnsignedByte();
      } else if(var2 == 21) {
         this.clipType = 0;
      } else if(var2 == 22) {
         this.nonFlatShading = true;
      } else if(var2 == 23) {
         this.modelClipped = true;
      } else if(var2 == 24) {
         this.animationId = var1.readUnsignedShort();
         if(this.animationId == 65535) {
            this.animationId = -1;
         }
      } else if(var2 == 27) {
         this.interactType = 1;
      } else if(var2 == 28) {
         this.decorDisplacement = var1.readUnsignedByte();
      } else if(var2 == 29) {
         this.ambient = var1.readByte();
      } else if(var2 == 39) {
         this.contrast = var1.readByte() * 25;
      } else if(var2 >= 30 && var2 < 35) {
         this.actions[var2 - 30] = var1.readStringCp1252NullTerminated();
         if(this.actions[var2 - 30].equalsIgnoreCase("Hidden")) {
            this.actions[var2 - 30] = null;
         }
      } else if(var2 == 40) {
         var3 = var1.readUnsignedByte();
         this.recolorFrom = new short[var3];
         this.recolorTo = new short[var3];

         for(var4 = 0; var4 < var3; ++var4) {
            this.recolorFrom[var4] = (short)var1.readUnsignedShort();
            this.recolorTo[var4] = (short)var1.readUnsignedShort();
         }
      } else if(var2 == 41) {
         var3 = var1.readUnsignedByte();
         this.retextureFrom = new short[var3];
         this.retextureTo = new short[var3];

         for (var4 = 0; var4 < var3; ++var4) {
            this.retextureFrom[var4] = (short) var1.readUnsignedShort();
            this.retextureTo[var4] = (short) var1.readUnsignedShort();
         }
      } else if (var2 == 61) {
         var1.readUnsignedShort();
      } else if(var2 == 62) {
         this.isRotated = true;
      } else if(var2 == 64) {
         this.clipped = false;
      } else if(var2 == 65) {
         this.modelSizeX = var1.readUnsignedShort();
      } else if(var2 == 66) {
         this.modelHeight = var1.readUnsignedShort();
      } else if(var2 == 67) {
         this.modelSizeY = var1.readUnsignedShort();
      } else if(var2 == 68) {
         this.mapSceneId = var1.readUnsignedShort();
      } else if(var2 == 69) {
         this.surroundings = var1.readUnsignedByte();
      } else if(var2 == 70) {
         this.offsetX = var1.__aq_303();
      } else if(var2 == 71) {
         this.offsetHeight = var1.__aq_303();
      } else if(var2 == 72) {
         this.offsetY = var1.__aq_303();
      } else if(var2 == 73) {
         this.obstructsGround = true;
      } else if(var2 == 74) {
         this.isSolid = true;
      } else if(var2 == 75) {
         this.supportItems = var1.readUnsignedByte();
      } else if(var2 != 77 && var2 != 92) {
         if(var2 == 78) {
            this.ambientSoundId = var1.readUnsignedShort();
            this.int4 = var1.readUnsignedByte();
         } else if(var2 == 79) {
            this.int5 = var1.readUnsignedShort();
            this.int6 = var1.readUnsignedShort();
            this.int4 = var1.readUnsignedByte();
            var3 = var1.readUnsignedByte();
            this.someSoundStuff = new int[var3];

            for(var4 = 0; var4 < var3; ++var4) {
               this.someSoundStuff[var4] = var1.readUnsignedShort();
            }
         } else if(var2 == 81) {
            this.clipType = var1.readUnsignedByte() * 256;
         } else if(var2 == 82) {
            this.mapIconId = var1.readUnsignedShort();
         } else if(var2 == 249) {
            this.params = OSCollections.readStringIntParameters(var1, this.params);
         }
      } else {
         this.transformVarbit = var1.readUnsignedShort();
         if(this.transformVarbit == 65535) {
            this.transformVarbit = -1;
         }

         this.transformConfigId = var1.readUnsignedShort();
         if(this.transformConfigId == 65535) {
            this.transformConfigId = -1;
         }

         var3 = -1;
         if(var2 == 92) {
            var3 = var1.readUnsignedShort();
            if(var3 == 65535) {
               var3 = -1;
            }
         }

         var4 = var1.readUnsignedByte();
         this.transforms = new int[var4 + 2];

         for(int var5 = 0; var5 <= var4; ++var5) {
            this.transforms[var5] = var1.readUnsignedShort();
            if(this.transforms[var5] == 65535) {
               this.transforms[var5] = -1;
            }
         }

         this.transforms[var4 + 1] = var3;
      }

   }

   public final boolean __u_421(int var1) {
      if(this.modelTypes != null) {
         for(int var4 = 0; var4 < this.modelTypes.length; ++var4) {
            if(this.modelTypes[var4] == var1) {
               return __jr_q.tryLoadRecord(this.modelIds[var4] & 65535, 0);
            }
         }

         return true;
      } else if(this.modelIds == null) {
         return true;
      } else if(var1 != 10) {
         return true;
      } else {
         boolean var2 = true;

         for(int var3 = 0; var3 < this.modelIds.length; ++var3) {
            var2 &= __jr_q.tryLoadRecord(this.modelIds[var3] & 65535, 0);
         }

         return var2;
      }
   }

   public final boolean __g_422() {
      if(this.modelIds == null) {
         return true;
      } else {
         boolean var1 = true;

         for(int var2 = 0; var2 < this.modelIds.length; ++var2) {
            var1 &= __jr_q.tryLoadRecord(this.modelIds[var2] & 65535, 0);
         }

         return var1;
      }
   }

   public final Renderable modelAt(int var1, int var2, int[][] var3, int var4, int var5, int var6) {
      long var7;
      if(this.modelTypes == null) {
         var7 = var2 + (this.id << 10);
      } else {
         var7 = var2 + (var1 << 3) + (this.id << 10);
      }

      DualNode var9 = __jr_u.get(var7);
      if(var9 == null) {
         ModelData var10 = this.getModelData(var1, var2);
         if(var10 == null) {
            return null;
         }

         if(!this.nonFlatShading) {
            var9 = var10.toModel(this.ambient + 64, this.contrast + 768, -50, -10, -50);
         } else {
            var10.ambient = (short)(this.ambient + 64);
            var10.contrast = (short)(this.contrast + 768);
            var10.calculateVertexNormals();
            var9 = var10;
         }

         __jr_u.put(var9, var7);
      }

      if(this.nonFlatShading) {
         var9 = ((ModelData)var9).copyModelData();
      }

      if(this.clipType >= 0) {
         if(var9 instanceof OgModel) {
            var9 = ((OgModel)var9).contourGround(var3, var4, var5, var6, true, this.clipType);
         } else if(var9 instanceof ModelData) {
            var9 = ((ModelData)var9).method4172(var3, var4, var5, var6, true, this.clipType);
         }
      }

      assert var9 instanceof Renderable;
      return (Renderable) var9;
   }
   public final Model getModel(int var1, int var2, int[][] var3, int var4, int var5, int var6) {
      long var7;
      if(this.modelTypes == null) {
         var7 = var2 + (this.id << 10);
      } else {
         var7 = var2 + (var1 << 3) + (this.id << 10);
      }

      Model var9 = (Model)ObjectDefinition_cachedModels.get(var7);
      if(var9 == null) {
         ModelData var10 = this.getModelData(var1, var2);
         if(var10 == null) {
            return null;
         }

         var9 = var10.toModel(this.ambient + 64, this.contrast + 768, -50, -10, -50);
         ObjectDefinition_cachedModels.put(var9, var7);
      }

      if(this.clipType >= 0) {
         var9 = var9.contourGround(var3, var4, var5, var6, true, this.clipType);
      }

      return var9;
   }


   final ModelData getModelData(int var1, int var2) {
      ModelData var3 = null;
      boolean var4;
      int var5;
      int var7;
      if(this.modelTypes == null) {
         if(var1 != 10) {
            return null;
         }

         if(this.modelIds == null) {
            return null;
         }

         var4 = this.isRotated;
         if(var1 == 2 && var2 > 3) {
            var4 = !var4;
         }

         var5 = this.modelIds.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            var7 = this.modelIds[var6];
            if(var4) {
               var7 += 65536;
            }

            var3 = (ModelData) baseModels.get(var7);
            if(var3 == null) {
               var3 = ModelData.ModelData_get(__jr_q, var7 & 65535, 0);
               if(var3 == null) {
                  return null;
               }

               if(var4) {
                  var3.__t_226();
               }

               baseModels.put(var3, var7);
            }

            if(var5 > 1) {
               __jr_l[var6] = var3;
            }
         }

         if(var5 > 1) {
            var3 = new ModelData(__jr_l, var5);
         }
      } else {
         int var9 = -1;

         for(var5 = 0; var5 < this.modelTypes.length; ++var5) {
            if(this.modelTypes[var5] == var1) {
               var9 = var5;
               break;
            }
         }

         if(var9 == -1) {
            return null;
         }

         var5 = this.modelIds[var9];
         boolean var10 = this.isRotated ^ var2 > 3;
         if(var10) {
            var5 += 65536;
         }

         var3 = (ModelData) baseModels.get(var5);
         if(var3 == null) {
            var3 = ModelData.ModelData_get(__jr_q, var5 & 65535, 0);
            if(var3 == null) {
               return null;
            }

            if(var10) {
               var3.__t_226();
            }

            baseModels.put(var3, var5);
         }
      }

      var4 = this.modelSizeX != 128 || this.modelHeight != 128 || this.modelSizeY != 128;

      boolean var11;
      var11 = this.offsetX != 0 || this.offsetHeight != 0 || this.offsetY != 0;

      ModelData var8 = new ModelData(var3, var2 == 0 && !var4 && !var11, this.recolorFrom == null, null == this.retextureFrom, true);
      if(var1 == 4 && var2 > 3) {
         var8.method4176(256);
         var8.changeOffset(45, 0, -45);
      }

      var2 &= 3;
      if(var2 == 1) {
         var8.method4177();
      } else if(var2 == 2) {
         var8.method4175();
      } else if(var2 == 3) {
         var8.method4205();
      }

      if(this.recolorFrom != null) {
         for(var7 = 0; var7 < this.recolorFrom.length; ++var7) {
            var8.recolor(this.recolorFrom[var7], this.recolorTo[var7]);
         }
      }

      if(this.retextureFrom != null) {
         for(var7 = 0; var7 < this.retextureFrom.length; ++var7) {
            var8.retexture(this.retextureFrom[var7], this.retextureTo[var7]);
         }
      }

      if(var4) {
         var8.resize(this.modelSizeX, this.modelHeight, this.modelSizeY);
      }

      if(var11) {
         var8.changeOffset(this.offsetX, this.offsetHeight, this.offsetY);
      }

      return var8;
   }

   public final OSObjectDefinition transform() {
      int var1 = -1;
      if(this.transformVarbit != -1) {
         var1 = OsCache.getVarbit(this.transformVarbit);
      } else if(this.transformConfigId != -1) {
         var1 = Varps.Varps_main[this.transformConfigId];
      }

      int var2;
      if(var1 >= 0 && var1 < this.transforms.length - 1) {
         var2 = this.transforms[var1];
      } else {
         var2 = this.transforms[this.transforms.length - 1];
      }

      return var2 != -1? lookup(var2):null;
   }

   public int getIntParam(int var1, int var2) {
      IterableNodeHashTable var4 = this.params;
      int var3;
      if(var4 == null) {
         var3 = var2;
      } else {
         IntegerNode var5 = (IntegerNode)var4.get(var1);
         if(var5 == null) {
            var3 = var2;
         } else {
            var3 = var5.integer;
         }
      }

      return var3;
   }

   public String getStringParam(int var1, String var2) {
      return OSCollections.method3238(this.params, var1, var2);
   }

   public boolean hasSound() {
      if(this.transforms == null) {
         return this.ambientSoundId != -1 || this.someSoundStuff != null;
      } else {
         for(int var1 = 0; var1 < this.transforms.length; ++var1) {
            if(this.transforms[var1] != -1) {
               OSObjectDefinition var2 = lookup(this.transforms[var1]);
               if(var2.ambientSoundId != -1 || var2.someSoundStuff != null) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public void setSize(int sizeX, int sizeY) {
      this.sizeX = sizeX;
      this.sizeY = sizeY;
   }

   public void setModelScale(int x, int height, int y){
      modelSizeX = x;
      modelHeight = height;
      modelSizeY = y;
   }
   public void setModelScale(int value){
      setModelScale(value, value, value);
   }
   public void makeType10(){
      impenetrable = false;
      interactType = 10;
      interactiveState = 1;
   }
   public void removeActions(){
      actions = null;
   }
   public void setActions(String... actions){
      if(actions.length > 5) {
         Log.error("You can have a maximum of 5 actions!");
         return;
      }
      this.actions = new String[5];
      System.arraycopy(actions, 0, this.actions, 0, actions.length);
   }
   public void setModelIds(int... modelIds){
      this.modelIds = modelIds;
   }

   public void copy(int other) {
      OSObjectDefinition copy = lookup(other);
      modelSizeX = copy.modelSizeX;
      modelHeight = copy.modelHeight;
      modelSizeY = copy.modelSizeY;
      obstructsGround = copy.obstructsGround;
      offsetX = copy.offsetX;
      contrast = copy.contrast;
      sizeX = copy.sizeX;
      offsetHeight = copy.offsetHeight;
      recolorTo = copy.recolorTo;
      impenetrable = copy.impenetrable;
      sizeY = copy.sizeY;
      modelClipped = copy.modelClipped;
      isSolid = copy.isSolid;
      nonFlatShading = copy.nonFlatShading;
      decorDisplacement = copy.decorDisplacement;
      modelTypes = copy.modelTypes;
      interactType = copy.interactType;
      interactiveState = copy.interactiveState;
      clipped = copy.clipped;
      offsetY = copy.offsetY;
      recolorFrom = copy.recolorFrom;
      actions = copy.actions;
      retextureTo = copy.retextureTo;
      retextureFrom = copy.retextureFrom;
      transforms = copy.transforms;
      modelIds = copy.modelIds;
   }


   public final Model getModelDynamic(int var1, int type, int[][] var3, int var4, int var5, int var6, SequenceDefinition animation, int frame) {
      long var9;
      if(this.modelTypes == null) {
         var9 = type + (this.id << 10);
      } else {
         var9 = type + (var1 << 3) + (this.id << 10);
      }

      Model model = (Model)ObjectDefinition_cachedModels.get(var9);
      if(model == null) {
         ModelData var12 = this.getModelData(var1, type);
         if(var12 == null) {
            return null;
         }

         model = var12.toModel(this.ambient + 64, this.contrast + 768, -50, -10, -50);
         ObjectDefinition_cachedModels.put(model, var9);
      }

      animation = null;

      if(animation == null && this.clipType == -1) {
         return model;
      } else {
         if(animation != null) {
            model = animation.animateObject(model, frame, type);
         } else {
            model = model.toSharedSequenceModel(true);
         }

         if(this.clipType >= 0) {
            model = model.contourGround(var3, var4, var5, var6, false, this.clipType);
         }

         return model;
      }
   }
}
