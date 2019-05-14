package com.example.myapplication;

import android.graphics.Color;

import com.example.myapplication.server.SensorData;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class SensorGraphViewsHelper {

    private static final int LIMIT = 100;
    private final LineChartView mHeightGraph;
    private final LineChartView mWeightGraph;

    public SensorGraphViewsHelper(LineChartView heightGraph, LineChartView weightGraph) {
        mHeightGraph = heightGraph;
        mWeightGraph = weightGraph;
    }

    public void setCurrentSensorData(List<SensorData> currentData) {
        setWeightGraphData(currentData);
        setHeightGraphData(currentData);
    }

    private void setWeightGraphData(List<SensorData> currentData) {
        int[] data = new int[currentData.size() < LIMIT ? currentData.size() : LIMIT];
        int maxValue = Integer.MIN_VALUE;
        int minValue = Integer.MAX_VALUE;
        for (int i = 0; i < data.length; i++) {
            int weight = currentData.get(i).weight;
            data[i] = weight;
            if (weight > maxValue)
                maxValue = weight;
            if (weight < minValue)
                minValue = weight;
        }
        draw(mWeightGraph, data, maxValue, minValue, Color.YELLOW);
    }

    private void setHeightGraphData(List<SensorData> currentData) {
        int[] data = new int[currentData.size() < LIMIT ? currentData.size() : LIMIT];
        int maxValue = Integer.MIN_VALUE;
        int minValue = Integer.MAX_VALUE;
        for (int i = 0; i < data.length; i++) {
            int acc_az = currentData.get(i).acc_az;
            data[i] = acc_az;
            if (acc_az > maxValue)
                maxValue = acc_az;
            if (acc_az < minValue)
                minValue = acc_az;
        }
        draw(mHeightGraph, data, maxValue, minValue, Color.WHITE);
    }

    private void draw(LineChartView graphView, int[] rawData, final int maxValue, final int minValue, int color) {
        int topLimit = maxValue + 100;
        int bottomLimit = minValue + 100;
        List yAxisValues = new ArrayList();
        List axisValues = new ArrayList();


        Line line = new Line(yAxisValues).setColor(color);

        for (int i = 0; i < rawData.length; i++) {
            axisValues.add(i, new AxisValue(i).setLabel(""));
        }

        for (int i = 0; i < rawData.length; i++) {
            yAxisValues.add(new PointValue(i, rawData[i]));
        }

        List lines = new ArrayList();
        lines.add(line);
        line.setHasPoints(false);

        LineChartData data = new LineChartData();
        data.setLines(lines);


        Axis axis = new Axis();
        axis.setValues(axisValues);
        data.setAxisXBottom(axis);

        graphView.setLineChartData(data);
        Viewport viewport = new Viewport(graphView.getMaximumViewport());
        viewport.top = topLimit;
        //viewport.bottom = bottomLimit;

        graphView.setMaximumViewport(viewport);
        graphView.setCurrentViewport(viewport);

    }
}