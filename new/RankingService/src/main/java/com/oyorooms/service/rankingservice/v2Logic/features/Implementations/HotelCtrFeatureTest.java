package com.oyorooms.service.rankingservice.v2Logic.features.Implementations;

import com.google.common.collect.ImmutableMap;
import com.oyorooms.service.rankingservice.v2Logic.entities.FeatureData;
import com.oyorooms.service.rankingservice.v2Logic.entities.HotelDetails;
import com.oyorooms.service.rankingservice.v2Logic.entities.RankingRequestData;
import com.oyorooms.service.rankingservice.v2Logic.entities.UserEventsConfig;
import com.oyorooms.service.rankingservice.v2Logic.enums.*;
import com.oyorooms.service.rankingservice.v2Logic.features.FeatureResult;
import com.oyorooms.service.rankingservice.v2Logic.services.userEvents.entities.UserEventResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static org.junit.Assert.assertTrue;

public class HotelCtrFeatureTest {

    @InjectMocks
    private HotelCtrFeature hotelCtrFeature;

    private static final String REQUEST_ID = "7d6e2cdc-7a47-432e-bc95-ed514076c243";

    private static final String[] NEW_HOTEL_IDS = {"3", "105", "107", "110"};

    private static final String EVENT_BOOKING = "HOTEL_BOOKING";

    private static final String EVENT_LISTING = "HOTEL_LISTING";

    private static final String EVENT_DETAILS = "HOTEL_DETAILS";

    private static final List<Double> COUNT_BOOKING1 = Arrays.asList(2.0, 3.0);

    private static final List<Double> COUNT_LISTING1 = Arrays.asList(2.0, 3.0, 15.0, 30.0);

    private static final List<Double> COUNT_BOOKING2 = Arrays.asList(2.0, 3.0, 5.0);

    private static final List<Double> COUNT_LISTING2 = Arrays.asList(2.0, 3.0, 15.0, 30.0);

    private static final List<Double> COUNT_LISTING3 = null;

    private static final List<Double> COUNT_LISTING4 = Arrays.asList(0.0);

    private static final List<Double> COUNT_DETAILS1 = Arrays.asList(1.0, 2.0);

    private static final List<Double> COUNT_DETAILS2 = Arrays.asList(1.0, 2.0, 5.0, 8.0);

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void TestGetFeatureResultWhenFeatureDataNull(){
        FeatureResult expected = new FeatureResult(Feature.HOTEL_CTR, getrawfeaturescores(), getrawfeaturescores());
        FeatureResult actual = hotelCtrFeature.getFeatureResult(getRankingRequestData(), getServiceAggregatedDataMap(getUserEventResponse()), null);
        assertTrue(expected.getHotelWiseRawFeatureScores().equals(actual.getHotelWiseRawFeatureScores()));
    }

    @Test
    public void TestGetFeatureResultWhenServiceAggDataMapNull(){
        FeatureResult expected = new FeatureResult(Feature.HOTEL_CTR, new HashMap<>(), new HashMap<>());
        FeatureResult actual = hotelCtrFeature.getFeatureResult(getRankingRequestData(), null, null);
        assertTrue(expected.getHotelWiseRawFeatureScores().equals(actual.getHotelWiseRawFeatureScores()));
    }

    @Test
    public void TestGetFeatureResultWhenUserEventResponseIsNull(){
        FeatureResult expected = new FeatureResult(Feature.HOTEL_CTR, new HashMap<>(), new HashMap<>());
        FeatureResult actual = hotelCtrFeature.getFeatureResult(getRankingRequestData(), getServiceAggregatedDataMap(null), null);
        assertTrue(expected.getHotelWiseRawFeatureScores().equals(actual.getHotelWiseRawFeatureScores()));
    }

    @Test
    public void TestGetFeatureResultWhenCustomWeightsApply(){
        FeatureResult expected = new FeatureResult(Feature.HOTEL_CTR, getfeaturescores(), getfeaturescores());
        FeatureResult actual = hotelCtrFeature.getFeatureResult(getRankingRequestData(), getServiceAggregatedDataMap(getUserEventResponse()), getFeatureData());
        assertTrue(expected.getHotelWiseFeatureScores().equals(actual.getHotelWiseFeatureScores()));
    }

    private FeatureData getFeatureData(){
        Map<FeatureConfigurationEnum, Object> configurationDataMap = ImmutableMap.<FeatureConfigurationEnum, Object>builder().put(FeatureConfigurationEnum.USER_EVENTS_CONFIG, new UserEventsConfig(Arrays.asList(1.0, 1.0, 2.0, 2.0, 3.0, 3.0), TransformationStrategyType.CUSTOM_WEIGHTS))
                .build();
        return new FeatureData.Builder().backFillingStrategy(BackFillingStrategy.MEAN).featureConfigurationData(configurationDataMap).weight(1.0).build();
    }

    private RankingRequestData getRankingRequestData(){
        HotelDetails[] hotelDetails = new HotelDetails[NEW_HOTEL_IDS.length];
        for(int i=0; i<hotelDetails.length; i++){
            hotelDetails[i] = new HotelDetails();
            hotelDetails[i].setHotelId(NEW_HOTEL_IDS[i]);
        }

        return new RankingRequestData.Builder().requestId(REQUEST_ID).hotelDetailsList(Arrays.asList(hotelDetails)).build();
    }

    private Map<String, Double> getrawfeaturescores() {
        Map<String, Double> featurerawscores = new HashMap<>();
        featurerawscores.put(NEW_HOTEL_IDS[0], sumListElements(COUNT_DETAILS1) / sumListElements(COUNT_LISTING1));
        featurerawscores.put(NEW_HOTEL_IDS[1], sumListElements(COUNT_DETAILS2) / sumListElements(COUNT_LISTING2));
        featurerawscores.put(NEW_HOTEL_IDS[3], 50.0);
        return featurerawscores;
    }

    private Map<String, Double> getfeaturescores(){
        Map<String, Double> featurescores = new HashMap<>();
        featurescores.put(NEW_HOTEL_IDS[0], 0.09722222222222221);
        featurescores.put(NEW_HOTEL_IDS[1], 0.19722222222222222);
        featurescores.put(NEW_HOTEL_IDS[3], 50.0);
        return featurescores;
    }

    private double sumListElements(List<Double> list){
        double sum = 0.0;
        for(Double val : list){
            sum += val;
        }
        return sum;
    }

    private Map<ServiceEnums, Future<Object>> getServiceAggregatedDataMap(Object object) {
        Map<ServiceEnums, Future<Object>> dataMap = new HashMap<>();
        dataMap.put(ServiceEnums.USER_EVENTS_SERVICE, CompletableFuture.completedFuture(object));
        return dataMap;
    }

    private UserEventResponse getUserEventResponse() {
        UserEventResponse userEventResponse = new UserEventResponse();
        UserEventResponse.ResponseObject responseObject = new UserEventResponse.ResponseObject();
        responseObject.setRequestId(REQUEST_ID);


        UserEventResponse.ResponseObject.Event event1 = new UserEventResponse.ResponseObject.Event();
        event1.setEventType(EVENT_BOOKING);
        Map<String, List<Double>> dateWiseHotelEventCount1 = new HashMap<>();
        dateWiseHotelEventCount1.put(NEW_HOTEL_IDS[0], COUNT_BOOKING1);
        dateWiseHotelEventCount1.put(NEW_HOTEL_IDS[1], COUNT_BOOKING2);
        event1.setDateWiseHotelEventCount(dateWiseHotelEventCount1);

        UserEventResponse.ResponseObject.Event event2 = new UserEventResponse.ResponseObject.Event();
        event2.setEventType(EVENT_LISTING);
        Map<String, List<Double>> dateWiseHotelEventCount2 = new HashMap<>();
        dateWiseHotelEventCount2.put(NEW_HOTEL_IDS[0], COUNT_LISTING1);
        dateWiseHotelEventCount2.put(NEW_HOTEL_IDS[1], COUNT_LISTING2);
        dateWiseHotelEventCount2.put(NEW_HOTEL_IDS[2], COUNT_LISTING3);
        dateWiseHotelEventCount2.put(NEW_HOTEL_IDS[3], COUNT_LISTING4);
        event2.setDateWiseHotelEventCount(dateWiseHotelEventCount2);

        UserEventResponse.ResponseObject.Event event3 = new UserEventResponse.ResponseObject.Event();
        event3.setEventType(EVENT_DETAILS);
        Map<String, List<Double>> dateWiseHotelEventCount3 = new HashMap<>();
        dateWiseHotelEventCount3.put(NEW_HOTEL_IDS[0], COUNT_DETAILS1);
        dateWiseHotelEventCount3.put(NEW_HOTEL_IDS[1], COUNT_DETAILS2);
        event3.setDateWiseHotelEventCount(dateWiseHotelEventCount3);

        List<UserEventResponse.ResponseObject.Event> eventList = Arrays.asList(event1, event2, event3);
        responseObject.setTotalEventCount(eventList.size());
        responseObject.setEvents(eventList);
        userEventResponse.setResponseObject(responseObject);
        return userEventResponse;
    }
}
