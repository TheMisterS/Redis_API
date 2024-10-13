package redis.api.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import redis.api.example.dto.*;
import redis.api.example.exceptions.ClientNotFoundException;
import redis.api.example.exceptions.InvalidMeterValueException;
import redis.api.example.exceptions.MeterNotFoundException;

import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@RestController
@RequestMapping("/client")
public class APIController {

    private JedisPool jedisPool = new JedisPool("localhost", 6379);

    @PutMapping
    public ResponseEntity<String> registerClient(@RequestBody Client client) throws JsonProcessingException {
        Jedis jedis = jedisPool.getResource();

        long nextId = jedis.incr("client:id:counter");
        client.setId((int)nextId);


        String clientJson = ClientSerialization.serialize(client);
        String clientKey = "client:" + client.getId();
        jedis.set(clientKey, clientJson);

        return new ResponseEntity<>("Client registered with the ID: " + client.getId(), HttpStatus.OK); //returns 200 -> OK

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getClientById(@PathVariable int id) throws IOException {

       try( Jedis jedis = jedisPool.getResource()) {
           String clientKey = "client:" + String.valueOf(id);
           if (jedis.get(String.valueOf(clientKey)) == null) {
               throw new ClientNotFoundException(id);
           }
           String clientJson = jedis.get(String.valueOf(clientKey));

           ObjectMapper objectMapper = new ObjectMapper();
           Client client = objectMapper.readValue(clientJson, Client.class);
           return new ResponseEntity<>(client, HttpStatus.OK);
       }
    }


    // after deletion id is not used anymore, could be improved.
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClientById(@PathVariable int id) {
        try (Jedis jedis = jedisPool.getResource()) {
            String clientKey = "client:" + String.valueOf(id);
            if (jedis.get(String.valueOf(clientKey)) == null) {
                throw new ClientNotFoundException(id);
            }
            jedis.del(clientKey);
            return new ResponseEntity<>("Client deleted successfully", HttpStatus.OK); // returns 200 -> OK
        }
    }

    @PostMapping("/{id}/meter/{meterId}")
    public ResponseEntity<String> appendMeter(@PathVariable int id,@PathVariable String meterId, @RequestBody String meterValue) throws JsonProcessingException {

        //check if it is a double
        try {
            double meterValueDouble = Double.parseDouble(meterValue);
        } catch (NumberFormatException e) {
            throw new InvalidMeterValueException(meterValue);
        }
       try( Jedis jedis = jedisPool.getResource()) {
           String clientKey = "client:" + String.valueOf(id);

           if (jedis.get(String.valueOf(clientKey)) == null) {
               throw new ClientNotFoundException(id);
           }

           ObjectMapper objectMapper = new ObjectMapper();
           String clientJson = jedis.get(String.valueOf(clientKey));
           Client client = objectMapper.readValue(clientJson, Client.class);
           String meterKey = "client:" + String.valueOf(id) + ":meter:" + meterId;
           jedis.set(meterKey, meterValue);

           // keeps track of the meter list for each client for GET requests
           jedis.sadd("client:" + id + ":meters", meterId);

           return new ResponseEntity<>("Meter appended succesfully!", HttpStatus.OK);
       }
    }


    @PostMapping("/{id}/meter/{meterId}/add")
    public ResponseEntity<String> addToMeter(@PathVariable int id,@PathVariable String meterId, @RequestBody String meterValue){

        double meterValueDouble;
        try {
            meterValueDouble = Double.parseDouble(meterValue);
        } catch (NumberFormatException e) {
            throw new InvalidMeterValueException(meterValue);
        }
        if(meterValueDouble <= 0) throw new InvalidMeterValueException(meterValueDouble);

        try (Jedis jedis = jedisPool.getResource()) {
            String clientKey = "client:" + String.valueOf(id);
            String meterKey = "client:" + id + ":meter:" + meterId;

            if (jedis.get(clientKey) == null) {
                throw new ClientNotFoundException(id);
            }
            if(jedis.get(meterKey) == null){
                throw new MeterNotFoundException();
            }

            String currentMeterValue = jedis.get(meterKey);

            double updatedMeterValue = Double.parseDouble(currentMeterValue) + meterValueDouble;
            jedis.set(meterKey, String.valueOf(updatedMeterValue));

            return new ResponseEntity<>("Meter updated succesfully!", HttpStatus.OK);
        }
    }


    @GetMapping("{id}/meter")
    public ResponseEntity<?> getMeters(@PathVariable int id){

        try (Jedis jedis = jedisPool.getResource()) {
            String clientKey = "client:" + String.valueOf(id);

            if (jedis.get(clientKey) == null) {
                throw new ClientNotFoundException(id);
            }

            Set<String> meterIds = jedis.smembers("client:" + id + ":meters"); // get client's meter IDs
            return new ResponseEntity<>(meterIds, HttpStatus.OK);
        }
    }

    @GetMapping("/{id}/meter/{meterId}")
    public ResponseEntity<?> getMeterReading(@PathVariable int id, @PathVariable String meterId){

        try(Jedis jedis = jedisPool.getResource()) {
            String clientKey = "client:" + String.valueOf(id);
            String meterKey = "client:" + String.valueOf(id) + ":meter:" + meterId;

            if (jedis.get(clientKey) == null) {
                throw new ClientNotFoundException(id);
            }
            if (jedis.get(meterKey) == null) {
                throw new MeterNotFoundException();
            }

            String meterValue = jedis.get(meterKey);
            return new ResponseEntity<>(meterValue, HttpStatus.OK);
        }
    }
}


