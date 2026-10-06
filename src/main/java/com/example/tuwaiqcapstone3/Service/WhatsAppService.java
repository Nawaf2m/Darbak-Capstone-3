package com.example.tuwaiqcapstone3.Service;

import com.example.tuwaiqcapstone3.Model.Ride;
import com.example.tuwaiqcapstone3.Model.RidePerticipant;
import com.example.tuwaiqcapstone3.Model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class WhatsAppService {

    @Value("${ultramsg.instance}")
    private String instance;

    @Value("${ultramsg.token}")
    private String token;

    // ---------- base method ----------

    public void sendMessage(String phone, String text) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            String url = "https://api.ultramsg.com/" + instance + "/messages/chat?token=" + token;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = Map.of(
                    "to", toInternational(phone),
                    "body", text
            );

            restTemplate.postForObject(url, new HttpEntity<>(body, headers), String.class);
        } catch (Exception e) {
            // a failed WhatsApp message must not break the main operation
            System.out.println("WhatsApp message failed: " + e.getMessage());
        }
    }

    // 05XXXXXXXX  ->  +9665XXXXXXXX
    private String toInternational(String phone) {
        if (phone.startsWith("05")) {
            return "+966" + phone.substring(1);
        }
        return phone;
    }

    // ---------- notifications ----------

    // driver: a passenger sent a join request
    public void notifyDriverNewRequest(Ride ride, User passenger) {
        sendMessage(ride.getDriver().getPhoneNumber(),
                "New join request from " + passenger.getName() + " for your ride to " + ride.getDestination());
    }

    // passenger: request accepted
    public void notifyPassengerRequestAccepted(Ride ride, User passenger) {
        sendMessage(passenger.getPhoneNumber(),
                "Your request to join the ride to " + ride.getDestination()
                        + " was accepted. Meeting point: " + ride.getMeetingPoint());
    }

    // passenger: request rejected
    public void notifyPassengerRequestRejected(Ride ride, User passenger) {
        sendMessage(passenger.getPhoneNumber(),
                "Sorry, your request to join the ride to " + ride.getDestination() + " was rejected.");
    }

    // all passengers: ride status changed (completed / cancelled)
    public void notifyRideStatusChanged(Ride ride) {
        if (ride.getRidePerticipants() == null) {
            return;
        }

        String message;
        if ("completed".equals(ride.getStatus())) {
            message = "Your ride to " + ride.getDestination() + " has been completed. You can now review the driver.";
        } else if ("cancelled".equals(ride.getStatus())) {
            message = "Sorry, your ride to " + ride.getDestination() + " has been cancelled by the driver.";
        } else {
            message = "The status of your ride to " + ride.getDestination() + " is now " + ride.getStatus();
        }

        for (RidePerticipant p : ride.getRidePerticipants()) {
            // skip the driver if he is also saved as a participant
            if (!p.getUser().getId().equals(ride.getDriver().getId())) {
                sendMessage(p.getUser().getPhoneNumber(), message);
            }
        }
    }
}