package entity;

public interface INotifier {
    void sendMailComplete(String destinatario);
    void sendMailCreate(String destinatario);
}
