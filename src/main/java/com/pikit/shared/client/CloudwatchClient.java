package com.pikit.shared.client;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import com.pikit.shared.util.CloudwatchMetricUtil;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

@Log4j2
public class CloudwatchClient {
    private final AmazonCloudWatch cloudwatchClient;

    public CloudwatchClient(AmazonCloudWatch cloudwatchClient) {
        this.cloudwatchClient = cloudwatchClient;
    }

    public void putMetricData(String metricName,
                              double value,
                              String unit,
                              Map<String, String> dimensionMap) {
        try {
            PutMetricDataRequest request = CloudwatchMetricUtil.buildCloudwatchRequest(metricName, value, unit, dimensionMap);
            cloudwatchClient.putMetricData(request);
        } catch (Exception e) {
            log.error("Exception thrown putting metric data for {}", metricName, e);
        }
    }
}
