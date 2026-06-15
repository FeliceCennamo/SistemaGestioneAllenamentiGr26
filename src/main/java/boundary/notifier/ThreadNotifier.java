package boundary.notifier;

public class ThreadNotifier implements INotifier{

    private static ThreadNotifier instance = null;

    private ThreadNotifier() {
    }

    public static ThreadNotifier getInstance() {
        if (instance == null) {
            instance = new ThreadNotifier();
        }
        return instance;
    }
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
