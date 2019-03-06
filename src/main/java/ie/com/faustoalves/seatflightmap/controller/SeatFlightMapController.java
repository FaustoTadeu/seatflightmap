package ie.com.faustoalves.seatflightmap.controller;

import ie.com.faustoalves.seatflightmap.service.SeatFlightMapService;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



@RestController
@RequestMapping(value = "/")
public class SeatFlightMapController {


    @Autowired
    private SeatFlightMapService seatFlightMapService;

    @RequestMapping(value = "/seatsprocess", method= RequestMethod.POST)
    public JSONArray seatsprocess(@RequestParam("file") MultipartFile file) {
        String fileType = file.getContentType();
        JSONArray result;
        if(!fileType.equals("text/plain")) {
            result = seatFlightMapService.convertStringToJSONArray("[ { \"error\": \"File format invalid. Content type is not text\"} ]");
        } else {
            result = seatFlightMapService.processSeats(file);
        }

        return result;

    }
}
