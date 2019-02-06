package com.cafe.crm.services.impl;

import com.cafe.crm.services.interfaces.DownloadCallRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

@Service
public class DownloadCallRecordServiceImpl implements DownloadCallRecordService {

    private static Logger logger = LoggerFactory.getLogger(DownloadCallRecordServiceImpl.class);

    @Override
    public String downloadRecord(String urlStr, Long clientId) {

        String path = "CallRecords";
        File dir = new File(path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                logger.error("Could not create folder for call records files");
            }
        }
        String fileName = "callRecord" + clientId + ".mp3";
        File file = new File(dir, fileName);
        try {
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    logger.error("Audio record file not created!");
                }
            }

            URL url = new URL(urlStr);
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            FileOutputStream fos = new FileOutputStream(file);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();
        } catch (IOException e) {
            logger.error("Could not download the call record file!");
            e.printStackTrace();
        }
        return "/rest/call/record/" + fileName;
    }
}
