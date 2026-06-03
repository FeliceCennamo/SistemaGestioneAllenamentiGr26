package Notifier;

public interface INotifier {
    void sendMailComplete(String destinatario);
    void sendMailCreate(String destinatario);
}
