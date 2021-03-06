package com.xeppaka.lentareader.utils;

import java.util.Calendar;

public class LentaConstants {
	public static final int SDK_VER = android.os.Build.VERSION.SDK_INT;
	public static final String LoggerMainAppTag = "LentaAnd";
	public static final String LoggerServiceTag = "LentaAndService";
	public static final String LoggerProviderTag = "LentaAndProvider";
	public static final String LoggerAnyTag = "LentaAndAny";
	public static final String LENTA_URL_ROOT = "http://lenta.ru";
    public static final String OWNSERVER_URL_ROOT = "http://news.lentaru.eu";
    public static final String OWNSERVER_URL_ROOT_JOLLA = "http://188.226.168.63";
	public static final String UserAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36";

    public static final String RSS_PATH_ROOT = "/rss";
    public static final String XML_PATH_ROOT = "";

    public static final String COMMENTS_WIDGET_ID = "5270";

    // Default values for bitmap caches
	public static int BITMAP_CACHE_MAX_SIZE_IN_BYTES = 3 * 1024 * 1024; // 3 MB
    public static int THUMBNAILS_BITMAP_CACHE_MAX_SIZE_IN_BYTES = 1 * 1024 * 1024; // 1 MB

    public static int BITMAP_CACHE_TRIM_MAX_SIZE_IN_BYTES = 2 * 1024 * 1024; // 2 MB
    public static int THUMBNAILS_BITMAP_CACHE_TRIM_MAX_SIZE_IN_BYTES = 512 * 1024; // 512 KB

	public static final boolean DEVELOPER_MODE = false;

    public static final String[] MONTHS_RUS = new String[12];
    public static final String[] MONTHS_SHORT_RUS = new String[12];
    public static final String[] DAYS_RUS = new String[8];
    public static final String[] DAYS_SHORT_RUS = new String[8];

    public static final int WITHOUT_PICTURE_LIMIT = 10;

    static {
        MONTHS_RUS[Calendar.JANUARY] = "января";
        MONTHS_RUS[Calendar.FEBRUARY] = "февраля";
        MONTHS_RUS[Calendar.MARCH] = "марта";
        MONTHS_RUS[Calendar.APRIL] = "апреля";
        MONTHS_RUS[Calendar.MAY] = "мая";
        MONTHS_RUS[Calendar.JUNE] = "июня";
        MONTHS_RUS[Calendar.JULY] = "июля";
        MONTHS_RUS[Calendar.AUGUST] = "августа";
        MONTHS_RUS[Calendar.SEPTEMBER] = "сентября";
        MONTHS_RUS[Calendar.OCTOBER] = "октября";
        MONTHS_RUS[Calendar.NOVEMBER] = "ноября";
        MONTHS_RUS[Calendar.DECEMBER] = "декабря";

        MONTHS_SHORT_RUS[Calendar.JANUARY] = "янв.";
        MONTHS_SHORT_RUS[Calendar.FEBRUARY] = "фев.";
        MONTHS_SHORT_RUS[Calendar.MARCH] = "мрт.";
        MONTHS_SHORT_RUS[Calendar.APRIL] = "апр.";
        MONTHS_SHORT_RUS[Calendar.MAY] = "мая";
        MONTHS_SHORT_RUS[Calendar.JUNE] = "июн";
        MONTHS_SHORT_RUS[Calendar.JULY] = "июл";
        MONTHS_SHORT_RUS[Calendar.AUGUST] = "авг.";
        MONTHS_SHORT_RUS[Calendar.SEPTEMBER] = "сен.";
        MONTHS_SHORT_RUS[Calendar.OCTOBER] = "окт.";
        MONTHS_SHORT_RUS[Calendar.NOVEMBER] = "нбр.";
        MONTHS_SHORT_RUS[Calendar.DECEMBER] = "дек.";

        DAYS_RUS[Calendar.MONDAY] = "Понедельник";
        DAYS_RUS[Calendar.TUESDAY] = "Вторник";
        DAYS_RUS[Calendar.WEDNESDAY] = "Среда";
        DAYS_RUS[Calendar.THURSDAY] = "Четверг";
        DAYS_RUS[Calendar.FRIDAY] = "Пятница";
        DAYS_RUS[Calendar.SATURDAY] = "Суббота";
        DAYS_RUS[Calendar.SUNDAY] = "Воскресенье";

        DAYS_SHORT_RUS[Calendar.MONDAY] = "Пн";
        DAYS_SHORT_RUS[Calendar.TUESDAY] = "Вт";
        DAYS_SHORT_RUS[Calendar.WEDNESDAY] = "Ср";
        DAYS_SHORT_RUS[Calendar.THURSDAY] = "Чт";
        DAYS_SHORT_RUS[Calendar.FRIDAY] = "Пт";
        DAYS_SHORT_RUS[Calendar.SATURDAY] = "Сб";
        DAYS_SHORT_RUS[Calendar.SUNDAY] = "Вс";
    }

    public static void adjustCacheSizes(int memClass) {
        // 12 MB should be enough(I hope) for everyone :D
        final int cacheMem = memClass - 12;

        BITMAP_CACHE_MAX_SIZE_IN_BYTES = cacheMem / 2 * 1024 * 1024;
        THUMBNAILS_BITMAP_CACHE_MAX_SIZE_IN_BYTES = cacheMem / 2 * 1024 * 1024;

        BITMAP_CACHE_TRIM_MAX_SIZE_IN_BYTES = BITMAP_CACHE_MAX_SIZE_IN_BYTES / 2;
        THUMBNAILS_BITMAP_CACHE_TRIM_MAX_SIZE_IN_BYTES = THUMBNAILS_BITMAP_CACHE_MAX_SIZE_IN_BYTES / 4;
    }
}
