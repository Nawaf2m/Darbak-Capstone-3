package com.example.tuwaiqcapstone3.Service;

import com.example.tuwaiqcapstone3.API.ApiException;
import com.example.tuwaiqcapstone3.Model.Stadium;
import com.example.tuwaiqcapstone3.Repository.StadiumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StadiumService {

    private final StadiumRepository stadiumRepository;

    public List<Stadium> getStadiums() {
        return stadiumRepository.findAll();
    }

    public Stadium getStadiumById(Integer id) {
        Stadium stadium = stadiumRepository.findStadiumById(id);

        if (stadium == null) {
            throw new ApiException("stadium not found");
        }

        return stadium;
    }

    public void addStadium(Stadium stadium) {
        stadiumRepository.save(stadium);
    }

    public void updateStadium(Integer id, Stadium stadium) {
        Stadium oldStadium = stadiumRepository.findStadiumById(id);

        if (oldStadium == null) {
            throw new ApiException("stadium not found");
        }

        oldStadium.setName(stadium.getName());
        oldStadium.setCity(stadium.getCity());
        oldStadium.setAddress(stadium.getAddress());
        oldStadium.setLatitude(stadium.getLatitude());
        oldStadium.setLongitude(stadium.getLongitude());

        stadiumRepository.save(oldStadium);
    }

    public void deleteStadium(Integer id) {
        Stadium stadium = stadiumRepository.findStadiumById(id);

        if (stadium == null) {
            throw new ApiException("stadium not found");
        }

        stadiumRepository.delete(stadium);
    }
}
