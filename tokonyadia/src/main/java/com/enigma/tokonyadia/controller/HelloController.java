package com.enigma.tokonyadia.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HelloController {

    @GetMapping
//	@RequestMapping(value = "/", method = RequestMethod.GET)
    public String hello() {
        return "<h1>Hello World</h1>";
    }

    @GetMapping("/hobbies")
    public String[] getHobbies() {
        return new String[]{"Tidur", "Ibadah"};
    }

    @GetMapping("/person")
    public Map<String, Object> getPerson() {
        Map<String, Object> person = new HashMap<>();

        person.put("name", "Edy");
        person.put("age", 17);
        person.put("is_married", false);

        String[] hobbies = new String[]{"Tidur", "Ibadah"};
        person.put("hobbies", hobbies);

        Map<String, String> address = new HashMap<>();
        address.put("rt", "08");
        address.put("rw", "04");
        address.put("street", "Jl. H. Dahlan");
        person.put("address", address);
        person.put("mobile_phone", null);

        return person;
    }

    // PathVariable
    @GetMapping("/person/{id}")
    public String getPersonById(@PathVariable String id) {
        return "Person " + id;
    }

//    @GetMapping("/products")
//    public String getProductByNameAndPrice(
//            @RequestParam(required = false, defaultValue = "Baju") String name,
//            @RequestParam(required = false, defaultValue = "10000") Integer price) {
//        return name + " : " + "Rp" + price;
//    }

}
