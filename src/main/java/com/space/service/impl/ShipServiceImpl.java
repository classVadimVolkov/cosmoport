package com.space.service.impl;

import com.space.controller.ShipOrder;
import com.space.exceptions.BadRequestException;
import com.space.exceptions.ShipNotFoundException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShipServiceImpl implements ShipService {
    private ShipRepository shipRepository;

    @Autowired
    public void setShipRepository(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    @Override
    public List<Ship> getSortAllShips(String name, String planet, ShipType shipType,
                                      Long after, Long before, Boolean isUsed,
                                      Double minSpeed, Double maxSpeed,
                                      Integer minCrewSize, Integer maxCrewSize,
                                      Double minRating, Double maxRating) {

        List<Ship> sortAllShips = shipRepository.findAll();

        if (name != null) {
            sortAllShips = sortAllShips.stream()
                    .filter(ship -> ship.getName().contains(name))
                    .collect(Collectors.toList());
        }

        if (planet != null) {
            sortAllShips = sortAllShips.stream()
                    .filter(ship -> ship.getPlanet().contains(planet))
                    .collect(Collectors.toList());
        }

        if (shipType != null) {
            sortAllShips = sortAllShips.stream()
                    .filter(ship -> ship.getShipType().equals(shipType))
                    .collect(Collectors.toList());
        }

        if (after != null) {
            sortAllShips = sortAllShips.stream()
                    .filter(ship -> ship.getProdDate().after(new Date(after)))
                    .collect(Collectors.toList());
        }

        if (before != null) {
            sortAllShips = sortAllShips.stream()
                    .filter(ship -> ship.getProdDate().before(new Date(before)))
                    .collect(Collectors.toList());
        }

        if (isUsed != null) {
            sortAllShips = sortAllShips.stream()
                    .filter(ship -> ship.getUsed().equals(isUsed))
                    .collect(Collectors.toList());
        }

        if (minSpeed != null) {
            sortAllShips = sortAllShips.stream()
                    .filter(ship -> ship.getSpeed() >= minSpeed)
                    .collect(Collectors.toList());
        }

        if (maxSpeed != null) {
            sortAllShips = sortAllShips.stream()
                    .filter(ship -> ship.getSpeed() <= maxSpeed)
                    .collect(Collectors.toList());
        }

        if (minCrewSize != null) {
            sortAllShips = sortAllShips.stream()
                    .filter(ship -> ship.getCrewSize() >= minCrewSize)
                    .collect(Collectors.toList());
        }

        if (maxCrewSize != null) {
            sortAllShips = sortAllShips.stream()
                    .filter(ship -> ship.getCrewSize() <= maxCrewSize)
                    .collect(Collectors.toList());
        }

        if (minRating != null) {
            sortAllShips = sortAllShips.stream()
                    .filter(ship -> ship.getRating() >= minRating)
                    .collect(Collectors.toList());
        }

        if (maxRating != null) {
            sortAllShips = sortAllShips.stream()
                    .filter(ship -> ship.getRating() <= maxRating)
                    .collect(Collectors.toList());
        }

        return sortAllShips;
    }

    @Override
    public List<Ship> getShipsForPage(List<Ship> sortAllShips, ShipOrder order,
                                      Integer pageNumber, Integer pageSize) {

        if (pageNumber == null) {
            pageNumber = 0;
        }
        if (pageSize == null) {
            pageSize = 3;
        }

        return sortAllShips.stream()
                .sorted(getComparator(order))
                .skip((long) pageNumber * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    @Override
    public Ship createNewShip(Ship newShip) {
        if (newShip.getName() == null
                || newShip.getPlanet() == null
                || newShip.getShipType() == null
                || newShip.getProdDate() == null
                || newShip.getSpeed() == null
                || newShip.getCrewSize() == null) {
            throw new BadRequestException();
        }

        if (newShip.getUsed() == null) {
            newShip.setUsed(false);
        }

        if (newShip.getName().length() >= 50 || newShip.getPlanet().length() >= 50
                || newShip.getName().isEmpty() || newShip.getPlanet().isEmpty()
                || newShip.getSpeed() < 0.01d || newShip.getSpeed() > 0.99d
                || newShip.getCrewSize() < 1 || newShip.getCrewSize() > 9999) {
            throw new BadRequestException();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(newShip.getProdDate());
        int year = calendar.get(Calendar.YEAR);

        if (year < 2800 || year > 3019) {
            throw new BadRequestException();
        }

        if (newShip.getUsed() == null) {
            newShip.setUsed(false);
        }

        newShip.setRating(shipRatingCalculation(newShip));

        return shipRepository.save(newShip);
    }

    @Override
    public Ship editShip(Ship newEditShip, Long id) {
        // 400
        if (!hasTheCorrectId(id)) throw new BadRequestException();

        // 404
        if (!shipRepository.existsById(id)) throw new ShipNotFoundException();

        // Получаем ship по id и начинаем править в соответствии с newEditShip
        Ship editableShip = getShipById(id);

        // Name
        String name = newEditShip.getName();

        if (name != null) {
            if (name.length() > 50 || name.isEmpty()) throw new BadRequestException();

            editableShip.setName(name);
        }

        String planet = newEditShip.getPlanet();

        if (planet != null) {
            if (planet.length() > 50 || planet.isEmpty()) throw new BadRequestException();

            editableShip.setPlanet(planet);
        }

        if (newEditShip.getShipType() != null)
            editableShip.setShipType(newEditShip.getShipType());

        if (newEditShip.getUsed() != null) {
            editableShip.setUsed(newEditShip.getUsed());
        }

         if (newEditShip.getProdDate() != null) {

            Calendar cal = Calendar.getInstance();
            cal.setTime(newEditShip.getProdDate());
            int year = cal.get(Calendar.YEAR);

            if (year < 2800 || year > 3019) throw new BadRequestException();

            editableShip.setProdDate(newEditShip.getProdDate());
        }

        Double speed = newEditShip.getSpeed();

        if (speed != null) {
            if (speed < 0.01d || speed > 0.99d) throw new BadRequestException();

            editableShip.setSpeed(speed);
        }

        Integer crewSize = newEditShip.getCrewSize();

        if (crewSize != null) {
            if (crewSize < 1 || crewSize > 9999) throw new BadRequestException();

            editableShip.setCrewSize(crewSize);
        }

        editableShip.setRating(shipRatingCalculation(editableShip));
        return editableShip;
    }

    @Override
    public void deleteShipById(Long id) {
        if (!hasTheCorrectId(id)) {
            throw new BadRequestException();
        }

        if (!shipRepository.existsById(id)) {
            throw new ShipNotFoundException();
        }

        shipRepository.deleteById(id);
    }

    @Override
    public Ship getShipById(Long id) {
        if (!hasTheCorrectId(id)) {
            throw new BadRequestException();
        }

        if (!shipRepository.existsById(id)) {
            throw new ShipNotFoundException();
        }

        return shipRepository.findById(id).orElse(null);
    }

    private Comparator<Ship> getComparator(ShipOrder shipOrder) {
        if (shipOrder == null) {
            return Comparator.comparing(Ship::getId);
        }

        Comparator<Ship> comparator = null;
        switch (shipOrder) {
            case ID: comparator = Comparator.comparing(Ship::getId);
            break;
            case DATE: comparator = Comparator.comparing(Ship::getProdDate);
            break;
            case SPEED: comparator = Comparator.comparing(Ship::getSpeed);
            break;
            case RATING: comparator = Comparator.comparing(Ship::getRating);
            break;
        }

        return comparator;
    }

    private Double shipRatingCalculation(Ship ship) {
        double shipSpeed = ship.getSpeed();
        double coefficient = ship.getUsed() ? 0.5d : 1.0d;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ship.getProdDate());
        double shipYear = calendar.get(Calendar.YEAR);
        double currentYear = 3019.0d;

        double rating = 80 * shipSpeed * coefficient / (currentYear - shipYear + 1);

        return (double) Math.round(rating*100) / 100;
    }

    private Boolean hasTheCorrectId(Long id) {
        return id != null
                && id > 0;
    }
}
