package eseguibile;
import controller.Controller;
import boundary.FormListaSessioni;

public class MainEseguibile {

    public void main(String[] args){

        Controller c_session = Controller.getInstance();
        c_session.setIdUtenteAutenticato(150L);

        FormListaSessioni form_base = new FormListaSessioni();
        form_base.setup();

    }

}
