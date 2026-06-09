package database;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JPATest {
    @Test
    public void testChiudiEMF(){
        EntityManager em = JpaUtil.getInstance().getEntityManager();
        assertNotNull(em);
        JpaUtil.getInstance().chiudi();
        assertThrows(RuntimeException.class, () -> {
            em.getMetamodel(); //qualsiasi metodo per dimostrare che non è più utilizzabile
        });
    }
}
