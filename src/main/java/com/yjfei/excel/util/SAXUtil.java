package com.yjfei.excel.util;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class SAXUtil {
    private static Logger log = LoggerFactory.getLogger(SAXUtil.class);

    public static SAXParserFactory getSAXParserFactory(boolean namespaceAware) {
        SAXParserFactory f = SAXParserFactory.newInstance();
        f.setNamespaceAware(namespaceAware);
        return f;
    }

    public static SAXParser getSAXParser(boolean namespaceAware) {
        try {
            return getSAXParserFactory(namespaceAware).newSAXParser();
        } catch (ParserConfigurationException e) {
        } catch (SAXException e) {
        }
        return null;
    }

    public static XMLReader getXMLReader(boolean namespaceAware) {
        try {
            return getSAXParserFactory(namespaceAware).newSAXParser().getXMLReader();
        } catch (ParserConfigurationException e) {
        } catch (SAXException e) {
        }
        return null;
    }

    public static XMLReader getXMLReader(Schema s, boolean namespaceAware) {
        try {
            return getSAXParserFactory(s, namespaceAware).newSAXParser().getXMLReader();
        } catch (ParserConfigurationException e) {
        } catch (SAXException e) {
        }
        return null;
    }

    public static SAXParser getSAXParser(Schema s, boolean namespaceAware) {
        try {
            return getSAXParserFactory(s, namespaceAware).newSAXParser();
        } catch (ParserConfigurationException e) {
        } catch (SAXException e) {
        }
        return null;
    }

    public static SAXParserFactory getSAXParserFactory(Schema s, boolean namespaceAware) {
        SAXParserFactory f = getSAXParserFactory(namespaceAware);
        f.setValidating(true);
        return f;
    }

    public static XMLReader xmlReader() {
        try {
            return XMLReaderFactory.createXMLReader();
        } catch (SAXException e) {
            log.error("Cant get the sax implemention!" + e.getMessage());
        }
        return null;
    }
}