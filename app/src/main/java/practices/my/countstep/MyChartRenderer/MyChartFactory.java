package practices.my.countstep.MyChartRenderer;

import android.content.Context;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.XYChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

/**
 * Created by user on 2016/07/31.
 */
public class MyChartFactory extends ChartFactory {
    public static final GraphicalView getConciseLineChartView(Context context,
                                                              XYMultipleSeriesDataset dataset, XYMultipleSeriesRenderer renderer) {
        checkParameters(dataset, renderer);
        XYChart chart = new ConciseLineChart(dataset, renderer);
        return new GraphicalView(context, chart);
    }
}
