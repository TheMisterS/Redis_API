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

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@RestController
@RequestMapping("/client")
public class APIController {

    private JedisPool jedisPool = new JedisPool("localhost", 6379);
//    private Map<Integer, Client> clientDatabase = new HashMap<>(); // not multithread safe, could cause incosistency issues
 //   private int currentId = 1;


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
    public ResponseEntity<Client> getClientById(@PathVariable int id) throws IOException {
        Jedis jedis = jedisPool.getResource();
        String clientKey = "client:" + String.valueOf(id);
        String clientJson = jedis.get(String.valueOf(clientKey));

        ObjectMapper objectMapper = new ObjectMapper();
        Client client = objectMapper.readValue(clientJson, Client.class);
        return new ResponseEntity<>(client, HttpStatus.OK);
    }

/*
    // after deletion id is not used anymore, could be improved.
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClientById(@PathVariable int id) {
        Client client = clientDatabase.get(id);
        if (client == null) {
            //return new ResponseEntity<>("Client not found", HttpStatus.NOT_FOUND); // returns 404 -> client not found
            throw new ClientNotFoundException(id); // returns 404 -> client not found
        }

        clientDatabase.remove(id);
        return new ResponseEntity<>("Client deleted successfully", HttpStatus.OK); // returns 200 -> OK
    }

    @PostMapping("/{id}/meter/{meterId}")
    public ResponseEntity<String> appendMeter(@PathVariable int id,@PathVariable String meterId, @RequestBody String meterValue){
        Client currentClient = clientDatabase.get(id);
        if (currentClient == null) {
            //return new ResponseEntity<>("Client not found", HttpStatus.NOT_FOUND); // returns 404 -> client not found
            throw new ClientNotFoundException(id); // returns 404 -> client not found
        } else if(currentClient.appendMeter(meterId, Double.parseDouble(meterValue))) {  //new meter created
                                                                                          //!!!!!!!!! need to improve parsing to make sense of lithuanian double standart
            clientDatabase.put(id, currentClient);
            return new ResponseEntity<>("New meter created succesfully!", HttpStatus.OK);

        }
        // else, meter is updated
        return new ResponseEntity<>("Meter updated succesfully!", HttpStatus.OK);

    }


    @PostMapping("/{id}/meter/{meterId}/add")
        public ResponseEntity<String> addToMeter(@PathVariable int id,@PathVariable String meterId, @RequestBody String meterValue){
        double meterValueDouble;

        try {
            meterValueDouble = Double.parseDouble(meterValue);
        } catch (NumberFormatException e) {
            //return new ResponseEntity<>("Invalid input (must be a number)", HttpStatus.BAD_REQUEST);
            throw new InvalidMeterValueException(meterValue);
        }
        if(meterValueDouble <= 0) throw new InvalidMeterValueException(meterValueDouble);
            //return new ResponseEntity<>("Invalid input(value has to be >=0)", HttpStatus.BAD_REQUEST); // returns 400 -> bad request

        Client currentClient = clientDatabase.get(id);
        if(currentClient == null) throw  new ClientNotFoundException(id); // returns 404 -> client not found
        //return new ResponseEntity<>("Client not found", HttpStatus.NOT_FOUND); // returns 404 -> client not found
        currentClient.appendMeter(meterId, currentClient.getMeterValue(meterId)+ meterValueDouble);
        return new ResponseEntity<>("Meter updated succesfully!", HttpStatus.OK);
    }

    @GetMapping("{id}/meter")
    public ResponseEntity<Object> getMeters(@PathVariable int id){
        Client currentClient = clientDatabase.get(id);
        if(currentClient == null) throw new ClientNotFoundException(id); // returns 404 -> client not found
            //return new ResponseEntity<>("Client not found", HttpStatus.NOT_FOUND); // returns 404 -> client not found

        return new ResponseEntity<>(currentClient.getMeters().keySet(), HttpStatus.OK);
    }

    @GetMapping("/{id}/meter/{meterId}")
    public ResponseEntity<Object> getMeterReading(@PathVariable int id, @PathVariable String meterId){
        Client currentClient = clientDatabase.get(id);
        if(currentClient == null) throw new ClientNotFoundException(id); // returns 404 -> client not found
            //return new ResponseEntity<>("Client not found", HttpStatus.NOT_FOUND); // returns 404 -> client not found
        if(currentClient.getMeterValue(meterId) == null) throw new MeterNotFoundException(); // returns 404 -> meter not found
            //return new ResponseEntity<>("Meter not found", HttpStatus.NOT_FOUND); // returns 404 -> meter not found
        return new ResponseEntity<>(currentClient.getMeterValue(meterId), HttpStatus.OK);
    }

 */
}


