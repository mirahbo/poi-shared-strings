package com.github.pjfanning.poi.xssf.streaming.sst.h2;

import com.github.pjfanning.poi.xssf.streaming.Constants;
import com.github.pjfanning.poi.xssf.streaming.sst.SSTStore;
import org.apache.poi.util.TempFile;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRst;

import java.io.File;
import java.util.Base64;
import java.util.Iterator;

public class SSTStoreH2 implements SSTStore {

    /**
     * Array of individual string items in the Shared String table.
     */
    private final MVMap<Integer, CTRst> strings;
    /**
     * Maps strings and their indexes in the <code>strings</code> arrays
     */
    private final MVMap<String, Integer> stmap;
    private File tempFile;
    private MVStore mvStore;

    public SSTStoreH2(boolean encryptTempFiles) {
        try {
            tempFile = TempFile.createTempFile("poi-shared-strings", ".tmp");
            MVStore.Builder mvStoreBuilder = new MVStore.Builder();
            if (encryptTempFiles) {
                byte[] bytes = new byte[1024];
                Constants.RANDOM.nextBytes(bytes);
                mvStoreBuilder.encryptionKey(Base64.getEncoder().encodeToString(bytes).toCharArray());
            }
            mvStoreBuilder.fileName(tempFile.getAbsolutePath());
            mvStore = mvStoreBuilder.open();
            strings = mvStore.openMap("strings");
            stmap = mvStore.openMap("stmap");
        } catch (Error | RuntimeException e) {
            if (mvStore != null) {
                mvStore.closeImmediately();
            }
            if (tempFile != null) {
                tempFile.delete();
            }
            throw e;
        } catch (Exception e) {
            if (mvStore != null) {
                mvStore.closeImmediately();
            }
            if (tempFile != null) {
                tempFile.delete();
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public CTRst putCTRst(Integer idx, CTRst st) {
        return strings.put(idx, st);
    }

    @Override
    public CTRst getCTRst(Integer idx) {
        return strings.get(idx);
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
        return strings.keyIterator(null);
    }

    @Override
    public void close() {
        if(mvStore != null) mvStore.closeImmediately();
        if(tempFile != null) tempFile.delete();
    }
}
