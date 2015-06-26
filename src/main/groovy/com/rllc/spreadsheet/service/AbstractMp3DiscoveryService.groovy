package com.rllc.spreadsheet.service

import com.mpatric.mp3agic.ID3v1
import com.mpatric.mp3agic.InvalidDataException
import com.mpatric.mp3agic.Mp3File
import com.mpatric.mp3agic.UnsupportedTagException
import com.rllc.spreadsheet.domain.Mp3SermonFile
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Created by Steven McAdams on 5/5/15.
 */
@Component
abstract class AbstractMp3DiscoveryService implements Mp3DiscoveryService {

    private static final Logger logger = LoggerFactory.getLogger(AbstractMp3DiscoveryService.class);

    @Autowired
    TextParsingService textParsingService

    abstract List<File> findMp3Files(String congregationKey)

    @Override
    List<Mp3SermonFile> processMp3Files(String congregationKey) {
        def sermonList = []
        List mp3Files = findMp3Files(congregationKey)
        mp3Files.each { mp3FileHandle ->
            logger.debug "> ${mp3FileHandle.name}"
            try {
                def mp3file = new Mp3File(mp3FileHandle.absolutePath);
                def id3v1Tag = mp3file.hasId3v1Tag() ? mp3file.id3v1Tag : mp3file.id3v2Tag
                sermonList << extractId3v1TagData(mp3FileHandle, id3v1Tag)
            } catch (IOException e) {
                e.printStackTrace();
            } catch (UnsupportedTagException e) {
                e.printStackTrace();
            } catch (InvalidDataException e) {
                e.printStackTrace();
            }
        }
        sermonList
    }

    @Override
    Mp3SermonFile extractId3v1TagData(File mp3FileHandle, ID3v1 id3v1Tag) {
        new Mp3SermonFile(
                file: textParsingService.parseFilename(mp3FileHandle.absolutePath),
                date: textParsingService.parseDate(id3v1Tag.title, mp3FileHandle.name),
                bibletext: textParsingService.parseBibleText(id3v1Tag.album),
                minister: textParsingService.parseMinister(id3v1Tag.artist),
                notes: textParsingService.parseNotes(id3v1Tag.comment)

        )
    }
}
