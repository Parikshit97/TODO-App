package com.oyorooms.service.rankingservice.v2logic.services;

import com.google.common.collect.Lists;
import com.oyorooms.service.rankingservice.entities.Coordinates;
import com.oyorooms.service.rankingservice.entities.Platform;
import com.oyorooms.service.rankingservice.testUtils.TestCommonUtil;
import com.oyorooms.service.rankingservice.testUtils.V2TestCommonUtil;
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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)

public class HotelSegmentationTest {

    @InjectMocks
    private HotelSegmentationService hotelSegmentationService;

    @Mock
    private HotelSegmentationHelper hotelSegmentationHelper;

    @Mock
    private RankingConfigurationData rcd = new RankingConfigurationData();

    @Mock
    private Map<Topic, Object > configurationMap = new HashMap<>();


    private String applicableSegments;


    private static final String PLACE_ID = "oyo-locality-17719172";

    @Test
    public void TestBatchPartition() {
        String[] hotelids = TestCommonUtil.getHotelIds();
        List<String> hotelidslist = Arrays.asList(hotelids);
        List<List<String>> subSets = Lists.partition(hotelidslist, 1);
        assertEquals(subSets.get(0).size(), 1);
    }

    @Test
    public void TestGetDataWhenHotelIds_null() {

        RankingRequestData rankingRequestData = new RankingRequestData.Builder()
                .hotelDetailsList(new ArrayList<>())
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
                .roomConfig(new Integer[]{1, 0, 0}).build();


        Set<String> applicableSegments = new HashSet<>();
        applicableSegments.addAll(new ArrayList<>(Arrays.asList(new String[]{"a", "b", "c", "d"})));

        when(configurationMap.get(Topic.COMMON_CONFIG)).thenReturn(rcd);
        when(rcd.getSegments()).thenReturn(applicableSegments);
        List<Future<Map<String, HotelSegmentationServiceResponse>>> actualResponse = hotelSegmentationService.getDataFromExternalService(null, rankingRequestData);
        assertEquals(actualResponse.size(), 0);

    }

    @Test
    public void TestGetDataWhenSegments_null(){

        HotelDetails[] hotelDetails = new HotelDetails[TestCommonUtil.getHotelIds().length];
        for(int i=0; i<hotelDetails.length; i++){
            hotelDetails[i] = new HotelDetails();
            hotelDetails[i].setHotelId(TestCommonUtil.getHotelIds()[i]);
        }

        List<HotelDetails> hotelDetailsList = new ArrayList<>(Arrays.asList(hotelDetails));

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

        when(configurationMap.get(Topic.COMMON_CONFIG)).thenReturn(rcd);
        when(rcd.getSegments()).thenReturn(applicableSegments);

        List<Future<Map<String, HotelSegmentationServiceResponse>>> actualResponse = hotelSegmentationService.getDataFromExternalService(null, rankingRequestData);
        assertEquals(actualResponse.size(), 0);
    }


}
