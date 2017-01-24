package eu.telecomnancy.tncyiot;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.github.javafaker.Faker;

import org.junit.Test;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.telecomnancy.tncyiot.Entity.Light;
import eu.telecomnancy.tncyiot.Util.MovingAverage;

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
    public void testDetecPeakInTime() {
        //https://fr.wikipedia.org/wiki/Moyenne_glissante

        //generates dummy data
        Faker faker = new Faker();
        HashMap<String,List<Light>> map = new HashMap<>();
        for (int i=0;i< 50;i++){
            String label = "light"+faker.number().randomDigit();
            Light l = new Light(faker.date().between(new Date(2017,1,1), new Date(2017,1,24)).getTime(),label,faker.number().randomDouble(2,2,20),faker.number().numberBetween(80,200)+"" );
            if (! map.containsKey(label)){
                map.put(label,new ArrayList<Light>());
            }
            map.get(label).add(l);
        }
        for(Map.Entry<String,List<Light>> entry : map.entrySet()) {
            List<Light> lightList = entry.getValue();

            Collections.sort(lightList, new Comparator<Light>() {
                @Override
                public int compare(Light l1, Light l2)
                {
                    return  l1.getTimestamp() > l2.getTimestamp() ? +1 : l1.getTimestamp() < l2.getTimestamp() ? -1 : 0;
                }
            });

            List<Double> testData = new ArrayList<>();
            for (Light l :  lightList) {
                testData.add(l.getValue());
            }


            int[] windowSizes = {5};
            double previousMovingAverage = Double.MIN_VALUE;
            for (int windSize : windowSizes) {
                MovingAverage ma = new MovingAverage(windSize);
                for (double x : testData) {
                    ma.newNum(x);
                    if (previousMovingAverage!=Double.MIN_VALUE){
                        if (previousMovingAverage*1.2 < ma.getAvg() || previousMovingAverage*0.8 < ma.getAvg() ){
                            Log.e("TEST","LUMIEEEERE");
                        }
                    }
                    previousMovingAverage = ma.getAvg();
                    Log.d("TEST","Next number = " + x + ", SMA = " + ma.getAvg());
                }
            }

        }




        assertThat(true, is(true));

    }
}