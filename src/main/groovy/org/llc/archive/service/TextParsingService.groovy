package org.llc.archive.service

/**
 * Created by Steven McAdams on 4/25/15.
 */
interface TextParsingService {

    String parseFilename(String basePath, String absoluteFilePath)

    String parseMinister(String artist)

    String parseBibleText(String album)

    String parseTime(String title)

    String parseDate(String title, String name)

    String parseDateFromFilename(String filename)

    String formatDate(int month, int day, int year)
    String parseNotes(String comment)

}