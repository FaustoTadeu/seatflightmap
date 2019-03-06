package ie.com.faustoalves.seatflightmap;

//import ie.com.faustoalves.seatflightmap.property.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
//@EnableConfigurationProperties({
//        FileStorageProperties.class
//})
public class SeatflightmapApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeatflightmapApplication.class, args);
    }

}
