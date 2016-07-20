package com.yjfei.excel.common;

public interface ISheetParseHandler {
	void start(ParseContext context);

	void startRow(Row row, ParseContext context);

	void endRow(Row row, ParseContext context);

	void end(ParseContext context);
}