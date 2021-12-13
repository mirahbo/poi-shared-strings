package com.github.pjfanning.poi.xssf.streaming.cache;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRst;

import java.util.Iterator;

public class SSTCacheLRU implements SSTCache {
    @Override
    public CTRst putCTRst(Integer idx, CTRst st) {
        return null;
    }

    @Override
    public CTRst getCTRst(Integer idx) {
        return null;
    }

    @Override
    public Integer putStringIndex(String s, Integer idx) {
        return 0;
    }

    @Override
    public Integer getStringIndex(String s) {
        return 0;
    }

    @Override
    public boolean containsString(String s) {
        return false;
    }

    @Override
    public Iterator<Integer> keyIterator() {
        return null;
    }

    @Override
    public void close() {

    }
}
