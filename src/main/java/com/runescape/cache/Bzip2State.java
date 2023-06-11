package com.runescape.cache;


public final class Bzip2State {

   final int __m;
   final int __f;
   final int __q;
   final int __w;
   final int __o;
   final int __u;
   byte[] __g;
   int __l;
   int __e;
   byte[] __x;
   int __d;
   int __k;
   int __n;
   byte __i;
   int __a;
   int __z;
   int __j;
   int __s;
   int __t;
   int __y;
   int __h;
   int[] __b;
   int __c;
   int[] __r;
   int __v;
   boolean[] __ag;
   boolean[] __aq;
   byte[] __aj;
   byte[] __av;
   int[] __ar;
   byte[] __ac;
   byte[] __ay;
   byte[][] __ah;
   int[][] __ak;
   int[][] __aw;
   int[][] __al;
   int[] __ab;
   int __ae;

   Bzip2State() {
      this.__m = 4096;
      this.__f = 16;
      this.__q = 258;
      this.__w = 6;
      this.__o = 50;
      this.__u = 18002;
      this.__l = 0;
      this.__d = 0;
      this.__b = new int[256];
      this.__r = new int[257];
      this.__ag = new boolean[256];
      this.__aq = new boolean[16];
      this.__aj = new byte[256];
      this.__av = new byte[4096];
      this.__ar = new int[16];
      this.__ac = new byte[18002];
      this.__ay = new byte[18002];
      this.__ah = new byte[6][258];
      this.__ak = new int[6][258];
      this.__aw = new int[6][258];
      this.__al = new int[6][258];
      this.__ab = new int[6];
   }
}
