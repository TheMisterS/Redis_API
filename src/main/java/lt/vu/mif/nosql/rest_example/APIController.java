package lt.vu.mif.nosql.rest_example;

import lt.vu.mif.nosql.rest_example.dto.Client;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import redis.clients.jedis.JedisPool;g



@RestController
@RequestMapping("/client")
public class APIController {
    
    // Connects to Redis, assuming Redis is running on the same machine
    // (incl. exposed via Docker) with default port 6379
    private JedisPool jedisPool = new JedisPool("localhost", 6379);

    private Pattern licensePlatePattern = Pattern.compile("^[A-Z0-9]{1,7}$");

    private Map<Integer, Client> clientDatabase = new HashMap<>();
    private int currentId = 0;

    @PutMapping
    public ResponseEntity<String> registerClient(@RequestBody Client client) {
        client.setId(currentId++);
        clientDatabase.put(client.getId(), client);
        return new ResponseEntity<>("Client registered with the ID: " + client.getId(), HttpStatus.OK);
    }

}
