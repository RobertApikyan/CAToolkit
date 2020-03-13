package sample.utils;

import javafx.scene.chart.XYChart;

public class Utils {

    public static double[] toDoubleArray(String text){
        String[] split = text.split(",");
        double[] array = new double[split.length];
        for (int i = 0; i < split.length; i++) {
            array[i] = tryToParse(split[i]);
        }
        return array;
    }

    public static double tryToParse(String text){
        try {
            return Double.parseDouble(text);
        }catch (Exception e){
            return 0;
        }
    }

    // returns elapsed nano time
    public static long timeWatch(Runnable block){
        ElapsedTimeCounter counter = new ElapsedTimeCounter();
        counter.start();
        block.run();
        return counter.stop();
    }

    public static void timeWatchPrint(String label,Runnable block){
        System.out.println(label+" elapsedTime= "+timeWatch(block));
    }

    public static int tryParseInt(String text){
        try {
            return Integer.parseInt(text);
        }catch (Exception e){
            return 0;
        }
    }

    public static int[] tryParseIntArray(String text,String separator){
        try {
            String[] values = text.split(separator);
            int[] intArray = new int[values.length];
            for (int i = 0; i < values.length; i++) {
                intArray[i] = tryParseInt(values[i].trim());
            }
            return intArray;
        }catch (Exception e){
            return new int[]{};
        }
    }

    public static void drawOnChart(XYChart xyChart, double[] values){
        xyChart.getData().clear();

        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        for (int i = 0; i < values.length; i++) {
            series.getData().add(new XYChart.Data<Number,Number>(i,values[i]));
        }

        xyChart.getData().add(series);
    }
}
