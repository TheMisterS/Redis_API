package lt.vu.mif.nosql.rest_example;

import lt.vu.mif.nosql.rest_example.dto.Client;
import lt.vu.mif.nosql.rest_example.dto.Meter;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import redis.clients.jedis.JedisPool;



@RestController
@RequestMapping("/client")
public class APIController {
    
    // Connects to Redis, assuming Redis is running on the same machine
    // (incl. exposed via Docker) with default port 6379
    private JedisPool jedisPool = new JedisPool("localhost", 6379);

    private Map<Integer, Client> clientDatabase = new HashMap<>();
    private int currentId = 0;

    @PutMapping
    public ResponseEntity<String> registerClient(@RequestBody Client client) {
        client.setId(currentId++);
        clientDatabase.put(client.getId(), client);
        return new ResponseEntity<>("Client registered with the ID: " + client.getId(), HttpStatus.OK); //returns 200 -> OK
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable int id) {
        Client client = clientDatabase.get(id);

        if (client == null) {    // Checking if the client exists
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // returns 404 -> client not found
        }

        return new ResponseEntity<>(client, HttpStatus.OK);
    }

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
    public ResponseEntity<String> addToMeter(@PathVariable int id,@PathVariable String meterId, @RequestBody Double meterValue){
        Client currentClient = clientDatabase.get(id);
        if(currentClient == null) return new ResponseEntity<>("Client not found", HttpStatus.NOT_FOUND); // returns 404 -> client not found
        currentClient.appendMeter(meterId)
        return new ResponseEntity<>("Meter updated succesfully!", HttpStatus.OK);
    }
}
