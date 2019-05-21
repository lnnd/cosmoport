package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("/rest")
public class ShipService {

    @Autowired
    private ShipRepository shipRepository;

    @Autowired
    private HttpServletRequest httpServletRequest;

    public ShipService() {}

    @RequestMapping(value = "/ships", method = RequestMethod.GET)
    public @ResponseBody List<Ship> getShipsList(
            @RequestParam(name = "name",        required = false) String name,
            @RequestParam(name = "planet",      required = false) String planet,
            @RequestParam(name = "shipType",    required = false) ShipType shipType,
            @RequestParam(name = "after",       required = false) Long after,
            @RequestParam(name = "before",      required = false) Long before,
            @RequestParam(name = "isUsed",      required = false) Boolean isUsed,
            @RequestParam(name = "minSpeed",    required = false) Double minSpeed,
            @RequestParam(name = "maxSpeed",    required = false) Double maxSpeed,
            @RequestParam(name = "minCrewSize", required = false) Integer minCrewSize,
            @RequestParam(name = "maxCrewSize", required = false) Integer maxCrewSize,
            @RequestParam(name = "minRating",   required = false) Double minRating,
            @RequestParam(name = "maxRating",   required = false) Double maxRating,
            @RequestParam(name = "order",       required = false, defaultValue = "ID") ShipOrder order,
            @RequestParam(name = "pageNumber",  required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(name = "pageSize",    required = false, defaultValue = "3") Integer pageSize,
            Model model
    ) {
        List<Ship> ships = getListWithFilters(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);

        Collections.sort(ships, new Comparator<Ship>() {
            @Override
            public int compare(Ship o1, Ship o2) {

                if (order == ShipOrder.ID) {
                    if (o1.getId() > o2.getId()) {
                        return 1;
                    } else {
                        return -1;
                    }
                } else if (order == ShipOrder.SPEED) {
                    if (o1.getSpeed() > o2.getSpeed()) {
                        return 1;
                    } else if (o1.getSpeed().equals(o2.getSpeed())) {
                        return 0;
                    } else {
                        return -1;
                    }
                } else if (order == ShipOrder.DATE) {
                    if (o1.getProdDate().getTime() > o2.getProdDate().getTime()) {
                        return 1;
                    } else if (o1.getProdDate().getTime() == o2.getProdDate().getTime()) {
                        return 0;
                    } else {
                        return -1;
                    }
                } else if (order == ShipOrder.RATING) {
                    if (o1.getRating() > o2.getRating()) {
                        return 1;
                    } else if (o1.getRating().equals(o2.getRating())) {
                        return 0;
                    }else {
                        return -1;
                    }
                }
                return 0;
            }
        });

        int start = pageNumber * pageSize;
        int end = Math.min(ships.size(), start + pageSize - 1);

        List<Ship> result = new ArrayList<Ship>();

        for (int i = 0; i < ships.size(); i++) {
            if (i >= start && i <= end)
                result.add(ships.get(i));
        }

        return result;
    }

    private List<Ship> getListWithFilters(
            String name,
            String planet,
            ShipType shipType,
            Long after,
            Long before,
            Boolean isUsed,
            Double minSpeed,
            Double maxSpeed,
            Integer minCrewSize,
            Integer maxCrewSize,
            Double minRating,
            Double maxRating) {

        List<Ship> ships = new ArrayList<Ship>();
        Iterator<Ship> iterator = shipRepository.findAll().iterator();

        while (iterator.hasNext()) {

            Ship ship = iterator.next();

            if (name != null && !name.isEmpty()) {
                if (!ship.getName().toUpperCase().contains(name.toUpperCase()))
                    continue;
            }

            if (planet != null && !planet.isEmpty()) {
                if (!ship.getPlanet().toUpperCase().contains(planet.toUpperCase()))
                    continue;
            }

            if (shipType != null) {
                if (shipType != ship.getShipType())
                    continue;
            }

            if (after != null) {
                if (after > ship.getProdDate().getTime())
                    continue;
            }

            if (before != null) {
                if (before < ship.getProdDate().getTime())
                    continue;
            }

            if (isUsed != null) {
                if (isUsed != ship.getUsed())
                    continue;
            }

            if (minSpeed != null) {
                if (minSpeed > ship.getSpeed())
                    continue;
            }

            if (maxSpeed != null) {
                if (maxSpeed < ship.getSpeed())
                    continue;
            }

            if (minCrewSize != null) {
                if (minCrewSize > ship.getCrewSize())
                    continue;
            }

            if (maxCrewSize != null) {
                if (maxCrewSize < ship.getCrewSize())
                    continue;
            }

            if (minRating != null) {
                if (minRating > ship.getRating())
                    continue;
            }

            if (maxRating != null) {
                if (maxRating < ship.getRating())
                    continue;
            }

            ships.add(ship);
        }
        return ships;
    }

    @RequestMapping(value = "/ships/count", method = RequestMethod.GET)
    public @ResponseBody int getCount(
            @RequestParam(name = "name",        required = false) String name,
            @RequestParam(name = "planet",      required = false) String planet,
            @RequestParam(name = "shipType",    required = false) ShipType shipType,
            @RequestParam(name = "after",       required = false) Long after,
            @RequestParam(name = "before",      required = false) Long before,
            @RequestParam(name = "isUsed",      required = false) Boolean isUsed,
            @RequestParam(name = "minSpeed",    required = false) Double minSpeed,
            @RequestParam(name = "maxSpeed",    required = false) Double maxSpeed,
            @RequestParam(name = "minCrewSize", required = false) Integer minCrewSize,
            @RequestParam(name = "maxCrewSize", required = false) Integer maxCrewSize,
            @RequestParam(name = "minRating",   required = false) Double minRating,
            @RequestParam(name = "maxRating",   required = false) Double maxRating
    ) {

        List<Ship> ships = getListWithFilters(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);
        return ships.size();
    }

    @RequestMapping(value = "/ships/{id}", method = RequestMethod.POST)
    public ResponseEntity updateShip(@PathVariable("id") Long id, @RequestBody Map<String, String> args) {

        if (!idValid(id)) {
            return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
        }

        Optional<Ship> optional = shipRepository.findById(id);
        if (optional.isEmpty()) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }

        String name         = args.containsKey("name")      ? args.get("name") : null;
        String planet       = args.containsKey("planet")    ? args.get("planet") : null;
        Long prodDate       = args.containsKey("prodDate")  ? Long.parseLong(args.get("prodDate")) : null;
        ShipType shipType   = args.containsKey("shipType")  ? ShipType.valueOf(args.get("shipType")) : null;
        Integer crewSize    = args.containsKey("crewSize")  ? Integer.parseInt(args.get("crewSize")) : null;
        Boolean isUsed      = args.containsKey("isUsed")    ? Boolean.parseBoolean(args.get("isUsed")) : null;
        Double speed        = args.containsKey("speed")     ? Double.parseDouble(args.get("speed")) : null;

        Ship ship = optional.get();

        if (name==null & planet==null & prodDate==null & shipType==null & crewSize==null & isUsed==null & speed==null)
            return new ResponseEntity<Ship>(ship, HttpStatus.OK);

        if (name != null)
            ship.setName(name);
        if (planet != null)
            ship.setPlanet(planet);
        if (prodDate != null)
            ship.setProdDate(new Date(prodDate));
        if (shipType != null)
            ship.setShipType(shipType);
        if (crewSize != null)
            ship.setCrewSize(crewSize);
        if (isUsed != null)
            ship.setUsed(isUsed);
        if (speed != null)
            ship.setSpeed(speed);

        if (!checkShip(ship))
            return new ResponseEntity(null, HttpStatus.BAD_REQUEST);

        ship.setRating(ship.getNewRating());
        shipRepository.save(ship);
        return new ResponseEntity<Ship>(ship, HttpStatus.OK);
    }

    private boolean checkShip(Ship ship) {

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(ship.getProdDate().getTime());

        if (ship.getName().length() > 50 || ship.getPlanet().length() > 50)
            return false;
        if (ship.getName().isEmpty() || ship.getPlanet().isEmpty() || ship.getShipType() == null || ship.getProdDate().getTime() == 0 || ship.getSpeed() == 0 || ship.getCrewSize() == 0)
            return false;
        if (calendar.get(Calendar.YEAR) < 2800 || calendar.get(Calendar.YEAR) > 3019)
            return false;
        if (ship.getSpeed() < 0.01 || ship.getSpeed() > 0.99)
            return false;
        if (ship.getCrewSize() < 1 || ship.getCrewSize() > 9999)
            return false;

        return true;
    }

    @GetMapping(value = "/ships/{id}")
    public @ResponseBody ResponseEntity<Ship> findShip(@PathVariable Long id, Model model) {

        if (!idValid(id)) {
            return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
        }

        Optional<Ship> optional = shipRepository.findById(id);
        if (optional.isEmpty()) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }

        Ship ship = optional.get();
        return new ResponseEntity<Ship>(ship, HttpStatus.OK);

    }

    private boolean idValid(Long id) {

        Long longId = (Long) id;

        if (id <= 0 || !longId.equals(id))
            return false;

        return true;
    }

    @PostMapping(value = "/ships")
    public @ResponseBody ResponseEntity<Ship> createShip(@RequestBody Map<String, String> args) {

        String name         = args.containsKey("name")      ? args.get("name") : null;
        String planet       = args.containsKey("planet")    ? args.get("planet") : null;
        ShipType shipType   = args.containsKey("shipType")  ? ShipType.valueOf(args.get("shipType")) : null;
        Long prodDate       = args.containsKey("prodDate")  ? Long.parseLong(args.get("prodDate")) : null;
        Boolean isUsed      = args.containsKey("isUsed")    ? Boolean.parseBoolean(args.get("isUsed")) : false;
        Double speed        = args.containsKey("speed")     ? Double.parseDouble(args.get("speed")) : null;
        Integer crewSize    = args.containsKey("crewSize")  ? Integer.parseInt(args.get("crewSize")) : null;

        if (name == null || planet == null || shipType == null || prodDate == null || speed == null || crewSize == null)
            return new ResponseEntity(null, HttpStatus.BAD_REQUEST);

        Ship ship = new Ship(name, planet, shipType, new Date(prodDate), isUsed, speed, crewSize);

        if (!checkShip(ship))
            return new ResponseEntity(null, HttpStatus.BAD_REQUEST);

        shipRepository.save(ship);

        return new ResponseEntity(ship, HttpStatus.OK);
    }

    @RequestMapping(value = "/ships/{id}", method = RequestMethod.DELETE)
    public @ResponseBody ResponseEntity<Ship> deleteShip(@PathVariable Long id) {

        if (!idValid(id)) {
            return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
        }

        Optional<Ship> optional = shipRepository.findById(id);
        if (optional.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        Ship ship = optional.get();
        shipRepository.delete(ship);
        return new ResponseEntity<Ship>(HttpStatus.OK);

    }

    public void test2() {

    }

    public void test() {
        System.out.println("yraaaaa");
    }

}
