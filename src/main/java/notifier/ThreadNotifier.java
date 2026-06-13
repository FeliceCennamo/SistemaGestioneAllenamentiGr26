package notifier;

public class ThreadNotifier implements INotifier{
    @Override
    public void sendMailComplete(String destinatario){
        Thread t = new Thread(() -> {
            Notifier.getInstance().sendMailComplete(destinatario);
        });
        t.start();
    }
    @Override
    public void sendMailCreate(String destinatario){
        Thread t = new Thread(() -> {
            Notifier.getInstance().sendMailCreate(destinatario);
        });
        t.start();
    }
}
