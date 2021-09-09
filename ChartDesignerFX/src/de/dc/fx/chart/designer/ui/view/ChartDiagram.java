package de.dc.fx.chart.designer.ui.view;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import de.dc.workbench.fx.core.command.di.EmfFXPlatform;
import de.dc.workbench.fx.core.event.EventContext;
import de.dc.workbench.fx.core.event.EventTopic;
import de.dc.workbench.fx.core.event.IEventBroker;
import de.dc.workbench.fx.core.model.EmfOutlineContext;
import de.dc.workbench.fx.core.service.ISelectionService;
import de.dc.workbench.fx.core.xtext.IXtextService;
import de.dc.workbench.fx.ui.EmfView;
import de.dc.workbench.fx.ui.chart.ChartProject;
import de.dc.workbench.fx.ui.chart.ChartXY;
import de.dc.workbench.fx.ui.chart.Function;
import de.dc.workbench.fx.ui.chart.renderer.ChartRenderer;
import de.dc.workbench.fx.ui.monaco.MonacoTextEditor;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.chart.Chart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;

public class ChartDiagram extends EmfView implements ChangeListener<TreeItem<Object>>{

	@Inject IEventBroker eventBroker;
	@Inject IXtextService xtextService;
	@Inject ISelectionService selectionService;
	
	private ChartRenderer renderer = new ChartRenderer();
	
	public ChartDiagram() {
		super("Chart Diagram");
		EmfFXPlatform.inject(this);
		eventBroker.register(this);
		selectionService.addListener(this);
	}
	
	@Subscribe
	public void subscribeCurrentSelectedEditor(EventContext<Tab> context) {
		if (context.match(EventTopic.CURRENT_SELECTED_EDITOR)) {
			if (context.getInput() instanceof MonacoTextEditor editor) {
				var input = editor.getInput();
				var content = input.getText();
				renderChartByString(content, input.getFile().getAbsolutePath());
			}
		}
	}

	@Subscribe
	public void subscribeMonacoEditorTextChanged(EventContext<EmfOutlineContext> context) throws IOException {
		if (context.match("/monaco/editor/text/changed")) {
			var input = context.getInput();
			var file = input.getFile();
			if (file==null) {
				return;
			}
			
			renderChartByString(input.getText(), file.getAbsolutePath());
		}
	}

	private void renderChartByString(String content, String fileName) {
		Platform.runLater(()->{
			var resourceSet = xtextService.getResourceSetByExtension(fileName);
			var resource = resourceSet.createResource(URI.createURI("dummy:/example.spellchart"));
			var in = new ByteArrayInputStream(content.getBytes());
			try {
				resource.load(in, resourceSet.getLoadOptions());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			var eObject = resource.getContents().get(0);
			var chart = renderer.doSwitch(eObject);
			if (chart.getUserData() instanceof ChartXY c) {
				for (Function f : c.getFunctions()) {
					var expression = f.getExpression();
					var series = new Series<>();
					series.setName(f.getTitle());
					if (expression != null) {
						for (int i = f.getStart(); i < f.getEnd(); i=i+f.getStep()) {
							var x = new Argument("x = "+i);
							var e = new Expression(expression, x);
							var result = e.calculate();
							series.getData().add(new Data<>(i, result));
						}
						if (chart instanceof XYChart chartXY) {
							chartXY.getData().add(series);
						}
					}
				}
			}
			
			setContent(chart);
		});
	}
	
	@Override
	public void changed(ObservableValue<? extends TreeItem<Object>> arg0, TreeItem<Object> arg1,
			TreeItem<Object> newValue) {
		if (newValue!=null) {
			Object value = newValue.getValue();
			if (value instanceof ChartProject) {
				Chart chart = renderer.doSwitch((EObject) value);
				setContent(chart);
			}
		}
	}
}
