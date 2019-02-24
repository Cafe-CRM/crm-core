package com.cafe.crm.controllers;

import com.cafe.crm.models.call.ClientCall;
import com.cafe.crm.services.interfaces.DownloadCallRecordService;
import com.cafe.crm.services.interfaces.call.ClientCallService;
import com.cafe.crm.services.interfaces.voximplant.IPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
@RequestMapping("/rest/call")
public class ClientCallRestController {

    private final IPService ipService;

    private final ClientCallService clientCallService;

    private static Logger logger = LoggerFactory.getLogger(ClientCallRestController.class);

    private final DownloadCallRecordService downloadCallRecordService;

    public ClientCallRestController(IPService ipService, ClientCallService clientCallService, DownloadCallRecordService downloadCallRecordService) {
        this.ipService = ipService;
        this.clientCallService = clientCallService;
        this.downloadCallRecordService = downloadCallRecordService;
    }

    @GetMapping(value = "/setCallRecord")
    public ResponseEntity setCallRecord(@RequestParam String url, @RequestParam Long clientCallId,
                                        @RequestParam String code) {
        if (!code.equals(ipService.getVoximplantCodeToSetRecord())) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        ClientCall callRecord = clientCallService.getById(clientCallId);
        if (Optional.ofNullable(callRecord).isPresent()) {
            String downloadLink = downloadCallRecordService.downloadRecord(url, clientCallId);
            callRecord.setLink(downloadLink);
            clientCallService.update(callRecord);
            logger.info("CallRecord to client id {} has download", clientCallId);
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = "/record/{file}")
    public byte[] getCallRecord(@PathVariable String file) throws IOException {
        Path fileLocation = Paths.get("CallRecords\\" + file);
        return Files.readAllBytes(fileLocation);
    }

}
