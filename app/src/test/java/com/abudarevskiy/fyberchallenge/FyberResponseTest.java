package com.abudarevskiy.fyberchallenge;

import com.abudarevskiy.fyberchallenge.model.FyberRequest;
import com.abudarevskiy.fyberchallenge.model.FyberResponse;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.HashMap;
import java.util.Map;

import volley.Config_URL;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FyberResponseTest {

    public final static String HEADER="X-Sponsorpay-Response-Signature";
    public static final String JSON_CODE_OK_COUNT_1_NO_OFFERS = "{code:'OK', message:'Ok', count:1, offers:[]}";

    @Test
    public void t1_failed_NoHeader(){
        FyberRequest fr = new FyberRequest();
        Map params = fr.makeRequestParams(Constants.UUID, Constants.appId, Constants.apiKey, Constants.pub0, Constants.uid );

        try {
            FyberResponse r = new FyberResponse(JSON_CODE_OK_COUNT_1_NO_OFFERS,new HashMap());
            assertThat(r.checkReponseMatchWithRequest(fr), is(false));
        } catch (FyberResponse.SponsorPayBadResponseException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void t2_passed_WithHeader(){
        FyberRequest fr = new FyberRequest();
        Map headers = new HashMap();
        String respText = JSON_CODE_OK_COUNT_1_NO_OFFERS;

        StringBuilder builder = new StringBuilder(respText).append(Constants.apiKey);
        String sha1FromString = Config_URL.getSha1Hex(builder.toString());

        headers.put(HEADER,sha1FromString);

        Map params = fr.makeRequestParams(Constants.UUID, Constants.appId, Constants.apiKey, Constants.pub0, Constants.uid );

        try {
            FyberResponse r = new FyberResponse(respText,headers);
            assertThat(r.checkReponseMatchWithRequest(fr), is(true));
        } catch (FyberResponse.SponsorPayBadResponseException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void t3_failed_HasCount_but_getOffers(){
        FyberRequest fr = new FyberRequest();
        Map headers = new HashMap();
        headers.put(HEADER,"abc");

        Map params = fr.makeRequestParams(Constants.UUID, Constants.appId, Constants.apiKey, Constants.pub0, Constants.uid );
        FyberResponse r=null;
        try {
            r = new FyberResponse(JSON_CODE_OK_COUNT_1_NO_OFFERS,headers);
        } catch (FyberResponse.SponsorPayBadResponseException e) {
            fail();
        }

        try {
            r.getOffers();
        } catch (FyberResponse.SponsorPayBadResponseException e) {
            fail();
        } catch (FyberResponse.SponsorPayNoOffersException e) {
            assertTrue(true);
        }
    }


}
