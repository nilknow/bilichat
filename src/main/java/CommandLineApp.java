import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import tool.Live;

public class CommandLineApp {
    @Parameter(names={"--mode","-m"})
    String mode;

    public static void main(String[] args){
        CommandLineApp app = new CommandLineApp();
        JCommander.newBuilder()
                .addObject(app)
                .build()
                .parse(args);
        app.run();
    }

    public void run() {
        switch (mode) {
            case "interest":
                Live.searchForInterest();
                break;
            case "app":
                App.run();
        }
    }
}
