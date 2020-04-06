package com.pracownia.vanet;

import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Data
public class StationaryNetworkPoint extends NetworkPoint
{
    private final static double TRUST_LEVEL_INCREASE = 0.1;
    private final static double TRUST_LEVEL_DECREASE = 0.4;

    public StationaryNetworkPoint(int id, Point currentLocation, double range)
    {
        super(id, currentLocation, range);
    }

    public void checkIfChangeVehicleTrustLevel() {
        for(Vehicle v : this.connectedVehicles){
            if(AntyBogus.vehiclesToIncreaseTrustLevel.contains(v)){
                increaseVehicleTrustLevel(v);
                AntyBogus.vehiclesToIncreaseTrustLevel.remove(v);
                Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
                Logger.log("[" + timeStamp + "] Increased trust level of Vehicle " + v.id);
                System.out.println("[" + timeStamp + "] Increased trust level of Vehicle " + v.id);
            } else if(AntyBogus.vehiclesToDecreaseTrustLevel.contains(v)){
                decreaseVehicleTrustLevel(v);
                AntyBogus.vehiclesToDecreaseTrustLevel.remove(v);
                Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
                Logger.log("[" + timeStamp + "] Decreased trust level of Vehicle " + v.id);
                System.out.println("[" + timeStamp + "] Decreased trust level of Vehicle " + v.id);
            }
        }
    }

    public void checkForSybilVehicles() {
        for (Vehicle v1 : connectedVehicles) {
            if (!v1.isSafe()) continue;
            for (Vehicle v2 : connectedVehicles) {
                if (v1 == v2 || !v2.isSafe()) continue;
                if (areChainTagsSimilar(v1.getObtainedTags(), v2.getObtainedTags())) {
                    markAsSybil(v1);
                    markAsSybil(v2);
                }
            }
        }
    }

    private void markAsSybil(Vehicle v) {
        v.setNotSafe("Its a sybil");
    }

    private boolean areChainTagsSimilar(LinkedList<RLUTag> firstVehicleTags, LinkedList<RLUTag> secondVehicleTags) {
        int yeetVehicleTreshold = 3;
        int sameTagsCount = 0;
        if (firstVehicleTags.size() < yeetVehicleTreshold || secondVehicleTags.size() < yeetVehicleTreshold)
            return false;
        for (int i = 0; i < Math.min(firstVehicleTags.size(), secondVehicleTags.size()); i++) {
            if (firstVehicleTags.get(i).equals(secondVehicleTags.get(i))) {
                sameTagsCount++;
            }
        }
        if (sameTagsCount >= yeetVehicleTreshold)
            return true;
        return false;
    }

    private void increaseVehicleTrustLevel(Vehicle vehicle) {
        double previousTrustLevel = vehicle.getTrustLevel();

        vehicle.setTrustLevel(previousTrustLevel + TRUST_LEVEL_INCREASE);
    }

    private void decreaseVehicleTrustLevel(Vehicle vehicle) {
        double previousTrustLevel = vehicle.getTrustLevel();

        vehicle.setTrustLevel(previousTrustLevel - TRUST_LEVEL_DECREASE);
    }

    public RLUTag obtainTag() {
        int roundedTimestamp = (int) new Date().getTime() / 100 * 100;
        return new RLUTag(id, roundedTimestamp);
    }

}
