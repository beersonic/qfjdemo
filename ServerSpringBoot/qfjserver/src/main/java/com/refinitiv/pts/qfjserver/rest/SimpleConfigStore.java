public class SimpleConfigStore implements ConfigRepository
{
    Config config;

    public SimpleConfigStore()
    {
        config = new Config();
    }

    @Override
    public Config GetConfig() {
        return config;
    }
    
}