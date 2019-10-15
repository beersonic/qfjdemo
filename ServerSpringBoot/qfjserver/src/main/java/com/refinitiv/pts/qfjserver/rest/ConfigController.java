
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ConfigController{

    private final ConfigRepository configRepo;

    ConfigController(ConfigRepository configuration)
    {
        this.configRepo = configuration;
    }

    @PostMapping("/messageRate")
    public void messageRate(double messageRate)
    {
        configu
    }
}