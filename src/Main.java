import com.voting.service.AuthService;
import com.voting.view.LoginFrame;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Online Voting System");

        SwingUtilities.invokeLater(() -> {
            AuthService authService = new AuthService();
            new LoginFrame(authService);
        });
    }
}