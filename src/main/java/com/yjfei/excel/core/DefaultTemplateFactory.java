package com.yjfei.excel.core;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.yjfei.excel.common.AbstractExcelTemplate;
import com.yjfei.excel.common.ColumnMeta;
import com.yjfei.excel.common.ConvertMeta;
import com.yjfei.excel.common.IConvert;
import com.yjfei.excel.common.ITemplateFactory;
import com.yjfei.excel.util.ReflectUtil;
import com.yjfei.excel.util.ReflectUtil.AnnotationCallBack;

public class DefaultTemplateFactory implements ITemplateFactory {
	public static final String[] ALPHA = { "", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
			"O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
	private static ConcurrentHashMap<Class<?>, Map<String, ColumnInfo>> columnMap = new ConcurrentHashMap<Class<?>, Map<String, ColumnInfo>>();
	private static ConcurrentHashMap<Class<?>, AbstractExcelTemplate> templateMap = new ConcurrentHashMap<Class<?>, AbstractExcelTemplate>();
	private static ConcurrentHashMap<Class<?>, Map<String, Field>> fieldMap = new ConcurrentHashMap<Class<?>, Map<String, Field>>();

	public void init() {
	}

	@Override
	public Map<String, ColumnInfo> getColumns(Class<?> clz) {
		Map<String, ColumnInfo> cmap = columnMap.get(clz);
		if (cmap == null) {
			synchronized (clz) {
				cmap = columnMap.get(clz);
				if (cmap == null) {
					cmap = _Columns(clz);
					columnMap.put(clz, cmap);
				}
			}
		}
		return cmap;
	}

	private Map<String, ColumnInfo> _Columns(Class<?> clazz) {
		final Map<String, ColumnInfo> map = new HashMap<String, ColumnInfo>();
		ReflectUtil.parseFieldAnnotation(clazz, new AnnotationCallBack<ColumnMeta>() {
			@Override
			public void addAnnotation(Field field, ColumnMeta annotation) {
				if (annotation != null) {
					ColumnInfo meta = new ColumnInfo();
					meta.setIndex(annotation.index());
					meta.setType(field.getType());
					if (!field.isAccessible()) {
						field.setAccessible(true);
					}
					meta.setField(field);
					meta.setTitle(annotation.title() == null ? "" : annotation.title());
					meta.setDisplayName(annotation.title() == null ? getColumnAlpha(annotation.index()) + "列"
							: annotation.title() + "(" + getColumnAlpha(annotation.index()) + "列)");
					map.put(field.getName(), meta);
				}
			}

			@Override
			public Class<ColumnMeta> annotationClazz() {
				return ColumnMeta.class;
			}
		});
		ReflectUtil.parseFieldAnnotation(clazz, new AnnotationCallBack<ConvertMeta>() {
			@Override
			public void addAnnotation(Field field, ConvertMeta annotation) {
				if (annotation != null) {
					ColumnInfo columnInfo = map.get(field.getName());
					if (columnInfo == null) {
						columnInfo = new ColumnInfo();
						map.put(field.getName(), columnInfo);
					}
					ConvertInfo meta = new ConvertInfo();
					meta.setDefaultValue(annotation.defaultVaule());
					meta.setTargetType(field.getType());
					meta.setSourceType(String.class);
					meta.setFormat(annotation.param());
					meta.setConvertor(meta.getSourceType() == meta.getTargetType() ? new SelfConvert()
							: _convert(annotation.convert(), meta));
					columnInfo.setConvert(meta);
				}
			}

			@Override
			public Class<ConvertMeta> annotationClazz() {
				return ConvertMeta.class;
			}
		});
		return map;
	}

	private static IConvert _convert(Class<? extends IConvert> clazz, ConvertInfo meta) {
		try {
			IConvert convert = clazz.newInstance();
			convert.setConvertInfo(meta);
			return convert;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String getColumnAlpha(int index) {
		int firstAlphaPos = index / 26;
		int secondAlphaPos = index % 26;
		return ALPHA[firstAlphaPos] + ALPHA[secondAlphaPos + 1];
	}

	@Override
	public AbstractExcelTemplate getTemplate(Class<?> clz) {
		AbstractExcelTemplate template = templateMap.get(clz);
		if (template == null) {
			synchronized (clz) {
				if (template == null) {
					try {
						template = (AbstractExcelTemplate) clz.newInstance();
						templateMap.put(clz, template);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		return template;
	}

	@Override
	public Map<String, Field> getClassField(Class<?> clz) {
		Map<String, Field> cmap = fieldMap.get(clz);
		if (cmap == null) {
			synchronized (clz) {
				cmap = fieldMap.get(clz);
				if (cmap == null) {
					cmap = _Fields(clz);
					fieldMap.put(clz, cmap);
				}
			}
		}
		return cmap;
	}

	private Map<String, Field> _Fields(Class<?> clazz) {
		Map<String, Field> map = new HashMap<String, Field>();
		Field[] fields = ReflectUtil.getAllField(clazz);
		for (Field field : fields) {
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			map.put(field.getName(), field);
		}
		return map;
	}
}