package eu.telecomnancy.tncyiot;

import android.app.Application;
import android.content.Context;
import android.test.ApplicationTestCase;

import com.github.javafaker.Faker;

import org.junit.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import eu.telecomnancy.tncyiot.Entity.Light;
import eu.telecomnancy.tncyiot.Entity.LightRecords;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    @Test
    public void testDetectPeakInTime() {
        //https://fr.wikipedia.org/wiki/Moyenne_glissante

        //generates dummy data
        Faker faker = new Faker();
        LightRecords list = new LightRecords(new LightRecords.ChangeListener() {
            @Override
            public void onChange(Light light) {
                System.out.println("OnChange called !");
            }
        });
        //No light, mote1 with lumen between 10-20
        Light l = null;
        for (int i=0;i< 10;i++){
            String label = "light"+faker.number().randomDigit();
            l = new Light(faker.date().between(new Date(2017,1,1), new Date(2017,1,24)).getTime(),label,faker.number().randomDouble(2,10,20),"mote1");
            list.add(l);
        }
        //Light is off :
        assertTrue("SwitchOff",!l.isSwitchOn());
        //sort light data
        Collections.sort(list, new Comparator<Light>() {
            @Override
            public int compare(Light l1, Light l2)
            {
                return  l1.getTimestamp() > l2.getTimestamp() ? +1 : l1.getTimestamp() < l2.getTimestamp() ? -1 : 0;
            }
        });
        //light switch on, mote1 with lumen between 50-70
        String label = "light1";
        l = new Light(new Date().getTime(),label,faker.number().randomDouble(2,50,50+20),"mote1" );
        list.add(l);
        //Light is on :
        assertTrue("SwitchOn", l.isSwitchOn());
    }


    @Test
    public void testRESTLightLast(){
        LightTimerTask timerTask = new LightTimerTask() {
            @Override
            public void myTimerTaskCallback(LightRecords lightsRecordsList) {
                assertTrue("/{experiment_id}/{labels}/(last|first) contains 3 results",lightsRecordsList.size()==3);
            }

            @Override
            public Context myTimerTaskContext() {
                return getContext();
            }

            @Override
            public String myTimerTaskUrl() {
                return "http://iotlab.telecomnancy.eu/rest/data/1/light1/last";
            }
        };
        timerTask.run();

    }

    @Test
    public void testRESTLight5results(){
        LightTimerTask timerTask = new LightTimerTask() {
            int expected = 5;
            @Override
            public void myTimerTaskCallback(LightRecords lightsRecordsList) {
                assertTrue("/{experiment_id}/{labels}/{nb} contains "+expected+"*3 results",lightsRecordsList.size()==expected*3);
            }

            @Override
            public Context myTimerTaskContext() {
                return getContext();
            }

            @Override
            public String myTimerTaskUrl() {
                return "http://iotlab.telecomnancy.eu/rest/data/1/light1/"+expected;
            }
        };
        timerTask.run();

    }
}