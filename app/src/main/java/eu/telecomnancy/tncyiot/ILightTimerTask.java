package eu.telecomnancy.tncyiot;

import eu.telecomnancy.tncyiot.Entity.LightRecords;

import android.content.Context;

/**
 * Created by kromer1u on 26/01/17.
 * interface allow us to inject method/data depending the contexte (tests, application)
 */
public interface ILightTimerTask {

    /**
     * the callback
     * @param lightsRecordsList
     */
    void myTimerTaskCallback(LightRecords lightsRecordsList);

    /**
     * application context
     *
     * @return
     */
    Context myTimerTaskContext();

    /**
     *
     * @return url to fetch
     */
    String myTimerTaskUrl();
}
