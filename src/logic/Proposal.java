package logic;

import jade.core.AID;

import java.io.Serializable;
import java.util.ArrayList;

public class Proposal implements Serializable {
    final Integer cfpId;
    final int cfpParentId;
    final AID proposalParentId;
    final ArrayList<Integer> proposedPath;
    final int loadIfAccepts;
    final int minutes;
    final float totalDistance;
    final float consumption;
    final float price;

    public Proposal(Integer cfpId, int cfpParentId, AID proposalParentId, ArrayList<Integer> proposedPath, int loadIfAccepts, int cost, float totalDistance, float consumption, float price) {
        this.proposalParentId = proposalParentId;
        this.proposedPath = proposedPath;
        this.cfpId = cfpId;
        this.loadIfAccepts = loadIfAccepts;
        this.cfpParentId = cfpParentId;
        this.minutes = cost;
        this.totalDistance = totalDistance;
        this.consumption = consumption;
        this.price = price;
    }

    public AID getProposalParentId() {
        return proposalParentId;
    }

    public ArrayList<Integer> getProposedPath() {
        return proposedPath;
    }

    public Integer getCfpId() {
        return cfpId;
    }

    public int getLoadIfAccepts() {
        return loadIfAccepts;
    }

    public int getCfpParentId() {
        return cfpParentId;
    }

    public int getMinutes() {
        return minutes;
    }

    public float getTotalDistance() {
        return totalDistance;
    }

    public float getConsumption() {
        return consumption;
    }

    public float getPrice() {
        return price;
    }

    public String toString() {
        return "ID: " + cfpId + " " + proposedPath.get(0) + " -> " + proposedPath.get(proposedPath.size() - 1) + " LOAD: " + loadIfAccepts + "% " + minutes + " min to travel " + totalDistance + "km consuming " + consumption + " with a cost of " + price + " €.";
    }
}
