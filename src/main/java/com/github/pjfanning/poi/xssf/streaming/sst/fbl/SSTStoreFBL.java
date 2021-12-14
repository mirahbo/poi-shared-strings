package com.github.pjfanning.poi.xssf.streaming.sst.fbl;

import com.github.pjfanning.poi.xssf.streaming.sst.SSTStore;
import org.apache.poi.util.TempFile;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRst;

import java.io.File;
import java.util.Iterator;

public class SSTStoreFBL implements SSTStore {

    private static final int DEFAULT_CACHE_CAPACITY = 100;
    /**
     * Array of individual string items in the Shared String table.
     */
    private final FileBackedList strings;
    /**
     * Maps strings and their indexes in the <code>strings</code> arrays
     */
    private final MVMap<String, Integer> stmap;
    private File stringsTempFile;
    private File stmapTempFile;
    private MVStore mvStore;

    public SSTStoreFBL() {
        this(DEFAULT_CACHE_CAPACITY);
    }

    public SSTStoreFBL(int cacheCapacity) {
        try {
            stringsTempFile = TempFile.createTempFile("poi-shared-strings", ".tmp");
            stmapTempFile = TempFile.createTempFile("poi-shared-strings-stmap", ".tmp");
            strings = new FileBackedList(stringsTempFile, cacheCapacity);

            MVStore.Builder mvStoreBuilder = new MVStore.Builder();
            mvStoreBuilder.fileName(stmapTempFile.getAbsolutePath());
            mvStore = mvStoreBuilder.open();
            stmap = mvStore.openMap("stmap");
        } catch (Exception e) {
            close();
            throw new RuntimeException(e);
        }
    }

    @Override
    public CTRst putCTRst(Integer idx, CTRst st) {
        strings.add(st.toString());
        return st;
    }

    @Override
    public CTRst getCTRst(Integer idx) {
        return new XSSFRichTextString(strings.getAt(idx)).getCTRst();
    }

    @Override
    public Integer putStringIndex(String s, Integer idx) {
        return stmap.put(s, idx);
    }

    @Override
    public Integer getStringIndex(String s) {
        return stmap.get(s);
    }

    @Override
    public boolean containsString(String s) {
        return stmap.containsKey(s);
    }

    @Override
    public Iterator<Integer> keyIterator() {
        return strings.keyIterator();
    }

    @Override
    public void close() {
        if (mvStore != null) {
            mvStore.closeImmediately();
        }
        if (stringsTempFile != null) {
            stringsTempFile.delete();
        }
        if (stmapTempFile != null) {
            stmapTempFile.delete();
        }
    }
}
