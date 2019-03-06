package ie.com.faustoalves.seatflightmap.service;

import com.sun.xml.internal.bind.v2.TODO;
import ie.com.faustoalves.seatflightmap.model.Passenger;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class SeatFlightMapService {

    public JSONArray processSeats (MultipartFile file) {
        String[] dataList;
        JSONArray result = new JSONArray();
        List<Integer> dimensions;
        List <Passenger> passengerList;

        try {
            dataList = new String(file.getBytes()).split("\n");
             dimensions = getFlightDimension(dataList[0]);
             passengerList = getPassengersList(dataList);
            if(passengerList.size() < (dimensions.get(0) * dimensions.get(1))) {
                result = convertStringToJSONArray("[{ \"error\": \"Passengers list is bigger than flight dimension\"}]");
            } else {
                result = createSeatList(passengerList, dimensions);
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return result;
    }


    public JSONArray createSeatList(List <Passenger> passengerList, List<Integer> flightDimension) {
        String satisfacitonLevel;
        int group = passengerList.get(0).getGroup();
        int row = 1;
        String passengersGroup = "";
        String result = "[ ";
        int lines = 1;

        //TODO: Change the business rules
        for(Passenger passenger: passengerList) {
            if(passenger.getGroup() == group) {
                passengersGroup += passenger.getCodPassenger() + " ";
            } else {
                result += " { \"row\": \"" +row + "\" " + " \"passengers\": \"" + passengersGroup + "\" }, ";
                passengersGroup = " ";
                passengersGroup += passenger.getCodPassenger() + " ";
                row++;
            }
            if (lines == passengerList.size()) {
                result += " { \"row\": \"" + row + "\" " + " \"passengers\": \"" + passengersGroup + "\" }, ";
            }
            group = passenger.getGroup();
            lines++;
        }

        satisfacitonLevel = getSatisfactionPassengers(passengerList.size(), 3);
        result += "{ \"satisfactionLevel\": \"" + satisfacitonLevel + "\" }";
        result += " ]";

        return convertStringToJSONArray(result);
    }

    private List<Integer> getFlightDimension(String dimensions) {
        List<Integer> dimensionsFlight = new ArrayList<>();
        String[] dimensionsList = dimensions.split(" ");
        Integer seatsByRow = Integer.valueOf(dimensionsList[0]);
        Integer rowsByFlight = Integer.valueOf(dimensionsList[1]);
        Integer totalSeats = seatsByRow * rowsByFlight;
        dimensionsFlight.add(seatsByRow);
        dimensionsFlight.add(rowsByFlight);
        dimensionsFlight.add(totalSeats);
        return dimensionsFlight;
    }

    private String getSatisfactionPassengers(int sumPassengers, int outOfSeat) {
        Double result = (double) outOfSeat / sumPassengers;
        result = result * 100;
        result = 100 - result;
        return result + "%";
    }

    private List<Passenger> getPassengersList (String[] dataList) {
        String[] passengerByGroup;
        List<Passenger> passengerList = new ArrayList<>();
        Passenger passenger;
        for (int i = 1; i < dataList.length; i++) {
            passengerByGroup = dataList[i].split(" ");
            for (String passengerCod : passengerByGroup) {
                passenger = new Passenger(i, passengerCod);
                passengerList.add(passenger);
            }
        }
        return passengerList;
    }

    public JSONArray convertStringToJSONArray(String objectJson) {
        JSONParser parser = new JSONParser();
        JSONArray json = new JSONArray();
        try {
            json = (JSONArray) parser.parse(objectJson);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return json;
    }
}
