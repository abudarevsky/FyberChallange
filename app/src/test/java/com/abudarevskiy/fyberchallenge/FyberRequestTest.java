package com.abudarevskiy.fyberchallenge;

import com.abudarevskiy.fyberchallenge.model.FyberRequest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FyberRequestTest {
    public String[] paramsBySpec = {"appid", "pub0", "uid", "timestamp", "os_version", "locale", "device_id"};

    @Test
    public void t1_makeRequestParams_asRequiedBySpec(){
        FyberRequest fr = new FyberRequest();

        try {
            Map params = fr.makeRequestParams(Constants.UUID, Constants.appId, Constants.apiKey, Constants.pub0, Constants.uid );
            Set defaultSet = new HashSet(Arrays.asList(paramsBySpec));

            assertThat(params.keySet().containsAll(defaultSet), is(true));
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void t2_makeRequestParams_HasHashKey(){
        FyberRequest fr = new FyberRequest();

        try {
            Map params = fr.makeRequestParams(Constants.UUID, Constants.appId, Constants.apiKey, Constants.pub0, Constants.uid );

            assertThat(params.containsKey("hashkey"), is(true));
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void t3_makeRequestParamsString_isSorted(){
        FyberRequest fr = new FyberRequest();

        try {
            String hashkey = "hashkey="+fr.makeRequestParams(Constants.UUID, Constants.appId, Constants.apiKey, Constants.pub0, Constants.uid ).get("hashkey");
            String params = fr.makeRequestParamsString(Constants.UUID, Constants.appId, Constants.apiKey, Constants.pub0, Constants.uid );
            String [] items = params.split("&");

            assertThat(params, Arrays.asList(items).indexOf(hashkey), is(items.length-1));

            String [] sortedItemsNoHashKey = Arrays.copyOfRange(items, 0, items.length-1);
            String [] itemsToSort = Arrays.copyOfRange(items, 0, items.length-1);

            assertThat(sortedItemsNoHashKey.equals(itemsToSort), is(false));

            Arrays.sort(itemsToSort, new Comparator<String>() {
                @Override
                public int compare(String s, String t1) {
                    String key = s.split("=")[0];
                    String t1_key = t1.split("=")[0];
                    return key.compareTo(t1_key);
                }
            });

            assertThat(Arrays.equals(sortedItemsNoHashKey, itemsToSort), is(true));
            assertThat(Arrays.equals(sortedItemsNoHashKey, items), is(false));
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }
}
