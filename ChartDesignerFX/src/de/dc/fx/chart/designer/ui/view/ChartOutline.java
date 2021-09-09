package de.dc.fx.chart.designer.ui.view;

import java.io.IOException;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import de.dc.workbench.fx.core.event.EventContext;
import de.dc.workbench.fx.core.event.IEventBroker;
import de.dc.workbench.fx.core.model.EmfOutlineContext;
import de.dc.workbench.fx.core.service.ISelectionService;
import de.dc.workbench.fx.core.xtext.IXtextService;
import de.dc.workbench.fx.ui.EmfTreeView;
import de.dc.workbench.fx.ui.chart.ChartFactory;
import de.dc.workbench.fx.ui.chart.ChartPackage;
import de.dc.workbench.fx.ui.chart.ChartProject;
import de.dc.workbench.fx.ui.chart.provider.ChartItemProviderAdapterFactory;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;

public class ChartOutline extends EmfTreeView<ChartProject> implements ChangeListener<TreeItem<Object>>{

	@Inject IEventBroker eventBroker;
	@Inject IXtextService xtextServie;
	@Inject ISelectionService selectionService;

	public ChartOutline() {
		super("Chart Outline");
		
		eventBroker.register(this);
		setShowContextMenu(false);
	}

	@Subscribe
	public void subscribeMonacoEditorTextChanged(EventContext<EmfOutlineContext> context) throws IOException {
		if (context.match("/monaco/editor/text/changed")) {
			EmfOutlineContext input = context.getInput();
			setInput((ChartProject) xtextServie.getRootFromDsl(input.getFile()));
			expandAll();
		}
	}
	
	@Override
	protected EObject createRootModel() {
		return ChartFactory.eINSTANCE.createChartProject();
	}

	@Override
	protected EPackage getEPackage() {
		return ChartPackage.eINSTANCE;
	}

	@Override
	protected AdapterFactory getModelItemProviderAdapterFactory() {
		return new ChartItemProviderAdapterFactory();
	}

	@Override
	public void changed(ObservableValue<? extends TreeItem<Object>> arg0, TreeItem<Object> arg1,
			TreeItem<Object> newValue) {
		if (newValue!=null) {
			Object value = newValue.getValue();
			if (value instanceof ChartProject) {
				setInput((ChartProject) value);
			}
		}
	}
}
