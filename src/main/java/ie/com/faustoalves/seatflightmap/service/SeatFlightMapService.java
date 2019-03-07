package ie.com.faustoalves.seatflightmap.service;

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
                result = createSeatList(dataList, passengerList, dimensions);
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return result;
    }


    public JSONArray createSeatList(String[] dataList, List <Passenger> passengerList, List<Integer> flightDimension) {
        String satisfacitonLevel;
        int row = 1;
        String passengersGroup;
        Integer passengerListSize = passengerList.size();
        String[][] seatMap = mountStringJson(dataList, passengerList, flightDimension);

        String result = "[ ";
        for(String[] groups: seatMap){
            passengersGroup = "";
            for(String pass: groups) {
                passengersGroup += pass + " ";
            }
            result += " { \"row\": \"" +row + "\" " + " \"passengers\": \"" + passengersGroup + "\" }, ";
            row++;
        }
        satisfacitonLevel = getSatisfactionPassengers(passengerListSize, 3);
        result += "{ \"satisfactionLevel\": \"" + satisfacitonLevel + "\" }";
        result += " ]";

        return convertStringToJSONArray(result);
    }

    private String[][] mountStringJson (String[] dataList, List <Passenger> passengerList, List<Integer> flightDimension ) {
        int group = passengerList.get(0).getGroup();
        String[][] seatsMap = new String[flightDimension.get(1)][flightDimension.get(0)];
        Integer[] sizeGroups = new Integer[dataList.length];
        String[] groups;
        for (int i = 1; i < dataList.length ; i++ ) {
            groups = dataList[i].split(" ");
            sizeGroups[i] = groups.length;
        }

        int row = 0;
        int seatNumber = 0;
        while (!passengerList.isEmpty()) {
            List<Passenger> removePassengerList = new ArrayList<>();
            for (Passenger passenger : passengerList) {
                if (passenger.getGroup() == group) {
                    if (seatNumber < flightDimension.get(0)) {
                        seatsMap[row][seatNumber] = passenger.getCodPassenger();
                        seatNumber++;
                        removePassengerList.add(passenger);
                    }
                }
            }
            for (Passenger removePassenger : removePassengerList) {
                passengerList.remove(removePassenger);
                removePassengerList = new ArrayList<>();
            }

            if (seatNumber < flightDimension.get(0)) {
                for (int f = 1; f < sizeGroups.length; f++) {
                    if (sizeGroups[f] <= flightDimension.get(0) - seatNumber) {
                        String[] groupsData = dataList[f].split(" ");
                        for (int j = 0; j < groupsData.length; j++) {
                            for (Passenger p : passengerList) {
                                if (p.getCodPassenger().equals(groupsData[j])) {
                                    seatsMap[row][seatNumber] = p.getCodPassenger();
                                    seatNumber++;
                                    removePassengerList.add(p);
                                }
                            }
                        }
                    }
                }
            } else {
                row++;
                seatNumber = 0;
            }

            for(Passenger removePass: removePassengerList) {
                passengerList.remove(removePass);
            }
        }

        for(int i = 0; i < seatsMap.length;i++) {
            for(int j = 0; j < seatsMap[i].length; j++) {
                if(seatsMap[i][j].contains("W")) {
                    if(j != 0 && j != (flightDimension.get(0) - 1)) {
                        if (!seatsMap[i][0].contains("W")) {
                            String before = seatsMap[i][j].replace("W", "");
                            seatsMap[i][0] = seatsMap[i][j].replace("W", "");
                            seatsMap[i][j] = before;
                        } else if (!seatsMap[i][flightDimension.get(0) - 1].contains("W")) {
                            String before = seatsMap[i][flightDimension.get(0) - 1].replace("W", "");
                            seatsMap[i][flightDimension.get(0) -1] = seatsMap[i][j].replace("W", "");
                            seatsMap[i][j] = before;
                        }
                    }
                }
            }
        }
        return seatsMap;
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
