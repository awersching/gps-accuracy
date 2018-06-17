package awersching.gammelgps.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import io.reactivex.Observable;

public class GPS {

    protected static final String START = "START";
    protected static final String STOP = "STOP";
    protected static final String SAVE = "SAVE";

    protected static final String BROADCAST = "BROADCAST";
    protected static final String DATA = "DATA";

    private Context context;
    private BroadcastReceiver receiver;

    public GPS(Context context) {
        this.context = context;
    }

    public Observable<Data> start() {
        Intent intent = new Intent(context, GPSService.class)
                .setAction(START);
        context.startService(intent);

        return Observable.create(subscriber -> {
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    subscriber.onNext((Data) intent.getExtras().get(DATA));
                }
            };
            context.registerReceiver(receiver, new IntentFilter(BROADCAST));
        });
    }

    public void stop(boolean save) {
        Intent intent = new Intent(context, GPSService.class)
                .setAction(STOP)
                .putExtra(SAVE, save);
        context.startService(intent);
    }

    public void pause() {
        context.unregisterReceiver(receiver);
    }
}
