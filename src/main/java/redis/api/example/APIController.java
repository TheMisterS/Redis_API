package redis.api.example;

import redis.api.example.dto.Client;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import redis.clients.jedis.JedisPool;

@RestController
@RequestMapping("/client")
public class APIController {

    private JedisPool jedisPool = new JedisPool("localhost", 6379);
    private Map<Integer, Client> clientDatabase = new HashMap<>();
    private int currentId = 1;

    @PutMapping
    public ResponseEntity<String> registerClient(@RequestBody Client client) {
        client.setId(currentId++);
        clientDatabase.put(client.getId(), client);
        return new ResponseEntity<>("Client registered with the ID: " + client.getId(), HttpStatus.OK); //returns 200 -> OK
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable int id) {
        Client client = clientDatabase.get(id);

        if (client == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // returns 404 -> client not found
        }

        return new ResponseEntity<>(client, HttpStatus.OK);
    }


    // after deletion id is not used anymore, could be improved.
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClientById(@PathVariable int id) {
        Client client = clientDatabase.get(id);
        if (client == null) {
            return new ResponseEntity<>("Client not found", HttpStatus.NOT_FOUND); // returns 404 -> client not found
        }

        clientDatabase.remove(id);
        return new ResponseEntity<>("Client deleted successfully", HttpStatus.OK); // returns 200 -> OK
    }

    @PostMapping("/{id}/meter/{meterId}")
    public ResponseEntity<String> appendMeter(@PathVariable int id,@PathVariable String meterId, @RequestBody String meterValue){
        Client currentClient = clientDatabase.get(id);
        if (currentClient == null) {
            return new ResponseEntity<>("Client not found", HttpStatus.NOT_FOUND); // returns 404 -> client not found
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
            return new ResponseEntity<>("Invalid input (must be a number)", HttpStatus.BAD_REQUEST);
        }
        if(meterValueDouble <= 0) return new ResponseEntity<>("Invalid input(value has to be >=0)", HttpStatus.BAD_REQUEST); // returns 400 -> bad request

        Client currentClient = clientDatabase.get(id);
        if(currentClient == null) return new ResponseEntity<>("Client not found", HttpStatus.NOT_FOUND); // returns 404 -> client not found
        currentClient.appendMeter(meterId, currentClient.getMeterValue(meterId)+ meterValueDouble);
        return new ResponseEntity<>("Meter updated succesfully!", HttpStatus.OK);
    }

    @GetMapping("{id}/meter")
    public ResponseEntity<Object> getMeters(@PathVariable int id){
        Client currentClient = clientDatabase.get(id);
        if(currentClient == null) return new ResponseEntity<>("Client not found", HttpStatus.NOT_FOUND); // returns 404 -> client not found

        return new ResponseEntity<>(currentClient.getMeters().keySet(), HttpStatus.OK);
    }

    @GetMapping("/{id}/meter/{meterId}")
    public ResponseEntity<Object> getMeterReading(@PathVariable int id, @PathVariable String meterId){
        Client currentClient = clientDatabase.get(id);
        if(currentClient == null) return new ResponseEntity<>("Client not found", HttpStatus.NOT_FOUND); // returns 404 -> client not found
        if(currentClient.getMeterValue(meterId) == null) return new ResponseEntity<>("Meter not found", HttpStatus.NOT_FOUND); // returns 404 -> meter not found
        return new ResponseEntity<>(currentClient.getMeterValue(meterId), HttpStatus.OK);
    }
}


