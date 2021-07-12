package ba.unsa.etf.rs.t5;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
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
public class IspitZadatak1Test {
    KorisniciModel model;
    KeyCode ctrl;

    @Start
    public void start (Stage stage) throws Exception {
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
    void izmjenaTest(FxRobot robot) {
        TextField fldWebStranica = robot.lookup("#fldWebStranica").queryAs(TextField.class);

        robot.clickOn("Sijerčić Tarik");
        robot.clickOn("#fldWebStranica");
        robot.press(ctrl).press(KeyCode.A).release(KeyCode.A).release(ctrl);
        robot.press(KeyCode.DELETE).release(KeyCode.DELETE);
        robot.write("http://www.etf.unsa.ba");

        robot.clickOn("Fejzić Rijad");
        // Default vrijednost treba biti prazan string
        assertEquals("", fldWebStranica.getText());

        robot.clickOn("Sijerčić Tarik");
        assertEquals("http://www.etf.unsa.ba", fldWebStranica.getText());

        robot.clickOn("#fldWebStranica");
        robot.press(ctrl).press(KeyCode.A).release(KeyCode.A).release(ctrl);
        robot.press(KeyCode.DELETE).release(KeyCode.DELETE);
        robot.write("https://www.uvoduprogramiranje.ba");

        robot.clickOn("Delić Amra");
        assertEquals("", fldWebStranica.getText());

        robot.clickOn("Sijerčić Tarik");
        assertEquals("https://www.uvoduprogramiranje.ba", fldWebStranica.getText());
    }

    @Test
    void izmjenaModelTest(FxRobot robot) {
        TextField fldWebStranica = robot.lookup("#fldWebStranica").queryAs(TextField.class);

        robot.clickOn("Sijerčić Tarik");
        model.getTrenutniKorisnik().setWebStranica("http://www.etf.unsa.ba");

        robot.clickOn("Fejzić Rijad");
        assertEquals("", fldWebStranica.getText());

        robot.clickOn("Sijerčić Tarik");
        assertEquals("http://www.etf.unsa.ba", fldWebStranica.getText());
    }


    @Test
    void izmjenaModel2Test(FxRobot robot) {
        robot.clickOn("Sijerčić Tarik");
        model.getTrenutniKorisnik().setWebStranica("http://www.etf.unsa.ba");

        robot.clickOn("Fejzić Rijad");

        ObservableList<Korisnik> korisniks = model.getKorisnici();

        assertEquals("http://www.etf.unsa.ba", korisniks.get(2).getWebStranica());

        korisniks.get(2).setWebStranica("https://www.uvoduprogramiranje.ba");

        robot.clickOn("Delić Amra");
        robot.clickOn("Sijerčić Tarik");
        TextField fldWebStranica = robot.lookup("#fldWebStranica").queryAs(TextField.class);
        assertEquals("https://www.uvoduprogramiranje.ba", fldWebStranica.getText());
    }

    @Test
    void dodavanjeTest(FxRobot robot) {
        TextField fldWebStranica = robot.lookup("#fldWebStranica").queryAs(TextField.class);

        robot.clickOn("Sijerčić Tarik");
        robot.clickOn("#btnDodaj");

        robot.clickOn("#fldIme").write("Kanita");
        robot.clickOn("#fldPrezime").write("Đokić");
        robot.clickOn("#fldEmail").write("kdokic1@etf.unsa.ba");
        robot.clickOn("#fldUsername").write("kanitad");
        robot.clickOn("#fldPassword").write("kanitad");
        robot.clickOn("#fldWebStranica");
        robot.press(ctrl).press(KeyCode.A).release(KeyCode.A).release(ctrl);
        robot.press(KeyCode.DELETE).release(KeyCode.DELETE);
        robot.write("https://www.google.com");

        robot.clickOn("Fejzić Rijad");
        // Default vrijednost treba biti prazan string
        assertEquals("", fldWebStranica.getText());

        robot.clickOn("Đokić Kanita");
        assertEquals("https://www.google.com", fldWebStranica.getText());

        robot.clickOn("#fldWebStranica");
        robot.press(ctrl).press(KeyCode.A).release(KeyCode.A).release(ctrl);
        robot.press(KeyCode.DELETE).release(KeyCode.DELETE);
        robot.write("http://www.etf.unsa.ba");

        robot.clickOn("Delić Amra");
        assertEquals("", fldWebStranica.getText());
        robot.clickOn("Đokić Kanita");
        assertEquals("http://www.etf.unsa.ba", fldWebStranica.getText());
    }

    @Test
    void dodavanjeModelTest(FxRobot robot) {
        robot.clickOn("Sijerčić Tarik");
        robot.clickOn("#btnDodaj");

        robot.clickOn("#fldIme").write("Kanita");
        robot.clickOn("#fldPrezime").write("Đokić");
        robot.clickOn("#fldEmail").write("kdokic1@etf.unsa.ba");
        robot.clickOn("#fldUsername").write("kanitad");
        robot.clickOn("#fldPassword").write("kanitad");
        model.getTrenutniKorisnik().setWebStranica("https://www.uvoduprogramiranje.ba");

        robot.clickOn("Fejzić Rijad");
        robot.clickOn("Đokić Kanita");

        ObservableList<Korisnik> korisniks = model.getKorisnici();

        assertEquals(5, korisniks.size());
        assertEquals("https://www.uvoduprogramiranje.ba", korisniks.get(4).getWebStranica());
        model.getTrenutniKorisnik().setWebStranica("https://www.google.com");

        robot.clickOn("Delić Amra");
        robot.clickOn("Đokić Kanita");

        assertEquals("https://www.google.com", korisniks.get(4).getWebStranica());
    }

    @Test
    void validacijaModelTest(FxRobot robot) {
        TextField fldWebStranica = robot.lookup("#fldWebStranica").queryAs(TextField.class);

        // Postavljamo Tariku nevalidnu web stranicu
        ObservableList<Korisnik> korisniks = model.getKorisnici();
        korisniks.get(2).setWebStranica("asdfasdf");

        robot.clickOn("Sijerčić Tarik");

        assertFalse(KorisnikControllerTest.sadrziStil(fldWebStranica, "poljeIspravno"));
        assertTrue(KorisnikControllerTest.sadrziStil(fldWebStranica, "poljeNijeIspravno"));

        robot.clickOn("Delić Amra");
        korisniks.get(2).setWebStranica("http://c2.etf.unsa.ba");

        robot.clickOn("Sijerčić Tarik");

        assertTrue(KorisnikControllerTest.sadrziStil(fldWebStranica, "poljeIspravno"));
        assertFalse(KorisnikControllerTest.sadrziStil(fldWebStranica, "poljeNijeIspravno"));
    }

    @Test
    void validacijaTest(FxRobot robot) {
        TextField fldWebStranica = robot.lookup("#fldWebStranica").queryAs(TextField.class);

        robot.clickOn("Fejzić Rijad");
        robot.clickOn("#fldWebStranica");
        robot.press(ctrl).press(KeyCode.A).release(KeyCode.A).release(ctrl);
        robot.press(KeyCode.DELETE).release(KeyCode.DELETE);
        // Nije ispravno, nedostaje dvotačka i dio iza
        robot.write("http");

        assertFalse(KorisnikControllerTest.sadrziStil(fldWebStranica, "poljeIspravno"));
        assertTrue(KorisnikControllerTest.sadrziStil(fldWebStranica, "poljeNijeIspravno"));

        robot.press(ctrl).press(KeyCode.A).release(KeyCode.A).release(ctrl);
        robot.press(KeyCode.DELETE).release(KeyCode.DELETE);
        // Ispravno
        robot.write("https://www.test.ba");

        assertTrue(KorisnikControllerTest.sadrziStil(fldWebStranica, "poljeIspravno"));
        assertFalse(KorisnikControllerTest.sadrziStil(fldWebStranica, "poljeNijeIspravno"));

        robot.press(ctrl).press(KeyCode.A).release(KeyCode.A).release(ctrl);
        robot.press(KeyCode.DELETE).release(KeyCode.DELETE);
        // Nije ispravno, viška slovo t
        robot.write("htttp://www.test.ba");

        assertFalse(KorisnikControllerTest.sadrziStil(fldWebStranica, "poljeIspravno"));
        assertTrue(KorisnikControllerTest.sadrziStil(fldWebStranica, "poljeNijeIspravno"));

        robot.press(ctrl).press(KeyCode.A).release(KeyCode.A).release(ctrl);
        robot.press(KeyCode.DELETE).release(KeyCode.DELETE);
        // Nije ispravno, fali dvotačka
        robot.write("http//www.test.ba");

        assertFalse(KorisnikControllerTest.sadrziStil(fldWebStranica, "poljeIspravno"));
        assertTrue(KorisnikControllerTest.sadrziStil(fldWebStranica, "poljeNijeIspravno"));

        robot.press(ctrl).press(KeyCode.A).release(KeyCode.A).release(ctrl);
        robot.press(KeyCode.DELETE).release(KeyCode.DELETE);
        // Ispravno
        robot.write("http://www.test.ba");

        assertTrue(KorisnikControllerTest.sadrziStil(fldWebStranica, "poljeIspravno"));
        assertFalse(KorisnikControllerTest.sadrziStil(fldWebStranica, "poljeNijeIspravno"));
    }
}
