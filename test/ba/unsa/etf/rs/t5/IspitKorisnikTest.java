package ba.unsa.etf.rs.t5;

import javafx.beans.property.SimpleStringProperty;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Test klase Korisnik za Zadatak 3
// Zakomentarisan dok ne dodate odgovarajući atribut
public class IspitKorisnikTest {
    @Test
    void testSetterGetter() {
        Korisnik k = new Korisnik("ime", "prezime", "email", "username", "password");
        k.setWebStranica("http://www.etf.unsa.ba");
        assertEquals("http://www.etf.unsa.ba", k.getWebStranica());
    }
    @Test
    void testProperty() {
        Korisnik k = new Korisnik("ime", "prezime", "email", "username", "password");
        SimpleStringProperty simpleStringProperty = k.webStranicaProperty();
        // Default treba biti prazan string
        assertEquals("", simpleStringProperty.get());
    }

}
