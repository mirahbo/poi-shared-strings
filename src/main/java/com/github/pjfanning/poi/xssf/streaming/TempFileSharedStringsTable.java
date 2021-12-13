package com.github.pjfanning.poi.xssf.streaming;

import com.github.pjfanning.poi.xssf.streaming.cache.SSTCacheH2;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRst;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSst;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.SstDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.apache.poi.xssf.usermodel.XSSFRelation.NS_SPREADSHEETML;

/**
 * Table of strings shared across all sheets in a workbook.
 * <p>
 * A workbook may contain thousands of cells containing string (non-numeric) data. Furthermore this data is very
 * likely to be repeated across many rows or columns. The goal of implementing a single string table that is shared
 * across the workbook is to improve performance in opening and saving the file by only reading and writing the
 * repetitive information once.
 * </p>
 * <p>
 * Consider for example a workbook summarizing information for cities within various countries. There may be a
 * column for the name of the country, a column for the name of each city in that country, and a column
 * containing the data for each city. In this case the country name is repetitive, being duplicated in many cells.
 * In many cases the repetition is extensive, and a tremendous savings is realized by making use of a shared string
 * table when saving the workbook. When displaying text in the spreadsheet, the cell table will just contain an
 * index into the string table as the value of a cell, instead of the full string.
 * </p>
 * <p>
 * The shared string table contains all the necessary information for displaying the string: the text, formatting
 * properties, and phonetic properties (for East Asian languages).
 * </p>
 * <p>
 * This implementation uses a H2 store with a temp file.
 * </p>
 */
public class TempFileSharedStringsTable extends SharedStringsTable {

    private CachedSharedStringsTable sst;

    public TempFileSharedStringsTable() {
        this(false, false);
    }

    public TempFileSharedStringsTable(boolean encryptTempFiles) {
        this(encryptTempFiles, false);
    }

    public TempFileSharedStringsTable(boolean encryptTempFiles, boolean fullFormat) {
        super();
        this.sst = new CachedSharedStringsTable.Builder()
                .sstCache(new SSTCacheH2.Builder()
                        .encryptTempFiles(encryptTempFiles)
                        .build())
                .fullFormat(fullFormat)
                .build();
    }

    public TempFileSharedStringsTable(OPCPackage pkg, boolean encryptTempFiles) throws IOException {
        this(pkg, encryptTempFiles, false);
    }

    public TempFileSharedStringsTable(OPCPackage pkg, boolean encryptTempFiles,
                                      boolean fullFormat) throws IOException {
        this(encryptTempFiles, fullFormat);
        ArrayList<PackagePart> parts = pkg.getPartsByContentType(XSSFRelation.SHARED_STRINGS.getContentType());
        if (parts.size() > 0) {
            PackagePart sstPart = parts.get(0);
            this.readFrom(sstPart.getInputStream());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void readFrom(InputStream is) throws IOException {
        sst.readFrom(is);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RichTextString getItemAt(int idx) {
        return sst.getItemAt(idx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCount() {
        return sst.getCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getUniqueCount() {
        return sst.getUniqueCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int addSharedStringItem(RichTextString string) {
        return sst.addSharedStringItem(string);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RichTextString> getSharedStringItems() {
        return sst.getSharedStringItems();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeTo(OutputStream out) throws IOException {
        sst.writeTo(out);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        sst.close();
    }
}
