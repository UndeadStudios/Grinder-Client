package com.runescape.cache;

import com.runescape.collection.IntHashTable;
import com.runescape.io.Buffer;
import com.runescape.util.ByteArrayUtil;

import java.util.Arrays;

public abstract class AbstractIndexCache {

    public static GzipDecompressor gzipDecompressor;
    public static int always0;

    int archiveCount;
    int[] archiveIds;
    int[] archiveNameHashes;
    IntHashTable archiveNameHashTable;
    int[] archiveCrcs;
    int[] archiveVersions;
    int[] recordCounts;
    int[][] recordIds;
    int[][] recordNameHashes;
    IntHashTable[] recordNameHashTables;
    Object[] archives;
    Object[][] records;
    public int hash;
    boolean releaseArchives;
    boolean shallowRecords;

    static {
        gzipDecompressor = new GzipDecompressor();
        always0 = 0;
    }

    AbstractIndexCache(boolean releaseArchives, boolean shallowRecords) {
        this.releaseArchives = releaseArchives;
        this.shallowRecords = shallowRecords;
    }

    void setIndexReference(byte[] data) {

        hash = HashUtil.createHash(data, data.length);

        Buffer buffer = new Buffer(ByteArrayUtil.decompressBytes(data));

        int revision  = buffer.readUnsignedByte();

        if(revision  >= 5 && revision  <= 7) {

            if(revision  >= 6)
                buffer.readInt();

            int archiveNameFlag = buffer.getUnsignedByte();
            if(revision  >= 7)
                archiveCount = buffer.readIntOrShort();
            else
                archiveCount = buffer.getUnsignedLEShort();

            int var5 = 0;
            int maxArchiveId = -1;
            archiveIds = new int[archiveCount];
            int i;
            if(revision  >= 7) {
                for(i = 0; i < archiveCount; ++i) {
                    archiveIds[i] = var5 += buffer.readIntOrShort();
                    if(archiveIds[i] > maxArchiveId) {
                        maxArchiveId = archiveIds[i];
                    }
                }
            } else {
                for(i = 0; i < archiveCount; ++i) {
                    archiveIds[i] = var5 += buffer.getUnsignedLEShort();
                    if(archiveIds[i] > maxArchiveId) {
                        maxArchiveId = archiveIds[i];
                    }
                }
            }

            archiveCrcs = new int[maxArchiveId + 1];
            archiveVersions = new int[maxArchiveId + 1];
            recordCounts = new int[maxArchiveId + 1];
            recordIds = new int[maxArchiveId + 1][];
            archives = new Object[maxArchiveId + 1];
            records = new Object[maxArchiveId + 1][];

            if(archiveNameFlag != 0) {
                archiveNameHashes = new int[maxArchiveId + 1];
                for(i = 0; i < archiveCount; ++i)
                    archiveNameHashes[archiveIds[i]] = buffer.readInt();
                archiveNameHashTable = new IntHashTable(archiveNameHashes);
            }

            for(i = 0; i < archiveCount; ++i)
                archiveCrcs[archiveIds[i]] = buffer.readInt();

            for(i = 0; i < archiveCount; ++i)
                archiveVersions[archiveIds[i]] = buffer.readInt();

            for(i = 0; i < archiveCount; ++i)
                recordCounts[archiveIds[i]] = buffer.getUnsignedLEShort();

            int var8;
            int var9;
            int var10;
            int var11;
            int var12;
            if(revision  >= 7) {
                for(i = 0; i < archiveCount; ++i) {
                    var8 = archiveIds[i];
                    var9 = recordCounts[var8];
                    var5 = 0;
                    var10 = -1;
                    recordIds[var8] = new int[var9];

                    for(var11 = 0; var11 < var9; ++var11) {
                        var12 = recordIds[var8][var11] = var5 += buffer.readIntOrShort();
                        if(var12 > var10) {
                            var10 = var12;
                        }
                    }

                    records[var8] = new Object[var10 + 1];
                }
            } else {
                for(i = 0; i < archiveCount; ++i) {
                    var8 = archiveIds[i];
                    var9 = recordCounts[var8];
                    var5 = 0;
                    var10 = -1;
                    recordIds[var8] = new int[var9];

                    for(var11 = 0; var11 < var9; ++var11) {
                        var12 = recordIds[var8][var11] = var5 += buffer.getUnsignedLEShort();
                        if(var12 > var10) {
                            var10 = var12;
                        }
                    }

                    records[var8] = new Object[var10 + 1];
                }
            }

            if(archiveNameFlag != 0) {
                recordNameHashes = new int[maxArchiveId + 1][];
                recordNameHashTables = new IntHashTable[maxArchiveId + 1];

                for(i = 0; i < archiveCount; ++i) {
                    var8 = archiveIds[i];
                    var9 = recordCounts[var8];
                    recordNameHashes[var8] = new int[records[var8].length];

                    for(var10 = 0; var10 < var9; ++var10) {
                        recordNameHashes[var8][recordIds[var8][var10]] = buffer.readInt();
                    }

                    recordNameHashTables[var8] = new IntHashTable(recordNameHashes[var8]);
                }
            }

        } else {
            throw new RuntimeException("");
        }
    }

    void requestFile(int var1) {
    }

    public byte[] takeRecord(int var1, int var2) {
        return takeRecordEncrypted(var1, var2, null);
    }
    public byte[] takeRecordEncrypted(int var1, int var2, int[] var3) {
        if(var1 >= 0 && var1 < records.length && records[var1] != null && var2 >= 0 && var2 < records[var1].length) {
            if(records[var1][var2] == null) {
                boolean var4 = buildRecords(var1, var3);
                if(!var4) {
                    loadArchive(var1);
                    var4 = buildRecords(var1, var3);
                    if(!var4) {
                        return null;
                    }
                }
            }

            byte[] var5 = ByteArrayUtil.byteArrayFromObject(records[var1][var2], false);
            if(shallowRecords) {
                records[var1][var2] = null;
            }

            return var5;
        } else {
            return null;
        }
    }

    public boolean tryLoadRecord(int var1, int var2) {
        if(var1 >= 0 && var1 < records.length && records[var1] != null && var2 >= 0 && var2 < records[var1].length) {
            if(records[var1][var2] != null) {
                return true;
            } else if(archives[var1] != null) {
                return true;
            } else {
                loadArchive(var1);
                return archives[var1] != null;
            }
        } else {
            return false;
        }
    }

    public boolean __u_393(int var1) {
        if(records.length == 1) {
            return tryLoadRecord(0, var1);
        } else if(records[var1].length == 1) {
            return tryLoadRecord(var1, 0);
        } else {
            throw new RuntimeException();
        }
    }

    public boolean tryLoadArchive(int var1) {
        if(archives[var1] != null) {
            return true;
        } else {
            loadArchive(var1);
            return archives[var1] != null;
        }
    }

    public boolean __l_394() {
        boolean var1 = true;

        for (int var3 : archiveIds) {
            if (archives[var3] == null) {
                loadArchive(var3);
                if (archives[var3] == null) {
                    var1 = false;
                }
            }
        }

        return var1;
    }

    int archiveLoadPercent(int var1) {
        return archives[var1] != null?100:0;
    }

    public byte[] takeRecordFlat(int var1) {
        if(records.length == 1) {
            return takeRecord(0, var1);
        } else if(records[var1].length == 1) {
            return takeRecord(var1, 0);
        } else {
            throw new RuntimeException();
        }
    }

    public byte[] getRecord(int var1, int var2) {
        if(var1 >= 0 && var1 < records.length && records[var1] != null && var2 >= 0 && var2 < records[var1].length) {
            if(records[var1][var2] == null) {
                boolean var3 = buildRecords(var1, null);
                if(!var3) {
                    loadArchive(var1);
                    var3 = buildRecords(var1, null);
                    if(!var3) {
                        return null;
                    }
                }
            }

            return ByteArrayUtil.byteArrayFromObject(records[var1][var2], false);
        } else {
            return null;
        }
    }

    public byte[] getRecordFlat(int var1) {
        if(records.length == 1) {
            return getRecord(0, var1);
        } else if(records[var1].length == 1) {
            return getRecord(var1, 0);
        } else {
            throw new RuntimeException();
        }
    }

    void loadArchive(int var1) {
    }

    public int[] getRedcordOrNull(int var1) {
        return var1 >= 0 && var1 < recordIds.length
                ?recordIds[var1]
                :null;
    }

    public int getRecordLength(int var1) {
        return records[var1].length;
    }

    public int recordsLength() {
        return records.length;
    }

    public void clearArchives() {
        Arrays.fill(archives, null);
    }

    public void clearRecords(int var1) {
        Arrays.fill(records[var1], null);
    }

    public void clearAllRecords() {
        if(records != null) {
            for (Object[] record : records) {
                if (record != null) {
                    Arrays.fill(record, null);
                }
            }
        }
    }

    boolean buildRecords(int var1, int[] archiveData) {
        if(archives[var1] == null) {
            return false;
        } else {
            int var3 = recordCounts[var1];
            int[] var4 = recordIds[var1];
            Object[] var5 = records[var1];
            boolean var6 = true;

            for(int var7 = 0; var7 < var3; ++var7) {
                if(var5[var4[var7]] == null) {
                    var6 = false;
                    break;
                }
            }

            if(var6) {
                return true;
            } else {
                byte[] data;
                if(archiveData == null || archiveData[0] == 0 && archiveData[1] == 0 && archiveData[2] == 0 && archiveData[3] == 0) {
                    data = ByteArrayUtil.byteArrayFromObject(archives[var1], false);
                } else {
                    data = ByteArrayUtil.byteArrayFromObject(archives[var1], true);
                    Buffer decryptBuffer = new Buffer(data);
                    decryptBuffer.xteaDecrypt(archiveData, 5, decryptBuffer.array.length);
                }

                byte[] var20 = ByteArrayUtil.decompressBytes(data);
                if(releaseArchives) {
                    archives[var1] = null;
                }

                if(var3 > 1) {
                    int var9 = var20.length;
                    --var9;
                    int var10 = var20[var9] & 255;
                    var9 -= var10 * var3 * 4;
                    Buffer var11 = new Buffer(var20);
                    int[] var12 = new int[var3];
                    var11.index = var9;

                    int var14;
                    int var15;
                    for(int var13 = 0; var13 < var10; ++var13) {
                        var14 = 0;

                        for(var15 = 0; var15 < var3; ++var15) {
                            var14 += var11.readInt();
                            var12[var15] += var14;
                        }
                    }

                    byte[][] var19 = new byte[var3][];

                    for(var14 = 0; var14 < var3; ++var14) {
                        var19[var14] = new byte[var12[var14]];
                        var12[var14] = 0;
                    }

                    var11.index = var9;
                    var14 = 0;

                    for(var15 = 0; var15 < var10; ++var15) {
                        int var16 = 0;

                        for(int var17 = 0; var17 < var3; ++var17) {
                            var16 += var11.readInt();
                            System.arraycopy(var20, var14, var19[var17], var12[var17], var16);
                            var12[var17] += var16;
                            var14 += var16;
                        }
                    }

                    for(var15 = 0; var15 < var3; ++var15) {
                        if(!shallowRecords) {
                            var5[var4[var15]] = ByteArrayUtil.byteArrayToObject(var19[var15], false);
                        } else {
                            var5[var4[var15]] = var19[var15];
                        }
                    }
                } else if(!shallowRecords) {
                    var5[var4[0]] = ByteArrayUtil.byteArrayToObject(var20, false);
                } else {
                    var5[var4[0]] = var20;
                }

                return true;
            }
        }
    }

    public int getArchiveId(String var1) {
        var1 = var1.toLowerCase();
        return archiveNameHashTable.get(Strings.hashString(var1));
    }

    public int getRecordId(int var1, String var2) {
        var2 = var2.toLowerCase();
        return recordNameHashTables[var1].get(Strings.hashString(var2));
    }

    public boolean __ag_401(String var1, String var2) {
        var1 = var1.toLowerCase();
        var2 = var2.toLowerCase();
        int var3 = archiveNameHashTable.get(Strings.hashString(var1));
        if(var3 < 0) {
            return false;
        } else {
            int var4 = recordNameHashTables[var3].get(Strings.hashString(var2));
            return var4 >= 0;
        }
    }

    public byte[] takeRecordByNames(String var1, String var2) {
        var1 = var1.toLowerCase();
        var2 = var2.toLowerCase();
        int var3 = archiveNameHashTable.get(Strings.hashString(var1));
        int var4 = recordNameHashTables[var3].get(Strings.hashString(var2));
        return takeRecord(var3, var4);
    }

    public boolean tryLoadRecordByNames(String var1, String var2) {
        var1 = var1.toLowerCase();
        var2 = var2.toLowerCase();
        int var3 = archiveNameHashTable.get(Strings.hashString(var1));
        int var4 = recordNameHashTables[var3].get(Strings.hashString(var2));
        return tryLoadRecord(var3, var4);
    }

    public boolean tryLoadArchiveByName(String var1) {
        var1 = var1.toLowerCase();
        int var2 = archiveNameHashTable.get(Strings.hashString(var1));
        return tryLoadArchive(var2);
    }

    public void requestFile(String var1) {
        var1 = var1.toLowerCase();
        int var2 = archiveNameHashTable.get(Strings.hashString(var1));
        if(var2 >= 0) {
            requestFile(var2);
        }
    }

    public int archiveLoadPercentByName(String archiveName) {
        archiveName = archiveName.toLowerCase();
        int archiveId = archiveNameHashTable.get(Strings.hashString(archiveName));
        return archiveLoadPercent(archiveId);
    }

    public int getArchiveCount() {
        return archiveCount;
    }
}
