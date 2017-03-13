package eu.telecomnancy.tncyiot;

import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
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
    final private Handler handler = new Handler();
    final private HashMap<String, MovingAverage> objAvg = new HashMap<>();
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

                            //Get SP & add listener to it
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(myTimerTaskContext());
                            String hourStartNotify = prefs.getString("hourStartNotify", "19:00");
                            String hourEndNotify = prefs.getString("hourEndNotify", "23:00");
                            String hourStartMail = prefs.getString("hourStartMail", "23:00");
                            String hourEndMail = prefs.getString("hourEndMail", "06:00");
                            //Easier to compare
                            final int intStartNotify = Integer.parseInt(hourStartNotify.split(":")[0]) * 100
                                    + Integer.parseInt(hourStartNotify.split(":")[1]);
                            final int intEndNotify = Integer.parseInt(hourEndNotify.split(":")[0]) * 100
                                    + Integer.parseInt(hourEndNotify.split(":")[1]);
                            final int intStartMail = Integer.parseInt(hourStartMail.split(":")[0]) * 100
                                    + Integer.parseInt(hourStartMail.split(":")[1]);
                            final int intEndMail = Integer.parseInt(hourEndMail.split(":")[0]) * 100
                                    + Integer.parseInt(hourEndMail.split(":")[1]);
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
                                    int hourOfDay  = calendar.get(Calendar.HOUR_OF_DAY)*100
                                            + calendar.get(Calendar.MINUTE);
                                    //If 23 > 6 && (hourOfDay < 6 OR hourofday > 23) or regular
                                    if ((intStartNotify > intEndNotify &&
                                            (hourOfDay < intEndNotify || hourOfDay > intStartNotify))
                                            || (hourOfDay > intStartNotify && hourOfDay < intEndNotify)){
                                        LightNotification.notify(myTimerTaskContext(),light.getMote(),simpleDateFormat.format(date));
                                    }
                                    if ((intStartMail > intEndMail &&
                                            (hourOfDay < intEndMail || hourOfDay > intStartMail))
                                            || (hourOfDay > intStartMail && hourOfDay < intEndMail)){
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
