package com.oyorooms.service.rankingservice.v2logic.services;

import com.google.common.collect.Lists;
import com.netflix.hystrix.contrib.javanica.command.AsyncResult;
import com.oyorooms.service.rankingservice.entities.Coordinates;
import com.oyorooms.service.rankingservice.entities.Platform;
import com.oyorooms.service.rankingservice.testUtils.TestCommonUtil;
import com.oyorooms.service.rankingservice.testUtils.V2TestCommonUtil;
import com.oyorooms.service.rankingservice.utils.ThreadLocalUtil;
import com.oyorooms.service.rankingservice.v1Logic.enums.API;
import com.oyorooms.service.rankingservice.v2Logic.entities.HotelDetails;
import com.oyorooms.service.rankingservice.v2Logic.entities.RankingRequestData;
import com.oyorooms.service.rankingservice.v2Logic.entities.SortBy;
import com.oyorooms.service.rankingservice.v2Logic.enums.SearchTypeEnum;
import com.oyorooms.service.rankingservice.v2Logic.enums.Topic;
import com.oyorooms.service.rankingservice.v2Logic.services.configservice.commonconfig.RankingConfigurationData;
import com.oyorooms.service.rankingservice.v2Logic.services.hotelsegmentation.HotelSegmentationHelper;
import com.oyorooms.service.rankingservice.v2Logic.services.hotelsegmentation.HotelSegmentationService;
import com.oyorooms.service.rankingservice.v2Logic.services.hotelsegmentation.entities.HotelSegmentationServiceResponse;
import edu.emory.mathcs.backport.java.util.Arrays;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.concurrent.Future;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)

public class HotelSegmentationServiceTest {

    @InjectMocks
    private HotelSegmentationService hotelSegmentationService;

    @Mock
    private HotelSegmentationHelper hotelSegmentationHelper;

    @Mock
    private RankingConfigurationData rankingConfigurationData = new RankingConfigurationData();

    @Mock
    private Map<Topic, Object > configurationMap = new HashMap<>();

    @Mock
    private RestTemplate restTemplate;

    private String applicableSegments;


    private static final String PLACE_ID = "oyo-locality-17719172";

    @BeforeClass
    public static void setUp() {
        ThreadLocalUtil.setTokenInThread(null, API.V2_RANK_HOTELS);
    }


    @Test
    public void TestGetDataWhenHotelIdsNull() {

        RankingRequestData rankingRequestData = new RankingRequestData.Builder()
                .hotelDetailsList(new ArrayList<>()).build();

        Set<String> applicableSegments = new HashSet<>();
        applicableSegments.addAll(new ArrayList<>(Arrays.asList(new String[]{"a", "b", "c", "d"})));

        when(configurationMap.get(Topic.COMMON_CONFIG)).thenReturn(rankingConfigurationData);
        when(rankingConfigurationData.getSegments()).thenReturn(applicableSegments);

        Future objectFuture = hotelSegmentationService.getDataFromService(rankingRequestData, ThreadLocalUtil.getToken());
        AsyncResult asyncResult = (AsyncResult) objectFuture;
        List<Future<Map<String, HotelSegmentationServiceResponse>>> actualResponse = (List<Future<Map<String, HotelSegmentationServiceResponse>>>) asyncResult.invoke();
        assertEquals(actualResponse.size(), 0);

    }

    @Test
    public void TestGetDataWhenSegmentsNull(){

        List<HotelDetails> hotelDetailsList = getHotelDetailsList();

        RankingRequestData rankingRequestData = new RankingRequestData.Builder()
                .hotelDetailsList(hotelDetailsList)
                .sortBy(java.util.Arrays.asList(new SortBy())).placeId(PLACE_ID)
                .checkin(V2TestCommonUtil.getCheckin())
                .checkout(V2TestCommonUtil.getCheckout())
                .userId(V2TestCommonUtil.getUserId())
                .cityIdSearched(null)
                .cityId(null)
                .countryId(null)
                .userLocation(new Coordinates(28.410316, 77.048979))
                .cityCoordinates(new Coordinates(28.410316, 77.048979))
                .searchedLocation(new Coordinates(28.410316, 77.048979))
                .searchTypeEnum(SearchTypeEnum.CITY_SEARCH)
                .platform(Platform.analytics)
                .roomConfig(new Integer[] {1, 0, 0}).build();

        when(configurationMap.get(Topic.COMMON_CONFIG)).thenReturn(rankingConfigurationData);
        when(rankingConfigurationData.getSegments()).thenReturn(new HashSet<>());

        Future objectFuture = hotelSegmentationService.getDataFromService(rankingRequestData, ThreadLocalUtil.getToken());
        AsyncResult asyncResult = (AsyncResult) objectFuture;
        List<Future<Map<String, HotelSegmentationServiceResponse>>> actualResponse = (List<Future<Map<String, HotelSegmentationServiceResponse>>>) asyncResult.invoke();
        assertEquals(actualResponse.size(), 0);
    }

    @Test
    public void TestGetData(){
        List<HotelDetails> hotelDetailsList = getHotelDetailsList();

        RankingRequestData rankingRequestData = new RankingRequestData.Builder()
                .hotelDetailsList(hotelDetailsList)
                .sortBy(java.util.Arrays.asList(new SortBy())).placeId(PLACE_ID)
                .checkin(V2TestCommonUtil.getCheckin())
                .checkout(V2TestCommonUtil.getCheckout())
                .userId(V2TestCommonUtil.getUserId())
                .cityIdSearched(null)
                .cityId(null)
                .countryId(null)
                .userLocation(new Coordinates(28.410316, 77.048979))
                .cityCoordinates(new Coordinates(28.410316, 77.048979))
                .searchedLocation(new Coordinates(28.410316, 77.048979))
                .searchTypeEnum(SearchTypeEnum.CITY_SEARCH)
                .platform(Platform.analytics)
                .roomConfig(new Integer[] {1, 0, 0}).build();

        Set<String> applicableSegments = new HashSet<>();
        applicableSegments.addAll(new ArrayList<>(Arrays.asList(new String[]{"a", "b", "c", "d"})));

        when(configurationMap.get(Topic.COMMON_CONFIG)).thenReturn(rankingConfigurationData);
        when(rankingConfigurationData.getSegments()).thenReturn(applicableSegments);

//        when(hotelSegmentationHelper.getHotelSegmentsData(restTemplate, "https://abc.com/", V2TestCommonUtil.getRequestId()));

    }

    private List<HotelDetails> getHotelDetailsList(){
        HotelDetails[] hotelDetails = new HotelDetails[V2TestCommonUtil.getHotelIds().length];
        for(int i=0; i<hotelDetails.length; i++){
            hotelDetails[i] = new HotelDetails();
            hotelDetails[i].setHotelId(TestCommonUtil.getHotelIds()[i]);
        }
        List<HotelDetails> hotelDetailsList = new ArrayList<>(Arrays.asList(hotelDetails));
        return hotelDetailsList;
    }


}
