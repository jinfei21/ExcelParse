package com.yjfei.excel.core;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRst;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import com.yjfei.excel.common.Col;
import com.yjfei.excel.common.ColType;
import com.yjfei.excel.common.ISheetParseHandler;
import com.yjfei.excel.common.ParseContext;
import com.yjfei.excel.common.Row;
import com.yjfei.excel.util.ParseUtils;
import com.yjfei.excel.util.SAXUtil;

public class ExcelReader extends DefaultHandler implements Closeable {
	private volatile boolean closed = false;
	private int sheetIndex = -1;
	private Row currentRow;
	private Col currentCol;
	private XMLReader currentSheetReader;
	private ISheetParseHandler currentSheetHandler;
	private boolean hasParseColSpan;
	private StringBuffer strContent = new StringBuffer();
	private int parseRowCount;
	private ParseContext parseContext = new ParseContext();
	private OPCPackage opcPkg;
	private XSSFReader xssFReader;
	private SharedStringsTable sharedString;

	public ExcelReader(String fileName) {
		this(new File(fileName));
	}

	public ExcelReader(File excelFile) {
		if (excelFile == null || !excelFile.exists()) {
			throw new RuntimeException("File not exists!");
		}
		try {
			opcPkg = OPCPackage.open(excelFile.toString());
			xssFReader = new XSSFReader(opcPkg);
			sharedString = xssFReader.getSharedStringsTable();
		} catch (Exception e) {
			throw new RuntimeException("can not load the excel!", e);
		}
	}

	public ExcelReader(InputStream inputStream) {
		if (inputStream == null) {
			throw new RuntimeException("inputStream not exists!");
		}
		try {
			opcPkg = OPCPackage.open(inputStream);
			xssFReader = new XSSFReader(opcPkg);
			sharedString = xssFReader.getSharedStringsTable();
		} catch (Exception e) {
			throw new RuntimeException("can not load the excel!", e);
		}
	}

	

	public void parse(int sheetIdx, ISheetParseHandler handler) throws Exception {
		if (sheetIdx < 0) {
			throw new IllegalArgumentException("Sheet not exists!");
		}
		assertOpen();
		this.currentSheetHandler = handler;
		InputStream input = xssFReader.getSheet("rId" + sheetIdx);
		if (input == null)
			throw new IllegalArgumentException("Sheet not exists!"); // parser
																		// 浼氬鏉傚叧闂祦
		InputSource source = new InputSource(input);
		currentSheetReader = SAXUtil.xmlReader();
		currentSheetReader.setContentHandler(this);
		this.sheetIndex = sheetIdx;
		currentSheetReader.parse(source);
	}

	@Override
	public void startDocument() throws SAXException {
		parseContext = new ParseContext();
		currentRow = null;
		currentCol = null;
		hasParseColSpan = false;
		currentSheetHandler.start(parseContext);
	}

	@Override
	public void endDocument() throws SAXException {
		currentSheetHandler.end(parseContext);
	}

	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
		/** start row */
		if ("row".equals(name)) {
			Row row = new Row();
			String rowNoStr = attributes.getValue("r");
			row.setRowNo(Integer.parseInt(rowNoStr));
			parseContext.setMaxRowNo(row.getRowNo());
			if (!hasParseColSpan) {
				String spans = attributes.getValue("spans");
				if (!StringUtils.isEmpty(spans)) {
					String[] parts = spans.split(":");
					parseContext.setMinColNo(Integer.parseInt(parts[0]));
					parseContext.setMaxColNo(parts.length > 1 ? Integer.parseInt(parts[1]) : Integer.MAX_VALUE);
				}
				parseContext.setMaxRowNo(row.getRowNo());
				hasParseColSpan = true;
			}
			currentRow = row;
			currentSheetHandler.startRow(row, parseContext);
		} else if ("c".equals(name)) {
			Col col = new Col();
			String rAttr = attributes.getValue("r");
			String tAttr = attributes.getValue("t");
			col.setColNo(ParseUtils.colNo(rAttr));
			col.settAttr(tAttr);
			currentCol = col;
			cleanStrContent();
		} else if ("f".equals(name) || "v".equals(name)) {
			cleanStrContent();
		}
	}

	public void endElement(String uri, String localName, String name) throws SAXException { // 鏍规嵁SST鐨勭储寮曞�肩殑鍒板崟鍏冩牸鐨勭湡姝ｈ瀛樺偍鐨勫瓧绗︿覆
																							// //
																							// 杩欐椂characters()鏂规硶鍙兘浼氳璋冪敤澶氭
		if ("row".equals(name)) {
			currentSheetHandler.endRow(currentRow, parseContext);
			parseRowCount++;
			parseContext.incrRow();
		} else if ("c".equals(name)) {
			currentRow.putCol(currentCol);
		} else if ("f".equals(name)) {
			currentCol.setType(ColType.FORMULA);
			currentCol.setFormula(strContent.toString());
			cleanStrContent();
		} else if ("v".equals(name)) {
			String vStr = strContent.toString();
			String t = currentCol.gettAttr();
			String strVal = vStr; // sharding staring
			if ("s".equals(t)) {
				currentCol.setType(ColType.STRING);
				strVal = getShardingString(Integer.parseInt(vStr));
			} else if ("e".equals(t)) {
				currentCol.setType(ColType.ERR);
			} else if ("str".equals(t)) {
				currentCol.setFormula(null);
			}
			currentCol.setStrVal(strVal);
			cleanStrContent();
		}
	}

	/** * 鑾峰彇鍏变韩瀛楃涓� * * @param index * @return */
	private String getShardingString(int index) {
		CTRst st = sharedString.getEntryAt(index);
		return st == null ? null : st.getT();
	}

	private void cleanStrContent() {
		strContent.delete(0, strContent.length());
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		strContent.append(ch, start, length);
	}

	@Override
	public void close() throws IOException {
		if (!closed) {
			currentSheetReader = null;
			xssFReader = null;
			opcPkg.close();
		}
	}

	private void assertOpen() {
		if (closed)
			throw new RuntimeException("The excel is closed!");
	}

	public boolean isClosed() {
		return closed;
	}

	public int getSheetIndex() {
		return sheetIndex;
	}

	public ParseContext getParseContext() {
		return parseContext;
	}

	public int getParseRowCount() {
		return parseRowCount;
	}

	public SharedStringsTable getSharedString() {
		return sharedString;
	}
}