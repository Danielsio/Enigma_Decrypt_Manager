package dto;

public class Secret {
    private String rotors;
    private String windows;
    private String reflector;
    private String plugs;

    public Secret() {
    }

    public Secret(String rotors, String windows, String reflector, String plugs) {
        this.rotors = rotors;
        this.windows = windows;
        this.reflector = reflector;
        this.plugs = plugs;
    }

    public String getRotors() {
        return rotors;
    }

    public void setRotors(String rotors) {
        this.rotors = rotors;
    }

    public String getWindows() {
        return windows;
    }

    public void setWindows(String windows) {
        this.windows = windows;
    }

    public String getReflector() {
        return reflector;
    }

    public void setReflector(String reflector) {
        this.reflector = reflector;
    }

    public String getPlugs() {
        return plugs;
    }

    public void setPlugs(String plugs) {
        this.plugs = plugs;
    }
}