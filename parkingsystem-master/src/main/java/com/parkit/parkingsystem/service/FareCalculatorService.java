package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public double calculateFare(Ticket ticket, int nombreOccurenceTicket) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString() + ticket.getInTime().toString());
        }


        final String welomeBackText = "Welcome back! your loyalty allows you to benefit from a 5% discount.";
        // getTime() replace getHours() deprecated méthode
        // Use double type instead of int
        double inHour = ticket.getInTime().getTime();
        double outHour = ticket.getOutTime().getTime();
        // TODO: Some tests are failing here. Need to check if this logic is correct =>
        double duration = ((outHour - inHour) / (60 * 60 * 1000));


        // Price is max if user park for the first time and stays more than half an hour
        if (duration > 0.5 && nombreOccurenceTicket == 1) {
            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR: {
                    // ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                    return duration * Fare.CAR_RATE_PER_HOUR;
                    // break;
                }
                case BIKE: {
                    // ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                    return duration * Fare.BIKE_RATE_PER_HOUR;
                    // break;
                }
                default:
                    throw new IllegalArgumentException("Unkown Parking Type");
            }
            // parking feature free for the first 30 minutes
        }else if (duration < 0.5) {
                return duration * Fare.PARK_LESS_THAN_HALF_HOUR;
        }
        return 1;
    }
}