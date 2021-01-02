package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/rest")
public class ShipController {
    private ShipService shipService;

    @Autowired
    public void setShipService(ShipService shipService) {
        this.shipService = shipService;
    }

    @GetMapping("/ships")
    public List<Ship> getAllShips(@RequestParam(required = false) String name,
                                  @RequestParam(required = false) String planet,
                                  @RequestParam(required = false) ShipType shipType,
                                  @RequestParam(required = false) Long after,
                                  @RequestParam(required = false) Long before,
                                  @RequestParam(required = false) Boolean isUsed,
                                  @RequestParam(required = false) Double minSpeed,
                                  @RequestParam(required = false) Double maxSpeed,
                                  @RequestParam(required = false) Integer minCrewSize,
                                  @RequestParam(required = false) Integer maxCrewSize,
                                  @RequestParam(required = false) Double minRating,
                                  @RequestParam(required = false) Double maxRating,
                                  @RequestParam(required = false) ShipOrder order,
                                  @RequestParam(required = false) Integer pageNumber,
                                  @RequestParam(required = false) Integer pageSize) {

        List<Ship> ships = shipService.getSortAllShips(name, planet, shipType,
                after, before, isUsed,
                minSpeed, maxSpeed, minCrewSize,
                maxCrewSize, minRating, maxRating);

        return shipService.getShipsForPage(ships, order, pageNumber, pageSize);
    }

    @GetMapping("/ships/count")
    public Integer getCountSortAllShips(@RequestParam(required = false) String name,
                                               @RequestParam(required = false) String planet,
                                               @RequestParam(required = false) ShipType shipType,
                                               @RequestParam(required = false) Long after,
                                               @RequestParam(required = false) Long before,
                                               @RequestParam(required = false) Boolean isUsed,
                                               @RequestParam(required = false) Double minSpeed,
                                               @RequestParam(required = false) Double maxSpeed,
                                               @RequestParam(required = false) Integer minCrewSize,
                                               @RequestParam(required = false) Integer maxCrewSize,
                                               @RequestParam(required = false) Double minRating,
                                               @RequestParam(required = false) Double maxRating) {

        return shipService.getSortAllShips(name, planet, shipType,
                after, before, isUsed,
                minSpeed, maxSpeed, minCrewSize,
                maxCrewSize, minRating, maxRating).size();
    }

    @PostMapping("/ships")
    @ResponseBody
    public Ship createNewShip(@RequestBody Ship newShip) {
        return shipService.createNewShip(newShip);
    }

    @GetMapping("/ships/{id}")
    public Ship getShipById(@PathVariable Long id) {
        return shipService.getShipById(id);
    }

    @PostMapping("/ships/{id}")
    @ResponseBody
    public Ship editShip(@RequestBody Ship newCharacteristics, @PathVariable Long id) {
        return shipService.editShip(newCharacteristics, id);
    }

    @DeleteMapping("/ships/{id}")
    public void deleteShipById(@PathVariable Long id) {
        shipService.deleteShipById(id);
    }

}
