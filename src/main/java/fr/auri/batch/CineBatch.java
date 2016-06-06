package fr.auri.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by CER3100444 on 06/06/2016.
 */
@SpringBootApplication
public class CineBatch {

    public static void main(String [] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(
                BatchConfig.class, args)));

    }
}
