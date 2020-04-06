package com.pracownia.vanet;

import java.util.ArrayList;
import java.util.List;

public class SybilVehicle extends Vehicle {
    private final List<Vehicle> fakeVehicles = new ArrayList<Vehicle>();

    public SybilVehicle(Route route, int id, double range, double speed, int fakeCount)
    {
        super(route, id, range, speed);
        for (int i = 0; i < fakeCount; i++) {
            fakeVehicles.add(new Vehicle(route, id + i, range, speed));
        }
    }

    @Override
    public void update(Map map) {
        super.update(map);
        for (Vehicle v : fakeVehicles) {
            v.update(map);
        }
    }

}
