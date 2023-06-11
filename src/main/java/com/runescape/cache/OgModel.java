package com.runescape.cache;

import com.runescape.draw.Rasterizer3D;
import com.runescape.entity.Renderable;

public class OgModel extends Renderable {

   static OgModel Model_sharedSequenceModel;
   static byte[] Model_sharedSequenceModelFaceAlphas;
   static OgModel Model_sharedSpotAnimationModel;
   static byte[] Model_sharedSpotAnimationModelFaceAlphas;
   static boolean[] indicesOutOfBounds;
   static boolean[] indicesOutOfReach;
   static int[] projected_vertex_x;
   static int[] projected_vertex_y;
   static int[] projected_vertex_z;
   static int[] __du_an;
   static int[] __du_az;
   static int[] __du_au;
   static int[] __du_aa;
   static int[][] __du_ax;
   static int[] __du_af;
   static int[][] __du_ai;
   static int[] __du_ba;
   static int[] __du_bb;
   static int[] __du_bs;
   static int[] __du_bq;
   static int[] __du_bn;
   static int[] __du_bk;
   static int Model_transformTempX;
   static int Model_transformTempY;
   static int Model_transformTempZ;
   static boolean __du_bx;
   static int[] Model_sine;
   static int[] Model_cosine;
   static int[] Model_palette;
   static int[] __du_bj;
   int verticesCount;
   int[] verticesX;
   int[] verticesY;
   int[] verticesZ;
   int indicesCount;
   int[] indices1;
   int[] indices2;
   int[] indices3;
   int[] faceColors1;
   int[] faceColors2;
   int[] faceColors3;
   byte[] faceRenderPriorities;
   byte[] faceAlphas;
   byte[] texture_coordinates;
   short[] faceTextures;
   byte face_priority;
   int numberOfTexturesFaces;
   int[] textures_face_a;
   int[] textures_face_b;
   int[] textures_face_c;
   int[][] vertexLabels;
   int[][] faceLabelsAlpha;
   public boolean isSingleTile;
   int boundsType;
   int bottomY;
   int xzRadius;
   int diameter;
   int radius;
   int xMid;
   int yMid;
   int zMid;
   int xMidOffset;
   int yMidOffset;
   int zMidOffset;

   static {
      Model_sharedSequenceModel = new OgModel();
      Model_sharedSequenceModelFaceAlphas = new byte[1];
      Model_sharedSpotAnimationModel = new OgModel();
      Model_sharedSpotAnimationModelFaceAlphas = new byte[1];
      indicesOutOfBounds = new boolean[4700];
      indicesOutOfReach = new boolean[4700];
      projected_vertex_x = new int[4700];
      projected_vertex_y = new int[4700];
      projected_vertex_z = new int[4700];
      __du_an = new int[4700];
      __du_az = new int[4700];
      __du_au = new int[4700];
      __du_aa = new int[1600];
      __du_ax = new int[1600][512];
      __du_af = new int[12];
      __du_ai = new int[12][2000];
      __du_ba = new int[2000];
      __du_bb = new int[2000];
      __du_bs = new int[12];
      __du_bq = new int[10];
      __du_bn = new int[10];
      __du_bk = new int[10];
      __du_bx = true;
      Model_sine = Rasterizer3D.Rasterizer3D_sine;
      Model_cosine = Rasterizer3D.Rasterizer3D_cosine;
      Model_palette = Rasterizer3D.Rasterizer3D_colorPalette;
      __du_bj = Rasterizer3D.__et_p;
   }

   OgModel() {
      this.verticesCount = 0;
      this.indicesCount = 0;
      this.face_priority = 0;
      this.numberOfTexturesFaces = 0;
      this.isSingleTile = false;
      this.xMidOffset = -1;
      this.yMidOffset = -1;
      this.zMidOffset = -1;
   }

   @Override
   public void renderDraw(int i, int j, int k, int l, int i1, int j1, int k1, int l1, long uid) {
//       super.renderDraw(i, j, k, l, i1, j1, k1, l1, uid);

      System.out.println("RENDERING NOT IMPLEMENTED!");
   }

   public OgModel(OgModel[] var1, int var2) {
      this.verticesCount = 0;
      this.indicesCount = 0;
      this.face_priority = 0;
      this.numberOfTexturesFaces = 0;
      this.isSingleTile = false;
      this.xMidOffset = -1;
      this.yMidOffset = -1;
      this.zMidOffset = -1;
      boolean var3 = false;
      boolean var4 = false;
      boolean var5 = false;
      boolean var6 = false;
      this.verticesCount = 0;
      this.indicesCount = 0;
      this.numberOfTexturesFaces = 0;
      this.face_priority = -1;

      int var7;
      OgModel var8;
      for(var7 = 0; var7 < var2; ++var7) {
         var8 = var1[var7];
         if(var8 != null) {
            this.verticesCount += var8.verticesCount;
            this.indicesCount += var8.indicesCount;
            this.numberOfTexturesFaces += var8.numberOfTexturesFaces;
            if(var8.faceRenderPriorities != null) {
               var3 = true;
            } else {
               if(this.face_priority == -1) {
                  this.face_priority = var8.face_priority;
               }

               if(this.face_priority != var8.face_priority) {
                  var3 = true;
               }
            }

            var4 |= var8.faceAlphas != null;
            var5 |= var8.faceTextures != null;
            var6 |= var8.texture_coordinates != null;
         }
      }

      this.verticesX = new int[this.verticesCount];
      this.verticesY = new int[this.verticesCount];
      this.verticesZ = new int[this.verticesCount];
      this.indices1 = new int[this.indicesCount];
      this.indices2 = new int[this.indicesCount];
      this.indices3 = new int[this.indicesCount];
      this.faceColors1 = new int[this.indicesCount];
      this.faceColors2 = new int[this.indicesCount];
      this.faceColors3 = new int[this.indicesCount];
      if(var3) {
         this.faceRenderPriorities = new byte[this.indicesCount];
      }

      if(var4) {
         this.faceAlphas = new byte[this.indicesCount];
      }

      if(var5) {
         this.faceTextures = new short[this.indicesCount];
      }

      if(var6) {
         this.texture_coordinates = new byte[this.indicesCount];
      }

      if(this.numberOfTexturesFaces > 0) {
         this.textures_face_a = new int[this.numberOfTexturesFaces];
         this.textures_face_b = new int[this.numberOfTexturesFaces];
         this.textures_face_c = new int[this.numberOfTexturesFaces];
      }

      this.verticesCount = 0;
      this.indicesCount = 0;
      this.numberOfTexturesFaces = 0;

      for(var7 = 0; var7 < var2; ++var7) {
         var8 = var1[var7];
         if(var8 != null) {
            int var9;
            for(var9 = 0; var9 < var8.indicesCount; ++var9) {
               this.indices1[this.indicesCount] = this.verticesCount + var8.indices1[var9];
               this.indices2[this.indicesCount] = this.verticesCount + var8.indices2[var9];
               this.indices3[this.indicesCount] = this.verticesCount + var8.indices3[var9];
               this.faceColors1[this.indicesCount] = var8.faceColors1[var9];
               this.faceColors2[this.indicesCount] = var8.faceColors2[var9];
               this.faceColors3[this.indicesCount] = var8.faceColors3[var9];
               if(var3) {
                  if(var8.faceRenderPriorities != null) {
                     this.faceRenderPriorities[this.indicesCount] = var8.faceRenderPriorities[var9];
                  } else {
                     this.faceRenderPriorities[this.indicesCount] = var8.face_priority;
                  }
               }

               if(var4 && var8.faceAlphas != null) {
                  this.faceAlphas[this.indicesCount] = var8.faceAlphas[var9];
               }

               if(var5) {
                  if(var8.faceTextures != null) {
                     this.faceTextures[this.indicesCount] = var8.faceTextures[var9];
                  } else {
                     this.faceTextures[this.indicesCount] = -1;
                  }
               }

               if(var6) {
                  if(var8.texture_coordinates != null && var8.texture_coordinates[var9] != -1) {
                     this.texture_coordinates[this.indicesCount] = (byte)(this.numberOfTexturesFaces + var8.texture_coordinates[var9]);
                  } else {
                     this.texture_coordinates[this.indicesCount] = -1;
                  }
               }

               ++this.indicesCount;
            }

            for(var9 = 0; var9 < var8.numberOfTexturesFaces; ++var9) {
               this.textures_face_a[this.numberOfTexturesFaces] = this.verticesCount + var8.textures_face_a[var9];
               this.textures_face_b[this.numberOfTexturesFaces] = this.verticesCount + var8.textures_face_b[var9];
               this.textures_face_c[this.numberOfTexturesFaces] = this.verticesCount + var8.textures_face_c[var9];
               ++this.numberOfTexturesFaces;
            }

            for(var9 = 0; var9 < var8.verticesCount; ++var9) {
               this.verticesX[this.verticesCount] = var8.verticesX[var9];
               this.verticesY[this.verticesCount] = var8.verticesY[var9];
               this.verticesZ[this.verticesCount] = var8.verticesZ[var9];
               ++this.verticesCount;
            }
         }
      }

   }

   public OgModel contourGround(int[][] var1, int var2, int var3, int var4, boolean var5, int var6) {
      this.calculateBoundsCylinder();
      int var7 = var2 - this.xzRadius;
      int var8 = var2 + this.xzRadius;
      int var9 = var4 - this.xzRadius;
      int var10 = var4 + this.xzRadius;
      if(var7 >= 0 && var8 + 128 >> 7 < var1.length && var9 >= 0 && var10 + 128 >> 7 < var1[0].length) {
         var7 >>= 7;
         var8 = var8 + 127 >> 7;
         var9 >>= 7;
         var10 = var10 + 127 >> 7;
         if(var3 == var1[var7][var9] && var3 == var1[var8][var9] && var3 == var1[var7][var10] && var3 == var1[var8][var10]) {
            return this;
         } else {
            OgModel var11;
            if(var5) {
               var11 = new OgModel();
               var11.verticesCount = this.verticesCount;
               var11.indicesCount = this.indicesCount;
               var11.numberOfTexturesFaces = this.numberOfTexturesFaces;
               var11.verticesX = this.verticesX;
               var11.verticesZ = this.verticesZ;
               var11.indices1 = this.indices1;
               var11.indices2 = this.indices2;
               var11.indices3 = this.indices3;
               var11.faceColors1 = this.faceColors1;
               var11.faceColors2 = this.faceColors2;
               var11.faceColors3 = this.faceColors3;
               var11.faceRenderPriorities = this.faceRenderPriorities;
               var11.faceAlphas = this.faceAlphas;
               var11.texture_coordinates = this.texture_coordinates;
               var11.faceTextures = this.faceTextures;
               var11.face_priority = this.face_priority;
               var11.textures_face_a = this.textures_face_a;
               var11.textures_face_b = this.textures_face_b;
               var11.textures_face_c = this.textures_face_c;
               var11.vertexLabels = this.vertexLabels;
               var11.faceLabelsAlpha = this.faceLabelsAlpha;
               var11.isSingleTile = this.isSingleTile;
               var11.verticesY = new int[var11.verticesCount];
            } else {
               var11 = this;
            }

            int var12;
            int var13;
            int var14;
            int var15;
            int var16;
            int var17;
            int var18;
            int var19;
            int var20;
            int var21;
            if(var6 == 0) {
               for(var12 = 0; var12 < var11.verticesCount; ++var12) {
                  var13 = var2 + this.verticesX[var12];
                  var14 = var4 + this.verticesZ[var12];
                  var15 = var13 & 127;
                  var16 = var14 & 127;
                  var17 = var13 >> 7;
                  var18 = var14 >> 7;
                  var19 = var1[var17][var18] * (128 - var15) + var1[var17 + 1][var18] * var15 >> 7;
                  var20 = var1[var17][var18 + 1] * (128 - var15) + var15 * var1[var17 + 1][var18 + 1] >> 7;
                  var21 = var19 * (128 - var16) + var20 * var16 >> 7;
                  var11.verticesY[var12] = var21 + this.verticesY[var12] - var3;
               }
            } else {
               for(var12 = 0; var12 < var11.verticesCount; ++var12) {
                  var13 = (-this.verticesY[var12] << 16) / super.height;
                  if(var13 < var6) {
                     var14 = var2 + this.verticesX[var12];
                     var15 = var4 + this.verticesZ[var12];
                     var16 = var14 & 127;
                     var17 = var15 & 127;
                     var18 = var14 >> 7;
                     var19 = var15 >> 7;
                     var20 = var1[var18][var19] * (128 - var16) + var1[var18 + 1][var19] * var16 >> 7;
                     var21 = var1[var18][var19 + 1] * (128 - var16) + var16 * var1[var18 + 1][var19 + 1] >> 7;
                     int var22 = var20 * (128 - var17) + var21 * var17 >> 7;
                     var11.verticesY[var12] = (var6 - var13) * (var22 - var3) / var6 + this.verticesY[var12];
                  }
               }
            }

            var11.resetBounds();
            return var11;
         }
      } else {
         return this;
      }
   }

   public OgModel toSharedSequenceModel(boolean var1) {
      if(!var1 && Model_sharedSequenceModelFaceAlphas.length < this.indicesCount) {
         Model_sharedSequenceModelFaceAlphas = new byte[this.indicesCount + 100];
      }

      return this.buildSharedModel(var1, Model_sharedSequenceModel, Model_sharedSequenceModelFaceAlphas);
   }

   public OgModel toSharedSpotAnimationModel(boolean var1) {
      if(!var1 && Model_sharedSpotAnimationModelFaceAlphas.length < this.indicesCount) {
         Model_sharedSpotAnimationModelFaceAlphas = new byte[this.indicesCount + 100];
      }

      return this.buildSharedModel(var1, Model_sharedSpotAnimationModel, Model_sharedSpotAnimationModelFaceAlphas);
   }

   OgModel buildSharedModel(boolean var1, OgModel var2, byte[] var3) {
      var2.verticesCount = this.verticesCount;
      var2.indicesCount = this.indicesCount;
      var2.numberOfTexturesFaces = this.numberOfTexturesFaces;
      if(var2.verticesX == null || var2.verticesX.length < this.verticesCount) {
         var2.verticesX = new int[this.verticesCount + 100];
         var2.verticesY = new int[this.verticesCount + 100];
         var2.verticesZ = new int[this.verticesCount + 100];
      }

      int var4;
      for(var4 = 0; var4 < this.verticesCount; ++var4) {
         var2.verticesX[var4] = this.verticesX[var4];
         var2.verticesY[var4] = this.verticesY[var4];
         var2.verticesZ[var4] = this.verticesZ[var4];
      }

      if(var1) {
         var2.faceAlphas = this.faceAlphas;
      } else {
         var2.faceAlphas = var3;
         if(this.faceAlphas == null) {
            for(var4 = 0; var4 < this.indicesCount; ++var4) {
               var2.faceAlphas[var4] = 0;
            }
         } else {
            for(var4 = 0; var4 < this.indicesCount; ++var4) {
               var2.faceAlphas[var4] = this.faceAlphas[var4];
            }
         }
      }

      var2.indices1 = this.indices1;
      var2.indices2 = this.indices2;
      var2.indices3 = this.indices3;
      var2.faceColors1 = this.faceColors1;
      var2.faceColors2 = this.faceColors2;
      var2.faceColors3 = this.faceColors3;
      var2.faceRenderPriorities = this.faceRenderPriorities;
      var2.texture_coordinates = this.texture_coordinates;
      var2.faceTextures = this.faceTextures;
      var2.face_priority = this.face_priority;
      var2.textures_face_a = this.textures_face_a;
      var2.textures_face_b = this.textures_face_b;
      var2.textures_face_c = this.textures_face_c;
      var2.vertexLabels = this.vertexLabels;
      var2.faceLabelsAlpha = this.faceLabelsAlpha;
      var2.isSingleTile = this.isSingleTile;
      var2.resetBounds();
      return var2;
   }

   void calculateBoundingBox(int var1) {
      if(this.xMidOffset == -1) {
         int var2 = 0;
         int minY = 0;
         int var4 = 0;
         int var5 = 0;
         int var6 = 0;
         int var7 = 0;
         int var8 = Model_cosine[var1];
         int var9 = Model_sine[var1];

         for(int v = 0; v < this.verticesCount; ++v) {
            int var11 = Rasterizer3D.method3039(this.verticesX[v], this.verticesZ[v], var8, var9);
            int y = this.verticesY[v];
            int var13 = Rasterizer3D.method3004(this.verticesX[v], this.verticesZ[v], var8, var9);
            if(var11 < var2) {
               var2 = var11;
            }

            if(var11 > var5) {
               var5 = var11;
            }

            if(y < minY) {
               minY = y;
            }

            if(y > var6) {
               var6 = y;
            }

            if(var13 < var4) {
               var4 = var13;
            }

            if(var13 > var7) {
               var7 = var13;
            }
         }

         this.xMid = (var5 + var2) / 2;
         this.yMid = (var6 + minY) / 2;
         this.zMid = (var7 + var4) / 2;
         this.xMidOffset = (var5 - var2 + 1) / 2;
         this.yMidOffset = (var6 - minY + 1) / 2;
         this.zMidOffset = (var7 - var4 + 1) / 2;
         if(this.xMidOffset < 32) {
            this.xMidOffset = 32;
         }

         if(this.zMidOffset < 32) {
            this.zMidOffset = 32;
         }

         if(this.isSingleTile) {
            this.xMidOffset += 8;
            this.zMidOffset += 8;
         }

      }
   }

   public void calculateBoundsCylinder() {
      if(this.boundsType != 1) {
         this.boundsType = 1;
         super.height = 0;
         this.bottomY = 0;
         this.xzRadius = 0;

         for(int var1 = 0; var1 < this.verticesCount; ++var1) {
            int var2 = this.verticesX[var1];
            int var3 = this.verticesY[var1];
            int var4 = this.verticesZ[var1];
            if(-var3 > super.height) {
               super.height = -var3;
            }

            if(var3 > this.bottomY) {
               this.bottomY = var3;
            }

            int var5 = var2 * var2 + var4 * var4;
            if(var5 > this.xzRadius) {
               this.xzRadius = var5;
            }
         }

         this.xzRadius = (int)(Math.sqrt((double)this.xzRadius) + 0.99D);
         this.radius = (int)(Math.sqrt((double)(this.xzRadius * this.xzRadius + super.height * super.height)) + 0.99D);
         this.diameter = this.radius + (int)(Math.sqrt((double)(this.xzRadius * this.xzRadius + this.bottomY * this.bottomY)) + 0.99D);
      }
   }

   void __g_232() {
      if(this.boundsType != 2) {
         this.boundsType = 2;
         this.xzRadius = 0;

         for(int var1 = 0; var1 < this.verticesCount; ++var1) {
            int var2 = this.verticesX[var1];
            int var3 = this.verticesY[var1];
            int var4 = this.verticesZ[var1];
            int var5 = var2 * var2 + var4 * var4 + var3 * var3;
            if(var5 > this.xzRadius) {
               this.xzRadius = var5;
            }
         }

         this.xzRadius = (int)(Math.sqrt((double)this.xzRadius) + 0.99D);
         this.radius = this.xzRadius;
         this.diameter = this.xzRadius + this.xzRadius;
      }
   }

   public int __l_233() {
      this.calculateBoundsCylinder();
      return this.xzRadius;
   }

   void resetBounds() {
      this.boundsType = 0;
      this.xMidOffset = -1;
   }

   public void animate(Frames var1, int var2) {
      if(this.vertexLabels != null) {
         if(var2 != -1) {
            Animation var3 = var1.frames[var2];
            Skeleton var4 = var3.skeleton;
            Model_transformTempX = 0;
            Model_transformTempY = 0;
            Model_transformTempZ = 0;

            for(int var5 = 0; var5 < var3.transformCount; ++var5) {
               int var6 = var3.transformSkeletonLabels[var5];
               this.transform(var4.getTransformTypes()[var6], var4.getLabels()[var6], var3.transformXs[var5], var3.transformYs[var5], var3.transformZs[var5]);
            }

            this.resetBounds();
         }
      }
   }

   public void animate2(Frames var1, int var2, Frames var3, int var4, int[] var5) {
      if(var2 != -1) {
         if(var5 != null && var4 != -1) {
            Animation var6 = var1.frames[var2];
            Animation var7 = var3.frames[var4];
            Skeleton var8 = var6.skeleton;
            Model_transformTempX = 0;
            Model_transformTempY = 0;
            Model_transformTempZ = 0;
            byte var9 = 0;
            int var13 = var9 + 1;
            int var10 = var5[var9];

            int var11;
            int var12;
            for(var11 = 0; var11 < var6.transformCount; ++var11) {
               for(var12 = var6.transformSkeletonLabels[var11]; var12 > var10; var10 = var5[var13++]) {
                  ;
               }

               if(var12 != var10 || var8.getTransformTypes()[var12] == 0) {
                  this.transform(var8.getTransformTypes()[var12], var8.getLabels()[var12], var6.transformXs[var11], var6.transformYs[var11], var6.transformZs[var11]);
               }
            }

            Model_transformTempX = 0;
            Model_transformTempY = 0;
            Model_transformTempZ = 0;
            var9 = 0;
            var13 = var9 + 1;
            var10 = var5[var9];

            for(var11 = 0; var11 < var7.transformCount; ++var11) {
               for(var12 = var7.transformSkeletonLabels[var11]; var12 > var10; var10 = var5[var13++]) {
                  ;
               }

               if(var12 == var10 || var8.getTransformTypes()[var12] == 0) {
                  this.transform(var8.getTransformTypes()[var12], var8.getLabels()[var12], var7.transformXs[var11], var7.transformYs[var11], var7.transformZs[var11]);
               }
            }

            this.resetBounds();
         } else {
            this.animate(var1, var2);
         }
      }
   }

   void transform(int var1, int[] var2, int var3, int var4, int var5) {
      int var6 = var2.length;
      int var7;
      int var8;
      int var11;
      int var12;
      if(var1 == 0) {
         var7 = 0;
         Model_transformTempX = 0;
         Model_transformTempY = 0;
         Model_transformTempZ = 0;

         for(var8 = 0; var8 < var6; ++var8) {
            int var9 = var2[var8];
            if(var9 < this.vertexLabels.length) {
               int[] var10 = this.vertexLabels[var9];

               for(var11 = 0; var11 < var10.length; ++var11) {
                  var12 = var10[var11];
                  Model_transformTempX += this.verticesX[var12];
                  Model_transformTempY += this.verticesY[var12];
                  Model_transformTempZ += this.verticesZ[var12];
                  ++var7;
               }
            }
         }

         if(var7 > 0) {
            Model_transformTempX = var3 + Model_transformTempX / var7;
            Model_transformTempY = var4 + Model_transformTempY / var7;
            Model_transformTempZ = var5 + Model_transformTempZ / var7;
         } else {
            Model_transformTempX = var3;
            Model_transformTempY = var4;
            Model_transformTempZ = var5;
         }

      } else {
         int[] var18;
         int var19;
         if(var1 == 1) {
            for(var7 = 0; var7 < var6; ++var7) {
               var8 = var2[var7];
               if(var8 < this.vertexLabels.length) {
                  var18 = this.vertexLabels[var8];

                  for(var19 = 0; var19 < var18.length; ++var19) {
                     var11 = var18[var19];
                     this.verticesX[var11] += var3;
                     this.verticesY[var11] += var4;
                     this.verticesZ[var11] += var5;
                  }
               }
            }

         } else if(var1 == 2) {
            for(var7 = 0; var7 < var6; ++var7) {
               var8 = var2[var7];
               if(var8 < this.vertexLabels.length) {
                  var18 = this.vertexLabels[var8];

                  for(var19 = 0; var19 < var18.length; ++var19) {
                     var11 = var18[var19];
                     this.verticesX[var11] -= Model_transformTempX;
                     this.verticesY[var11] -= Model_transformTempY;
                     this.verticesZ[var11] -= Model_transformTempZ;
                     var12 = (var3 & 255) * 8;
                     int var13 = (var4 & 255) * 8;
                     int var14 = (var5 & 255) * 8;
                     int var15;
                     int var16;
                     int var17;
                     if(var14 != 0) {
                        var15 = Model_sine[var14];
                        var16 = Model_cosine[var14];
                        var17 = var15 * this.verticesY[var11] + var16 * this.verticesX[var11] >> 16;
                        this.verticesY[var11] = var16 * this.verticesY[var11] - var15 * this.verticesX[var11] >> 16;
                        this.verticesX[var11] = var17;
                     }

                     if(var12 != 0) {
                        var15 = Model_sine[var12];
                        var16 = Model_cosine[var12];
                        var17 = var16 * this.verticesY[var11] - var15 * this.verticesZ[var11] >> 16;
                        this.verticesZ[var11] = var15 * this.verticesY[var11] + var16 * this.verticesZ[var11] >> 16;
                        this.verticesY[var11] = var17;
                     }

                     if(var13 != 0) {
                        var15 = Model_sine[var13];
                        var16 = Model_cosine[var13];
                        var17 = var15 * this.verticesZ[var11] + var16 * this.verticesX[var11] >> 16;
                        this.verticesZ[var11] = var16 * this.verticesZ[var11] - var15 * this.verticesX[var11] >> 16;
                        this.verticesX[var11] = var17;
                     }

                     this.verticesX[var11] += Model_transformTempX;
                     this.verticesY[var11] += Model_transformTempY;
                     this.verticesZ[var11] += Model_transformTempZ;
                  }
               }
            }

         } else if(var1 == 3) {
            for(var7 = 0; var7 < var6; ++var7) {
               var8 = var2[var7];
               if(var8 < this.vertexLabels.length) {
                  var18 = this.vertexLabels[var8];

                  for(var19 = 0; var19 < var18.length; ++var19) {
                     var11 = var18[var19];
                     this.verticesX[var11] -= Model_transformTempX;
                     this.verticesY[var11] -= Model_transformTempY;
                     this.verticesZ[var11] -= Model_transformTempZ;
                     this.verticesX[var11] = var3 * this.verticesX[var11] / 128;
                     this.verticesY[var11] = var4 * this.verticesY[var11] / 128;
                     this.verticesZ[var11] = var5 * this.verticesZ[var11] / 128;
                     this.verticesX[var11] += Model_transformTempX;
                     this.verticesY[var11] += Model_transformTempY;
                     this.verticesZ[var11] += Model_transformTempZ;
                  }
               }
            }

         } else if(var1 == 5) {
            if(this.faceLabelsAlpha != null && this.faceAlphas != null) {
               for(var7 = 0; var7 < var6; ++var7) {
                  var8 = var2[var7];
                  if(var8 < this.faceLabelsAlpha.length) {
                     var18 = this.faceLabelsAlpha[var8];

                     for(var19 = 0; var19 < var18.length; ++var19) {
                        var11 = var18[var19];
                        var12 = (this.faceAlphas[var11] & 255) + var3 * 8;
                        if(var12 < 0) {
                           var12 = 0;
                        } else if(var12 > 255) {
                           var12 = 255;
                        }

                        this.faceAlphas[var11] = (byte)var12;
                     }
                  }
               }
            }

         }
      }
   }

   public void rotateY90Ccw() {
      for(int var1 = 0; var1 < this.verticesCount; ++var1) {
         int var2 = this.verticesX[var1];
         this.verticesX[var1] = this.verticesZ[var1];
         this.verticesZ[var1] = -var2;
      }

      this.resetBounds();
   }

   public void rotateY180() {
      for(int var1 = 0; var1 < this.verticesCount; ++var1) {
         this.verticesX[var1] = -this.verticesX[var1];
         this.verticesZ[var1] = -this.verticesZ[var1];
      }

      this.resetBounds();
   }

   public void rotateY270Ccw() {
      for(int var1 = 0; var1 < this.verticesCount; ++var1) {
         int var2 = this.verticesZ[var1];
         this.verticesZ[var1] = this.verticesX[var1];
         this.verticesX[var1] = -var2;
      }

      this.resetBounds();
   }

   public void rotateZ(int var1) {
      int var2 = Model_sine[var1];
      int var3 = Model_cosine[var1];

      for(int var4 = 0; var4 < this.verticesCount; ++var4) {
         int var5 = var3 * this.verticesY[var4] - var2 * this.verticesZ[var4] >> 16;
         this.verticesZ[var4] = var2 * this.verticesY[var4] + var3 * this.verticesZ[var4] >> 16;
         this.verticesY[var4] = var5;
      }

      this.resetBounds();
   }

   public void offsetBy(int var1, int var2, int var3) {
      for(int var4 = 0; var4 < this.verticesCount; ++var4) {
         this.verticesX[var4] += var1;
         this.verticesY[var4] += var2;
         this.verticesZ[var4] += var3;
      }

      this.resetBounds();
   }

   public void scale(int var1, int var2, int var3) {
      for(int var4 = 0; var4 < this.verticesCount; ++var4) {
         this.verticesX[var4] = this.verticesX[var4] * var1 / 128;
         this.verticesY[var4] = var2 * this.verticesY[var4] / 128;
         this.verticesZ[var4] = var3 * this.verticesZ[var4] / 128;
      }

      this.resetBounds();
   }
}
