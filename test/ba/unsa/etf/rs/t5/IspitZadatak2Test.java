package ba.unsa.etf.rs.t5;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class IspitZadatak2Test {
    KorisniciModel model;
    KeyCode ctrl;

    @Start
    public void start(Stage stage) throws Exception {
        model = new KorisniciModel();
        model.napuni();
        KorisnikController controller = new KorisnikController(model);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/korisnici.fxml"));
        loader.setController(controller);
        Parent root = loader.load();
        stage.setTitle("Korisnici");
        stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        stage.show();
        stage.toFront();

        ctrl = KeyCode.CONTROL;
        if (System.getProperty("os.name").equals("Mac OS X"))
            ctrl = KeyCode.COMMAND;
    }

    @Test
    void defaultTest(FxRobot robot) {
        // Sa default podacima nema ponavljanja
        robot.clickOn("#btnProvjeri");

        // Čekamo da dijalog postane vidljiv
        robot.lookup(".dialog-pane").tryQuery().isPresent();

        // Klik na dugme Ok
        DialogPane dialogPane = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        robot.clickOn(okButton);
    }

    @Test
    void addModelTest(FxRobot robot) {
        Button btnDodaj = robot.lookup("#btnDodaj").queryAs(Button.class);

        robot.clickOn("Delić Amra");
        robot.clickOn("#btnDodaj");
        robot.clickOn("#fldIme").write("Kanita");
        robot.clickOn("#fldPrezime").write("Đokić");
        robot.clickOn("#fldEmail").write("kdokic1@etf.unsa.ba");
        robot.clickOn("#fldUsername").write("amrad"); // duplikat!
        robot.clickOn("#fldPassword").write("kdokic1");

        robot.clickOn("Sijerčić Tarik");

        robot.clickOn("#btnProvjeri");
        // Provjera da je Amra Delić trenutni korisnik

        assertEquals("Delić", model.getTrenutniKorisnik().getPrezime());
        assertEquals("Amra", model.getTrenutniKorisnik().getIme());
        assertEquals("amrad", model.getTrenutniKorisnik().getUsername());
    }

    @Test
    void addUiTest(FxRobot robot) {
        robot.clickOn("Delić Amra");
        robot.clickOn("#btnDodaj");
        robot.clickOn("#fldIme").write("Kanita");
        robot.clickOn("#fldPrezime").write("Đokić");
        robot.clickOn("#fldEmail").write("kdokic1@etf.unsa.ba");
        robot.clickOn("#fldUsername").write("rijadf"); // duplikat!
        robot.clickOn("#fldPassword").write("kdokic1");

        robot.clickOn("Sijerčić Tarik");

        robot.clickOn("#btnProvjeri");
        // Provjera da je Rijad Fejzić trenutni korisnik

        TextField fldPrezime = robot.lookup("#fldPrezime").queryAs(TextField.class);
        assertEquals("Fejzić", fldPrezime.getText());
        TextField fldIme = robot.lookup("#fldIme").queryAs(TextField.class);
        assertEquals("Rijad", fldIme.getText());
        TextField fldUsername = robot.lookup("#fldUsername").queryAs(TextField.class);
        assertEquals("rijadf", fldUsername.getText());

        // Polje je označeno kao nevalidno
        assertFalse(KorisnikControllerTest.sadrziStil(fldUsername, "poljeIspravno"));
        assertTrue(KorisnikControllerTest.sadrziStil(fldUsername, "poljeNijeIspravno"));
    }

    @Test
    void editTest(FxRobot robot) {
        TextField fldUsername = robot.lookup("#fldUsername").queryAs(TextField.class);

        // Prepravićemo postojećeg korisnika da ima isti username kao neki drugi
        robot.clickOn("Sijerčić Tarik");
        robot.clickOn("#fldUsername");
        robot.press(ctrl).press(KeyCode.A).release(KeyCode.A).release(ctrl);
        robot.press(KeyCode.DELETE).release(KeyCode.DELETE);
        robot.write("vedranlj");

        // Provjera da li se zapamtila promjena
        robot.clickOn("Fejzić Rijad");
        robot.clickOn("Sijerčić Tarik");
        assertEquals("vedranlj", fldUsername.getText());
        ObservableList<Korisnik> korisniks = model.getKorisnici();
        assertEquals("vedranlj", korisniks.get(2).getUsername());

        // Sada pokrećemo provjeru
        robot.clickOn("Fejzić Rijad");
        robot.clickOn("#btnProvjeri");

        // Editovanje treba pokazivati korisnika Vedran Ljubović
        TextField fldPrezime = robot.lookup("#fldPrezime").queryAs(TextField.class);
        assertEquals("Ljubović", fldPrezime.getText());
        TextField fldIme = robot.lookup("#fldIme").queryAs(TextField.class);
        assertEquals("Vedran", fldIme.getText());
        assertEquals("vedranlj", fldUsername.getText());

        // Polje je označeno kao nevalidno
        assertFalse(KorisnikControllerTest.sadrziStil(fldUsername, "poljeIspravno"));
        assertTrue(KorisnikControllerTest.sadrziStil(fldUsername, "poljeNijeIspravno"));
    }

    @Test
    void editModelTest(FxRobot robot) {
        // Korisnika Tarik Sijerčić prepravljamo kroz model tako da ima isti username kao Rijad Fejzić
        ObservableList<Korisnik> korisniks = model.getKorisnici();
        korisniks.get(2).setUsername("rijadf");

        TextField fldUsername = robot.lookup("#fldUsername").queryAs(TextField.class);

        // Provjera da li se zapamtila promjena
        robot.clickOn("Fejzić Rijad");
        robot.clickOn("Sijerčić Tarik");
        assertEquals("rijadf", fldUsername.getText());

        // Sada pokrećemo provjeru
        robot.clickOn("Delić Amra");
        robot.clickOn("#btnProvjeri");

        // Editovanje treba pokazivati korisnika Sijerčić Tarik
        TextField fldPrezime = robot.lookup("#fldPrezime").queryAs(TextField.class);
        assertEquals("Sijerčić", fldPrezime.getText());
        TextField fldIme = robot.lookup("#fldIme").queryAs(TextField.class);
        assertEquals("Tarik", fldIme.getText());
        assertEquals("rijadf", fldUsername.getText());

        // Polje je označeno kao nevalidno
        assertFalse(KorisnikControllerTest.sadrziStil(fldUsername, "poljeIspravno"));
        assertTrue(KorisnikControllerTest.sadrziStil(fldUsername, "poljeNijeIspravno"));
    }

    @Test
    void popravkaTest(FxRobot robot) {
        // Popravljamo korisnika Amra Delić da ima isti username kao Tarik Sijerčić
        robot.clickOn("Delić Amra");
        robot.clickOn("#fldUsername");
        robot.press(ctrl).press(KeyCode.A).release(KeyCode.A).release(ctrl);
        robot.press(KeyCode.DELETE).release(KeyCode.DELETE);
        robot.write("tariks");

        // Sada pokrećemo provjeru
        robot.clickOn("Fejzić Rijad");
        robot.clickOn("#btnProvjeri");

        // Sada smo pozicionirani opet na Amru Delić
        robot.clickOn("#fldUsername");
        robot.press(ctrl).press(KeyCode.A).release(KeyCode.A).release(ctrl);
        robot.press(KeyCode.DELETE).release(KeyCode.DELETE);
        robot.write("tariks2");

        robot.clickOn("#btnProvjeri");

        // Sada više nema greške
        robot.lookup(".dialog-pane").tryQuery().isPresent();

        // Klik na dugme Ok
        DialogPane dialogPane = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        robot.clickOn(okButton);

        // Polje je validno
        TextField fldUsername = robot.lookup("#fldUsername").queryAs(TextField.class);
        assertTrue(KorisnikControllerTest.sadrziStil(fldUsername, "poljeIspravno"));
        assertFalse(KorisnikControllerTest.sadrziStil(fldUsername, "poljeNijeIspravno"));
    }

    @Test
    void popravkaDodajTest(FxRobot robot) {
        robot.clickOn("Delić Amra");
        robot.clickOn("#btnDodaj");
        robot.clickOn("#fldIme").write("Kanita");
        robot.clickOn("#fldPrezime").write("Đokić");
        robot.clickOn("#fldEmail").write("kdokic1@etf.unsa.ba");
        robot.clickOn("#fldUsername").write("tariks"); // duplikat!
        robot.clickOn("#fldPassword").write("kdokic1");

        robot.clickOn("Fejzić Rijad");

        // Provjera da li je Kanita dodana
        ObservableList<Korisnik> korisniks = model.getKorisnici();

        assertEquals(5, korisniks.size());
        assertEquals("Đokić", korisniks.get(4).getPrezime());
        assertEquals("tariks", korisniks.get(4).getUsername());

        robot.clickOn("#btnProvjeri");
        // Provjera da je Sijerčić Tarik trenutni korisnik

        TextField fldPrezime = robot.lookup("#fldPrezime").queryAs(TextField.class);
        assertEquals("Sijerčić", fldPrezime.getText());
        TextField fldIme = robot.lookup("#fldIme").queryAs(TextField.class);
        assertEquals("Tarik", fldIme.getText());
        TextField fldUsername = robot.lookup("#fldUsername").queryAs(TextField.class);
        assertEquals("tariks", fldUsername.getText());

        // Popravljamo Kanitu Đokić
        robot.clickOn("Đokić Kanita");
        robot.clickOn("#fldUsername");
        robot.press(ctrl).press(KeyCode.A).release(KeyCode.A).release(ctrl);
        robot.press(KeyCode.DELETE).release(KeyCode.DELETE);
        robot.write("kdokic");
        robot.clickOn("Fejzić Rijad");

        // Provjera da li je popravljen username
        assertEquals("kdokic", korisniks.get(4).getUsername());

        robot.clickOn("#btnProvjeri");

        // Sada više nema greške
        robot.lookup(".dialog-pane").tryQuery().isPresent();

        // Klik na dugme Ok
        DialogPane dialogPane = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        robot.clickOn(okButton);

        // Polje je validno
        assertTrue(KorisnikControllerTest.sadrziStil(fldUsername, "poljeIspravno"));
        assertFalse(KorisnikControllerTest.sadrziStil(fldUsername, "poljeNijeIspravno"));
    }

    @Test
    void visestrukiTest(FxRobot robot) {
        // Postavljamo svim korisnicima username "amrad"
        robot.clickOn("Ljubović Vedran");
        robot.clickOn("#fldUsername");
        robot.press(ctrl).press(KeyCode.A).release(KeyCode.A).release(ctrl);
        robot.press(KeyCode.DELETE).release(KeyCode.DELETE);
        robot.write("amrad");

        robot.clickOn("Sijerčić Tarik");
        robot.clickOn("#fldUsername");
        robot.press(ctrl).press(KeyCode.A).release(KeyCode.A).release(ctrl);
        robot.press(KeyCode.DELETE).release(KeyCode.DELETE);
        robot.write("amrad");

        robot.clickOn("Fejzić Rijad");
        robot.clickOn("#fldUsername");
        robot.press(ctrl).press(KeyCode.A).release(KeyCode.A).release(ctrl);
        robot.press(KeyCode.DELETE).release(KeyCode.DELETE);
        robot.write("amrad");

        robot.clickOn("#btnProvjeri");
        // Provjera treba selektovati korisnika Ljubović Vedran jer je prvi na listi

        TextField fldPrezime = robot.lookup("#fldPrezime").queryAs(TextField.class);
        assertEquals("Ljubović", fldPrezime.getText());
        TextField fldIme = robot.lookup("#fldIme").queryAs(TextField.class);
        assertEquals("Vedran", fldIme.getText());
        TextField fldUsername = robot.lookup("#fldUsername").queryAs(TextField.class);
        assertEquals("amrad", fldUsername.getText());

        assertFalse(KorisnikControllerTest.sadrziStil(fldUsername, "poljeIspravno"));
        assertTrue(KorisnikControllerTest.sadrziStil(fldUsername, "poljeNijeIspravno"));

        robot.clickOn("#fldUsername");
        robot.press(ctrl).press(KeyCode.A).release(KeyCode.A).release(ctrl);
        robot.press(KeyCode.DELETE).release(KeyCode.DELETE);
        robot.write("vedranlj");

        assertTrue(KorisnikControllerTest.sadrziStil(fldUsername, "poljeIspravno"));
        assertFalse(KorisnikControllerTest.sadrziStil(fldUsername, "poljeNijeIspravno"));
        robot.clickOn("Fejzić Rijad");

        robot.clickOn("#btnProvjeri");
        // Sljedeća je Delić Amra
        assertEquals("Delić", fldPrezime.getText());
        assertEquals("Amra", fldIme.getText());
        assertEquals("amrad", fldUsername.getText());

        assertFalse(KorisnikControllerTest.sadrziStil(fldUsername, "poljeIspravno"));
        assertTrue(KorisnikControllerTest.sadrziStil(fldUsername, "poljeNijeIspravno"));

        robot.clickOn("#fldUsername");
        robot.press(ctrl).press(KeyCode.A).release(KeyCode.A).release(ctrl);
        robot.press(KeyCode.DELETE).release(KeyCode.DELETE);
        robot.write("amrad2");

        assertTrue(KorisnikControllerTest.sadrziStil(fldUsername, "poljeIspravno"));
        assertFalse(KorisnikControllerTest.sadrziStil(fldUsername, "poljeNijeIspravno"));
        robot.clickOn("Fejzić Rijad");

        robot.clickOn("#btnProvjeri");
        assertEquals("Sijerčić", fldPrezime.getText());
        assertEquals("Tarik", fldIme.getText());
        assertEquals("amrad", fldUsername.getText());
    }

    @Test
    void radiNeRadiTest(FxRobot robot) {
        TextField fldUsername = robot.lookup("#fldUsername").queryAs(TextField.class);

        robot.clickOn("Sijerčić Tarik");
        robot.clickOn("#fldUsername");
        robot.press(ctrl).press(KeyCode.A).release(KeyCode.A).release(ctrl);
        robot.press(KeyCode.DELETE).release(KeyCode.DELETE);
        robot.write("username");

        robot.clickOn("#btnProvjeri");

        // U ovom trenutku su svi različiti
        robot.lookup(".dialog-pane").tryQuery().isPresent();

        // Klik na dugme Ok
        DialogPane dialogPane = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        robot.clickOn(okButton);

        // Polje je validno
        assertTrue(KorisnikControllerTest.sadrziStil(fldUsername, "poljeIspravno"));
        assertFalse(KorisnikControllerTest.sadrziStil(fldUsername, "poljeNijeIspravno"));

        robot.clickOn("Fejzić Rijad");
        robot.clickOn("#fldUsername");
        robot.press(ctrl).press(KeyCode.A).release(KeyCode.A).release(ctrl);
        robot.press(KeyCode.DELETE).release(KeyCode.DELETE);
        robot.write("username");

        robot.clickOn("#btnProvjeri");

        TextField fldPrezime = robot.lookup("#fldPrezime").queryAs(TextField.class);
        assertEquals("Sijerčić", fldPrezime.getText());
        TextField fldIme = robot.lookup("#fldIme").queryAs(TextField.class);
        assertEquals("Tarik", fldIme.getText());
        assertEquals("username", fldUsername.getText());

        assertFalse(KorisnikControllerTest.sadrziStil(fldUsername, "poljeIspravno"));
        assertTrue(KorisnikControllerTest.sadrziStil(fldUsername, "poljeNijeIspravno"));

        robot.clickOn("#fldUsername");
        robot.press(KeyCode.END).release(KeyCode.END);
        robot.press(KeyCode.LEFT).release(KeyCode.LEFT);
        robot.press(KeyCode.DELETE).release(KeyCode.DELETE); // Sada je "usernam"

        // Polje je validno
        assertTrue(KorisnikControllerTest.sadrziStil(fldUsername, "poljeIspravno"));
        assertFalse(KorisnikControllerTest.sadrziStil(fldUsername, "poljeNijeIspravno"));


        robot.clickOn("#btnProvjeri");

        // U ovom trenutku su svi različiti
        robot.lookup(".dialog-pane").tryQuery().isPresent();

        // Klik na dugme Ok
        dialogPane = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        robot.clickOn(okButton);

        // Polje je validno
        assertTrue(KorisnikControllerTest.sadrziStil(fldUsername, "poljeIspravno"));
        assertFalse(KorisnikControllerTest.sadrziStil(fldUsername, "poljeNijeIspravno"));
    }
}