package eu.telecomnancy.tncyiot;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimerTask;

import eu.telecomnancy.tncyiot.Entity.Light;
import eu.telecomnancy.tncyiot.Entity.LightRecords;
import eu.telecomnancy.tncyiot.Entity.RestResult;
import eu.telecomnancy.tncyiot.UI.LightNotification;
import eu.telecomnancy.tncyiot.Util.MailManager;
import eu.telecomnancy.tncyiot.Util.MovingAverage;
import eu.telecomnancy.tncyiot.Util.RestTask;

/**
 * Created by kromer1u on 26/01/17.
 * Main task which fetch the api and convert result to a  @class {@link LightRecords}
 * use a callback to publishresult
 */
public abstract class LightTimerTask extends TimerTask  implements ILightTimerTask {
    final Handler handler = new Handler();
    final HashMap<String, MovingAverage> objAvg = new HashMap<>();
    @Override
    public void run() {
        //use a handler to run a toast that shows the current timestamp
        handler.post(new Runnable() {
            public void run() {

                    RestTask task = new RestTask(myTimerTaskContext(),new RestTask.TaskListener() {
                        @Override
                        public void onFinished(String jsonresult) {
                            // Do Something after the task has finished
                            Log.i("MainService", "RESULT = " + jsonresult);


                            //list for save lights with the listener in order to detect changes
                            LightRecords lightsRecordsList = new LightRecords(new LightRecords.ChangeListener() {
                                @Override
                                public void onChange(Light light) {
                                    Date date = new Date(light.getTimestamp());
                                    // S is the millisecond
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy' 'HH:mm:ss:S");

                                    Calendar calendar = Calendar.getInstance();
                                    boolean isWeekEnd = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                                            calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
                                    int hourOfDay  = calendar.get(Calendar.HOUR_OF_DAY);
                                    if (hourOfDay >= 19 && hourOfDay<= 23 && !isWeekEnd)
                                        LightNotification.notify(myTimerTaskContext(),light.getMote(),simpleDateFormat.format(date));
                                    else if ((isWeekEnd && (hourOfDay >= 19 && hourOfDay<= 23)) ||
                                                (!isWeekEnd && (hourOfDay <= 6 || hourOfDay>= 23))){
                                        MailManager.sendMailSilent(myTimerTaskContext(), light);
                                    }
                                }
                            }, objAvg);

                            final Gson gson = new GsonBuilder().create();
                            Type castType = new TypeToken<RestResult<Light>>(){}.getType();

                            RestResult<Light> restresult = gson.fromJson(jsonresult, castType);
                            //todo:check if data not null
                            for(Light l : restresult.data){
                                lightsRecordsList.add(l);
                            }
                            myTimerTaskCallback(lightsRecordsList);
                            //publishResults(lightsRecordsDataMap);

                        }
                    });
                try {
                    task.execute(new URL(myTimerTaskUrl()));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

            }
        });
    }


}
